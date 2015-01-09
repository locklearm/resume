package edu.ncsu.csc.itrust.dao.appointment;

import java.sql.Timestamp;
import java.util.List;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.beans.ApptRequestBean;
import edu.ncsu.csc.itrust.dao.mysql.ApptRequestDAO;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class ApptRequestDAOTest extends TestCase {

	private ApptRequestDAO arDAO;
	private TestDataGenerator gen = new TestDataGenerator();

	protected void setUp() throws Exception {
		gen.clearAllTables();
		gen.standardData();
		gen.apptRequestConflicts();
		arDAO = TestDAOFactory.getTestInstance().getApptRequestDAO();
	}

	public void testGetApptRequestsFor() throws Exception {
		List<ApptRequestBean> beans = arDAO.getApptRequestsFor(9000000000L);
		assertEquals(1, beans.size());
		assertEquals(2L, beans.get(0).getRequestedAppt().getPatient());
		assertTrue(beans.get(0).isPending());
	}

	public void testAddApptRequest() throws Exception {
		gen.clearAllTables();
		ApptBean bean = new ApptBean();
		bean.setApptType("General Checkup");
		bean.setHcp(9000000000L);
		bean.setPatient(2L);
		bean.setDate(new Timestamp(System.currentTimeMillis()));
		ApptRequestBean req = new ApptRequestBean();
		req.setRequestedAppt(bean);
		arDAO.addApptRequest(req);
		List<ApptRequestBean> reqs = arDAO.getApptRequestsFor(9000000000L);
		assertEquals(1, reqs.size());
		assertEquals(bean.getPatient(), reqs.get(0).getRequestedAppt().getPatient());
		assertTrue(reqs.get(0).isPending());
		assertFalse(reqs.get(0).isAccepted());
	}

	public void testGetApptRequest() throws Exception {
		List<ApptRequestBean> beans = arDAO.getApptRequestsFor(9000000000L);
		ApptRequestBean b = beans.get(0);
		int id = b.getRequestedAppt().getApptID();
		ApptRequestBean b2 = arDAO.getApptRequest(id);
		assertEquals(b.getRequestedAppt().getApptID(), b2.getRequestedAppt().getApptID());
	}

	public void testUpdateApptRequest() throws Exception {
		List<ApptRequestBean> beans = arDAO.getApptRequestsFor(9000000000L);
		ApptRequestBean b = beans.get(0);
		int id = b.getRequestedAppt().getApptID();
		b.setPending(false);
		b.setAccepted(true);
		arDAO.updateApptRequest(b);
		ApptRequestBean b2 = arDAO.getApptRequest(id);
		assertEquals(b.getRequestedAppt().getApptID(), b2.getRequestedAppt().getApptID());
		assertFalse(b2.isPending());
		assertTrue(b2.isAccepted());
	}
}
