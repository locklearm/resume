<%@page import="java.io.DataInputStream"%>
<%@page errorPage="/auth/exceptionHandler.jsp" %>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.io.InputStream"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.CSVParser"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PercentilesDataDAO"%>
<%@page import="edu.ncsu.csc.itrust.action.UpdatePercentileDataAction"%> 
<%@page import="edu.ncsu.csc.itrust.exception.CSVFormatException"%>
<%@page import="edu.ncsu.csc.itrust.exception.AddPatientFileException"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@page import="org.apache.commons.fileupload.DefaultFileItemFactory" %>
<%@page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@page import="org.apache.commons.fileupload.FileItem" %>
<%@page import="java.util.Scanner" %>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Update Percentiles Data";
%>

<%@include file="/header.jsp" %>

<%
//Strings to hold messages to the user
String updateErrorMessage = "";
String updateMessage = "";

//Flag for if the file has been chosen (and uploaded)
boolean isMultipart = ServletFileUpload.isMultipartContent(request);

//Go ahead and instantiate the action class
UpdatePercentileDataAction pdAction = new UpdatePercentileDataAction(prodDAO);

if (!isMultipart) {
	updateMessage = "Please select a file";
}
//Now we handle the actual update portion of this
else {
	String returnMessage = "";
	
	
	String error = "";
	Boolean fatal = false;
	InputStream fileStream = null;
	boolean ignore = true;
	FileItemFactory factory = new DefaultFileItemFactory();
	ServletFileUpload upload = new ServletFileUpload(factory);
	List<FileItem> items = upload.parseRequest(request);
	Iterator iter = items.iterator();
	
	String myType = items.get(0).getString();
	
			try {
				
				String myData = items.get(1).getString();
				
				
				returnMessage = pdAction.addPercentilesData(myType.trim(), myData);
				if (!"".equals(returnMessage)) {
					updateErrorMessage = returnMessage;
				}
				else {
					updateMessage = "Successfully updated percentiles data";
					loggingAction.logEvent(TransactionType.ADMIN_UPDATE_PERCENTILE_DATA, loggedInMID, 0, myType);
				}
				
				//Add the header to the error message
				if (!"".equals(returnMessage)) {
					returnMessage = "The following rows weren't added:" + returnMessage;
				}
				
			}
			catch (Exception e) {
				e.printStackTrace();
				returnMessage = "Error uploading file";
			}
			finally {
				//Do nothing
			}
}





//Handle displaying update message
if (!"".equals(updateMessage)) {
	%>  <span class="iTrustMessage"><%= updateMessage %></span>  <%
}
//Handle displaying update error message
if (!"".equals(updateErrorMessage)) {
	%>  <span class="iTrustError">Error:<%= updateErrorMessage %></span>  <%
}
%>



<form name="updatePercentilesDataForm" enctype="multipart/form-data" id="updatePercentilesDataForm" method="post" action="updatePercentileData.jsp">
	<div>Please select the type of percentiles data:</div>
	<select id="updatePercentilesDataFormPercentileType" name="updatePercentilesDataFormPercentileType">
		<option value="notSelected">--- Please Select Percentile Type ---</option>
		<option value="weightForAgeInfant">Weight for age (birth to 36 months)</option>
		<option value="weightForAgeAdult">Weight for age (2 to 20 years)</option>
		<option value="lengthForAgeInfant">Length for age (birth to 36 months)</option>
		<option value="lengthForAgeAdult">Length for age (2 to 20 years)</option>
		<option value="headCircumferenceForAge">Head circumference (birth to 3 years)</option>
		<option value="BMIForAge">BMI (2 to 20 years)</option>
	</select>
	<div>Please select a CSV file</div>
	<input name="csvFile" type="file" />
	<input type="submit" id="updatePercentilesDataFormSubmit" value="Update Data" />
</form>


<br/><br/>








<br /><br />




<%@include file="/footer.jsp" %>
