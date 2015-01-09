package analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import datamodel.FBFindBugsAlertVersion;
import datamodel.FBHistoryNode;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporterObserver;
import edu.umd.cs.findbugs.FindBugs;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.TextUICommandLine;
import edu.umd.cs.findbugs.ba.SourceFinder;
import edu.umd.cs.findbugs.filter.FilterException;


public class FBFindBugsBugCollector {
	
	public static boolean collectBugs(FBWorkingDirectoryManager wdm) {
		
		//We assume that everything has been prepared already
		
		
		String sourcePath = wdm.getSourceDirectoryPath();
		String classPath = wdm.getClassDirectoryPath();
		if (sourcePath == null || classPath == null) {
			return false;
		}
		
		class FBBugReporterObserver implements BugReporterObserver {

			public List<BugInstance> bugs = new ArrayList<BugInstance>();
			
			@Override
			public void reportBug(BugInstance bug) {
				this.bugs.add(bug);
			}
			
		}
		FBBugReporterObserver bro = new FBBugReporterObserver();
		
		FindBugs2 findBugsEngine = new FindBugs2();
		TextUICommandLine cmdLine = new TextUICommandLine();
		String[] cmdArgs = {"-effort:max", "-low", "-quiet:true", "-sourcepath", sourcePath, classPath};
		
		try {
			FindBugs.processCommandLine(cmdLine, cmdArgs, findBugsEngine);
			findBugsEngine.getBugReporter().addObserver(bro);
			findBugsEngine.execute();
			findBugsEngine.getBugReporter().finish();
		} catch (FilterException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Problem running findbugs");
		}
		
		//TESTING
		SourceFinder sf = findBugsEngine.getProject().getSourceFinder();
		for (BugInstance bug : bro.bugs) {
			String foundSource = null;
			try {
				foundSource = sf.findSourceFile(bug.getPrimarySourceLineAnnotation()).getFullFileName();
				//Strip the source directory path, and add in the checkout path
				foundSource = foundSource.substring(wdm.getSourceDirectoryPath().length());
				foundSource = wdm.getCheckoutPath() + foundSource;
				foundSource = foundSource.replace('\\', '/');
				
				//XXX The sources found here are on the paths that we created.  Need to keep track of their old locations somewhere
				
//				//Find the correct history node
//				FBHistoryNode node = null;
//				for (Entry<String, FBHistoryNode> entry : wdm.sourceNodes.entrySet()) {
//					if (entry.getKey().endsWith(suffix))
//				}
//				
//				//Create the bug
//				FBFindBugsAlertVersion alert = new FBFindBugsAlertVersion(node, bug.getPrimarySourceLineAnnotation().getStartLine(), bug.getInstanceHash(), bug.getType(), bug.getCategoryAbbrev(), bug.getPriority(), wdm.currentBuildProcess);
//				node.alerts.add(alert);
//				node.bugsCollected = true;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Problem finding source file");
			}
			
		}
		
		
		return true;
		
	}
	
}
