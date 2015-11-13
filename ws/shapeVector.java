import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import analysis.ForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.EDivExpr;
import ast.ETimesExpr;
import ast.Expr;
import ast.FPLiteralExpr;
import ast.ForStmt;
import ast.IntLiteralExpr;
import ast.MDivExpr;
import ast.MTimesExpr;
import ast.MinusExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.PlusExpr;
import ast.RangeExpr;
import ast.Stmt;
import ast.StringLiteralExpr;
import natlab.toolkits.path.BuiltinQuery;
import sun.launcher.resources.launcher_de;
import natlab.toolkits.BuiltinSet;


/*
 * Shape analysis
 * 
 * Consider:
 *   1:step:n
 *   iteration after loop may be: 1) [1,1] 2) []
 * 
 * Restrictions:
 *   no redefinition is allowed (assigned once)
 */

public class shapeVector extends ForwardAnalysis<Set<infoDim>> {
	public shapeVector(ASTNode tree){
		super(tree);
	}
	
	private Set<Stmt> skip = new HashSet<>();
	private Map<String, infoDim> shapeInfo = new HashMap<>();
	private BuiltinQuery query = BuiltinSet.getBuiltinQuery();

	// (6)
	@Override
	public Set<infoDim> newInitialFlow() {
		return new HashSet<>();
	}

	@Override
	public Set<infoDim> copy(Set<infoDim> src) {
		return new HashSet<>(src);
	}

	@Override
	public Set<infoDim> merge(Set<infoDim> in1, Set<infoDim> in2) {
		Set<infoDim> out = new HashSet<>(in1);
		out.addAll(in2);
		return out;
	}
	
	// case methods
	@Override
	public void caseASTNode(ASTNode node) {
		// TODO Auto-generated method stub
		for (int i = 0; i < node.getNumChild(); i++) {
			node.getChild(i).analyze(this);
		}
	}
	
	@Override
	public void caseAssignStmt(AssignStmt node){
		if(skip.contains(node)) return;
//		System.out.println("here = " + node.getPrettyPrinted());
		
		infoDim rtn = walkExpr(node);
		
//		inFlowSets.put(node, copy(currentInSet));
//	    
//	    // out = in
//	    currentOutSet = copy(currentInSet);
//	    // out = out - kill
//	    currentOutSet.removeAll(kill(node));
//	    // out = out + gen
//	    currentOutSet.addAll(gen(node));
//
//	    outFlowSets.put(node, copy(currentOutSet));
	    
		skip.add(node);
	}
	
	/*
	 * Iteration may be empty after loop []
	 *   for example, 3:2  --> empty
	 */
	@Override
	public void caseForStmt(ForStmt node){
		String iter = node.getAssignStmt().getLHS().getVarName();
		caseASTNode(node);
		infoDim old = shapeInfo.get(iter);
//		old.setDimUnknown();
		old.setDim2(1, 1);
//		System.out.println("hello:::" + old.toString());
		shapeInfo.put(iter, old);
	}
	
	infoDim walkExpr(AssignStmt node){
		System.out.println(node.getPrettyPrinted() + " --> " + node.dumpString());
		Expr exprleft = node.getLHS();
		String varname = "";
		infoDim rtn = new infoDim();
		if(exprleft instanceof ParameterizedExpr){
			ParameterizedExpr p = (ParameterizedExpr) exprleft;
			varname = p.getVarName();
		}
		else {
			varname = ((NameExpr)exprleft).getVarName();
		}
		rtn.setDim(evalExpr(node.getRHS()));
		rtn.setName(varname); //setname
		System.out.println("--> " + rtn.toString());
		saveVarInfo(varname, rtn); //save variable
		return rtn;
	}
	
	infoDim evalExpr(Expr e){
//		System.out.println(e.getPrettyPrinted() + " --> " + e.dumpString());
		infoDim rtn = new infoDim();
		int ChildNum = e.getNumChild();
		
		if(ChildNum < 2){
			if(e instanceof IntLiteralExpr || e instanceof FPLiteralExpr){
				rtn.setDim2(1,1);
			}
			else if(e instanceof StringLiteralExpr){
				int v = ((StringLiteralExpr)e).getValue().length();
				rtn.setDim2(1, v);
			}
			else if(e instanceof NameExpr){
				String n = ((NameExpr)e).getVarName();
				if(shapeInfo.containsKey(n)){
					rtn.setDim(shapeInfo.get(n));
				}
				else {
					// unknown
				}
			}
		}
		else if (e instanceof ParameterizedExpr) {
			ParameterizedExpr pe = (ParameterizedExpr) e;
			String leftname = pe.getVarName();
			
			if(shapeInfo.containsKey(leftname)){
				ArrayList<infoDim> dlist = new ArrayList<infoDim>();
				for(Expr te : pe.getArgList()){
					infoDim lhd = evalExpr(te);
					dlist.add(lhd);
				}
				rtn.setDim(decideDimIndex(leftname, dlist));
			}
			else if (query.isBuiltin(leftname)) {
				// built-in function
				rtn.setDim(decideDimBuiltin(pe));
			}
			else {
				// user-defined function
			}
		}
		else if (ChildNum == 2){
			infoDim lhd = evalExpr((Expr) (e.getChild(0)));
			infoDim rhd = evalExpr((Expr) (e.getChild(1)));
			rtn.setDim(decideDim(e, lhd, rhd));
		}
		else if (e instanceof RangeExpr){ // # of children is 3
			infoDim lhd = evalExpr((Expr) (e.getChild(0)));
			infoDim rhd = evalExpr((Expr) (e.getChild(2)));
//			rtn.setDim(decideDim(e, lhd, rhd));
			rtn.setDim(decideDimRange(e, lhd, rhd));
		}
		else {
			// special node
			System.out.println("warning: there are " + e.getNumChild() + " nodes. (>2)");
//			System.out.println(e.getChild(0).getPrettyPrinted() + " --> " + e.getChild(0).dumpString());
//			System.out.println(e.getChild(2).getPrettyPrinted() + " --> " + e.getChild(2).dumpString());
		}
		
//		System.out.println(e.getPrettyPrinted() + " --> " + e.dumpString() + " : " + rtn.toString());
		
		
//		System.out.println("total " + e.getNumChild());
//		System.out.println("----- " + e.getChild(1).getNumChild());
//		for(int i=0;i<e.getNumChild();i++){
//			System.out.println("child " + e.getChild(i).dumpString());
//		}
		
		return rtn;
	}
	
	// dim rule
	infoDim decideDim(Expr e, infoDim lhd, infoDim rhd){
		infoDim rtn = new infoDim();
		// + - .* ./
		if(e instanceof PlusExpr || e instanceof MinusExpr
				|| e instanceof ETimesExpr
				|| e instanceof EDivExpr){
			rtn.setDim(decideDimExpr0(lhd,rhd));
		}
		else if(e instanceof MTimesExpr){ // *
			rtn.setDim(decideDimExpr1(lhd,rhd,1));
		}
		else if(e instanceof MDivExpr){ // /
			rtn.setDim(decideDimExpr1(lhd,rhd,2));
		}
		return rtn;
	}
	
	infoDim decideDim(Expr e, infoDim rhd){
		return null;
	}
	
	/*
	 * Decide shapes
	 *   +, -, .*, ./
	 */
	infoDim decideDimExpr0(infoDim lhd, infoDim rhd){
		infoDim rtn = new infoDim();
		if(lhd.getStatus()==1){
			if(rhd.getStatus()==1)
				rtn.setDim(infoDim.decideInfoA1(lhd, rhd));
			else if(rhd.getStatus()==2)
				rtn.setDim(infoDim.decideInfoA2(lhd, rhd));
		}
		else if(lhd.getStatus()==2){
			if(rhd.getStatus()==1)
				rtn.setDim(infoDim.decideInfoA2(rhd, lhd));
			else if(rhd.getStatus()==2)
				rtn.setDim(infoDim.decideInfoA3(lhd, rhd));
		}
		return rtn;
	}
	
	infoDim decideDimExpr1(infoDim lhd, infoDim rhd, int op){
		infoDim rtn = new infoDim();
		if(lhd.getStatus()==1){
			if(rhd.getStatus()==1)
				rtn.setDim(infoDim.decideInfoB1(lhd, rhd, op));
			else if(rhd.getStatus()==2)
				rtn.setDim(infoDim.decideInfoB2(lhd, rhd, op));
		}
		else if(lhd.getStatus()==2){
			if(rhd.getStatus()==1)
				rtn.setDim(infoDim.decideInfoB2(rhd, lhd, op));
			else if(rhd.getStatus()==2)
				rtn.setDim(infoDim.decideInfoB3(lhd, rhd, op));
		}
		return rtn;
	}
	
	/*
	 * Decide range
	 *   1:n
	 */
	infoDim decideDimRange(Expr e, infoDim lhd, infoDim rhd){
		infoDim rtn = new infoDim();
		if(lhd.isScalar() && rhd.isScalar()){
			Expr e0 = (Expr)(e.getChild(0));
			Expr e2 = (Expr)(e.getChild(2));
			String left = e0.getNodeString();
			String right= e2.getNodeString();
			if(e0 instanceof IntLiteralExpr){
				if(e2 instanceof IntLiteralExpr){
					int v = Integer.parseInt(right) - Integer.parseInt(left) + 1;
					rtn.setDim2(1, v);
				}
				else if(left.equals("1")){
					rtn.setDim2("1", right);
				}
				else {
					String s = right + "-" + left + "1";
					rtn.setDim2("1", s);
				}
			}
			else {
				String s = right + "-" + left + "1";
				rtn.setDim2("1", s);
			}
		}
		return rtn;
	}
	
	infoDim decideDimBuiltin(ParameterizedExpr e){
		String name = e.getVarName();
		infoDim rtn = new infoDim();
		ast.List<Expr> vars = e.getArgList();
		int len = vars.getNumChild();
		if(name.equals("zeros")){
			System.out.println("len = " + len);
			if(len==1){
				Expr e0 = vars.getChild(0);
				if(e0 instanceof IntLiteralExpr){
					int v = Integer.parseInt(getExprIndex(e0));
					rtn.setDim2(v,v);
				}
				else if(e0 instanceof NameExpr){
					String s = getExprIndex(e0);
					rtn.setDim2(s, s);
				}
			}
			else if(len==2){
				Expr e0 = vars.getChild(0);
				Expr e1 = vars.getChild(1);
				if(e0 instanceof IntLiteralExpr && e1 instanceof IntLiteralExpr){
					int v0 = Integer.parseInt(getExprIndex(e0));
					int v1 = Integer.parseInt(getExprIndex(e1));
					rtn.setDim2(v0,v1);
				}
				else if((e0 instanceof IntLiteralExpr || e0 instanceof NameExpr)
						&& (e1 instanceof IntLiteralExpr || e1 instanceof NameExpr)){
					String s0 = getExprIndex(e0);
					String s1 = getExprIndex(e1);
					rtn.setDim2(s0,s1);
				}
			}
		}
		else if(name.equals("input")){
			rtn.setDim2(1,1);
		}
		else if(name.equals("sum")){
			// todo
		}
		//System.out.println("name = " + name + " : " + rtn.toString());
		return rtn;
	}
	
	infoDim decideDimIndex(String name, ArrayList<infoDim> dlist){
		infoDim rtn = new infoDim();
		infoDim val = shapeInfo.get(name); // check dim match??
		if(dlist.size() == 1){ // follow index
			rtn.setDim(dlist.get(0));
		}
		else {
			int[] arr = new int[99]; // max dims
			int arrx = 0;
			boolean f = true;
			for(infoDim x : dlist){
				if(x.status == 1){
					arr[arrx] = x.getLength();
					arrx++;
				}
				else {
					f = false;
					break;
				}
			}
			if(f) {
				rtn.setDim(arrx, 1, arr);
			}
		}
		return rtn;
	}
	
	/*
	 * Copy from autoVector.java
	 */
	private String getExprIndex(Expr x) {
		String rtn = "";
		if (x instanceof ast.IntLiteralExpr) {
			rtn = ((IntLiteralExpr) x).getValue().getValue().toString();
		} else if (x instanceof ast.NameExpr) {
			rtn = ((NameExpr) x).getVarName();
		}
		return rtn;
	}
	
	/*
	 * If variable is redefined,
	 *   the value is cleared to be unknown
	 * else
	 *   insert new <key, value>
	 */
	private void saveVarInfo(String name, infoDim x){
		if(shapeInfo.containsKey(name)){
			infoDim old = shapeInfo.get(name);
			if(!old.equals(x)) {
				System.out.println("warning : variable " + name + " is redefined.");
				shapeInfo.put(name, new infoDim());
			}
		}
		else shapeInfo.put(name, x);
	}
	
	public void printFinal(){
		printV(shapeInfo);
	}

	private void printV(Map<String, infoDim> x) {
		int n = 0;
		System.out.println("--Set--infoDim--");
		for (Map.Entry<String, infoDim> s : x.entrySet()) {
			System.out.println(n + ": " + s.getKey());
			System.out.println("\t" + s.getValue().toString());
			n++;
		}
		System.out.println("---------------");
	}
}
