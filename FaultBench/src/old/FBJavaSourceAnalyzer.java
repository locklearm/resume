package old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.tmatesoft.svn.core.SVNException;



public class FBJavaSourceAnalyzer {
	
	
	public static FBSourceFile createSourceFile(FBHistoryNode historyNode, FBRepository repo) throws SVNException, IOException {
		
		//First we get the associated file contents
		String fileContents = repo.getFileNodeContents(historyNode);
		
		
		//Now we set up the parser
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(fileContents.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		//TODO Figure out why we need this
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
		parser.setCompilerOptions(options);
		
		//Then parse the file
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		class FBASTVisitor extends ASTVisitor {
			
			public CompilationUnit cu;
			public PackageDeclaration pckgDec;
			public List<AbstractTypeDeclaration>	types	= new ArrayList<AbstractTypeDeclaration>();
			public AbstractTypeDeclaration topLevelType;
			
			public FBASTVisitor(CompilationUnit cu) {
				this.cu = cu;
			}
			
			public boolean visit(PackageDeclaration pd) {
				this.pckgDec = pd;
				return true;
			}
			
			public boolean visit(TypeDeclaration td) {
				
				if(td.isPackageMemberTypeDeclaration()) {
					this.topLevelType = td;
				}
				else {
					this.types.add(td);
				}
				return true;
			}
			
			public boolean visit(EnumDeclaration ed) {
				
				if(ed.isPackageMemberTypeDeclaration()) {
					this.topLevelType = ed;
				}
				else {
					this.types.add(ed);
				}
				return true;
			}
			
			public boolean visit(AnnotationTypeDeclaration atd) {
				
				if(atd.isPackageMemberTypeDeclaration()) {
					this.topLevelType = atd;
				}
				else {
					this.types.add(atd);
				}
				return true;
			}
			
		}
		FBASTVisitor fbv = new FBASTVisitor(cu);
		cu.accept(fbv);
		
		//First, we find the fully qualified name of the top level type;
		String topLevelClass = fbv.pckgDec.getName().getFullyQualifiedName() + "." + fbv.topLevelType.getName().getIdentifier();
		
		//Then we create the object
		FBSourceFile sourceFile = new FBSourceFile(historyNode, repo, topLevelClass);
		
		
		
		
		return sourceFile;
	}
	
	
	public static void main(String[] args) throws Exception {
		
		FBHistoryNode n = new FBHistoryNode();
		n.nodeID = "58-638";
		n.branchID = "0";
		n.revisionID = 638;
		n.offset = 3518949;
		n.kind = FBNodeKind.FILE;
		
		FBRepository repo = new FBRepository();
		repo.repoPath = "F:\\bigbluebutton";
		
		createSourceFile(n, repo);
	}
	
}
