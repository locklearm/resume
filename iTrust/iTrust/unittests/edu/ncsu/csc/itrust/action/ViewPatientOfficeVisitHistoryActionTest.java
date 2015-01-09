package edu.ncsu.csc.itrust.action;

import java.util.List;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.PatientVisitBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class ViewPatientOfficeVisitHistoryActionTest extends TestCase{

	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private TestDataGenerator gen = new TestDataGenerator();
	private ViewPatientOfficeVisitHistoryAction action;
	
	@Override
	protected void setUp() throws Exception{
		action = new ViewPatientOfficeVisitHistoryAction(factory, 9000000000L);
		gen.clearAllTables();
		gen.standardData();
	}
	
	public void testGetPersonnel() throws Exception {
		PersonnelBean hcp = action.getPersonnel();
		assertNotNull(hcp.getFirstName(),"Kelly");
	}
	
	public void testGetPatients() throws Exception {
		List<PatientVisitBean> list = action.getPatients();
		assertEquals(25,list.size());
		assertEquals("01",list.get(16).getLastOVDateD());
		assertEquals("01",list.get(16).getLastOVDateM());
	}
	
	//added by melockle
	public void testPatientInList() throws Exception {
		List<PatientVisitBean> list = action.getPatients();
		PatientVisitBean exampleVisit = list.get(0);
		PatientBean patient = exampleVisit.getPatient();
		String date = exampleVisit.getLastOVDate();
		assertTrue(action.PatientInList(patient, date));
	}
}
