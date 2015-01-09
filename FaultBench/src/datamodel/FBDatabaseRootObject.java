package datamodel;

import org.garret.perst.Index;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;


public class FBDatabaseRootObject extends Persistent {
	
	public Index<FBAnalysisProject> projects;
	
	public FBDatabaseRootObject(Storage db) {
		super(db);
		this.projects = db.<FBAnalysisProject>createIndex(String.class, true);
	}
	public FBDatabaseRootObject() {}
	
	public FBAnalysisProject getProject(String name) {
		return this.projects.get(name);
	}
	
	public void addProject(FBAnalysisProject project) {
		this.projects.put(project.projectName, project);
	}
	
	
}
