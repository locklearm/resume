<%@page import="edu.ncsu.csc.itrust.action.ActivityFeedAction"%>
<%@page import="java.util.List"%>
<%@page import="edu.ncsu.csc.itrust.action.EditPatientAction"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewPersonnelAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.TransactionBean"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="edu.ncsu.csc.itrust.enums.TransactionType"%>
<%@page import="edu.ncsu.csc.itrust.beans.PersonnelBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO"%>
<%@page import="edu.ncsu.csc.itrust.exception.iTrustException"%>
<%@page errorPage="/auth/exceptionHandler.jsp" %>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>

<%
	loggingAction.logEvent(TransactionType.ACTIVITY_FEED_VIEW, loggedInMID.longValue(), 0, "");
		
	pageTitle = "iTrust - View My Access Log";
	ActivityFeedAction action = new ActivityFeedAction(prodDAO, loggedInMID);
	List<TransactionBean> accesses;
	Date viewTime = new Date();

	int pageNumber = 1;
	
	if (request.getParameter("date") != null && request.getParameter("page") != null) {
		try {
			viewTime = new Date(Long.parseLong(request.getParameter("date")));
		} catch (NumberFormatException e) {
			viewTime = new Date();
		}
		try {
			pageNumber = Integer.parseInt(request.getParameter("page"));
		} catch (NumberFormatException e) {
			pageNumber = 1;
		}
	}
	
	accesses = action.getTransactions(viewTime, pageNumber);
	ArrayList<PersonnelBean> hcpList = new ArrayList<PersonnelBean>(); 
	session.setAttribute("personnelList", hcpList);
	
	boolean more = false;
	if(accesses.size() == 21) {
		more = true;
	}
%>


<div id="notificationArea" style="width:60%; padding: 0px;">
  <ul>
    <h2>Activity Feed</h2>
<%
	if (pageNumber > 1) {
%>
	<li style="width: 100%;">
		<span style="float: left;"><a href="home.jsp?page=1">Refresh</a></span>
		<%
		if(accesses.size() == pageNumber * 20 + 1) {
		%>
			<span style="float: right;"><a href="home.jsp?date=<%= StringEscapeUtils.escapeHtml("" + ( viewTime.getTime())) %>&page=<%= StringEscapeUtils.escapeHtml("" + (pageNumber + 1 )) %>">Older Activities</a></span>
		<%
		}
		%>
		<br />
	</li>
<%
	}
	ViewPersonnelAction personnels = new ViewPersonnelAction(prodDAO, loggedInMID);
	EditPatientAction patients;
	
	String name = "";
	int zebraStripes = 0;
	int hcpCount = 0;
	
	if (pageNumber == 1) {
		if(accesses.isEmpty()) {
%>
			<li style="background-color:"white";">
				<%= StringEscapeUtils.escapeHtml("" + ( "No recent activity" )) %>
			</li>
			<%
		} else {
								/* getTransactions() returns one more than necessary */
			for(int i=0; i < (more ? accesses.size() - 1 : accesses.size()); i++) { 
				zebraStripes++;
				TransactionBean t = accesses.get(i);
				
				try {
					PersonnelBean hcp = personnels.getPersonnel(t.getLoggedInMID() + "");
					if (hcp != null) {
						name = "<a href=\"/iTrust/auth/viewPersonnel.jsp?personnel=" + hcpCount++ + "\">" +
							StringEscapeUtils.escapeHtml(hcp.getFullName()) + "</a>";
						hcpList.add(hcp);
					}
				} catch (iTrustException e) {
					patients = new EditPatientAction(prodDAO, loggedInMID, t.getLoggedInMID() + "");
					PatientBean eventPatient = patients.getPatient();
					name = eventPatient.getFullName();
					StringEscapeUtils.escapeHtml(name);
				}
			%>
				<li class="<%= (zebraStripes % 2) == 0 ? "zebraOn" : "zebraOff" %>">
					<%= action.getMessageAsSentence(name, t.getTimeLogged(), t.getTransactionType())  %>
				</li>
	<%
			}
		}
		if(accesses.size() == 21) {
	%>
		<a href="home.jsp?date=<%= StringEscapeUtils.escapeHtml("" + ( viewTime.getTime() )) %>&page=2">Older Activities</a>
	<%
		}
	} else {
		for(int i=0; i < (more ? accesses.size() - 1 : accesses.size()); i++) { 
			zebraStripes++;
			TransactionBean t = accesses.get(i);
			try {
				PersonnelBean hcp = personnels.getPersonnel(t.getLoggedInMID() + "");
				if (hcp != null) {
					name = "<a href=\"/iTrust/auth/viewPersonnel.jsp?personnel=" + hcpCount++ + "\">" +
						StringEscapeUtils.escapeHtml(hcp.getFullName()) + "</a>";
					hcpList.add(hcp);
				}
			} catch (iTrustException e) {
				patients = new EditPatientAction(prodDAO, loggedInMID, t.getLoggedInMID() + "");
				PatientBean eventPatient = patients.getPatient();
				name = StringEscapeUtils.escapeHtml(eventPatient.getFullName());
			}
	%>
			<li style="background-color:<%= (zebraStripes % 2) == 0 ? "#DDD" : "white" %>;">
				<%= action.getMessageAsSentence(name, t.getTimeLogged(), t.getTransactionType()) %>
			</li>
	<%
		}
	%>
	<li style="width: 100%;">
		<span style="float: left;"><a href="home.jsp?page=1">Refresh</a></span>
	<%
	if(accesses.size() == pageNumber * 20 + 1) {
	%>
		<span style="float: right;"><a href="home.jsp?date=<%= StringEscapeUtils.escapeHtml("" + ( viewTime.getTime())) %>&page=<%= StringEscapeUtils.escapeHtml("" + (pageNumber + 1 )) %>">Older Activities</a></span>
	<%
	}
	%>
	</li>
<%
	}
%>
</ul>
</div>
