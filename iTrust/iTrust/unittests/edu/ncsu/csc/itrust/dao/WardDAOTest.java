package edu.ncsu.csc.itrust.dao;

import static org.easymock.classextension.EasyMock.createControl;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.dao.mysql.WardDAO;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.iTrustException;
import edu.ncsu.csc.itrust.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;
import edu.ncsu.csc.itrust.beans.HospitalBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.beans.WardBean;
import edu.ncsu.csc.itrust.beans.WardRoomBean;
import java.sql.SQLException;
import java.util.ArrayList;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import java.util.List;
import org.easymock.classextension.IMocksControl;

public class WardDAOTest extends TestCase{
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private DAOFactory factory2; 
	
	WardDAO wd1;
	WardDAO wd2;
	
	private IMocksControl ctrl;
	
	public void setUp(){
		ctrl = createControl();
		factory2 = ctrl.createMock(DAOFactory.class);
		
		wd1 = new WardDAO(factory);
		
		wd2 = new WardDAO(factory2);
	}
	
	public void testgetAllWardsByHospitalID(){
		List<WardBean> list = new ArrayList<WardBean>();
		
		try {
			list = wd1.getAllWardsByHospitalID("1");
			assertNotNull(list);
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.getAllWardsByHospitalID("1");
			fail();
		} catch (Exception e) {}
		  
	}
	
	public void testAddWard(){
		WardBean wb = new WardBean(0l, "name", 0l);
		
		try {
			assertTrue(wd1.addWard(wb));
		} catch (DBException e) {}  catch (iTrustException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.addWard(wb);
			fail();
		} catch (Exception e) {}
		
	}
	
	public void testUpdateWard(){
		WardBean wb = new WardBean(0l, "name", 0l);
		
		try {
			assertEquals(0,wd1.updateWard(wb));
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.updateWard(wb);
			fail();
		} catch (Exception e) {}
	}
	
	public void testRemoveWard(){
		
		try {
			wd1.removeWard(1l);
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.removeWard(1l);
			fail();
		} catch (Exception e) {}
		
	}
	
	public void testgetAllWardRoomsByWardID(){
		List<WardRoomBean> list = new ArrayList<WardRoomBean>();
		
		try {
			list = wd1.getAllWardRoomsByWardID(1l);
			assertNotNull(list);
		} catch (DBException e) {}
		  
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.getAllWardRoomsByWardID(1l);
			fail();
		} catch (Exception e) {}
	}
	
	public void testAddWardRoom(){
		WardRoomBean wb = new WardRoomBean(0, 0, 0, "name1", "status");
		
		try {
			assertTrue(wd1.addWardRoom(wb));
		} catch (DBException e) {}
		  catch (iTrustException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.addWardRoom(wb);
			fail();
		} catch (Exception e) {}
	}
	
	public void testUpdateWardRoom(){
		WardRoomBean wb = new WardRoomBean(0, 0, 0, "name1", "status");
		try {
			assertEquals(0,wd1.updateWardRoom(wb));
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.updateWardRoom(wb);
			fail();
		} catch (Exception e) {}
	}
	
	public void testRemoveWardRoom(){
		try {
			wd1.removeWardRoom(1l);
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.removeWardRoom(1l);
			fail();
		} catch (Exception e) {}
	}
	
	public void testgetAllWardsByHCP(){
		List<WardBean> list = new ArrayList<WardBean>();
		
		try {
			list = wd1.getAllWardsByHCP(1l);
			assertNotNull(list);
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.getAllWardsByHCP(1l);
			fail();
		} catch (Exception e) {}
		  
	}
	
	public void testgetAllHCPsAssignedToWard(){
		List<PersonnelBean> list = new ArrayList<PersonnelBean>();
		
		try {
			list = wd1.getAllHCPsAssignedToWard(1l);
			assertNotNull(list);
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.getAllHCPsAssignedToWard(1l);
			fail();
		} catch (Exception e) {}
		  
	}
	
	public void testAssignHCPToWard(){
		try {
			assertTrue(wd1.assignHCPToWard(1l,1l));
		} catch (DBException e) {} catch (iTrustException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.assignHCPToWard(1l,1l);
			fail();
		} catch (Exception e) {}
	}
	
	public void testRemoveWard2(){
		try {
			assertNotNull(wd1.removeWard(1l,1l));
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.removeWard(1l,1l);
			fail();
		} catch (Exception e) {}
	}
	
	public void testUpdateWardRoomOccupant(){
		
		WardRoomBean wrb = new WardRoomBean(0, 0, 0, "name", "status");
		
		try {
			assertEquals(0,wd1.updateWardRoomOccupant(wrb));
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.updateWardRoomOccupant(wrb);
			fail();
		} catch (Exception e) {}
	}
	
	public void testGetWardRoomsByStatus(){
		
		List<WardRoomBean> list = new ArrayList<WardRoomBean>();
		
		try {
			list = wd1.getWardRoomsByStatus("status",1l);
			assertNotNull(list);
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.getWardRoomsByStatus("status",1l);
			fail();
		} catch (Exception e) {}
	}
	
	public void testGetWardRoom(){
		WardRoomBean wrb = new WardRoomBean(0, 0, 0, "name", "status");
	
		try {
			wrb = wd1.getWardRoom("0");
			assertNull(wrb);
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.getWardRoom("0");
			fail();
		} catch (Exception e) {}
	}
	
	public void testGetHospitalByWard(){
		HospitalBean hb = new HospitalBean();
	
		try {
			hb = wd1.getHospitalByWard("name");
			assertNull(hb);
		} catch (DBException e) {}
		
		try {
			expect(factory2.getConnection()).andThrow(new SQLException());
			ctrl.replay();
			wd2.getHospitalByWard("name");
			fail();
		} catch (Exception e) {}
	}
	
	public void testGetWard() throws Exception {
		
		TestDataGenerator td = new TestDataGenerator();
		td.clearAllTables();
		td.insertwards();
		
		//Get and make sure is same
		WardBean returned = wd1.getWard("3");
		assertEquals(3l, returned.getWardID());
		
	}
	
	public void testGetWardNoSuchWardID() throws Exception {
		
		TestDataGenerator td = new TestDataGenerator();
		td.clearAllTables();
		td.insertwards();
		
		//Get and make sure is same
		WardBean returned = wd1.getWard("34");
		assertNull(returned);
		
	}
	
	public void testGetHospitalByPatientID() throws Exception {
		
		TestDataGenerator td = new TestDataGenerator();
		td.clearAllTables();
		td.standardData();
		
		//Get and make sure is same
		HospitalBean returned = wd1.getHospitalByPatientID(1l);
		assertEquals("Health Institute Dr. E", returned.getHospitalName());
		
		//Get bad id and make sure is null
		returned = wd1.getHospitalByPatientID(145l);
		assertNull(returned);
		
	}
	
	public void testGetWardBadDatabase() throws Exception {
		
		//Create everything new with bad factory
		DAOFactory evilDAOFactory = new EvilDAOFactory();
		WardDAO myWard = new WardDAO(evilDAOFactory);
		
		//Testing getWard
		try {
			myWard.getWard("0");
			fail("Expected DBException");
		} catch (DBException e) {
			assertTrue(true);
		}
		
		
	}
	
	public void testGetHospitalByPatientIDBadDatabase() throws Exception {
		
		//Create everything new with bad factory
		DAOFactory evilDAOFactory = new EvilDAOFactory();
		WardDAO myWard = new WardDAO(evilDAOFactory);
		
		//Testing getHospitalByPatientID
		try {
			myWard.getHospitalByPatientID(0l);
			fail("Expected DBException");
		} catch (DBException e) {
			assertTrue(true);
		}
		
		
	}
	
	public void testCheckOutPatientReasonBadDatabase() throws Exception {
		
		//Create everything new with bad factory
		DAOFactory evilDAOFactory = new EvilDAOFactory();
		WardDAO myWard = new WardDAO(evilDAOFactory);
		
		//Testing checkOutPatientReason
		try {
			myWard.checkOutPatientReason(0l, "");
			fail("Expected DBException");
		} catch (DBException e) {
			assertTrue(true);
		}
		
		
	}
	
	public void testCheckOutPatientReason() throws Exception {
		
		//Testing checkOutPatientReason
		assertTrue(wd1.checkOutPatientReason(0l, "A good reason."));
		
		
	}
}
