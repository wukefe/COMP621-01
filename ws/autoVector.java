import java.util.*;
import natlab.*;
import analysis.*;
import ast.*;
import ast.List;

import com.google.common.base.Joiner;

public class autoVector extends ForwardAnalysis<Set<AssignStmt>> {
  public autoVector(ASTNode tree, String name){
	  super(tree);
	  currentInSet = newInitialFlow();
	  currentOutSet= new HashSet<>(currentInSet);
	  ShapeAnalysis= new shapeVector(tree, name);
	  ShapeAnalysis.analyze();
	  methodList = new ArrayList<String>(ShapeAnalysis.getMethodList());
  }
	
  public static void main(String[] args) {
    // Parse the contents of args[0]
    // (If it doesn't parse, abort)
	if(args.length > 0){
		Program ast = parseOrDie(args[0]);
		autoVector analysis = new autoVector(ast, args[0]);
//		analysis.analyze();
		// get function list
//		methodVector mv = new methodVector();
//		ast.analyze(mv);
//		shapeVector ShapeAnalysis = new shapeVector(ast, args[0]);
//		ShapeAnalysis.analyze();
//		analysis.methodList = new ArrayList<String>(ShapeAnalysis.getMethodList());
//		shapeanalysis.printFinal();
		ast.analyze(analysis); // traverse autoVector
		// System.out.println(ast.getPrettyPrinted());
	}
  }
  
  /*
   * add variables below
   */
  // map Assignment to string to look up conditions
  Map<AssignStmt, String> OutFlowCond;
  int DepthFor = 0;
  public ArrayList<String> methodList;
  private shapeVector ShapeAnalysis;
  
  // (6)
  @Override
  public Set<AssignStmt> newInitialFlow(){
	  return new HashSet<>();
  }
  
  @Override
  public Set<AssignStmt> copy(Set<AssignStmt> src) {
	  return new HashSet<>(src);
  }
  
  // (5)
  @Override
  public Set<AssignStmt> merge(Set<AssignStmt> in1, Set<AssignStmt> in2) {
	  Set<AssignStmt> out = new HashSet<>(in1);
	  out.addAll(in2);
	  return out;
  }
  
  // case functions
  
  @Override
  public void caseASTNode(ASTNode node) {
  	// TODO Auto-generated method stub
	for(int i=0;i<node.getNumChild();i++){
		node.getChild(i).analyze(this);
	}
  }
  
  @Override
  public void caseFunction(Function node){
	  caseASTNode(node);
  }
  
  @Override
  public void caseScript(Script node) {
	  caseASTNode(node);
  }
  
  // main method
  @Override
  public void caseForStmt(ForStmt node){
	  // clean flow before going to next loop?
	  OutFlowCond = new HashMap<>();
	  //currentInSet = newInitialFlow();
	  //currentOutSet= new HashSet<>(currentInSet);
	  
	  processForStmt(node);
	  //Print("size = " + outFlowSets.size());
  }
  
  private infoDim ForRange;
  
  private void processForStmt(ForStmt node){
//	  DepthFor++;
	  ast.List<Stmt> x = node.getStmts();
	  String iter =  node.getAssignStmt().getLHS().getVarName(); // iter variable
	  ForRange = new infoDim(iter);
	  ForRange.setDim(ShapeAnalysis.getForRange(node.getAssignStmt().getRHS()));
	  Print("iter = " + iter);
	  Print("range= " + ForRange.toString());
	  for(Stmt a : x){
		  if (a instanceof AssignStmt){
			  AssignStmt ta = (AssignStmt)a;
			  processAssignment(ta, "");
		  }
		  else if(a instanceof IfStmt){
			  processIfStmt((IfStmt)a);
		  }
//		  else caseASTNode(node);
		  else if(a instanceof ForStmt){
			  processForStmt((ForStmt)a); // goto next for loop
		  }
	  }
	  // test(iter, outFlowSets)
	  PrintA(outFlowSets); //recursive solution
	  PrintB(OutFlowCond);
//	  DepthFor--;
  }
  
  @Override
  public void caseIfStmt(IfStmt node){
//	  if(DepthFor > 0)
		  processIfStmt(node);
	  caseASTNode(node);
  }
  
  private void processIfStmt(IfStmt node){
	  Print("entering  processIfStmt: " + node.getNumIfBlock());
	  ast.List<Expr> CurrentCondList = new ast.List<>();
	  outFlowSets.clear(); // initial
	  for(IfBlock ifb : node.getIfBlocks()){
		  Expr CurrentCond = ifb.getCondition(); // get current condition
		  CurrentCondList.add(CurrentCond);
		  for(Stmt x : ifb.getStmts()){
			  if(x instanceof AssignStmt){
				  processAssignment((AssignStmt)x, CurrentCond.getPrettyPrinted());
			  }
		  }
	  }
	  ElseBlock elsenode = node.getElseBlock();
	  if(elsenode != null){
		  String elsecond = genConditionString(CurrentCondList);
		  for(Stmt x : elsenode.getStmts()){
			  if(x instanceof AssignStmt){
				  processAssignment((AssignStmt)x, elsecond);
			  }
		  }
	  }
  }
  
  private void processAssignment(AssignStmt node, String ifcondition){
	  if(node.getLHS() instanceof ParameterizedExpr){
		  addAssignment(node, ifcondition); // decide flow
		  //Print(ta.getLHS().getNodeString());
		  ParameterizedExpr tc = (ParameterizedExpr)node.getLHS();
		  Print(getExprIndex(tc.getArg(0)));  //then get argument
		  //Print(tc.getVarName());
	  }
	  //Print(ta.getPrettyPrinted());
	  //Expr ra = ta.getRHS();
	  //Print("result = " + getExprNames(ra));
	  //String ne = ((NameExpr)ta.getRHS()).getNodeString();
	  //Print(ne);
  }
  
  // --- No override functions below
  private String getExprIndex(Expr x){
	  String rtn = "";
	  if(x instanceof ast.IntLiteralExpr){
		  rtn = ((IntLiteralExpr)x).getValue().getValue().toString();
	  }
	  else if(x instanceof ast.NameExpr){
		  rtn = ((NameExpr)x).getVarName();
	  }
	  return rtn;
  }
  
  private Set<String> getExprNames(ASTNode node){
	  Set<String> s = new HashSet<>();
	  if(node instanceof NameExpr){
		  s.add(node.getVarName());
	  }
	  else {
		  //Print(e.getNumChild());
		  //Print("check " + e.getChild(0).dumpString()); // check
		  for(int i=0;i<node.getNumChild();i++){
			  //Print("check " + e.getChild(i).dumpString());
			  s.addAll(getExprNames(node.getChild(i))); // go to next expr
		  }
	  }
	  return s;
  }
  
  private Set<String> getAsssignmentNames(AssignStmt node){
	  Set<String> s = new HashSet<>();
	  s.addAll(getExprNames(node.getLHS()));
	  s.addAll(getExprNames(node.getRHS()));
	  return s;
  }
  
  /*
   * out = (in + gen) - kill; 
   */
  private void addAssignment(AssignStmt node, String cond){
	  inFlowSets.put(node, copy(currentInSet));
	  
	  Print("current "+ cond + " = " + currentInSet.size());
	  // out = in
	  currentOutSet = copy(currentInSet);
	  // out = out + gen
	  currentOutSet.addAll(gen(node));
	  // out = out - kill
	  currentOutSet.removeAll(kill(node));
	  
	  outFlowSets.put(node, copy(currentOutSet));
	  OutFlowCond.put(node, cond);
  }
  
  private Set<AssignStmt> gen(AssignStmt node) {
	  Set<AssignStmt> s = new HashSet<>();
	  s.add(node);
	  return s;
  }
  
  private Set<AssignStmt> kill(AssignStmt node) {
	  Set<AssignStmt> r = new HashSet<>();
	  Set<String> names = node.getLValues(); //y(i) --> y
	  //Print("current = " + currentInSet.size());
	  for(AssignStmt var : currentInSet){
		  Set<String> nvar = getAsssignmentNames(var);
		  if(!nvar.containsAll(names)){
			  Print(nvar);
			  Print("nvar");
			  // not contain y
			  r.add(var);
		  }
		  else{
			  Print("killed set = " + var.getPrettyPrinted());
		  }
	  }
	  return r;
  }
  
  private String genConditionString(ast.List<Expr> x){
	  String str = "~";
	  int cnt = 0;
	  for(Expr a : x){
		  if(cnt > 0) str += " & ";
		  str += a.getPrettyPrinted();
		  cnt ++;
	  }
	  if(cnt == 0) str = "";
	  return str;
  }
  
	/*
	 * Z(i) = X(i) + y; ==> Z = X .+ y;
	 */
  public String genCodeVector(AssignStmt a){
	  Expr lx = a.getLHS();
	  Expr rx = a.getRHS();
	  if(lx instanceof ParameterizedExpr){
		  String omit = genExprList(((ParameterizedExpr) lx).getArgList());
		  System.out.println("val : " + lx.getVarName() + " = " + evalParameter(rx, omit));
		  
//		  String left = lx.getNodeString();
//		  String next = left.substring(left.indexOf('('));
//		  String text = a.getNodeString();
//		  System.out.println("left = " + left);
//		  System.out.println("next = " + next);
//		  next = next.replaceAll("(", "\\(");
//		  next = next.replaceAll(")", "\\)");
//		  System.out.println("next = " + next);
//		  System.out.println("final : " + text.replaceAll(next, ""));
	  }
	  return "";
  }
  
  private String evalParameter(Expr x, String omit){
	  String rtn = "";
	  if(x instanceof ParameterizedExpr){
		  ParameterizedExpr pe = (ParameterizedExpr)x;
		  String val = genExprList(pe.getArgList());
		  if(omit.equals(val)) {
			  String leftname = pe.getVarName();
			  if(ShapeAnalysis.isBuiltin(leftname)){
				  return leftname+"("+ForRange.genForRange()+")";
			  }
			  return leftname;
		  }
	  }
	  for(int i=0;i<x.getNumChild();i++){
		  rtn += evalParameter((Expr)(x.getChild(i)), omit);
	  }
	  return rtn;
  }
  
  private String genExprList(List<Expr> e){
	  String rtn = "";
	  for(Expr x : e){
		  rtn += x.getNodeString();
	  }
	  return rtn;
  }

  private static Program parseOrDie(String path) {
    java.util.List<CompilationProblem> errors = new ArrayList<>();
    Program ast = Parse.parseMatlabFile(path, errors);
    if (!errors.isEmpty()) {
      System.err.println("Parse error: " + Joiner.on('\n').join(errors));
      System.exit(1);
    }
    return ast;
  }
  
  private void PrintA(Map<ASTNode, Set<AssignStmt>> x){
	  int n = 0;
	  System.out.println("--Set--Assignment--");
	  for(Map.Entry<ASTNode, Set<AssignStmt>> s : x.entrySet()){
		  System.out.println(n + ": " + s.getKey().getPrettyPrinted());
		  Set<AssignStmt> t = s.getValue();
		  for(AssignStmt ta : t){
			  genCodeVector(ta);
			  System.out.println("\t" + ta.getPrettyPrinted());
		  }
		  n ++;
	  }
	  System.out.println("-------------------");
  }
  
  private void PrintB(Map<AssignStmt, String> x){
	  int n = 0;
	  System.out.println("--Map--Assignment--");
	  for(Map.Entry<AssignStmt, String> s : x.entrySet()){
		  System.out.println(n + ": " + s.getKey().getPrettyPrinted() + " --> " + s.getValue());
		  n ++;
	  }
	  System.out.println("-------------------");
  }
  
  private void Print(int x) {
	Print(Integer.toString(x));
  }
  
  private void Print(double x){
	  Print(Double.toString(x));
  }
  
  private void Print(Set<String> s){
	  System.out.println("length = " + s.size());
	  for(String x : s){
		  Print(x);
	  }
  }
	  
  private void Print(String m) {
	System.out.println("[debug]:");
	for(int i=0;i<5;i++) System.out.print("-"); System.out.println();
	System.out.println(m);
	for(int i=0;i<5;i++) System.out.print("-"); System.out.println();
  }

}
