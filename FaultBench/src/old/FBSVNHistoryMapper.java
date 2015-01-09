package old;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.garret.perst.*;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.ISvnObjectReceiver;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;



public class FBSVNHistoryMapper {
	
	
	
	public static void main(String[] args) throws SVNException {
		
		String databaseFile = "test3.dbs";
		long pagePoolSize = 52428800l;
		String repoLocation = "http://bigbluebutton.googlecode.com/svn/";
		String projectDir = "";
		
		
		
		
		Storage db = StorageFactory.getInstance().createStorage();
		db.setProperty("perst.string.encoding", "UTF-8");
		db.open(new CompressedReadWriteFile(databaseFile), pagePoolSize);
		FBDBRootObject dbro = (FBDBRootObject) db.getRoot();
		if (dbro == null) {
			dbro = new FBDBRootObject(db);
			db.setRoot(dbro);
		}
		
		
		
		FBSVNHistoryMapper me = new FBSVNHistoryMapper();
		List<SVNLogEntry> logEntries = me.getLogs(repoLocation + projectDir);
		me.buildMap(db, logEntries, projectDir);
		
		
		
		
//		try {
//			db.exportXML(new PrintWriter(new File("out.xml")));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		db.close();
		
		
		
		
	}
	
	
	public void buildMap(Storage db, List<SVNLogEntry> logEntries, String projDir) {
		
		
		
		FBHistoryVersion prev = null;
		for (SVNLogEntry logEntry : logEntries) {
			
			if (logEntry.getRevision() == -1) {
				continue;
			}
			
			
			System.out.println("Processing revision: " + logEntry.getRevision());
			
			FBHistoryVersion cur = null;
			if (prev == null) {
				//First, create the versions
				cur = new FBHistoryVersion(db, logEntry.getRevision(), projDir);
				
			}
			else {
				cur = prev.makeNext(db, logEntry.getRevision(), projDir);
			}
			
			
			//Now create the nodes
			for (SVNLogEntryPath p : logEntry.getChangedPaths().values()) {
				
				cur.addNode(db, 
				            FBModType.parseString("" + p.getType()), 
				            FBNodeKind.parseString(p.getKind().toString()), 
				            p.getPath(), 
				            (p.getCopyPath() == null) ? p.getPath() : p.getCopyPath(), 
				            (p.getCopyRevision() == -1) ? prev.revisionID : p.getCopyRevision(),
				             (p.getCopyPath()) == null ? false : true);
				
				
			}
			
			
//			System.out.println("\n\nRevision: " + cur.revisionID);
//			Iterator<FBHistoryNode> nodeIter = cur.nodes.iterator();
//			while (nodeIter.hasNext()) {
//				System.out.println(nodeIter.next().path);
//			}
			
			
			prev = cur;
			
		}
		
	}
	

	public List<SVNLogEntry> getLogs(String svnUrl) throws SVNException {
		
		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		
		final SvnLog log = svnOperationFactory.createLog();
		log.setSingleTarget(SvnTarget.fromURL(SVNURL.parseURIEncoded(svnUrl)));
		log.setLimit(0);
		log.setStopOnCopy(false);
		log.setUseMergeHistory(true);
		log.setDiscoverChangedPaths(true);
		log.setDepth(SVNDepth.INFINITY);
		log.addRange(SvnRevisionRange.create(SVNRevision.create(0), SVNRevision.HEAD));
		List<SVNLogEntry> svnLogEntries = new ArrayList<SVNLogEntry>();
		log.run(svnLogEntries);
		
		return svnLogEntries;
	}
	
}
