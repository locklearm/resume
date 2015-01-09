package old;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.admin.ISVNTreeHandler;
import org.tmatesoft.svn.core.wc.admin.SVNAdminPath;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;



public class FBWorkingDirectoryManager {
	
	private File faultBenchDirectory;
	
	private File workingCopy;
	private File svnRepo;
	//Should NOT include the full URL, and must start with a slash
	private String checkoutPath;
	
	private File generatedSourceDirectory;
	private File classDirectory;
	
	
	private long currentRevision = -1;
	
	private List<FBSourceFile> sourceFiles;
	
	private SVNClientManager svnClientManager = SVNClientManager.newInstance();
	
	public FBWorkingDirectoryManager(File faultBenchDirectory, File workingCopy, File svnRepo, String checkoutPath, File classDirectory) {
		this.faultBenchDirectory = faultBenchDirectory;
		this.workingCopy = workingCopy;
		this.svnRepo = svnRepo;
		this.checkoutPath = checkoutPath;
		this.classDirectory = classDirectory;
	}
	
	public boolean checkoutRevision(long revisionID) {
		
		//First clean the working directory of any unversioned files
		try {
			this.cleanWorkingCopy();
		} catch (SVNException e) {
			this.currentRevision = -1;
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		//Then do the checkout
		SVNUpdateClient updateClient = this.svnClientManager.getUpdateClient();
		//If this is the first, then do a checkout
		if (this.currentRevision == -1) {
			try {
				updateClient.doCheckout(SVNURL.fromFile(this.svnRepo), 
				                        this.workingCopy, 
				                        SVNRevision.create(revisionID), 
				                        SVNRevision.create(revisionID), 
				                        SVNDepth.INFINITY, 
				                        false);
			} catch (SVNException e) {
				this.currentRevision = -1;
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		else {
			try {
				updateClient.doUpdate(this.workingCopy, SVNRevision.create(revisionID), SVNDepth.INFINITY, false, true);
			} catch (SVNException e) {
				this.currentRevision = -1;
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		
		
		//This is where we collect source files
		
		
		
		this.currentRevision = revisionID;
		
		return true;
		
	}
	
	public boolean collectSourceFiles() {
		
		SVNLookClient lookClient = this.svnClientManager.getLookClient();
		
		class TreeHandler implements ISVNTreeHandler {

			@Override
			public void handlePath(SVNAdminPath adminPath) throws SVNException {
				
				System.out.println(adminPath.getNodeID() + "\t" + adminPath.getPath() + "\t" + adminPath.getTreeDepth() + "\t" + adminPath.isDir());
				
			}
			
		}
		
		TreeHandler treeHandler = new TreeHandler();
		
		try {
			lookClient.doGetTree(this.svnRepo, this.checkoutPath, SVNRevision.create(this.currentRevision), true, true, treeHandler);
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		
		
		return true;
		
	}
	
 	public void cleanWorkingCopy() throws SVNException {
		
		if (this.currentRevision == -1) {
			FileUtils.deleteQuietly(this.workingCopy);
			this.workingCopy.mkdirs();
			return;
		}
		
		SVNStatusClient statusClient = this.svnClientManager.getStatusClient();
		statusClient.setIgnoreExternals(true);
		class StatusHandler implements ISVNStatusHandler{

			public List<SVNStatus> statusItems = new ArrayList<SVNStatus>();
			
			@Override
			public void handleStatus(SVNStatus status) throws SVNException {
				
				this.statusItems.add(status);
				
			}
			
		}
		StatusHandler statusHandler = new StatusHandler();
		statusClient.doStatus(this.workingCopy, SVNRevision.create(this.currentRevision), SVNDepth.INFINITY, false, true, true, false, statusHandler, null);
		
		for (SVNStatus status : statusHandler.statusItems) {
			//TODO, look for modified files as well
			if (!status.isVersioned()) {
				status.getFile().delete();
			}
			
		}
		
	}
	
	public boolean build(List<String> buildCommands) throws ExecuteException, IOException {
		
		//Execute each command
		for (String buildCommand : buildCommands) {
			
			CommandLine cmdLine = CommandLine.parse(buildCommand);
			DefaultExecutor executor = new DefaultExecutor();
			int exitValue = executor.execute(cmdLine);
			if (exitValue != 0) {
				return false;
			}
		}
		
		return true;
		
	}

	
	public File getClassDirectory() {
		return this.classDirectory;
	}
	
	public File generateSourceDirectory() throws IOException, SVNException {
		
		//Make the reference to the source directory
		this.generatedSourceDirectory = new File(this.faultBenchDirectory, "sourceDir");
		
		//If it already existed, delete it and everything in it
		if (this.generatedSourceDirectory.exists()) {
			FileUtils.forceDelete(this.generatedSourceDirectory);
		}
		
		//Make the directory
		this.generatedSourceDirectory.mkdirs();
		
		//For each source file, make the appropriate directory path, based on the package, and then copy the file into it
		for (FBSourceFile sourceFile : this.sourceFiles) {
			
			//Separate the package structure
			String[] names = sourceFile.topLevelClass.split("\\.");
			
			//Build the file path
			String path = "";
			for (int j = 0; j < names.length; j++) {
				path += File.separator + names[j];
			}
			
			//Make a reference to the file
			File f = new File(this.generatedSourceDirectory, path);
			
			//Copy the file into its new location (making any directories needed)
			String origPath = sourceFile.getCheckoutPath();
			if (origPath.startsWith(this.checkoutPath)) {
				origPath = origPath.substring(this.checkoutPath.length());
			}
			FileUtils.copyFile(new File(this.workingCopy, origPath), f, true);
			
		}
		
		return this.generatedSourceDirectory;
	}
	
	
	
	public static void main(String[] args) {
		
		File fbDir = new File("F:\\reallyDeleteMe");
		File workCopyDir = new File(fbDir, "\\workingCopy");
		File svnRepo = new File("F:\\bigbluebutton");
		String coPath = "/";
		File classDir = workCopyDir;
		
		
		FBWorkingDirectoryManager me = new FBWorkingDirectoryManager(fbDir, workCopyDir, svnRepo, coPath, classDir);
		
		me.currentRevision = 5;
		me.collectSourceFiles();
		
	}
	
}
