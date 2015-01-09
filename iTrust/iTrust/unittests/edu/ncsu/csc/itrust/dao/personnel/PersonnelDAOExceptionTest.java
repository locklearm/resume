package edu.ncsu.csc.itrust.dao.personnel;

import java.awt.List;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.beans.PrescriptionBean;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.enums.Role;

public class PersonnelDAOExceptionTest extends TestCase {
	private PersonnelDAO evilDAO = EvilDAOFactory.getEvilInstance().getPersonnelDAO();

	@Override
	protected void setUp() throws Exception {
	}

	public void testAddEmptyPersonnelException() throws Exception {
		try {
			evilDAO.addEmptyPersonnel(Role.HCP);
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testCheckPersonnelExistsException() throws Exception {
		try {
			evilDAO.checkPersonnelExists(0L);
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testEditPersonnelException() throws Exception {
		try {
			evilDAO.editPersonnel(new PersonnelBean());
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testGetHospitalsException() throws Exception {
		try {
			evilDAO.getHospitals(0L);
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testGetNameException() throws Exception {
		try {
			evilDAO.getName(0L);
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testGetLabTechs() throws Exception {
		try {
			evilDAO.getLabTechs();
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testGetUAPsForHCP() throws Exception {
		try {
			evilDAO.getUAPsForHCP(0L);
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testGetAllPersonnel() throws Exception {
		try {
			evilDAO.getAllPersonnel();
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testGetPersonnelFromHospital() throws Exception {
		try {
			evilDAO.getPersonnelFromHospital("","");
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testAddEmptyPersonnel() throws Exception {
		try {
			evilDAO.addEmptyPersonnel(Role.ADMIN);
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testGetPersonnelFromHospitalNoSpeciality() throws Exception {
		try {
			evilDAO.getPersonnelFromHospital("");
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testGetPrescribingDoctor() throws Exception {
		try {
			PrescriptionBean prescription = new PrescriptionBean();
			evilDAO.getPrescribingDoctor(prescription);
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}

	public void testPersonnelException() throws Exception {
		try {
			evilDAO.getPersonnel(0L);
			fail("DBException should have been thrown");
		} catch (DBException e) {
			assertEquals(EvilDAOFactory.MESSAGE, e.getSQLException().getMessage());
		}
	}
}
