package old;
import java.util.List;

import org.garret.perst.Index;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;

public class FBHistoryNode extends Persistent {
	
	public String nodeID;
	public String branchID;
	public long revisionID;
	public long offset;
	
	FBNodeKind kind;
	
	public FBHistoryNode(Storage db, String nodeID, String branchID, long revisionID, long offset, FBNodeKind kind) {
		
		super(db);
		
		this.nodeID = nodeID;
		this.branchID = branchID;
		this.revisionID = revisionID;
		this.offset = offset;
		this.kind = kind;
		
	}
	
	public FBHistoryNode() {}
	
}
