## Benchmarks

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