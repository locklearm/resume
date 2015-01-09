package edu.ncsu.csc.itrust.action;

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

public class UpdatePercentileDataActionTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private TestDataGenerator gen;
	private UpdatePercentileDataAction action;

	@Override
	protected void setUp() throws Exception {
		gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.patient1();
		gen.patient2();
		action = new UpdatePercentileDataAction(factory);
	}
	
	public void testAddPercentileDataBMI() throws Exception
	{
		gen.clearAllTables();
		String a ="BMIForAge";
		String percentilesData = "1,2,3.0,4.0,5.0";
				
		assertEquals("",action.addPercentilesData(a, percentilesData));
	}

	public void testAddPercentileDataHC() throws Exception
	{
		gen.clearAllTables();
		String a ="headCircumferenceForAge";
		String percentilesData = "1,2,3,4,5,5,6,7,8";
				
		assertEquals("",action.addPercentilesData(a, percentilesData));
	}
	
	public void testAddPercentileDataHA() throws Exception
	{
		gen.clearAllTables();
		String a ="lengthForAgeAdult";
		String percentilesData = "1,2,3,4,5,5,6,7,8";
				
		assertEquals("",action.addPercentilesData(a, percentilesData));
	}
	
	public void testAddPercentileDataHI() throws Exception
	{
		gen.clearAllTables();
		String a ="lengthForAgeInfant";
		String percentilesData = "1,2,3,4,5,5,6,7,8";
				
		assertEquals("",action.addPercentilesData(a, percentilesData));
	}
	
	public void testAddPercentileWA() throws Exception
	{
		gen.clearAllTables();
		String a ="weightForAgeAdult";
		String percentilesData = "1,2,3,4,5,5,6,7,8";
				
		assertEquals("",action.addPercentilesData(a, percentilesData));
	}
	
	public void testAddPercentileWI() throws Exception
	{
		gen.clearAllTables();
		String a ="weightForAgeInfant";
		String percentilesData = "1,2,3,4,5,5,6,7,8\n"+"1,2,3,4,5,6,7";
				
		assertEquals("",action.addPercentilesData(a, percentilesData));
		assertEquals("",action.addPercentilesData(a, percentilesData));
	}
	
	public void testGetPercentile() throws Exception
	{
		gen.clearAllTables();
		gen.percentiledata();
		assertEquals(0.049347784010972395,action.getPercentile(4, 1, 24, 24));
		assertEquals(99.99999903403194,action.getPercentile(4, 1, 24, 12));
		
	}
	

}
