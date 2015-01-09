<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.action.EditPatientAction"%>
<%@page import="edu.ncsu.csc.itrust.BeanBuilder"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="edu.ncsu.csc.itrust.action.SetSecurityQuestionAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.SecurityQA"%>
<%@page import="org.apache.commons.validator.CreditCardValidator"%>

<%@taglib prefix="itrust" uri="/WEB-INF/tags.tld"%>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@include file="/global.jsp"%>

<%
	pageTitle = "iTrust - Edit Patient";
%>

<%@include file="/header.jsp"%>

<% 

	/* If the patient id doesn't check out, then kick 'em out to the exception handler */
	EditPatientAction action = new EditPatientAction(prodDAO,
			loggedInMID.longValue(), "" + loggedInMID.longValue());
	SetSecurityQuestionAction saction = new SetSecurityQuestionAction(
			prodDAO, loggedInMID.longValue());
	long pid = action.getPid();
	
	/* Now take care of updating information */
	boolean formIsFilled = request.getParameter("formIsFilled") != null
			&& request.getParameter("formIsFilled").equals("true");
	PatientBean p;
	SecurityQA s;
	if (formIsFilled) {
		p = new BeanBuilder<PatientBean>().build(request
				.getParameterMap(), new PatientBean());
		s = new BeanBuilder<SecurityQA>().build(request
				.getParameterMap(), new SecurityQA());
		try {
			action.updateInformation(p);
			saction.updateInformation(s);
			loggingAction.logEvent(TransactionType.DEMOGRAPHICS_EDIT, loggedInMID, loggedInMID, "");
%>
		<div align=center>
			<span class="iTrustMessage">Information Successfully Updated</span>
		</div>
<%
		} catch (FormValidationException e) {
%>
		<div align=center>
			<span class="iTrustError"><%=StringEscapeUtils.escapeHtml(e.getMessage()) %></span>
		</div>
<%
		p = action.getPatient();
		}
	} else {
		p = action.getPatient();
		s = saction.retrieveInformation();
		loggingAction.logEvent(TransactionType.DEMOGRAPHICS_VIEW, loggedInMID, pid, "");
	}
%>

<form action="editMyDemographics.jsp" method="post" id="edit">
<input type="hidden" name="formIsFilled" value="true"> 
<input type="hidden" name="pid" value="<%= StringEscapeUtils.escapeHtml("" + (pid)) %>"> 
<br />
<table cellspacing=0 align=center cellpadding=0>
	<tr>
		<td valign=top>
		<table class="fTable" align=center style="width: 350px;">
			<tr>
				<th colspan=2>Patient Information</th>
			</tr>
			<tr>
				<td class="subHeaderVertical">First Name:</td>
				<td><input name="firstName" value="<%= StringEscapeUtils.escapeHtml("" + (p.getFirstName())) %>" type="text"></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Last Name:</td>
				<td><input name="lastName" value="<%= StringEscapeUtils.escapeHtml("" + (p.getLastName())) %>" type="text"></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Email:</td>
				<td><input name="email" value="<%= StringEscapeUtils.escapeHtml("" + (p.getEmail())) %>" type="text">
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Address:</td>
				<td><input name="streetAddress1"
					value="<%= StringEscapeUtils.escapeHtml("" + (p.getStreetAddress1())) %>" type="text"><br />
				<input name="streetAddress2" value="<%= StringEscapeUtils.escapeHtml("" + (p.getStreetAddress2())) %>"
					type="text"></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">City:</td>
				<td><input name="city" value="<%= StringEscapeUtils.escapeHtml("" + (p.getCity())) %>" type="text">
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">State:</td>
				<td><itrust:state name="state" value="<%= StringEscapeUtils.escapeHtml(p.getState()) %>" /></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Zip:</td>
				<td>
					<input name="zip" value="<%= StringEscapeUtils.escapeHtml("" + (p.getZip())) %>" maxlength="10" type="text" size="10">
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Phone:</td>
				<td>
					<input name="phone" value="<%= StringEscapeUtils.escapeHtml("" + (p.getPhone())) %>" type="text" size="12" maxlength="12">
			</tr>

			<tr>
				<td class="subHeaderVertical">Mother MID:</td>
				<td><input name="MotherMID" value="<%= StringEscapeUtils.escapeHtml("" + (p.getMotherMID())) %>"
					maxlength="10" type="text"></td>
			</tr>

			<tr>
				<td class="subHeaderVertical">Father MID:</td>
				<td><input name="FatherMID" value="<%= StringEscapeUtils.escapeHtml("" + (p.getFatherMID())) %>"
					maxlength="10" type="text"></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Credit Card Type:</td>
				<td><select name="creditCardType">
				<option value="">Select:</option>
				<option value="MASTERCARD" <%= StringEscapeUtils.escapeHtml("" + ( p.getCreditCardType().equals("MASTERCARD") ? "selected" : "" )) %>>Mastercard</option>
				<option value="VISA" <%= StringEscapeUtils.escapeHtml("" + ( p.getCreditCardType().equals("VISA") ? "selected" : "" )) %>>Visa</option>
				<option value="DISCOVER" <%= StringEscapeUtils.escapeHtml("" + ( p.getCreditCardType().equals("DISCOVER") ? "selected" : "" )) %>>Discover</option>
				<option value="AMEX" <%= StringEscapeUtils.escapeHtml("" + ( p.getCreditCardType().equals("AMEX") ? "selected" : "" )) %>>American Express</option>
				</select>
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Credit Card Number:</td>
				<td><input name="creditCardNumber" value="<%= StringEscapeUtils.escapeHtml("" + (p.getCreditCardNumber())) %>"
					maxlength="19" type="text"></td>
			</tr>
		</table>
		</td>
		<td width="15px">&nbsp;</td>
		<td valign=top>
		<table class="fTable" align=center style="width: 350px;">
			<tr>
				<th colspan=2>Insurance Information</th>
			</tr>
			<tr>
				<td class="subHeaderVertical">Name:</td>
				<td><input name="icName" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcName())) %>" type="text">
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Address:</td>
				<td><input name="icAddress1" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcAddress1())) %>"
					type="text"><br />
				<input name="icAddress2" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcAddress2())) %>" type="text">
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">City:</td>
				<td><input name="icCity" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcCity())) %>" type="text">
				</td>
			</tr>

			<tr>
				<td class="subHeaderVertical">State:</td>
				<td><itrust:state name="icState" value="<%= StringEscapeUtils.escapeHtml(p.getIcState()) %>" />
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Zip:</td>
				<td>
					<input name="icZip" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcZip())) %>" maxlength="10" type="text" size="10">
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Phone:</td>
				<td>
					<input name="icPhone" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcPhone())) %>" type="text" size="12" maxlength="12">
				</td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Insurance ID:</td>
				<td><input name="icID" value="<%= StringEscapeUtils.escapeHtml("" + (p.getIcID())) %>" type="text">
				</td>
			</tr>
		</table>
		
		<br>
		
		<div align="center">

		</td>
	</tr>
	<tr height="15px">
		<td colspan=3>&nbsp;</td>
	</tr>
	<tr>
		<td valign="top">
		<table class="fTable" align=center style="width: 350px;">
			<tr>
				<th colspan=2>Authentication Information</th>
			</tr>
			<tr>
				<td class="subHeaderVertical">Security Question:</td>
				<td><input name="question" value="<%= StringEscapeUtils.escapeHtml("" + (s.getQuestion())) %>"
					type="text"></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Security Answer:</td>
				<td><input name="answer" value="<%= StringEscapeUtils.escapeHtml("" + (s.getAnswer())) %>"
					type="password"></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Confirm Security Answer:</td>
				<td><input name="confirmAnswer" value="<%= StringEscapeUtils.escapeHtml("" + (s.getAnswer())) %>"
					type="password"></td>
			</tr>
		</table>
		</td>
		<td width="15px">&nbsp;</td>
		<td valign="top">
		<table class="fTable" align=center style="width: 350px;">
			<tr>
				<th colspan=2>Emergency Contact</th>
			</tr>
			<tr>
				<td class="subHeaderVertical">Name:</td>
				<td><input name="emergencyName"
					value="<%= StringEscapeUtils.escapeHtml("" + (p.getEmergencyName())) %>" type="text"></td>
			</tr>
			<tr>
				<td class="subHeaderVertical">Phone:</td>
				<td>
					<input name="emergencyPhone" value="<%= StringEscapeUtils.escapeHtml("" + (p.getEmergencyPhone())) %>" type="text" size="12" maxlength="12">
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
<br />
<div align="center"><input type="submit" name="action"
	style="font-size: 16pt; font-weight: bold;" value="Edit Patient Record">
<br />
<br />
Note: in order to set your password, use the "Reset Password" link at
the login page.</div>
</form>
<!--  		<% if("Sync With Google Account".equals(request.getParameter("sync"))){%>
		<input type="submit" name="synced" id="synced"
			style="font-size: 12pt" value="Sync">
		<%}else{%> -->
		<br />
			<div align="center">
			<a id="syncLink" href="https://accounts.google.com/o/oauth2/auth?scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile& state=signup&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2FiTrust%2Fauth%2FGoogleOAuthSync&response_type=code&client_id=210947325531-pv0pemp6vt6cdv50fmu8lf497igo3rpf.apps.googleusercontent.com">
			<form action="/iTrust/auth/patient/editMyDemographics.jsp">
			<input type="button" name="sync" id="sync" value="Sync With Google Account">
			 </form> </a></div>
		
<!--		<%} %> -->
<%@include file="/footer.jsp"%>
