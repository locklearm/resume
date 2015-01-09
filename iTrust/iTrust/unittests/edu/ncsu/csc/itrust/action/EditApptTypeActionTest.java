package edu.ncsu.csc.itrust.action;

import java.sql.SQLException;
import edu.ncsu.csc.itrust.beans.ApptTypeBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;
import junit.framework.TestCase;

public class EditApptTypeActionTest extends TestCase {

	private EditApptTypeAction action;
	private DAOFactory factory;
	private long adminId = 9000000001L;
	
	@Override
	protected void setUp() throws Exception {
		TestDataGenerator gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();
		
		this.factory = TestDAOFactory.getTestInstance();
		this.action = new EditApptTypeAction(this.factory, this.adminId);
	}
	
	//Added by melockle
	//Seems like this isn't hitting the sections I want it to.  Don't know why. - melockle
	public void testEvilFactory() throws Exception {
		this.action = new EditApptTypeAction(EvilDAOFactory.getEvilInstance(), this.adminId);
		
		ApptTypeBean a = new ApptTypeBean();
		a.setName("Test");
		a.setDuration(30);
		
		try {
			this.action.addApptType(a);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		try {
			this.action.editApptType(a);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
	}
	
	//added by melockle
	public void testEditApptTypeNoSuchAppt() throws Exception {
		ApptTypeBean a = new ApptTypeBean();
		a.setName("I doubt this exists");
		a.setDuration(30);
		
		assertEquals("Appointment Type: I doubt this exists you are trying to update does not exist.", action.editApptType(a));
		
	}
	
	public void testGetApptTypes() throws SQLException {
		assertEquals(6, action.getApptTypes().size());
	}
	
	public void testAddApptType() throws SQLException, FormValidationException {
		ApptTypeBean a = new ApptTypeBean();
		a.setName("Test");
		a.setDuration(30);
		
		assertTrue(action.addApptType(a).startsWith("Success"));
		assertEquals(7, action.getApptTypes().size());
	}
	
	public void testAddApptType2() throws SQLException, FormValidationException {
		ApptTypeBean a = new ApptTypeBean();
		a.setName("General Checkup");
		a.setDuration(30);
		
		assertTrue(action.addApptType(a).equals("Appointment Type: General Checkup already exists."));
	}
	
	public void testEditApptType() throws SQLException, FormValidationException {
		ApptTypeBean a = new ApptTypeBean();
		a.setName("General Checkup");
		a.setDuration(30);
		
		assertTrue(action.editApptType(a).startsWith("Success"));
	}
	
	public void testEditApptType2() throws SQLException, FormValidationException {
		ApptTypeBean a = new ApptTypeBean("General Checkup", 45);
		
		assertEquals("Appointment Type: General Checkup already has a duration of 45 minutes.", action.editApptType(a));
	}
	
	public void testAddApptTypeLengthZero() throws SQLException{
		ApptTypeBean a = new ApptTypeBean();
		a.setName("Test");
		a.setDuration(0);
		
		try {
			action.addApptType(a);
		} catch (FormValidationException e) {
			//Exception is good.
			return;
		}
		assertTrue(false);
	}
}
