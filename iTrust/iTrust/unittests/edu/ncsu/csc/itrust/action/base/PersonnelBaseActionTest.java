package edu.ncsu.csc.itrust.action.base;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class PersonnelBaseActionTest {
	
	private AuthDAO authDAO = TestDAOFactory.getTestInstance().getAuthDAO();
	private TestDataGenerator gen = new TestDataGenerator();
	PersonnelBaseAction testPBA = null;
	
	@Before
	public void setUp() throws Exception {
		gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();
		authDAO = TestDAOFactory.getTestInstance().getAuthDAO();
		
		// Create a PersonnelBaseAction for Shelly Vang, MID 8000000010
		testPBA = new PersonnelBaseAction (TestDAOFactory.getTestInstance(), "8000000010");
	}

	@Test
	public void testGetPid() {
		assertEquals (8000000010L, testPBA.getPid());
	}

}
