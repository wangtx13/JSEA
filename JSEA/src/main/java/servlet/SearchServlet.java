/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import xml.parse.ExtractPhraseLabels;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utility.Tools.*;


/**
 *
 * @author apple
 */
public class SearchServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        boolean findMatchedQuery = false;
        try (PrintWriter out = response.getWriter()) {
            String programRootPath = getServletContext().getInitParameter("program-root-path");
//            request.setAttribute("program-root-path", programRootPath);
            String searchQuery = request.getParameter("searchQuery").toLowerCase();
            String[] searchQueryList = searchQuery.split(" |,|;");
            ArrayList<Integer> matchedTopicIndex = new ArrayList<>();
            Map<Integer, Double> distSumToSort = new HashMap<Integer, Double>();
            Map<Integer, Double> sameValue = new HashMap<Integer, Double>();
            Map<Integer, Double> distSum = new HashMap<Integer, Double>();

            String[] showType = request.getParameterValues("showType");

            try {
                String topicsFilePath = programRootPath + "showFile/keys.txt";
                File topicsKey = new File(topicsFilePath);
                int topicCount = 0;
                StringBuffer matchedQueryBuffer = new StringBuffer();
                Map<Integer, Integer> matchedTopics = new HashMap<>();
                Map<Integer, String> topics = new HashMap<>();

                File top3DocFile = new File(programRootPath + "showFile/top3Documents.txt");
                Map<Integer, Map<String, Double>> top3Documents = new HashMap<>();

                try (
                        InputStream inTopicKeys = new FileInputStream(topicsKey.getPath());
                        BufferedReader readerTopicKeys = new BufferedReader(new InputStreamReader(inTopicKeys));
                        InputStream inTop3Doc = new FileInputStream(top3DocFile.getPath());
                        BufferedReader readerTop3Doc = new BufferedReader(new InputStreamReader(inTop3Doc))) {
                    String topicLine = "";
                    while ((topicLine = readerTopicKeys.readLine()) != null) {
                        topics.put(topicCount, topicLine);
                        topicCount++;
                    }

                    //generate top3Documents Map with <topic No., <fileName, percentage of doc-topic>>
                    String top3DocLine = "";
                    while (( top3DocLine = readerTop3Doc.readLine()) != null) {
                        String[] linePart =  top3DocLine.split("\t");
                        int topicNo =Integer.parseInt(linePart[0]);
                        Map<String, Double> top3ForATopic = new HashMap<>();
                        for(int i = 1; i < linePart.length; i = i + 2) {
                            if(i+1 < linePart.length) {
                                top3ForATopic.put(linePart[i], Double.parseDouble(linePart[i + 1]));//top file
                            }
                        }
                        sortMapByValueWithStringKey(top3ForATopic);
                        top3Documents.put(topicNo, top3ForATopic);
                    }

                    //calculate the distance
                    //calculate topics distance
                        for (Map.Entry<Integer, String> entry : topics.entrySet()) {
                            String[] aSetOfTopics = entry.getValue().split(" |\t");
                            int topicNo = entry.getKey();
                            double distSumForATopicToSort = 0;
                            double sameValueForATopic = 0;
                            double distSumForATopic = 0;
                            for (int i = 2; i < aSetOfTopics.length; i++) {
                                for (int j = 0; j < searchQueryList.length; j++) {
                                    if (aSetOfTopics[i].equals(searchQueryList[j])) {
                                        distSumForATopicToSort += 100;
                                        sameValueForATopic++;
                                        findMatchedQuery = true;
                                        break;
                                    }
                                    double distance = compute(aSetOfTopics[i], searchQueryList[j]);

                                    distSumForATopicToSort += distance;
                                    distSumForATopic += distance;
                                }
                            }
                            sameValue.put(topicNo, sameValueForATopic);
                            distSum.put(topicNo, distSumForATopic);
                            distSumToSort.put(topicNo, distSumForATopicToSort);
                        }

                    //calculate the match degree between search query and top 3 document names
                    //Map<Integer, Map<String, Double>> top3Documents
                    for (Map.Entry<Integer, Map<String, Double>> entry : top3Documents.entrySet()) {
                        int topicNo = entry.getKey();
                        Map<String, Double> top3DocForATopic = entry.getValue();
                        double distSumForATopicToSort = distSumToSort.get(topicNo);
                        double sameValueForATopic = sameValue.get(topicNo);
                        for (Map.Entry<String, Double> entry_doc : top3DocForATopic.entrySet()) {
                            String fileName = entry_doc.getKey();
                            String[] classNameList = fileName.split("-");
                            String className = classNameList[classNameList.length - 1];
                            double percentage = entry_doc.getValue();
                            for (int i = 0; i < searchQueryList.length; i++) {
                                if (className.toLowerCase().contains(searchQueryList[i])) {
                                    distSumForATopicToSort += 100 * percentage;
                                    sameValueForATopic += percentage;
                                    findMatchedQuery = true;
                                    break;
                                }
                            }
                        }
                        sameValue.put(topicNo, sameValueForATopic);
                        distSumToSort.put(topicNo, distSumForATopicToSort);
                    }

                }

                distSumToSort = sortMapByValueIntDouble(distSumToSort);

                String phraseLabelFilePath = programRootPath + "showFile/topic-phrases.xml";
                ExtractPhraseLabels extractPhraseLabels = new ExtractPhraseLabels(topicCount);
                String[] allPhraseLabels = extractPhraseLabels.getAllPhraseLabels(phraseLabelFilePath, topicCount);

                request.setAttribute("topics", topics);
                request.setAttribute("allPhraseLabels", allPhraseLabels);
                request.setAttribute("top3Documents", top3Documents);
                request.setAttribute("distSumToSort", distSumToSort);
                request.setAttribute("sameValue", sameValue);
                request.setAttribute("distSum", distSum);
//                request.setAttribute("hasTopic", hasTopic);
//                request.setAttribute("hasDocuments", hasDocuments);
                request.setAttribute("findMatchedQuery", findMatchedQuery);

            } catch (FileNotFoundException ex) {
                out.println(ex);
            } catch (IOException ex) {
                out.println(ex);
            }

//            if(findMatchedQuery) {
                request.getRequestDispatcher("./searchResults.jsp").forward(request, response);
//            } else {
//                request.getRequestDispatcher("./noResults.jsp").forward(request, response);
//            }
        }



    }

    private String getAllFileNames(String[] documentsList) {
        String allFileNames = "";
        for (String doc : documentsList) {
            String[] nameParts = doc.split("/");
            String textName = nameParts[nameParts.length - 1];
            String fileName = "";
            int lastIndexOfStrigula = textName.lastIndexOf('-');
            if (lastIndexOfStrigula >= 0) {
                fileName = textName.substring(0, lastIndexOfStrigula);
                allFileNames += fileName + "\t";
            }
        }
        return allFileNames;
    }

    private static double compute(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);
        ILexicalDatabase db = new NictWordNet();
        double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
        return s;
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
