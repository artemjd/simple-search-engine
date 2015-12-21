<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
	<title>Search Page</title>
</head>
<body>

	<h1>Search</h1>
	<form action="search" method="post">
		Enter search text:<br>
  		<input type="text" name="q">
  		<input type="submit" value="Search">	 
	</form>

	<c:if test="${not empty error}">
		${error}
	</c:if>		
		
</body>
</html>
