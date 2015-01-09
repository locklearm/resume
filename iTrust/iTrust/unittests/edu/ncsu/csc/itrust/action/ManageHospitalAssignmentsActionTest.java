package edu.ncsu.csc.itrust.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.HospitalBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.iTrustException;
import edu.ncsu.csc.itrust.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class ManageHospitalAssignmentsActionTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private DAOFactory evil = EvilDAOFactory.getEvilInstance();
	private ManageHospitalAssignmentsAction action;
	private ManageHospitalAssignmentsAction ltAction;
	private TestDataGenerator gen = new TestDataGenerator();
	private final static long performingAdmin = 9000000001L;
	private final static long hcp0 = 9000000000l;
	private final static long lt0 = 5000000000l;
	private final static String hosp0 = "1";
	private final static String hosp1 = "9191919191";
	private final static String hosp2 = "8181818181";

	@Override
	protected void setUp() throws Exception {
		gen.clearAllTables();
		gen.hcp0();
		gen.admin1();
		gen.hospitals();
		gen.clearHospitalAssignments();
		gen.ltData0();
		action = new ManageHospitalAssignmentsAction(factory, performingAdmin);
		ltAction = new ManageHospitalAssignmentsAction(factory, lt0);
	}

	private String doAssignment() throws iTrustException {
		return action.assignHCPToHospital("" + hcp0, hosp0);
	}

	private String doAssignment(String hospitalID) throws iTrustException, DBException {
		return action.assignHCPToHospital("" + hcp0, hospitalID);
	}

	public void testAssignHCPToHospital() throws iTrustException {
		assertEquals("HCP successfully assigned.", doAssignment());
		List<HospitalBean> h = action.getAssignedHospitals("" + hcp0);
		assertEquals(1, h.size());
		assertEquals("1", h.get(0).getHospitalID());
	}

	public void testAssignHCPToHospitalEvil() {
		action = new ManageHospitalAssignmentsAction(evil, performingAdmin);
		try {
			action.getAssignedHospitals("" + hcp0);
			fail();
		} catch (iTrustException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getExtendedMessage());
		}

	}

	public void testAssignDuplicate() throws iTrustException {
		assertEquals("HCP successfully assigned.", doAssignment());
		try {
			doAssignment();
			fail("testAssignDuplicate failed: assignHCPToHospital should have thrown exception");
		} catch (iTrustException e) {
			assertEquals("HCP 9000000000 already assigned to hospital 1", e.getMessage());
		}
	}

	public void testRemovePersonnelAssignmentToHospital() throws iTrustException {
		doAssignment();
		assertEquals("HCP successfully unassigned", action.removeHCPAssignmentToHospital("" + hcp0, hosp0));
		assertEquals(0, action.getAssignedHospitals("" + hcp0).size());
	}

	public void testRemoveNonAssigned() throws iTrustException {
		assertEquals("HCP not unassigned", action.removeHCPAssignmentToHospital("" + hcp0, hosp0));
	}

	public void testRemoveAll() throws iTrustException, Exception {
		doAssignment();
		doAssignment(hosp1);
		doAssignment(hosp2);
		assertEquals(3, action.getAssignedHospitals("" + hcp0).size());
		assertEquals(3, action.removeAllAssignmentsFromHCP("" + hcp0));
		assertEquals(0, action.getAssignedHospitals("" + hcp0).size());
	}

	public void testRemoveAllEvil() {
		action = new ManageHospitalAssignmentsAction(evil, performingAdmin);
		try {
			action.removeAllAssignmentsFromHCP("" + hcp0);
			fail();
		} catch (iTrustException e) {
		}

	}

	public void testRemovaAllUnassigned() throws iTrustException {
		assertEquals(0, action.removeAllAssignmentsFromHCP("" + hcp0));
	}

	public void testCheckHCPIDBadMID() {
		try {
			action.checkHCPID("90000000001");
			fail();
		} catch (iTrustException e) {

		}
	}

	public void testCheckHCPID() throws iTrustException {
		assertEquals(9000000000l, action.checkHCPID("9000000000"));
	}

	public void testCheckHCPIDStringMID() {
		try {
			action.checkHCPID("f");
			fail();
		} catch (iTrustException e) {
		}
	}

	public void testCheckHCPIDEvil() {
		try {
			action = new ManageHospitalAssignmentsAction(evil, performingAdmin);
			action.checkHCPID("9000000000");
			fail();
		} catch (iTrustException e) {
		}
	}

	public void testGetAvailableHospitals() throws iTrustException {
		assertSame(10, action.getAvailableHospitals("9000000000").size());
	}

	public void testGetAvailableHospitalsBadMID() {
		try {
			action.getAvailableHospitals("f");
			fail();
		} catch (iTrustException e) {
		}
	}

	public void testGetAssignedHospitalsBadMID() {
		try {
			action.getAssignedHospitals("f");
			fail();
		} catch (iTrustException e) {
		}
	}

	public void testAssignHCPToHospitalBadID() {
		try {
			action.assignHCPToHospital("f", "1");
			fail();
		} catch (iTrustException e) {
		}
	}

	public void testRemoveHCPtoHospitalBadID() {
		try {
			action.removeHCPAssignmentToHospital("f", "1");
			fail();
		} catch (iTrustException e) {
		}
	}

	public void testRemoveHCPAssignmentsBadID() {
		try {
			action.removeAllAssignmentsFromHCP("l");
			fail();
		} catch (iTrustException e) {
		}
	}
	
	/**
	 * New Method to check and make sure LTs only have 1 hospital
	 * 
	 * @throws iTrustException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void testCheckLTHospital() throws iTrustException, FileNotFoundException, IOException, SQLException {
		assertTrue(ltAction.checkLTHospital("5000000001"));
		gen.clearHospitalAssignments();
		assertFalse(ltAction.checkLTHospital("5000000001"));
		
		
	}

	/**
	 * This method checks to make sure checkLTHospital method can correctly handle
	 * and illegal MID.
	 */
	public void testCheckLTIDStringMID() {
		try {
			assertFalse(ltAction.checkLTHospital("ABCD"));
			fail();
		} catch (iTrustException e) {
		}
	}

}
