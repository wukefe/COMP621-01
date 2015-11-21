import java.util.*;
import natlab.*;
import natlab.tame.BasicTamerTool;
import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysis;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueFlowMap;
import natlab.tame.valueanalysis.ValueSet;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.AggrValueFactory;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValueFactory;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfoFactory;
import natlab.tame.valueanalysis.components.shape.DimValue;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.components.shape.ShapePropagator;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;
import natlab.toolkits.BuiltinSet;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.BuiltinQuery;
import natlab.toolkits.path.FileEnvironment;
import analysis.*;
import ast.*;
import ast.List;
import ast.ASTNodeAnnotation.Child;

import com.google.common.base.Joiner;
import com.sun.javafx.binding.DoubleConstant;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


/*
 * Useful files from https://github.com/Sable/mclab-core/
 *   tame/InterproceduralAnalysis/InterproceduralAnalysis.java     --main analysis
 *   tame/InterproceduralAnalysis/InterproceduralAnalysisNode.java --individual node analysis
 *   
 */

public class autoVector extends ForwardAnalysis<Set<AssignStmt>> {
  public autoVector(ASTNode tree, String name){
	  super(tree);
	  currentInSet = newInitialFlow();
	  currentOutSet= new HashSet<>(currentInSet);
	  initAnalysis(name);
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
	if(1==0){
		System.out.println("++++++++");
		GenericFile gFile = GenericFile.create("code/demo3.m");
		FileEnvironment env = new FileEnvironment(gFile);
		SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env);
//		BasicTamerTool.doIntOk = false;
		BasicTamerTool.setDoIntOk(false);
//		
//		String argx = "DOUBLE&1*1&REAL";
		String argx = "";
		String[] argsList = {argx};
//		ArrayList<AggrValue<BasicMatrixValue>> inputValues = getListOfInputValues(argsList);
		ArrayList<AggrValue<BasicMatrixValue>> inputValues = new ArrayList<>();
        ValueFactory<AggrValue<BasicMatrixValue>> factory = new BasicMatrixValueFactory();
        System.out.println(callgraph.toString());
        ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = new ValueAnalysis<AggrValue<BasicMatrixValue>>(
                callgraph, Args.newInstance(inputValues) , factory);
        System.out.println("here1 - " + analysis.toString());
        System.out.println("++++++++");
//        System.out.println(analysis.getMainNode().getFunction().toString());
        System.out.println(analysis.getMainNode().getResult().toString());
        analysis.getNodeList();
//        ShapePropagator sp = ShapePropagator.getInstance();
//        analysis.
	}
  }
  
  public ArrayList<AggrValue<BasicMatrixValue>> getListOfInputValues(String[] args) {
      ArrayList<AggrValue<BasicMatrixValue>> list = new ArrayList<AggrValue<BasicMatrixValue>>(
              args.length);
      for (String argSpecs : args) {
          String delims = "[\\&]";
          String[] specs = argSpecs.split(delims);
          PrimitiveClassReference clsType = PrimitiveClassReference
                  .valueOf(specs[0]);
          list.add(new BasicMatrixValue(null, clsType, specs[1], specs[2]));
      }
      return list;
  }
  
  /*
   * Initialize interprocedural analysis
   */
  public void initAnalysis(String filepath){
	  GenericFile gFile = GenericFile.create(filepath); //input file
	  FileEnvironment env = new FileEnvironment(gFile);
	  SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env); //contains all functions
	  BasicTamerTool.setDoIntOk(false);
	  
	  String parametern = "DOUBLE&1*1&REAL";
	  String[] parameters = {parametern};
	  ArrayList<AggrValue<BasicMatrixValue>> inputValues = new ArrayList<>();
//	  ArrayList<AggrValue<BasicMatrixValue>> inputValues = getListOfInputValues(parameters);
	  ValueFactory<AggrValue<BasicMatrixValue>> factory = new BasicMatrixValueFactory();
	  FuncAnalysis = new ValueAnalysis<AggrValue<BasicMatrixValue>>(
              callgraph, Args.newInstance(inputValues) , factory);
	  VarsAnalysis = new HashMap<>();
//	  System.out.println(callgraph.toString());
//	  System.out.println(FuncAnalysis.getMainNode().getFunction().toString());
	  System.out.println("++++++++++++ Result: +++++++++++");
	  System.out.println(FuncAnalysis.toString());
//	  System.out.println(FuncAnalysis.getMainNode().getPrettyPrinted());
//	  for(Map.Entry<ASTNode, ?> x : FuncAnalysis.getMainNode().getAnalysis().getOutFlowSets().entrySet()){
//		  ASTNode y = (ASTNode) x.getKey();
//		  System.out.println(y.getPrettyPrinted());
//		  System.out.println(y.dumpString());
//		  System.out.println(x.getValue());
//		  System.out.println("************");
//	  }
//	  ValueFlowMap<?> x = (ValueFlowMap<?>)FuncAnalysis.getMainNode().getAnalysis().getOutFlowSets().get(FuncAnalysis.getMainNode().getFunction().getAst());
//	  System.out.println(x);
//	  for(String name : x.keySet()){
//		  System.out.println(name + " --> " +x.get(name));
//	  }
//	  System.out.println(FuncAnalysis.getPrettyPrinted());
//	  System.out.println(FuncAnalysis.getMainNode().getResult().toString());
//	  System.out.println(callgraph.toString());
	  isFuncVectorOK();
	  System.out.println("--is-function-vector-okay--");
  }
  
  public boolean isFuncVectorOK(){
	  Map<String, Boolean> FuncVector = new HashMap<>();
	  // very very long
	  for(InterproceduralAnalysisNode<IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>, Args<AggrValue<BasicMatrixValue>>, Res<AggrValue<BasicMatrixValue>>> currentfunction : FuncAnalysis.getNodeList()){
//		  currentfunction.getAnalysis().getOutFlowSets()
		  Map<String, ValueSet<AggrValue<BasicMatrixValue>>> SingleFunc = new HashMap<>();
		  String FuncName = currentfunction.getFunction().getName();
		  boolean flag = true;
//		  System.out.println("fname = " + FuncName);
		  ValueFlowMap<AggrValue<BasicMatrixValue>> currentflows = (ValueFlowMap<AggrValue<BasicMatrixValue>>)currentfunction.getAnalysis().getOutFlowSets().get(currentfunction.getFunction().getAst());
		  decideOutset(currentflows, SingleFunc);
//		  for(Map.Entry<ASTNode, ValueFlowMap<AggrValue<BasicMatrixValue>>> currentflows : currentfunction.getAnalysis().getOutFlowSets().entrySet()){
//			  ValueFlowMap<?> x = (ValueFlowMap<?>)currentflow;
//			  decideOutset(currentflows.getValue(), SingleFunc);
//			  for(ValueFlowMap<AggrValue<BasicMatrixValue>> currentflow : currentflows.getValue())
//				  decideOutset((ValueFlowMap<AggrValue<BasicMatrixValue>>)currentflow);
//			  System.out.println(currentflows.getValue());
//		  }
		  FuncVector.put(FuncName, flag);
//		  if(SingleFunc.containsKey("res"))
//			  System.out.println("  | " + SingleFunc.get("res"));
//		  for(Map.Entry<String, ValueSet<AggrValue<BasicMatrixValue>>> iter : SingleFunc.entrySet()){
//			  System.out.println(iter.getKey() + " --> " + iter.getValue());
//		  }
		  VarsAnalysis.put(FuncName, SingleFunc);
	  }
	  return false;
  }
  
  private boolean decideOutset(ValueFlowMap<AggrValue<BasicMatrixValue>> oneset, Map<String, ValueSet<AggrValue<BasicMatrixValue>>> SingleFunc) {
	  for(String name : oneset.keySet()){
		  ValueSet<AggrValue<BasicMatrixValue>> x = oneset.get(name);
		  SingleFunc.put(name, x);
//		  Iterator<AggrValue<BasicMatrixValue>> iter = x.iterator();
//		  System.out.println("111111111");
//		  for(AggrValue<BasicMatrixValue> one : x.values()){
//			  BasicMatrixValue t = (BasicMatrixValue)one;
//			  System.out.println(t.getShape().toString());
//			  System.out.println(one.toString());
//			  if(!SingleFunc.containsKey(name)){
//				  System.out.println(name + " --> " + one);
//				  SingleFunc.put(name, one); //need a copy of one ?
//			  }
//		  }
//		  System.out.println("222222222");
	  }
	  System.out.println("--decide--");
	  return false;
  }
  
  /*
   * add variables below
   */
  // map Assignment to string to look up conditions
  Map<AssignStmt, String> OutFlowCond;
  int DepthFor = 0;
  public ArrayList<String> methodList;
  ValueAnalysis<AggrValue<BasicMatrixValue>> FuncAnalysis;
  Map<String, Map<String, ValueSet<AggrValue<BasicMatrixValue>>>> VarsAnalysis;
  Set<String> MyCurrentDefs = new HashSet<>(); //var in the expr should not appear in prior defs
  Set<AssignStmt> MyCurrentSet = new HashSet<>();
  Map<AssignStmt, String> MyCurrentCond = new HashMap<>();
  private BuiltinQuery builtinquery = BuiltinSet.getBuiltinQuery();
  Set<String> allindex = new HashSet<>();
  
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
//	  System.out.println(node.getAnalysisPrettyPrinted((StructuralAnalysis<?>)FuncAnalysis,true,true));
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
  
  
  private void processForStmt(ForStmt node){
	  String iter =  node.getAssignStmt().getLHS().getVarName(); // iter variable
	  System.out.println(node.getAssignStmt().getPrettyPrinted());
	  infoDim ForRange = new infoDim(iter);
	  ForRange.setDim(getForRange(node.getAssignStmt().getRHS()));
	  ast.List<Stmt> x = node.getStmts();
	  for(Stmt a : x){
		  if(a instanceof AssignStmt){
			  processAssignment((AssignStmt)a, ""); //pass for
		  }
	  }
	  System.out.println("-*-*-*-*-Output-*-*-*-*-");
	  ArrayList<String> tempindex = new ArrayList<String>(allindex);
	  String commonindex = "";
	  if(tempindex.size() == 1) commonindex = tempindex.get(0);
	  for(AssignStmt a : MyCurrentSet){
//		  System.out.println(genForStmt(a, ForRange));
		  String oldstring = a.getPrettyPrinted();
		  Set<String> vars = getExprNames(a);
		  for(String name : vars){
			  CharSequence oldkey = name+"("+commonindex+")";
			  CharSequence newkey = genForVar(a, name, ForRange);
			  System.out.println("oldkey = " + oldkey + " ; newkey = " + newkey);
			  oldstring = oldstring.replace(oldkey, newkey);
		  }
		  System.out.println(oldstring); //now is new string
	  }
	  System.out.print(evalName("val"));
////	  DepthFor++;
//	  ast.List<Stmt> x = node.getStmts();
//	  String iter =  node.getAssignStmt().getLHS().getVarName(); // iter variable
//	  ForRange = new infoDim(iter);
//	  ForRange.setDim(ShapeAnalysis.getForRange(node.getAssignStmt().getRHS()));
//	  Print("iter = " + iter);
//	  Print("range= " + ForRange.toString());
//	  for(Stmt a : x){
//		  if (a instanceof AssignStmt){
//			  AssignStmt ta = (AssignStmt)a;
//			  processAssignment(ta, "");
//		  }
//		  else if(a instanceof IfStmt){
//			  processIfStmt((IfStmt)a);
//		  }
////		  else caseASTNode(node);
//		  else if(a instanceof ForStmt){
//			  processForStmt((ForStmt)a); // goto next for loop
//		  }
//	  }
//	  // test(iter, outFlowSets)
//	  PrintA(outFlowSets); //recursive solution
//	  PrintB(OutFlowCond);
////	  DepthFor--;
  }
  
  /*
   * waiting list....
   *   need rewrite the whole code, getPrettyPrinting
   */
  private String genForVar(ASTNode node, String varname, infoDim ForRange) {
	  String strindex="";
	  String leftindex = "i";
	  if(node instanceof ParameterizedExpr){
		  String leftname = ((ParameterizedExpr)node).getVarName();
		  if(leftname.equals(varname)){
			  ParameterizedExpr p = (ParameterizedExpr)node;
			  if(p.getArgList().getNumChild() > 0){
				  if(p.getArg(0).getNodeString().equals(leftindex)){
					  System.out.println("xx " + p.getArg(0).getNodeString());
					  strindex = genForVarOne(varname, ForRange);
					  if(!strindex.isEmpty()){
						  if(p.getArgList().getNumChild() == 1)
							  strindex += ")";
					  }
				  }
			  }
			  System.out.println("[genForVar] " + strindex);
			  if(!strindex.isEmpty()) return varname + "(" + strindex;
//			  return (strindex.isEmpty()?varname:varname+"("+strindex+")");
		  }
		  for(int i=0;i<node.getNumChild();i++)
			  return genForVar(node.getChild(i), leftname, ForRange);
	  }
	  else {
		  for(int i=0;i<node.getNumChild();i++)
			  return genForVar(node.getChild(i), varname, ForRange);
	  }
	  return varname;
  }
  
  String genForVarOne(String varname, infoDim ForRange){
	  String strindex = "";
	  if(builtinquery.isBuiltin(varname)){
		  strindex = ForRange.genForRange();
	  }
	  else{
		  infoDim varshape = evalName(varname);
//		  System.out.println("shape = " + varshape.toString());
		  if(!varshape.equals(ForRange)){
			  strindex = ForRange.genForRange();
		  }
	  }
	  return strindex;
  }
  
  private String genForStmt(ASTNode node, infoDim ForRange) {
	  String rtn = "";
	  if(node instanceof ParameterizedExpr){
		  String varname = ((ParameterizedExpr)node).getVarName();
		  String strindex="";
		  if(builtinquery.isBuiltin(varname)){
			  strindex = ForRange.genForRange();
		  }
		  else{
			  infoDim varshape = evalName(varname);
			  if(!varshape.equals(ForRange)){
				  strindex = ForRange.genForRange();
			  }
		  }
		  rtn = (strindex.isEmpty()?varname:varname+"("+strindex+")");
	  }
	  else if(node.getNumChild()<2){
		  rtn = node.getPrettyPrinted();
	  }
	  else {
		  System.out.println(node.getNodeString());
		  for(int i=0;i<node.getNumChild();i++)
			  rtn += genForStmt(node.getChild(i),ForRange);
	  }
	  return rtn;
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
  
  /*
   * Add range iterator: e.g. 'i'
   */
  private void processAssignment(AssignStmt node, String ifcondition){
	  Set<String> rightnames = getExprNames(node.getRHS());
	  boolean flagvar = true;
	  for(String rn : rightnames){
		  if(MyCurrentDefs.contains(rn)) {
			  flagvar = false; break;
		  }
	  }
	  if(!flagvar) addNoAssignment(node);
	  
	  if(node.getLHS() instanceof ParameterizedExpr){
		  //Print(ta.getLHS().getNodeString());
		  ParameterizedExpr tc = (ParameterizedExpr)node.getLHS();
//		  Print(getExprIndex(tc.getArg(0)));  //then get argument
		  System.out.println("Processing ... \n\t" + node.getPrettyPrinted());
		
		  allindex =  getParameterIndex(node.getRHS());
		  allindex.addAll(getParameterIndex(node.getLHS()));
		  System.out.println("\t"+allindex.toString());
		  if(allindex.size() == 1){ //single index
			  addAssignment(node, ifcondition); // decide flow
		  }
		  else {
			  addNoAssignment(node);
		  }
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
  
  private Set<String> getParameterIndex(ASTNode node){
	  Set<String> s = new HashSet<>();
	  String leftindex = "i";
	  if(node instanceof ParameterizedExpr){
		  for(Expr e : ((ParameterizedExpr)node).getArgList()){
			  String slot1 = e.getNodeString();
			  if(slot1.equals(leftindex)){ //only include by match
				  s.add(slot1);
			  }
			  else {
				  // go into next slots
				  for(int i=0;i<node.getNumChild();i++){
					  s.addAll(getParameterIndex(node.getChild(i)));
				  }
			  }
		  }
	  }
	  else {
		  for(int i=0;i<node.getNumChild();i++){
			  s.addAll(getParameterIndex(node.getChild(i)));
		  }
	  }
	  return s;
  }
  
  private Set<String> getExprNames(ASTNode node){
	  Set<String> s = new HashSet<>();
	  if(node instanceof NameExpr){
		  s.add(node.getVarName());
	  }
	  else if(node instanceof ParameterizedExpr){
		  s.add(((ParameterizedExpr)node).getVarName()); // x(i), only take x 
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
  private void addAssignmentOld(AssignStmt node, String cond){
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
  
  private void addAssignment(AssignStmt node, String cond){
	  System.out.println("[add assignment]" + node.getPrettyPrinted());
	  MyCurrentSet.add(node);
	  MyCurrentCond.put(node, cond);
  }
  
  private void addNoAssignment(AssignStmt node) {
	  String v = getExprName(node.getLHS());
	  if(!v.isEmpty()){
		  MyCurrentDefs.add(v); // add into set
	  }
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
//		Analysis.isBuiltin(leftname)){
//				  return leftname+"("+ForRange.genForRange()+")";
//			  }
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
  
	private infoDim getForRange(Expr arg) {
		infoDim rtn = new infoDim();
		if (arg instanceof RangeExpr) {
			RangeExpr e = (RangeExpr) arg;
			infoDim lhd = evalExpr(e.getLower());
			infoDim rhd = evalExpr(e.getUpper());
			if (lhd.isScalar() && rhd.isScalar()) {
				Expr e0 = (Expr) (e.getChild(0));
				Expr e2 = (Expr) (e.getChild(2));
				String left = e0.getNodeString();
				String right = e2.getNodeString();
				ArrayList<Double> r0 = getNameInt(left);  // change constant variable to int
				ArrayList<Double> r2 = getNameInt(right); // e.g. n = 10; 'n' is replaced by 10
				boolean f0 = r0.get(0) == 1;
				boolean f2 = r2.get(0) == 1;
				if ((e0 instanceof IntLiteralExpr || f0) && (e2 instanceof IntLiteralExpr || f2)) {
					int v0 = f0 ? (int) (r0.get(1).doubleValue()) : Integer.parseInt(left);
					int v2 = f2 ? (int) (r2.get(1).doubleValue()) : Integer.parseInt(right);
//					System.out.println("[genForRange] " + v0 + " " + v2 + " " + f0 + " " + f2);
					System.out.println("[genForRange] " + r2.get(1).toString());
					int v = v2 - v0 + 1;
					rtn.setDim2(1, v);
				} else if (left.equals("1")) {
					rtn.setDim2("1", right);
				} else {
					String s = right + "-" + left + "1";
					rtn.setDim2("1", s);
				}
			}
		}
		System.out.println("[genForRange] " + rtn.toString());
		return rtn;
	}
  
  infoDim evalExpr(Expr e){
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
			  rtn.setDim(evalName(n));
		  }
	  }
	  // else
	  return rtn;
  }
  
  infoDim evalName(String name){
	  infoDim rtn = new infoDim(name);
	  ValueSet<AggrValue<BasicMatrixValue>> namelist = VarsAnalysis.get("demo4").get(name);
//	  if(namelist || namelist.size() == 1){
	  if(namelist.size() == 1){
		  for(AggrValue<BasicMatrixValue> x : namelist){
			  BasicMatrixValue x0 = (BasicMatrixValue)x;
			  Shape shape0 = x0.getShape();
			  System.out.println("evalExpr --> " + shape0.toString() + " : " + x.toString());
			  java.util.List<DimValue> dims = shape0.getDimensions();
			  if(shape0.isScalar()){
//				  System.out.println(x0.getConstant().getValue());
				  rtn.setDim2(1, 1);
			  }
			  else if(shape0.isConstant()){
				  if(dims.size() == 2){
					  System.out.println("\tfind const shape");
					  rtn.setDim2(dims.get(0).getIntValue(), dims.get(1).getIntValue());
				  }
			  }
			  else if(dims.size() == 2){
				  rtn.setDim2(getStringDimVal(dims.get(0)), getStringDimVal(dims.get(1)));
				  System.out.println(rtn.toString() + " " + rtn.getStatus());
			  }
			  else {
				  System.out.println("*********WARNING***Found******  " + name +
						  " : dims = " + dims.size());
			  }
		  }
	  }
	  return rtn;
  }
  
  String getStringDimVal(DimValue x){
	  System.out.println("x = " + x.hasSymbolic() + " " + x.getSymbolic());
	  return (x.hasIntValue()?x.getIntValue()+"":x.hasSymbolic()?x.getSymbolic():"?");
  }
  
  ArrayList<Double> getNameInt(String name){
	  double f = 0, v = 0;
	  ArrayList<Double> rtn = new ArrayList<>();
	  ValueSet<AggrValue<BasicMatrixValue>> namelist = VarsAnalysis.get("demo4").get(name);
//	  System.out.println("getNameInt = " + name);
	  if(namelist!=null && namelist.size() == 1){
		  for(AggrValue<BasicMatrixValue> x : namelist){
			  BasicMatrixValue x0 = (BasicMatrixValue)x;
			  Shape shape0 = x0.getShape();
			  if(shape0.isScalar()){
				  if(shape0.isConstant() && x0.hasConstant()){
//					  System.out.println(name + " from getNameInt");
//					  System.out.println(shape0.toString());
//					  System.out.println(x0.hasShape());
//					  System.out.println(shape0.isConstant());
//					  System.out.println("[getNameInt] " + shape0.toString());
					  v = (double)(x0.getConstant().getValue());  // return value other than shape
					  f = 1;
				  }
			  }
		  }
	  }
	  rtn.add(f);
	  rtn.add(v);
	  return rtn;
  }
  
  String getExprName(Expr e){
	  String rtn = "";
	  if(e instanceof NameExpr) rtn = ((NameExpr) e).getName().getVarName();
	  else if(e instanceof ParameterizedExpr){
		  rtn = ((ParameterizedExpr)e).getVarName();
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
