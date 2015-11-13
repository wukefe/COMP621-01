import java.util.*;
import ast.*;
import nodecases.*;


public class methodVector extends AbstractNodeCaseHandler {
	private ArrayList<String>methodList;
	
	public methodVector(){
		// initialization
		methodList = new ArrayList<>();
	}
	
	// case functions
	@Override
	public void caseASTNode(ASTNode node) {
		// TODO Auto-generated method stub
		for (int i = 0; i < node.getNumChild(); i++) {
			node.getChild(i).analyze(this);
		}
	}
	
	@Override
	public void caseFunction(Function node){
		System.out.println("enter function");
		methodList.add(node.getName().getVarName());
	}
	
	@Override
	public void caseScript(Script node){
		System.out.println(node.getPrettyPrinted());
		caseASTNode(node);
	}
	
	public ArrayList<String> getMethodList(){
		printList(methodList);
		return methodList;
	}
	
	private void printList(ArrayList<String> x){
		int n = 0;
		for(String v : x){
			System.out.println(n + " : " + v);
			n++;
		}
		System.out.println("--end--of--printList--");
	}
}
