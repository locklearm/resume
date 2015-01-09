
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="edu.ncsu.csc.itrust.action.ChangePasswordAction"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Change Password";
%>

<%
ChangePasswordAction action = new ChangePasswordAction(prodDAO, loggedInMID.longValue());

String updateMessage = "";
String updateError = "";
String oldPass = request.getParameter("old_pass");
String pass = request.getParameter("new_pass");
String pass2 = request.getParameter("new_pass_2");

if(oldPass != null && pass != null && pass2 != null) {
	if (!pass.equals(pass2)) {
		updateError = "New Passwords Don't Match";
	}
	else if(action.checkPassword(oldPass)) {
		if(action.validatePassword(pass)) {
			action.changePassword(pass);
			updateMessage = "Password Change Successful";
		} else {
			updateError = "Invalid New Password";
		}
	} else {
		updateError = "Current Password Entered Is Not Correct";
	}
}
%>

<%@include file="/header.jsp" %>

<form method="post" action="/iTrust/changePassword.jsp" id="changePasswordForm" name="changePasswordForm">
<div align=center>
	<h2>Change Password</h2>
	<%
	if (!"".equals(updateError)) {
		%>  <span class="iTrustError"><%= updateError %></span>  <%
	}
	if (!"".equals(updateMessage)) {
		%>  <span class="iTrustMessage"><%= updateMessage %></span>  <%
	}
	%>
	<table class="fancyTable">
		<tr>
			<td>
				<span>Enter Current Password: </span>
			</td>
			<td>
				<input type="password" maxlength="20" name="old_pass" style="width: 158px;">
			</td>
		</tr>
		<tr>
			<td>
				<span>Enter New Password: </span>
			</td>
			<td>
				<input type="password" maxlength="20" name="new_pass" style="width: 158px;">
			</td>
		</tr>
		<tr>
			<td>
				<span>Enter New Password Again: </span>
			</td>
			<td>
				<input type="password" maxlength="20" name="new_pass_2" style="width: 158px;">
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" value="Confirm" style="margin-left:120px;">
			</td>
		</tr>
	</table>
</div>
</form>

<%@include file="/footer.jsp" %>
