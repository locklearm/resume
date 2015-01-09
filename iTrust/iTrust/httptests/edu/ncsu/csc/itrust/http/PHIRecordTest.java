package edu.ncsu.csc.itrust.http;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import com.meterware.httpunit.*;
import edu.ncsu.csc.itrust.enums.TransactionType;

public class PHIRecordTest extends iTrustHTTPTest {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gen.clearAllTables();
		gen.uap1();
		gen.patient2();
		gen.patient1();
		gen.patient4();
		gen.hcp0();
	}
	
	/*
	 * Authenticate HCP
	 * MID: 9000000000
	 * Password: pw
	 * Choose Edit Basic Health History
	 * enter 0000000002 and confirm
	 * Enter fields:
	 * Height: **
	 * Weight: 400 pounds
	 * Blood Pressure: 999/000
	 * Smokes: Y
	 * HDL: 50
	 * LDL: 200
	 * Triglycerides: 200
	 * Confirm and approve entries
	 */
	public void testCreatePHIRecord() throws Exception {
		// login as hcp
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getResponse(ADDRESS + "auth/hcp/home.jsp");
		assertEquals("iTrust - HCP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L,"");
		
		// click on Edit Basic Health Information
		wr = wr.getLinkWith("Basic Health Information").click();
		assertEquals(ADDRESS + "auth/getPatientID.jsp?forward=hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		// Choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		//Logging includes current age of patient.  Unpredictable
		//assertLogged(TransactionType.PATIENT_HEALTH_INFORMATION_VIEW, 9000000000L, 2L,"");
		
		// attempt to add a record with 0 height and 0 weight
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable form = fm.getScriptableObject();
		form.setParameterValue("healthMetricAddOfficeVisitID", "0");
		form.setParameterValue("healthMetricAddRecordID", "0");
		form.setParameterValue("healthMetricAddHeight", "0");
		form.setParameterValue("healthMetricAddWeight", "0");
		form.setParameterValue("healthMetricAddHeadCircumference", "0.1");
		form.setParameterValue("healthMetricAddSmokingStatus", "1");
		form.setParameterValue("healthMetricAddHouseholdSmokingStatus", "9");
		form.setParameterValue("healthMetricAddBloodPressureN", "999");
		form.setParameterValue("healthMetricAddBloodPressureD", "000");
		form.setParameterValue("healthMetricAddCholesterolHDL", "50");
		form.setParameterValue("healthMetricAddCholesterolLDL", "200");
		form.setParameterValue("healthMetricAddCholesterolTri", "200");
		form.submit();
		WebResponse add = wc.getCurrentPage();
		assertTrue(add.getText().contains("Height must be greater than 0")); 
		assertTrue(add.getText().contains("Weight must be greater than 0"));
	}

	/*
	 * Authenticate HCP
	 * MID: 9000000000
	 * Password: pw
	 * Choose Edit Basic Health History
	 * enter 0000000002 and confirm
	 * Enter fields:
	 * Height: 10 inches
	 * Weight: 400 pounds
	 * Blood Pressure: 999/000
	 * Smokes: Y
	 * HDL: 50
	 * LDL: 200
	 * Triglycerides: 200
	 * Confirm and approve entries
	 */
	public void testCreatePHIRecord1() throws Exception {
		// login hcp
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L,"");
		
		// click Edit Basic Health Information
		wr = wr.getLinkWith("Basic Health Information").click();
		assertEquals(ADDRESS + "auth/getPatientID.jsp?forward=hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		// choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		//Logging has changed, and is now ambiguously specified, so removed this portion of the test
		//assertLogged(TransactionType.PATIENT_VIEW_HEALTH_METRICS, 9000000000L, 2L,"");
		
		// add a new record
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable form = fm.getScriptableObject();
		form.setParameterValue("healthMetricAddOfficeVisitID", "0");
		form.setParameterValue("healthMetricAddRecordID", "0");
		form.setParameterValue("healthMetricAddHeight", "10");
		form.setParameterValue("healthMetricAddWeight", "400");
		form.setParameterValue("healthMetricAddHeadCircumference", "0.1");
		form.setParameterValue("healthMetricAddSmokingStatus", "1");
		form.setParameterValue("healthMetricAddHouseholdSmokingStatus", "9");
		form.setParameterValue("healthMetricAddBloodPressureN", "999");
		form.setParameterValue("healthMetricAddBloodPressureD", "000");
		form.setParameterValue("healthMetricAddCholesterolHDL", "50");
		form.setParameterValue("healthMetricAddCholesterolLDL", "200");
		form.setParameterValue("healthMetricAddCholesterolTri", "200");
		form.submit();
		WebResponse add = wc.getCurrentPage();
		assertTrue(add.getText().contains("Information Recorded"));
		//Logging includes current age of patient.  Unpredictable
		//assertLogged(TransactionType.CREATE_HEALTH_METRIC, 9000000000L, 2L,"");
	}

	/*
	 * Authenticate HCP
	 * MID: 9000000000
	 * Password: pw
	 * Choose Edit Basic Health History
	 * enter 0000000002 and confirm
	 * Enter fields:
	 * Height: **
	 * Weight: 400 pounds
	 * Blood Pressure: 999/000
	 * Smokes: Y
	 * HDL: 50
	 * LDL: 200
	 * Triglycerides: 200
	 * Confirm and approve entries
	 */
	public void testCreatePHIRecord6() throws Exception {
		// login as hcp
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getResponse(ADDRESS + "auth/hcp/home.jsp");
		assertEquals("iTrust - HCP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L,"");
		
		// click on Edit Basic Health Information
		wr = wr.getLinkWith("Basic Health Information").click();
		assertEquals(ADDRESS + "auth/getPatientID.jsp?forward=hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		// Choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		//Logging includes current age of patient.  Unpredictable
		//assertLogged(TransactionType.PATIENT_HEALTH_INFORMATION_VIEW, 9000000000L, 2L,"");
		
		// attempt to add a record with invalid height
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable form = fm.getScriptableObject();
		form.setParameterValue("healthMetricAddOfficeVisitID", "0");
		form.setParameterValue("healthMetricAddRecordID", "0");
		form.setParameterValue("healthMetricAddHeight", "**");
		form.setParameterValue("healthMetricAddWeight", "400");
		form.setParameterValue("healthMetricAddHeadCircumference", "0.1");
		form.setParameterValue("healthMetricAddSmokingStatus", "1");
		form.setParameterValue("healthMetricAddHouseholdSmokingStatus", "9");
		form.setParameterValue("healthMetricAddBloodPressureN", "999");
		form.setParameterValue("healthMetricAddBloodPressureD", "000");
		form.setParameterValue("healthMetricAddCholesterolHDL", "50");
		form.setParameterValue("healthMetricAddCholesterolLDL", "200");
		form.setParameterValue("healthMetricAddCholesterolTri", "200");
		form.submit();
		WebResponse add = wc.getCurrentPage();
		assertTrue(add.getText().contains("Height: Up to 3-digit number + up to 1 decimal place"));
	}

	/*
	 * Authenticate HCP
	 * MID 9000000000
	 * Password: pw
	 * Choose Chronic Disease Risks
	 * Select and confirm patient 2.
	 */
	public void testDetectExistingHeartDiseaseRiskTest() throws Exception {
		// login hcp
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L,"");
		
		// click Chronic Disease Risks
		wr = wr.getLinkWith("Chronic Disease Risks").click();
		assertEquals(ADDRESS + "auth/getPatientID.jsp?forward=hcp-uap/chronicDiseaseRisks.jsp", wr.getURL().toString());
		// choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/chronicDiseaseRisks.jsp", wr.getURL().toString());
		assertLogged(TransactionType.RISK_FACTOR_VIEW, 9000000000L, 2L,"");
		
		// make sure the correct factors for heart disease are displayed
		assertTrue(wr.getText().contains("Patient is male"));
		assertTrue(wr.getText().contains("Patient's body mass index is over 30"));
		assertTrue(wr.getText().contains("Patient has hypertension"));
		assertTrue(wr.getText().contains("Patient has bad cholesterol"));
		assertTrue(wr.getText().contains("Patient is or was a smoker"));
		assertTrue(wr.getText().contains("Patient has had related diagnoses"));
		assertTrue(wr.getText().contains("Patient has a family history of this disease"));
	}
	
	public void testNoHealthRecordException() throws Exception{
		// login hcp
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L,"");		
		// click Chronic Disease Risks
		wr = wr.getLinkWith("Chronic Disease Risks").click();
		assertEquals(ADDRESS + "auth/getPatientID.jsp?forward=hcp-uap/chronicDiseaseRisks.jsp", wr.getURL().toString());
		// choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "4");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/chronicDiseaseRisks.jsp", wr.getURL().toString());
		// make sure the correct factors for heart disease are displayed
		assertTrue(wr.getText().contains("No Data"));
		assertNotLogged(TransactionType.RISK_FACTOR_VIEW, 9000000000L, 4L,"");
	}

	/*
	 * Choose Add Patient option.
	 * Last name: Kent
	 * First name: Clark
	 * Contact email: clark@ncsu.edu
	 * Street address 1: 1100 Main Campus Dr
	 * Street address 2: Rm 500
	 * City: Raleigh
	 * State: NC
	 * Zip code: 27606-4321
	 * Phone: 919-555-2000
	 * Emergency contact name: Lana Lang
	 * Emergency contact phone: 919-400-4000 
	 * Insurance company name: BlueCross
	 * Insurance company address 1: 1000 Walnut Street
	 * Insurance company address 2: Room 5454
	 * Insurance company city: Cary
	 * Insurance company state: NC 
	 * Insurance company zip code: 27513-9999
	 * Insurance company phone: 919-300-3000
	 * Insurance identification: BLUE0000000001
	 */
	public void testCreatePatient1() throws Exception {
		// login as uap
		WebConversation wc = login("8000000009", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - UAP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 8000000009L, 0L,"");
		
		WebResponse addPatient = wr.getLinkWith("Add Patient").click();
		// add a patient with valid information
		WebForm form = addPatient.getForms()[0];
		form.setParameter("firstName", "Clark");
		form.setParameter("lastName", "Kent");
		form.setParameter("email", "clark@ncsu.edu");
		WebResponse editPatient = form.submit();
		WebTable table = editPatient.getTables()[0];
		String newMID = table.getCellAsText(1, 1);
		assertTrue(editPatient.getText().contains("New patient Clark Kent successfully added!"));
		assertLogged(TransactionType.PATIENT_CREATE, 8000000009L, Long.parseLong(newMID),"");
		
		editPatient = editPatient.getLinkWith("Continue to patient information.").click();
		assertEquals("iTrust - Edit Patient", editPatient.getTitle());
		form = editPatient.getForms()[1];
		form.setParameter("streetAddress1", "1100 Main Campus Dr");
		form.setParameter("streetAddress2", "Rm 500");
		form.setParameter("city", "Raleigh");
		form.setParameter("state", "NC");
		form.setParameter("zip", "27606-4321");
		form.setParameter("phone", "919-555-2000");
		form.setParameter("emergencyName", "Lana Lang");
		form.setParameter("emergencyPhone", "919-400-4000");
		form.setParameter("icName", "BlueCross");
		form.setParameter("icAddress1", "1000 Walnut Street");
		form.setParameter("icAddress2", "Room 5454");
		form.setParameter("icCity", "Cary");
		form.setParameter("icState", "NC");
		form.setParameter("icZip", "27513-9999");
		form.setParameter("icPhone", "919-300-3000");
		form.setParameter("icID", "BLUE0000000001");
		form.getButtons()[2].click();
		WebResponse add = wc.getCurrentPage();
		assertTrue(add.getText().contains("Information Successfully Updated"));
		assertLogged(TransactionType.DEMOGRAPHICS_EDIT, 8000000009L, Long.parseLong(newMID),"");
	}

	/*
	 * Auuthenticate UAP
	 * MID 8000000009
	 * Password: pw
	 * Choose Edit Basic Health History
	 * enter 0000000002 and confirm
	 * Enter fields:
	 * Height: 10 inches
	 * Weight: 400 pounds
	 * Blood Pressure: 999/000
	 * Smokes: Y
	 * HDL: 50
	 * LDL: 200
	 * Triglycerides: 200
	 * Confirm and approve entries
	 */
	public void testCreatePHIRecord2() throws Exception {
		// login as uap
		WebConversation wc = login("8000000009", "pw");
		WebResponse wr = wc.getResponse(ADDRESS + "auth/uap/home.jsp");
		assertEquals("iTrust - UAP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 8000000009L, 0L,"");
		
		// click on Edit Basic Health Information
		wr = wr.getLinkWith("Basic Health Information").click();
		assertEquals(ADDRESS + "auth/getPatientID.jsp?forward=hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		// choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		//Logging includes current age of patient.  Unpredictable
		//assertLogged(TransactionType.PATIENT_HEALTH_INFORMATION_VIEW, 8000000009L, 2L,"");
		
		// add a record
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable form = fm.getScriptableObject();
		form.setParameterValue("healthMetricAddOfficeVisitID", "0");
		form.setParameterValue("healthMetricAddRecordID", "0");
		form.setParameterValue("healthMetricAddHeight", "10");
		form.setParameterValue("healthMetricAddWeight", "400");
		form.setParameterValue("healthMetricAddHeadCircumference", "0.1");
		form.setParameterValue("healthMetricAddSmokingStatus", "1");
		form.setParameterValue("healthMetricAddHouseholdSmokingStatus", "9");
		form.setParameterValue("healthMetricAddBloodPressureN", "999");
		form.setParameterValue("healthMetricAddBloodPressureD", "000");
		form.setParameterValue("healthMetricAddCholesterolHDL", "50");
		form.setParameterValue("healthMetricAddCholesterolLDL", "200");
		form.setParameterValue("healthMetricAddCholesterolTri", "200");
		form.submit();
		WebResponse add = wc.getCurrentPage();
		// make sure it was recorded
		assertTrue(add.getText().contains("Information Recorded"));
		//Logging includes current age of patient.  Unpredictable
		//assertLogged(TransactionType.CREATE_HEALTH_METRIC, 8000000009L, 2L,"");
	}

	/*
	 * Auuthenticate UAP
	 * MID 8000000009
	 * Password: pw
	 * Choose Edit Basic Health History
	 * enter 0000000002 and confirm
	 * Enter fields:
	 * Height: 10 inches
	 * Weight: 400 pounds
	 * Blood Pressure: 999/000
	 * Smokes: Y
	 * HDL: 150
	 * LDL: 200
	 * Triglycerides: 200
	 * Confirm and approve entries
	 */
	public void testCreatePHIRecord3() throws Exception {
		// login as uap
		WebConversation wc = login("8000000009", "pw");
		WebResponse wr = wc.getResponse(ADDRESS + "auth/uap/home.jsp");
		assertEquals("iTrust - UAP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 8000000009L, 0L,"");
		
		// click on Edit Basic Health Information
		wr = wr.getLinkWith("Basic Health Information").click();
		assertEquals(ADDRESS + "auth/getPatientID.jsp?forward=hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		// choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		//Logging includes current age of patient.  Unpredictable
		//assertLogged(TransactionType.PATIENT_HEALTH_INFORMATION_VIEW, 8000000009L, 2L,"");
		
		// attempt to add a record with invalid hdl
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable form = fm.getScriptableObject();
		form.setParameterValue("healthMetricAddOfficeVisitID", "0");
		form.setParameterValue("healthMetricAddRecordID", "0");
		form.setParameterValue("healthMetricAddHeight", "10");
		form.setParameterValue("healthMetricAddWeight", "400");
		form.setParameterValue("healthMetricAddHeadCircumference", "0.1");
		form.setParameterValue("healthMetricAddSmokingStatus", "1");
		form.setParameterValue("healthMetricAddHouseholdSmokingStatus", "9");
		form.setParameterValue("healthMetricAddBloodPressureN", "999");
		form.setParameterValue("healthMetricAddBloodPressureD", "000");
		form.setParameterValue("healthMetricAddCholesterolHDL", "150");
		form.setParameterValue("healthMetricAddCholesterolLDL", "200");
		form.setParameterValue("healthMetricAddCholesterolTri", "200");
		form.submit();
		WebResponse add = wc.getCurrentPage();
		assertTrue(add.getText().contains("Cholesterol HDL must be an integer in [0,89]"));	}

	/*
	 * Auuthenticate UAP
	 * MID 8000000009
	 * Password: pw
	 * Choose Edit Basic Health History
	 * enter 0000000002 and confirm
	 * Enter fields:
	 * Height: 10 inches
	 * Weight: <'>
	 * Blood Pressure: 999/000
	 * Smokes: Y
	 * HDL: 50
	 * LDL: 200
	 * Triglycerides: 200
	 * Confirm and approve entries
	*/
	public void testCreatePHIRecord4() throws Exception {
		// login hcp
		WebConversation wc = login("8000000009", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - UAP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 8000000009L, 0L,"");
		
		// click Edit Basic Health Information
		wr = wr.getLinkWith("Basic Health Information").click();
		assertEquals(ADDRESS + "auth/getPatientID.jsp?forward=hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		// choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		//Logging includes current age of patient.  Unpredictable
		//assertLogged(TransactionType.PATIENT_HEALTH_INFORMATION_VIEW, 8000000009L, 2L,"");
		
		// add a new record
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable form = fm.getScriptableObject();
		form.setParameterValue("healthMetricAddOfficeVisitID", "0");
		form.setParameterValue("healthMetricAddRecordID", "0");
		form.setParameterValue("healthMetricAddHeight", "10");
		form.setParameterValue("healthMetricAddWeight", "<'>");
		form.setParameterValue("healthMetricAddHeadCircumference", "0.1");
		form.setParameterValue("healthMetricAddSmokingStatus", "1");
		form.setParameterValue("healthMetricAddHouseholdSmokingStatus", "9");
		form.setParameterValue("healthMetricAddBloodPressureN", "999");
		form.setParameterValue("healthMetricAddBloodPressureD", "000");
		form.setParameterValue("healthMetricAddCholesterolHDL", "50");
		form.setParameterValue("healthMetricAddCholesterolLDL", "200");
		form.setParameterValue("healthMetricAddCholesterolTri", "200");
		form.submit();
		WebResponse add = wc.getCurrentPage();
		assertTrue(add.getText().contains("Weight: Up to 4-digit number + up to 1 decimal place"));
	}

	/*
	 * Auuthenticate UAP
	 * MID 8000000009
	 * Password: pw
	 * Choose Edit Basic Health History
	 * enter 0000000002 and confirm
	 * Enter fields:
	 * Height: 10 inches
	 * Weight: 40000 pounds
	 * Blood Pressure: 999/000
	 * Smokes: Y
	 * HDL: 50
	 * LDL: 200
	 * Triglycerides: 200
	 * Confirm and approve entries
	 */
	public void testCreatePHIRecord5() throws Exception {
		// login hcp
		WebConversation wc = login("8000000009", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - UAP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 8000000009L, 0L,"");
		
		// click Edit Basic Health Information
		wr = wr.getLinkWith("Basic Health Information").click();
		assertEquals(ADDRESS + "auth/getPatientID.jsp?forward=hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		// choose patient 2
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "2");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/editBasicHealth.jsp", wr.getURL().toString());
		//Logging includes current age of patient.  Unpredictable
		//assertLogged(TransactionType.PATIENT_HEALTH_INFORMATION_VIEW, 8000000009L, 2L,"");
		
		// add a new record
		WebForm fm = wr.getFormWithID("healthMetricAddRecordForm");
		WebForm.Scriptable form = fm.getScriptableObject();
		form.setParameterValue("healthMetricAddOfficeVisitID", "0");
		form.setParameterValue("healthMetricAddRecordID", "0");
		form.setParameterValue("healthMetricAddHeight", "10");
		form.setParameterValue("healthMetricAddWeight", "400000");
		form.setParameterValue("healthMetricAddHeadCircumference", "0.1");
		form.setParameterValue("healthMetricAddSmokingStatus", "1");
		form.setParameterValue("healthMetricAddHouseholdSmokingStatus", "9");
		form.setParameterValue("healthMetricAddBloodPressureN", "999");
		form.setParameterValue("healthMetricAddBloodPressureD", "000");
		form.setParameterValue("healthMetricAddCholesterolHDL", "50");
		form.setParameterValue("healthMetricAddCholesterolLDL", "200");
		form.setParameterValue("healthMetricAddCholesterolTri", "200");
		form.submit();
		WebResponse add = wc.getCurrentPage();
		assertTrue(add.getText().contains("Weight: Up to 4-digit number + up to 1 decimal place"));
	}
}
