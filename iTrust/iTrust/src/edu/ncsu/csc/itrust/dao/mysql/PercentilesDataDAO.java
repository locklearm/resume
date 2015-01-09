package edu.ncsu.csc.itrust.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.dao.DAOFactory;

public class PercentilesDataDAO {
	
	private DAOFactory factory;

	/**
	 * The typical constructor.
	 * @param factory The {@link DAOFactory} associated with this DAO, which is used for obtaining SQL connections, etc.
	 */
	public PercentilesDataDAO(DAOFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Adds the given percentiles data row to the database
	 * 
	 * @param percentilesType 1 = weightForAge, 2 = lengthForAge, 3 = headCircumferenceForAge, 4 = BMIForAge
	 * @param sex 1 = male, 2 = female
	 * @param ageInMonths
	 * @param L
	 * @param M
	 * @param S
	 * @return
	 */
	public boolean addPercentileDataRow(int percentilesType, int sex, double ageInMonths, double L, double M, double S) {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = factory.getConnection();
			ps = conn.prepareStatement("INSERT INTO PercentilesData "
					+ "(PercentileType, Sex, AgeInMonths, LValue, MValue, SValue) "
					+ "VALUES "
					+ "(?,?,?,?,?,?) "
					+ "ON DUPLICATE KEY UPDATE "
					+ "LValue = VALUES(LValue), MValue = VALUES(MValue), SValue = VALUES(SValue);");
			ps.setInt(1, percentilesType);
			ps.setInt(2, sex);
			ps.setDouble(3, ageInMonths);
			ps.setDouble(4, L);
			ps.setDouble(5, M);
			ps.setDouble(6, S);
			ps.executeUpdate();
			
		} catch (SQLException e) {
			return false;
			
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
		
		return true;
		
	}
	
	/**
	 * Gets the L, M, and S values used for calculating percentiles from the database
	 * @param percentilesType 1 = weightForAge, 2 = lengthForAge, 3 = headCircumferenceForAge, 4 = BMIForAge
	 * @param sex 1 = male, 2 = female
	 * @param ageInMonths
	 * @return An array of doubles with three elements (in order) L value, M value, S value.  If nothing is found NULL is returned.
	 */
	public double[] getPercentileLMSValues(int percentilesType, int sex, double ageInMonths) {
		
		double[] values = {0.0, 0.0, 0.0};
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = factory.getConnection();
			ps = conn.prepareStatement("SELECT LValue, MValue, SValue FROM PercentilesData "
					+ "WHERE PercentileType=? "
					+ "AND Sex=? "
					+ "ORDER BY abs(AgeInMonths - ?) "
					+ "LIMIT 1;");
			ps.setInt(1, percentilesType);
			ps.setInt(2, sex);
			ps.setDouble(3, ageInMonths);
			rs = ps.executeQuery();
			if (!rs.next()) {
				return null;
			}
			//Put the result data into the values array
			values[0] = rs.getDouble(1);
			values[1] = rs.getDouble(2);
			values[2] = rs.getDouble(3);
		}
		catch (SQLException e) {
		}
		finally {
			DBUtil.closeConnection(conn, ps);
		}
		
		
		
		return values;
		
	}
	
	

}
