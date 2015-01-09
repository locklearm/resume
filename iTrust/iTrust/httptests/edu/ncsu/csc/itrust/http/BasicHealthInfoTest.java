package edu.ncsu.csc.itrust.http;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import edu.ncsu.csc.itrust.enums.TransactionType;

/**
 * Has an HCP view a patient's health information
 */
public class BasicHealthInfoTest extends iTrustHTTPTest {
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		gen.clearAllTables();
		gen.standardData();
	}
	
	public void testBasicHealthViewed() throws Exception{
		WebConversation wc = login("9000000000", "pw");	
		WebResponse wr = wc.getCurrentPage();
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		
		wr = wr.getLinkWith("Basic Health Information").click();
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		
		wr = wr.getLinkWith("Logout").click();
		assertEquals(ADDRESS + "auth/forwardUser.jsp", wr.getURL().toString());
		
		wc = login("2", "pw");
		wr = wc.getCurrentPage();
		String s = wr.getText();

		assertTrue(s.contains("Kelly Doctor"));
	}
	
	public void testBasicHealthSmokingStatus() throws Exception{
		WebConversation wc = login("9000000000", "pw");	
		WebResponse wr = wc.getCurrentPage();
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		
		wr = wr.getLinkWith("Basic Health Information").click();
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		
		assertTrue(wr.getText().contains("5 - Smoker, current status unknown"));
	}

	/*
	 */
	public void testHCPCheckMetricHistory2YearOld() throws Exception {
		//Generate test specific data
		gen.patientHealthMetrics();
		
		//Log Shelly Vang in
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		
		//Navigate to the Basic Health Information Page
		wr = wr.getLinkWith("Basic Health Information").click();
		
		//Select Caldwell Hudson
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "201");
		patientForm.getButtons()[1].click();
		
		//Get the current page
		wr = wc.getCurrentPage();
		
		//Check that view was logged
		assertLogged(TransactionType.HCP_VIEW_HEALTH_METRICS, 8000000010L, 201L, "none");
		
		//Get the health metrics table
		WebTable wt = wr.getTableStartingWithPrefix("Basic Health History");
		
		//Check headers
		assertTrue(wt.getCellAsText(1, 0).startsWith("Office"));
		assertEquals(wt.getCellAsText(1, 1), "Weight");
		assertEquals(wt.getCellAsText(1, 2), "Length");
		assertEquals(wt.getCellAsText(1, 5), "Head Circumference");
		assertTrue(wt.getCellAsText(1, 6).startsWith("Household"));
		
		//Check values
		assertEquals(wt.getCellAsText(2, 1), "30.2 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(2, 2), "34.7 in.(0.0%)");
		assertEquals(wt.getCellAsText(2, 5), "19.4 in.(0.0%)");
		assertEquals(wt.getCellAsText(2, 6), "indoor smokers");

		assertEquals(wt.getCellAsText(3, 1), "15.8 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(3, 2), "25.7 in.(0.0%)");
		assertEquals(wt.getCellAsText(3, 5), "17.1 in.(0.0%)");
		assertEquals(wt.getCellAsText(3, 6), "indoor smokers");

		assertEquals(wt.getCellAsText(4, 1), "12.1 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(4, 2), "22.5 in.(0.0%)");
		assertEquals(wt.getCellAsText(4, 5), "16.3 in.(0.0%)");
		assertEquals(wt.getCellAsText(4, 6), "indoor smokers");

		assertEquals(wt.getCellAsText(5, 1), "10.3 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(5, 2), "21.1 in.(0.0%)");
		assertEquals(wt.getCellAsText(5, 5), "15.3 in.(0.0%)");
		assertEquals(wt.getCellAsText(5, 6), "indoor smokers");

		assertEquals(wt.getCellAsText(6, 1), "8.3 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(6, 2), "19.6 in.(0.0%)");
		assertEquals(wt.getCellAsText(6, 5), "14.5 in.(0.0%)");
		assertEquals(wt.getCellAsText(6, 6), "indoor smokers");
		
		
		
	}

	/*
	 */
	public void testHCPViewHealthMetrics5YearOld() throws Exception {
		//Generate test specific data
		gen.patientHealthMetrics();
		
		//Log Shelly Vang in
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		
		//Navigate to the Basic Health Information Page
		wr = wr.getLinkWith("Basic Health Information").click();
		
		//Select Caldwell Hudson
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "202");
		patientForm.getButtons()[1].click();
		
		//Get the current page
		wr = wc.getCurrentPage();
		
		//Check that view was logged
		assertLogged(TransactionType.HCP_VIEW_HEALTH_METRICS, 8000000010L, 202L, "none");
		
		//Get the health metrics table
		WebTable wt = wr.getTableStartingWithPrefix("Basic Health History");
		
		//Check headers
		assertTrue(wt.getCellAsText(1, 0).startsWith("Office"));
		assertEquals(wt.getCellAsText(1, 1), "Weight");
		assertEquals(wt.getCellAsText(1, 2), "Height");
		assertEquals(wt.getCellAsText(1, 5), "Blood Pressure");
		assertTrue(wt.getCellAsText(1, 6).startsWith("Household"));
		
		//Check values
		assertEquals(wt.getCellAsText(2, 1), "36.5 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(2, 2), "39.3 in.(0.0%)");
		assertEquals(wt.getCellAsText(2, 5), "0/0mmHg");
		assertEquals(wt.getCellAsText(2, 6), "indoor smokers");
		
	}

	/*
	 */
	public void testHCPViewHealthMetrics20YearOld() throws Exception {
		//Generate test specific data
		gen.patientHealthMetrics();
		
		//Log Shelly Vang in
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		
		//Navigate to the Basic Health Information Page
		wr = wr.getLinkWith("Basic Health Information").click();
		
		//Select Caldwell Hudson
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "203");
		patientForm.getButtons()[1].click();
		
		//Get the current page
		wr = wc.getCurrentPage();
		
		//Check that view was logged
		assertLogged(TransactionType.HCP_VIEW_HEALTH_METRICS, 8000000010L, 203L, "none");
		
		//Get the health metrics table
		WebTable wt = wr.getTableStartingWithPrefix("Basic Health History");
		
		//Check headers
		assertTrue(wt.getCellAsText(1, 0).startsWith("Office"));
		assertEquals(wt.getCellAsText(1, 1), "Weight");
		assertEquals(wt.getCellAsText(1, 2), "Height");
		assertEquals(wt.getCellAsText(1, 5), "Blood Pressure");
		assertTrue(wt.getCellAsText(1, 6).startsWith("Household"));
		assertEquals(wt.getCellAsText(1, 7), "Smokes?");
		assertEquals(wt.getCellAsText(1, 8), "HDL");
		assertEquals(wt.getCellAsText(1, 9), "LDL");
		assertEquals(wt.getCellAsText(1, 10), "Triglycerides");
		
		//Check values
		assertEquals(wt.getCellAsText(2, 1), "124.3 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(2, 2), "62.3 in.(0.0%)");
		assertEquals(wt.getCellAsText(2, 5), "100/75mmHg");
		assertEquals(wt.getCellAsText(2, 6), "non-smoking household");
		assertEquals(wt.getCellAsText(2, 7), "Former smoker");
		assertEquals(wt.getCellAsText(2, 8), "65");
		assertEquals(wt.getCellAsText(2, 9), "102");
		assertEquals(wt.getCellAsText(2, 10), "147");
		
	}

	/*
	 */
	public void testHCPOfficeVisitAddMetricsCheckMetricHistory4MonthOld() throws Exception {
		//Generate test specific data
		
		//Log Shelly Vang in
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		
		//Document office visit page
		wr = wr.getLinkWith("Document Office Visit").click();
		
		//Select Brynn McClain
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "200");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/1/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Brynn can start eating rice cereal mixed with breast milk or formula once a day.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		
		//add a new health metric
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable forms = fm.getScriptableObject();
		forms.setParameterValue("healthMetricAddOfficeVisitID", "0");
		forms.setParameterValue("healthMetricAddRecordID", "0");
		forms.setParameterValue("healthMetricAddHeight", "22.3");
		forms.setParameterValue("healthMetricAddWeight", "16.5");
		forms.setParameterValue("healthMetricAddHeadCircumference", "16.1");
		forms.setParameterValue("healthMetricAddSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddHouseholdSmokingStatus", "1");
		forms.setParameterValue("healthMetricAddBloodPressureN", "95");
		forms.setParameterValue("healthMetricAddBloodPressureD", "65");
		forms.setParameterValue("healthMetricAddCholesterolHDL", "65");
		forms.setParameterValue("healthMetricAddCholesterolLDL", "102");
		forms.setParameterValue("healthMetricAddCholesterolTri", "147");
		forms.submit();
		
		// navigate to basic health page
		wr = wr.getLinkWith("Basic Health History").click();
		
		//Get the health metrics table
		WebTable wt = wr.getTableStartingWithPrefix("Basic Health History");
		
		//Check headers
		assertTrue(wt.getCellAsText(1, 0).startsWith("Office"));
		assertEquals(wt.getCellAsText(1, 1), "Weight");
		assertEquals(wt.getCellAsText(1, 2), "Length");
		assertEquals(wt.getCellAsText(1, 5), "Head Circumference");
		assertTrue(wt.getCellAsText(1, 6).startsWith("Household"));
		
		//Check values
		assertEquals(wt.getCellAsText(2, 1), "16.5 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(2, 2), "22.3 in.(0.0%)");
		assertEquals(wt.getCellAsText(2, 5), "16.1 in.(0.0%)");
		assertEquals(wt.getCellAsText(2, 6), "non-smoking household");
	}

	/*
	 */
	public void testHCPOfficeVisitAddMetricsCheckMetricHistory5YearOld() throws Exception {
		//Generate test specific data
		
		//Log Shelly Vang in
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		
		//Document office visit page
		wr = wr.getLinkWith("Document Office Visit").click();
		
		//Select Brynn McClain
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "202");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/1/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Fulton Gray can start eating rice cereal mixed with breast milk or formula once a day.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		
		//add a new health metric
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable forms = fm.getScriptableObject();
		forms.setParameterValue("healthMetricAddOfficeVisitID", "0");
		forms.setParameterValue("healthMetricAddRecordID", "0");
		forms.setParameterValue("healthMetricAddHeight", "42.9");
		forms.setParameterValue("healthMetricAddWeight", "37.9");
		forms.setParameterValue("healthMetricAddHeadCircumference", "0.1");
		forms.setParameterValue("healthMetricAddSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddHouseholdSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddBloodPressureN", "95");
		forms.setParameterValue("healthMetricAddBloodPressureD", "65");
		forms.setParameterValue("healthMetricAddCholesterolHDL", "65");
		forms.setParameterValue("healthMetricAddCholesterolLDL", "102");
		forms.setParameterValue("healthMetricAddCholesterolTri", "147");
		forms.submit();
		
		// navigate to basic health page
		wr = wr.getLinkWith("Basic Health History").click();
		
		//Get the health metrics table
		WebTable wt = wr.getTableStartingWithPrefix("Basic Health History");
		
		//Check headers 2
		assertTrue(wt.getCellAsText(1, 0).startsWith("Office"));
		assertEquals(wt.getCellAsText(1, 1), "Weight");
		assertEquals(wt.getCellAsText(1, 2), "Height");
		assertEquals(wt.getCellAsText(1, 5), "Blood Pressure");
		assertTrue(wt.getCellAsText(1, 6).startsWith("Household"));
		
		//Check values
		assertEquals(wt.getCellAsText(2, 1), "37.9 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(2, 2), "42.9 in.(0.0%)");
		assertEquals(wt.getCellAsText(2, 5), "95/65mmHg");
		assertEquals(wt.getCellAsText(2, 6), "indoor smokers");
		
	}

	/*
	 */
	public void testHCPOfficeVisitAddMetricsCheckMetricHistory20YearOld() throws Exception {
		
		//Log Shelly Vang in
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		
		//Document office visit page
		wr = wr.getLinkWith("Document Office Visit").click();
		
		//Select Brynn McClain
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "203");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/1/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Daria Griffin can start eating rice cereal mixed with breast milk or formula once a day.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		
		//add a new health metric
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable forms = fm.getScriptableObject();
		forms.setParameterValue("healthMetricAddOfficeVisitID", "0");
		forms.setParameterValue("healthMetricAddRecordID", "0");
		forms.setParameterValue("healthMetricAddHeight", "42.9");
		forms.setParameterValue("healthMetricAddWeight", "37.9");
		forms.setParameterValue("healthMetricAddHeadCircumference", "0.1");
		forms.setParameterValue("healthMetricAddSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddHouseholdSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddBloodPressureN", "95");
		forms.setParameterValue("healthMetricAddBloodPressureD", "65");
		forms.setParameterValue("healthMetricAddCholesterolHDL", "65");
		forms.setParameterValue("healthMetricAddCholesterolLDL", "102");
		forms.setParameterValue("healthMetricAddCholesterolTri", "147");
		forms.submit();
		
		// navigate to basic health page
		wr = wr.getLinkWith("Basic Health History").click();
		
		//Check that view was logged
		//Logging includes current age of patient.  Unpredictable
		//assertLogged(TransactionType.MEDICAL_RECORD_VIEW, 8000000010L, 203L, "");
		
		//Get the health metrics table
		WebTable wt = wr.getTableStartingWithPrefix("Basic Health History");
		
		//Check headers
		assertTrue(wt.getCellAsText(1, 0).startsWith("Office"));
		assertEquals(wt.getCellAsText(1, 1), "Weight");
		assertEquals(wt.getCellAsText(1, 2), "Height");
		assertEquals(wt.getCellAsText(1, 5), "Blood Pressure");
		assertTrue(wt.getCellAsText(1, 6).startsWith("Household"));
		assertEquals(wt.getCellAsText(1, 7), "Smokes?");
		assertEquals(wt.getCellAsText(1, 8), "HDL");
		assertEquals(wt.getCellAsText(1, 9), "LDL");
		assertEquals(wt.getCellAsText(1, 10), "Triglycerides");
		
		//Check values
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date today = new Date();
		assertEquals(wt.getCellAsText(2, 0), df.format(today));
		assertEquals(wt.getCellAsText(2, 1), "37.9 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(2, 2), "42.9 in.(0.0%)");
		assertEquals(wt.getCellAsText(2, 5), "95/65mmHg");
		assertEquals(wt.getCellAsText(2, 6), "indoor smokers");
		assertEquals(wt.getCellAsText(2, 7), "Former smoker");
		assertEquals(wt.getCellAsText(2, 8), "65");
		assertEquals(wt.getCellAsText(2, 9), "102");
		assertEquals(wt.getCellAsText(2, 10), "147");
		
	}
}
