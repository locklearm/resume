package datamodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.garret.perst.Index;
import org.garret.perst.IterableIterator;
import org.garret.perst.Key;
import org.garret.perst.Link;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;
import org.tmatesoft.svn.core.SVNException;


public class FBAnalysisProject extends Persistent {
	
	public String projectName;
	public FBRepository repo;
	public Index<FBHistoryNode> historyNodes;
	public Index<FBClassVersion> classes;
	Link<FBBuildProcess> buildProcs;
	
	public FBAnalysisProject(Storage db, String projectName, String repositoryLocation) {
		super(db);
		
		this.projectName = projectName;
		try {
			this.repo = new FBRepository(db, this, repositoryLocation);
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Couldn't connect to repository");
			System.exit(1);
		}
		this.historyNodes = db.<FBHistoryNode>createIndex(new Class[] {long.class, String.class, String.class}, true);
		this.classes = db.<FBClassVersion>createIndex(new Class[] {String.class, String.class, FBHistoryNode.class}, true);
		this.buildProcs = db.<FBBuildProcess>createLink();
		
	}
	public FBAnalysisProject() {}
	
	public FBHistoryNode getHistoryNode(String nodeID, String branchID, long revisionID) {
		
		Key k = new Key(new Object[] {revisionID, branchID, nodeID});
		
		FBHistoryNode found = this.historyNodes.get(k);
		
		if (found == null) {
			return null;
		}
		
		found.nodeID = nodeID;
		found.branchID = branchID;
		found.revisionID = revisionID;
		return found;
		
		
	}
	
	public void indexHistoryNode(FBHistoryNode node) {
		
		Key k = new Key(new Object[] {node.revisionID, node.branchID, node.nodeID});
		this.historyNodes.put(k, node);
		
		
	}
	
	public void addBuildProcess(FBBuildProcess bp) {
		this.buildProcs.add(bp);
	}
	
	public FBClassVersion getClassVersion(String packageName, String className, FBHistoryNode node) {
		
		Key k = new Key(new Object[] {packageName, className, node});
		FBClassVersion found = this.classes.get(k);
		
		if (found == null) {
			return null;
		}
		
		found.historyNode = node;
		found.className = className;
		found.packageName = packageName;
		
		return found;
		
	}
	
	public void indexClassVersion(FBClassVersion classV) {
		
		Key k = new Key(new Object[] {classV.packageName, classV.className, classV.historyNode});
		this.classes.put(k, classV);
	}
	
	public Map<String, FBClassVersion> getNodeClasses(FBHistoryNode node) {
		
		
		//FIXME Might have to rework this, since this will be an expensive operation.
		//Also, it might not work at all
		
		IterableIterator<Entry<Object, FBClassVersion>> entryIter = this.classes.entryIterator();
		Map<String, FBClassVersion> classes = new HashMap<String, FBClassVersion>();
		while (entryIter.hasNext()) {
			
			Entry<Object, FBClassVersion> entry = entryIter.next();
			Object[] key = (Object[]) entry.getKey();
			String packageName = (String) key[0];
			String className = (String) key[1];
			FBHistoryNode histNode = (FBHistoryNode) key[2];
			
			if (node.getOid() == histNode.getOid()) {
				FBClassVersion clazz = entry.getValue();
				clazz.className = className;
				clazz.packageName = packageName;
				clazz.historyNode = node;
				classes.put(className, clazz);
			}
			
			
		}
		
		return classes;
		
	}
	
}
