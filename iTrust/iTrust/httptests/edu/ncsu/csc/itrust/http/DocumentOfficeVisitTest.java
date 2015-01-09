package edu.ncsu.csc.itrust.http;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import edu.ncsu.csc.itrust.enums.TransactionType;


/**
 * Test all office visit document
 *  
 * @ author student
 *
 */
public class DocumentOfficeVisitTest extends iTrustHTTPTest {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gen.clearAllTables();
		gen.standardData();
	}

	/*
	 * Authenticate UAP
	 * MID 8000000009
	 * Password: pw
	 * Choose "Document Office Visit"
	 * Enter Patient MID 1
	 * Enter Fields:
	 * Date: 2005-11-21
	 * Notes: "I like diet-coke"
	 */
	public void testDocumentOfficeVisit6() throws Exception {
		// login UAP
		WebConversation wc = login("8000000009", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - UAP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 1
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "1");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "11/21/2005");
		form.setParameter("notes", "I like diet-coke");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		assertLogged(TransactionType.OFFICE_VISIT_CREATE, 8000000009L, 1L, "Office visit");
	}

	/*
	 * Authenticate HCP
	 * MID 9000000000
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 2 and confirm
	 * Choose to document new office vist.
	 * Enter Fields:
	 * Date: 2005-11-2
	 * Notes: Great patient!
	 */
	public void testDocumentOfficeVisit1() throws Exception {
		// login HCP
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "11/02/2005");
		form.setParameter("notes", "Great Patient!");
		form.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		assertLogged(TransactionType.OFFICE_VISIT_CREATE, 9000000000L, 2L, "Office visit");
	}

	/*
	 * Authenticate HCP
	 * MID 9000000000
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 2 and confirm
	 * Choose to document new office vist.
	 * Enter Fields:
	 * Date: 2005-11-21
	 * Notes: <script>alert('ha ha ha');</script>
	 */
	public void testDocumentOfficeVisit2() throws Exception {
		// login HCP
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "11/21/2005");
		form.setParameter("notes", "<script>alert('ha ha ha');</script>");
		form.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Notes: Up to 300 alphanumeric characters, with space, and other punctuation"));
		assertNotLogged(TransactionType.OFFICE_VISIT_CREATE, 9000000000L, 2L, "Office visit");
	}
	
	public void testUpdateOfficeVisitSemicolon() throws Exception {
		// login UAP
		WebConversation wc = login("8000000009", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - UAP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 1
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "1");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "11/21/2005");
		form.setParameter("notes", "I like diet-coke ;");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		assertLogged(TransactionType.OFFICE_VISIT_CREATE, 8000000009L, 1L, "Office visit");
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000009
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 1 and confirm
	 * Choose to document new office vist.
	 * Enter Fields:
	 * Date: 2005-11-21
	 */
	public void testUpdateOfficeVisitOctothorpe() throws Exception {
		// login UAP
		WebConversation wc = login("8000000009", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - UAP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 1
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "1");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "11/21/2005");
		form.setParameter("notes", "I like diet-coke #");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		assertLogged(TransactionType.OFFICE_VISIT_CREATE, 8000000009L, 1L, "Office visit");
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000010
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 200 and confirm
	 * Choose to document new office visit.
	 * Date: 2013-09-17
	 */
	public void testAddBasicHealthMetrics4MonthOld() throws Exception {
		// login UAP
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 200
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "200");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/01/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Brynn can start eating rice cereal mixed with breast milk or formula once a day.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		// add health metric
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
		//assertTrue(wr.getText().contains("Information Successfully Updated"));
		wr = wc.getCurrentPage();
		wr.getText().contains("Information Successfully Updated");
		assertTrue(wr.getText().contains("Information Recorded"));
		assertLogged(TransactionType.OFFICE_VISIT_CREATE, 8000000010L, 200L, "Office visit");
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000010
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 201 and confirm
	 * Choose to document new office visit.
	 * Date: 2013-09-17
	 */
	public void testAddBasicHealthMetrics2YearOld() throws Exception {
		// login UAP
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 201
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "201");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/28/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Diagnosed with strep throat. Avoid contact with others for first 24 hours of antibiotics.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		
		// add health metric
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable forms = fm.getScriptableObject();
		forms.setParameterValue("healthMetricAddOfficeVisitID", "0");
		forms.setParameterValue("healthMetricAddRecordID", "0");
		forms.setParameterValue("healthMetricAddHeight", "34.7");
		forms.setParameterValue("healthMetricAddWeight", "30.2");
		forms.setParameterValue("healthMetricAddHeadCircumference", "19.4");
		forms.setParameterValue("healthMetricAddSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddHouseholdSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddBloodPressureN", "95");
		forms.setParameterValue("healthMetricAddBloodPressureD", "65");
		forms.setParameterValue("healthMetricAddCholesterolHDL", "65");
		forms.setParameterValue("healthMetricAddCholesterolLDL", "102");
		forms.setParameterValue("healthMetricAddCholesterolTri", "147");
		forms.submit();
		
		wr = wc.getCurrentPage();
		wr.getText().contains("Information Successfully Updated");
		assertTrue(wr.getText().contains("Information Recorded"));
		// add prescription
		
		wr = wc.getCurrentPage();
		form = wr.getFormWithID("prescriptionForm");
		form.setParameter("medID", "664662530");
		form.setParameter("dosage", "50");
		form.setParameter("instructions", "Take three times a day.");
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Prescription information successfully updated."));
		// add diagnosis
		form = wr.getFormWithID("diagnosisForm");
		form.setParameter("ICDCode", "34.00");
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Diagnosis information successfully updated"));
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000010
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 202 and confirm
	 * Choose to document new office visit.
	 * Date: 2013-09-17
	 */
	public void testAddBasicHealthMetrics5YearOld() throws Exception {
		// login UAP
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 202
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "202");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/14/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Fulton has all required immunizations to start kindergarten next year.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		// add health metric
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
		wr = wc.getCurrentPage();
		wr.getText().contains("Information Successfully Updated");
		assertTrue(wr.getText().contains("Information Recorded"));
		assertLogged(TransactionType.OFFICE_VISIT_CREATE, 8000000010L, 202L, "Office visit");
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000010
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 203 and confirm
	 * Choose to document new office visit.
	 * Date: 2013-09-17
	 */
	public void testAddBasicHealthMetrics20YearOld() throws Exception {
		// login UAP
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 203
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "203");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/25/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Patient is Healthy");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		// add health metric
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
		wr = wc.getCurrentPage();
		wr.getText().contains("Information Successfully Updated");
		assertTrue(wr.getText().contains("Information Recorded"));
		assertLogged(TransactionType.OFFICE_VISIT_CREATE, 8000000010L, 203L, "Office visit");
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000010
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 204 and confirm
	 * Choose to document new office visit.
	 * Date: 2013-09-17
	 */
	public void testAddBasicHealthMetrics24YearOld() throws Exception {
		// login UAP
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 204
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "204");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/25/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Thane should consider modifying diet and exercise to avoid future heart disease.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		// add health metric
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
		wr = wc.getCurrentPage();
		wr.getText().contains("Information Successfully Updated");
		assertTrue(wr.getText().contains("Information Recorded"));
		assertLogged(TransactionType.OFFICE_VISIT_CREATE, 8000000010L, 204L, "Office visit");
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000010
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 200 and confirm
	 * Choose to document new office visit.
	 * Date: 2013-09-17
	 */
	public void testInvalidHeadCircumferenceNegativeNumber() throws Exception {
		// login UAP
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 200
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "200");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/01/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Brynn can start eating rice cereal mixed with breast milk or formula once a day.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		// add health metric
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable forms = fm.getScriptableObject();
		forms.setParameterValue("healthMetricAddOfficeVisitID", "0");
		forms.setParameterValue("healthMetricAddRecordID", "0");
		forms.setParameterValue("healthMetricAddHeight", "42.9");
		forms.setParameterValue("healthMetricAddWeight", "37.9");
		forms.setParameterValue("healthMetricAddHeadCircumference", "-1");
		forms.setParameterValue("healthMetricAddSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddHouseholdSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddBloodPressureN", "95");
		forms.setParameterValue("healthMetricAddBloodPressureD", "65");
		forms.setParameterValue("healthMetricAddCholesterolHDL", "65");
		forms.setParameterValue("healthMetricAddCholesterolLDL", "102");
		forms.setParameterValue("healthMetricAddCholesterolTri", "147");
		forms.submit();
		//assertTrue(wr.getText().contains("Information Successfully Updated"));
		wr = wc.getCurrentPage();
		wr.getText().contains("Information Successfully Updated");
		assertTrue(wr.getText().contains("This form has not been validated correctly"));
		//assertLogged(TransactionType.OFFICE_VISIT_CREATE, 8000000010L, 200L, "Office visit");
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000010
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 200 and confirm
	 * Choose to document new office visit.
	 * Date: 2013-09-17
	 */
	public void testInvalidHeadCircumferenceNotNumber() throws Exception {
		// login UAP
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 200
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "200");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/01/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Brynn can start eating rice cereal mixed with breast milk or formula once a day.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		// add health metric
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable forms = fm.getScriptableObject();
		forms.setParameterValue("healthMetricAddOfficeVisitID", "0");
		forms.setParameterValue("healthMetricAddRecordID", "0");
		forms.setParameterValue("healthMetricAddHeight", "42.9");
		forms.setParameterValue("healthMetricAddWeight", "37.9");
		forms.setParameterValue("healthMetricAddHeadCircumference", "abc");
		forms.setParameterValue("healthMetricAddSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddHouseholdSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddBloodPressureN", "95");
		forms.setParameterValue("healthMetricAddBloodPressureD", "65");
		forms.setParameterValue("healthMetricAddCholesterolHDL", "65");
		forms.setParameterValue("healthMetricAddCholesterolLDL", "102");
		forms.setParameterValue("healthMetricAddCholesterolTri", "147");
		forms.submit();
		//assertTrue(wr.getText().contains("Information Successfully Updated"));
		wr = wc.getCurrentPage();
		wr.getText().contains("Information Successfully Updated");
		assertTrue(wr.getText().contains("This form has not been validated correctly"));
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000010
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 200 and confirm
	 * Choose to document new office visit.
	 * Date: 2013-09-17
	 */
	public void testInvalidHeadCircumferenceEntersZero() throws Exception {
		// login UAP
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 200
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "200");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/01/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Brynn can start eating rice cereal mixed with breast milk or formula once a day.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		// add health metric
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable forms = fm.getScriptableObject();
		forms.setParameterValue("healthMetricAddOfficeVisitID", "0");
		forms.setParameterValue("healthMetricAddRecordID", "0");
		forms.setParameterValue("healthMetricAddHeight", "22.3");
		forms.setParameterValue("healthMetricAddWeight", "16.5");
		forms.setParameterValue("healthMetricAddHeadCircumference", "0");
		forms.setParameterValue("healthMetricAddSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddHouseholdSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddBloodPressureN", "95");
		forms.setParameterValue("healthMetricAddBloodPressureD", "65");
		forms.setParameterValue("healthMetricAddCholesterolHDL", "65");
		forms.setParameterValue("healthMetricAddCholesterolLDL", "102");
		forms.setParameterValue("healthMetricAddCholesterolTri", "147");
		forms.submit();
		//assertTrue(wr.getText().contains("Information Successfully Updated"));
		wr = wc.getCurrentPage();
		wr.getText().contains("Information Successfully Updated");
		assertTrue(wr.getText().contains("This form has not been validated correctly"));
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000010
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 200 and confirm
	 * Choose to document new office visit.
	 * Date: 2013-09-17
	 */
	public void testInvalidHeadCircumference4DigitNumber() throws Exception {
		// login UAP
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 200
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "200");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/01/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Brynn can start eating rice cereal mixed with breast milk or formula once a day.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		// add health metric
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable forms = fm.getScriptableObject();
		forms.setParameterValue("healthMetricAddOfficeVisitID", "0");
		forms.setParameterValue("healthMetricAddRecordID", "0");
		forms.setParameterValue("healthMetricAddHeight", "42.9");
		forms.setParameterValue("healthMetricAddWeight", "37.9");
		forms.setParameterValue("healthMetricAddHeadCircumference", "4000");
		forms.setParameterValue("healthMetricAddSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddHouseholdSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddBloodPressureN", "95");
		forms.setParameterValue("healthMetricAddBloodPressureD", "65");
		forms.setParameterValue("healthMetricAddCholesterolHDL", "65");
		forms.setParameterValue("healthMetricAddCholesterolLDL", "102");
		forms.setParameterValue("healthMetricAddCholesterolTri", "147");
		forms.submit();
		//assertTrue(wr.getText().contains("Information Successfully Updated"));
		wr = wc.getCurrentPage();
		wr.getText().contains("Information Successfully Updated");
		assertTrue(wr.getText().contains("This form has not been validated correctly"));
	}
	
	/*
	 * Authenticate HCP
	 * MID 8000000010
	 * Password: pw
	 * Choose Document Office Visit
	 * Enter Patient MID 200 and confirm
	 * Choose to document new office visit.
	 * Date: 2013-09-17
	 */
	public void testInvalidHeadCircumferenceNumberWith2DecimalPlaces() throws Exception {
		// login UAP
		WebConversation wc = login("8000000010", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 200
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "200");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		form = wr.getFormWithID("mainForm");
		form.setParameter("visitDate", "10/01/2013");
		form.setParameter("hospitalID", "8");
		form.setParameter("notes", "Brynn can start eating rice cereal mixed with breast milk or formula once a day.");
		form.getButtonWithID("update").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Information Successfully Updated"));
		// add health metric
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable forms = fm.getScriptableObject();
		forms.setParameterValue("healthMetricAddOfficeVisitID", "0");
		forms.setParameterValue("healthMetricAddRecordID", "0");
		forms.setParameterValue("healthMetricAddHeight", "42.9");
		forms.setParameterValue("healthMetricAddWeight", "37.9");
		forms.setParameterValue("healthMetricAddHeadCircumference", "0.17");
		forms.setParameterValue("healthMetricAddSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddHouseholdSmokingStatus", "3");
		forms.setParameterValue("healthMetricAddBloodPressureN", "95");
		forms.setParameterValue("healthMetricAddBloodPressureD", "65");
		forms.setParameterValue("healthMetricAddCholesterolHDL", "65");
		forms.setParameterValue("healthMetricAddCholesterolLDL", "102");
		forms.setParameterValue("healthMetricAddCholesterolTri", "147");
		forms.submit();
		//assertTrue(wr.getText().contains("Information Successfully Updated"));
		wr = wc.getCurrentPage();
		wr.getText().contains("Information Successfully Updated");
		assertTrue(wr.getText().contains("This form has not been validated correctly"));
	}
}
