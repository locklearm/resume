package edu.ncsu.csc.itrust.http;

import org.apache.commons.codec.digest.DigestUtils;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import edu.ncsu.csc.itrust.enums.TransactionType;

public class SecurePasswordTest extends iTrustHTTPTest {

	protected void setUp() throws Exception {
		super.setUp();
		gen.clearAllTables();
		gen.standardData();
	}
	
	public void testLoginHash() throws Exception {
		// Kelly Doctor logs into iTrust
		WebConversation wc = login("9000000000", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - HCP Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		
		// Ensure password 'pw' is hashed correctly and stored in the database
		wr = wr.getLinkWith("Display Database").click();
		assertEquals(DigestUtils.sha256Hex("pw" + DigestUtils.sha256Hex("" + 9000000000L)), wr.getTableWithID("users").getCellAsText(1, 1));
	}
	
	public void testResetPasswordHash() throws Exception {
		// Click 'Reset Password' at the login screen
		WebConversation wc = new WebConversation();
		WebResponse wr = wc.getResponse(ADDRESS);
		wr = wr.getLinkWith("Reset Password").click();
		
		// Enter '1' in the MID field for Random Person
		wr.getForms()[1].setParameter("mid", "1");
		wr = wr.getForms()[1].submit();
		
		// Answer security question and set new password
		wr.getForms()[1].setParameter("answer", "blue");
		wr.getForms()[1].setParameter("password", "newPw12345");
		wr.getForms()[1].setParameter("confirmPassword", "newPw12345");
		wr = wr.getForms()[1].submit();
		assertLogged(TransactionType.PASSWORD_RESET, 1L, 1L, "");
		
		// Ensure that the password has been hashed
		wr = wr.getLinkWith("Display Database").click();
		assertEquals("f9c06f200a8d4332731a96fd945acd457a96784f0b1a87189bedf98f691511de", wr.getTableWithID("users").getCellAsText(9, 1));
	}

}
