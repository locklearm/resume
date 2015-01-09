package old;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;


public class FBBuildCommandHelper {
	
	
	public static void findBuildTargets(long startRevision, long endRevision, File repo, String pathInRepo) throws SVNException, IOException {
		
		class BuildTarget {
			public String path;
			public long revisionID;
			public List<String> targets = new ArrayList<String>();
			public List<String> directories = new ArrayList<String>();
			
			public String toString() {
				
				String out = "";
				out += revisionID + "\t" + path;
				for (String t : targets) {
					out += "\n\t" + t;
				}
				for (String d : directories) {
					out += "\n\t" + d;
				}
				return out;
			}
		}
		
		
		SVNClientManager clientManager = SVNClientManager.newInstance();
		
		SVNLogClient logClient = clientManager.getLogClient();
		List<BuildTarget> antBuilds = new ArrayList<BuildTarget>();
		List<BuildTarget> mavenBuilds = new ArrayList<BuildTarget>();
		class LogHandler implements ISVNLogEntryHandler	 {

			List<BuildTarget> antBuilds = new ArrayList<BuildTarget>();
			List<BuildTarget> mavenBuilds = new ArrayList<BuildTarget>();
			
			@Override
			public void handleLogEntry(SVNLogEntry arg0) throws SVNException {
				
				for (SVNLogEntryPath logEPath : arg0.getChangedPaths().values()) {
					
					if (logEPath.getType() == SVNLogEntryPath.TYPE_DELETED) {
						continue;
					}
					
					String path = logEPath.getPath();
					
					if (path.toLowerCase().endsWith("build.xml")) {
						BuildTarget newT = new BuildTarget();
						newT.path = path;
						newT.revisionID = arg0.getRevision();
						this.antBuilds.add(newT);
					}
					else if (path.toLowerCase().endsWith("pom.xml")) {
						BuildTarget newT = new BuildTarget();
						newT.path = path;
						newT.revisionID = arg0.getRevision();
						this.mavenBuilds.add(newT);
					}
				}
				
				
			}
			
		}
		LogHandler logHandler = new LogHandler();
		logClient.doLog(SVNURL.fromFile(repo), new String[]{}, SVNRevision.create(endRevision), SVNRevision.create(startRevision), SVNRevision.create(endRevision), false, true, 0, logHandler);
		
		
		SVNLookClient lookClient = clientManager.getLookClient();
		for (BuildTarget bt : logHandler.antBuilds) {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			lookClient.doCat(repo, bt.path, SVNRevision.create(bt.revisionID), baos);
			
			String contents = baos.toString();
//			contents = contents.replace('\n', ' ');
//			contents = contents.replace('\r', ' ');
//			contents = contents.replace('\t', ' ');
			contents = contents.replaceAll("\\s+", " ");
			
			Pattern targetPattern = Pattern.compile("<target.*?>");
			Matcher targetMatcher = targetPattern.matcher(contents);
			while (targetMatcher.find()) {
				bt.targets.add(targetMatcher.group());
			}
			
			Pattern directoryPatter = Pattern.compile("dir=\".*?\"");
			Matcher directoryMatcher = directoryPatter.matcher(contents);
			while (directoryMatcher.find()) {
				bt.directories.add(directoryMatcher.group());
			}
			
			baos.close();
		}
		
		System.out.println("___________Ant Builds Found______________");
		for (BuildTarget bt : logHandler.antBuilds) {
			System.out.println(bt.toString());
		}
		System.out.println();
		System.out.println("___________Maven Builds Found____________");
		for (BuildTarget bt : logHandler.mavenBuilds) {
			System.out.println(bt.toString());
		}
		
	}
	
	public static void main(String[] args) {
		
		long startRevision = 0;
		long endRevision = 522; 
		File repo = new File("F:\\epubcheck"); 
		String pathInRepo = "";
		
		try {
			findBuildTargets(startRevision, endRevision, repo, pathInRepo);
		} catch (SVNException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
