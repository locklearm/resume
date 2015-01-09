package edu.ncsu.csc.itrust.bean;

import edu.ncsu.csc.itrust.beans.HCPVisitBean;
import junit.framework.TestCase;

public class HCPVisitBeanTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testHashCode() {
		HCPVisitBean a = new HCPVisitBean();
		a.setDesignated(true);
		a.setHCPAddr("abc");
		a.setHCPMID(1);
		a.setHCPName("My HCP");
		a.setHCPSpecialty("Tree Surgeon");
		a.setOVDate("The day before tomorrow.");
		
		assertEquals(a.hashCode(), a.hashCode());

		a.setDesignated(false);
		
		assertEquals(a.hashCode(), a.hashCode());
	}

	public void testEqualsObject() {
		HCPVisitBean a = new HCPVisitBean();
		a.setDesignated(true);
		a.setHCPAddr("abc");
		a.setHCPMID(1);
		a.setHCPName("My HCP");
		a.setHCPSpecialty("Tree Surgeon");
		a.setOVDate("The day before tomorrow.");
		
		assertTrue(a.equals(a));
		
		HCPVisitBean b = new HCPVisitBean();
		b.setDesignated(true);
		b.setHCPAddr("abc");
		b.setHCPMID(1);
		b.setHCPName("My HCP");
		b.setHCPSpecialty("Tree Surgeon");
		b.setOVDate("The day before tomorrow.");
		
		assertTrue(b.equals(a));
		
		// The order of the following statements is important.
		b.setOVDate("The day after yesterday");
		assertFalse(b.equals(a));
		
		b.setHCPSpecialty("Love Doctor");
		assertFalse(b.equals(a));
		
		b.setHCPName("The best HCP");
		assertFalse(b.equals(a));
		
		b.setHCPAddr("Elm Street");
		assertFalse(b.equals(a));
		
		assertFalse(b.equals("string"));
		
		assertFalse(b.equals(null));
		
	}
	
	public void testGettersSetters() {
		HCPVisitBean a = new HCPVisitBean();
		a.setDesignated(true);
		a.setHCPAddr("abc");
		a.setHCPMID(1);
		a.setHCPName("My HCP");
		a.setHCPSpecialty("Tree Surgeon");
		a.setOVDate("The day before tomorrow.");
		
		assertTrue(a.equals(a));
		
		HCPVisitBean b = new HCPVisitBean();
		b.setDesignated(true);
		b.setHCPAddr("abc");
		b.setHCPMID(1);
		b.setHCPName("My HCP");
		b.setHCPSpecialty("Tree Surgeon");
		b.setOVDate("The day before tomorrow.");
		
		assertTrue(b.equals(a));
		
		//test some getters
		assertEquals(a.getHCPMID(), b.getHCPMID());
		assertEquals(a.getHCPName(), b.getHCPName());
		assertEquals(a.getHCPSpecialty(), b.getHCPSpecialty());
		assertEquals(a.getHCPAddr(), b.getHCPAddr());
		assertEquals(a.getOVDate(), b.getOVDate());
		assertEquals(a.isDesignated(), b.isDesignated());
	}

}
