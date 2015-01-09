package analysis;

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

import datamodel.FBClassVersion;
import datamodel.FBHistoryNode;
import datamodel.FBRepository;
import enums.FBNodeType;


public class FBJavaSourceAnalyzer {
	
	public static boolean analyzeFileNode(FBHistoryNode node, FBRepository repo) {
		
		//If this isn't a file node, return false
		if (node.type != FBNodeType.FILE) {
			return false;
		}
		
		
		//First we get the associated file contents
		String fileContents;
		try {
			fileContents = repo.getFileContents(node);
			
			if (fileContents == null) {
				return false;
			}
			
		} catch (SVNException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Problem getting file contents");
			return false;
		}
		
		
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
		
		
		//XXX This is incomplete --- just for testing
		
		
		//Create the top level class
		FBClassVersion topClass = new FBClassVersion(node, fbv.topLevelType.getName().getIdentifier(), fbv.pckgDec.getName().getFullyQualifiedName(), -1, -1);
		
		//Go ahead and index it
		repo.aProj.indexClassVersion(topClass);
		
		//Save the name etc to the node
		node.topLevelClassName = topClass.className;
		node.topLevelClassPackageName = topClass.packageName;
		
		
		
		
		
		
		
		
		//XXX Write this
		
		
		
		
		//Mark that we've analyzed this node
		node.sourceAnalyzed = true;
		
		
		
		return true;
		
	}
	
}
