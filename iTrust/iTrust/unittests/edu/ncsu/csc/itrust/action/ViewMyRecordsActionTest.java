package edu.ncsu.csc.itrust.action;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.FamilyDAO;
import java.util.List;
import edu.ncsu.csc.itrust.beans.AllergyBean;
import edu.ncsu.csc.itrust.beans.Email;
import edu.ncsu.csc.itrust.beans.FamilyMemberBean;
import edu.ncsu.csc.itrust.beans.HealthRecord;
import edu.ncsu.csc.itrust.beans.LabProcedureBean;
import edu.ncsu.csc.itrust.beans.OfficeVisitBean;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.beans.ReportRequestBean;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.exception.iTrustException;
import edu.ncsu.csc.itrust.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class ViewMyRecordsActionTest extends TestCase {
	private ViewMyRecordsAction action;
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private DAOFactory evil = EvilDAOFactory.getEvilInstance();
	private FamilyDAO famDAO = new FamilyDAO(factory);
	private List<FamilyMemberBean> fmbList = null;
	private FamilyMemberBean fmBean = null;
	
	private long pid = 2L;
	private long hcpId = 9000000000L;

	@Override
	protected void setUp() throws Exception {
		TestDataGenerator gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();

		action = new ViewMyRecordsAction(factory, pid);
		fmbList = famDAO.getParents(5);
		fmBean = fmbList.get(0);
	}

	public void testViewMyRecords() throws Exception {
		assertEquals(pid, action.getPatient().getMID());
		assertEquals(2, action.getAllergies().size());
		assertEquals(9, action.getFamily().size());
		assertEquals(2, action.getAllHealthRecords().size());
		assertEquals(10, action.getAllOfficeVisits().size());
		assertEquals(6, action.getRepresented().size());
		assertEquals(0, action.getRepresenting().size());
		assertTrue(action.isSurveyCompleted(952));
		assertEquals(1, action.getLabs().size());
		action.representPatient("1");
		assertEquals(1L, action.getPatient().getMID());
		assertTrue(action.doesFamilyMemberHaveHighBP(fmBean));
		assertTrue(action.doesFamilyMemberHaveDiabetes(fmBean));
		assertTrue(action.isFamilyMemberSmoker(fmBean));
		assertFalse(action.doesFamilyMemberHaveCancer(fmBean));
		assertFalse(action.doesFamilyMemberHaveHighCholesterol(fmBean));
		assertFalse(action.doesFamilyMemberHaveHeartDisease(fmBean));
		assertTrue(action.getFamilyMemberCOD(fmBean).contains("Diabetes"));
		assertEquals(5, new ViewMyRecordsAction(factory, 5L).getFamilyHistory().size());
	}
	
	public void testGetters() throws Exception{
		assertEquals("10666-6", action.getProcedureName("10666-6").get(0).getLabProcedureCode());
		assertEquals(2, action.getCompleteOfficeVisit(952).getPatientID());
		assertEquals("Injection procedure", action.getProcedures(955).get(0).getDescription());
	}
	
	public void testViewNonExistantRecords()  {
		action = new ViewMyRecordsAction(evil, 0l);
		try
		{
			action.getFamilyHistory();
		}
		catch (iTrustException e)
		{
			assertEquals("A database exception has occurred. Please see the log in the console for stacktrace", e.getMessage());
		}
		
		try
		{
			action.getFamily();
		}
		catch (iTrustException e)
		{
			assertEquals("A database exception has occurred. Please see the log in the console for stacktrace", e.getMessage());
		}
	}

	public void testRepresentPatient() throws iTrustException {
		String StrRep = "3";
		long longRep = (new Long(StrRep)).longValue();
		long mid = action.representPatient("3");
		
		assertEquals(longRep, mid);
		
		try {
			action.representPatient("ab");
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		try {
			action.representPatient("3245974309857234");
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	public void testGetPatient() throws iTrustException {
		PatientBean pBean = action.getPatient();
		assertEquals(2L, pBean.getMID());

		pBean = action.getPatient(2L);
		assertEquals(2L, pBean.getMID());
	}

	public void testGetPersonnel() throws iTrustException {
		PersonnelBean pBean = action.getPersonnel(hcpId);
		
		assertEquals(hcpId, pBean.getMID());
	}
	
	public void testGetEmailHistory() throws iTrustException {
		List<Email> eList = action.getEmailHistory();
		
		assertEquals(2, eList.size());
	}
	
	public void testGetAllergies() throws iTrustException {
		List<AllergyBean> abList = action.getAllergies();
		
		assertEquals(2, abList.size());
	}
	
	public void testGetFamily() throws iTrustException {
		List<FamilyMemberBean> fmbList = action.getFamily();
		
		assertEquals(9, fmbList.size());
	}
	
	public void testGetFamilyHistory() throws iTrustException {
		List<FamilyMemberBean> fmbList = action.getFamilyHistory();
		
		assertEquals(5, fmbList.size());
	}
	
	public void testGetAllHealthRecords() throws iTrustException {
		List<HealthRecord> hrList = action.getAllHealthRecords();
		
		assertEquals(2, hrList.size());
	}

	public void testGetFamilyHealthRecords() throws iTrustException {
		List<HealthRecord> hrList = action.getFamilyHealthRecords(this.pid);
		
		assertEquals(2, hrList.size());
	}
	
	public void testGetAllOfficeVisits() throws iTrustException {
		List<OfficeVisitBean> ovbList = action.getAllOfficeVisits();
		
		assertEquals(10, ovbList.size());
	}
	
	public void testGetRepresented() throws iTrustException {
		List<PatientBean> pbList = action.getRepresented();
		
		assertEquals(6, pbList.size());
	}

	public void testGetRepresenting() throws iTrustException {
		List<PatientBean> pbList = action.getRepresenting();
		
		assertEquals(0, pbList.size());
	}
	
	public void testIsSurveyCompleted() throws iTrustException {
		List<OfficeVisitBean> ovbList = action.getAllOfficeVisits();

		OfficeVisitBean ovBean = ovbList.get(0);
		
		assertFalse(action.isSurveyCompleted(ovBean.getID()));
	}
	
	public void testGetLabs() throws iTrustException {
		List<LabProcedureBean> lpbList = action.getLabs();
		
		assertEquals(1, lpbList.size());
	}
	
	public void testGetReportRequests() throws iTrustException {
		List<ReportRequestBean> rrbList = action.getReportRequests();

		assertEquals(4, rrbList.size());
	}
	
	public void testDoesFamilyMemberHaveHighBP() throws iTrustException {
		assertTrue(action.doesFamilyMemberHaveHighBP(fmBean));
	}
	
	public void testDoesFamilyMemberHaveHighCholesterol() throws iTrustException {
		assertFalse(action.doesFamilyMemberHaveHighCholesterol(fmBean));
	}
	
	public void testDoesFamilyMemberHaveDiabetes() throws iTrustException {
		assertTrue(action.doesFamilyMemberHaveDiabetes(fmBean));
	}

	public void testDoesFamilyMemberHaveCancer() throws iTrustException {
		assertFalse(action.doesFamilyMemberHaveCancer(fmBean));
	}

	public void testDoesFamilyMemberHaveHeartDisease() throws iTrustException {
		assertFalse(action.doesFamilyMemberHaveHeartDisease(fmBean));
	}

	public void testIsFamilyMemberSmoker() throws iTrustException {
		assertTrue(action.isFamilyMemberSmoker(fmBean));
	}

	public void testGetFamilyMemberCOD() throws iTrustException {
		assertEquals("Diabetes with ketoacidosis", action.getFamilyMemberCOD(fmBean));
	}
	
	public void testGetSpecificLabs() throws iTrustException {
		List<LabProcedureBean> lpbList = action.getSpecificLabs(pid, "10640-1");
		
		assertEquals(1, lpbList.size());
	}
	
}
