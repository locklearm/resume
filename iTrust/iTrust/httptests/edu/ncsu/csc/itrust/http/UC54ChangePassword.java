package edu.ncsu.csc.itrust.http;

import com.meterware.httpunit.TableRow;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import edu.ncsu.csc.itrust.enums.TransactionType;

public class UC54ChangePassword extends iTrustHTTPTest {

	@Override
	protected void setUp() throws Exception {
			super.setUp();
			gen.clearAllTables();
			gen.standardData();
		}
	

	public void testPatientChangePassword() throws Exception{
			//login as pateint 1
		WebConversation wc = login("1", "pw");
		WebResponse wr = wc.getCurrentPage();
		assertEquals("iTrust - Patient Home", wr.getTitle());
		assertLogged(TransactionType.HOME_VIEW, 1L, 0L, "");
		
		//change page
		wr.getLinkWith("Change Password").click();
		wr = wc.getCurrentPage();
		assertEquals(ADDRESS + "changePassword.jsp",wr.getURL().toString());
		//change password
		WebForm passwordForm = wr.getFormWithID("changePasswordForm");
		passwordForm.setParameter("old_pass", "pw");
		passwordForm.setParameter("new_pass", "newpass1");
		passwordForm.getButtons()[0].click();
		
		//Assert statements for checking password.
		wr = wc.getCurrentPage();
		assertTrue( wr.getText().contains("Password Change Successful"));
		wr.getLinkWith("Display Database").click();
		wr = wc.getCurrentPage();
		//Check Database for updated Password Hash
		assertEquals("58d1e9f0c95b0b4b222427a2a801e9a35f2b775dd89c456f5bc2730abb3c6daf", wr.getTableWithID("users").getCellAsText(9, 1));
	}
	public void testPatientChangeBadNewPassword() throws Exception{
		//login as pateint 1
	WebConversation wc = login("1", "pw");
	WebResponse wr = wc.getCurrentPage();
	assertEquals("iTrust - Patient Home", wr.getTitle());
	assertLogged(TransactionType.HOME_VIEW, 1L, 0L, "");
	
	//change page
	wr.getLinkWith("Change Password").click();
	wr = wc.getCurrentPage();
	assertEquals(ADDRESS + "changePassword.jsp",wr.getURL().toString());
	//change password
	WebForm passwordForm = wr.getFormWithID("changePasswordForm");
	passwordForm.setParameter("old_pass", "pw");
	passwordForm.setParameter("new_pass", "newpass");
	passwordForm.getButtons()[0].click();
	
	//Assert statements for checking password.
	wr = wc.getCurrentPage();
	assertTrue( wr.getText().contains("Invalid New Password"));
	wr.getLinkWith("Display Database").click();
	wr = wc.getCurrentPage();
	//Check Database did not update
	assertEquals("2d5cb7a32f2e7c6057485de6106f3844a8bc393dfd984252d4c7dfb43fd37957", wr.getTableWithID("users").getCellAsText(9, 1));
}
	public void testHCPChangeBadCurrentPassword() throws Exception{
		//login as pateint 1
	WebConversation wc = login("9000000000", "pw");
	WebResponse wr = wc.getCurrentPage();
	assertEquals("iTrust - HCP Home", wr.getTitle());
	assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
	
	//change page
	wr.getLinkWith("Change Password").click();
	wr = wc.getCurrentPage();
	assertEquals(ADDRESS + "changePassword.jsp",wr.getURL().toString());
	//change password
	WebForm passwordForm = wr.getFormWithID("changePasswordForm");
	passwordForm.setParameter("old_pass", "pw1234");
	passwordForm.setParameter("new_pass", "newpass1");
	passwordForm.getButtons()[0].click();
	
	//Assert statements for checking password.
	wr = wc.getCurrentPage();
	assertTrue(wr.getText().contains("Current Password Entered Is Not Correct"));
	wr.getLinkWith("Display Database").click();
	wr = wc.getCurrentPage();
	//Check Database did not change
	assertEquals("5f7174ed8dd9150ebbdcebc293d82f0dd2dba6d5da1041b4c7badbd601fa1913", wr.getTableWithID("users").getCellAsText(1, 1));
}
}
