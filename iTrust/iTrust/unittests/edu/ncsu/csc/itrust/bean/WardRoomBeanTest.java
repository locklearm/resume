package edu.ncsu.csc.itrust.bean;

import edu.ncsu.csc.itrust.beans.WardRoomBean;
import junit.framework.TestCase;

public class WardRoomBeanTest extends TestCase {
	
	WardRoomBean wrb1;
	WardRoomBean wrb2;
	WardRoomBean wrb3;
	
	public void setUp(){
		wrb1 = new WardRoomBean(0l, 0l, 0l, "","");
		wrb2 = new WardRoomBean(0l, 0l, 0l, "","");
		wrb3 = new WardRoomBean(0l, 0l, 0l, "","");
	}
	
	public void testRoomID(){
		wrb1.setRoomID(1l);
		assertEquals(1l,wrb1.getRoomID());
	}
	
	public void testOccupiedBy(){
		wrb1.setOccupiedBy(1l);
		assertTrue(1L == wrb1.getOccupiedBy());
	}
	
	public void testInWard(){
		wrb1.setInWard(1l);
		assertTrue(1L == wrb1.getInWard());
	}
	
	public void testRoomName(){
		wrb1.setRoomName("name");
		assertTrue(wrb1.getRoomName().equals("name"));
	}
	
	public void testRoomStatus(){
		wrb1.setStatus("name");
		assertTrue(wrb1.getStatus().equals("name"));
	}
	
	public void testEquals(){
		assertTrue(wrb2.equals(wrb3));
	}

}
