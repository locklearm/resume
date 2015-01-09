package edu.ncsu.csc.itrust.dao.personnel;

import java.util.List;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class PersonnelExistsTest extends TestCase {
	PersonnelDAO personnelDAO = TestDAOFactory.getTestInstance().getPersonnelDAO();
	TestDataGenerator gen;

	@Override
	protected void setUp() throws Exception {
		gen = new TestDataGenerator();
		gen.clearAllTables();
	}

	public void testGetPersonnel2() throws Exception {
		gen.uap1();
		assertTrue(personnelDAO.checkPersonnelExists(8000000009l));
		assertFalse(personnelDAO.checkPersonnelExists(8999999999l));
	}
	
	public void testGetPersonnelFromHospitalNoSpeciality() throws Exception {
		//Get test data for a known association
		gen.hcp0();
		
		//call the method in question
		List<PersonnelBean> hcps = personnelDAO.getPersonnelFromHospital("9191919191");
		
		//Check that we have the correct person
		assertEquals(Long.parseLong("9000000000"), hcps.get(0).getMID());
		
		
	}
	
	public void testGetPersonnelFromHospitalNoSpecialityNoAssociations() throws Exception {
		
		//call the method in question with bad hosID
		List<PersonnelBean> hcps = personnelDAO.getPersonnelFromHospital("45755");
		
		//Check that list size is zero
		assertEquals(0, hcps.size());
		
		
	}
	
}
