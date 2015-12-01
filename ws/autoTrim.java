import java.util.HashSet;
import java.util.Set;

import analysis.ForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.Stmt;

public class autoTrim extends ForwardAnalysis<Set<AssignStmt>> {
	public autoTrim(ASTNode tree) {
		super(tree);
	}
	
	/*
	 * Added function
	 */
	private void processKillSet(Set<AssignStmt> s){
		if(s.size() == 0) return ;
		for(AssignStmt a : s){
			ASTNode parent = a.getParent();
			int index = parent.getIndexOfChild(a);
			parent.removeChild(index);
			System.out.println("[Warning Line "+ a.getStartLine() +" is removed] " + a.getPrettyPrinted());
		}
		System.out.println("removed total " + s.size());
	}
	
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

		// out = in
		currentOutSet = copy(currentInSet);
		// out = out - kill
		Set<AssignStmt> killset = kill(node);
		processKillSet(killset);
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