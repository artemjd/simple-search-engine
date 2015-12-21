<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<%@ page pageEncoding="UTF-8"%>
<html>
<head>
<title>Search Results</title>
<link href="<c:url value="/resources/main.css" />" rel="stylesheet"
	type="text/css" />
</head>
<body>
	<h1>Search Results</h1>
	<c:if test="${not empty pages}">
		<c:forEach items="${pages}" var="page">
			<a href="${page.url}" class="title"> <b>${page.title}</b><br />
			</a>
			<a href="${page.url}" class="url"> ${page.url}<br /> <br />
			</a>
		</c:forEach>
	</c:if>
	<c:if test="${empty pages}">
		Nothing found
	</c:if>
</body>
</html>
