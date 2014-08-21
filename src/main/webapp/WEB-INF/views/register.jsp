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
        <link rel="stylesheet" href="assets/styles/app.css"/>
        <script>
            window.tool_proxy_registration_request = ${tool_proxy_registration_request};
            window.tool_consumer_retrieval_token = "${tool_consumer_retrieval_token}";
        </script>
    </head>
    <body ng-app="ltiApp">
        <div ng-controller="ExampleLtiAppController">
            <h1>Got register request:</h1>
            <h3>Required Params:</h3>
            <b>lti_message_type:</b> ${params.lti_message_type} <br/>
            <b>reg_key:</b> ${params.reg_key} <br/>
            <b>reg_password:</b> ${params.reg_password} <br/>
            <b>tc_profile_url:</b> ${params.tc_profile_url} <br/>
            <b>launch_presentation_return_url:</b> ${params.launch_presentation_return_url} <br/>

            <h3>Recommended Params:</h3>
            <b>launch_presentation_document_target:</b> ${params.launch_presentation_document_target}<br/>
            <b>launch_presentation_height:</b> ${params.launch_presentation_height} <br/>
            <b>launch_presentation_width:</b> ${params.launch_presentation_width} <br/>
            <b>user_id:</b> ${params.user_id} <br/>
            <b>roles:</b> ${params.roles} <br/>

            <h1>{{stuff}}</h1>


            <button onclick="window.location = '${params.launch_presentation_return_url}'">Back to Tool Consumer</button>
        </div>
    </body>
</html>
