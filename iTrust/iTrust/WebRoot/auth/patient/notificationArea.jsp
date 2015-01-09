<%@page import="edu.ncsu.csc.itrust.action.ViewMyMessagesAction"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewMyApptsAction"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewMyLabProceduresAction"%>
<%@page import="edu.ncsu.csc.itrust.action.DeclareHCPAction"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewMyRemoteMonitoringListAction"%>
<%@page import="edu.ncsu.csc.itrust.action.EditRepresentativesAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.MessageBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.ApptBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.PersonnelBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.RemoteMonitoringDataBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewPatientReferralsAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.ReferralBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.HospitalBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.WardDAO"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>


<%
ViewMyMessagesAction messageAction = new ViewMyMessagesAction(prodDAO, loggedInMID.longValue());
ViewMyApptsAction apptAction = new ViewMyApptsAction(prodDAO, loggedInMID.longValue());
ViewMyLabProceduresAction labProcsAction = new ViewMyLabProceduresAction(prodDAO, loggedInMID.longValue());
ViewPatientReferralsAction referralAction = new ViewPatientReferralsAction(prodDAO, loggedInMID);
List <ApptBean> appointments = apptAction.getAppointments(loggedInMID.longValue());
DeclareHCPAction hcpAction = new DeclareHCPAction(prodDAO, loggedInMID.longValue());
List<PersonnelBean> hcps = hcpAction.getDeclaredHCPS();
ViewMyRemoteMonitoringListAction remoteMonitoringAction = new ViewMyRemoteMonitoringListAction(prodDAO, loggedInMID.longValue());
List<RemoteMonitoringDataBean> remoteData = remoteMonitoringAction.getPatientsData();
EditRepresentativesAction representativeAction = new EditRepresentativesAction(prodDAO, loggedInMID.longValue(), String.valueOf(loggedInMID.longValue()));
List<PatientBean> representees = representativeAction.getRepresented(loggedInMID.longValue());
loggingAction.logEvent(TransactionType.NOTIFICATIONS_VIEW, loggedInMID, 0, "");

int labProcsCount = labProcsAction.getUnviewedCount();
int referralCount = referralAction.getReferralsForPatientUnread();
Date rightNow = new Date();
%>

<div id="notificationArea" style="float:right; width:40%;background-color:white;" class="rightArea">
  
    <h2>Notifications</h2>
	<div id="areaContainer">
<!-- Begin Message Notification -->    
    <div class="contentBlock" style="margin:0.3em;">
    <% WardDAO wardDAO = new WardDAO(prodDAO);
    HospitalBean hospital = wardDAO.getHospitalByPatientID(loggedInMID.longValue());
    %>
    <ul style="list-style-type: none;">
    
<% if(messageAction.getUnreadCount() == 0) { %>
	<li>
	   <img class="icon" src="/iTrust/image/icons/inboxEmpty.png" style="border:0px;">
        No unread messages.
	</li>
<% } else { %>
	<li>
	   <a href="/iTrust/auth/hcp-patient/messageInbox.jsp">
	     <img class="icon" src="/iTrust/image/icons/inboxUnread.png" style="border:0px;"></a>
       <a href="/iTrust/auth/hcp-patient/messageInbox.jsp">
	     <%= StringEscapeUtils.escapeHtml("" + (messageAction.getUnreadCount())) %></a>
	   Unread message(s).
	</li>
<% } %>

<% if(labProcsCount == 0) { %>
	<li>
	   <img class="icon" src="/iTrust/image/icons/beaker.png" style="border:0px; margin: 0 0 0 -2px;">
	   No new completed lab procedures.
	</li>
<%	} else { %>
		<li>
		  <a href="viewMyLabProcedures.jsp"><img class="icon" src="/iTrust/image/icons/beaker.png" style="border:0px; margin: 0 0 0 -2px;"></a>
		  <a href="viewMyLabProcedures.jsp">
		    <%= StringEscapeUtils.escapeHtml("" + labProcsCount) %></a>
		  new completed lab procedure<%= ((labProcsCount > 1) ? "s" : "") %>.
		</li>
 <% } %>
 <%if(referralCount == 0) { %>
 	<li>
	   <img class="icon" src="/iTrust/image/icons/icon-doctor.png" style="border:0px;">
        No new referrals.
	</li>
<%} else { %>
     <li>
		  <a href="viewPatientReferrals.jsp"><img class="icon" src="/iTrust/image/icons/icon-doctor.png" style="border:0px; margin: 0 0 0 ;"></a>
		  <a href="viewPatientReferrals.jsp">
		    <%= StringEscapeUtils.escapeHtml("" + referralCount) %></a>
		  new patient referral<%= ((referralCount > 1) ? "s" : "") %>.
	</li>
 <% } %>
    </ul>
	</div>
<!-- End Message Notification -->

<!-- Begin Telemedine Notification -->
<%
	if (remoteMonitoringAction.getMonitoringHCPs().size() > 0 && remoteData.size() <= 0) {
%>
	<div class="contentBlock" style="margin:0.3em;">
		<img style="border:0px" src="/iTrust/image/icons/noTelemedicine.png" />
		You haven't entered remote monitoring information for today yet!
	</div>
<%
	}
%>
<!-- End Telemedicine Notification -->

<!-- Begin Designated HCP Information -->
	<span class="subheading">Your Designated HCPs</span><br />
	<%
	if(hcps.size() > 0) {
		for(PersonnelBean hcp : hcps) {
			%>
			<div class="contentBlock" style="margin:0.3em;">
			<img style="float:left;margin:2px;height:45px" src="/iTrust/image/user/<%= StringEscapeUtils.escapeHtml("" + (hcp.getMID() )) %>.png" alt="/iTrust/image/user/">
			<%= StringEscapeUtils.escapeHtml("" + (hcp.getFullName() )) %><br />
			<%= StringEscapeUtils.escapeHtml("" + (hcp.getPhone() )) %><br />
			<%= StringEscapeUtils.escapeHtml("" + (hcp.getEmail() )) %><br />
			</div>
			<%
		}
	} else {
		%>
		<div class="contentBlock" style="margin:0.3em;">
		You have no declared LHCP.<br />
		</div>
		<%
	}
	%>
<!-- End Designated HCP Information -->

<!-- Begin Upcoming Appointments Information -->	
	<span class="subheading" style="margin-top:10px;">Your Upcoming Appointments</span><br />
	<div class="contentBlock" style="margin:0.3em;">
	<ul>
	<%
	int index = 0;
	for(ApptBean appt : appointments) {
		if(appt.getDate().after(rightNow)) {
			Timestamp apptDate = appt.getDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat();
			dateFormat.applyPattern("MM/dd/yyyy");
			String dateString = dateFormat.format(apptDate);
			%>
			<li>
			<a href="/iTrust/auth/patient/viewAppt.jsp?apt=<%= StringEscapeUtils.escapeHtml("" + appt.getApptID()) %>">
				<img class="icon" style="border:0px;" src="/iTrust/image/icons/appointment.png" /></a>
			<a href="/iTrust/auth/patient/viewAppt.jsp?apt=<%= StringEscapeUtils.escapeHtml("" + appt.getApptID()) %>">
				<%= StringEscapeUtils.escapeHtml("" + (dateString )) %></a> - <%= StringEscapeUtils.escapeHtml("" + (appt.getApptType() )) %>
			</li>
			<%
		}
		index++;
	}
	%>
	</ul>
	</div>
	<%
	for(PatientBean patientDataBean : representees) {
		List <ApptBean> patientAppointments = apptAction.getAppointments(patientDataBean.getMID());
		boolean isFirstBean = true;
		index = 0;
		for(ApptBean appt : patientAppointments) {
			if(appt.getDate().after(rightNow)) {
				if(isFirstBean) {
					isFirstBean = false;
					%>
					<span class="subheading"><%= StringEscapeUtils.escapeHtml("" + (patientDataBean.getFullName() )) %>'s Upcoming Appointments</span><br />
					<div class="contentBlock" style="margin:0.3em;">
					<ul>
					<%
				}
				Timestamp apptDate = appt.getDate();
				SimpleDateFormat dateFormat = new SimpleDateFormat();
				dateFormat.applyPattern("MM/dd/yyyy");
				String dateString = dateFormat.format(apptDate);
				%>
				<li>
					<a href="/iTrust/auth/patient/viewAppt.jsp?apt=<%= StringEscapeUtils.escapeHtml("" + appt.getApptID()) %>">
						<img class="icon" style="border:0px" src="/iTrust/image/icons/appointment.png" /></a>
					<a href="/iTrust/auth/patient/viewAppt.jsp?apt=<%= StringEscapeUtils.escapeHtml("" + appt.getApptID()) %>">
						<%= StringEscapeUtils.escapeHtml("" + (dateString )) %>
					</a> - <%= StringEscapeUtils.escapeHtml("" + (appt.getApptType() )) %>
			    </li>
				<%
			}
			index++;
		}
		if(!isFirstBean) {
			%>
			</ul>
			</div>
			<%
		}
	}
	%>
<!-- End Upcoming Appointment Information -->

<!--  Begin Office Visit Survey Information -->	
	<span class="subheading">Office Visit Surveys</span>
	<div class="contentBlock" style="margin:0.3em;">
	<ul>
	<%
	if (0 != surList.size()) {
	%>
	<%
		for (OfficeVisitBean ov : surList) {
			if (!surveyAction.isSurveyCompleted(ov.getID())){
	%>
				<li>
				<a href="survey.jsp?ovID=<%= StringEscapeUtils.escapeHtml("" + (ov.getVisitID())) %>&ovDate=<%= StringEscapeUtils.escapeHtml("" + (ov.getVisitDateStr())) %>">
					<img class="icon" style="border:0px" src="/iTrust/image/icons/survey.png" /></a>
				<a href="survey.jsp?ovID=<%= StringEscapeUtils.escapeHtml("" + (ov.getVisitID())) %>&ovDate=<%= StringEscapeUtils.escapeHtml("" + (ov.getVisitDateStr())) %>">
					Survey
				</a> for <%= StringEscapeUtils.escapeHtml("" + (ov.getVisitDateStr())) %>
				</li>
	<%
			}
		}
	%>
	<%
	}
	else {
	%>
		<i>No Unfinished Surveys</i>
	<%
	}
	%>
	</ul>
	</div>
<!-- End Office Visit Survey Information -->

  </div>
</div>