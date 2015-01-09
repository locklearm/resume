package analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSID;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.admin.ISVNTreeHandler;
import org.tmatesoft.svn.core.wc.admin.SVNAdminPath;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;

import datamodel.FBBuildCommand;
import datamodel.FBBuildProcess;
import datamodel.FBHistoryNode;
import datamodel.FBRepository;


public class FBWorkingDirectoryManager {
	
	private File faultBenchDirectory;
	private File workingCopyDirectory;
	private FBRepository repo;
	private File generatedSourceDirectory = null;
	private String checkoutPath;
	private long currentRevisionID = -1;
	public FBBuildProcess currentBuildProcess;
	private boolean isPrepared = false;
	public Map<String, FBHistoryNode> sourceNodes = new HashMap<String, FBHistoryNode>();
	private SVNClientManager svnClientManager;
	
	public FBWorkingDirectoryManager(File faultBenchDirectory, String workingCopyPath, FBRepository repo, String checkoutPath) {
		this.faultBenchDirectory = faultBenchDirectory;
		this.workingCopyDirectory = new File(this.faultBenchDirectory, workingCopyPath);
		this.repo = repo;
		this.checkoutPath = checkoutPath;
		this.svnClientManager = SVNClientManager.newInstance();
		this.setup();
	}
	
	public String getSourceDirectoryPath() {
		try {
			return this.generatedSourceDirectory.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getClassDirectoryPath() {
		
		try {
			return this.workingCopyDirectory.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		return null;
	}
	
	public String getCheckoutPath() {
		return this.checkoutPath;
	}
	
	private void setup() {
		
		//Create the workspace, if it doesn't already exist
		if (!this.faultBenchDirectory.exists()) {
			this.faultBenchDirectory.mkdir();
		}
		
		//Delete the given working copy directory, and anything that's currently in it
		FileUtils.deleteQuietly(this.workingCopyDirectory);
		
		//Now make it again, clean like
		this.workingCopyDirectory.mkdir();
		
	}
	
	
	public boolean prepareRevision(long revisionID, FBBuildProcess buildP) {
		
		this.isPrepared = false;
		
		if (!this.checkoutRevision(revisionID)) {
			return false;
		}
		
		if (!this.collectSourceFiles()) {
			return false;
		}
		
		if (!this.generateSourceDirectory()) {
			return false;
		}
		
		if (!this.buildVersion(buildP)) {
			return false;
		}
		
		this.currentBuildProcess = buildP;
		this.isPrepared = true;
		
		return true;
		
	}
	
	private boolean generateSourceDirectory() {
		
		//Make the reference to the source directory
		this.generatedSourceDirectory = new File(this.faultBenchDirectory, "tempSourceDir");
		
		//If it already existed, delete it and everything in it
		if (this.generatedSourceDirectory.exists()) {
			FileUtils.deleteQuietly(this.generatedSourceDirectory);
		}
		
		//Make the directory
		this.generatedSourceDirectory.mkdirs();
		
		//For each source file, make the appropriate directory path, based on the top level class (and package) and then copy the file into it
		for (Entry<String, FBHistoryNode> entry : this.sourceNodes.entrySet()) {
			
			//Separate the package structure
			String[] names = entry.getValue().getTopLevelClass(this.repo.aProj).packageName.split("\\.");
			
			//Build the directory path
			String path = "";
			for (int i = 0; i < names.length; i++) {
				
				path += File.separator + names[i];
				
			}
			
			//Get the fileName and add it to the path
			String curPath = entry.getKey();
			String fileName = curPath.substring(curPath.lastIndexOf('/') + 1);
			path += File.separator + fileName;
			
			//Get the path to this file in the current working copy
			String workPath = curPath.substring(this.checkoutPath.length());
			
			//Copy the file into the new directory
			try {
				FileUtils.copyFile(new File(this.workingCopyDirectory, workPath), new File(this.generatedSourceDirectory, path), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("problem copying file");
				return false;
			}
			
		}
		
		return true;
		
	}
	
	private boolean collectSourceFiles() {
		
		SVNLookClient lookClient = this.svnClientManager.getLookClient();
		
		class TreeHandler implements ISVNTreeHandler {
			
			public FBRepository repo;
			public Map<String,FBHistoryNode> sourceNodes = new HashMap<String, FBHistoryNode>();
			
			public TreeHandler(FBRepository repo) {
				this.repo = repo;
			}

			@Override
			public void handlePath(SVNAdminPath adminPath) throws SVNException {
				
				//If this isn't a java source file, and it's not on the path we want, we ignore it
				if (adminPath.getPath() == null
						|| !adminPath.getPath().toLowerCase().endsWith(".java")
						|| !adminPath.getPath().toLowerCase().startsWith(checkoutPath)) {
					return;
				}
				
				//Get the corresponding history node from the db
				FSID fsid = FSID.fromString(adminPath.getNodeID());
				FBHistoryNode found = this.repo.aProj.getHistoryNode(fsid.getNodeID(), fsid.getCopyID(), fsid.getRevision());
				
				//If we didn't find it, make it
				if (found == null) {
					found = this.repo.makeHistoryNode(fsid);
				}
				
				//Finally, add it to the list
				this.sourceNodes.put(adminPath.getPath(), found);
				
				
			}
			
		}
		
		TreeHandler treeHandler = new TreeHandler(this.repo);
		
		//FIXME This is an expensive operation.  It would be great to find another way to do things
		try {
			lookClient.doGetTree(new File(this.repo.repositoryLocation), this.checkoutPath, SVNRevision.create(this.currentRevisionID), true, true, treeHandler);
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Couldn't build tree");
			return false;
		}
		
		
		//Save the source nodes
		this.sourceNodes = treeHandler.sourceNodes;
		
		return true;
		
	}
	
	private boolean buildVersion(FBBuildProcess buildP) {
		
		//Execute each command
		for (FBBuildCommand bc : buildP.buildCommands) {
			
			CommandLine cmdLine = new CommandLine(bc.executable);
			for (String arg : bc.arguments) {
				cmdLine.addArgument(arg);
			}
			DefaultExecutor executor = new DefaultExecutor();
			executor.setWorkingDirectory(workingCopyDirectory);
			int exitValue;
			try {
				exitValue = executor.execute(cmdLine);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Problem building");
				return false;
			}
			if (executor.isFailure(exitValue)) {
				return false;
			}
			
		}
		
		return true;
		
	}
	
	private boolean checkoutRevision(long revisionID) {
		
		//First clean the working directory
		if (!this.removeUnversionedFiles()) {
			//If there was a problem, mark everything for reset
			this.currentRevisionID = -1;
			return false;
		}
		
		//Then we do the checkout
		SVNUpdateClient updateClient = this.svnClientManager.getUpdateClient();
		
		//If this is the first time, then do a full checkout
		if (this.currentRevisionID == -1) {
			
			try {
				updateClient.doCheckout(SVNURL.fromFile(new File(this.repo.repositoryLocation)).appendPath(this.checkoutPath, true), 
				                        this.workingCopyDirectory, 
				                        SVNRevision.create(revisionID), 
				                        SVNRevision.create(revisionID), 
				                        SVNDepth.INFINITY, 
				                        false);
			} catch (SVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Problem doing checkout");
				this.currentRevisionID = -1;
				return false;
			}
			
		}
		//Otherwise, just do an update
		else {
			try {
				updateClient.doUpdate(this.workingCopyDirectory, 
				                      SVNRevision.create(revisionID), 
				                      SVNDepth.INFINITY, 
				                      false, 
				                      true);
			} catch (SVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Problem doing update");
				this.currentRevisionID = -1;
				return false;
			}
		}
		
		this.currentRevisionID = revisionID;
		
		return true;
		
	}
	
	private boolean removeUnversionedFiles() {
		
		if (this.currentRevisionID == -1) {
			this.setup();
			return true;
		}
		
		SVNStatusClient statusClient = this.svnClientManager.getStatusClient();
		statusClient.setIgnoreExternals(true);  //TODO Do we need this?
		
		class StatusHandler implements ISVNStatusHandler {

			public List<SVNStatus> unversionedItems = new ArrayList<SVNStatus>();
			
			@Override
			public void handleStatus(SVNStatus status) throws SVNException {
				//TODO Make sure this catches everything
				if (!status.isVersioned()) {
					this.unversionedItems.add(status);
				}
				
			}
			
		}
		
		StatusHandler statusHandler = new StatusHandler();
		try {
			statusClient.doStatus(this.workingCopyDirectory, SVNRevision.create(this.currentRevisionID), SVNDepth.INFINITY, false, true, true, false, statusHandler, null);
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		for (SVNStatus status : statusHandler.unversionedItems) {
			
			if (status.getFile().exists()) {
				FileUtils.deleteQuietly(status.getFile());
			}
			
		}
		
		return true;
		
		
	}
	
}
