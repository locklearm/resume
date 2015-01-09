package edu.ncsu.csc.itrust.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.beans.loaders.ApptBeanLoader;
import edu.ncsu.csc.itrust.dao.DAOFactory;

public class ApptDAO {
	private DAOFactory factory;
	private ApptBeanLoader abloader;
	private ApptTypeDAO apptTypeDAO;
	
	public ApptDAO(DAOFactory factory) {
		this.factory = factory;
		this.apptTypeDAO = factory.getApptTypeDAO();
		this.abloader = new ApptBeanLoader();
	}
	
	public List<ApptBean> getAppt(int apptID) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		conn = factory.getConnection();
		
		ps = conn.prepareStatement("SELECT * FROM appointment WHERE appt_id=?");
		
		ps.setInt(1, apptID);
		
		ResultSet rs = ps.executeQuery();
		List<ApptBean> abList = this.abloader.loadList(rs);
		DBUtil.closeConnection(conn, ps);
		return abList;
	}
	
	public List<ApptBean> getApptsFor(long mid) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		conn = factory.getConnection();
		
		if(mid >= 999999999){
			ps = conn.prepareStatement("SELECT * FROM appointment WHERE doctor_id=? AND sched_date > NOW() ORDER BY sched_date;");
		}
		else {
			ps = conn.prepareStatement("SELECT * FROM appointment WHERE patient_id=? AND sched_date > NOW() ORDER BY sched_date;");
		}
		
		ps.setLong(1, mid);
		
		ResultSet rs = ps.executeQuery();
		List<ApptBean> abList = this.abloader.loadList(rs);
		DBUtil.closeConnection(conn, ps);
		return abList;
	}
	
	public List<ApptBean> getAllApptsFor(long mid) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		conn = factory.getConnection();
		
		if(mid >= 999999999){
			ps = conn.prepareStatement("SELECT * FROM appointment WHERE doctor_id=? ORDER BY sched_date;");
		}
		else {
			ps = conn.prepareStatement("SELECT * FROM appointment WHERE patient_id=? ORDER BY sched_date;");
		}
		
		ps.setLong(1, mid);
		
		ResultSet rs = ps.executeQuery();
		List<ApptBean> abList = this.abloader.loadList(rs);
		DBUtil.closeConnection(conn, ps);
		return abList;
	}
	
	public void scheduleAppt(ApptBean appt) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		conn = factory.getConnection();
		
		ps = conn.prepareStatement(
				"INSERT INTO appointment (appt_type, patient_id, doctor_id, sched_date, comment) "
			  + "VALUES (?, ?, ?, ?, ?)");
		ps = this.abloader.loadParameters(ps, appt);
		
		ps.executeUpdate();
		DBUtil.closeConnection(conn, ps);
	}
	
	public void editAppt(ApptBean appt) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		conn = factory.getConnection();

		ps = conn.prepareStatement(
				"UPDATE appointment SET appt_type=?, sched_date=?, comment=? WHERE appt_id=?");
		ps.setString(1, appt.getApptType());
		ps.setTimestamp(2, appt.getDate());
		ps.setString(3, appt.getComment());
		ps.setInt(4, appt.getApptID());
		
		ps.executeUpdate();
		DBUtil.closeConnection(conn,ps);
	}
	
	public void removeAppt(ApptBean appt) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		conn = factory.getConnection();

		ps = conn.prepareStatement(
				"DELETE FROM appointment WHERE appt_id=?");
		ps.setInt(1, appt.getApptID());
		
		ps.executeUpdate();
		DBUtil.closeConnection(conn,ps);
	}
	
	public List<ApptBean> getAllHCPConflictsForAppt(long mid, ApptBean appt) throws SQLException{
		

		int duration = apptTypeDAO.getApptType(appt.getApptType()).getDuration();
		
		Connection conn = null;
		PreparedStatement ps = null;
		conn = factory.getConnection();
		ps = conn.prepareStatement("SELECT * " +
				"FROM appointment a, appointmenttype type " +	//and the corresponding types
				"WHERE a.appt_type=type.appt_type AND " +				//match them with types
				"((DATE_ADD(a.sched_date, INTERVAL type.duration MINUTE)>? AND " +	//a1 ends after a2 starts AND
				"a.sched_date<=?) OR " +				//a1 starts before a2 OR
				"(DATE_ADD(?, INTERVAL ? MINUTE)>a.sched_date AND " +		//a2 ends after a1 starts AND
				"?<=a.sched_date)) AND " + 			//a2 starts before a1 starts
				"a.doctor_id=? AND a.appt_id!=?;");

		ps.setTimestamp(1, appt.getDate());
		ps.setTimestamp(2, appt.getDate());
		ps.setTimestamp(3, appt.getDate());
		ps.setInt(4, duration);
		ps.setTimestamp(5, appt.getDate());
		ps.setLong(6, mid);
		ps.setInt(7, appt.getApptID());

		ResultSet rs = ps.executeQuery();
		
		List<ApptBean> conflictList = this.abloader.loadList(rs);
		DBUtil.closeConnection(conn,ps);
		
		return conflictList;
	}
	
	public List<ApptBean> getAllPatientConflictsForAppt(long mid, ApptBean appt) throws SQLException{
		int duration = apptTypeDAO.getApptType(appt.getApptType()).getDuration();
		Connection conn = null;
		PreparedStatement ps = null;
		conn = factory.getConnection();
		ps = conn.prepareStatement("SELECT * " +
				"FROM appointment a, appointmenttype type " +	//and the corresponding types
				"WHERE a.appt_type=type.appt_type AND " +				//match them with types
				"((DATE_ADD(a.sched_date, INTERVAL type.duration MINUTE)>? AND " +	//a1 ends after a2 starts AND
				"a.sched_date<=?) OR " +				//a1 starts before a2 OR
				"(DATE_ADD(?, INTERVAL ? MINUTE)>a.sched_date AND " +		//a2 ends after a1 starts AND
				"?<=a.sched_date)) AND " + 			//a2 starts before a1 starts
				"a.patient_id=? AND a.appt_id!=?;");

		ps.setTimestamp(1, appt.getDate());
		ps.setTimestamp(2, appt.getDate());
		ps.setTimestamp(3, appt.getDate());
		ps.setInt(4, duration);
		ps.setTimestamp(5, appt.getDate());
		ps.setLong(6, mid);
		ps.setInt(7, appt.getApptID());

		ResultSet rs = ps.executeQuery();
		
		List<ApptBean> conflictList = this.abloader.loadList(rs);
		DBUtil.closeConnection(conn,ps);
		
		return conflictList;
	}
	
	/**
	 * Returns all past and future appointment conflicts for the doctor with the given MID
	 * @param mid
	 * @throws SQLException
	 */
	public List<ApptBean> getAllConflictsForDoctor(long mid) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		conn = factory.getConnection();
		
		ps = conn.prepareStatement("SELECT a1.* " +
				"FROM appointment a1, appointment a2, " +			//all possible sets of 2 appts
				"appointmenttype type1,appointmenttype type2 " +	//and the corresponding types
				"WHERE a1.appt_id!=a2.appt_id AND " +				//exclude itself
				"a1.appt_type=type1.appt_type AND a2.appt_type=type2.appt_type AND " +				//match them with types
				"((DATE_ADD(a1.sched_date, INTERVAL type1.duration MINUTE)>a2.sched_date AND " +	//a1 ends after a2 starts AND
				"a1.sched_date<=a2.sched_date) OR" +				//a1 starts before a2 OR
				"(DATE_ADD(a2.sched_date, INTERVAL type2.duration MINUTE)>a1.sched_date AND " +		//a2 ends after a1 starts AND
				"a2.sched_date<=a1.sched_date)) AND " + 			//a2 starts before a1 starts
				"a1.doctor_id=? AND a2.doctor_id=?;");
		
		ps.setLong(1, mid);
		ps.setLong(2, mid);

		ResultSet rs = ps.executeQuery();
		
		List<ApptBean> conflictList = this.abloader.loadList(rs);
		DBUtil.closeConnection(conn,ps);
		
		return conflictList;
	}
	
	/**
	 * Returns all past and future appointment conflicts for the patient with the given MID
	 * @param mid
	 * @throws SQLException
	 */
	public List<ApptBean> getAllConflictsForPatient(long mid) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		conn = factory.getConnection();
		
		ps = conn.prepareStatement("SELECT a1.* " +
				"FROM appointment a1, appointment a2, " +			//all possible sets of 2 appts
				"appointmenttype type1,appointmenttype type2 " +	//and the corresponding types
				"WHERE a1.appt_id!=a2.appt_id AND " +				//exclude itself
				"a1.appt_type=type1.appt_type AND a2.appt_type=type2.appt_type AND " +				//match them with types
				"((DATE_ADD(a1.sched_date, INTERVAL type1.duration MINUTE)>a2.sched_date AND " +	//a1 ends after a2 starts AND
				"a1.sched_date<=a2.sched_date) OR" +				//a1 starts before a2 OR
				"(DATE_ADD(a2.sched_date, INTERVAL type2.duration MINUTE)>a1.sched_date AND " +		//a2 ends after a1 starts AND
				"a2.sched_date<=a1.sched_date)) AND " + 			//a2 starts before a1 starts
				"a1.patient_id=? AND a2.patient_id=?;");
		
		ps.setLong(1, mid);
		ps.setLong(2, mid);

		ResultSet rs = ps.executeQuery();
		
		List<ApptBean> conflictList = this.abloader.loadList(rs);
		DBUtil.closeConnection(conn,ps);
		
		return conflictList;
	}
}
