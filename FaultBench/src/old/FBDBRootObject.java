package old;
import org.garret.perst.MultidimensionalIndex;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;




public class FBDBRootObject extends Persistent {
	
	public MultidimensionalIndex<FBHistoryNode> historyNodes;

	public FBDBRootObject(Storage db) {

		super(db);
		
		this.historyNodes = db.createMultidimensionalIndex(FBHistoryNode.class, new String[] {"nodeID", "branchID", "revisionID"}, false);
	}

	public FBDBRootObject() {}
	
	
}
