<%@ page import="java.io.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %><%--
    Document   : table
    Created on : 2015-5-23, 21:57:55
    Author     : tianxia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="./image/analysis.jpg">

    <title>JSEA·Java Software Engineers Assistant</title>

    <!-- Bootstrap core CSS -->
    <link href="./css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="./css/show.css" rel="stylesheet">
</head>
<body>
<div class="navbar-wrapper">
    <div class="container">
        <nav class="navbar navbar-inverse navbar-static-top" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <a class="navbar-brand" href="#">JSEA</a>
                </div>
                <div id="navbar" class="navbar-collapse collapse">
                    <ul class="nav navbar-nav">
                        <li><a href="home.html">Home </a></li>
                        <li><a href="help.html">Help </a></li>
                        <li class="active"><a href="show.html">Show </a></li>
                        <li><a href="search.html">Search </a></li>
                    </ul>
                </div>
            </div>
        </nav>

    </div>
</div>
<!-- Marketing messaging and featurettes
   ================================================== -->
<!-- Wrap the rest of the page in another container to center all the content. -->

<div class="container marketing">
    <div class="row featurette show_divider">
        <p class="illustrate">If you want to select again, please click <a href="show.html">here</a></p>
        <br/>
        <h3>Topics:</h3>
        <br/>


        <%
            String topics = request.getAttribute("topics").toString();
            String[] topicsArray = topics.split("\n");
            String[] labelsArray = (String[]) request.getAttribute("labels");
            String programRootPath = request.getAttribute("program-root-path").toString();

            File topDocumentsFile = new File(programRootPath + "showFile/topic-docs.txt");
            Map<Integer, String> topDocuments = new HashMap<>();
            try {
                try (
                        InputStream in = new FileInputStream(topDocumentsFile.getPath());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    String docsLine = "";
                    String allFileName = "";
                    int documentCountIndex = 0;
                    int documentCount = 0;
                    int checkCountLabel = 1;
                    boolean checked = false;
                    while ((docsLine = reader.readLine()) != null) {
                        if (!docsLine.equals("#topic doc name proportion ...")) {
                            String[] linePart = docsLine.split("\t| ");
                            int topicIndex = Integer.parseInt(linePart[0]);
                            if (topicIndex == checkCountLabel && !checked) {
                                documentCount = documentCountIndex;
                                checked = true;
                                topDocuments.put(topicIndex - 1, allFileName);
                                allFileName = "";
                                documentCountIndex = 0;
                            }
                            String[] nameParts = linePart[2].split("/");
                            String fileName = nameParts[nameParts.length - 1];
                            allFileName = allFileName + fileName + "\n";
                            documentCountIndex++;
                            if (documentCountIndex == documentCount) {
                                topDocuments.put(topicIndex, allFileName);
                                allFileName = "";
                                documentCountIndex = 0;
                            }
                        }
                    }
                }
            } catch (IOException ex) {
        %>
        <p><%=ex%>
        </p>
        <%
            }
        %>
        <table class="table table-striped">
            <%
                int topicIndex = 0;
                for (int i = 0; i < topicsArray.length; ++i) {
            %>
            <tr>
                <td>
                    <p>
                        <b>Topics: </b>
                        <%=topicsArray[i]%>
                    </p>
                    <p>
                        <b>Phrases: </b>
                        <%=labelsArray[i]%>
                    </p>
                    <p>
                        <b>More Top Documents: </b>
                    <p>
                            <%
                        String[] documentsStr = topDocuments.get(i).split("\n");
                        int index = 0;
                        for(String document:documentsStr) {
                        String[] nameParts = document.split("/");
                                String textName = nameParts[nameParts.length - 1];//textName eg. src-org-jhotdraw-app-AbstractApplication.java-src.txt
                                int lastIndexOfStrigula = textName.lastIndexOf('-');
                                if (lastIndexOfStrigula >= 0) {
                                    String fileNameWithPath = textName.substring(0, lastIndexOfStrigula);//fileNameWithPath eg. src-org-jhotdraw-app-AbstractApplication.java
                                    String fileName = fileNameWithPath.substring(fileNameWithPath.lastIndexOf('-')+1, lastIndexOfStrigula);//fileName eg. AbstractApplication.java
                                    String link = "http://localhost:8080/static/JSEA-store-data/upload/" + fileNameWithPath;

                    %><a href="<%=link%>" target="_blank"><%=fileName%>
                    </a>,
                    <%
                            }
                        }
                    %>
                </td>
            </tr>
            <%
                }
            %>
        </table>
    </div>

    <hr class="featurette-divider">

    <!-- /END THE FEATURETTES -->


    <!-- FOOTER -->
    <footer>
        <p class="pull-right"><a href="#">Back to top</a></p>
        <p>2017 @Tianxia, Wang</p>
    </footer>


</div><!-- /.container -->

<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="./js/jquery.min.js"></script>
<script src="./js/bootstrap.min.js"></script>
<script src="./js/docs.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="./js/ie10-viewport-bug-workaround.js"></script>
<!--custom js -->
<script src="./js/home.js"></script>
</body>
</html>
