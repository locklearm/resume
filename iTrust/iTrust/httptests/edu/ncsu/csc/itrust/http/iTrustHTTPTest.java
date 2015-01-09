package edu.ncsu.csc.itrust.http;

import java.net.ConnectException;
import java.util.List;
import junit.framework.TestCase;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import edu.ncsu.csc.itrust.beans.TransactionBean;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.enums.TransactionType;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

/**
 * There's nothing special about this class other than adding a few handy test utility methods and
 * variables. When extending this class, be sure to invoke super.setUp() first.
 * 
 */
abstract public class iTrustHTTPTest extends TestCase {
	/*
	 * The URL for iTrust, change as needed
	 */
	public static final String ADDRESS = "http://localhost:8080/iTrust/";
	protected TestDataGenerator gen = new TestDataGenerator();

	@Override
	protected void setUp() throws Exception {
		HttpUnitOptions.clearScriptErrorMessages();
		HttpUnitOptions.setScriptingEnabled(true);
		HttpUnitOptions.setExceptionsThrownOnScriptError(false);
		gen.clearAllTables();
	}

	/**
	 * Helper method for logging in to iTrust
	 * @param username
	 * @param password
	 * @return {@link WebConversation}
	 * @throws Exception
	 */
	public WebConversation login(String username, String password) throws Exception {
		try {
			// begin at the iTrust home page
			WebConversation wc = new WebConversation();
			WebResponse loginResponse = wc.getResponse(ADDRESS);
			// log in using the given username and password
			WebForm form = loginResponse.getForms()[0];
			form.setParameter("j_username", username);
			form.setParameter("j_password", password);
			WebResponse homePage = loginResponse.getForms()[0].submit();
			if (homePage.getTitle().equals("iTrust Login")) {
				throw new IllegalArgumentException("Error logging in, user not in database?");
			}
			assertLogged(TransactionType.LOGIN_SUCCESS, Long.parseLong(username), Long.parseLong(username),"");
			return wc;
		} catch (ConnectException e) {
			throw new ConnectException("Tomcat must be running to run HTTP tests.");
		}
	}
	
	public static void assertLogged(TransactionType code, long loggedInMID,
			long secondaryMID, String addedInfo)
			throws DBException {
		List<TransactionBean> transList = TestDAOFactory.getTestInstance().getTransactionDAO().getAllTransactions();
		for (TransactionBean t : transList)
		{	
			if( (t.getTransactionType() == code) &&
				(t.getLoggedInMID() == loggedInMID) &&
				(t.getSecondaryMID() == secondaryMID))
				{
					assertTrue(t.getTransactionType() == code);
					if(!t.getAddedInfo().trim().contains(addedInfo.trim()))
					{
						fail("Additional Information is not logged correctly.");
					}
					return;
				}
		}
		fail("Event not logged as specified.");
	}

	public static void assertNotLogged(TransactionType code, long loggedInMID,
			long secondaryMID, String addedInfo)
			throws DBException {
		List<TransactionBean> transList = TestDAOFactory.getTestInstance().getTransactionDAO().getAllTransactions();
		for (TransactionBean t : transList)
		{	
			if( (t.getTransactionType() == code) &&
				(t.getLoggedInMID() == loggedInMID) &&
				(t.getSecondaryMID() == secondaryMID) &&
				(t.getAddedInfo().trim().contains(addedInfo)) )
				{
					fail("Event was logged, but should NOT have been logged");
					return;
				}
		}
	}
}
