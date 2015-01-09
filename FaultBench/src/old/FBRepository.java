package old;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.garret.perst.Persistent;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.delta.SVNDeltaCombiner;
import org.tmatesoft.svn.core.internal.io.fs.FSFS;
import org.tmatesoft.svn.core.internal.io.fs.FSID;
import org.tmatesoft.svn.core.internal.io.fs.FSInputStream;
import org.tmatesoft.svn.core.internal.io.fs.FSRevisionNode;
import org.tmatesoft.svn.core.internal.wc.SVNAdminHelper;



public class FBRepository extends Persistent {
	
	public String repoPath;
	
	public String getNodePath(FBHistoryNode historyNode) throws SVNException {
		
		FSFS fsfs = SVNAdminHelper.openRepository(new File(this.repoPath), true);
		FSID nodeID = FSID.createRevId(historyNode.nodeID, historyNode.branchID, historyNode.revisionID, historyNode.offset);
		FSRevisionNode revNode = fsfs.getRevisionNode(nodeID);
		
		return revNode.getCreatedPath();
		
	}
	
	public String getFileNodeContents(FBHistoryNode historyNode) throws SVNException, IOException {
		
		FSFS fsfs = SVNAdminHelper.openRepository(new File(this.repoPath), true);
		FSID nodeID = FSID.createRevId(historyNode.nodeID, historyNode.branchID, historyNode.revisionID, historyNode.offset);
		FSRevisionNode revNode = fsfs.getRevisionNode(nodeID);
		
		InputStream inFile = FSInputStream.createDeltaStream(new SVNDeltaCombiner(), revNode, fsfs);
		return IOUtils.toString(inFile);
		
	}
	
}
