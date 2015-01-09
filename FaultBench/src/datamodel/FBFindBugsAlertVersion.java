package datamodel;

import org.garret.perst.Persistent;

import enums.FBBugStatus;


public class FBFindBugsAlertVersion extends Persistent {
	
	public FBHistoryNode node;
	public int primarySourceLineStart;
	public String instanceHash;
	public String alertType;
	public String alertCategory;
	public int alertPriority;
	public FBBugStatus bugStatus = FBBugStatus.UNKNOWN;
	public FBBuildProcess buildProcessUsed;
	
	public FBFindBugsAlertVersion(	FBHistoryNode node,
									int primarySourceLineStart,
									String instanceHash,
									String alertType,
									String alertCategory,
									int alertPriority,
									FBBuildProcess buildProcessUsed) {
		this.node = node;
		this.primarySourceLineStart = primarySourceLineStart;
		this.instanceHash = instanceHash;
		this.alertType = alertType;
		this.alertCategory = alertCategory;
		this.alertPriority = alertPriority;
		this.buildProcessUsed = buildProcessUsed;
	}
	public FBFindBugsAlertVersion() {}
	
	
}
