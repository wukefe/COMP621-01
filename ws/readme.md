## To-do Lists

1. Read the whole file to get the list of functions
2. Process for-loop
3. Process if-else inside for-loop
4. Apply dataflow equation
5. Generate transformed code - format in vectorization
6. Generate transformed code - format in parfor
7. Benchmark tests
8. Write up reports

## Implementation Questions

ParameterizedExpr

- s0: y(i) = x(i) + 1;
- y(i) is supposed to be a ParameterizedExpr
- s0.getLHS() instanceof ParameterizedExpr
- NOT CellIndexExpr

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

- `Map<Assignment, cond>`: `cond` is a string
	
currentInSet

- always empty? (solve it)


## McSAF and Tamer notes

Expressions

- PlusExpr: `+`, plus
- MinusExpr: `-`, minus
- MTimesExpr: `*`, matrix multiplication
- MDivExpr: `/`, matrix inversion
- ETimesExpr: `.*`, element-wise multiplication
- EDivExpr: `./`, element-wise division
- RangeExpr: `:`, 1:n

Constant

- IntLiteralExpr: integer
- FPLiteralExpr: floating point
- StringLiteralExpr: 'string'


## Built-in list

Done

1. zeros
2. input


To-do

- sum
- and, or (logic)


