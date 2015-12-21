<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
	<title>Error Page</title>
	<link href="<c:url value="/resources/main.css" />" rel="stylesheet"  type="text/css" />	
</head>
<body>
	<h1>Something went wrong...</h1>
	${message}
	
</body>
</html>
