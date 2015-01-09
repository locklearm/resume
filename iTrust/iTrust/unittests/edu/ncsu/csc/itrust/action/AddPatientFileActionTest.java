package edu.ncsu.csc.itrust.action;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.exception.AddPatientFileException;
import edu.ncsu.csc.itrust.exception.AddPatientFileExceptionTest;
import edu.ncsu.csc.itrust.exception.CSVFormatException;
import junit.framework.TestCase;

public class AddPatientFileActionTest extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		
	}
	
	public void testValidData() throws CSVFormatException, AddPatientFileException, FileNotFoundException{
		DAOFactory prodDAO = DAOFactory.getProductionInstance(); 
		AuthDAO authDAO    = prodDAO.getAuthDAO();
		InputStream testFile = new FileInputStream("testing-files/sample_patientupload/HCPPatientUploadValidData.csv");
		AddPatientFileAction apfa=new AddPatientFileAction(testFile,null,0);
		assertEquals(3,apfa.getPatients().size());
		assertFalse(apfa.getErrors().hasErrors());
	}
	
	public void testInvalidData() throws CSVFormatException, AddPatientFileException, FileNotFoundException{
		DAOFactory prodDAO = DAOFactory.getProductionInstance(); 
		AuthDAO authDAO    = prodDAO.getAuthDAO();
		InputStream testFile = new FileInputStream("testing-files/sample_patientupload/HCPPatientUploadInvalidData.csv");
		AddPatientFileAction apfa=new AddPatientFileAction(testFile,null,0);
		assertEquals(1,apfa.getPatients().size());
		assertTrue(apfa.getErrors().hasErrors());
	}
	
	public void testDuplicateField() throws CSVFormatException, AddPatientFileException, FileNotFoundException{
		DAOFactory prodDAO = DAOFactory.getProductionInstance(); 
		AuthDAO authDAO    = prodDAO.getAuthDAO();
		InputStream testFile = new FileInputStream("testing-files/sample_patientupload/HCPPatientUploadDuplicateField.csv");
		try{
			AddPatientFileAction apfa=new AddPatientFileAction(testFile,null,0);
		}catch(AddPatientFileException e){
			return;
		}
		assertTrue(false);
	}
	
	public void testInvalidHeader() throws CSVFormatException, AddPatientFileException, FileNotFoundException{
		DAOFactory prodDAO = DAOFactory.getProductionInstance(); 
		AuthDAO authDAO    = prodDAO.getAuthDAO();
		InputStream testFile = new FileInputStream("testing-files/sample_patientupload/HCPPatientUploadInvalidField.csv");
		try{
			AddPatientFileAction apfa=new AddPatientFileAction(testFile,null,0);
		}catch(AddPatientFileException e){
			return;
		}
		assertTrue(false);
	}
	
	public void testRequiredFieldMissing() throws CSVFormatException, AddPatientFileException, FileNotFoundException{
		DAOFactory prodDAO = DAOFactory.getProductionInstance(); 
		AuthDAO authDAO    = prodDAO.getAuthDAO();
		InputStream testFile = new FileInputStream("testing-files/sample_patientupload/HCPPatientUploadRequiredFieldMissing.csv");
		try{
			AddPatientFileAction apfa=new AddPatientFileAction(testFile,null,0);
		}catch(AddPatientFileException e){
			return;
		}
		assertTrue(false);
	}
}
