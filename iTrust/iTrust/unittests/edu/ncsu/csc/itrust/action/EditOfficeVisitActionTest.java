package edu.ncsu.csc.itrust.action;

import java.util.List;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.Email;
import edu.ncsu.csc.itrust.beans.HospitalBean;
import edu.ncsu.csc.itrust.beans.OfficeVisitBean;
import edu.ncsu.csc.itrust.beans.forms.EditOfficeVisitForm;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.iTrustException;
import edu.ncsu.csc.itrust.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

/**
 * Test all office visit by doctors
 *
 */
public class EditOfficeVisitActionTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private TestDataGenerator gen = new TestDataGenerator();
	private EditOfficeVisitAction action;

	@Override
	protected void setUp() throws Exception {
		gen.clearAllTables();
		gen.hospitals();
		gen.hcp0();
		gen.patient1();
		gen.admin1();
		gen.officeVisit1();
		gen.ndCodes();
		
		action = new EditOfficeVisitAction(factory, 9000000001L, "1", "1");
	}

	public void testOVID() {
		try {
			action = new EditOfficeVisitAction(factory, 0l, "1", "NaN");
			fail("exception should have been thrown");
		} catch (iTrustException e) {
			assertEquals("Office Visit ID is not a number: For input string: \"NaN\"", e.getMessage());
		}
	}

	public void testEvilDatabase() {
		try {
			action = new EditOfficeVisitAction(EvilDAOFactory.getEvilInstance(), 0l, "1", "1");
			fail("exception should have been thrown");
		} catch (iTrustException e) {
			assertEquals(
					"A database exception has occurred. Please see the log in the console for stacktrace", e
							.getMessage());
			DBException dbe = (DBException) e;
			assertEquals(EvilDAOFactory.MESSAGE, dbe.getSQLException().getMessage());
		}
	}

	public void testOVDoesntExist() {
		try {
			action = new EditOfficeVisitAction(TestDAOFactory.getTestInstance(), 0l, "1", "158");
			fail("exception should have been thrown");
		} catch (iTrustException e) {
			assertEquals("Office Visit 158 with Patient MID 1 does not exist", e.getMessage());
		}
	}
/** Test Hospital location
 * 
 * @throws Exception
 */
	public void testGetHospitals() throws Exception {
		// NOTE: may have to fix this... use different pid and ovid.
		action = new EditOfficeVisitAction(factory, 9000000000L, "1", "1");
		List<HospitalBean> hospitals = action.getHospitals();
		assertEquals(10, hospitals.size());
		// First comes hospitals associated with the HCP in alphabetical order.
		// Then comes all other hospitals in alphabetical order.
		assertEquals("Test Hospital 8181818181", hospitals.get(0).getHospitalName());
		assertEquals("Test Hospital 9191919191", hospitals.get(1).getHospitalName());
		assertEquals("Central Hospital", hospitals.get(2).getHospitalName());
		assertEquals("Facebook Rehab Center", hospitals.get(3).getHospitalName());
		assertEquals("Health Institute Dr. E", hospitals.get(4).getHospitalName());
		assertEquals("Health Institute Mr. Barry", hospitals.get(5).getHospitalName());
		assertEquals("Health Institute Mr. Donghoon", hospitals.get(6).getHospitalName());
		assertEquals("Le Awesome Hospital", hospitals.get(7).getHospitalName());
	}

	/**
	 * Test patient office visit
	 * @throws iTrustException
	 */
	public void testGetOfficeVisit() throws iTrustException {
		OfficeVisitBean ovb = action.getOfficeVisit();
		assertEquals(1l, action.getOvID());
		assertEquals("Generated for Death for Patient: 1", ovb.getNotes());
		assertEquals(9000000000l, ovb.getHcpID());
		assertEquals(1l, ovb.getID());
		assertEquals(1, ovb.getVisitID());
		/*assertEquals(0, ovb.getDiagnoses().size());*/
		assertEquals("1", ovb.getHospitalID());
		
		assertEquals(0, action.prescriptions().getPrescriptions().size());
	}

	public void testUpdateInformationEmptyForm() {
		try {
			EditOfficeVisitForm frm = new EditOfficeVisitForm();
			action.updateInformation(frm, false);
			fail("should have thrown exception");
		} catch (FormValidationException fve) {
		}
	}

	/**
	 * Test if patient information is update
	 * test update information semicolon
	 * test updateInformation Octothorpe
	 * @throws FormValidationException
	 */
	public void testUpdateInformation() throws FormValidationException {
		EditOfficeVisitForm frm = new EditOfficeVisitForm();
		frm.setHcpID("9000000000");
		frm.setPatientID("1");
		frm.setVisitDate("05/02/2001");
		//frm.setAddDiagID("79.3");
		action.updateInformation(frm, false);
	}
	
	public void testUpdateInformationSemicolon() throws FormValidationException {
		EditOfficeVisitForm frm = new EditOfficeVisitForm();
		frm.setHcpID("9000000000");
		frm.setPatientID("1");
		frm.setVisitDate("05/02/2001");
		frm.setNotes("semicolon test ;");
		action.updateInformation(frm, false);
	}
	
	public void testUpdateInformationOctothorpe() throws Exception {
		EditOfficeVisitForm frm = new EditOfficeVisitForm();
		frm.setHcpID("9000000000");
		frm.setPatientID("1");
		frm.setVisitDate("05/02/2001");
		frm.setNotes("semicolon test #");
		try {
			action.updateInformation(frm, false);
		} catch (FormValidationException e) {
			fail(e.getMessage());
		}

	}
	
	public void testUpdateInformationNewOfficeVisit() throws Exception {
		action = new EditOfficeVisitAction(factory, 9000000001L, "1");
		assertEquals(true, action.isUnsaved());
		assertEquals(-1, action.getOvID());
		EditOfficeVisitForm frm = new EditOfficeVisitForm();
		frm.setHcpID("9000000001");
		frm.setPatientID("1");
		frm.setVisitDate("05/02/2001");
		frm.setNotes("That was a doctor's visit");
		try {
			action.updateInformation(frm, false);
		} catch (FormValidationException e) {
			fail(e.getMessage());
		}
		assertEquals(false, action.isUnsaved());
		assertFalse(-1 == action.getOvID());
	}

	/*
	public void testCheckAddPrescription() throws FormValidationException {
		EditOfficeVisitForm frm = new EditOfficeVisitForm();
		frm.setHcpID("9000000000");
		frm.setPatientID("1");
		frm.setVisitDate("05/02/2001");
		frm.setAddMedID("5");
		frm.setDosage("5");
		frm.setStartDate("02/02/2007");
		frm.setEndDate("02/02/2008");
		frm.setOvID("9000000000");
		
		try {
			frm.setOverrideCode(null);
			fail();
		} catch(FormValidationException e) {
			
		}
		
		String[] overrideCodes = {"00001", "00002"};
		frm.setOverrideCode(overrideCodes);
		frm.setOverrideComment("Other reasons");
		action.updateInformation(frm);
		
		
	}*/
	/*
	public void testCheckRemoveSubaction() throws FormValidationException {
		EditOfficeVisitForm frm = new EditOfficeVisitForm();
		frm.setHcpID("9000000000");
		frm.setPatientID("1");
		frm.setVisitDate("05/02/2001");
		frm.setAddDiagID("35");
		frm.setRemoveDiagID("35");
		action.updateInformation(frm);
	}
	*/
	
	/*public void testNoAllergyPrescribe() throws FormValidationException, Exception {
		gen.patient2();
		gen.officeVisit2();
		assertTrue(action.hasInteraction("081096", "2","2009/9/22","2009/9/22")=="");
		
	}

	public void testInteraction() throws FormValidationException, Exception {
		gen.officeVisit3();
		gen.ndCodes1();
		gen.drugInteractions3();
		assertFalse(action.hasInteraction("619580501", "1","9/22/2009","10/11/2009")=="");
		
	}*/
	
	public void testMakeEmailApp() throws FormValidationException, Exception {
		gen.patient2();
		gen.hcp0();
		Email testEmail = action.makeEmailApp(9000000000L, "2", "You are allergic.");
		assertEquals("no-reply@itrust.com",testEmail.getFrom());
		assertEquals("andy.programmer@gmail.com",testEmail.getToListStr());	
		assertEquals("HCP has prescribed you a potentially dangerous medication",testEmail.getSubject());
		assertEquals("Kelly Doctor has prescribed a medication that you are allergic to or that has a known interaction with a drug you are currently taking. You are allergic.",testEmail.getBody());
	}
	/*
	public void testIsAllergyOnList()  throws Exception {
		gen.patient2();
		assertEquals("Allergy: Penicillin. First Found: 06/04/2007", action.isAllergyOnList("2", "664662530"));
	}
	*/
	/*
	public void testIsAllergyOnListWithBadPatientID()  throws Exception {
		gen.patient2();
		try{
			action.isAllergyOnList("2", "4");
			action.isAllergyOnList("a", "4");
			fail("Patient does not follow long format!!!!");
		}catch(Exception e){
			
		}
	}
	*/
}
