package edu.ncsu.csc.itrust.action;

import java.util.List;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.LabProcedureBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.LabProcedureDAO;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class LabProcLTActionTest extends TestCase {

	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private LabProcedureDAO lpDAO = factory.getLabProcedureDAO();
	private TestDataGenerator gen;
	LabProcLTAction action;
	LabProcLTAction action2;
	
	@Override
	protected void setUp() throws Exception {
		gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.transactionLog();
		gen.ltData0();
		gen.ltData1();
		gen.ltData2();
		gen.hcp0();
		gen.labProcedures();
		action = new LabProcLTAction(factory, 5000000001L);
	}

	public void testUpdateProcedure() throws Exception {
		LabProcedureBean lp = new LabProcedureBean();
		lp.setLoinc("10763-1");
		lp.setCommentary("");
		lp.setOvID(902L);
		lp.setPid(2L);
		lp.setResults("");
		lp.allow();
		lp.statusInTransit();
		lp.setLTID(5000000005L);
		long id = lpDAO.addLabProcedure(lp);
		lp.setProcedureID(id);
		
		LabProcedureBean procedures = lpDAO.getLabProcedure(id);
		
		List<LabProcedureBean> beans = action.viewInTransitProcedures(5000000005L);
		assertEquals(1, beans.size());
		LabProcedureBean lpBean = beans.get(0);
		
		assertEquals(procedures.getLoinc(), lpBean.getLoinc());
		assertEquals(procedures.getOvID(), lpBean.getOvID());
		assertEquals(procedures.getLoinc(), lpBean.getLoinc());
		assertEquals("In Transit", lpBean.getStatus());
		assertEquals(procedures.getProcedureID(), lpBean.getProcedureID());
	}

	public void testViewReceivedProcedures() throws Exception {
		LabProcedureBean lp = new LabProcedureBean();
		lp.setLoinc("10763-1");
		lp.setCommentary("");
		lp.setOvID(902L);
		lp.setPid(2L);
		lp.setResults("");
		lp.allow();
		lp.statusReceived();
		lp.setLTID(5000000005L);
		long id = lpDAO.addLabProcedure(lp);
		lp.setProcedureID(id);
		
		LabProcedureBean procedures = lpDAO.getLabProcedure(id);
		
		List<LabProcedureBean> beans = action.viewReceivedProcedures(5000000005L);
		assertEquals(1, beans.size());
		LabProcedureBean lpBean = beans.get(0);
		
		assertEquals(procedures.getLoinc(), lpBean.getLoinc());
		assertEquals(procedures.getOvID(), lpBean.getOvID());
		assertEquals(procedures.getLoinc(), lpBean.getLoinc());
		assertEquals("Received", lpBean.getStatus());
		assertEquals(procedures.getProcedureID(), lpBean.getProcedureID());
	}

	public void testViewTestingProcedures() throws Exception {
		LabProcedureBean lp = new LabProcedureBean();
		lp.setLoinc("10763-1");
		lp.setCommentary("");
		lp.setOvID(902L);
		lp.setPid(2L);
		lp.setResults("");
		lp.allow();
		lp.statusTesting();
		lp.setLTID(5000000005L);
		long id = lpDAO.addLabProcedure(lp);
		lp.setProcedureID(id);
		
		LabProcedureBean procedures = lpDAO.getLabProcedure(id);
		
		List<LabProcedureBean> beans = action.viewTestingProcedures(5000000005L);
		assertEquals(1, beans.size());
		LabProcedureBean lpBean = beans.get(0);
		
		assertEquals(procedures.getLoinc(), lpBean.getLoinc());
		assertEquals(procedures.getOvID(), lpBean.getOvID());
		assertEquals(procedures.getLoinc(), lpBean.getLoinc());
		assertEquals("Testing", lpBean.getStatus());
		assertEquals(procedures.getProcedureID(), lpBean.getProcedureID());
	}

	public void testGetHCPName() throws Exception {
		LabProcedureBean lp = new LabProcedureBean();
		lp.setLoinc("10763-1");
		lp.setCommentary("");
		lp.setOvID(902L);
		lp.setPid(2L);
		lp.setResults("");
		lp.allow();
		lp.statusTesting();
		lp.setLTID(5000000005L);
		long id = lpDAO.addLabProcedure(lp);
		lp.setProcedureID(id);
		
		lpDAO.getLabProcedure(id);
		
		assertEquals("Kelly Doctor", action.getHCPName(902L));
		
	}

	public void testSubmitResults() throws Exception {
		LabProcedureBean lp = new LabProcedureBean();
		lp.setLoinc("10763-1");
		lp.setCommentary("");
		lp.setOvID(902L);
		lp.setPid(2L);
		lp.setResults("");
		lp.allow();
		lp.statusReceived();
		lp.setLTID(5000000005L);
		long id = lpDAO.addLabProcedure(lp);
		lp.setProcedureID(id);
		
		assertTrue(action.submitResults(""+id, "12","grams", "13", "14"));
		
		LabProcedureBean procedures = lpDAO.getLabProcedure(id);
		
		assertEquals("Pending", procedures.getStatus());
		assertEquals("12", procedures.getNumericalResult());
		assertEquals("grams",procedures.getNumericalResultUnit());
		assertEquals("13", procedures.getUpperBound());
		assertEquals("14", procedures.getLowerBound());

	}

	public void testSubmitResultsBadNumber() throws Exception {
		
		assertFalse(action.submitResults("2134gt,78", "12","grams", "13", "14"));

	}
	
	public void testSubmitReceived() throws Exception {
		LabProcedureBean lp = new LabProcedureBean();
		lp.setLoinc("10763-1");
		lp.setCommentary("");
		lp.setOvID(902L);
		lp.setPid(2L);
		lp.setResults("");
		lp.allow();
		lp.statusInTransit();
		lp.setLTID(5000000005L);
		long id = lpDAO.addLabProcedure(lp);
		lp.setProcedureID(id);
		
		assertTrue(action.submitReceived(""+id));
		
		LabProcedureBean procedures = lpDAO.getLabProcedure(id);
		
		assertEquals("Received", procedures.getStatus());
	}
	
	public void testSubmitReceivedBadNumber() throws Exception {
		
		assertFalse(action.submitReceived("asd5,"));
		
	}
	
	public void testSetToTesting() throws Exception {
		LabProcedureBean lp = new LabProcedureBean();
		lp.setLoinc("10763-1");
		lp.setCommentary("");
		lp.setOvID(902L);
		lp.setPid(2L);
		lp.setResults("");
		lp.allow();
		lp.statusReceived();
		lp.setLTID(5000000005L);
		long id = lpDAO.addLabProcedure(lp);
		lp.setProcedureID(id);
		
		assertTrue(action.setToTesting(id));
		
		LabProcedureBean procedures = lpDAO.getLabProcedure(id);
		
		assertEquals("Testing", procedures.getStatus());
	}
}
