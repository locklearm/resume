package edu.ncsu.csc.itrust.beans;

import java.sql.Timestamp;
import java.util.Date;

/**
 * A bean for storing health record data.
 * 
 * A bean's purpose is to store data. Period. Little or no functionality is to be added to a bean 
 * (with the exception of minor formatting such as concatenating phone numbers together). 
 * A bean must only have Getters and Setters (Eclipse Hint: Use Source > Generate Getters and Setters� 
 * to create these easily)
 */
public class HealthRecord {
	private long recordID = 0;
	private long patientID = 0;
	private double height = 0;
	private double cmHeight = 0;
	private double kgWeight = 0;
	private double weight = 0;
	private double headCircumference = 0;
	private boolean isSmoker = false;
	private String smokingStatusDesc = "Unknown if ever smoked";
	private int smokingStatus = 9;
	private int householdSmokingStatus = 9;
	private String householdSmokingStatusDesc = "Unknown";
	private int bloodPressureN = 0;
	private int bloodPressureD = 0;
	private int cholesterolHDL = 1;
	private int cholesterolLDL = 100;
	private int cholesterolTri = 100;
	private long personnelID = 0;
	private long officeVisitID = 0;
	private Timestamp dateRecorded = new Timestamp(new Date().getTime());

	public HealthRecord() {
	}

	
	
	/**
	 * @return the recordID
	 */
	public long getRecordID() {
		return recordID;
	}



	/**
	 * @param recordID the recordID to set
	 */
	public void setRecordID(long recordID) {
		this.recordID = recordID;
	}



	/**
	 * @return the headCircumference
	 */
	public double getHeadCircumference() {
		return headCircumference;
	}



	/**
	 * @param headCircumference the headCircumference to set
	 */
	public void setHeadCircumference(double headCircumference) {
		this.headCircumference = headCircumference;
	}



	/**
	 * @return the householdSmokingStatus
	 */
	public int getHouseholdSmokingStatus() {
		return householdSmokingStatus;
	}



	/**
	 * Sets the householdSmokingStatus field.  Also sets the houseHoldSmokingStatusDesc field,
	 * based on the value of householdSmokingStatus.
	 * 
	 * @param householdSmokingStatus the householdSmokingStatus to set
	 */
	public void setHouseholdSmokingStatus(int householdSmokingStatus) {
		this.householdSmokingStatus = householdSmokingStatus;
		
		if (householdSmokingStatus == 1) {
			this.householdSmokingStatusDesc = "non-smoking household";
		}
		else if (householdSmokingStatus == 2) {
			this.householdSmokingStatusDesc = "outdoor smokers";
		}
		else if (householdSmokingStatus == 3) {
			this.householdSmokingStatusDesc = "indoor smokers";
		}
		else {
			this.householdSmokingStatusDesc = "unknown";
		}
	}

	/**
	 * @return the householdSmokingStatusDesc
	 */
	public String getHouseholdSmokingStatusDesc() {
		return householdSmokingStatusDesc;
	}


	/**
	 * @return the officeVisitID
	 */
	public long getOfficeVisitID() {
		return officeVisitID;
	}



	/**
	 * @param officeVisitID the officeVisitID to set
	 */
	public void setOfficeVisitID(long officeVisitID) {
		this.officeVisitID = officeVisitID;
	}



	public int getBloodPressureD() {
		return bloodPressureD;
	}

	public void setBloodPressureD(int bloodPressureD) {
		this.bloodPressureD = bloodPressureD;
	}

	public void setBloodPressureSystolic(int bloodPressure) {
		this.bloodPressureN = bloodPressure;
	}

	public void setBloodPressureDiastolic(int bloodPressure) {
		this.bloodPressureD = bloodPressure;
	}

	public int getBloodPressureN() {
		return bloodPressureN;
	}

	public int getBloodPressureSystolic() {
		return bloodPressureN;
	}

	public int getBloodPressureDiastolic() {
		return bloodPressureD;
	}

	public void setBloodPressureN(int bloodPressureN) {
		this.bloodPressureN = bloodPressureN;
	}

	public String getBloodPressure() {
		return getBloodPressureN() + "/" + getBloodPressureD();
	}

	public int getCholesterolHDL() {
		return cholesterolHDL;
	}

	public void setCholesterolHDL(int cholesterolHDL) {
		this.cholesterolHDL = cholesterolHDL;
	}

	public int getCholesterolLDL() {
		return cholesterolLDL;
	}

	public void setCholesterolLDL(int cholesterolLDL) {
		this.cholesterolLDL = cholesterolLDL;
	}

	public int getCholesterolTri() {
		return cholesterolTri;
	}

	public void setCholesterolTri(int cholesterolTri) {
		this.cholesterolTri = cholesterolTri;
	}

	/**
	 * Note that this is a simplistic view. See the Wikipedia article on cholesterol.
	 * 
	 * @return
	 */
	public int getTotalCholesterol() {
		return getCholesterolHDL() + getCholesterolLDL() + getCholesterolTri();
	}

	public Date getDateRecorded() {
		return dateRecorded;
	}

	public void setDateRecorded(Timestamp dateRecorded) {
		this.dateRecorded = dateRecorded;
	}

	// Rounds the height off here because MySQL won't return the *exact* value you put in it
	public double getHeight() {
		return Math.round(height * 10000) / 10000D;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public long getPatientID() {
		return patientID;
	}

	public void setPatientID(long patientID) {
		this.patientID = patientID;
	}

	public long getPersonnelID() {
		return personnelID;
	}

	public void setPersonnelID(long personnelID) {
		this.personnelID = personnelID;
	}

	public boolean isSmoker() {
		return isSmoker;
	}
	
	public int getSmokingStatus() {
		return smokingStatus;
	}
	
	public String getSmokingStatusDesc() {
		return smokingStatusDesc;
	}
	
	public void setSmoker(int smoker) {
		this.smokingStatus = smoker;
		switch (smoker) {
			case 1: this.isSmoker = true;
			this.smokingStatusDesc = "Current every day smoker"; break;
			case 2: this.isSmoker = true;
			this.smokingStatusDesc = "Current some day smoker"; break;
			case 3: this.isSmoker = false;
			this.smokingStatusDesc = "Former smoker"; break;
			case 4: this.isSmoker = false;
			this.smokingStatusDesc = "Never smoker"; break;
			case 5: this.isSmoker = true;
			this.smokingStatusDesc = "Smoker, current status unknown"; break;
			case 9: this.isSmoker = false;
			this.smokingStatusDesc = "Unknown if ever smoked"; break;
		}
	}

	public double getWeight() {
		return Math.round(weight * 10000) / 10000D;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getHeightInCM()
	{
		cmHeight = this.height*2.54;
		return cmHeight;
	}
	
	public double getWeightInKG()
	{
		kgWeight = this.weight/2.2;
		return kgWeight;
	}
	
	
	public double getHeadCircumferenceInCM()
	{
		return this.headCircumference*2.54;
	}
	
	public double getBodyMassIndex() {
		double BMI = 0;
		BMI= getHeightInCM()/100;
		BMI =BMI*BMI;
		BMI = getWeightInKG()/BMI;
		BMI = BMI*10;
		BMI = Math.floor(BMI);
		BMI = BMI/10;
		return BMI;
	}
}
