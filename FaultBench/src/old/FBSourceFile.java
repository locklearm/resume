package old;
import java.io.IOException;
import java.util.List;

import org.garret.perst.Persistent;
import org.tmatesoft.svn.core.SVNException;



public class FBSourceFile extends Persistent {
	
	public FBHistoryNode historyNode;
	
	public FBRepository repo;
	
	//Fully enumerated main class (class name is same as file name)
	public String topLevelClass;
	
	public FBSourceFile(FBHistoryNode historyNode, FBRepository repo, String topLevelClass) {
		this.historyNode = historyNode;
		this.repo = repo;
		this.topLevelClass = topLevelClass;
	}
	public FBSourceFile() {}
	
	public String getCheckoutPath() throws SVNException {
		
		return this.repo.getNodePath(this.historyNode);
		
	}
	
	public String getFileContents() throws SVNException, IOException {
		return this.repo.getFileNodeContents(this.historyNode);
	}
	
}
