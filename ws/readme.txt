
ParameterizedExpr
	s0: y(i) = x(i) + 1;
	* y(i) is supposed to be a ParameterizedExpr
	* s0.getLHS() instanceof ParameterizedExpr
	* NOT CellIndexExpr

How to get index var/num from ParameterizedExpr?
	There are two cases: ast.IntLiteralExpr and ast.Expr

