package edu.ncsu.csc.itrust.http;

import com.meterware.httpunit.TableRow;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import edu.ncsu.csc.itrust.enums.TransactionType;

public class PrintImmunizationRecordsTest extends iTrustHTTPTest {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gen.clearAllTables();
		gen.standardData();
	}

	public void testPrintPatientImmunizationRecords() throws Exception {
		// login patient 2
		WebConversation wc = login("5", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - Patient Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 5L, 0L, "");
		
		// click on View My Records
		wr = wr.getLinkWith("My Records").click();
		assertEquals(ADDRESS + "auth/patient/viewMyRecords.jsp",wr.getURL().toString());
		// click on a particular office visit to check medication and diagnoses
		wr = wr.getLinkWith("Print Immunization Report").click();
		assertTrue(wr.getText().contains("Baby Programmer"));
		assertTrue(wr.getText().contains("2004-11-10"));
		assertTrue(wr.getText().contains("Rotavirus"));
		assertLogged(TransactionType.PATIENT_PRINT_IMMUNIZATION_RECORDS, 5L, 5L, "");
	}
		public void testPrintDependentImmunizationRecords() throws Exception {
		// login patient 2
		WebConversation wc = login("2", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - Patient Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 2L, 0L, "");
		
		// click on View My Records
		wr = wr.getLinkWith("My Records").click();
		assertEquals(ADDRESS + "auth/patient/viewMyRecords.jsp",wr.getURL().toString());
		wr = wr.getLinkWith("Baby Programmer").click();
		// click on a particular office visit to check medication and diagnoses
		wr = wr.getLinkWith("Print Immunization Report").click();
		assertTrue(wr.getText().contains("Baby Programmer"));
		assertTrue(wr.getText().contains("2004-11-10"));
		assertTrue(wr.getText().contains("Rotavirus"));
		assertLogged(TransactionType.PATIENT_PRINT_IMMUNIZATION_RECORDS, 2L, 5L, "");
	}

	
	public void testPrintPatientImmunizationRecordsWithNoData() throws Exception {
		// login patient 2
		WebConversation wc = login("2", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - Patient Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 2L, 0L, "");
		
		// click on View My Records
		wr = wr.getLinkWith("My Records").click();
		assertEquals(ADDRESS + "auth/patient/viewMyRecords.jsp",wr.getURL().toString());
		// click on a particular office visit to check medication and diagnoses
		wr = wr.getLinkWith("Print Immunization Report").click();
		assertTrue(wr.getText().contains("No Data"));
		assertLogged(TransactionType.PATIENT_PRINT_IMMUNIZATION_RECORDS, 2L, 2L, "");
	}
	
	public void testPrintPatientImmunizationRecordsAsHCP() throws Exception {
		// login UAP
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 1
		WebForm patientForm = wr.getForms()[0];
		patientForm.getScriptableObject().setParameterValue("UID_PATIENTID", "5");
		patientForm.getButtons()[1].click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/hcp-uap/documentOfficeVisit.jsp", wr.getURL().toString());
		// click Yes, Document Office Visit
		WebForm form = wr.getForms()[0];
		form.getButtons()[0].click();
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Document Office Visit", wr.getTitle());
		// add a new office visit
		wr = wr.getLinkWith("Print Immunization Report").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Baby Programmer"));
		assertTrue(wr.getText().contains("2004-11-10"));
		assertTrue(wr.getText().contains("Rotavirus"));
		assertLogged(TransactionType.PATIENT_PRINT_IMMUNIZATION_RECORDS, 9000000000L, 5L, "");
	}
	
	public void testPrintPatientImmunizationRecordsAsHCPWithNoData() throws Exception {
		// login UAP
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("Document Office Visit").click();
		// choose patient 1
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
		wr = wr.getLinkWith("Print Immunization Report").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("No Data"));
		assertLogged(TransactionType.PATIENT_PRINT_IMMUNIZATION_RECORDS, 9000000000L, 2L, "");
	}
	
	public void testPrintPatientImmunizationRecordsAsHCPInEditPHR() throws Exception {
		// login UAP
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		// click Document Office Visit
		wr = wr.getLinkWith("All Patients").click();
		// choose patient 1
		wr = wc.getCurrentPage();
		wr = wr.getLinkWith("Baby Programmer").click();
		assertEquals(ADDRESS + "auth/hcp-uap/editPHR.jsp?patient=4", wr.getURL().toString());
		// click Yes, Document Office Visit
		wr = wc.getCurrentPage();
		assertEquals("iTrust - Edit Personal Health Record", wr.getTitle());
		// add a new office visit
		wr = wr.getLinkWith("Print Immunization Report").click();
		wr = wc.getCurrentPage();
		assertTrue(wr.getText().contains("Baby Programmer"));
		assertTrue(wr.getText().contains("2004-11-10"));
		assertTrue(wr.getText().contains("Rotavirus"));
		assertLogged(TransactionType.PATIENT_PRINT_IMMUNIZATION_RECORDS, 9000000000L, 5L, "");
	}
}



