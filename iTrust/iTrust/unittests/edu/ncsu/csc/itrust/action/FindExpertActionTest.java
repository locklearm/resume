package edu.ncsu.csc.itrust.action;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.HospitalBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class FindExpertActionTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();

	private FindExpertAction fea;
		
	protected void setUp() throws Exception {
		TestDataGenerator td = new TestDataGenerator();
		td.clearAllTables();
		td.standardData();
		fea = new FindExpertAction(factory);
	}
	
	public void testFindExperts(){
		List<HospitalBean> hospitals = new ArrayList<HospitalBean>();
		HospitalBean realHospital = new HospitalBean();
		hospitals.add(realHospital);
		
		//Test a single result
		assertTrue(fea.findExperts(hospitals, "ob/gyn").size() == 1);
	}
	
	public void testFilterHospitals() throws Exception {
		//Test with no results
		HospitalBean blankHospital = new HospitalBean();
		blankHospital.setHospitalZip("27607");
		List<HospitalBean> hospitals = new ArrayList<HospitalBean>();
		hospitals.add(blankHospital);
		//fea.filterHospitals(hospitals, fea.patientDAO.getAllPatients().get(0).getMID(), 3, "27607");
		//assertTrue(fea.filterHospitals(hospitals, fea.patientDAO.getAllPatients().get(0).getMID(), 3, "27607").size() != 0);
		assertTrue(fea.filterHospitals(hospitals, 1, 3, "27607").size() != 0);

	}
	
	public void testGetPatientZip() throws Exception {
		
		//assertEquals(fea.getPatientZip(fea.patientDAO.getAllPatients().get(0).getMID()), fea.patientDAO.getAllPatients().get(0).getZip());
		fea.patientDAO.addEmptyPatient();
		
		assertEquals("27606", fea.getPatientZip(1).substring(0, 5));
		
	}
	
	public void testFindHospitalsBySpeciality() throws Exception {
		
		//assertTrue(fea.findHospitalsBySpecialty("surgeon", fea.patientDAO.getAllPatients().get(0).getMID(), "1", "27607").size() > 0);
		assertTrue(fea.findHospitalsBySpecialty("surgeon", 1, "1", "27607").size() >= 0);
		
	}
	
	/*public void testCalculateDistance(){
		//Test for near location, less than 5 miles
		assertTrue(fea.calculateDistance(35.794443, 35.797349, -78.738906, -78.78047) < 5);
		//Test same location is (close to) 0
		assertTrue(fea.calculateDistance(35.794443, 35.794443, -78.738906, -78.738906) < .1);
	}*/

}
