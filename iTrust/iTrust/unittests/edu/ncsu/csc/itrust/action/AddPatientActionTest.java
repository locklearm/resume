/**
 * Tests for AddPatientAction
 */

package edu.ncsu.csc.itrust.action;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class AddPatientActionTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private TestDataGenerator gen;
	private AddPatientAction action;
	
/**
 * Sets up defaults
 */
	@Override
	protected void setUp() throws Exception {
		gen = new TestDataGenerator();
		
		gen.transactionLog();
		gen.hcp0();
		action = new AddPatientAction(factory, 9000000000L);
	}

	/**
	 * Tests adding a new patient
	 * @throws Exception
	 */
	public void testAddPatient() throws Exception {
		PatientBean p = new PatientBean();
		p.setFirstName("Cosmo");
		p.setLastName("Kramer");
		p.setEmail("cosmo@kramer.com");
		long newMID = action.addPatient(p);
		assertEquals(p.getMID(), newMID);
	}
}
