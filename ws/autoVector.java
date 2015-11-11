import java.util.*;
import ast.*;
import natlab.*;
import nodecases.*;
import com.google.common.base.Joiner;

public class autoVector extends AbstractNodeCaseHandler {
  public static void main(String[] args) {
    // Parse the contents of args[0]
    // (If it doesn't parse, abort)
	if(args.length > 0){
		Program ast = parseOrDie(args[0]);
		ast.analyze(new autoVector());
		System.out.println(ast.getPrettyPrinted());
	}
	System.out.println("hello world");
  }

  private Set<Stmt> skip = new HashSet<>();
  
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
  public void caseForStmt(ForStmt node){
	  ast.List<Stmt> x = node.getStmts();
	  for(Stmt a : x){
		  if (a instanceof AssignStmt){
			  AssignStmt ta = (AssignStmt)a;
			  if(ta.getLHS() instanceof ParameterizedExpr){
				  //Print(ta.getLHS().getNodeString());
				  ParameterizedExpr tc = (ParameterizedExpr)ta.getLHS();
				  //Print(getExprIndex(tc.getArg(0)));  //then get argument
				  //Print(tc.getVarName());
			  }
			  Print(ta.getPrettyPrinted());
		  }
	  }
	  caseASTNode(node);
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

  private static Program parseOrDie(String path) {
    java.util.List<CompilationProblem> errors = new ArrayList<>();
    Program ast = Parse.parseMatlabFile(path, errors);
    if (!errors.isEmpty()) {
      System.err.println("Parse error: " + Joiner.on('\n').join(errors));
      System.exit(1);
    }
    return ast;
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
