package edu.ncsu.csc.itrust.charts;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jfree.data.category.DefaultCategoryDataset;
import de.laures.cewolf.DatasetProduceException;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.AdverseEventBean;
import edu.ncsu.csc.itrust.charts.AdverseEventsData;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;

public class AdverseEventsDataTest extends TestCase {
	private AdverseEventsData chart;
	
	@Override
	protected void setUp() throws Exception {
		TestDataGenerator gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();

		this.chart = new AdverseEventsData();
	}

	public void testProductDataset()
	{
		String codeName = "Testing";
		List<AdverseEventBean> adEvents = new LinkedList<AdverseEventBean>();
		AdverseEventBean event = new AdverseEventBean();
		event.setCode("12345");
		event.setDescription("Testing");
		event.setStatus("Active");
		event.setDate("2010-08-15 08:47:00");
		adEvents.add(event);
		Map<String, String> params = new HashMap<String, String>();
		try {
			chart.setAdverseEventsList(adEvents, codeName);
			DefaultCategoryDataset data = (DefaultCategoryDataset)chart.produceDataset(params);
			assertEquals(1.0, data.getValue(codeName, "Aug"));
			assertEquals("AdverseEventsData DatasetProducer", chart.getProducerId());
		} catch (DatasetProduceException e) {
			
			fail();
		}
	}

	//Added by melockle
	public void testHasExpired() {
		
		//Get the current time less 6000, so that it is already expired
		Date since = new Date(System.currentTimeMillis() - 6000);
		//Create a crappy hash map to complete the worthless parameter
		Map myHashMap = new HashMap();
		//Assert true with function call
		assertTrue(this.chart.hasExpired(myHashMap, since));
		
		
	}
	
	//Added by melockle
	public void testGenerateLink() {
		
		//Create a generic object
		Object myObj = new Object();
		
		//Test the function, expecting "Jan" as the string
		assertEquals("Jan", this.chart.generateLink(myObj, 0, myObj));
		
	}
	
	//Added by melockle
	public void testGenerateToolTip() {
		
		//Test the function, expecting "Jan" as the string
		assertEquals("Jan", this.chart.generateToolTip(null, 0, 0));
		
	}
	
}
