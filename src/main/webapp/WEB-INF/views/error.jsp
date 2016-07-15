<%--
    Document   : lti.jsp
    Created on : May 24, 2014, 10:58:04 PM
    Author     : pgray
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Access Denied</title>
        <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
        <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css" rel="stylesheet">
        <link href="assets/styles/app.css" rel="stylesheet"/>
    </head>
    <body style="background-color:#FD6F6F; height:100%;">
        <h1>Access Denied!</h1>
        <div>
          ${ ltiError }
        </div>
    </body>
</html>
