package edu.ncsu.csc.itrust.action;

import static edu.ncsu.csc.itrust.testutils.JUnitiTrustUtils.assertTransactionsNone;
import java.util.List;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.exception.iTrustException;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class EditRepresentativesActionTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private TestDataGenerator gen = new TestDataGenerator();
	private EditRepresentativesAction action;

	@Override
	protected void setUp() throws Exception {
		gen.clearAllTables();
		gen.patient1(); // 2 represents 1, but not 4
		gen.patient2();
		gen.patient4();
	}

	public void testGetRepresentatives() throws Exception {
		action = new EditRepresentativesAction(factory, 9000000000L, "2");
		List<PatientBean> reps = action.getRepresented(2L);
		assertEquals(1, reps.size());
		assertEquals(1L, reps.get(0).getMID());
	}

	public void testAddRepresentative() throws Exception {
		action = new EditRepresentativesAction(factory, 9000000000L, "2");
		action.addRepresentative("4");
		List<PatientBean> reps = action.getRepresented(2);
		assertEquals(2, reps.size());
		assertEquals(4L, reps.get(1).getMID());
	}

	public void testRemoveRepresentative() throws Exception {
		action = new EditRepresentativesAction(factory, 9000000000L, "2");
		action.removeRepresentative("1");
		List<PatientBean> reps = action.getRepresented(2);
		assertEquals(0, reps.size());
	}

	public void testAddNotNumber() throws Exception {
		action = new EditRepresentativesAction(factory, 9000000000L, "2");
		assertEquals("MID not a number", action.addRepresentative("a"));
		assertEquals("MID not a number", action.removeRepresentative("a"));
	}

	public void testRemoveNothing() throws Exception {
		action = new EditRepresentativesAction(factory, 9000000000L, "2");
		assertEquals("No change made", action.removeRepresentative("2"));
		assertTransactionsNone();
		assertEquals(1, action.getRepresented(2).size());
	}

	public void testCannotRepresentSelf() throws Exception {
		action = new EditRepresentativesAction(factory, 9000000000L, "2");
		try {
			action.addRepresentative("2");
			fail("exception should have been thrown");
		} catch (iTrustException e) {
			assertEquals("This user cannot represent themselves.", e.getMessage());
		}
		assertTransactionsNone();
		assertEquals(1, action.getRepresented(2).size());
	}
	
	public void testNotAPatient() throws Exception {
		action = new EditRepresentativesAction(factory, 9000000000L, "2");
		assertEquals("No change made", action.removeRepresentative("9000000000"));
		assertTransactionsNone();
		assertEquals(1, action.getRepresented(2).size());
	}
}
