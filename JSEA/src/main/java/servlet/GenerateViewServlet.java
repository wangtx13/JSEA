/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import matrixreader.DocumentTopicMatrixReader;
import matrixreader.MatrixReader;
import org.xml.sax.SAXException;
import xml.parse.ExtractPhraseLabels;
import xml.parse.TopicPhraseHandler;

import java.io.*;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author apple
 */
public class GenerateViewServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String programRootPath = getServletContext().getInitParameter("program-root-path");
            String viewContent = request.getParameter("viewContent");
            String phraseLabelFilePath = programRootPath + "showFile/topic-phrases.xml";

            File topicKeysFile = new File(programRootPath + "showFile/keys.txt");
            File phraseLabelFile = new File(phraseLabelFilePath);

            if (!topicKeysFile.exists() || !phraseLabelFile.exists()) {
                request.getRequestDispatcher("./error.jsp").forward(request, response);
            } else {
                StringBuffer topicBuffer = new StringBuffer();
                int topicCount = 0;

                try {
                    try (
                            InputStream inTopicKeys = new FileInputStream(topicKeysFile.getPath());
                            BufferedReader readerTopicKeys = new BufferedReader(new InputStreamReader(inTopicKeys))) {
                        String line = "";
                        while ((line = readerTopicKeys.readLine()) != null) {
                            topicCount++;
                            topicBuffer = topicBuffer.append(line + "\n");
                        }
                    }
                } catch (IOException ex) {
                    out.println(ex);
                }

                ExtractPhraseLabels extractPhraseLabels = new ExtractPhraseLabels(topicCount);
                String[] allPhraseLabels = extractPhraseLabels.getAllPhraseLabels(phraseLabelFilePath, topicCount);

                request.setAttribute("topics", topicBuffer.toString());
                if (viewContent.equals("Topics")) {
                    request.getRequestDispatcher("./topics.jsp").forward(request, response);
                } else if (viewContent.equals("Topics and Phrases")) {
                    request.setAttribute("labels", allPhraseLabels);
                    request.getRequestDispatcher("./labels.jsp").forward(request, response);
                } else if (viewContent.equals("Topics, Phrases and Top 3 Documents (Recommended)")) {
                    request.setAttribute("labels", allPhraseLabels);
                    request.setAttribute("program-root-path", programRootPath);
                    request.getRequestDispatcher("./top3Documents.jsp").forward(request, response);
                } else if (viewContent.equals("Topics, Phrases and More Top Documents")) {
                    request.setAttribute("labels", allPhraseLabels);
                    request.setAttribute("program-root-path", programRootPath);
                    request.getRequestDispatcher("./moreTopDocuments.jsp").forward(request, response);
                }
            }
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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
