<%@taglib uri="/WEB-INF/tags.tld" prefix="itrust"%>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="edu.ncsu.csc.itrust.action.EditPHRAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.PersonnelBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.AllergyBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.FamilyMemberBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.HealthRecord"%>
<%@page import="edu.ncsu.csc.itrust.beans.OfficeVisitBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.ProcedureBean"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewMyRecordsAction"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO"%>

<%@page import="java.text.DecimalFormat"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.action.EditHealthHistoryAction"%>
<%@page import="edu.ncsu.csc.itrust.BeanBuilder"%>
<%@page import="edu.ncsu.csc.itrust.beans.forms.HealthRecordForm"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="edu.ncsu.csc.itrust.enums.TransactionType" %>


<%@include file="/global.jsp"%>

<%
	pageTitle = "iTrust - View My Records";
%>

<%@include file="/header.jsp"%>

<%

session.removeAttribute("personnelList");
Long originalLoggedInMID = loggedInMID;

	String representee = request.getParameter("rep");
	boolean isRepresenting = false;
	if (representee != null && !"".equals(representee)) {
		int representeeIndex = Integer.parseInt(representee);
		List<PatientBean> representees = (List<PatientBean>) session.getAttribute("representees");
		if(representees != null) {
			loggedInMID = new Long("" + representees.get(representeeIndex).getMID());
//			session.removeAttribute("representees");
			isRepresenting = true;
//		loggedInMID = new Long(action.representPatient(representee));
%>
<span >You are currently viewing your representee's records</span><br />
<%
		}
	}
	
	PatientBean patient = new PatientDAO(prodDAO).getPatient(loggedInMID.longValue());
	DateFormat df = DateFormat.getDateInstance();
	ViewMyRecordsAction action = new ViewMyRecordsAction(prodDAO, loggedInMID.longValue());

	patient = action.getPatient();
	List<HealthRecord> records = action.getAllHealthRecords();
	List<OfficeVisitBean> officeVisits = action.getAllOfficeVisits();
	List<FamilyMemberBean> family = action.getFamilyHistory();
	List<AllergyBean> allergies = action.getAllergies();
	List<PatientBean> represented = action.getRepresented();
	
	loggingAction.logEvent(TransactionType.MEDICAL_RECORD_VIEW, originalLoggedInMID, patient.getMID(), "");
%> 

<%
if (request.getParameter("message") != null) {
%>
	<div class="iTrustMessage" style="font-size: 24px;" align=center>
		<%= StringEscapeUtils.escapeHtml("" + (request.getParameter("message") )) %>
	</div>
<%
}
%>
<br />
<table align=center>
	<tr> <td>
	<div style="float:left; margin-right:5px;">
		<table class="fTable" border=1 align="center">
			<tr>
				<th colspan="2" >Patient Information</th>
			</tr>
			<tr>
				<td class="subHeaderVertical">Name:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (patient.getFullName())) %></td>
			</tr>
			<tr>
				<td  class="subHeaderVertical">Address:</td>
				<td >
					<%= StringEscapeUtils.escapeHtml("" + (patient.getStreetAddress1())) %><br />
					<%="".equals(patient.getStreetAddress2()) ? ""
						: patient.getStreetAddress2() + "<br />"%>
					<%= StringEscapeUtils.escapeHtml("" + (patient.getStreetAddress3())) %><br />
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Phone:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (patient.getPhone())) %></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Email:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (patient.getEmail())) %></td>
			</tr>
			<tr>
				<th colspan="2">
					Insurance Information
				</th>
			</tr>
			<tr>
				<td class="subHeaderVertical">
					Provider Name:
				</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (patient.getIcName())) %></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Address:</td>
				<td >
					<%= StringEscapeUtils.escapeHtml("" + (patient.getIcAddress1())) %><br />
					<%="".equals(patient.getIcAddress2()) ? "" : patient
						.getIcAddress2()
						+ "<br />"%>
					<%= StringEscapeUtils.escapeHtml("" + (patient.getIcAddress3())) %><br />
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Phone:</td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (patient.getIcPhone())) %></td>
			</tr>
		</table>
	</div>
	<div style="float: left; margin-left: 5px;">
		<table class="fTable" border=1 align="center">
			<tr>
				<th>Office Visits</th>
				<th>Survey</th>
			</tr>
<%
	for (OfficeVisitBean ov : officeVisits) {
%>
			<tr>
				<td >
					<a href="viewOfficeVisit.jsp?ovID=<%= StringEscapeUtils.escapeHtml("" + (ov.getVisitID())) %><%=isRepresenting ? "&repMID=" + loggedInMID.longValue() : "" %>"><%= StringEscapeUtils.escapeHtml("" + (df.format(ov.getVisitDate()))) %></a></td>
<%
		if (action.isSurveyCompleted(ov.getVisitID())) {
%>
				<td>&nbsp;</td>
<%
		} else {
%>
				<td >
					<a href="survey.jsp?ovID=<%= StringEscapeUtils.escapeHtml("" + (ov.getVisitID())) %>&ovDate=<%= StringEscapeUtils.escapeHtml("" + (df.format(ov.getVisitDate()))) %>">
						Complete Visit Survey
					</a>
				</td>
<%
		}
	}
%>
			</tr>
			<tr>
				<td colspan=2 align=center>
					<a href="viewPrescriptionRecords.jsp?<%= StringEscapeUtils.escapeHtml("" + (isRepresenting ? "&rep=" + loggedInMID.longValue() : "" )) %>">
						Get Prescriptions
					</a>
				</td>
			</tr>
		</table>
	</div>
	</td> </tr>
</table>

<br />
<br />
<table class="fTable" align="center" >
	<tr>
		<th colspan="9">
			Family Medical History
		</th>
	</tr>
	<tr class="subHeader">
		<td>Name</td>
		<td>Relation</td>
		<td>High Blood Pressure</td>
		<td>High Cholesterol</td>
		<td>Diabetes</td>
		<td>Cancer</td>
		<td>Heart Disease</td>
		<td>Smoker</td>
		<td>Cause of Death</td>
	</tr>
	<%
		if (family.size() == 0) {
	%>
	<tr>
		<td colspan="9" style="text-align: center;">
			No Relations on record
		</td>
	</tr>
	<%
		} else {
			for (FamilyMemberBean member : family) {
	%>
	<tr>
		<td ><%= StringEscapeUtils.escapeHtml("" + (member.getFullName())) %></td>
		<td ><%= StringEscapeUtils.escapeHtml("" + (member.getRelation())) %></td>
		<td  align=center><%=action.doesFamilyMemberHaveHighBP(member) ? "x"
							: ""%></td>
		<td  align=center><%=action
									.doesFamilyMemberHaveHighCholesterol(member) ? "x"
							: ""%></td>
		<td  align=center><%=action
									.doesFamilyMemberHaveDiabetes(member) ? "x"
							: ""%></td>
		<td  align=center><%=action.doesFamilyMemberHaveCancer(member) ? "x"
							: ""%></td>
		<td  align=center><%=action
									.doesFamilyMemberHaveHeartDisease(member) ? "x"
							: ""%></td>
		<td  align=center><%=action.isFamilyMemberSmoker(member) ? "x"
					: ""%></td>
		<td ><%= StringEscapeUtils.escapeHtml("" + (action.getFamilyMemberCOD(member))) %></td>
	</tr>
	<%
			}
		}
	%>
</table>
<br />
<br />
<table align=center>
	<tr> <td>
	<div style="float:left; margin-right:5px;">
		<table class="fTable" align="center" >
			<tr>
				<th colspan="2">Allergies</th>
			</tr>
			<tr class="subHeader">
				<td>Allergy Description</td>
				<td>First Found</td>
			</tr>

<%
	if (allergies.size() == 0) {
%>
			<tr>
				<td colspan="2" >No Allergies on record</td>
			</tr>
<%
	} else {
		for (AllergyBean allergy : allergies) {
%>
			<tr>
				<td ><%= StringEscapeUtils.escapeHtml("" + (allergy.getDescription())) %></td>
				<td ><%= StringEscapeUtils.escapeHtml("" + (df.format(allergy.getFirstFound()))) %></td>
			</tr>
<%
		}
	}
%>
		</table>
	</div>
	
	<div style="float:left; margin-left:5px;">
		<table class="fTable">
			<tr>
				<th> Patients <%= StringEscapeUtils.escapeHtml("" + (patient.getFirstName())) %> Represents </th>
			</tr>
			<tr class="subHeader">
				<td>Patient</td>
			</tr>
<%
	if (represented.size() == 0) {
%>
			<tr>
				<td >
					<%= StringEscapeUtils.escapeHtml("" + (patient.getFirstName())) %> is not representing any patients
				</td>
			</tr>
<%
	} else {
		int index = 0;
		for (PatientBean p : represented) {
%>
			<tr>
				<td >
<%
	if(isRepresenting) {
%>
		<%= StringEscapeUtils.escapeHtml("" + (p.getFullName())) %>
<%
	} else {
%>
		<a href="viewMyRecords.jsp?rep=<%= StringEscapeUtils.escapeHtml("" + (index)) %>"><%= StringEscapeUtils.escapeHtml("" + (p.getFullName())) %></a>
<%
	}
%>
					
				</td>
			</tr>
<%
		index++;
		}
		if(!isRepresenting) {
			session.setAttribute("representees", represented);
		}
	}
%>
		</table>
	</div>
	</td></tr>
</table>
<br />
<br />




<%

//Get the action object
EditHealthHistoryAction actionHH = new EditHealthHistoryAction(prodDAO, 0, "" + loggedInMID);
int sex = 0;
if(actionHH.getSex().equals("Male"))
		sex = 1;
else if(actionHH.getSex().equals("Female"))
	sex = 2;
else 
	sex = -1;

//Get the current age of the patient
long patientAgeCurrent = TimeUnit.DAYS.convert(System.currentTimeMillis() - actionHH.getPatientDOB().getTime(), TimeUnit.MILLISECONDS) / 365;

//Go ahead and log the view action before anything else
loggingAction.logEvent(TransactionType.PATIENT_VIEW_HEALTH_METRICS, originalLoggedInMID, actionHH.getPid(), "none");

//Get the necessary records for this office visit
List<HealthRecord> patientHealthRecordsAll = actionHH.getAllHealthRecords(actionHH.getPid());
//And then sort into separate lists based on age
ArrayList<HealthRecord> patientHealthRecords0To3 = new ArrayList<HealthRecord>();
ArrayList<HealthRecord> patientHealthRecords3To12 = new ArrayList<HealthRecord>();
ArrayList<HealthRecord> patientHealthRecords12AndOlder = new ArrayList<HealthRecord>();
for (HealthRecord hr : patientHealthRecordsAll) {
	long ageAtRecording = TimeUnit.DAYS.convert(hr.getDateRecorded().getTime() - actionHH.getPatientDOB().getTime(), TimeUnit.MILLISECONDS) / 365;
	if (ageAtRecording < 3) {
		patientHealthRecords0To3.add(hr);
	}
	else if (ageAtRecording < 12) {
		patientHealthRecords3To12.add(hr);
	}
	else {
		patientHealthRecords12AndOlder.add(hr);
	}
}

%>

<!-- Now we get to the actual page elements -->

<%
//Only show a table at all if there are records to show
if (!(patientHealthRecords0To3.isEmpty() && patientHealthRecords3To12.isEmpty() && patientHealthRecords12AndOlder.isEmpty())) {
%>
<div align=center>
	<table align="center" class="fTable">
<%
	//Here we begin the section for 12 and older, but only if such records exist
	if ((!patientHealthRecords12AndOlder.isEmpty()) || patientAgeCurrent >= 12) {
%>
		<tr>
			<th colspan="11">Basic Health History: 12 years and older</th>
		</tr>
		<tr class="subHeader">
			<td>Office Visit<br />Date</td>
			<td>Weight</td>
			<td>Height</td>
			<td>BMI</td>
			<td>Weight Class</td>
			<td>Blood Pressure</td>
			<td>Household Smoking Status</td>
			<td>Smokes?</td>
			<td>HDL</td>
			<td>LDL</td>
			<td>Triglycerides</td>
		</tr>
<%
		//Start the loop for each record row
		for (HealthRecord hr : patientHealthRecords12AndOlder) {
			long ageAtRecording = TimeUnit.DAYS.convert(hr.getDateRecorded().getTime() - actionHH.getPatientDOB().getTime(), TimeUnit.MILLISECONDS);
			ageAtRecording = ageAtRecording/30;
%>
		<tr>
			<td align="center"><%=actionHH.getOfficeVisitDateStr(hr.getOfficeVisitID()) %></td>
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getWeight()+" lbs.("+actionHH.getPercentile(1, sex, ageAtRecording, hr.getWeightInKG())+"%)") %></td>
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getHeight()+" in.("+actionHH.getPercentile(2, sex, ageAtRecording, hr.getHeightInCM())+"%)") %></td>
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getBodyMassIndex()+"("+actionHH.getPercentile(4, sex, ageAtRecording, hr.getBodyMassIndex())+"%)") %></td>
			<td align="center"><%= StringEscapeUtils.escapeHtml(""+ actionHH.getBMICategoryString(sex, ageAtRecording, hr.getBodyMassIndex())) %></td>
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getBloodPressure()) %>mmHg</td>
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getHouseholdSmokingStatusDesc()) %></td>
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getSmokingStatusDesc()) %></td>
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getCholesterolHDL()) %></td>
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getCholesterolLDL()) %></td>
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getCholesterolTri()) %></td>
		</tr>
<%
		}
%></table>
		<br><%
		
	} //Ends the 12 and older section
	
	//Begin the 3 to 12 section
	if (!patientHealthRecords3To12.isEmpty()) {
%>
<div align=center>
	<table align="center" class="fTable">
		<tr>
			<th colspan="9">Basic Health History: 3 years to 12 years of age</th>
		</tr>
		<tr class="subHeader">
			<td>Office Visit<br />Date</td>
			<td colspan="">Weight</td>
			<td colspan="">Height</td>
			<td colspan="">BMI</td>
			<td colspan="">Weight Class</td>
			<td colspan="">Blood Pressure</td>
			<td colspan="">Household Smoking Status</td>
		</tr>
<%
		for (HealthRecord hr : patientHealthRecords3To12) {
			long ageAtRecording = TimeUnit.DAYS.convert(hr.getDateRecorded().getTime() - actionHH.getPatientDOB().getTime(), TimeUnit.MILLISECONDS);
			ageAtRecording = ageAtRecording/30;
%>	
		<tr>
			<td align="center"><%=actionHH.getOfficeVisitDateStr(hr.getOfficeVisitID()) %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getWeight()+" lbs.("+actionHH.getPercentile(1, sex, ageAtRecording, hr.getWeightInKG())+"%)") %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getHeight()+" in.("+actionHH.getPercentile(2, sex, ageAtRecording, hr.getHeightInCM())+"%)") %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getBodyMassIndex()+"("+actionHH.getPercentile(4, sex, ageAtRecording, hr.getBodyMassIndex())+"%)") %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + actionHH.getBMICategoryString(sex, ageAtRecording, hr.getBodyMassIndex())) %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getBloodPressure()) %>mmHg</td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getHouseholdSmokingStatusDesc()) %></td>
		</tr>
		
<%
		}
	} //Ends the 3 to 12 section
if ((!patientHealthRecords0To3.isEmpty()) || patientAgeCurrent < 3) {
%>
</table>
		<br>
<div align=center>
	<table align="center" class="fTable">
		<tr>
			<th colspan="8">Basic Health History: Birth to 3 years of age</th>
		</tr>
		<tr class="subHeader">
			<td>Office Visit<br />Date</td>
			<td colspan="">Weight</td>
			<td colspan="">Length</td>
			<td colspan="">BMI</td>
			<td colspan="">Weight Class</td>
			<td colspan="">Head Circumference</td>
			<td colspan="">Household Smoking Status</td>
		</tr>
<%
	for (HealthRecord hr : patientHealthRecords0To3) {

		double ageAtRecording = TimeUnit.DAYS.convert(hr.getDateRecorded().getTime() - actionHH.getPatientDOB().getTime(), TimeUnit.MILLISECONDS);
		ageAtRecording = ageAtRecording/30;

%>		<tr>
		<td align="center"><%=actionHH.getOfficeVisitDateStr(hr.getOfficeVisitID()) %></td> 
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getWeight()+"  lbs.("+actionHH.getPercentile(1, sex, ageAtRecording, hr.getWeightInKG())+"%)") %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getHeight()+"  in.("+actionHH.getPercentile(2, sex, ageAtRecording, hr.getHeightInCM())+"%)") %></td>
			<%
			if(ageAtRecording>=24)
			{
			%>
				<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getBodyMassIndex()+"("+actionHH.getPercentile(4, sex, ageAtRecording, hr.getBodyMassIndex())+"%)") %></td>
				<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + actionHH.getBMICategoryString(sex, ageAtRecording, hr.getBodyMassIndex())) %>
			<%}
			else{%>
				<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("N/A") %></td>
				<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("N/A")%></td>
				<%} %>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getHeadCircumference()+"  in.("+actionHH.getPercentile(3, sex, ageAtRecording, hr.getHeadCircumferenceInCM())+"%)") %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getHouseholdSmokingStatusDesc()) %></td>
			</tr>
<%
		}
	} //Ends the 0 to 3 section
} //Ends the table section
%>
	</table>
</div>







<br />
<br />
 <form method="post" name="immunizationReportGeneratorInfoForm" action="/iTrust/auth/printImmunizationReport.jsp">
 <input type="hidden" name="immunizationRecordIDToReportForPrinting" value="<%=loggedInMID %>" />
 </form>
 
<a href="#" onClick="document.immunizationReportGeneratorInfoForm.submit(); return false">Print Immunization Report</a>
<table class="fTable" align="center">
	<tr>
		<th colspan="4">Immunizations</th>
	</tr>
	<tr class="subHeader">
  		<td>CPT Code</th>
	 	<td>Description</th>
   		<td>Date Received</th>
   		<td>Adverse Event</th>
  	</tr>
<%
boolean hasNoData = true;
for (OfficeVisitBean ov: officeVisits) {
	List<ProcedureBean> ovProcs = action.getProcedures(ov.getVisitID());
	for (ProcedureBean proc: ovProcs) {
		if (null != proc.getAttribute() && proc.getAttribute().equals("immunization")) {
			hasNoData=false;
%>
	<tr>
		<td><%= StringEscapeUtils.escapeHtml("" + (proc.getCPTCode())) %></td>
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
<br />

<%@include file="/footer.jsp"%>
