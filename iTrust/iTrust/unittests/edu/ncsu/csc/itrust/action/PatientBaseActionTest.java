package edu.ncsu.csc.itrust.action;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.action.base.PatientBaseAction;
import edu.ncsu.csc.itrust.exception.iTrustException;
import edu.ncsu.csc.itrust.testutils.EvilDAOFactory;

public class PatientBaseActionTest extends TestCase {

	public void testEvilDatabase() {
		try {
			new PatientBaseAction(EvilDAOFactory.getEvilInstance(), "2222");
			fail("exception should have been thrown");
		} catch (iTrustException e) {
			assertEquals(
					"A database exception has occurred. Please see the log in the console for stacktrace", e
							.getMessage());
		}
	}
}
