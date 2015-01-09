<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.List"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.action.FindExpertAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.HospitalBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.PersonnelBean"%>
<%@page import="java.util.HashMap"%>

<%@include file="/global.jsp"%>

<%
	pageTitle = "iTrust - Find an Expert";
%>

<%
	FindExpertAction fea = new FindExpertAction(DAOFactory.getProductionInstance());
%>

<%@include file="/header.jsp"%>

<h1>Find an Expert</h1>
<form method="post" id="mainForm" name="mainForm">
	<div>
		<label style="margin-left: 10px;" for="specialty">Specialty:</label> <select
			name="specialty">
			<option value="ob/gyn">OB/GYN</option>
			<option value="surgeon">Surgeon</option>
			<option value="heart surgeon">Heart Surgeon</option>
			<option value="pediatrician">Pediatrician</option>
			<option value="general physician">General Physician</option>
		</select> </br>
		</br>
		<td><span style="margin-left: 10px;" class="font1">Zip
				Code: </span></td>
		<input type="text" name="zip"
			value=<%=fea.getPatientZip(loggedInMID)%>>
		</td> </br>
		</br> <label style="margin-left: 10px;" for="scope">Zoom Level:</label> <select
			name="scope">
			<option value="5">5 (Closest to you)</option>
			<option value="4">4</option>
			<option value="3" selected="selected">3 (Default)</option>
			<option value="2">2</option>
			<option value="1">1 (Widest area)</option>
		</select> </br>
		</br> <input type="submit" name="findExpert" value="Find Expert" /> </br>
		</br>
	</div>
</form>

<%
	if (request.getParameter("findExpert") != null) {
	String tZip = request.getParameter("zip");
	boolean vZip = true;
	try{
		Integer.parseInt(tZip.substring(0,5));
	} catch (Exception e) {
		vZip = false;
	}
	if(vZip){
	HashMap<HospitalBean, List<PersonnelBean>> experts = new HashMap<HospitalBean, List<PersonnelBean>>();
	experts = fea.findHospitalsBySpecialty(request.getParameter("specialty"), loggedInMID, request.getParameter("scope"), request.getParameter("zip"));
	loggingAction.logEvent(TransactionType.FIND_EXPERT, loggedInMID, 0,"");
%>

<%
	if(experts.size() > 0){
				String hospitalName = "";
			    String hospitalAddress = "";
			    String hospitalCity = "";
			    String hospitalState = "";
			    String hospitalZip = "";
			    String expertName = "";
			    String expertEmail = "";
			    String expertPhone = "";
				for (HospitalBean hospital : experts.keySet()) {
					if(experts.get(hospital).size() > 0){
						hospitalName = hospital.getHospitalName();
						hospitalAddress = hospital.getHospitalAddress();
						hospitalCity = hospital.getHospitalCity();
						hospitalState = hospital.getHospitalState();
						hospitalZip = hospital.getHospitalZip();
%>
<div style="margin-top: 10px; margin-bottom: 0px;">
	<b><%=StringEscapeUtils.escapeHtml(hospitalName)%></b>
</div>
<label><%=StringEscapeUtils.escapeHtml(hospitalAddress + ", " + hospitalCity + " " + hospitalState + ", " + hospitalZip)%></label>
<br>
<%
	List<PersonnelBean> expertList = experts.get(hospital);
						for(PersonnelBean expert : expertList){
							expertName = expert.getFirstName() + " " + expert.getLastName();
							expertEmail = expert.getEmail();
							expertPhone = expert.getPhone();
%>
<ul>
	<li>
		<dl>
			<dt><%=StringEscapeUtils.escapeHtml(expertName)%></dt>
			<dd><%=StringEscapeUtils.escapeHtml(expertEmail)%></dd>
			<dd><%=StringEscapeUtils.escapeHtml(expertPhone)%></dd>
		</dl>
	</li>
</ul>

<%
	}
					}
				} 
			} else{
%>
<i><b>No Results!</b></i>
<%
	}
%>

<%
	}
	else{
%>
Invalid zip code
<%
	}
		}
%>

<%@include file="/footer.jsp"%>
