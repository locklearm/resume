package edu.ncsu.csc.itrust.bean;

import static org.junit.Assert.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.easymock.classextension.IMocksControl;
import org.junit.Test;
import edu.ncsu.csc.itrust.beans.WardBean;
import edu.ncsu.csc.itrust.beans.WardRoomBean;
import edu.ncsu.csc.itrust.beans.loaders.WardBeanLoader;
import edu.ncsu.csc.itrust.beans.loaders.WardRoomBeanLoader;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createControl;

public class WardRoomBeanLoaderTest extends TestCase{
	
	private IMocksControl ctrl;
	java.util.List<WardRoomBean> list = new ArrayList<WardRoomBean>();
	ResultSet rs;
	WardRoomBeanLoader wbl = new WardRoomBeanLoader();
	
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
			expect(rs.getLong("RoomID")).andReturn(1l).once();
			expect(rs.getLong("OccupiedBy")).andReturn(1l).once();
			expect(rs.getLong("InWard")).andReturn(1l).once();
			expect(rs.getString("roomName")).andReturn("CleanRoom").once();
			expect(rs.getString("Status")).andReturn("Clean").once();
			ctrl.replay();
	
			wbl.loadSingle(rs);
		} catch (SQLException e) {}
	}
	
	public void testLoadParameters(){
		try{
			wbl.loadParameters(null,null);
			fail();
		} catch(Exception e) {}
		
		assertTrue(true);
	}

}
