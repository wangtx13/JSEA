<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="">
        <meta name="author" content="">
        <link rel="icon" href="./image/analysis.jpg">

        <title>JSEA-Java Software Engineers Assistant</title>

        <!-- Bootstrap core CSS -->
        <link href="./css/bootstrap.min.css" rel="stylesheet">

        <!-- Custom styles for this template -->
        <link href="./css/show.css" rel="stylesheet">
    </head>
    <!-- NAVBAR
    ================================================== -->
    <body>
        <div class="navbar-wrapper">
            <div class="container">
                <nav class="navbar navbar-inverse navbar-static-top" role="navigation">
                    <div class="container">
                        <div class="navbar-header">
                            <a class="navbar-brand" href="home.html">JSEA</a>
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

            <!-- START THE FEATURETTES -->
            <div class="row featurette show_divider">
                <h3>No results for "<%=request.getParameter("searchQuery")%>"...</h3>
                <p>Tips: </p>
                <ul>
                    <li>Please check whether the words are spelled correctly</li>
                    <li>Please select more search type: Topics, Labels, Top 3 Documents</li>
                    <li>Please try another query words</li>
                    <li>Please reduce the number of query words</li>
                </ul>
                <p class="illustrate">Please come <a href="search.html">back</a> to search again.</p>
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
    </body>
</html>
