<%-- 
    Document   : register
    Created on : Jul 28, 2014, 11:45:08 PM
    Author     : paul
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Register ExampleLti</title>
        <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.22/angular.min.js"></script>
        <script src="assets/scripts/app.js"></script>
        <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
        <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css" rel="stylesheet">
        <link href="assets/styles/app.css" rel="stylesheet"/>
        <script>
            window.tool_proxy_registration_request = ${tool_proxy_registration_request};
            window.tool_consumer_retrieval_token = "${tool_consumer_retrieval_token}";
        </script>
    </head>
    <body ng-app="ltiApp">
        <div ng-controller="ExampleLtiAppController">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-md-4 col-md-offset-4 main-view">
                        <div class="header">
                            LtiApp
                        </div>
                        <div ng-if="loadingProfile">
                            <i class="fa fa-circle-o-notch fa-spin"></i> Loading Profile
                        </div>
                        <div ng-if="!loadingProfile">
                            <div class="subheader">Capabilities offered:</div>
                            <div class="row">
                                <div class="col-md-6 col-md-offset-3 main-view caps">
                                    <div class="capability" ng-repeat="capability in tc_profile.capability_offered">
                                        {{capability}}
                                    </div>
                                </div>
                            </div>
                        </div>

                        <button class="btn" onclick="window.location = '${params.launch_presentation_return_url}'">Back to Tool Consumer</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
