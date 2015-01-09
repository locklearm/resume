package edu.ncsu.csc.itrust.http;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import edu.ncsu.csc.itrust.enums.TransactionType;
import java.io.File;

/**
 * Use Case 52
 */
public class UpdatePercentilesTest extends iTrustHTTPTest {
	
	protected void setUp() throws Exception{
		super.setUp();
		gen.clearAllTables();
		gen.standardData();
	}
	
	public void testAdminAddsCSVFileToDatabase() throws Exception
	{
		WebConversation wc = login("9000000001", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/admin/home.jsp", wr.getURL().toString());
		wr = wr.getLinkWith("Update Percentile Data").click();
		WebForm adminForm = wr.getFormWithName("updatePercentilesDataForm");
		adminForm.setParameter("updatePercentilesDataFormPercentileType", "BMIForAge");
		adminForm.setParameter("csvFile", new File("testing-files/sample_percentiles/bmiagerev.csv"));
		adminForm.getButtonWithID("updatePercentilesDataFormSubmit").click();
		assertLogged(TransactionType.ADMIN_UPDATE_PERCENTILE_DATA,9000000001L,0L,"");
	}
	
	public void testAdminAddsNonCSVFileToDatabase() throws Exception
	{
		WebConversation wc = login("9000000001", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/admin/home.jsp", wr.getURL().toString());
		wr = wr.getLinkWith("Update Percentile Data").click();
		WebForm adminForm = wr.getFormWithName("updatePercentilesDataForm");
		adminForm.setParameter("updatePercentilesDataFormPercentileType", "BMIForAge");
		adminForm.setParameter("csvFile", new File("testing-files/sample_patientupload/HCPPatientUploadBinaryData.doc"));
		adminForm.getButtonWithID("updatePercentilesDataFormSubmit").click();
		assertNotLogged(TransactionType.ADMIN_UPDATE_PERCENTILE_DATA,9000000001L,0L,"");
	}
	
	public void testAdminAddsSingleCSVLineToDatabase() throws Exception
	{
		WebConversation wc = login("9000000001", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "auth/admin/home.jsp", wr.getURL().toString());
		wr = wr.getLinkWith("Update Percentile Data").click();
		WebForm adminForm = wr.getFormWithName("updatePercentilesDataForm");
		adminForm.setParameter("updatePercentilesDataFormPercentileType", "BMIForAge");
		adminForm.setParameter("csvFile", new File("testing-files/sample_percentiles/singleLine.csv"));
		adminForm.getButtonWithID("updatePercentilesDataFormSubmit").click();
		assertLogged(TransactionType.ADMIN_UPDATE_PERCENTILE_DATA,9000000001L,0L,"");
	}
}