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
import matlab.MatlabParser.name_list_return;

import com.google.common.base.Joiner;
import com.sun.javafx.binding.DoubleConstant;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
	  treeroot = tree;
	  initAnalysis(name);
  }
	
  public static void main(String[] args) {
    // Parse the contents of args[0]
    // (If it doesn't parse, abort)
	if(args.length > 0){
		Program ast = parseOrDie(args[0]);
		// step 1
		autoUDChain aud = new autoUDChain(ast);
		ast.analyze(aud); // analyze on aud
		if(1==1){
		// step 2
		autoVector analysis = new autoVector(ast, args[0]);
//		analysis.analyze();
		// get function list
//		methodVector mv = new methodVector();
//		ast.analyze(mv);
//		shapeVector ShapeAnalysis = new shapeVector(ast, args[0]);
//		ShapeAnalysis.analyze();
//		analysis.methodList = new ArrayList<String>(ShapeAnalysis.getMethodList());
//		shapeanalysis.printFinal();
		// traverse autoVector
		ast.analyze(analysis); // 1st pass
		ast.analyze(analysis); // 2nd pass
		analysis.printWholeNodes();
		System.out.println("going to trim");
		autoTrim atm = new autoTrim(ast); // remove redefinitions
		ast.analyze(atm);      // trim
//		ast.analyze(aud);      // necessary? 
		autoFunction aft = new autoFunction(analysis.FuncVector);
		aft.convertNode(ast);
//		System.out.println(ast.getPrettyPrinted());
		analysis.printWholeNodes();
		}
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
	  String parameterv = "DOUBLE&1*?&REAL";
	  String[] parameters = {parametern, parameterv, parameterv, parameterv, parameterv, parameterv, parameterv, parameterv};
//	  ArrayList<AggrValue<BasicMatrixValue>> inputValues = new ArrayList<>();
	  ArrayList<AggrValue<BasicMatrixValue>> inputValues = getListOfInputValues(parameters);
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
  
  Map<String, Boolean> FuncVector = new HashMap<>();
  public boolean isFuncVectorOK(){
	  // check stmt if, for, break and continue
	  checkFunction(treeroot);
	  // very very long
	  for(InterproceduralAnalysisNode<IntraproceduralValueAnalysis<AggrValue<BasicMatrixValue>>, Args<AggrValue<BasicMatrixValue>>, Res<AggrValue<BasicMatrixValue>>> currentfunction : FuncAnalysis.getNodeList()){
//		  currentfunction.getAnalysis().getOutFlowSets()
		  Map<String, ValueSet<AggrValue<BasicMatrixValue>>> SingleFunc = new HashMap<>();
		  String FuncName = currentfunction.getFunction().getName();
//		  boolean flag = true;
//		  System.out.println("fname = " + FuncName);
		  ValueFlowMap<AggrValue<BasicMatrixValue>> currentflows = (ValueFlowMap<AggrValue<BasicMatrixValue>>)currentfunction.getAnalysis().getOutFlowSets().get(currentfunction.getFunction().getAst());
		  decideOutset(currentflows, SingleFunc);
		  boolean flag = functionAnalysis(currentflows);
//		  for(Map.Entry<ASTNode, ValueFlowMap<AggrValue<BasicMatrixValue>>> currentflows : currentfunction.getAnalysis().getOutFlowSets().entrySet()){
//			  ValueFlowMap<?> x = (ValueFlowMap<?>)currentflow;
//			  decideOutset(currentflows.getValue(), SingleFunc);
//			  for(ValueFlowMap<AggrValue<BasicMatrixValue>> currentflow : currentflows.getValue())
//				  decideOutset((ValueFlowMap<AggrValue<BasicMatrixValue>>)currentflow);
//			  System.out.println(currentflows.getValue());
//		  }
//		  FuncVector.put(FuncName, flag);
		  updateFuncVector(FuncName, flag);
		  System.out.println("function = " + FuncName + ", scalar(t/f) = " + flag);
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
//		  System.out.print("name = " + name);
//		  Iterator<AggrValue<BasicMatrixValue>> iter = x.iterator();
//		  if(x.size() != 1) return false; // should be a singleton
//		  for(AggrValue<BasicMatrixValue> one : x.values()){
//			  BasicMatrixValue t = (BasicMatrixValue)one;
//			  Shape s0 = t.getShape();
//			  System.out.println(t.getShape().toString());
//		  }
	  }
	  System.out.println("--decide--");
	  return false;
  }
  
  /*
   * two principles:
   *   1) every var should be scalar
   *   2) no if-else structure is allowed
   */
  private boolean functionAnalysis(ValueFlowMap<AggrValue<BasicMatrixValue>> oneset){
	  // variable check
	  for(String name : oneset.keySet()){
		  ValueSet<AggrValue<BasicMatrixValue>> x = oneset.get(name);
		  if(x.size() != 1) return false;
		  for(AggrValue<BasicMatrixValue> one : x.values()){
			  BasicMatrixValue t = (BasicMatrixValue)one;
			  Shape s0 = t.getShape();
			  if(!s0.isScalar()) return false;
		  }
	  }
	  // 
	  return true;
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
//  Set<AssignStmt> MyCurrentSet = new HashSet<>(); //replaced by MyCurrentList
  ArrayList<AssignStmt> MyCurrentList = new ArrayList<>();
  Map<String, Map<Expr, AssignStmt>> MyCurrentCond = new HashMap<>();
  private BuiltinQuery builtinquery = BuiltinSet.getBuiltinQuery();
  Set<String> allindex = new HashSet<>();
  private ASTNode treeroot;
//  Map<String, Boolean> MyElseblock = new HashMap<>();
//  String defaultfuncname = "demo6"; //--> currentfuncname
  private String currentfuncname = "";
  Set<ASTNode> skips = new HashSet<>();
  
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
//	if(node instanceof AssignStmt){
//		System.out.println("testing ... " + node.getPrettyPrinted());
//		System.out.println("        ... " + ((AssignStmt)node).getLHS().dumpString());
//	}
  }
  
  @Override
  public void caseFunction(Function node){
//	  System.out.println(node.getAnalysisPrettyPrinted((StructuralAnalysis<?>)FuncAnalysis,true,true));
	  currentfuncname = node.getName().getVarName();
	  caseASTNode(node);
	  // after function analysis
	  //  - check function
	  //  - set new value/property
	  updateFuncVector(node.getName().getVarName(), checkFunctionSub(node));
//	  System.out.println("----->" + node.getName().getVarName() + "  <-----123");
  }
  
  @Override
  public void caseScript(Script node) {
	  caseASTNode(node);
  }
  
//  @Override
//  public void caseStmt(Stmt node) {
//	  if(!(node instanceof ForStmt)){
//		  printCodeGen(node.getPrettyPrinted());
//	  }
//  }
  
  // main method
  @Override
  public void caseForStmt(ForStmt node){
	  if(skips.contains(node)) return;
	  // clean flow before going to next loop?
	  OutFlowCond = new HashMap<>();
	  //currentInSet = newInitialFlow();
	  //currentOutSet= new HashSet<>(currentInSet);
	  processForStmt(node);
	  //Print("size = " + outFlowSets.size());
	  skips.add(node);
  }
  
  
  private void processForStmt(ForStmt node){
	  String iter =  node.getAssignStmt().getLHS().getVarName(); // iter variable
	  System.out.println(node.getAssignStmt().getPrettyPrinted());
	  infoDim ForRange = new infoDim(iter);
	  ForRange.setDim(getForRange(node.getAssignStmt().getRHS()));
	  ast.List<Stmt> x =  node.getStmtList(); //node.getStmts();
	  int tot = node.getNumChild();
	  ASTNode parent = node.getParent();
	  int childindex = parent.getIndexOfChild(node);
	  //different between x and node
//	  System.out.println("myparent: " + node.getIndexOfChild((x.getChild(0))));
//	  System.out.println("myparent: " + node.getIndexOfChild(node.getChild(0)));
	  // skip for assignment
//	  for(int myindex = 1 ; myindex < node.getNumChild(); myindex++){
//		  ASTNode a = node.getChild(myindex);
//		  if(a instanceof AssignStmt){
//			  processAssignment((AssignStmt)a, ""); //pass for
//		  }
//	  }
	  for(Stmt a : x){
		  if(a instanceof AssignStmt){
			  processAssignment((AssignStmt)a, ""); //pass for
		  }
	  }
	  System.out.println("-*-*-*-*-Output:for:statement-*-*-*-*-");
	  ArrayList<String> tempindex = new ArrayList<String>(allindex);
	  String commonindex = "";
	  if(tempindex.size() == 1) commonindex = tempindex.get(0);
	  for(AssignStmt a : MyCurrentList){
//		  System.out.println("commonindex = " + commonindex);
		  ASTNode newnode = genForVar(a, commonindex, ForRange);
		  parent.insertChild(newnode, childindex++); //insert before for loop
		  System.out.println("childindex: " + childindex + ", size = " + MyCurrentList.size());
//		  printCodeGen(newnode.getPrettyPrinted());  //commonindex=="i"
	  }
//	  System.out.println("myparent: " + parent.dumpString());
//	  System.out.println("check: " + MyCurrentList.size() + " != " + tot);
	  if(MyCurrentList.size() != tot){
//		  System.out.println("before: " + x.getNumChild());
//		  System.out.println("string0: " + x.getChild(0).getPrettyPrinted());
//		  for(int myindex = 1 ; myindex < node.getNumChild(); myindex++){
//			  ASTNode a = node.getChild(myindex);
//			  System.out.println("a = " + a.getPrettyPrinted());
//			  if(a instanceof AssignStmt)
//				  System.out.println("test = " + MyCurrentList.contains((AssignStmt)a));
//			  if((a instanceof AssignStmt) && MyCurrentList.contains((AssignStmt)a)) {
//				  System.out.println(myindex + " is being removed");
//				  node.removeChild(myindex); // remove old statements
//			  }
//		  }
		  int myindex = 0;
		  for(Stmt a : x){
//			  if((a instanceof AssignStmt) && MyCurrentCond.containsKey(a)) continue;
//			  printCodeGen(a.getPrettyPrinted());
			  if((a instanceof AssignStmt) && MyCurrentList.contains(a)) {
				  // why old parent doesn't work?
//				  ASTNode myparent = a.getParent();
//				  System.out.println("myparent: " + parent.getPrettyPrinted());
//				  System.out.println("node: " + node.getPrettyPrinted());
//				  System.out.println("a: " + a.getPrettyPrinted());
//				  System.out.println(myindex + " is being removed");
				  x.removeChild(myindex); // remove old statements
			  }
			  myindex++;
		  }
//		  System.out.println("left: " + x.getNumChild());
//		  System.out.println("string: " + x.getChild(0).getPrettyPrinted());
//		  System.out.println("finally: " + node.getPrettyPrinted());
	  }
	  else {
		  printCodeGen("for loop is reduced.");
//		  int childindex = parent.getIndexOfChild(node);
		  parent.removeChild(childindex); //clean it
	  }
//	  System.exit(1);
//	  System.out.print(evalName("val", defaultfuncname));
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
  private ASTNode genForVar(ASTNode node, String strindex, infoDim ForRange) {
	  System.out.println("[genForVar] " + node.getPrettyPrinted());
	  return transformAssignment(node, strindex, ForRange);
  }
  
  private ASTNode transformAssignment(ASTNode node, String keyword, infoDim ForRange){
	  ASTNode rtn = node.copy();
//	  System.out.println("[transformAssignment] " + rtn.getNumChild());
	  for(int i=0;i<node.getNumChild();i++){
		  ASTNode child = node.getChild(i);
		  int gonext = 0;
		  ArrayList<Integer> localindex = new ArrayList<>();
		  if(child instanceof ParameterizedExpr){
			  // consider not built-in case
			  if(FuncVector.containsKey(child.getVarName())) {
				  // if function
				  boolean flag = FuncVector.get(child.getVarName());
				  if(!flag) gonext = 2; // if false, don't go next
				  System.out.println("[Function]" + child.getVarName() + " can be vectorized." + flag);
			  }
			  if(gonext==0 && child.getNumChild()==2){
				  ast.List<Expr> parameters = ((ParameterizedExpr) child).getArgList(); // a list
				  int cnt = 0;
					for (Expr grandchild : parameters) {
						if ((grandchild instanceof NameExpr) && grandchild.getVarName().equals(keyword)) {
							localindex.add(cnt);
						}
						cnt++;
					}
				  if(parameters.getNumChild()==1 && localindex.size() == 1 && !builtinquery.isBuiltin(child.getVarName())) {
					  gonext = 1;
				  }
//				  System.out.println("---: " + gonext + "," + localindex.size() + ","+parameters.dumpString());
				  
//				  if(parameters.getNumChild() == 1){
////					  System.out.println("[transformAssignment] entering");
//					  ASTNode grandchild = parameters.getChild(0);
//					  if(grandchild instanceof NameExpr){
//						  if(grandchild.getVarName().equals(keyword)) {
//							  gonext = (builtinquery.isBuiltin(child.getVarName()))?1:2;
//						  }
//					  }
////					  System.out.println("gonext = " + gonext + " , " + grandchild.dumpString());
//				  }
//				  else {
//					  
//				  }
				  if(gonext != 1 && localindex.size() > 0){
//					  Expr left = new IntLiteralExpr(new DecIntNumericLiteralValue("1"));
//					  Expr right= new NameExpr(new Name("n"));
//					  RangeExpr x = new RangeExpr(left, new Opt(), right);
//					  ast.List parameters = ((ParameterizedExpr) child).getArgList();
					  for(int x : localindex){
						  parameters.setChild(ForRange.getRangeExpr(), x); //update parameters
					  }
				  }
				  
			  }
		  }
		  
		  
		  
		  if(gonext == 0) rtn.setChild(transformAssignment(child, keyword, ForRange), i);
		  else if(gonext == 1){
			  NameExpr x = new NameExpr(new Name(child.getChild(0).getVarName()));
//			  System.out.println("adding " + x.getPrettyPrinted() + ", " + rtn.dumpString());
//			  System.out.println("  j = " + rtn.getPrettyPrinted());
//			  System.out.println("  j = " + rtn.getChild(0).dumpString());
//			  System.out.println("  j = " + rtn.getChild(1).dumpString());
			  rtn.setChild(x, i); // update
//			  System.out.println("---: " + rtn.getPrettyPrinted());
			  /*if(rtn instanceof AssignStmt)  { // only 0,1
				  if(i == 0) ((AssignStmt)rtn).setLHS(x);
				  else ((AssignStmt)rtn).setRHS(x);
			  }
			  else {
//				  rtn.removeChild(i);
				  rtn.setChild(x, i); // update
				  System.out.println("---: " + rtn.getPrettyPrinted());
			  }*/
//			  System.out.println("  i = " + rtn.getChild(0).dumpString());
//			  System.out.println("  i = " + rtn.getChild(1).dumpString());
		  }
		  else if(gonext == 2){
			  // skip
		  }
	  }
	  return rtn;
  }
  
  String genForVarOne(String varname, infoDim ForRange){
	  String strindex = "";
	  if(builtinquery.isBuiltin(varname)){
		  strindex = ForRange.genForRange();
	  }
	  else{
		  infoDim varshape = evalName(varname, currentfuncname);
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
			  infoDim varshape = evalName(varname, currentfuncname);
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
	  if(skips.contains(node)) return;
//	  if(DepthFor > 0)
		  processIfStmt(node);
	  caseASTNode(node);
	  skips.add(node);
  }
  
  private void processIfStmt(IfStmt node){
	  Print("entering  processIfStmt: " + node.getNumIfBlock());
	  ast.List<Expr> CurrentCondList = new ast.List<>();
	  outFlowSets.clear(); // initial
	  MyCurrentList.clear();
	  MyCurrentCond.clear();
//	  MyElseblock.clear();
	  boolean reducable = true;
	  
	  Vector<String> leftlist = new Vector<>();
	  for(IfBlock ifb : node.getIfBlocks()){
		  for(Stmt x : ifb.getStmts()){
			  if(x instanceof AssignStmt){
				  leftlist.add(((AssignStmt)(x)).getLHS().getPrettyPrinted());
			  }
		  }
	  }
	  ElseBlock elsenode = node.getElseBlock();
	  if(elsenode != null){
		  for(Stmt x : elsenode.getStmts()){
			  if(x instanceof AssignStmt){
				  leftlist.add(((AssignStmt)(x)).getLHS().getPrettyPrinted());
			  }
		  }
	  }
	  
	  for(IfBlock ifb : node.getIfBlocks()){
		  Expr CurrentCond = ifb.getCondition(); // get current condition
		  CurrentCondList.add(CurrentCond);
		  Vector<String> oneblock = new Vector<>();
		  MyCurrentDefs.clear();
		  for(Stmt x : ifb.getStmts()){
			  if(x instanceof AssignStmt){
//				  processAssignment((AssignStmt)x, CurrentCond.getPrettyPrinted());
				  addAssignmentCond((AssignStmt)x, CurrentCond);
				  oneblock.add(((AssignStmt)(x)).getLHS().getPrettyPrinted());
			  }
			  else reducable = false;
		  }
		  Vector<String> remained = new Vector<>(leftlist);
		  remained.removeAll(oneblock);
		  if(remained.size() > 0){
			  for(String name : remained){
				  NameExpr n = new NameExpr(new Name(name));
				  addAssignmentCond(new AssignStmt(n, n), CurrentCond);
			  }
		  }
	  }
	  Expr elseexpr = genConditionExpr(CurrentCondList);
	  if(elsenode != null){
//		  String elsecond = genConditionString(CurrentCondList);
		  CurrentCondList.add(elseexpr);
//		  String elsecond = elseexpr.getPrettyPrinted();
		  Vector<String> oneblock = new Vector<>();
		  MyCurrentDefs.clear();
		  for(Stmt x : elsenode.getStmts()){
			  if(x instanceof AssignStmt){
//				  processAssignment((AssignStmt)x, elsecond);
				  addAssignmentCond((AssignStmt)x, elseexpr);
				  oneblock.add(((AssignStmt)(x)).getLHS().getPrettyPrinted());
			  }
			  else reducable = false;
		  }
		  Vector<String> remained = new Vector<>(leftlist);
		  remained.removeAll(oneblock);
		  if(remained.size() > 0){
			  for(String name : remained){
				  NameExpr n = new NameExpr(new Name(name));
				  addAssignmentCond(new AssignStmt(n, n), elseexpr);
			  }
		  }
	  }
	  else {
		  // all are remained
		  for(String name : leftlist){
			  NameExpr n = new NameExpr(new Name(name));
			  addAssignmentCond(new AssignStmt(n, n), elseexpr);
		  }
	  }
	  System.out.println("[translating if-else]");
//	  for(Expr w : CurrentCondList){
//	  for(AssignStmt w : MyCurrentSet){
//		  System.out.println("w = " + w.getPrettyPrinted());
//	  }
	  
//	  for(Map.Entry<String, Map<AssignStmt, String>> s : MyCurrentCond.entrySet()){
//		  System.out.println("left value: " + s.getKey());
//		  Map<AssignStmt, String> t = s.getValue();
//		  for(Map.Entry<AssignStmt, String> w : t.entrySet()){
//			  System.out.println(" node   value: " + w.getKey().getPrettyPrinted());
//			  System.out.println(" string value: " + w.getValue().getPrettyPrinted());
//		  }
//	  }
	  ASTNode parent = node.getParent();
	  int myindex = parent.getIndexOfChild(node);
	  Map<String, Boolean> namelist = new HashMap<>();
	  for(Map.Entry<String, Map<Expr, AssignStmt>> s : MyCurrentCond.entrySet()){
		  System.out.println("[processing ifelse] " + s.getKey());
		  Map<Expr, AssignStmt> t = s.getValue();
		  AssignStmt newnode = null;
		  NameExpr leftexpr = new NameExpr(new Name(s.getKey()));
		  Expr rightexpr = null;
		  namelist.put(s.getKey(), true);
		  // create pseudo statement for invisible else block
//		  if(!MyElseblock.containsKey(s.getKey())){
//			  ast.List<Expr> TempCondList = new ast.List<>();
//			  for(Map.Entry<Expr, AssignStmt> w : t.entrySet()){
//				  TempCondList.add(w.getKey());
//			  }
//			  rightexpr = composeIfelseTime(leftexpr, genConditionExpr(TempCondList));
//		  }
		  
		  for(Map.Entry<Expr, AssignStmt> w : t.entrySet()){
			  Expr curexpr = composeIfelseTime(w.getValue(), w.getKey());
			  if(rightexpr == null) rightexpr = curexpr;
			  else {
				  rightexpr = composeIfelsePlus(rightexpr, curexpr);
			  }
		  }
		  newnode = new AssignStmt(leftexpr, rightexpr); //set LHS and RHS
		  System.out.println(newnode.getPrettyPrinted());
		  parent.insertChild(newnode, myindex++);
	  }
	  
	  if(reducable){
		  // remove entire the if-else block
		  parent.removeChild(myindex);
		  System.out.println("warning: if-else has been removed");
	  }
	  else {
		  for(IfBlock ifb : node.getIfBlocks()){
			  for(Stmt x : ifb.getStmts()){
				  if((x instanceof AssignStmt) && namelist.containsKey(((AssignStmt)(x)).getLHS().getPrettyPrinted())){
					  ifb.removeChild(ifb.getIndexOfChild(x));
				  }
			  }
		  }
		  if(elsenode != null){
			  int blockindex = node.getIndexOfChild(elsenode);
			  for(Stmt x : elsenode.getStmts()){
				  if((x instanceof AssignStmt) && namelist.containsKey(((AssignStmt)(x)).getLHS().getPrettyPrinted())){
					  elsenode.removeChild(elsenode.getIndexOfChild(x));
				  }
			  }
			  // special case for else block
			  if(elsenode.getNumChild() == 0){
				  node.removeChild(blockindex);
			  }
		  }
	  }
//	  System.out.print("[query w] ");
//	  System.out.println(evalName("w", "demo4"));
  }
  
  private Expr composeIfelseTime(AssignStmt node, Expr cond){
	  return new ETimesExpr(node.getRHS(), cond);
  }
  
  private Expr composeIfelseTime(NameExpr node, Expr cond){
	  return new ETimesExpr(node, cond);
  }
  
  private Expr composeIfelsePlus(Expr one, Expr two){
	  return new PlusExpr(one, two);
  }
  
  /*
   * Add range iterator: e.g. 'i'
   */
  private int processAssignment(AssignStmt node, String ifcondition){
	  Set<String> rightnames = getExprNames(node.getRHS());
	  boolean flagvar = true;
	  int rtn = 0;
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
			  rtn = 1;
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
	  return rtn;
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
	  MyCurrentList.add(node);
  }
  
  private void addNoAssignment(AssignStmt node) {
	  String v = getExprName(node.getLHS());
	  if(!v.isEmpty()){
		  MyCurrentDefs.add(v); // add into set
	  }
  }
  /*
   * clear at the beginning of every block, including the else block
   */
	private void addAssignmentCond(AssignStmt node, Expr cond) {
		Set<String> rightnames = getExprNames(node.getRHS());
		Vector<String> remained = new Vector<>(MyCurrentDefs);
		Vector<String> rightvec = new Vector<>(rightnames);
		remained.retainAll(rightvec); // common element
		String leftname = node.getLHS().getPrettyPrinted();
		if (remained.isEmpty()) {
			if (MyCurrentCond.containsKey(leftname)) {
				Map<Expr, AssignStmt> oldset = MyCurrentCond.get(leftname);
				oldset.put(cond, node);
			} else {
				Map<Expr, AssignStmt> oldset = new HashMap<>();
				oldset.put(cond, node);
				MyCurrentCond.put(leftname, oldset);
			}
			// if(elseblock) MyElseblock.put(leftname, true);
			// node.getLHS().getVarname() --> (node, cond)
		} else {
			MyCurrentDefs.add(leftname); //get prior dependencies
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
  
  private Expr genConditionExpr(ast.List<Expr> x){
	  int len = x.getNumChild();
	  Expr rtn = null;
	  if(len == 1){
		  rtn = new NotExpr(x.getChild(0));
	  }
	  else if(len > 1){
		  Expr andexpr = x.getChild(len-1);
		  for(int i=len-2;i>=0;i--){
			  Expr tmp = new AndExpr(x.getChild(i), andexpr);
			  andexpr = tmp;
		  }
		  rtn = new NotExpr(andexpr);
	  }
	  return rtn;
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
			  rtn.setDim(evalName(n, currentfuncname));
		  }
	  }
	  // else
	  return rtn;
  }
  
  infoDim evalName(String name, String funcname){
	  infoDim rtn = new infoDim(name);
	  System.out.println("name : " + name + ", funcname : " + funcname);
	  ValueSet<AggrValue<BasicMatrixValue>> namelist = VarsAnalysis.get(funcname).get(name);
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
  
  /*
   *  trace all nodes
   *    - checkFunction
   *    - checkFunctionEntry
   *    - checkFunctionSub
   */
  void checkFunction(ASTNode root){
	  checkFunctionEntry(root);
  }
  
  void checkFunctionEntry(ASTNode node){
	  if(node instanceof Function){
		  boolean flag = checkFunctionSub(node);
		  updateFuncVector(((Function)node).getName().getVarName(), flag);
	  }
	  else {
		  for(int i=0;i<node.getNumChild();i++){
			  checkFunctionEntry(node.getChild(i));
		  }
	  }
  }
  
  boolean checkFunctionSub(ASTNode node){
	  if(node instanceof IfStmt || node instanceof ForStmt || node instanceof WhileStmt
			  || node instanceof BreakStmt || node instanceof ContinueStmt){
		  return false;
	  }
	  for(int i=0;i<node.getNumChild();i++){
		  if(!checkFunctionSub(node.getChild(i))) return false;
	  }
	  return true;
  }
  
  void updateFuncVector(String funcname, boolean flag){
	  FuncVector.put(funcname, flag);
//	  if(funcname.equals("foo")){
//		  System.out.println("[updateFuncVector] foo = " + flag);
//	  }
  }
  
  String getStringDimVal(DimValue x){
	  System.out.println("x = " + x.hasSymbolic() + " " + x.getSymbolic());
	  return (x.hasIntValue()?x.getIntValue()+"":x.hasSymbolic()?x.getSymbolic():"?");
  }
  
  ArrayList<Double> getNameInt(String name){
	  double f = 0, v = 0;
	  ArrayList<Double> rtn = new ArrayList<>();
//	  ValueSet<AggrValue<BasicMatrixValue>> namelist = VarsAnalysis.get("demo4").get(name);
	  ValueSet<AggrValue<BasicMatrixValue>> namelist = VarsAnalysis.get(currentfuncname).get(name);
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
  
  public void printWholeNodes(){
	  System.out.println("[printWholeNodes]: ");
	  System.out.println(treeroot.getPrettyPrinted());
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
  
  private int codegenindex = 0;
  void printCodeGen(String msg){
	  if(msg.isEmpty()) return;
	  System.out.println("[code gen "+ codegenindex + "] " + msg);
	  codegenindex++;
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
