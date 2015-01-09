package edu.ncsu.csc.itrust.http;

import static org.junit.Assert.*;
import org.junit.Test;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

public class UC47_Finding_an_Expert{ 
	public static final String ADDRESS = "http://localhost:8080/iTrust";
	@Test
	public void test() throws Exception {
		WebConversation webConversation = new WebConversation();
		WebResponse menuResponse = webConversation.getResponse(ADDRESS);
		WebResponse login = menuResponse.getLinkWith("Patient 2").click();
		WebResponse findExpert = login.getLinkWith("Find an Expert").click();
		
		
	}

}
