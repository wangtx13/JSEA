/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import matrixreader.DocumentTopicMatrixReader;
import matrixreader.MatrixReader;
import xml.parse.ExtractPhraseLabels;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utility.Tools.sortMapByValueForTopics;


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

            boolean hasTopic = false;
            boolean hasLabels = false;
            boolean hasDocuments = false;
            String[] showType = request.getParameterValues("showType");
            for(String str : showType) {
                if(str.contains("topics")) {
                    hasTopic = true;
                } else if(str.contains("labels")) {
                    hasLabels = true;
                } else if(str.contains("documents")) {
                    hasDocuments = true;
                }
            }

            try {
                String topicsFilePath = programRootPath + "showFile/keys.txt";
                File topicsKey = new File(topicsFilePath);
                int topicCount = 0;
                StringBuffer matchedQueryBuffer = new StringBuffer();
                Map<Integer, Integer> matchedTopics = new HashMap<>();
                Map<Integer, String> topics = new HashMap<>();

                File topDocumentsFile = new File(programRootPath + "showFile/topic-docs.txt");
                Map<Integer, String> topThreeDocuments = new HashMap<>();

                try (
                        InputStream inTopicKeys = new FileInputStream(topicsKey.getPath());
                        BufferedReader readerTopicKeys = new BufferedReader(new InputStreamReader(inTopicKeys));
                        InputStream inTopicDocs = new FileInputStream(topDocumentsFile.getPath());
                        BufferedReader readerTopicsDocs = new BufferedReader(new InputStreamReader(inTopicDocs))) {
                    String topicLine = "";
                    while ((topicLine = readerTopicKeys.readLine()) != null) {
                        topics.put(topicCount, topicLine);

                        int matchedDegree = 0;
                        for(String str : searchQueryList) {
                            if(topicLine.contains(str)) {
                                matchedDegree++;
                            }
                        }

                        if(matchedDegree > 0) {
                            matchedTopics.put(topicCount, matchedDegree);
                        }

                        topicCount++;
                    }

                    String docsLine = "";
                    String threeFileName = "";
                    int documentCountIndex = 0;
                    int documentCount = 0;
                    int checkCountLabel = 1;
                    boolean checked = false;
                    while ((docsLine = readerTopicsDocs.readLine()) != null) {
                        if (!docsLine.equals("#topic doc name proportion ...")) {
                            String[] linePart = docsLine.split("\t| ");
                            int topicIndex = Integer.parseInt(linePart[0]);
                            if (topicIndex == checkCountLabel && !checked) {
                                documentCount = documentCountIndex;
                                checked = true;
                                topThreeDocuments.put(topicIndex - 1, threeFileName);
                                threeFileName = "";
                                documentCountIndex = 0;
                            }
                            if(documentCountIndex < 3) {
                                String[] nameParts = linePart[2].split("/");
                                String textName = nameParts[nameParts.length - 1];
                                int lastIndexOfStrigula = textName.lastIndexOf('-');
                                if (lastIndexOfStrigula >= 0) {
                                    String fileName = textName.substring(0, lastIndexOfStrigula);
                                    threeFileName = threeFileName + fileName + "\t";
                                }
                            }
                            documentCountIndex++;
                            if (documentCountIndex == documentCount) {
                                topThreeDocuments.put(topicIndex, threeFileName);
                                threeFileName = "";
                                documentCountIndex = 0;
                            }
                        }
                    }
                }

                String phraseLabelFilePath = programRootPath + "showFile/topic-phrases.xml";
                ExtractPhraseLabels extractPhraseLabels = new ExtractPhraseLabels(topicCount);
                String[] allPhraseLabels = extractPhraseLabels.getAllPhraseLabels(phraseLabelFilePath, topicCount);


                if(hasTopic) {
                    Map<Integer, Integer> sortedMatchedTopics = sortMapByValueForTopics(matchedTopics);
                    for (Map.Entry<Integer, Integer> entry : sortedMatchedTopics.entrySet()) {
                        int topicIndex = entry.getKey();
                        int matchedDegree = entry.getValue();
                        String matchedTopicsString = topics.get(topicIndex);
                        if (!matchedTopicIndex.contains(topicIndex)) {
                            matchedTopicIndex.add(topicIndex);
                            String relativeLabels = allPhraseLabels[topicIndex];
                            String relativeDocuments = topThreeDocuments.get(topicIndex);

                            matchedQueryBuffer = matchedQueryBuffer.append("<b>Topics: </b>" + matchedTopicsString + "\n");
                            matchedQueryBuffer = matchedQueryBuffer.append("<b>Phrases: </b>" + relativeLabels + "\n");
                            matchedQueryBuffer = matchedQueryBuffer.append("Top 3 Documents: " + relativeDocuments);
                            matchedQueryBuffer = matchedQueryBuffer.append("|");//another topic

                            findMatchedQuery = true;
                        }
                    }
                }

                if(hasLabels) {
                    for (int i = 0; i < allPhraseLabels.length; ++i) {
                        for (String str : searchQueryList) {
                            if (allPhraseLabels[i].toLowerCase().contains(str) && !matchedTopicIndex.contains(i)) {
                                matchedTopicIndex.add(i);
                                String relativeTopics = topics.get(i);
                                String relativeDocuments = topThreeDocuments.get(i);
                                matchedQueryBuffer = matchedQueryBuffer.append("<b>Topics: </b>" + relativeTopics + "\n");
                                matchedQueryBuffer = matchedQueryBuffer.append("<b>Phrases: </b>" + allPhraseLabels[i] + "\n");
                                matchedQueryBuffer = matchedQueryBuffer.append("Top 3 Documents: " + relativeDocuments);
                                matchedQueryBuffer = matchedQueryBuffer.append("|");//another topic

                                findMatchedQuery = true;
                            }
                        }
                    }
                }

                if(hasDocuments) {
                    for(Map.Entry<Integer, String> entry : topThreeDocuments.entrySet()) {
                        int index = entry.getKey();
                        if(!matchedTopicIndex.contains(index)) {
                            String documents = entry.getValue();
                            String documentsToMatch = documents.toLowerCase();
                            for (String str : searchQueryList) {
                                if (documentsToMatch.contains(str)) {
                                    String relativeTopics = topics.get(index);
                                    String relativeLabels = allPhraseLabels[index];
                                    matchedQueryBuffer = matchedQueryBuffer.append("<b>Topics: </b>" + relativeTopics + "\n");
                                    matchedQueryBuffer = matchedQueryBuffer.append("<b>Phrases: </b>" + relativeLabels + "\n");
                                    matchedQueryBuffer = matchedQueryBuffer.append("Top 3 Documents: " + documents);
                                    matchedQueryBuffer = matchedQueryBuffer.append("|");//another topic

                                    findMatchedQuery = true;
                                }
                            }
                        }
                    }
                }

                request.setAttribute("matchedQuery", matchedQueryBuffer.toString());

//                request.setAttribute("topicCount", topicCount);

            } catch (FileNotFoundException ex) {
                out.println(ex);
            } catch (IOException ex) {
                out.println(ex);
            }

            if(findMatchedQuery) {
                request.getRequestDispatcher("./searchResults.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("./noResults.jsp").forward(request, response);
            }
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
