package edu.ncsu.csc.itrust.dao.mysql;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class PercentilesDataDAOTest {

	private PercentilesDataDAO testDAO;
	private PercentilesDataDAO badDAO;
	
	@Before
	public void setUp() throws Exception {
		TestDataGenerator gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();
		gen.percentiledata();
		
		DAOFactory factory = TestDAOFactory.getTestInstance();
//		DAOFactory factory = DAOFactory.getProductionInstance();
		testDAO = new PercentilesDataDAO(factory);
		
		DAOFactory evilFactory = EvilDAOFactory.getEvilInstance();
		badDAO = new PercentilesDataDAO(evilFactory);
	}
	
	@Test
	public void testExceptionAddPercentileDataRow() {
		// Parameters don't matter, we want the Exception
		assertFalse (badDAO.addPercentileDataRow(1, 2, 37.0, 5, 6, 7));
	}
	
	@Test
	public void testBadGetPercentileLMSValues() {
		double[] result = badDAO.getPercentileLMSValues(6, 6, 12.4);
		double[] expected = {0.0, 0.0, 0.0};
		assertArrayEquals(expected, result, 0.1);
	}
	
	@Test
	public void testGetPercentileLMSValues() {
		double[] result = testDAO.getPercentileLMSValues(4, 1, 12);
		assertEquals (3, result.length);
	}
}
