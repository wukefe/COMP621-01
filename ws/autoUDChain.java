import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.attribute.standard.RequestingUserName;

import java.util.Set;

import analysis.ForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.Function;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Stmt;

public class autoUDChain extends ForwardAnalysis<Set<AssignStmt>> {
	public autoUDChain(ASTNode tree) {
		super(tree);
	}
	
	// function -> node -> nodes
	Map<String, Map<AssignStmt, ArrayList<AssignStmt>>> duchains = new HashMap<>();
	Map<String, Map<AssignStmt, ArrayList<AssignStmt>>> udchains = new HashMap<>();
	
	private void process(Map<AssignStmt, ArrayList<AssignStmt>> duchain, Map<AssignStmt, ArrayList<AssignStmt>> udchain){
		for(Entry<AssignStmt, ArrayList<AssignStmt>> everydef : duchain.entrySet()){
			AssignStmt def = everydef.getKey();
			ArrayList<AssignStmt> use = everydef.getValue();
			boolean fid = false;
			if(use.size() == 1) {
				if(udchain.containsKey(use.get(0))){
					ArrayList<AssignStmt> reverseuse = udchain.get(use.get(0));
					if(reverseuse.size() == 1){
						fid = true;
					}
				}
			}
			if(fid){
				processMerge(def, use.get(0));
			}
		}
	}
	
	private void processMerge(AssignStmt def, AssignStmt use) {
		System.out.println("[changing] old = " + use.getPrettyPrinted());
		setExprNames(use, def.getLHS().getVarName(), def.getRHS());
		System.out.println("[changing] new = " + use.getPrettyPrinted());
		removeNode(def); // remove old one
	}
	
	private void removeNode(ASTNode node) {
		ASTNode parent = node.getParent();
		if(parent != null) parent.removeChild(parent.getIndexOfChild(node));
	}
	
	private void setExprNames(ASTNode node, String leftname, Expr expr){
		if(node instanceof NameExpr){
			if(node.getVarName().equals(leftname)){
				ASTNode father = node.getParent();
				int position = father.getIndexOfChild(node);
				father.setChild(expr, position);
			}
		}
		for(int i=0;i<node.getNumChild();i++){
			setExprNames(node.getChild(i), leftname, expr);
		}
	}
	
	private void setUDChains(String funcname, ASTNode root) {
		Map<AssignStmt, ArrayList<AssignStmt>> duchain = new HashMap<>();
		Map<AssignStmt, ArrayList<AssignStmt>> udchain = new HashMap<>();
		System.out.println("[UDchaining] entering " + funcname);
		pseudoCaseASTNode(root, duchain, udchain);
		process(duchain, udchain);
		duchains.put(funcname, duchain);
		udchains.put(funcname, udchain);
		System.out.println("[UDchaining] leaving " + funcname);
	}
	
	private void pseudoCaseASTNode(ASTNode node,
			Map<AssignStmt, ArrayList<AssignStmt>> duchain,
			Map<AssignStmt, ArrayList<AssignStmt>> udchain) {
		// a(i) = expr; //currently not be considered
		if((node instanceof AssignStmt) && !(((AssignStmt)node).getLHS() instanceof ParameterizedExpr)){
//		if(node instanceof AssignStmt) {
//			System.out.println("[dump] node = " + ((AssignStmt)node).getLHS().dumpString());
			Set<AssignStmt> stmts = getInFlowSets().get(node);
			Set<String> defs = new HashSet<>();
			Map<String, AssignStmt> defstmt = new HashMap<>();
			for(AssignStmt a : stmts){
				defs.add(a.getLHS().getVarName()); // definition
				defstmt.put(a.getLHS().getVarName(), a);
			}
			Set<String> stmtuse = getExprNames(((AssignStmt) node).getRHS());// use
			System.out.println("[dump] string = " + ((AssignStmt) node).getRHS().getPrettyPrinted());
			stmtuse.retainAll(defs); // find used vars in the expr
			for (String name : stmtuse) {
				AssignStmt targ = defstmt.get(name);
				System.out.println("[UDchaining] targ = " + targ.getPrettyPrinted());
				System.out.println("[UDchaining] node = " + node.getPrettyPrinted());
				chainLink(duchain, targ, (AssignStmt) node); // link: targ -> node
				chainLink(udchain, (AssignStmt) node, targ); // link: node -> targ
			}
		}
		for(int i=0;i<node.getNumChild();i++){
			pseudoCaseASTNode(node.getChild(i), duchain, udchain);
		}
	}
	
	private void chainLink(Map<AssignStmt, ArrayList<AssignStmt>> chain, AssignStmt from, AssignStmt to){
		if(chain.containsKey(from)){
			chain.get(from).add(to);
		}
		else {
			ArrayList<AssignStmt> newarray = new ArrayList<>();
			newarray.add(to);
			chain.put(from, newarray);
		}
	}
	
	private Set<String> getExprNames(ASTNode node){
		Set<String> rtn = new HashSet<>();
		if(node instanceof NameExpr){
			rtn.add(node.getVarName());
		}
		for(int i=0;i<node.getNumChild();i++){
			rtn.addAll(getExprNames(node.getChild(i)));
		}
		return rtn;
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
		String funcname = node.getName().getVarName();
		if(currentInSet  != null) currentInSet.clear();
		else currentInSet = new HashSet<>();
		if(currentOutSet != null) currentOutSet.clear();
		else currentOutSet = new HashSet<>();
//		System.out.println("entering " + node.getName().getVarName());
		caseASTNode(node);
		setUDChains(funcname, node); //
	}

	@Override
	public Set<AssignStmt> copy(Set<AssignStmt> src) {
		// TODO Auto-generated method stub
		if(src == null || src.isEmpty()) return new HashSet<>();
		return new HashSet<>(src);
	}

	@Override
	public Set<AssignStmt> merge(Set<AssignStmt> in1, Set<AssignStmt> in2) {
		// TODO Auto-generated method stub
		Set<AssignStmt> out = new HashSet<>(in1);
		out.addAll(in2);
		return out; // clear everything
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
