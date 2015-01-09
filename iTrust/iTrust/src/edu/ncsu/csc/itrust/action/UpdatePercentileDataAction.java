/**
 * 
 */
package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.PercentilesDataDAO;
import edu.ncsu.csc.itrust.exception.iTrustException;

/**
 *
 */
public class UpdatePercentileDataAction {
	
	private DAOFactory factory;
	private PercentilesDataDAO pdDAO;
	private double a=0;
	private double b=0;
	//TODO - Worry about security??
	//private AuthDAO authDAO;
	//private long loggedInMID;
	
	public UpdatePercentileDataAction(DAOFactory factory) throws iTrustException {
		
		this.factory = factory;
		this.pdDAO = this.factory.getPercentilesDataDAO();
		
	}
	
	public String addPercentilesData(String percentilesType, String percentilesData) {
		
		System.out.println("Adding percentiles information in UpdatePercentileDataAction.java");
		
		String message = "";
		
		//Check the percentiles type is valid
		if (!(
				"weightForAgeInfant".equals(percentilesType)
				|| "weightForAgeAdult".equals(percentilesType)
				|| "lengthForAgeInfant".equals(percentilesType)
				|| "lengthForAgeAdult".equals(percentilesType)
				|| "headCircumferenceForAge".equals(percentilesType)
				|| "BMIForAge".equals(percentilesType)
			)) {
			
			message = "Unknown percentiles type";
			return message;
		}
		
		//Get the percentiles type code
		int percentilesTypeCode = 0;
		
		if ("weightForAgeInfant".equals(percentilesType)) {
			percentilesTypeCode = 1;
		}
		if ("weightForAgeAdult".equals(percentilesType)) {
			percentilesTypeCode = 1;
		}
		if ("lengthForAgeInfant".equals(percentilesType)) {
			percentilesTypeCode = 2;
		}
		if ("lengthForAgeAdult".equals(percentilesType)) {
			percentilesTypeCode = 2;
		}
		if ("headCircumferenceForAge".equals(percentilesType)) {
			percentilesTypeCode = 3;
		}
		if ("BMIForAge".equals(percentilesType)) {
			percentilesTypeCode = 4;
		}
		
		//Now we parse the data string for submission
		
		//Get the separate lines
		String[] lines = percentilesData.split("\\r?\\n");
		
		//TODO - Check column headers.
		
		//Now, for each line, we add it to the database
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];
			
			//If this is a line of headers, ignore it
			if (line.contains("Sex")) {
				System.out.println("Ignored line " + i + " of " + percentilesType);
				continue;
			}
			
			//Get the individual values
			String[] values = line.split(",");
			
			//Add this row of data
			boolean rowAdded = pdDAO.addPercentileDataRow(percentilesTypeCode,
					Integer.parseInt(values[0]), 
					Double.parseDouble(values[1]),
					Double.parseDouble(values[2]),
					Double.parseDouble(values[3]),
					Double.parseDouble(values[4]));
			
			if (rowAdded == false) {
				message += "<br />Failed added row " + i;
			}
			
		}
		
		if (!"".equals(message)) {
			message = "The following rows weren't added:" + message;
		}
		return message;
		
	}
	
	/**
	 * Gets the percentile for a given measurement
	 * 
	 * @param percentilesType percentilesType 1 = weightForAge, 2 = lengthForAge, 3 = headCircumferenceForAge, 4 = BMIForAge
	 * @param sex 1 = male, 2 = female
	 * @param ageInMonths
	 * @param measurement the measurement to get the percentile for
	 * @return The percentile as a double
	 */
	public double getPercentile(int percentilesType, int sex, double ageInMonths, double measurement) {
		
		//Get the L, M, and S value
		double[] values = pdDAO.getPercentileLMSValues(percentilesType, sex, ageInMonths);
		
		//Do a null check
		if (values == null) {
			return 0.0;
		}
		
		//for(int i = 0; i<values.length; i++) System.out.println("VALUE"+i+" "+values[i]);
		//Get the z-score
		double zScore = getZScore(measurement, values[0], values[1], values[2]);
		
		//Get and return the percentile
		if(zScore>0)
		{
			a = (1-erf(zScore/Math.sqrt(2)))/2;
			b = (a*100);
			return b;
		}
		else
		{
			a= (1+erf(zScore/Math.sqrt(2)))/2;
			b= (a*100);
			return b;
		}
	}
	
	private double getZScore(double x, double l, double m, double s) {
		
		//Different formula for if l is 0
		if (l > 0.0) {
			return Math.log(x / m) / s;
		}
		else {
			return (Math.pow((x / m), l) - 1) / (l * s);
		}
		
	}
	
	/**
	 * @author aaboulang2002 (via http://stackoverflow.com/questions/11603228/z-score-to-percentile-in-php)
	 * @param z double needed for calculation
	 * @return percentile in decimal format
	 */
	private static double erf(double z)
	{
		double a = (8*(Math.PI - 3))/(3*Math.PI*(4 - Math.PI));
		double z2 = z*z;

		double az2 = a*z2;
		double num = (4/Math.PI)+az2;
		double denom = 1+az2;
		double in = (-z2)*num/denom;
		double erf2= 1-Math.exp(in);
		
		return Math.sqrt(erf2);
	}
}
