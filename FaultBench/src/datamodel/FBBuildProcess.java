package datamodel;

import java.util.Date;
import java.util.List;

import org.garret.perst.Persistent;
import org.garret.perst.Storage;

public class FBBuildProcess extends Persistent {

	//Checkout path should be valid throughout the entire revision range
	public String				checkoutPath;
	public long					startRevisionID;
	public long					endRevisionID;
	public List<FBBuildCommand>	buildCommands;
	public List<String>			classPaths;
	public List<String>			sourcePaths;
	public Date dateCreated;

	public FBBuildProcess(	Storage db,
							String checkoutPath,
							long startRevisionID,
							long endRevisionID,
							List<FBBuildCommand> buildCommands,
							List<String> classPaths,
							List<String> sourcePaths) {
		super(db);
		this.checkoutPath = checkoutPath;
		this.startRevisionID = startRevisionID;
		this.endRevisionID = endRevisionID;
		this.buildCommands = buildCommands;
		this.classPaths = classPaths;
		this.sourcePaths = sourcePaths;
		this.dateCreated = new Date(System.currentTimeMillis());
	}

	public FBBuildProcess() {
	}

}
