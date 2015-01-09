package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import org.apache.commons.codec.digest.DigestUtils;
import edu.ncsu.csc.itrust.exception.iTrustException;

/**
 * Used for Change Password Page (changePassword.jsp). This takes in a patients mid and new password and updates
 * the database for the new password.
 * 
 * 
 */
public class ChangePasswordAction {
	private AuthDAO authDAO;
	private long loggedInMID;

	/**
	 * Just the factory and logged in MID
	 * 
	 * @param factory
	 * @param loggedInMID
	 */
	public ChangePasswordAction(DAOFactory factory, long loggedInMID) {
		this.loggedInMID = loggedInMID;
		this.authDAO = factory.getAuthDAO();
	}
	
	/**
	 * Checks to see if the password the user entered is the correct current password.
	 * 
	 * @throws DBException
	 * @throws iTrustException
	 */
	public boolean checkPassword(String pwd) throws DBException, iTrustException {
		String pass = DigestUtils.sha256Hex(pwd + DigestUtils.sha256Hex("" + loggedInMID));
		return pass.equals(authDAO.getPassword(loggedInMID));
	}

	/**
	 * Changes the password of a Patient based on the mid and the new Password they want to set.
	 * 
	 * @throws DBException
	 */
	public void changePassword(String pwd) throws DBException {
		authDAO.resetPassword(loggedInMID, pwd);
	}
	
	/**
	 * Checks to see if the new password the user entered is a valid password.
	 * 
	 * @throws DBException
	 * @throws iTrustException
	 * @return True if it is a valid password, false if it is not.
	 */
	public boolean validatePassword(String pwd) {
		boolean hasNum = false;
		boolean hasChar = false;
		if(pwd.length() > 5) {
			for(int i = 0; i < pwd.length(); i++) {
				if((pwd.charAt(i) >= 'A' && pwd.charAt(i) <= 'Z') || (pwd.charAt(i) >= 'a' && pwd.charAt(i) <= 'z')) {
					hasChar = true;
				}
				if(pwd.charAt(i) >= '0' && pwd.charAt(i) <= '9') {
					hasNum = true;
				}
			}
			return hasChar && hasNum;
		}
		return false;
	}
}
