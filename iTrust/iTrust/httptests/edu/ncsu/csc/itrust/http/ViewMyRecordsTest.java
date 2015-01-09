package edu.ncsu.csc.itrust.http;

import com.meterware.httpunit.TableRow;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import edu.ncsu.csc.itrust.enums.TransactionType;

public class ViewMyRecordsTest extends iTrustHTTPTest {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gen.clearAllTables();
		gen.icd9cmCodes();
		gen.ndCodes();
		gen.uap1();
		gen.patient2();
		gen.patient1();
		gen.patient4();
		gen.hcp0();
		gen.clearLoginFailures();
		gen.hcp3();
	}

	/*
	 * Authenticate Patient
	 * MID: 2
	 * Password: pw
	 * Choose option View My Records
	 */
	public void testViewRecords3() throws Exception {
		// login patient 2
		WebConversation wc = login("2", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - Patient Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 2L, 0L, "");
		
		// click on View My Records
		wr = wr.getLinkWith("My Records").click();
		assertEquals(ADDRESS + "auth/patient/viewMyRecords.jsp",wr.getURL().toString());
		// click on a particular office visit to check medication and diagnoses
		wr = wr.getLinkWith("Jun 10, 2007").click();
		assertTrue(wr.getText().contains("Diabetes with ketoacidosis"));
		assertTrue(wr.getText().contains("Prioglitazone"));
		assertTrue(wr.getText().contains("Tetracycline"));
		assertTrue(wr.getText().contains("Notes:"));
		assertLogged(TransactionType.MEDICAL_RECORD_VIEW, 2L, 2L, "");
	}

	/*
	 * Authenticate Patient
	 * MID: 4
	 * Password: pw
	 * Choose option View My Records
	 */
	public void testViewRecords4() throws Exception {
		// login patient who has no records
		WebConversation wc = login("4", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - Patient Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 4L, 0L, "");
		
		// upon viewing records, make sure that no exceptions are thrown
		wr = wr.getLinkWith("My Records").click();
		assertFalse(wr.getText().contains("Exception"));
		assertLogged(TransactionType.MEDICAL_RECORD_VIEW, 4L, 4L, "");
	}

	/*
	 * Authenticate Patient
	 * MID: 2
	 * Password: pw
	 * Choose option View My Records
	 * Choose to view records for mid 1, the person he represents.
	 */
	public void testViewRecords5() throws Exception {
		// login patient 2
		WebConversation wc = login("2", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - Patient Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 2L, 0L, "");
		
		// click View My Records
		wr = wr.getLinkWith("My Records").click();
		assertLogged(TransactionType.MEDICAL_RECORD_VIEW, 2L, 2L, "");
		
		WebTable wt = wr.getTableStartingWith("Patients Andy Represents");
		
		assertEquals("Random Person", wt.getTableCell(2,0).getLinkWith("Random Person").getText() );
		wr = wt.getTableCell(2,0).getLinkWith("Random Person").click();
		
		// check to make sure you are viewing patient 1's records
		assertTrue(wr.getText().contains("You are currently viewing your representee's records"));
		assertLogged(TransactionType.MEDICAL_RECORD_VIEW, 2L, 1L, "");
	}

	/*
	 */
	public void testPatientViewsHealthMetrics24YearOld() throws Exception {
		//Generate test specific data
		gen.patient204();
		gen.patientHealthMetrics();
		
		//Log patient 204 in
		WebConversation wc = login("204", "pw");
		WebResponse wr = wc.getCurrentPage();
		
		//Click View My Records
		wr = wr.getLinkWith("My Records").click();
		
		//Check that view was logged
		assertLogged(TransactionType.MEDICAL_RECORD_VIEW, 204L, 204L, "");
		
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
		assertEquals(wt.getCellAsText(2, 1), "210.1 lbs.(0.0%)");
		assertEquals(wt.getCellAsText(2, 2), "73.1 in.(0.0%)");
		assertEquals(wt.getCellAsText(2, 5), "160/100mmHg");
		assertEquals(wt.getCellAsText(2, 6), "non-smoking household");
		assertEquals(wt.getCellAsText(2, 7), "Never smoker");
		assertEquals(wt.getCellAsText(2, 8), "37");
		assertEquals(wt.getCellAsText(2, 9), "141");
		assertEquals(wt.getCellAsText(2, 10), "162");
		
		
	}

	/*
	 */
	public void testPatientViewsHealthMetrics2YearOld() throws Exception {
		//Generate test specific data
		gen.patient201();
		gen.patientHealthMetrics();
		
		//Log patient 201 in
		WebConversation wc = login("201", "pw");
		WebResponse wr = wc.getCurrentPage();
		
		//Click View My Records
		wr = wr.getLinkWith("My Records").click();
		
		//Check that view was logged
		assertLogged(TransactionType.MEDICAL_RECORD_VIEW, 201L, 201L, "");
		
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
	public void testPatientViewsHealthMetrics5YearOld() throws Exception {
		//Generate test specific data
		gen.patient202();
		gen.patientHealthMetrics();
		
		//Log patient 202 in
		WebConversation wc = login("202", "pw");
		WebResponse wr = wc.getCurrentPage();
		
		//Click View My Records
		wr = wr.getLinkWith("My Records").click();
		
		//Check that view was logged
		assertLogged(TransactionType.MEDICAL_RECORD_VIEW, 202L, 202L, "");
		
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
	public void testPatientViewsHealthMetrics20YearOld() throws Exception {
		//Generate test specific data
		gen.patient203();
		gen.patientHealthMetrics();
		
		//Log patient 203 in
		WebConversation wc = login("203", "pw");
		WebResponse wr = wc.getCurrentPage();
		
		//Click View My Records
		wr = wr.getLinkWith("My Records").click();
		
		//Check that view was logged
		assertLogged(TransactionType.MEDICAL_RECORD_VIEW, 203L, 203L, "");
		
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
}
