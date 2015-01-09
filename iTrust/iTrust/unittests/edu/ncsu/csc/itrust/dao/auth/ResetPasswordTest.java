package edu.ncsu.csc.itrust.dao.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.codec.digest.DigestUtils;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.iTrustException;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class ResetPasswordTest extends TestCase {
	private DAOFactory factory = TestDAOFactory.getTestInstance();
	AuthDAO authDAO = factory.getAuthDAO();
	private TestDataGenerator gen = new TestDataGenerator();

	@Override
	protected void setUp() throws Exception {
		gen.clearAllTables();
		gen.patient2();
	}

	public void testResetPassword() throws Exception {
		assertEquals(DigestUtils.sha256Hex("pw" + DigestUtils.sha256Hex("" + 2L)), getPassword(2L));
		authDAO.resetPassword(2L, "password");
		assertEquals(DigestUtils.sha256Hex("password" + DigestUtils.sha256Hex("" + 2L)), getPassword(2L));
	}

	public void testResetPasswordNonExistent() throws Exception {
		// Still runs with no exception - that's the expected behavior
		authDAO.resetPassword(500L, "password");
	}

	private String getPassword(long mid) throws SQLException {
		Connection conn = null; 
		PreparedStatement ps = null;
		try {
			conn = factory.getConnection();
			ps = conn.prepareStatement("SELECT password from users WHERE MID=" + mid);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getString("password");
		} finally {
			DBUtil.closeConnection(conn,ps);
		}
	}

	public void testResetSecurityQuestionAnswer() throws DBException {
		authDAO.setSecurityQuestionAnswer("how you doin?", "good", 2l);
	}

	public void testGetNoSecurityAnswer() throws Exception {
		long mid = factory.getPatientDAO().addEmptyPatient();
		try {
			authDAO.getSecurityAnswer(mid);
			fail("exception should have been thrown");
		} catch (iTrustException e) {
			assertEquals("No security answer set for MID " + mid, e.getMessage());
		}
	}
	
	public void testGetNoSecurityQuestion() throws Exception {
		long mid = factory.getPatientDAO().addEmptyPatient();
		try {
			authDAO.getSecurityQuestion(mid);
			fail("exception should have been thrown");
		} catch (iTrustException e) {
			assertEquals("No security question set for MID: " + mid, e.getMessage());
		}
	}
}
