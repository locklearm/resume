package edu.ncsu.csc.itrust.action;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class ChangePasswordActionTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private TestDataGenerator gen;
	private ChangePasswordAction action;
	private AuthDAO authDAO;
	
	@Override
	protected void setUp() throws Exception {
		gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();
		action = new ChangePasswordAction(factory, 1);
		this.authDAO = new AuthDAO(factory);
		
	}
	
	
	
	public void testCheckPassword() throws Exception{
		assertTrue(action.checkPassword("pw"));
		assertTrue(action.validatePassword("newpass1"));
		assertFalse(action.validatePassword("pw"));
		assertTrue(action.validatePassword("ABCDEFGHIJKLMNOPQRSTUVWXWZabcdefghijklmnopqrstuvwxyz0123456789"));
		assertFalse(action.validatePassword("$/*-+"));
		action.changePassword("newpass1");
		assertEquals("58d1e9f0c95b0b4b222427a2a801e9a35f2b775dd89c456f5bc2730abb3c6daf", authDAO.getPassword(1));
		
		
		//assertTrue(action.validatePassword("a9monney"));
		//assertFalse(action.changePassword("newPass1"));
	}
}
