<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri='/WEB-INF/cewolf.tld' prefix='cewolf'%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Date"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.OfficeVisitBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.ProcedureBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.PersonnelBean"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewMyRecordsAction" %>

<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>


<%@include file="/global.jsp"%>
<%
	pageTitle = "iTrust - Print My Immunization";
		
		
		String pid = request.getParameter("immunizationRecordIDToReportForPrinting");
		long currentID = Long.parseLong(pid);
		ViewMyRecordsAction action = new ViewMyRecordsAction(prodDAO, currentID);
		PatientBean pb = new PatientBean();
		pb = action.getPatient(currentID);
		String first = pb.getFirstName();
		String last = pb.getLastName();
		String printer = first+" "+last;
		out.println("Immunization Report for: "+printer);
		java.util.Date d= new java.util.Date();
		List<OfficeVisitBean> officeVisits = action.getAllOfficeVisits();
		
		loggingAction.logEvent(TransactionType.PATIENT_PRINT_IMMUNIZATION_RECORDS, loggedInMID, currentID, "");

%>

<br>
<table class="fTable" align="center">
	<tr>
		<th colspan="3">Immunizations</th>
	</tr>
	<tr class="subHeader">
  		<td style="width:1in" align="left">CPT Code</th>
	 	<td style="width:3in" align="center">Description</th>
   		<td style="width:1in" align="left">Date Received</th>
  	</tr>
 <%
 	out.println(new Timestamp(d.getTime()));
boolean hasNoData = true;
for (OfficeVisitBean ov: officeVisits) {
	List<ProcedureBean> ovProcs = action.getProcedures(ov.getVisitID());
	for (ProcedureBean proc: ovProcs) {
		if (null != proc.getAttribute() && proc.getAttribute().equals("immunization")) {
			hasNoData=false;
%>
	<tr>
		<td algin="center"><%= StringEscapeUtils.escapeHtml("" + (proc.getCPTCode())) %></td>
		<td><%= StringEscapeUtils.escapeHtml("" + (proc.getDescription() )) %></td>
		<td><%= StringEscapeUtils.escapeHtml("" + (proc.getDate() )) %></td>
		<td>
		<%
			Date date = new Date();
			date.setYear(date.getYear()-1);
			if(proc.getDate().after(date)){
		%>
		<a href="reportAdverseEvent.jsp?presID=<%= StringEscapeUtils.escapeHtml("" + (proc.getDescription())) %>&HCPMID=<%= StringEscapeUtils.escapeHtml("" + (ov.getHcpID() )) %>&code=<%= StringEscapeUtils.escapeHtml("" + (proc.getCPTCode())) %>">Report</a>	
	
<%
		}%></td></tr><% }
	}
}
if(hasNoData) {
%>
	<tr>
		<td colspan=4 align=center>
			No Data
		</td>
	</tr>
<%
}


%>
</table>

</body>
</html>