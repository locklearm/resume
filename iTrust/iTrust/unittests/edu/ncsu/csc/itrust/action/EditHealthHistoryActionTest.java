package edu.ncsu.csc.itrust.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.HealthRecord;
import edu.ncsu.csc.itrust.beans.forms.HealthRecordForm;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.iTrustException;
import edu.ncsu.csc.itrust.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class EditHealthHistoryActionTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private TestDataGenerator gen;
	private EditHealthHistoryAction action;

	@Override
	protected void setUp() throws Exception {
		gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.patient1();
		gen.patient2();
		action = new EditHealthHistoryAction(factory, 9000000000L, "2");
	}

	public void testAddToEmptyRecordSimple() throws Exception {
		assertEquals(1, action.getAllHealthRecords(1L).size());
		HealthRecordForm hr = new HealthRecordForm();
		hr.setBloodPressureN("999");
		hr.setBloodPressureD("999");
		hr.setCholesterolHDL("50");
		hr.setCholesterolLDL("50");
		hr.setCholesterolTri("499");
		hr.setHeight("65.2");
		hr.setWeight("9999.9");
		hr.setHeadCircumference("20.1");
		hr.setIsSmoker("1");
		String confirm = action.addHealthRecord(1L, hr);
		assertEquals("Information Recorded", confirm);
		List<HealthRecord> records = action.getAllHealthRecords(1L);
		assertEquals(2, records.size());
		// Note that we don't need to check the returned values here - see the DAO test, AddPHRTest
	}

	public void testTotalCholesterol() throws Exception {
		HealthRecordForm hr = new HealthRecordForm();
		hr.setBloodPressureN("999");
		hr.setBloodPressureD("999");
		hr.setCholesterolHDL("50");
		hr.setCholesterolLDL("50");
		hr.setCholesterolTri("500");
		hr.setHeight("65.2");
		hr.setWeight("9999.9");
		hr.setHeadCircumference("20.1");
		action.addHealthRecord(2L, hr);
		hr.setCholesterolTri("501");

		String response = action.addHealthRecord(2L, hr);
		assertEquals("This form has not been validated correctly. The following field are not properly filled in: [Total cholesterol must be in [100,600]]", response);
	}

	public void testPatientNameNull() throws Exception {
		assertNotNull(action.getPatientName());
	}
	
	public void testGetPatientDOB() throws Exception {
		assertNotNull(action.getPatientDOB());
	}
	
	public void testRemoveHealthRecord() throws Exception {
		action.removeHealthRecord(2l);
		assertTrue(!action.getAllHealthRecords(2l).isEmpty());
	}
	
	public void testGetHealthRecordsByOfficeVisit() throws Exception {
		assertTrue(!action.getHealthRecordsByOfficeVisit(0l, 2l).isEmpty());
	}

	public void testAddHealthRecordEvil() throws Exception {
		//Correct form data
		HealthRecordForm hr = new HealthRecordForm();
		hr.setBloodPressureN("999");
		hr.setBloodPressureD("999");
		hr.setCholesterolHDL("50");
		hr.setCholesterolLDL("50");
		hr.setCholesterolTri("499");
		hr.setHeight("65.2");
		hr.setWeight("9999.9");
		hr.setHeadCircumference("20.1");
		hr.setIsSmoker("1");
		try {
			action = new EditHealthHistoryAction(new EvilDAOFactory(1), 9000000000L, "2");
			action.addHealthRecord(1l, hr);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	public void testGetOfficeVisitDateStr() throws Exception
	{
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date today = new Date();
		assertEquals(action.getOfficeVisitDateStr(11), df.format(today));
		
	}
	
	public void testGetPercentile() throws Exception
	{
		gen.percentiledata();
		assertEquals(action.getPercentile(4, 1, 24.5, 16.1),63.752);
	}
	
	public void testGetBMIStrChild() throws Exception
	{
		gen.percentiledata();
		assertEquals(action.getBMICategoryString(1, 24.5, 16.1),"Normal");
		assertEquals(action.getBMICategoryString(1, 24.5, 5.1),"Obese");
		assertEquals(action.getBMICategoryString(1, 24.5, 30.1),"Underweight");
		assertEquals(action.getBMICategoryString(1, 24.5, 15.1),"Overweight");
	}
	
	public void testGetBMIStrAdult() throws Exception
	{
		gen.percentiledata();
		assertEquals(action.getBMICategoryString(1, 241.5, 20.1),"Normal");
		assertEquals(action.getBMICategoryString(1, 241.5, 30.1),"Obese");
		assertEquals(action.getBMICategoryString(1, 241.5, 5.1),"Underweight");
		assertEquals(action.getBMICategoryString(1, 241.5, 25.1),"Overweight");
		
	}
}
