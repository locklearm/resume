package edu.ncsu.csc.itrust.beans.loaders;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import edu.ncsu.csc.itrust.beans.HealthRecord;

/**
 * A loader for HealthRecords.
 * 
 * Loads in information to/from beans using ResultSets and PreparedStatements. Use the superclass to enforce consistency. 
 * For details on the paradigm for a loader (and what its methods do), see {@link BeanLoader}
 */
public class HealthRecordsBeanLoader implements BeanLoader<HealthRecord> {
	
	public List<HealthRecord> loadList(ResultSet rs) throws SQLException {
		ArrayList<HealthRecord> list = new ArrayList<HealthRecord>();
		while (rs.next()) {
			list.add(loadSingle(rs));
		}
		return list;
	}

	public PreparedStatement loadParameters(PreparedStatement ps, HealthRecord bean) throws SQLException {
		int i = 1;
		ps.setLong(i++, bean.getPatientID());
		ps.setDouble(i++, bean.getHeight());
		ps.setDouble(i++, bean.getWeight());
		ps.setDouble(i++, bean.getHeadCircumference());
		ps.setInt(i++, bean.isSmoker() ? 1 : 0);
		ps.setInt(i++, bean.getSmokingStatus());
		ps.setInt(i++, bean.getHouseholdSmokingStatus());
		ps.setInt(i++, bean.getBloodPressureN());
		ps.setInt(i++, bean.getBloodPressureD());
		ps.setInt(i++, bean.getCholesterolHDL());
		ps.setInt(i++, bean.getCholesterolLDL());
		ps.setInt(i++, bean.getCholesterolTri());
		ps.setLong(i++, bean.getPersonnelID());
		//If officeVisitID is 0, this is flag to store null in the database
		if (bean.getOfficeVisitID() == 0) {
			ps.setNull(i++, Types.BIGINT);
		}
		else {
			ps.setLong(i++, bean.getOfficeVisitID());
		}
		if (bean.getRecordID() != 0) {
			ps.setLong(i++, bean.getRecordID());
		}
		
		return ps;
	}

	public HealthRecord loadSingle(ResultSet rs) throws SQLException {
		HealthRecord hr = new HealthRecord();
		hr.setRecordID(rs.getInt("PersonalHealthInformationID"));
		hr.setBloodPressureN(rs.getInt("BloodPressureN"));
		hr.setBloodPressureD(rs.getInt("BloodPressureD"));
		hr.setCholesterolHDL(rs.getInt("CholesterolHDL"));
		hr.setCholesterolLDL(rs.getInt("CholesterolLDL"));
		hr.setCholesterolTri(rs.getInt("CholesterolTri"));
		hr.setDateRecorded(rs.getTimestamp("AsOfDate"));
		hr.setSmoker(rs.getInt("SmokingStatus"));
		hr.setHouseholdSmokingStatus(rs.getInt("HouseholdSmokingStatus"));
		hr.setHeight(rs.getDouble("Height"));
		hr.setWeight(rs.getDouble("Weight"));
		hr.setHeadCircumference(rs.getDouble("HeadCircumference"));
		hr.setPersonnelID(rs.getLong("HCPID"));
		hr.setPatientID(rs.getLong("PatientID"));
		hr.setOfficeVisitID(rs.getLong("OfficeVisitID"));
		return hr;
	}
}
