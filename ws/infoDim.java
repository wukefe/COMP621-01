import java.awt.Dimension;

import org.antlr.runtime.tree.RewriteRuleElementStream;

import ast.ASTNode;
import ast.Expr;
import ast.IntLiteralExpr;
import ast.Name;
import ast.NameExpr;
import ast.Opt;
import ast.RangeExpr;
import matlab.MatlabParser.row_list_return;
import natlab.DecIntNumericLiteralValue;
import sun.security.x509.DeltaCRLIndicatorExtension;

/*
 * infoDim
 *   class for variable dimensions
 * 
 * status
 *   0: not known
 *   1: known with int
 *   2: known with var
 */
public class infoDim {
	int rank;      //rank: 1,2,...
	int status;    //0,1
	int[] dims;    //max 20 dims
	String[] dimv; //max 20 dims
	String name;   //name
	
	public infoDim(){
		dims = new int[20];
		dimv = new String[20];
		clear();
	}
	
	public infoDim(String n){
		dims = new int[20];
		dimv = new String[20];
		clear();
		name = n;
	}
	
	public void clear(){
		status = 0; name = ""; rank = 0;
	}
	
	public void setDim(infoDim x){
		if(x.status == 1) setDim(x.rank, x.status, x.dims);
		else if(x.status == 2) setDim(x.rank, x.status, x.dimv);
		else status = x.status;
	}
	
	public void setDim(int r, int s, int[] d, String n){
		rank = r;
		status = s;
		name = n;
		for(int i=0;i<rank;i++){
			dims[i] = d[i];
		}
	}
	
	public void setDim(int r, int s, int[] d){
		setDim(r,s,d,name);
	}
	
	public void setDim(int r, int s, String[] d){
		rank = r;
		status = s;
		for(int i=0;i<rank;i++){
			dimv[i] = d[i];
		}
	}
	
	public void setDim2(int d0, int d1){
		rank = 2;
		status = 1;
		dims[0] = d0; dims[1] = d1;
	}
	
	public void setDim2(String d0, String d1){
		rank = 2;
		status = 2;
		dimv[0] = d0; dimv[1] = d1;
	}
	
	public void setDimUnknown(){
		status = 0; rank = 0;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public int getStatus() {
		return status;
	}
	
	/*
	 * decide shape between two known vars 
	 */
	public static infoDim decideInfoA1(infoDim d0, infoDim d1){
		infoDim rtn = new infoDim();
		boolean f0,f1;
		f0 = f1 = true;
		for(int i=0;i<d0.rank;i++) if(d0.dims[i]!=1) {f0=false; break;} 
		for(int i=0;i<d1.rank;i++) if(d1.dims[i]!=1) {f1=false; break;}
		if(f0) rtn.setDim(d1);
		else if(f1) rtn.setDim(d0);
		else if(d0.rank == d1.rank){
			boolean same = true;
			for(int i=0;i<d0.rank;i++) if(d0.dims[i]!=d1.dims[i]) {same=false; break;}
			if(same) rtn.setDim(d0);
		}
		return rtn;
	}
	
	/*
	 * d0's status is 1
	 * d1's status is 2
	 */
	public static infoDim decideInfoA2(infoDim d0, infoDim d1){
		infoDim rtn = new infoDim();
		boolean f0 = true;
		for(int i=0;i<d0.rank;i++) if(d0.dims[i]!=1) {f0=false; break;}
		if(f0) rtn.setDim(d1);
		return rtn;
	}
	
	/*
	 * d0's status is 2
	 * d1's status is 2
	 */
	public static infoDim decideInfoA3(infoDim d0, infoDim d1){
		infoDim rtn = new infoDim();
		if(d0.rank == d1.rank){
			boolean same = true;
			for(int i=0;i<d0.rank;i++) if(!d0.dimv[i].equals(d1.dimv[i])) {same=false; break;}
			if(same) rtn.setDim(d0);
		}
		return rtn;
	}
	
	/*
	 * Decide known var
	 *   * (op == 1) multiplication
	 *   / (op == 2) inversion
	 */
	public static infoDim decideInfoB1(infoDim d0, infoDim d1, int op){
		infoDim rtn = new infoDim();
		boolean f0,f1;
		f0 = f1 = true;
		for(int i=0;i<d0.rank;i++) if(d0.dims[i]!=1) {f0=false; break;} 
		for(int i=0;i<d1.rank;i++) if(d1.dims[i]!=1) {f1=false; break;}
		if(f0) rtn.setDim(d1);
		else if(f1) rtn.setDim(d0);
		else if(d0.rank == 2 && d1.rank == 2){ //max 2
			boolean doable = (op==1?d0.dims[1]==d1.dims[0]:op==2?d0.dims[1]==d1.dims[1]:false);
			if(doable) {
				if(op==1) rtn.setDim2(d0.dims[0], d1.dims[1]);
				else rtn.setDim2(d0.dims[0], d1.dims[0]);
			}
		}
		return rtn;
	}
	
	/*
	 * Incomplete:
	 *   consider the case: 2x3 * 3xN  --> 2xN
	 */
	public static infoDim decideInfoB2(infoDim d0, infoDim d1, int op){
		infoDim rtn = new infoDim();
		return rtn;
	}
	
	public static infoDim decideInfoB3(infoDim d0, infoDim d1, int op){
		infoDim rtn = new infoDim();
		if(d0.rank==2 && d1.rank==2){
			boolean doable = (op==1?d0.dimv[1].equals(d1.dimv[0]):op==2?d0.dimv[1].equals(d1.dimv[1]):false);
			if(doable) {
				if(op==1) rtn.setDim2(d0.dimv[0], d1.dimv[1]);
				else rtn.setDim2(d0.dimv[0], d1.dimv[0]); //set string
			}
		}
		return rtn;
	}
	
	public boolean equals(infoDim x){
		if(status != x.status) return false;
		if(rank != x.rank) return false;
		if(status==1) {
			for(int i=0;i<rank;i++) if(dims[i]!=x.dims[i]) return false;
		}
		else if(status==2){
			for(int i=0;i<rank;i++) if(!dimv[i].equals(x.dimv[i])) return false;
		}
		return true;
	}
	
	public boolean isScalar() {
		boolean f = (rank==2?(status==1?dims[0]==dims[1] && dims[0]==1:false):false);
		return f;
	}
	
	public int getLength(){
		int tot = 0;
		if(status == 1){
			tot = rank>0?1:0;
			for(int i=0;i<rank;i++) tot *= dims[i];
		}
		return tot;
	}
	
	public RangeExpr getRangeExpr() {
		RangeExpr rtn = null;
		if (status == 1) {
			Expr left = new IntLiteralExpr(new DecIntNumericLiteralValue("" + dims[0]));
			Expr right = new NameExpr(new Name("" + dims[1]));
			rtn = new RangeExpr(left, new Opt(), right);
		} else if (status == 2) {
			Expr left = new IntLiteralExpr(new DecIntNumericLiteralValue(dimv[0]));
			Expr right = new NameExpr(new Name(dimv[1]));
			rtn = new RangeExpr(left, new Opt(), right);
		}
		return rtn;
	}
	
	/*
	 * Builtin
	 *   sum: reduce one dimension (skip 1) 
	 */
	public infoDim getDimSum(){
		infoDim rtn = new infoDim();
		if(status == 1){
			int slot = -1;
			for(int i=0;i<rank;i++){
				if(dims[i]!=1) {
					slot = i; break;
				}
			}
			if(slot<0) rtn.setDim2(1, 1);
			else {
				rtn.setDim(this);
				rtn.dims[slot] = 1; //set to one (int)
			}
		}
		else if(status == 2){
			int slot = -1;
			for(int i=0;i<rank;i++){
				if(!dimv[i].equals("1")) {
					slot = i; break;
				}
			}
			if(slot>=0){
				rtn.setDim(this);
				rtn.dimv[slot] = "1"; //set to one (string)
			}
			else rtn.setDim2(1, 1);//maybe impossible
		}
		return rtn;
	}
	
	/*
	 * size:
	 *   reduce dimension
	 */
	public infoDim getDimSize(){
		infoDim rtn = new infoDim();
		rtn.setDim2(1, rank);
		return rtn;
	}
	
	/*
	 * length, ndims, det:
	 *   return the length of max dim
	 */
	public infoDim getDimMax(){
		infoDim rtn = new infoDim();
		rtn.setDim2(1, 1);
		return rtn;
	}
	
	public String genForRange(){
		String rtn = "";
		if(status == 1){
			if(rank == 2){
				rtn = dims[0] + ":" + dims[1];
			}
		}
		else if(status == 2){
			if(rank == 2){
				rtn = dimv[0] + ":" + dimv[1];
			}
		}
		return rtn;
	}
	
	/*
	 * Set infoDim
	 */
//	public void setShape2(infoDim x){
//		rank = x.rank;
//		status = x.status;
//		if(x.status==1){
//			if(x.rank==1) {
//				dims[0]=dims[1]=x.dims[0];
//				rank = 2;
//			}
//			else {
//				for(int i=0;i<x.rank;i++) dims[i]=x.dims[i];
//			}
//		}
//		else if(x.status==2){
//			if(x.rank==1) { //expand rank
//				dimv[0]=dimv[1]=x.dimv[0];
//				rank = 2;
//			}
//			else {
//				for(int i=0;i<x.rank;i++) dimv[i]=x.dimv[i];
//			}
//		}
//	}
	
	@Override
	public String toString(){
		String rtn = (name==""?"<null>":name) + ":[";
		if(status==0) rtn += "unknown";
		else if(status == 1) {
			for(int i=0;i<rank;i++){
				if(i>0) rtn += ",";
				rtn += dims[i];
			}
		}
		else if(status == 2){
			for(int i=0;i<rank;i++){
				if(i>0) rtn += ",";
				rtn += dimv[i];
			}
		}
		rtn += "]";
		return rtn;
	}
}
