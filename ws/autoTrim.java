import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import analysis.ForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.EDivExpr;
import ast.ETimesExpr;
import ast.Function;
import ast.MDivExpr;
import ast.MTimesExpr;
import ast.NameExpr;
import ast.Stmt;

/*
 * deprecated, not used
 */

public class autoTrim extends ForwardAnalysis<Set<AssignStmt>> {
	public autoTrim(ASTNode tree, Map<String, Boolean> vector) {
		super(tree);
		FuncVector = vector;
	}
	
	Map<String, Boolean> FuncVector;
	boolean onefunc = false;
	// Added functions
	/*
	 * StmtReduct:
	 *   def: A = 0;     --> def should not be deleted
	 *   use: A = A + 1; --> keep both stmts
	 */
	private void processKillSet(Set<AssignStmt> s, AssignStmt node){
		if(s.size() == 0) return ;
		for(AssignStmt a : s){
			ASTNode parent = a.getParent();
			int index = parent.getIndexOfChild(a);
			parent.removeChild(index);
			System.out.println("[Warning Line "+ a.getStartLine() +" is removed] " + a.getPrettyPrinted());
		}
		System.out.println("removed total " + s.size());
	}

//	public void travelAllNode(ASTNode node) {
//		if(node instanceof AssignStmt){
//			
//		}
//		else if(node instanceof Stmt){
//			Set<AssignStmt> defs = new HashSet<>();
//			for(int i=0;i<node.getNumChild();i++){
//				if(node instanceof AssignStmt){
//					
//				}
//			}
//			for(int i=0;i<node.getNumChild();i++){
//				travelAllNode(node.getChild(i));
//			}
//		}
//	}
	
	@Override
	public void caseASTNode(ASTNode node) {
		for(int i=0;i<node.getNumChild();i++){
			node.getChild(i).analyze(this);
		}
	}
	
	@Override
	public void caseFunction(Function node) {
		if(currentInSet  != null) currentInSet.clear();
		else currentInSet = new HashSet<>();
		if(currentOutSet != null) currentOutSet.clear();
		else currentOutSet = new HashSet<>();
		String funcname = node.getName().getVarName();
		onefunc = FuncVector.get(funcname);
//		System.out.println("entering " + node.getName().getVarName());
		caseASTNode(node);
	}

	@Override
	public Set<AssignStmt> copy(Set<AssignStmt> src) {
		// TODO Auto-generated method stub
		if(src == null || src.isEmpty()) return new HashSet<>();
		return new HashSet<>(src);
	}

	@Override
	public Set<AssignStmt> merge(Set<AssignStmt> arg0, Set<AssignStmt> arg1) {
		// TODO Auto-generated method stub
		return new HashSet<>(); // clear everything
	}

	@Override
	public Set<AssignStmt> newInitialFlow() {
		// TODO Auto-generated method stub
		return new HashSet<>();
	}

//	@Override
//	public void caseStmt(Stmt node) {
//		// superclass variables:
//		// currentInSet: A
//		// currentOutSet: A
//		// inFlowSets: Map<ASTNode, A>
//		// outFlowSets: Map<ASTNode, A>
////		System.out.println("[caseStmt] " + node.getPrettyPrinted());
//		inFlowSets.put(node, copy(currentInSet));
//		currentOutSet = copy(currentInSet);
//		outFlowSets.put(node, copy(currentOutSet));
////		System.out.print("currentoutset = " + currentOutSet.size());
//	}

	@Override
	public void caseAssignStmt(AssignStmt node) {
		inFlowSets.put(node, copy(currentInSet));
		
//		if(onefunc){ // change for functionOK functions
//			addDot(node);
//		}

		// out = in
		currentOutSet = copy(currentInSet);
		// out = out - kill
		Set<AssignStmt> killset = kill(node);
		processKillSet(killset, node);
		currentOutSet.removeAll(killset);
		// out = out + gen
		currentOutSet.addAll(gen(node));
		
//		System.out.println("currentInSet = " + currentInSet.size());

		outFlowSets.put(node, copy(currentOutSet));
		
		// finally <----
		currentInSet = copy(currentOutSet);
	}

	private Set<AssignStmt> kill(AssignStmt node) {
		Set<AssignStmt> r = new HashSet<>();
		Set<String> namesToKill = node.getLValues();
		for (AssignStmt def : currentInSet) {
			Set<String> names = def.getLValues();
			names.retainAll(namesToKill);
			if (!names.isEmpty()) {
				r.add(def);
			}
		}
		return r;
	}

	private Set<AssignStmt> gen(AssignStmt node) {
		Set<AssignStmt> s = new HashSet<>();
		s.add(node);
		return s;
	}
}
