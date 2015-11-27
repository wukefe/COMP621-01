## Implementation Questions

transformAssignment

- [Not yet] consider a variable as a function
- [Not yet]  

ParameterizedExpr

- s0: y(i) = x(i) + 1;
- y(i) is supposed to be a ParameterizedExpr
- s0.getLHS() instanceof ParameterizedExpr
- NOT CellIndexExpr

How to get index var/num from ParameterizedExpr?

- There are two cases: ast.IntLiteralExpr and ast.Expr


Ifcond

- `Map<Assignment, cond>`: `cond` is a string
	
currentInSet

- always empty? (solve it)

## Restrictions

### Case 1

```matlab
if n > 10
	w = 1;
else
	w = 2;
```

**Error message**:

Parse error: [1, 1] Function lacks an explicit end.  If any function has an explicit end, then all must.

### Case 2

```matlab
w = zeros(1, 1);
w = zeros(1, 1);
```

**Compiled successfully**


### Case 3

```matlab
function [res] = foo(x)
	w = zeros(1, 5);
	for i=1:5
		k = 1 + i;
		w(i) = k - 1;
	res = x;
end
```

**The same error as case 1**

## McSAF and Tamer notes

Expressions

- PlusExpr: `+`, plus
- MinusExpr: `-`, minus
- MTimesExpr: `*`, matrix multiplication
- MDivExpr: `/`, matrix inversion
- ETimesExpr: `.*`, element-wise multiplication
- EDivExpr: `./`, element-wise division
- RangeExpr: `:`, 1:n

Logic

- LTExpr: `<`
- LEExpr: `<=`
- GTExpr: `>`
- GEExpr: `>=`
- EQExpr: `=`
- NEExpr: `~=`
- NotExpr:`~`

Constant

- IntLiteralExpr: integer
- FPLiteralExpr: floating point
- StringLiteralExpr: 'string'


## Built-in list

Done

- StringGroup1: `zeros,ones,eye,rand,randn`
- StringGroup2: `sqrt,exp,log,log10,log2,pow2,abs,ceil,floor,round,sin,cos,tan,asin,acos,atan`
- StringGroup3: `sum,max,min,mean,std,prod,median`
- `input`
- `size`
- `length,ndims,det`


To-do

- and, or (logic)
- ones

## M-File mode

Script only
- no function is allowed
- only statements
- see `demo0.m`

Function only
- no statement outside function
- accept multiple functions
- see `demo2.m`


