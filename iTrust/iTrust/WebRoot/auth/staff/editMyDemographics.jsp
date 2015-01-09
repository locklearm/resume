<%@taglib prefix="itrust" uri="/WEB-INF/tags.tld" %>
<%@page errorPage="/auth/exceptionHandler.jsp" %>

<%@page import="edu.ncsu.csc.itrust.beans.PersonnelBean"%>
<%@page import="edu.ncsu.csc.itrust.action.EditPersonnelAction"%>
<%@page import="edu.ncsu.csc.itrust.BeanBuilder"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="edu.ncsu.csc.itrust.action.SetSecurityQuestionAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.SecurityQA"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Edit Personnel";
%>

<%@include file="/header.jsp" %>

<%
	/* Require a Personnel ID first */
	String pidString= "" + loggedInMID.longValue();

	/* A bad personnel ID gets you exiled to the exception handler */
	EditPersonnelAction personnelEditor = new EditPersonnelAction(prodDAO,loggedInMID.longValue(), pidString);
	long pid  = personnelEditor.getPid();
	SetSecurityQuestionAction saction = new SetSecurityQuestionAction(prodDAO, loggedInMID.longValue());
	
	/* Now take care of updating information */
	boolean formIsFilled = request.getParameter("formIsFilled")!=null && request.getParameter("formIsFilled").equals("true");
	PersonnelBean personnelForm;
	SecurityQA sbean;
	if(formIsFilled){
		personnelForm = new BeanBuilder<PersonnelBean>().build(request.getParameterMap(),new PersonnelBean());
		sbean = new BeanBuilder<SecurityQA>().build(request.getParameterMap(),new SecurityQA());
		try {
			personnelEditor.updateInformation(personnelForm);
			saction.updateInformation(sbean);
			loggingAction.logEvent(TransactionType.DEMOGRAPHICS_EDIT, loggedInMID.longValue(), 0, "");
%>
		<div align=center>
			<span class="iTrustMessage">Information Successfully Updated</span>
		</div>
<%
		} catch(FormValidationException e) {
%>
			<div align=center>
				<span class="iTrustError"><%=StringEscapeUtils.escapeHtml(e.getMessage()) %></span>
			</div>
<%
		}
	} else {
		personnelForm = prodDAO.getPersonnelDAO().getPersonnel(pid);
		sbean = saction.retrieveInformation();
		loggingAction.logEvent(TransactionType.DEMOGRAPHICS_VIEW, loggedInMID, pid, "");
	}
%>

<form action="editMyDemographics.jsp" method="post">
<input type="hidden" name="formIsFilled" value="true">
<input type="hidden" name="pid" value="<%= StringEscapeUtils.escapeHtml("" + (pid)) %>">
<br />
<div align=center>
	    <table class="fTable" align=center>
	      <tr><th colspan=2 >Personnel Information</th></tr>
	      <tr><td class="subHeaderVertical">
		  First Name:
		</td><td>
		  <input name="firstName"  value="<%= StringEscapeUtils.escapeHtml("" + (personnelForm.getFirstName())) %>"  type="text">
	      </td></tr>
	      <tr ><td class="subHeaderVertical">
		 Last Name:
		</td><td>

		  <input name="lastName"  value="<%= StringEscapeUtils.escapeHtml("" + (personnelForm.getLastName())) %>"  type="text">
	      </td></tr>
	      <tr><td class="subHeaderVertical">
		  Address:
		</td><td>

		  <input name="streetAddress1"  value="<%= StringEscapeUtils.escapeHtml("" + (personnelForm.getStreetAddress1())) %>"  type="text"><br />
		  <input name="streetAddress2"  value="<%= StringEscapeUtils.escapeHtml("" + (personnelForm.getStreetAddress2())) %>"  type="text">
	      </td></tr>
	      <tr ><td class="subHeaderVertical">
		  City:
		</td><td>
		  <input name="city"  value="<%= StringEscapeUtils.escapeHtml("" + (personnelForm.getCity())) %>"  type="text">
	      </td></tr>
	      <tr ><td class="subHeaderVertical">

		  State:
		</td><td>
			<itrust:state name="state" value="<%= StringEscapeUtils.escapeHtml(personnelForm.getState()) %>"/>
	      </td></tr>
		      <tr>
			    <th bgcolor=silver colspan=2>Authentication Information</th>
	          </tr>
		      <tr>
		      	<td class="subHeaderVertical">Security Question:</td>
		      	<td><input name="question"  value="<%= StringEscapeUtils.escapeHtml("" + (sbean.getQuestion())) %>"  type="text"></td>
		      </tr>
		      <tr>
		      	<td class="subHeaderVertical">Security Answer:</td>
		      	<td><input name="answer"  value="<%= StringEscapeUtils.escapeHtml("" + (sbean.getAnswer())) %>"  type="password"></td>
		      </tr>
		      <tr>
		      	<td class="subHeaderVertical">Confirm Security Answer:</td>
		      	<td><input name="confirmAnswer"  value="<%= StringEscapeUtils.escapeHtml("" + (sbean.getAnswer())) %>"  type="password"></td>
		      </tr>
		    </table>
		  </td></tr>
		  <tr>
		  	<td colspan=2 align=center>
		  	<br />
      <input type="submit" name="action" style="font-size: 16pt; font-weight: bold;" value="Edit Personnel Record">
		  	</td>
		  </tr>
      </table>
      <br />
</form>
      <br />

<a href="https://accounts.google.com/o/oauth2/auth?scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile& state=signup&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2FiTrust%2Fauth%2FGoogleOAuthSync&response_type=code&client_id=210947325531-pv0pemp6vt6cdv50fmu8lf497igo3rpf.apps.googleusercontent.com">
			<form action="/iTrust/auth/patient/editMyDemographics.jsp">
			<input type="button" name="sync" id="sync" value="Sync With Google Account">
			</form>
<br />
<div align=center>
Note: in order to set the password for this user, use the "Reset Password" link at the login page.
</div>
			
<%@include file="/footer.jsp" %>
