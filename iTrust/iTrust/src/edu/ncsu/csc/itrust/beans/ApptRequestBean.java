package edu.ncsu.csc.itrust.beans;

/**
 * 
 *
 */
public class ApptRequestBean {
	private ApptBean requestedAppt;
	private Boolean status;

	/**
	 * 
	 * @return
	 */
	public ApptBean getRequestedAppt() {
		return requestedAppt;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPending() {
		return status == null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAccepted() {
		return status != null && status.booleanValue();
	}

	/**
	 * 
	 * @param appt
	 */
	public void setRequestedAppt(ApptBean appt) {
		requestedAppt = appt;
	}

	/**
	 * 
	 * @param pending
	 */
	public void setPending(boolean pending) {
		if (pending) {
			status = null;
		} else {
			status = new Boolean(false);
		}
	}

	/**
	 * If setPending(false) has not been called before using this method, this method will have no effect.
	 * 
	 * @param accepted
	 */
	public void setAccepted(boolean accepted) {
		if (status != null) {
			status = Boolean.valueOf(accepted);
		}
	}
}
