<%@page import="edu.ncsu.csc.itrust.action.EditHealthHistoryAction" %>
<%@page import="edu.ncsu.csc.itrust.beans.HealthRecord" %>
<%@page import="edu.ncsu.csc.itrust.beans.forms.HealthRecordForm" %>
<%@page import="edu.ncsu.csc.itrust.enums.TransactionType" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>
<%@page import="java.util.concurrent.TimeUnit" %>

<%

//Create update and error message strings to deal with later
String updateHealthMetricMessage = "";
String updateHealthMetricErrorMessage = "";

//Get the action object
EditHealthHistoryAction actionHH = new EditHealthHistoryAction(prodDAO, loggedInMID.longValue(), pidString);

int sex = 0;
if(actionHH.getSex().equals("Male"))
		sex = 1;
else if(actionHH.getSex().equals("Female"))
	sex = 2;
else 
	sex = -1;
//Get the current age of the patient
long patientAgeCurrent = TimeUnit.DAYS.convert(System.currentTimeMillis() - actionHH.getPatientDOB().getTime(), TimeUnit.MILLISECONDS) / 365;

//If this is the first page load
//if (ovIDString == null || ovIDString.length() == 0 || ovIDString.equals("-1")) {
	
	//Go ahead and log the view action before anything else
	loggingAction.logEvent(TransactionType.VIEW_HEALTH_METRIC_IN_OV, loggedInMID.longValue(), actionHH.getPid(), "Patient Age: " + patientAgeCurrent);
	
//}

//If we are deleting a record
if ("healthMetricDeleteRecordForm".equals(submittedFormName)) {
	
	//Go ahead and log the edit
	loggingAction.logEvent(TransactionType.EDIT_HEALTH_METRIC, loggedInMID.longValue(), actionHH.getPid(), "Patient Age: " + patientAgeCurrent);
	
	//Remove the record
	actionHH.removeHealthRecord(Long.parseLong(request.getParameter("healthMetricDeleteRecordID")));
	
	//Set up the message
	updateHealthMetricMessage = "Record Removed";
	
}

//If we are adding or editing a record
if ("healthMetricAddRecordForm".equals(submittedFormName)) {
	
	//If we are adding, log one way
	if (request.getParameter("healthMetricAddRecordID").equalsIgnoreCase("0")) {
		loggingAction.logEvent(TransactionType.CREATE_HEALTH_METRIC, loggedInMID.longValue(), actionHH.getPid(), "Patient Age: " + patientAgeCurrent);
	}
	//otherwise, log the other way
	else {
		loggingAction.logEvent(TransactionType.EDIT_HEALTH_METRIC, loggedInMID.longValue(), actionHH.getPid(), "Patient Age: " + patientAgeCurrent);
	}
	
	
	//Create the health record form from the request variables
	HealthRecordForm hrf = new HealthRecordForm();
	hrf.setHeight(request.getParameter("healthMetricAddHeight"));
	hrf.setWeight(request.getParameter("healthMetricAddWeight"));
	hrf.setHeadCircumference(request.getParameter("healthMetricAddHeadCircumference"));
	hrf.setIsSmoker(request.getParameter("healthMetricAddSmokingStatus"));
	hrf.setHouseholdSmokingStatus(request.getParameter("healthMetricAddHouseholdSmokingStatus"));
	hrf.setBloodPressureN(request.getParameter("healthMetricAddBloodPressureN"));
	hrf.setBloodPressureD(request.getParameter("healthMetricAddBloodPressureD"));
	hrf.setCholesterolHDL(request.getParameter("healthMetricAddCholesterolHDL"));
	hrf.setCholesterolLDL(request.getParameter("healthMetricAddCholesterolLDL"));
	hrf.setCholesterolTri(request.getParameter("healthMetricAddCholesterolTri"));
	hrf.setOfficeVisitID(request.getParameter("healthMetricAddOfficeVisitID"));
	hrf.setRecordID(request.getParameter("healthMetricAddRecordID"));
	
	
	//Then submit and store the message
	updateHealthMetricMessage = actionHH.addHealthRecord(actionHH.getPid(), hrf);
	
	//If it didn't return "Information Recorded", there was an error.
	if (!updateHealthMetricMessage.equals("Information Recorded")) {
		updateHealthMetricErrorMessage = updateHealthMetricMessage;
		updateHealthMetricMessage = "";
	}
	
	
}

//Handle displaying update message
if (!"".equals(updateHealthMetricMessage)) {
	%>  <span class="iTrustMessage"><%= updateHealthMetricMessage %></span>  <%
}
//Handle displaying update error message
if (!"".equals(updateHealthMetricErrorMessage)) {
	%>  <span class="iTrustError"><%= updateHealthMetricErrorMessage %></span>  <%
}

//Get the necessary records for this office visit
List<HealthRecord> patientHealthRecordsAll = actionHH.getAllHealthRecords(actionHH.getPid());
//And then sort into separate lists based on age
ArrayList<HealthRecord> patientHealthRecords0To3 = new ArrayList<HealthRecord>();
ArrayList<HealthRecord> patientHealthRecords3To12 = new ArrayList<HealthRecord>();
ArrayList<HealthRecord> patientHealthRecords12AndOlder = new ArrayList<HealthRecord>();
for (HealthRecord hr : patientHealthRecordsAll) {
	long ageAtRecording = TimeUnit.DAYS.convert(hr.getDateRecorded().getTime() - actionHH.getPatientDOB().getTime(), TimeUnit.MILLISECONDS)/365;
	 
	//Exclude those metrics that aren't associated with this office visit
	if (hr.getOfficeVisitID() != ovaction.getOvID()) {
		continue;
	}
	
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

<!-- Before any page elements load, we go ahead and load the action function scripts -->

<script type="text/javascript">
	
	//Called by the delete record button
	function deleteRecord(recordID) {
		document.getElementById("healthMetricDeleteRecordID").value= recordID;
		document.forms["healthMetricDeleteRecordForm"].submit();
	}
	
	//Called by the edit record button
	function showEditRecordRow(recordID, ageGroup) {
		if (ageGroup === 3) {
			//First set up the values
			document.getElementById("healthMetricAddHeightInput3").value= document.getElementById("healthMetricHeightRecord" + recordID).value;
			document.getElementById("healthMetricAddWeightInput3").value= document.getElementById("healthMetricWeightRecord" + recordID).value;
			//Smoking status and household smoking status have to be handled differently, since they are drop downs
			document.getElementById("healthMetricAddBloodPressureNInput3").value= document.getElementById("healthMetricBloodPressureNRecord" + recordID).value;
			document.getElementById("healthMetricAddBloodPressureDInput3").value= document.getElementById("healthMetricBloodPressureDRecord" + recordID).value;
			document.getElementById("healthMetricAddCholesterolHDLInput3").value= document.getElementById("healthMetricCholesterolHDLRecord" + recordID).value;
			document.getElementById("healthMetricAddCholesterolLDLInput3").value= document.getElementById("healthMetricCholesterolLDLRecord" + recordID).value;
			document.getElementById("healthMetricAddCholesterolTriInput3").value= document.getElementById("healthMetricCholesterolTriRecord" + recordID).value;
		}
		if (ageGroup === 2) {
			//First set up the values
			document.getElementById("healthMetricAddHeightInput2").value= document.getElementById("healthMetricHeightRecord" + recordID).value;
			document.getElementById("healthMetricAddWeightInput2").value= document.getElementById("healthMetricWeightRecord" + recordID).value;
			//Smoking status and household smoking status have to be handled differently, since they are drop downs
			document.getElementById("healthMetricAddBloodPressureNInput2").value= document.getElementById("healthMetricBloodPressureNRecord" + recordID).value;
			document.getElementById("healthMetricAddBloodPressureDInput2").value= document.getElementById("healthMetricBloodPressureDRecord" + recordID).value;
		}
		if (ageGroup === 1) {
			//First set up the values
			document.getElementById("healthMetricAddHeightInput1").value= document.getElementById("healthMetricHeightRecord" + recordID).value;
			document.getElementById("healthMetricAddWeightInput1").value= document.getElementById("healthMetricWeightRecord" + recordID).value;
			document.getElementById("healthMetricAddHeadCircumferenceInput1").value= document.getElementById("healthMetricHeadCircumferenceRecord" + recordID).value;
			//Smoking status and household smoking status have to be handled differently, since they are drop downs
		}
		//Store the recordID in relevent field
		document.getElementById("healthMetricAddRecordIDInput" + ageGroup).value= recordID;
		//Hide the update/delete buttons for that row
		document.getElementById("updateRecordButton" + recordID).style.display= "none";
		document.getElementById("deleteRecordButton" + recordID).style.display= "none";
		//Change the add button to an update button
		document.getElementById("addRecordButton" + ageGroup).value= "Save Changes";
		//Then display the row (if not already displayed)
		document.getElementById("healthMetricInputRow" + ageGroup).style.display= "table-row";
	}
	
	// ageGroups
	//      3 -> 12 and older
	//      2 -> 3 years to 12 years
	//      1 -> 0 to 3 years
	function saveRecord(ageGroup) {
		if (ageGroup === 3) {
			document.getElementById("healthMetricAddOfficeVisitID").value= <%=officeVisitID %>;
			document.getElementById("healthMetricAddRecordID").value = document.getElementById("healthMetricAddRecordIDInput3").value;
			document.getElementById("healthMetricAddHeight").value = document.getElementById("healthMetricAddHeightInput3").value;
			document.getElementById("healthMetricAddWeight").value = document.getElementById("healthMetricAddWeightInput3").value;
			document.getElementById("healthMetricAddHeadCircumference").value = 0.1;
			document.getElementById("healthMetricAddSmokingStatus").value = document.getElementById("healthMetricAddSmokingStatusInput3").value;
			document.getElementById("healthMetricAddHouseholdSmokingStatus").value = document.getElementById("healthMetricAddHouseholdSmokingStatusInput3").value;
			document.getElementById("healthMetricAddBloodPressureN").value = document.getElementById("healthMetricAddBloodPressureNInput3").value;
			document.getElementById("healthMetricAddBloodPressureD").value = document.getElementById("healthMetricAddBloodPressureDInput3").value;
			document.getElementById("healthMetricAddCholesterolHDL").value = document.getElementById("healthMetricAddCholesterolHDLInput3").value;
			document.getElementById("healthMetricAddCholesterolLDL").value = document.getElementById("healthMetricAddCholesterolLDLInput3").value;
			document.getElementById("healthMetricAddCholesterolTri").value = document.getElementById("healthMetricAddCholesterolTriInput3").value;
			document.forms["healthMetricAddRecordForm"].submit();
		}
		if (ageGroup === 2) {
			document.getElementById("healthMetricAddOfficeVisitID").value= <%=officeVisitID %>;
			document.getElementById("healthMetricAddRecordID").value = document.getElementById("healthMetricAddRecordIDInput2").value;
			document.getElementById("healthMetricAddHeight").value = document.getElementById("healthMetricAddHeightInput2").value;
			document.getElementById("healthMetricAddWeight").value = document.getElementById("healthMetricAddWeightInput2").value;
			document.getElementById("healthMetricAddHeadCircumference").value = 0.1;
			document.getElementById("healthMetricAddSmokingStatus").value = 9;
			document.getElementById("healthMetricAddHouseholdSmokingStatus").value = document.getElementById("healthMetricAddHouseholdSmokingStatusInput2").value;
			document.getElementById("healthMetricAddBloodPressureN").value = document.getElementById("healthMetricAddBloodPressureNInput2").value;
			document.getElementById("healthMetricAddBloodPressureD").value = document.getElementById("healthMetricAddBloodPressureDInput2").value;
			document.getElementById("healthMetricAddCholesterolHDL").value = 0;
			document.getElementById("healthMetricAddCholesterolLDL").value = 0;
			document.getElementById("healthMetricAddCholesterolTri").value = 101;
			document.forms["healthMetricAddRecordForm"].submit();
		}
		if (ageGroup === 1) {
			document.getElementById("healthMetricAddOfficeVisitID").value= <%=officeVisitID %>;
			document.getElementById("healthMetricAddRecordID").value = document.getElementById("healthMetricAddRecordIDInput1").value;
			document.getElementById("healthMetricAddHeight").value = document.getElementById("healthMetricAddHeightInput1").value;
			document.getElementById("healthMetricAddWeight").value = document.getElementById("healthMetricAddWeightInput1").value;
			document.getElementById("healthMetricAddHeadCircumference").value = document.getElementById("healthMetricAddHeadCircumferenceInput1").value;
			document.getElementById("healthMetricAddSmokingStatus").value = 9;
			document.getElementById("healthMetricAddHouseholdSmokingStatus").value = document.getElementById("healthMetricAddHouseholdSmokingStatusInput1").value;
			document.getElementById("healthMetricAddBloodPressureN").value = 0;
			document.getElementById("healthMetricAddBloodPressureD").value = 0;
			document.getElementById("healthMetricAddCholesterolHDL").value = 0;
			document.getElementById("healthMetricAddCholesterolLDL").value = 0;
			document.getElementById("healthMetricAddCholesterolTri").value = 101;
			document.forms["healthMetricAddRecordForm"].submit();
		}
		
	}
	
</script>

<!-- These are the hidden forms which will actually be submitted -->

<form action="editOfficeVisit.jsp" id="healthMetricDeleteRecordForm" name="healthMetricDeleteRecordForm" method="post">
	<input type="hidden" name="formIsFilled" value="true" />
	<input type="hidden" name="formName" value="healthMetricDeleteRecordForm" />
	<input type="hidden" name="ovID" value="<%= StringEscapeUtils.escapeHtml("" + (ovaction.getOvID())) %>" />
	<input type="hidden" id="healthMetricDeleteRecordID" name="healthMetricDeleteRecordID" value="" />
</form>

<form action="editOfficeVisit.jsp" id="healthMetricAddRecordForm" name="healthMetricAddRecordForm" method="post">
	<input type="hidden" name="formIsFilled" value="true" />
	<input type="hidden" name="formName" value="healthMetricAddRecordForm" />
	<input type="hidden" name="ovID" value="<%= StringEscapeUtils.escapeHtml("" + (ovaction.getOvID())) %>" />
	<input type="hidden" id="healthMetricAddOfficeVisitID" name="healthMetricAddOfficeVisitID" value="" />
	<input type="hidden" id="healthMetricAddRecordID" name="healthMetricAddRecordID" value="" />
	<input type="hidden" id="healthMetricAddHeight" name="healthMetricAddHeight" value="" />
	<input type="hidden" id="healthMetricAddWeight" name="healthMetricAddWeight" value="" />
	<input type="hidden" id="healthMetricAddHeadCircumference" name="healthMetricAddHeadCircumference" value="" />
	<input type="hidden" id="healthMetricAddSmokingStatus" name="healthMetricAddSmokingStatus" value="" />
	<input type="hidden" id="healthMetricAddHouseholdSmokingStatus" name="healthMetricAddHouseholdSmokingStatus" value="" />
	<input type="hidden" id="healthMetricAddBloodPressureN" name="healthMetricAddBloodPressureN" value="" />
	<input type="hidden" id="healthMetricAddBloodPressureD" name="healthMetricAddBloodPressureD" value="" />
	<input type="hidden" id="healthMetricAddCholesterolHDL" name="healthMetricAddCholesterolHDL" value="" />
	<input type="hidden" id="healthMetricAddCholesterolLDL" name="healthMetricAddCholesterolLDL" value="" />
	<input type="hidden" id="healthMetricAddCholesterolTri" name="healthMetricAddCholesterolTri" value="" />
</form>

<!-- Now we get to the actual page elements -->

<%
//Here we begin the section for 12 and older, but only if such records exist
if ((!patientHealthRecords12AndOlder.isEmpty()) || patientAgeCurrent >= 12) {
%>
<div align=center>
	<table align="center" class="fTable">
		<tr>
			<th colspan="12">Basic Health History: 12 years and older</th>
		</tr>
		<tr class="subHeader">
			<td>Office Visit<br />Date</td>
			<td>Weight</td>
			<td>Height</td>
			<td>BMI</td>
			<td>Weight Class</td>
			<td>Blood Pressure</td>
			<td>Household Smoking Statu</td>
			<td>Smokes?</td>
			<td>HDL</td>
			<td>LDL</td>
			<td>Triglycerides</td>
			<td>Actions</td>
		</tr>
<%
	//Start the loop for each record row
	for (HealthRecord hr : patientHealthRecords12AndOlder) {
		long ageAtRecording = TimeUnit.DAYS.convert(hr.getDateRecorded().getTime() - actionHH.getPatientDOB().getTime(), TimeUnit.MILLISECONDS);
		ageAtRecording = ageAtRecording/30;
		
%>			
		<tr>
			<!-- This is for use by the update function.  Stores values in a retrievable manner -->
			<input type="hidden" id="healthMetricWeightRecord<%=hr.getRecordID() %>" name="healthMetricWeightRecord<%=hr.getRecordID() %>" value="<%=hr.getWeight() %>" />
			<input type="hidden" id="healthMetricHeightRecord<%=hr.getRecordID() %>" name="healthMetricHeightRecord<%=hr.getRecordID() %>" value="<%=hr.getHeight() %>" />
			<input type="hidden" id="healthMetricBloodPressureNRecord<%=hr.getRecordID() %>" name="healthMetricBloodPressureNRecord<%=hr.getRecordID() %>" value="<%=hr.getBloodPressureN() %>" />
			<input type="hidden" id="healthMetricBloodPressureDRecord<%=hr.getRecordID() %>" name="healthMetricBloodPressureDRecord<%=hr.getRecordID() %>" value="<%=hr.getBloodPressureD() %>" />
			<input type="hidden" id="healthMetricHouseholdSmokingStatusRecord<%=hr.getRecordID() %>" name="healthMetricHouseholdSmokingStatusRecord<%=hr.getRecordID() %>" value="<%=hr.getHouseholdSmokingStatus() %>" />
			<input type="hidden" id="healthMetricSmokingStatusRecord<%=hr.getRecordID() %>" name="healthMetricSmokingStatusRecord<%=hr.getRecordID() %>" value="<%=hr.getSmokingStatus() %>" />
			<input type="hidden" id="healthMetricCholesterolHDLRecord<%=hr.getRecordID() %>" name="healthMetricCholesterolHDLRecord<%=hr.getRecordID() %>" value="<%=hr.getCholesterolHDL() %>" />
			<input type="hidden" id="healthMetricCholesterolLDLRecord<%=hr.getRecordID() %>" name="healthMetricCholesterolLDLRecord<%=hr.getRecordID() %>" value="<%=hr.getCholesterolLDL() %>" />
			<input type="hidden" id="healthMetricCholesterolTriRecord<%=hr.getRecordID() %>" name="healthMetricCholesterolTriRecord<%=hr.getRecordID() %>" value="<%=hr.getCholesterolTri() %>" />
			<!-- This is what is actually displayed to the user -->
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
			<td>
				<input id="deleteRecordButton<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>" type="submit" value="Delete" onClick="deleteRecord(<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>)"/>
				<input id="updateRecordButton<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>" type="submit" value="Modify" onClick="showEditRecordRow(<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>, 3)" />
			</td>
		</tr>
<%
	}
	//Now build the insert/update record row
%>
	
		<!-- Only displayed if the patient is currently the appropriate age -->
		<tr id="healthMetricInputRow3" <%= (patientAgeCurrent >= 12) ? "" : "style=\"display:none\"" %>>
			<input type="hidden" id="healthMetricAddRecordIDInput3" name="healthMetricAddRecordIDInput3" value="0" />
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + ovbean.getVisitDateStr()) %></td>
			<td align="center"><input type="text" id="healthMetricAddWeightInput3" name="healthMetricAddWeightInput3" value="" style="width: 30px"/></td>
			<td align="center"><input type="text" id="healthMetricAddHeightInput3" name="healthMetricAddHeightInput3" value="" style="width: 30px"/></td>
			<td colspan="3" align="right">
				<input type="text" id="healthMetricAddBloodPressureNInput3" name="healthMetricAddBloodPressureNInput3" value="" style="width: 30px"/>
				 / <input type="text" id="healthMetricAddBloodPressureDInput3" name="healthMetricAddBloodPressureDInput3" value="" style="width: 30px"/>
			</td>
			<td align="center">
				<select name="healthMetricAddHouseholdSmokingStatusInput3" id="healthMetricAddHouseholdSmokingStatusInput3">
	                	<option value="">Select a Household Smoking Status</option>
	                    <option value="1">1 - non-smoking household</option>
	               		<option value="2">2 - outdoor smokers</option>
	               		<option value="3">3 - indoor smokers</option>
	               		<option value="9">9 - unknown</option>
				</select>
			</td>
			<td align="center">
				<select name="healthMetricAddSmokingStatusInput3" id="healthMetricAddSmokingStatusInput3">
                	<option value="">Select a Smoking Status</option>
                    <option value="1">1 - Current every day smoker</option>
               		<option value="2">2 - Current some day smoker</option>
               		<option value="3">3 - Former smoker</option>
               		<option value="4">4 - Never smoker</option>
               		<option value="5">5 - Smoker, current status unknown</option>
               		<option value="9">9 - Unknown if ever smoked</option>
	        	</select>
			</td>
			<td align="center"><input type="text" id="healthMetricAddCholesterolHDLInput3" name="healthMetricAddCholesterolHDLInput3" value="" style="width: 30px"/></td>
			<td align="center"><input type="text" id="healthMetricAddCholesterolLDLInput3" name="healthMetricAddCholesterolLDLInput3" value="" style="width: 30px"/></td>
			<td align="center"><input type="text" id="healthMetricAddCholesterolTriInput3" name="healthMetricAddCholesterolTriInput3" value="" style="width: 30px"/></td>
			<td align="center"><input id="addRecordButton3" type="submit" value="Add Record" onClick="saveRecord(3)"/></td>
		</tr>
	</table>
	<br />
</div>
<%
} //Ends the 12 and older section

//Begin the 3 to 12 section
if ((!patientHealthRecords3To12.isEmpty()) || (patientAgeCurrent >= 3 && patientAgeCurrent < 12)) {
%>
<div align=center>
	<table align="center" class="fTable">
		<tr>
			<th colspan="8">Basic Health History: 3 years to 12 years of age</th>
		</tr>
		<tr class="subHeader">
			<td>Office Visit<br />Date</td>
			<td colspan="">Weight</td>
			<td colspan="">Height</td>
			<td colspan="">BMI</td>
			<td colspan="">Weight Class</td>
			<td colspan="">Blood Pressure</td>
			<td colspan="">Household Smoking Status</td>
			<td>Actions</td>
		</tr>
<%
	for (HealthRecord hr : patientHealthRecords3To12) {
		double ageAtRecording = TimeUnit.DAYS.convert(hr.getDateRecorded().getTime() - actionHH.getPatientDOB().getTime(), TimeUnit.MILLISECONDS);
		ageAtRecording = ageAtRecording/30;
%>	
		<tr>
			<!-- This is for use by the update function.  Stores values in a retrievable manner -->
			<input type="hidden" id="healthMetricWeightRecord<%=hr.getRecordID() %>" name="healthMetricWeightRecord<%=hr.getRecordID() %>" value="<%=hr.getWeight() %>" />
			<input type="hidden" id="healthMetricHeightRecord<%=hr.getRecordID() %>" name="healthMetricHeightRecord<%=hr.getRecordID() %>" value="<%=hr.getHeight() %>" />
			<input type="hidden" id="healthMetricBloodPressureNRecord<%=hr.getRecordID() %>" name="healthMetricBloodPressureNRecord<%=hr.getRecordID() %>" value="<%=hr.getBloodPressureN() %>" />
			<input type="hidden" id="healthMetricBloodPressureDRecord<%=hr.getRecordID() %>" name="healthMetricBloodPressureDRecord<%=hr.getRecordID() %>" value="<%=hr.getBloodPressureD() %>" />
			<input type="hidden" id="healthMetricHouseholdSmokingStatusRecord<%=hr.getRecordID() %>" name="healthMetricHouseholdSmokingStatusRecord<%=hr.getRecordID() %>" value="<%=hr.getHouseholdSmokingStatus() %>" />
			<!-- This is what is actually displayed to the user -->
			<td align="center"><%=actionHH.getOfficeVisitDateStr(hr.getOfficeVisitID()) %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getWeight()+" lbs.("+actionHH.getPercentile(1, sex, ageAtRecording, hr.getWeightInKG())+"%)") %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getHeight()+" in.("+actionHH.getPercentile(2, sex, ageAtRecording, hr.getHeightInCM())+"%)") %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getBodyMassIndex()+"("+actionHH.getPercentile(4, sex, ageAtRecording, hr.getBodyMassIndex())+"%)") %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + actionHH.getBMICategoryString(sex, ageAtRecording, hr.getBodyMassIndex())) %></td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getBloodPressure()) %>mmHg</td>
			<td colspan="" align="center"><%= StringEscapeUtils.escapeHtml("" + hr.getHouseholdSmokingStatusDesc()) %></td>
			<td>
				<input id="deleteRecordButton<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>" type="submit" value="Delete" onClick="deleteRecord(<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>)"/>
				<input id="updateRecordButton<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>" type="submit" value="Modify" onClick="showEditRecordRow(<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>, 2)" />
			</td>
		</tr>
<%
	}
	//Now build the insert/update record row
%>
		<!-- Only displayed if the patient is currently the appropriate age -->
		<tr id="healthMetricInputRow2" <%= (patientAgeCurrent >= 3 && patientAgeCurrent < 12) ? "" : "style=\"display:none\"" %>>
			<input type="hidden" id="healthMetricAddRecordIDInput2" name="healthMetricAddRecordIDInput2" value="0" />
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + ovbean.getVisitDateStr()) %></td>
			<td colspan="" align="center"><input type="text" id="healthMetricAddWeightInput2" name="healthMetricAddWeightInput2" value="" style="width: 30px"/></td>
			<td colspan="" align="center"><input type="text" id="healthMetricAddHeightInput2" name="healthMetricAddHeightInput2" value="" style="width: 30px"/></td>
			<td colspan="3" align="right">
				<input type="text" id="healthMetricAddBloodPressureNInput2" name="healthMetricAddBloodPressureNInput2" value="" style="width: 30px"/>
				 / <input type="text" id="healthMetricAddBloodPressureDInput2" name="healthMetricAddBloodPressureDInput2" value="" style="width: 30px"/>
			</td>
			<td colspan="2" align="center">
				<select name="healthMetricAddHouseholdSmokingStatusInput2" id="healthMetricAddHouseholdSmokingStatusInput2">
	                	<option value="">Select a Household Smoking Status</option>
	                    <option value="1">1 - non-smoking household</option>
	               		<option value="2">2 - outdoor smokers</option>
	               		<option value="3">3 - indoor smokers</option>
	               		<option value="9">9 - unknown</option>
	        	</select>
			</td>
			<td align="center"><input id="addRecordButton2" type="submit" value="Add Record" onClick="saveRecord(2)"/></td>
		</tr>
	</table>
	<br />
</div>
<%
} //Ends the 3 to 12 section

//Begin the 0 to 3 section
if ((!patientHealthRecords0To3.isEmpty()) || patientAgeCurrent < 3) {
%>
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
			<td>Actions</td>
		</tr>
<%
	for (HealthRecord hr : patientHealthRecords0To3) {
		double ageAtRecording = TimeUnit.DAYS.convert(hr.getDateRecorded().getTime() - actionHH.getPatientDOB().getTime(), TimeUnit.MILLISECONDS);
		ageAtRecording = ageAtRecording/30;
%>
		<tr>
			<!-- This is for use by the update function.  Stores values in a retrievable manner -->
			<input type="hidden" id="healthMetricWeightRecord<%=hr.getRecordID() %>" name="healthMetricWeightRecord<%=hr.getRecordID() %>" value="<%=hr.getWeight() %>" />
			<input type="hidden" id="healthMetricHeightRecord<%=hr.getRecordID() %>" name="healthMetricHeightRecord<%=hr.getRecordID() %>" value="<%=hr.getHeight() %>" />
			<input type="hidden" id="healthMetricHeadCircumferenceRecord<%=hr.getRecordID() %>" name="healthMetricHeadCircumferenceRecord<%=hr.getRecordID() %>" value="<%=hr.getHeadCircumference() %>" />
			<input type="hidden" id="healthMetricHouseholdSmokingStatusRecord<%=hr.getRecordID() %>" name="healthMetricHouseholdSmokingStatusRecord<%=hr.getRecordID() %>" value="<%=hr.getHouseholdSmokingStatus() %>" />
			<!-- This is what is actually displayed to the user -->
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
			<td>
				<input id="deleteRecordButton<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>" type="submit" value="Delete" onClick="deleteRecord(<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>)"/>
				<input id="updateRecordButton<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>" type="submit" value="Modify" onClick="showEditRecordRow(<%= StringEscapeUtils.escapeHtml("" + hr.getRecordID()) %>, 1)" />
			</td>
		</tr>
<%
	}
	//Now build the insert/update record row
%>
		<!-- Only displayed if the patient is currently the appropriate age -->
		<tr id="healthMetricInputRow1" <%= (patientAgeCurrent < 3) ? "" : "style=\"display:none\"" %>>
			<input type="hidden" id="healthMetricAddRecordIDInput1" name="healthMetricAddRecordIDInput1" value="0" />
			<td align="center"><%= StringEscapeUtils.escapeHtml("" + ovbean.getVisitDateStr()) %></td>
			<td colspan="2" align="center"><input type="text" id="healthMetricAddWeightInput1" name="healthMetricAddWeightInput1" value="" style="width: 30px"/></td>
			<td colspan="2" align="center"><input type="text" id="healthMetricAddHeightInput1" name="healthMetricAddHeightInput1" value="" style="width: 30px"/></td>
			<td colspan="2" align="center"><input type="text" id="healthMetricAddHeadCircumferenceInput1" name="healthMetricAddHeadCircumferenceInput1" value="" style="width: 30px"/></td>
			<td colspan="2" align="center">
				<select name="healthMetricAddHouseholdSmokingStatusInput1" id="healthMetricAddHouseholdSmokingStatusInput1">
	                	<option value="">Select a Household Smoking Status</option>
	                    <option value="1">1 - non-smoking household</option>
	               		<option value="2">2 - outdoor smokers</option>
	               		<option value="3">3 - indoor smokers</option>
	               		<option value="9">9 - unknown</option>
	        	</select>
			</td>
			<td align="center"><input id="addRecordButton1" type="submit" value="Add Record" onClick="saveRecord(1)"/></td>
		</tr>
	</table>
	<br />
</div>
<%
} //Ends the 0 to 3 section
%>
