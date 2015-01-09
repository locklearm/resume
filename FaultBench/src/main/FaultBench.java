package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.garret.perst.CompressedReadWriteFile;
import org.garret.perst.Storage;
import org.garret.perst.StorageFactory;

import analysis.FBFindBugsBugCollector;
import analysis.FBWorkingDirectoryManager;
import datamodel.FBAnalysisProject;
import datamodel.FBBuildCommand;
import datamodel.FBBuildProcess;
import datamodel.FBDatabaseRootObject;

public class FaultBench {

	public static File faultBenchDirectory;
	
	public static void main(String[] args) {

		// TODO This is information that we should collect from elsewhere
		String projectName = "martinTest";
		String repoLocation = "F:\\epubcheck";
		String workspace = "F:\\faultBenchWorkspace";
		String databaseFile = "fbtest.dbs";
		long pagePoolSize = 52428800l;

		//Mark the workspace
		faultBenchDirectory = new File(workspace);
		
		// Setup the database
		Storage db = StorageFactory.getInstance().createStorage();
		db.setProperty("perst.string.encoding", "UTF-8");
		db.open(new CompressedReadWriteFile(databaseFile), pagePoolSize);
		FBDatabaseRootObject dbro = (FBDatabaseRootObject) db.getRoot();
		if (dbro == null) {
			dbro = new FBDatabaseRootObject(db);
			db.setRoot(dbro);
		}

		// Sample information for a build process TODO get this from elsewhere
		String checkoutPath = "/trunk";
		long startRevisionID = 515;
		long endRevisionID = 520;
		List<FBBuildCommand> buildCommands = new ArrayList<FBBuildCommand>();
		List<String> bcargs = new ArrayList<String>();
		bcargs.add("compile");
		FBBuildCommand bc = new FBBuildCommand(db, "C:\\Users\\Martin\\Downloads\\Installers\\apache-maven-3.2.1-bin\\apache-maven-3.2.1\\bin\\mvn.bat", bcargs);
		buildCommands.add(bc);
		//FIXME Actually use these
		List<String> classPaths = new ArrayList<String>();
		classPaths.add("target/classes");
		List<String> sourcePaths = new ArrayList<String>();
		sourcePaths.add("/src/main/java");
		FBBuildProcess buildProcess = new FBBuildProcess(	db,
															checkoutPath,
															startRevisionID,
															endRevisionID,
															buildCommands,
															classPaths,
															sourcePaths);
		List<FBBuildProcess> buildProcesses = new ArrayList<FBBuildProcess>();
		buildProcesses.add(buildProcess);
		
		//Get the correct project
		FBAnalysisProject aProj = dbro.getProject(projectName);
		
		//If it doesn't already exist, we make it
		if (aProj == null) {
			aProj = new FBAnalysisProject(db, projectName, repoLocation);
			dbro.addProject(aProj);
		}
		
		
		//Add the build processes to the project
		for (FBBuildProcess bp : buildProcesses) {
			aProj.addBuildProcess(bp);
		}
		
		//And we're off!
		collectBugs(aProj, buildProcesses);
		
		
		try {
			db.exportXML(new PrintWriter(new File("out.xml")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Close the database
		db.close();
		
		
		
	}
	
	public static void collectBugs(FBAnalysisProject aProj, List<FBBuildProcess> buildProcesses) {
		
		//For each build process
		for (FBBuildProcess bp : buildProcesses) {
			
			//First we find the interesting revisions for this revision range
			List<Long> interestingRevs = aProj.repo.findInterestingRevisions(bp.checkoutPath, bp.startRevisionID, bp.endRevisionID);
			
			//Then we set up the working directory manager
			FBWorkingDirectoryManager wdm = new FBWorkingDirectoryManager(faultBenchDirectory, aProj.projectName, aProj.repo, bp.checkoutPath);
			
			//Now, for each interesting revision
			for (Long interestingRevID : interestingRevs) {
				
				//Prepare the revision for analysis 
				if (!wdm.prepareRevision(interestingRevID, bp)) {
					//If there were problems, skip this revision
					continue;
				}
				
				//Then collect the bugs
				FBFindBugsBugCollector.collectBugs(wdm);
				
			}
			
		}
		
		
	}

}
