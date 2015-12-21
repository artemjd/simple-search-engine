<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false" %>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
	<title>Index Page</title>
	<link href="<c:url value="/resources/main.css" />" rel="stylesheet" type="text/css" />
</head>
<body>
	<h1>Index</h1>

	<form action="index" method="post">
		Enter URL:<br>
  		<input type="text" name="url" class="url">
  		<input type="submit" value="Index">	 
  		<br>Indexing depth:<br>
 		<select name="depth" >
  			<option value="0">0</option>
  			<option value="1">1</option>
  			<option value="2">2</option>
  			<option value="3">3</option>
			<option value="4">4</option>
			<option value="5">5</option>
  			<option value="6">6</option>
  			<option value="7">7</option>
  			<option value="8">8</option>
  			<option value="9">9</option>
			<option value="10">10</option>
		</select>
 	</form>
 		
	<c:if test="${not empty indexedUrls}">
		${fn:length(indexedUrls)} indexed URLs:<br>
		<c:forEach items="${indexedUrls}" var="url">
	    	<a href="${url}" class="indexed_url">
	    		${url}<br/>
	    	</a>
		</c:forEach>
	</c:if>
	
	<c:if test="${not empty error}">
		${error}
	</c:if>		
			
</body>
</html>
