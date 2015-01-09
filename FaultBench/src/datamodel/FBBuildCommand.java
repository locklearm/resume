package datamodel;

import java.util.ArrayList;
import java.util.List;

import org.garret.perst.Persistent;
import org.garret.perst.Storage;

public class FBBuildCommand extends Persistent {

	public String executable;
	public List<String> arguments;
	
	public FBBuildCommand(Storage db, String executable, List<String> arguments) {
		super(db);
		this.executable = executable;
		this.arguments = arguments;
	}
	public FBBuildCommand() {}
	
}
