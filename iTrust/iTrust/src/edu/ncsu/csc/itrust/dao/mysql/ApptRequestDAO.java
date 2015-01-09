package edu.ncsu.csc.itrust.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.beans.ApptRequestBean;
import edu.ncsu.csc.itrust.beans.loaders.ApptRequestBeanLoader;
import edu.ncsu.csc.itrust.dao.DAOFactory;

/**
 * 
 *
 */
public class ApptRequestDAO {

	private DAOFactory factory;
	private ApptRequestBeanLoader loader;

	public ApptRequestDAO(DAOFactory factory) {
		this.factory = factory;
		loader = new ApptRequestBeanLoader();
	}

	/**
	 * 
	 * @param hcpid
	 * @return
	 */
	public List<ApptRequestBean> getApptRequestsFor(long hcpid) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		conn = factory.getConnection();

		stmt = conn
				.prepareStatement("SELECT * FROM appointmentrequests WHERE doctor_id=? ORDER BY sched_date");

		stmt.setLong(1, hcpid);

		ResultSet rs = stmt.executeQuery();
		List<ApptRequestBean> list = loader.loadList(rs);
		DBUtil.closeConnection(conn, stmt);
		return list;
	}

	/**
	 * 
	 * @param req
	 * @throws SQLException
	 */
	public void addApptRequest(ApptRequestBean req) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		conn = factory.getConnection();

		stmt = conn
				.prepareStatement("INSERT INTO appointmentrequests (appt_type, patient_id, doctor_id, sched_date, comment, pending, accepted) VALUES (?, ?, ?, ?, ?, ?, ?)");
		loader.loadParameters(stmt, req);

		stmt.executeUpdate();
		DBUtil.closeConnection(conn, stmt);
	}

	/**
	 * 
	 * @param req
	 * @throws SQLException
	 */
	public void updateApptRequest(ApptRequestBean req) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		conn = factory.getConnection();

		stmt = conn.prepareStatement("UPDATE appointmentrequests SET pending=?, accepted=? WHERE appt_id=?");

		stmt.setBoolean(1, req.isPending());
		stmt.setBoolean(2, req.isAccepted());
		stmt.setInt(3, req.getRequestedAppt().getApptID());

		stmt.executeUpdate();
		DBUtil.closeConnection(conn, stmt);
	}
	
	public ApptRequestBean getApptRequest(int reqID) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		conn = factory.getConnection();

		stmt = conn
				.prepareStatement("SELECT * FROM appointmentrequests WHERE appt_id=?");

		stmt.setInt(1, reqID);

		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			ApptRequestBean b = loader.loadSingle(rs);
			DBUtil.closeConnection(conn, stmt);
			return b;
		} else {
			DBUtil.closeConnection(conn, stmt);
			return null;
		}
	}
}
