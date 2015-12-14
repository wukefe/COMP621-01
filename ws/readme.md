## data dependency check

Denote a statement as a candidate of transformable statement as `d`
Denote variables in LHS as `dl`
Denote variables in RHS as `dr`
Denote variables in `d` as `dv`

### If-else block

- Predefine dummy variables for conditions (if there is any statement transformable)
  - conditionvar1 = "x > 1"
  - conditionvar2 = "~(x > 1)"
  - ... goes on
- 


## Benchmark Go through

### blackscholes

Not supported list (builtin)

- `strcat`
- `reshape`

### bestresponse



### md (~~cannot go through~~)

Not supported primitive

- `str2num`

### nbodyvect (~~cannot go through~~)

Not supported primitives

- `textread`
- [repmat](http://www.mathworks.com/help/matlab/ref/repmat.html): repeat copies of array
- [ode113](http://www.mathworks.com/help/matlab/ref/ode113.html): solve nonstiff differential equations

Changes

- comment `plot_planet_path` function
- temporarily remove textread and reshape

### rch (~~cannot go through~~)

Not supported primitives

- [tsearchn](http://www.mathworks.com/help/matlab/ref/tsearchn.html): N-D closest simplex search
- [mat2str](http://www.mathworks.com/help/matlab/ref/mat2str.html): convent matrix to string
- [delaunayn](http://www.mathworks.com/help/matlab/ref/delaunayn.html): N-D Delaunay triangulation
- [convhulln](http://www.mathworks.com/help/matlab/ref/convhulln.html): N-D convex hull

### tsp (~~cannot go through~~)

A complex data structure can be initialized in the following way.

```matlab
Data(NumOfDataSets).S=[];
Data(NumOfDataSets).l=0;
Data(NumOfDataSets).cost=inf;
Data(NumOfDataSets).pre=[];
Data(NumOfDataSets).m=[];
LookUpTable(NumOfDataSets)=0;
%Define a data structure that holds the following pieces of data we need
%for later. This data structure uses the same notation used in the paper 
%by Held and Karp (1962):
```

### MNISTBenchmark (~~cannot go through~~)

Note: *Miss an `end` at the end of the program*

Not supported primitives

- `repmat`



### keypointsdetectionprogram (~~cannot go through~~)

Not supported primitives

- [imresize](http://www.mathworks.com/help/images/ref/imresize.html): resize image
- [rgb2gray](http://www.mathworks.com/help/matlab/ref/rgb2gray.html): convert RGB image or colormap to grayscale
- [im2double](http://www.mathworks.com/help/matlab/ref/im2double.html): convert image to double precision


### gaborfilter (~~cannot go through~~)

Note: *add an `end` at the end of the program `gaborFilterBank`*

Not supported primitives

- [downsample](http://www.mathworks.com/help/signal/ref/downsample.html): decrease sampling rate by integer factor
- [imfilter](http://www.mathworks.com/help/images/ref/imfilter.html): N-D filtering of multidimensional images

### kmeans (~~cannot go through~~)

Not supported primitives

- [spdiags](http://www.mathworks.com/help/matlab/ref/spdiags.html): extract and create sparse band and diagonal matrices
- [bsxfun](http://www.mathworks.com/help/matlab/ref/bsxfun.html): apply element-by-element binary operation to two arrays with singleton expansion enabled

### CLLL (~~cannot go through~~)

Note: *COMPLEX* is not supported.
Input type: [BuiltinMatlabClass](https://github.com/Sable/mclab-core/blob/50d7a655adfc62d963905792e490b73cffa69de3/languages/Natlab/src/natlab/tame/classes/BuiltinMatlabClass.java)

Error: `UnsupportedOperationException` in (ShapePropagator.java:49)

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


