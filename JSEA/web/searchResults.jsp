<%-- 
    Document   : table
    Created on : 2016-8-10, 19:18:20
    Author     : tianxia
--%>

<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="static utility.Tools.highlightKeywords" %>
<%@ page import="static utility.Tools.formatDouble" %>
<%@ page import="static utility.Tools.splitCamelWords" %>
<%@ page import="static utility.Tools.*" %>
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
        <%
            String searchQuery = request.getParameter("searchQuery").toLowerCase();
            List<String> searchQueriesList = new ArrayList<>();
            for(String str : searchQuery.split(" |,|;")) {
                searchQueriesList.add(str);
            }

            Map<Integer, String> topics = (Map<Integer, String>)request.getAttribute("topics");
            String[] allPhraseLabels = (String[])request.getAttribute("allPhraseLabels");
            Map<Integer, Map<String, Double>> top3Documents = (Map<Integer, Map<String, Double>>)request.getAttribute("top3Documents");
            Map<Integer, Double> distSumToSort = (Map<Integer, Double>)request.getAttribute("distSumToSort");
            Map<Integer, Double> sameValue = (Map<Integer, Double>)request.getAttribute("sameValue");
            Map<Integer, Double> distSum = (Map<Integer, Double>)request.getAttribute("distSum");
            boolean findMatchedQuery = (boolean)request.getAttribute("findMatchedQuery");
        %>
        <h4>Your search query:</h4>
        <p><%=searchQuery%>
        </p>
        <div class="illustrate">
            <p><b>Tips: </b></p>
            <li>1. Topics are sorted by their semantic similarity with the search query.</li>
            <%
                if (!findMatchedQuery) {
            %>
            <li>2. Red words are same as the search query. For your search query: "<%=searchQuery%>", <b class="text-tips">there is no same word in the topics and the top 3 class names...</b></li>
            <%
                } else {
            %>
            <li>2. Red words are same as the search query.</li>
            <%
                }
            %>
            <li>3. The heart icon means how much the topics and/or the names of top 3 documents are same with the search query.</li>
            <li>4. The smile icon means how much the topics are similar with the search query in semantic meaning.</li>
        </div>
        <br/>
        <h3>Topics: </h3>
        <br/>
        <table class="table table-striped">
            <%
                for (Map.Entry<Integer, Double> entry_sorted : distSumToSort.entrySet()) {
                    int topicNo = entry_sorted.getKey();//topic index
                    double sameValueForATopic = sameValue.get(topicNo); // same value = the number of words are same with query words + doc name contains same words * percentage of doc-topic
                    sameValueForATopic = formatDouble(sameValueForATopic);
                    double distForATopic = distSum.get(topicNo); // distance between words without same word and query words
                    distForATopic = formatDouble(distForATopic);
                    String topicLine = topics.get(topicNo); // topics line
                    String[] aSetOfTopics = topicLine.split(" |\t");
                    String highlightMatchedTopic = aSetOfTopics[0] + "\t" + aSetOfTopics[1] + "\t";
                    for (int i = 2; i < aSetOfTopics.length; i++) {
                        highlightMatchedTopic += highlightKeywords(aSetOfTopics[i], searchQueriesList)+ " ";
                    }
                    topicLine = highlightMatchedTopic;
                    String label = allPhraseLabels[topicNo];

            %>
            <tr>
            <%
                    if (sameValueForATopic != 0) {
            %>
                <td class="emotion">
                    <img src="./image/heart.png" class="img-circle img-heart">
                    <div class="text-heart"><%=sameValueForATopic%></div>
                </td>
                <td class="emotion">
                    <img src="./image/smile-grey.png" class="img-circle img-smile">
                    <div class="text-smile text-grey"><%=distForATopic%></div>
                </td>
            <%
                    } else if (distForATopic != 0) {
            %>
                <td class="emotion">
                    <img src="./image/heart-grey.png" class="img-circle img-heart">
                    <div class="text-heart text-grey"><%=sameValueForATopic%></div>
                </td>
                <td class="emotion">
                    <img src="./image/smile.png" class="img-circle img-smile">
                    <div class="text-smile"><%=distForATopic%></div>
                </td>
            <%
                    } else {
            %>
                <td class="emotion">
                    <img src="./image/heart-grey.png" class="img-circle img-heart">
                    <div class="text-heart text-grey"><%=sameValueForATopic%></div>
                </td>
                <td class="emotion">
                    <img src="./image/smile-grey.png" class="img-circle img-smile">
                    <div class="text-smile text-grey"><%=distForATopic%></div>
                </td>
            <%
                    }
            %>
                <td>
                    <p>
                        <b>Topics: </b>
                        <span><%=topicLine%></span>
                    </p>
                    <p>
                        <b>Top 3 Documents: </b>
                        <%
                            // top 3 doc
                            Map<String, Double> top3DocAndPerForATopic = top3Documents.get(topicNo); // <doc name, percentage of doc-topic>
                            int docIndex = 1;
                            //add link and highlight matched word
                            for(Map.Entry<String, Double> entry_doc : top3DocAndPerForATopic.entrySet()) {
                                String fileName = entry_doc.getKey();
                                String link = "http://localhost:8080/static/JSEA-store-data/upload/" + fileName;
                                String[] classNameList = fileName.split("-");
                                String className = classNameList[classNameList.length - 1];
                                String[] classNameNoSuffixList = className.split("\\.");
                                String classNameNoSuffix = className;
                                if(classNameNoSuffixList.length > 0) {
                                    classNameNoSuffix = classNameNoSuffixList[0];
                                }
                                String[] splitClass = splitDocName(classNameNoSuffix);
                                String highlightMatchedClassName = "";
                                for(int i = 0; i < splitClass.length; i++) {
                                    splitClass[i] = highlightKeywords(splitClass[i], searchQueriesList);
                                    highlightMatchedClassName += splitClass[i];
                                }
                                String highlightMatchedDoc = "";
                                for (int i = 0; i < classNameList.length - 1; i++) {
                                    highlightMatchedDoc += classNameList[i] + "-";
                                }
                                highlightMatchedDoc += highlightMatchedClassName + ".java";
                                fileName = highlightMatchedDoc;

                                docIndex++;

                                if (docIndex < 3) {
                        %>
                        <a href="<%=link%>" target="_blank"><%=fileName%></a>;
                        <%
                                } else {
                        %>
                        <a href="<%=link%>" target="_blank"><%=fileName%></a>
                        <%
                                }
                            }

                            //if the number of doc less than 3, add "no more file tips"
                            if (docIndex < 3) {
                        %>
                            no more relevant file...
                        <%
                            }
                        %>
                    </p>
                    <p>
                        <b>Phrases: </b>
                        <%
                            if(label.equals("")) {
                        %>
                        <span>/</span>
                        <%
                            } else {
                        %>
                        <span><%=label%></span>
                        <%
                            }
                        %>
                    </p>
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
