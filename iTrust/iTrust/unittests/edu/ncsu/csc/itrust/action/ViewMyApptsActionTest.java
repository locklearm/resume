package edu.ncsu.csc.itrust.action;

import java.sql.SQLException;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.exception.iTrustException;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;
import junit.framework.TestCase;

public class ViewMyApptsActionTest extends TestCase {
	private ViewMyApptsAction action;
	private DAOFactory factory;
	private long mid = 1L;
	private long hcpId = 9000000000L;
	
	@Override
	protected void setUp() throws Exception {
		TestDataGenerator gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();
		
		this.factory = TestDAOFactory.getTestInstance();
		this.action = new ViewMyApptsAction(this.factory, this.hcpId);
	}
	
	public void testGetMyAppointments() throws SQLException {
		assertEquals(15, action.getAllMyAppointments().size());
	}
	
	
	public void testGetName() throws iTrustException {
		assertEquals("Kelly Doctor", action.getName(hcpId));
	}
	
	public void testGetName2() throws iTrustException {
		assertEquals("Random Person", action.getName(mid));
	}
}
