package edu.ncsu.csc.itrust.validate.bean;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.forms.HealthRecordForm;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.validate.HealthRecordFormValidator;

public class HealthRecordBeanValidatorTest extends TestCase {
	private HealthRecordFormValidator validator = new HealthRecordFormValidator();

	public void testAllCorrect() throws Exception {
		HealthRecordForm hr = new HealthRecordForm();
		hr.setBloodPressureN("999");
		hr.setBloodPressureD("999");
		hr.setCholesterolHDL("89");
		hr.setCholesterolLDL("100");
		hr.setCholesterolTri("100");
		hr.setHeight("657.2");
		hr.setWeight("999.9");
		hr.setHeadCircumference("20.1");
		hr.setIsSmoker("1");
		validator.validate(hr);
	}
	
	public void testZeroWeight(){
		HealthRecordForm hr = new HealthRecordForm();
		hr.setBloodPressureN("999");
		hr.setBloodPressureD("999");
		hr.setCholesterolHDL("89");
		hr.setCholesterolLDL("100");
		hr.setCholesterolTri("100");
		hr.setHeight("100");
		hr.setWeight("0");
		hr.setIsSmoker("1");
		try {
			validator.validate(hr);
			fail("Should have thrown exception");
		} catch (FormValidationException e) {
			//good
		}
	}	
	
	public void testZeroHeight(){
		HealthRecordForm hr = new HealthRecordForm();
		hr.setBloodPressureN("999");
		hr.setBloodPressureD("999");
		hr.setCholesterolHDL("89");
		hr.setCholesterolLDL("100");
		hr.setCholesterolTri("100");
		hr.setHeight("0");
		hr.setWeight("100");
		hr.setIsSmoker("1");
		try {
			validator.validate(hr);
			fail("Should have thrown exception");
		} catch (FormValidationException e) {
			//good
		}
	}

	
	public void testJustAboveBoundaries() throws Exception {
		try {
			HealthRecordForm hr = new HealthRecordForm();
			hr.setBloodPressureN("1000");
			hr.setBloodPressureD("1000");
			hr.setCholesterolHDL("90");
			hr.setCholesterolLDL("601");
			hr.setCholesterolTri("601");
			hr.setHeight("99.01");
			hr.setWeight("9999.01");
			hr.setHeadCircumference("1000.01");
			hr.setIsSmoker("Tru");
			validator.validate(hr);
			fail("exception should have been thrown");
		} catch (FormValidationException e) {
			assertEquals("Systolic blood pressure must be an integer in [0,999]", e.getErrorList().get(0));
			assertEquals("Diastolic blood pressure must be an integer in [0,999]", e.getErrorList().get(1));
			assertEquals("Cholesterol HDL must be an integer in [0,89]", e.getErrorList().get(2));
			assertEquals("Cholesterol LDL must be an integer in [0,600]", e.getErrorList().get(3));
			assertEquals("Cholesterol Triglycerides must be an integer in [100,600]", e.getErrorList().get(4));
			assertEquals("Height: Up to 3-digit number + up to 1 decimal place", e.getErrorList().get(5));
			assertEquals("Weight: Up to 4-digit number + up to 1 decimal place", e.getErrorList().get(6));
			assertEquals("Head Circumference: Up to 4 digit number and 1 decimal place", e.getErrorList().get(7));
			assertEquals("Smoker must be an integer in [0,10]", e.getErrorList().get(8));
			assertEquals(9, e.getErrorList().size());
		}
	}

	public void testNotNumbers() throws Exception {
		try {
			HealthRecordForm hr = new HealthRecordForm();
			hr.setBloodPressureN("a");
			hr.setBloodPressureD("b");
			hr.setCholesterolHDL("b");
			hr.setCholesterolLDL("b");
			hr.setCholesterolTri("b");
			hr.setHeight("b");
			hr.setWeight("b");
			hr.setHeadCircumference("e");
			hr.setIsSmoker("b");
			validator.validate(hr);
			fail("exception should have been thrown");
		} catch (FormValidationException e) {
			assertEquals("Systolic blood pressure must be an integer in [0,999]", e.getErrorList().get(0));
			assertEquals("Diastolic blood pressure must be an integer in [0,999]", e.getErrorList().get(1));
			assertEquals("Cholesterol HDL must be an integer in [0,89]", e.getErrorList().get(2));
			assertEquals("Cholesterol LDL must be an integer in [0,600]", e.getErrorList().get(3));
			assertEquals("Cholesterol Triglycerides must be an integer in [100,600]", e.getErrorList().get(4));
			assertEquals("Height: Up to 3-digit number + up to 1 decimal place", e.getErrorList().get(5));
			assertEquals("Weight: Up to 4-digit number + up to 1 decimal place", e.getErrorList().get(6));
			assertEquals("Head Circumference: Up to 4 digit number and 1 decimal place", e.getErrorList().get(7));
			assertEquals("Smoker must be an integer in [0,10]", e.getErrorList().get(8));
			assertEquals(9, e.getErrorList().size());
		}
	}

}
