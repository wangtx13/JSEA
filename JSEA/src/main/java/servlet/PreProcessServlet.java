/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import preprocess.PreProcessTool;
import topicmodeling.Executor;
import utility.ProcessPrinter;

import static utility.Tools.createDirectoryIfNotExisting;
import static utility.Tools.deleteFolderContent;
import static utility.Tools.writeMapIntStrAToFile;


/**
 *
 * @author apple
 */
public class PreProcessServlet extends HttpServlet {

    private boolean isMultipart;
    private String uploadRootPath;
    private int maxFileSize = 50 * 1024 * 1024;
    private int maxMemSize = 4 * 1024;
    private File file;
    private static Map<String, Boolean> libraryTypeCondition = new HashMap<String, Boolean>() {
        {
            put("Drawing", false);
            put("Modeling", false);
            put("Need_to_do_1", false);
            put("Need_to_do_2", false);
            put("Need_to_do_3", false);
        }
    };

//    public void init() {
//        uploadRootPath = getServletContext().getInitParameter("file-upload");
//        File upload = new File(uploadRootPath);
//        if(upload.exists()) {
//            deleteFolderContent(upload);
//        }
//        createDirectoryIfNotExisting(uploadRootPath);
//    }

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
        uploadRootPath = getServletContext().getInitParameter("file-upload");
        File upload = new File(uploadRootPath);
        if(upload.exists()) {
            deleteFolderContent(upload);
        }
        createDirectoryIfNotExisting(uploadRootPath);
        String programRootPath = getServletContext().getInitParameter("program-root-path");
        boolean isGeneral = false;
        String copyrightStoplist = "";
        String customizedPackageList = "";
        String projectName = "";
        int topicCount = -1;

        String inputRootFilePath = uploadRootPath;
        String outputFilePath = programRootPath + "preprocessOutput/PreProcessTool";
        File outputFile = new File(outputFilePath);
        if(outputFile.exists()) {
            deleteFolderContent(outputFile);
        }

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");

            out.println("<html lang=\"en\">");
            out.println("<head>"
                    + "<meta charset=\"utf-8\">"
                    + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                    + "<meta name=\"description\" content=\"\">"
                    + "<meta name=\"author\" content=\"\">"
                    + "<link rel=\"icon\" href=\"./image/analysis.jpg\">"
                    + "<title>Programmer Assistor</title>"
                    + "<link href=\"./css/bootstrap.min.css\" rel=\"stylesheet\">"
                    + "<script src=\"./js/ie-emulation-modes-warning.js\">"
                    + "</script><!-- Custom styles for this template -->"
                    + "<link href=\"./css/list.css\" rel=\"stylesheet\">"
                    + "</head>");
            out.println("<body>"
                    + "<div class=\"navbar-wrapper\">"
                    + "<div class=\"container\">"
                    + "<nav class=\"navbar navbar-inverse navbar-static-top\" role=\"navigation\">"
                    + "<div class=\"container\">"
                    + "<div class=\"navbar-header\">"
                    + "<a class=\"navbar-brand\" href=\"home.html\">Programmer Assistor</a>"
                    + "</div>"
                    + "<div id=\"navbar\" class=\"navbar-collapse collapse\">"
                    + "<ul class=\"nav navbar-nav\">"
                    + "<li>"
                    + "<a href=\"home.html\">Home </a>"
                    + "</li>"
                    + "<li>"
                    + "<a href=\"help.html\">Help </a>"
                    + "</li>"
                    + "<li>"
                    + "<a href=\"show.html\">Show </a>"
                    + "</li>"
                    + "<li>"
                    + "<a href=\"search.html\">Search </a>"
                    + "</li>"
                    + "</ul>"
                    + "</div>"
                    + "</div>"
                    + "</nav>"
                    + "</div>"
                    + "</div>");
            out.println("<div class=\"container marketing\">");
            out.println("<div class=\"row featurette files\" id=\"fileList\">");
            out.println("<h2>1. Preprocessing </h2>");
            out.println("<h3>Preprocessed Data Directory: </h3>");
            out.println("<p>");
            out.println(programRootPath + "preprocessOutput/PreProcessTool");
            out.println("</p>");
            out.println("<h3>Uploaded Files: </h3>");

            isMultipart = ServletFileUpload.isMultipartContent(request);

            if (!isMultipart) {
                out.println("<p>the request isn't multipart</p>");
                return;
            }
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(maxMemSize);
            // Location to save data that is larger than maxMemSize.
            factory.setRepository(new File(programRootPath, "temp"));

            ServletFileUpload uploadProcess = new ServletFileUpload(factory);
            uploadProcess.setSizeMax(maxFileSize);

            try {
                List fileItems = uploadProcess.parseRequest(request);

                Iterator i = fileItems.iterator();

                while (i.hasNext()) {
                    FileItem fi = (FileItem) i.next();
                    if (!fi.isFormField()) {
                        String fileName = fi.getName();
                        file = new File(inputRootFilePath
                                + fileName.replace('/', '-'));//replace all / to -
                        fi.write(file);

                        out.println("<p>");
                        out.println(fileName);
                        out.println("</p>");
                    } else {
                        String fieldName = fi.getFieldName();
                        String fieldValue = fi.getString();
                        if (fieldName.equals("general") && fieldValue.equals("on")) {
                            isGeneral = true;
                        } else if (fieldName.equals("project_type")) {
                            if (fieldValue.equals("Drawing")) {
                                libraryTypeCondition.remove("Drawing");
                                libraryTypeCondition.put("Drawing", true);
                            }  else if (fieldValue.equals("Modeling")) {
                                libraryTypeCondition.remove("Modeling");
                                libraryTypeCondition.put("Modeling", true);
                            }
                        } else if(fieldName.equals("customizePackageInfoContent")) {
                            String customizePackageInfoContent = fieldValue;
                            customizedPackageList = customizePackageInfoContent.toLowerCase().replaceAll("\\*|\n|[0-9]|,|;|`|'|\"", "");
                            customizedPackageList = customizedPackageList.replaceAll("\\(|\\)|-|//|:|~|/|\\.", " ");

                        } else if(fieldName.equals("copyrightInfoContent")) {
                            String copyrightInfoContent = fieldValue;
                            copyrightInfoContent = copyrightInfoContent.replaceAll(".java", "java");
                            copyrightStoplist = copyrightInfoContent.toLowerCase().replaceAll("\\*|\n|[0-9]|,|;|`|'|\"", "");
                            copyrightStoplist = copyrightStoplist.replaceAll("\\(|\\)|-|//|:|~|/|\\.", " ");

                        } else if(fieldName.equals("projectNameContent")) {
                            projectName = fieldValue;
                        } else if (fieldName.equals("topicCountContent")) {
                            topicCount = Integer.parseInt(fieldValue);
                        }
                    }
                }

//                out.println("<p>"+ copyrightStoplist.toString() +"</p>");


            } catch (FileUploadException e) {
                // TODO Auto-generated catch block  
                out.println("The size of files is too much, please upload files less than 50MB");
                e.printStackTrace();
            } catch (Exception ex) {
                out.println(ex);

            }

            PreProcessTool preProcessTool = new PreProcessTool(inputRootFilePath, outputFilePath, isGeneral, libraryTypeCondition, copyrightStoplist, customizedPackageList);
            preProcessTool.preProcess();
            out.println("<h3 id = \"success\" class=\"fileHead\"> Successful Uploading and preprocessing!</h3>");

            //topic modeling
            out.println("<p></p>");
            out.println("<h2>2. Topic Modeling: </h2>");
            out.println("<p>The project name: ");
            out.println(projectName);
            out.println("</p>");
            out.println("<p> The number of topics: ");
            out.println(topicCount);
            out.println("</p>");
            out.println("<h3 id=\"topModelingLogHeader\">Start Topic Modeling: </h3>");
            out.println(
                    "<script>" +
                      "window.wtx = window.setInterval(function () {" +
                            "var div = document.getElementById('topModelingLog'); " +
                            "div.scrollTop = div.scrollHeight; " +
                            "div.scrollIntoView(true); " +
                            "}, 100);" +
                    "</script>"
            );
            out.println("<div id=\"topModelingLog\" style=\"height: 300px; overflow:scroll;\">");
            Executor executor = new Executor(projectName, topicCount, new ProcessPrinter(out::println));
            executor.run();
            out.println("</div>");
            out.println("<script>window.clearInterval(window.wtx);</script>");

            //prepare for search
            File compositeFile = new File(programRootPath + "showFile/composition.txt");
            String top3DocumentsFilePath = programRootPath + "showFile/top3Documents.txt";
            Map<String, String> top3Documents = new HashMap<>();
            Map<Integer, String[]> top3DocAndPer = new HashMap<>();

            int totalFileNo = 0;

            try (
                    InputStream inComposite = new FileInputStream(compositeFile.getPath());
                    BufferedReader readerComposite = new BufferedReader(new InputStreamReader(inComposite))) {


                String compositeLine = "";
                while ((compositeLine = readerComposite.readLine()) != null) {
                    totalFileNo++;
                }
            }

            try (
                    InputStream inComposite = new FileInputStream(compositeFile.getPath());
                    BufferedReader readerComposite = new BufferedReader(new InputStreamReader(inComposite))) {


                String compositeLine = "";
                Double[][] topicDocMatrix = new Double[topicCount][totalFileNo];
                String[] fileNameList = new String[totalFileNo];
                int fileNo = 0;
                while ((compositeLine = readerComposite.readLine()) != null) {
                    String[] linePart = compositeLine.split("\t| ");
                    String[] nameParts = linePart[1].split("/");
                    String textName = nameParts[nameParts.length - 1];
                    int lastIndexOfStrigula = textName.lastIndexOf('-');
                    if (lastIndexOfStrigula >= 0) {
                        String fileName = textName.substring(0, lastIndexOfStrigula);
                        fileNameList[fileNo] = fileName;
                    }

                    for (int i = 2; i < linePart.length; i++) {
                        topicDocMatrix[i-2][fileNo] = Double.parseDouble(linePart[i]);
                    }

                    fileNo++;
                }

                //find top3 document for each topic
                for(int i = 0; i < topicDocMatrix.length; i++) {
                    double max1 = 0;
                    String file1 = "";
                    double max2 = 0;
                    String file2 = "";
                    double max3 = 0;
                    String file3 = "";
                    for (int j = 0; j < topicDocMatrix[i].length; j++) {
                        if(topicDocMatrix[i][j] > max1) {
                            max3 = max2;
                            max2 = max1;
                            max1 = topicDocMatrix[i][j];
                            file1 = fileNameList[j];
                        } else if (topicDocMatrix[i][j] > max2) {
                            max3 = max2;
                            max2 = topicDocMatrix[i][j];
                            file2 = fileNameList[j];
                        } else if (topicDocMatrix[i][j] > max3) {
                            max3 = topicDocMatrix[i][j];
                            file3 = fileNameList[j];
                        }
                    }
                    String[] docAndPerList = new String[3];
                    docAndPerList[0] = file1 + "\t" + max1;
                    docAndPerList[1] = file2 + "\t" + max2;
                    docAndPerList[2] = file3 + "\t" + max3;
                    top3DocAndPer.put(i, docAndPerList);
                }

            }


            File top3DocumentsFile = new File(top3DocumentsFilePath);
            if(top3DocumentsFile.createNewFile()) {
                System.out.println("Create successful: " + top3DocumentsFile.getName());
            }
            writeMapIntStrAToFile(top3DocumentsFile, top3DocAndPer);


            out.println("<h3 id = \"success\" class=\"fileHead\">Successful Topic Modeling!</h3>");
            out.println("<h2 id = \"success\" class=\"fileHead\">Now, you can start using the \"Show\" function and the \"Search\" function.</h2>");

            out.println("</div>");
            out.println("<hr class=\"featurette-divider\">");
            out.println("<footer>"
                    + "<p class=\"pull-right\">"
                    + "<a href=\"#\">Back to top</a>"
                    + "</p>"
                    + "<p>2017 @Tianxia, Wang</p>"
                    + "</footer>");
            out.println("</div>");
            out.println("<script src=\"./js/jquery.min.js\"></script>");
            out.println("<script src=\"./js/bootstrap.min.js\"></script>");
            out.println("<script src=\"./js/docs.min.js\"></script>");
            out.println("<script src=\"./js/ie10-viewport-bug-workaround.js\"></script>");
            out.println("<script src=\"./js/home.js\"></script>");
            out.println("</body>");
            out.println("</html>");

        }
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
