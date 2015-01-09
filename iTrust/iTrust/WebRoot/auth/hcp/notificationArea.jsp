<%@page import="edu.ncsu.csc.itrust.action.ViewMyMessagesAction"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewMyApptsAction"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewReceivingReferralsAction"%>
<%@page import="edu.ncsu.csc.itrust.action.DeclareHCPAction"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewMyRemoteMonitoringListAction"%>
<%@page import="edu.ncsu.csc.itrust.action.EditRepresentativesAction"%>
<%@page import="edu.ncsu.csc.itrust.action.LabProcHCPAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.MessageBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.ApptBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.PersonnelBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.RemoteMonitoringDataBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewApptRequestsAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.ApptRequestBean"%>

<%
ViewMyMessagesAction messageAction = new ViewMyMessagesAction(prodDAO, loggedInMID.longValue());
LabProcHCPAction lpaction = new LabProcHCPAction(prodDAO, loggedInMID.longValue());
ViewReceivingReferralsAction referralAction = new ViewReceivingReferralsAction(prodDAO, loggedInMID);
ViewMyApptsAction apptAction = new ViewMyApptsAction(prodDAO, loggedInMID.longValue());
List <ApptBean> appointments = apptAction.getAppointments(loggedInMID.longValue());
ViewMyRemoteMonitoringListAction remoteMonitoringAction = new ViewMyRemoteMonitoringListAction(prodDAO, loggedInMID.longValue());
List<RemoteMonitoringDataBean> remoteData = remoteMonitoringAction.getPatientsData();
loggingAction.logEvent(TransactionType.NOTIFICATIONS_VIEW, loggedInMID.longValue(), 0, "");
ViewApptRequestsAction reqAction = new ViewApptRequestsAction(loggedInMID.longValue(), prodDAO);
List<ApptRequestBean> reqs = reqAction.getApptRequests();


int bucketPhysiologic = 0;
int bucketWeightPedometer = 0;
int referralCount = referralAction.getReferralsForReceivingHCPUnread();

for(RemoteMonitoringDataBean datum: remoteData) {
	if (datum.getSystolicBloodPressure() > 0 || datum.getDiastolicBloodPressure() > 0 || datum.getGlucoseLevel() > 0) {
		bucketPhysiologic++;
	}
	if (datum.getWeight() > 0 || datum.getPedometerReading() > 0){
		bucketWeightPedometer++;
	}
}

Date rightNow = new Date();
int pendingLabProcsCount = lpaction.getPendingCount();
%>

<div id="notificationArea" style="float:right; width:40%;background-color:white;" class="rightArea">
  
    <h2>Notifications</h2>
	<div id="areaContainer">
<!-- Begin Message Notification -->    
    <div class="contentBlock" style="margin:0.3em;">
    <ul>
<% if(reqAction.getNumRequests(reqs) == 0) { %>
	<li><img class="icon" src="/iTrust/image/icons/inboxEmpty.png" style="border:0px;">
	No appointment requests.</li>
<%	} else { %>    
	<li><a href="/iTrust/auth/hcp-patient/messageInbox.jsp">
	<img class="icon" src="/iTrust/image/icons/inboxUnread.png" style="border:0px;"></a>
    <a href="/iTrust/auth/hcp-patient/messageInbox.jsp"> 
<%= StringEscapeUtils.escapeHtml("" + (reqAction.getNumRequests(reqs))) %></a>
	Appointment requests.
	</li>
<%	} %>
<% if(messageAction.getUnreadCount() == 0) { %>
	<li><img class="icon" src="/iTrust/image/icons/inboxEmpty.png" style="border:0px;">
	No unread messages.</li>
<%	} else { %>    
	<li><a href="/iTrust/auth/hcp-patient/messageInbox.jsp">
	<img class="icon" src="/iTrust/image/icons/inboxUnread.png" style="border:0px;"></a>
    <a href="/iTrust/auth/hcp-patient/messageInbox.jsp">
    <%= StringEscapeUtils.escapeHtml("" + (messageAction.getUnreadCount())) %></a>
    Unread messages.
    </li>
<%	} %>

<% if(pendingLabProcsCount == 0) { %>
    <li>
       <img class="icon" src="/iTrust/image/icons/beaker.png" style="border:0px; margin: 0 0 0 -2px;">
       No pending lab procedures.
    </li>
<%  } else { %>
        <li>
          <a href="LabProcHCP.jsp?filterPatients=all"><img class="icon" src="/iTrust/image/icons/beaker.png" style="border:0px; margin: 0 0 0 -2px;"></a>
          <a href="LabProcHCP.jsp?filterPatients=all">
            <%= StringEscapeUtils.escapeHtml("" + pendingLabProcsCount) %></a>
          pending lab procedure<%= ((pendingLabProcsCount > 1) ? "s" : "") %>.
        </li>
 <% } %>
<%if(referralCount == 0) { %>
 	<li>
	   <img class="icon" src="/iTrust/image/icons/icon-doctor.png" style="border:0px;">
        No new referrals.
	</li>
<%} else { %>
     <li>
		  <a href="/iTrust/auth/hcp-uap/viewReceivedReferrals.jsp"><img class="icon" src="/iTrust/image/icons/icon-doctor.png" style="border:0px; margin: 0 0 0 ;"></a>
		  <a href="/iTrust/auth/hcp-uap/viewReceivedReferrals.jsp">
		    <%= StringEscapeUtils.escapeHtml("" + referralCount) %></a>
		  new received referral<%= ((referralCount > 1) ? "s" : "") %>.
	</li>
 <% } %>
    </ul>
    </div>
<!-- End Message Notification -->

<!-- Begin Today's Appointment Information -->
	<span class="subheading" style="font-weight: bold;">Today's Appointments</span><br />
	<div class="contentBlock" style="margin:0.3em;">
	<ul>
	<%
	session.setAttribute("appts", appointments);
	int index = 0;
	DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		for(ApptBean appt : appointments) {
			Date beanDate = new Date(appt.getDate().getTime());
			if (sdf.format(rightNow).equals(sdf.format(beanDate))) {
				Timestamp apptDate = appt.getDate();
				SimpleDateFormat dateFormat = new SimpleDateFormat();
				dateFormat.applyPattern("hh:mmaa");
				String dateString = dateFormat.format(apptDate);
				%>
				<li>
					<a href="/iTrust/auth/hcp/viewAppt.jsp?apt=<%= StringEscapeUtils.escapeHtml("" + appt.getApptID()) %>">
						<img class="icon" style="border:0px;" src="/iTrust/image/icons/appointment.png" /></a>
					<a href="/iTrust/auth/hcp/viewAppt.jsp?apt=<%= StringEscapeUtils.escapeHtml("" + appt.getApptID()) %>">
						<%= StringEscapeUtils.escapeHtml("" + (dateString )) %>
					</a> - <%= StringEscapeUtils.escapeHtml("" + (appt.getApptType() )) %>
				</li>
				<%
			}
			index++;
		}
%>
	</ul>
	</div>
<!-- End Today's Appointment Information -->

<!-- Being Telemedicine Reports Information -->	
	<span class="subheading" style="font-weight: bold;">Today's Telemedicine Reports</span><br />
	<div class="contentBlock" style="margin:0.3em;">
	<ul>
	<li>
	   <a href="/iTrust/auth/hcp/monitorPatients.jsp">
	   <img class="icon" style="border:0px" src="/iTrust/image/icons/telemedicine.png"></a>
	   <a href="/iTrust/auth/hcp/monitorPatients.jsp">
	   <%= StringEscapeUtils.escapeHtml("" + (bucketPhysiologic )) %> physiological status reports</a></li>
	<li>
	   <a href="/iTrust/auth/hcp/monitorPatients.jsp">
	   <img class="icon" style="border:0px" src="/iTrust/image/icons/physicalActivity.png"></a>
	   <a href="/iTrust/auth/hcp/monitorPatients.jsp">
	   <%= StringEscapeUtils.escapeHtml("" + (bucketWeightPedometer )) %> weight/pedometer status reports</a></li>
	</ul>
	</div>	
<!-- End Telemedicine Reports Information -->
  </div>
</div>