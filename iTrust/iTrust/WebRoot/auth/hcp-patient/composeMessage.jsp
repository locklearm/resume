
<%@page import="edu.ncsu.csc.itrust.beans.HCPVisitBean"%>
<% List<HCPVisitBean> hcplist = vHcpAction.getVisitedHCPs(); %>
<h4>CC </h4>
<table id="hcp_table" class="fTable" style="text-align: center;">			
<%	
int i = 0; 
for (HCPVisitBean vb: hcplist) {
	if ( vb.getHCPMID() != ignoreMID) {
%>
		<tr>
			<td><%= StringEscapeUtils.escapeHtml("" + (vb.getHCPName())) %></td>
			<td>
				<input name="cc" value="<%= StringEscapeUtils.escapeHtml("" + (vb.getHCPMID())) %>" 
						type="checkbox" <%= StringEscapeUtils.escapeHtml("") %> />
			</td>
		</tr> 
<%
	i++;
	}
}
%>
</table>
<!-- end cc checkbox -->

<br />
<span>Subject: </span><input type="text" name="subject" size="50" value="<%= StringEscapeUtils.escapeHtml(subject) %>"/><br /><br />
<span>Message: </span><br />
<textarea name="messageBody" cols="100" rows="10"></textarea><br />
<br />
<input type="submit" value="Send" name="sendMessage"/>


