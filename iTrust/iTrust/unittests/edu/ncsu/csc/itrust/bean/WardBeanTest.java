package edu.ncsu.csc.itrust.bean;

import edu.ncsu.csc.itrust.beans.WardBean;
import junit.framework.TestCase;

public class WardBeanTest extends TestCase {
	
	WardBean testBean;
	WardBean testBean2;
	WardBean testBean3;
	
	
	public void setUp(){
		testBean = new WardBean(0, "", 0);
		testBean2 = new WardBean(0, "", 0);
		testBean3 = new WardBean(0, "", 0);
	}
	
	public void testWardID(){
		testBean.setWardID(1);
		
		assertEquals(testBean.getWardID(),1l);
	}
	
	public void testSpecialty(){
		testBean.setRequiredSpecialty("special");
		
		assertEquals(testBean.getRequiredSpecialty(),"special");
	}
	
	public void testInHospital(){
		testBean.setInHospital(1);
		
		assertEquals(testBean.getInHospital(),1l);
	}
	
	public void testEquals(){
		assertTrue(testBean2.equals(testBean3));
	}

}
