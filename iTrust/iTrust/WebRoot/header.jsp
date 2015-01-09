<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri='/WEB-INF/cewolf.tld' prefix='cewolf'%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>

<%@include file="/authenticate.jsp"%>

<%
if (validSession) {
	errorMessage = (String) session.getAttribute("errorMessage");
	session.removeAttribute("errorMessage");
}

if (loggedInMID != null
		&& session.getAttribute("loginFlag") != null
		&& session.getAttribute("loginFlag").equals("true")) {
	loggingAction.logEvent(TransactionType.LOGIN_SUCCESS,
			loggedInMID, loggedInMID, "");
	session.removeAttribute("loginFlag");
}

if (request.getRequestURI().contains("home.jsp")) {
	session.removeAttribute("pid");
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><%=StringEscapeUtils.escapeHtml("" + (pageTitle))%></title>
<link href="/iTrust/css.jsp" type="text/css" rel="stylesheet" />
<link href="/iTrust/css/datepicker.css" type="text/css" rel="stylesheet" />
<script src="/iTrust/js/DatePicker.js" type="text/javascript"></script>
<script src="/iTrust/js/jquery-1.7.2.js" type="text/javascript"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
	<div id="container2" style="background-image:url(/iTrust/image/new/repeatableImage.png); background-repeat: repeat-x;">
		<div id="iTrustHeader2">
			<div id="iTrustLogo2">
				<a class="iTrustNavlink" href="/iTrust"><img
					style="position:absolute; top:1px; float: left; height: 114px; border: 0px;"
					src="/iTrust/image/new/title.png"
					alt="iTrust Electronic Medical Records Software" height="75px" />
				</a>
			</div>
			<div id="iTrustUserInfo">
<%
if (validSession) {
	if ((loggedInMID != null) && (loggedInMID.longValue() != 0L)) //if no one is logged in
	{
%>
				<div
					style="width: 200px; position: absolute; top: 0px; right: 0px; background:#3e3e3e; float: right; text-align: right; vertical-align: bottom; margin-right: 20px; color: white;">
	<%
		out.println("Welcome, "
							+ StringEscapeUtils.escapeHtml("" + userName)
							+ "<BR/>");
	%>
					<a class="iTrustNavlink" href="/iTrust">Home</a> | <a
						class="iTrustNavlink" href="/iTrust/logout.jsp">Logout</a>
				</div>
			</div>
			<div id="iTrustSelectedPatient">
	<%
		if (session.getAttribute("pid") != null
							&& ((String) session.getAttribute("pid")).length() > 0
							&& !session.getAttribute("pid").equals("null"))
							//if(session.getAttribute("pid") != null)
		{
	%>
				<span class="selectedPatient"> Viewing information for <b><%=selectedPatientName%></b>
					| <a
					href="/iTrust/auth/getPatientID.jsp?forward=<%=request.getRequestURI()%>">Select
						a Different Patient</a> </span>
<%
		}
	} //no one is logged in
} //valid session
%>
			</div>
		</div>
		<br>
		<br>
		<br>
		<br>
		<br>
		<br>
		<div id="iTrustMain">
			<div id="iTrustMenu">
				<!-- 	<img id="menuPic" src="/iTrust/image/new/menu.png"  /> 
				<img src="/iTrust/image/new/menu_top.png"  /> -->
				<div class="iTrustMenuContents">
<%
if (validSession) {
	if ((loggedInMID != null) && (loggedInMID.longValue() != 0L)) //someone is logged in
	{
		if (userRole.equals("patient")) { %>
					<%@include file="/auth/patient/menu.jsp"%>
					<%
		} else if (userRole.equals("uap")) {
					%><%@include file="/auth/uap/menu.jsp"%>
					<%
		} else if (userRole.equals("hcp")) {
					%><%@include file="/auth/hcp/menu.jsp"%>
					<%
		} else if (userRole.equals("er")) {
					%><%@include file="/auth/er/menu.jsp"%>
					<%
		} else if (userRole.equals("pha")) {
					%><%@include file="/auth/pha/menu.jsp"%>
					<%
		} else if (userRole.equals("admin")) {
					%><%@include file="/auth/admin/menu.jsp"%>
					<%
		} else if (userRole.equals("tester")) {
					%><%@include file="/auth/tester/menu.jsp"%>
					<%
		} else if (userRole.equals("lt")) {
					%><%@include file="/auth/lt/menu.jsp"%>
	<%
		}
	} //no one is logged in	
	else {
		String uri = request.getRequestURI();
		if (uri.indexOf("privacyPolicy.jsp") >= 0) { //looking at privacy policy, include logout menu.
	%>
					<%@include file="logoutMenu.jsp"%>
					<%
		} else { //we are actually logged out entirely, show login menu
	%>
					<%@include file="loginMenu.jsp"%>
	<%
		} //else
	} //else
} //if valid session
else {
%>
					<%@include file="/logoutMenu.jsp"%>
<%
}
%>
				</div>
				<!-- <img src="/iTrust/image/new/menu_bottom.png"  /> -->
			</div>
			<div id="iTrustPage">
				<div class="leftBorder"></div>
				<div id="iTrustContent">
<%
if (errorMessage != null) {
%>
					<div
						style="text-align: center; width: 100%; background-color: black;">
						<span style="color: red; font-size: 28px; font-weight: bold;"><%=StringEscapeUtils.escapeHtml(errorMessage)%></span>
					</div>
<%
}
%>