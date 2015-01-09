package datamodel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.garret.perst.Key;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.delta.SVNDeltaCombiner;
import org.tmatesoft.svn.core.internal.io.fs.FSFS;
import org.tmatesoft.svn.core.internal.io.fs.FSID;
import org.tmatesoft.svn.core.internal.io.fs.FSInputStream;
import org.tmatesoft.svn.core.internal.io.fs.FSNodeHistory;
import org.tmatesoft.svn.core.internal.io.fs.FSRevisionNode;
import org.tmatesoft.svn.core.internal.io.fs.FSRevisionRoot;
import org.tmatesoft.svn.core.internal.wc.SVNAdminHelper;

import enums.FBNodeType;

public class FBRepository extends Persistent {

	public FBAnalysisProject	aProj;
	public String				repositoryLocation;
	public String				uuid;

	private transient FSFS		fsfs;

	public FBRepository(Storage db, FBAnalysisProject aProj, String repositoryLocation) throws SVNException {
		super(db);
		this.aProj = aProj;
		this.repositoryLocation = repositoryLocation;

		this.initialize();
	}

	public FBRepository() {
	}

	public void onLoad() {

		// TODO this craziness is bad

		try {
			this.initialize();
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Could not connect to the repository");
			System.exit(1);
		}
	}

	public void initialize() throws SVNException {
		this.connectToRepository();
		this.setUUID();
	}

	private void connectToRepository() throws SVNException {

		this.fsfs = SVNAdminHelper.openRepository(new File(this.repositoryLocation), true);

	}

	private void setUUID() throws SVNException {

		if (this.uuid == null) {
			this.uuid = this.fsfs.getUUID();
		}

	}

	public List<Long> findInterestingRevisions(String checkoutPath, long startRevisionID, long endRevisionID) {

		List<Long> interestingRevs = new ArrayList<Long>();

		try {
			FSRevisionRoot root = this.fsfs.createRevisionRoot(endRevisionID);
			FSNodeHistory history = root.getNodeHistory(checkoutPath);
			boolean crossCopies = true;
			while (history != null) {
				if (history.getHistoryEntry().getRevision() < startRevisionID) {
					break;
				}
				interestingRevs.add(history.getHistoryEntry().getRevision());
				history = history.getPreviousHistory(crossCopies);
			}
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Could not connect to repository");
			System.exit(1);
		}

		return interestingRevs;
	}

	public String getCheckoutPath(FBHistoryNode node) {

		FSID fsid = FSID.createRevId(node.nodeID, node.branchID, node.revisionID, node.offset);
		try {
			FSRevisionNode revNode = this.fsfs.getRevisionNode(fsid);
			return revNode.getCreatedPath();
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Could not connect to repository");
			System.exit(0);
		}

		return null;

	}

	public FBHistoryNode getPreviousNode(FBHistoryNode node) {

		FSID fsid = FSID.createRevId(node.nodeID, node.branchID, node.revisionID, node.offset);
		FSID prev = null;
		try {
			FSRevisionNode revNode = this.fsfs.getRevisionNode(fsid);
			prev = revNode.getPredecessorId();
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Could not connect to repository");
			System.exit(0);
		}

		Key k = new Key(new Object[] { prev.getRevision(), prev.getCopyID(), prev.getNodeID() });
		return this.aProj.historyNodes.get(k);

	}

	public String getFileContents(FBHistoryNode node) throws SVNException, IOException {

		if (node.type != FBNodeType.FILE) { return null; }

		FSID fsid = FSID.createRevId(node.nodeID, node.branchID, node.revisionID, node.offset);
		FSRevisionNode revNode = this.fsfs.getRevisionNode(fsid);
		return IOUtils.toString(FSInputStream.createDeltaStream(new SVNDeltaCombiner(), revNode, this.fsfs));

	}

	public FBHistoryNode makeHistoryNode(FSID fsid) {

		FBHistoryNode newNode = null;
		try {

			FSRevisionNode revNode = this.fsfs.getRevisionNode(fsid);

			newNode = new FBHistoryNode(getStorage(),
										fsid.getNodeID(),
										fsid.getCopyID(),
										fsid.getRevision(),
										fsid.getOffset(),
										FBNodeType.parseString(revNode.getType().toString()));

		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Could not connect to repository");
			System.exit(1);
		}
		
		//Go ahead and index it
		this.aProj.indexHistoryNode(newNode);
		
		return newNode;

	}
}
