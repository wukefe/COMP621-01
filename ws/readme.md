
ParameterizedExpr
- s0: y(i) = x(i) + 1;
- * y(i) is supposed to be a ParameterizedExpr
- * s0.getLHS() instanceof ParameterizedExpr
- * NOT CellIndexExpr

How to get index var/num from ParameterizedExpr?
- There are two cases: ast.IntLiteralExpr and ast.Expr

Six steps
1. Set of assignments
2. Define domain:
3. Forward analysis
4. Dataflow equation: out = (in + gen) - kill
5. Merge: intersection
6. Initial state: out(start)= out(Si) = {}


Ifcond
	Map<Assignment, cond> 
	
currentInSet
	always empty? (solve it)