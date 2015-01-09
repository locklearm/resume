package edu.ncsu.csc.itrust.bean;

import static org.junit.Assert.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.easymock.classextension.IMocksControl;
import org.junit.Test;
import edu.ncsu.csc.itrust.beans.WardBean;
import edu.ncsu.csc.itrust.beans.loaders.WardBeanLoader;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createControl;

public class WardBeanLoaderTest extends TestCase{
	
	private IMocksControl ctrl;
	java.util.List<WardBean> list = new ArrayList<WardBean>();
	ResultSet rs;
	WardBeanLoader wbl = new WardBeanLoader();
	
	protected void setUp() throws Exception {
		ctrl = createControl();
		rs = ctrl.createMock(ResultSet.class);
	}
	
	@Test
	public void testLoadList() {
		try {
			list = wbl.loadList(rs);
		} catch (SQLException e) {}
		
		assertEquals(0,list.size());
	}
	
	public void testloadSingle(){
		try {
			expect(rs.getLong("WardID")).andReturn(1l).once();
			expect(rs.getString("RequiredSpecialty")).andReturn("specialty").once();
			expect(rs.getLong("InHospital")).andReturn(1l).once();
	
			ctrl.replay();
	
			wbl.loadSingle(rs);
		} catch (SQLException e) {}
	}
	
	public void testLoadParameters(){
		try{
			wbl.loadParameters(null,null);
			fail();
		} catch(IllegalStateException e){} catch (SQLException e) {}
		
		assertTrue(true);
	}

}
