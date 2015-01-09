package edu.ncsu.csc.itrust.http;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import edu.ncsu.csc.itrust.enums.TransactionType;


/**
 * Use Case 52
 */
public class DisplayingNewMetrics extends iTrustHTTPTest {
	
	protected void setUp() throws Exception{
		super.setUp();
		gen.clearAllTables();
		gen.standardData();
	}
	
	public void testCalculatingAndViewingBMI() throws Exception
	{
		gen.clearAllTables();
		gen.standardData();
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp/home.jsp", wr.getURL().toString());
		wr = wr.getLinkWith("Document Office Visit").click();
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "203");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		
		wr = wc.getCurrentPage();
		patientForm = wr.getFormWithID("mainForm");
		patientForm.setParameter("visitDate", "10/29/2013");
		patientForm.setParameter("hospitalID","8");
		patientForm.setParameter("notes","Healthy");
		patientForm.getButtonWithID("update").click();
		
		wr = wc.getCurrentPage();
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable forms = fm.getScriptableObject();
		forms.setParameterValue("healthMetricAddWeightInput3","110");
		forms.setParameterValue("healthMetricAddHeightInput3","60");
		forms.setParameterValue("healthMetricAddBloodPressureNInput3","100");
		forms.setParameterValue("healthMetricAddBloodPressureDInput3","75");
		forms.setParameterValue("healthMetricAddHouseholdSmokingStatusInput3","1");
		forms.setParameterValue("healthMetricAddSmokingStatusInput3","4");
		forms.setParameterValue("healthMetricAddCholesterolHDLInput3","65");
		forms.setParameterValue("healthMetricAddCholesterolLDLInput3", "110");
		forms.setParameterValue("healthMetricAddCholesterolTriInput3","110");
		forms.submit();
		assertLogged(TransactionType.EDIT_HEALTH_METRIC,9000000000L,203,"");
	}
	
	public void testPatientViewingPercentilesAbove20() throws Exception
	{
		WebConversation wc = login("205", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/patient/home.jsp", wr.getURL().toString());
		wr = wr.getLinkWith("View My Records").click();
		WebTable wt = wr.getTableStartingWithPrefix("Basic Health History: 12 years and older");
		assertEquals(wt.getCellAsText(1, 3), "BMI");
		assertEquals(wt.getCellAsText(2, 3), "21.5(0.0%)");
		
	}
	
	public void testPatientViewingBMI() throws Exception
	{
		WebConversation wc = login("205", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/patient/home.jsp", wr.getURL().toString());
		wr = wr.getLinkWith("View My Records").click();
		WebTable wt = wr.getTableStartingWithPrefix("Basic Health History: 12 years and older");
		assertEquals(wt.getCellAsText(1, 3), "BMI");
		assertEquals(wt.getCellAsText(2, 3), "21.5(0.0%)");
	}
	
	
	
}