<%-- 
    Document   : table
    Created on : 2016-8-10, 19:18:20
    Author     : tianxia
--%>

<%@ page import="java.util.Map" %>
<%@ page import="matrixreader.MatrixReader" %>
<%@ page import="matrixreader.DocumentTopicMatrixReader" %>
<%@ page import="javax.xml.parsers.SAXParser" %>
<%@ page import="javax.xml.parsers.SAXParserFactory" %>
<%@ page import="xml.parse.TopicPhraseHandler" %>
<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="javax.xml.parsers.ParserConfigurationException" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="static utility.Tools.highlightKeywords" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="tianxia">
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
                        <li><a href="show.html">Show </a></li>
                        <li class="active"><a href="search.html">Search </a></li>
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
        <p class="illustrate">If you want to search other key words, please click <a href="search.html">here</a></p>
        <br/>
        <h4>Your search query:</h4>
        <p><%= request.getParameter("searchQuery")%>
        </p>
        <br/>
        <h3>Relative topics:</h3>
        <br/>
        <table class="table table-striped">
            <%
                String searchQuery = request.getParameter("searchQuery").toLowerCase();
                String[] tableResults = request.getAttribute("matchedQuery").toString().split("\\|");

                List<String> searchQueriesList = new ArrayList<>();
                for(String str : searchQuery.split(" |,|;")) {
                    searchQueriesList.add(str);
                }

                for (String topicLine : tableResults) {
            %>
            <tr>
                <td class="emotion">
                    <img src="./image/heart-grey.png" class="img-circle img-heart grayscale">
                    <div class="text-heart text-grey">10</div>
                </td>
                <td class="emotion">
                    <img src="./image/emotion.png" class="img-circle img-smile">
                    <div class="text-smile">97.66</div>
                </td>
                <td>
                    <%
                        String[] allLine = topicLine.split("\n");
                        for (String line : allLine) {
                            if (line.contains("Top 3 Documents: ")) {
                    %>
                    <p>
                        <b>Top 3 Documents: </b>
                    <%
                                line = line.replace("Top 3 Documents: ", "");
                                String[] documents = line.split("\t");
                                int index = 0;
                                for (String fileName : documents) {
                                    String link = "http://localhost:8080/static/JSEA-store-data/upload/" + fileName;
                                    fileName = highlightKeywords(fileName, searchQueriesList);
//                                    for (String str : searchQuery.split(" |,|;")) {
//                                        String fileNameToMatch = fileName.toLowerCase();
//                                        if(fileNameToMatch.contains(str)) {
//
//                                            fileName = "<b style='color:red'>" + fileName + "</b>";
//                                        }
////                                        fileName = fileName.replaceAll("(?i)" + str, "<b style='color:red'>" + str + "</b>");
//                                    }
                                    index++;
                    %>
                    <a href="<%=link%>" target="_blank"><%=fileName%>
                    </a>;
                    <%
                        }
                        if (index < 3) {
                    %>
                    no more file...
                    <%
                        }
                    %>
                    </p>
                    <%
                    } else {
                        for (String str : searchQuery.split(" |,|;")) {
                            line = line.replaceAll(str, "<b style='color:red'>" + str + "</b>");
                        }
                    %>
                    <p>
                        <%=line%>
                    </p>
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
        <p class="pull-right affix-top"><a href="#">Back to top</a></p>
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
