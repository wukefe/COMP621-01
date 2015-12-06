import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ast.ASTNode;
import ast.AssignStmt;
import ast.EDivExpr;
import ast.ETimesExpr;
import ast.Expr;
import ast.Function;
import ast.MDivExpr;
import ast.MTimesExpr;
import ast.NameExpr;
import ast.PlusExpr;
import ast.Stmt;

public class autoFunction {
	public autoFunction(Map<String, Boolean> vector) {
		FunctionVector = vector;
	}
	
	Map<String, Boolean> FunctionVector;
	boolean onefunc = false;
	
	
	public void convertNode(ASTNode node){
		if(node instanceof Function){
			String funcname = ((Function)node).getName().getVarName();
			onefunc = FunctionVector.get(funcname);
		}
		else if((node instanceof Stmt) && onefunc){
			addDot(node);
		}
//		if(node instanceof AssignStmt){
//			AssignStmt a = (AssignStmt)node;
//			if(a.getPrettyPrinted().trim().equals("xK2 = (1 ./ (1 + (0.2316419 * InputX)));")){
//				System.out.println("[find point here]");
//				System.out.println(" " + a.getRHS().dumpString() + a.getRHS().getNumChild()+
//						((ASTNode)(a.getRHS()) instanceof Stmt));
//				System.out.println(" " + ((EDivExpr)a.getRHS()).getRHS().dumpString() + ((EDivExpr)a.getRHS()).getRHS().getNumChild());
//				System.out.println(" " + ((PlusExpr)((EDivExpr)a.getRHS()).getRHS()).getRHS().dumpString()+
//						((PlusExpr)((EDivExpr)a.getRHS()).getRHS()).getRHS().getNumChild()+
//						(((ASTNode)(((PlusExpr)((EDivExpr)a.getRHS()).getRHS()).getRHS()) instanceof Stmt)?"yes":"no"));
//				System.exit(1);
//			}
//		}
		for(int i=0;i<node.getNumChild();i++){
			convertNode(node.getChild(i));
		}
	}
	
	private void addDot(ASTNode a){
		if(a instanceof MTimesExpr){
			ASTNode parent = a.getParent();
			int index = parent.getIndexOfChild(a);
			ETimesExpr newa = new ETimesExpr(((MTimesExpr) a).getLHS(), ((MTimesExpr) a).getRHS());
			parent.setChild(newa, index);
		}
		else if(a instanceof MDivExpr){
			ASTNode parent = a.getParent();
			int index = parent.getIndexOfChild(a);
			EDivExpr newa = new EDivExpr(((MDivExpr) a).getLHS(), ((MDivExpr) a).getRHS());
			parent.setChild(newa, index);
		}
		for(int i=0;i<a.getNumChild();i++){
			addDot(a.getChild(i));
		}
	}
}
