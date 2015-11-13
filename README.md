### Project: Automatic Vectorization for MATLAB

---
Hanfeng Chen

The details of the development note can be found in the folder `ws` [link](ws/readme.md)

## Time lines

- ** Project proposal ** on November 4, 2015
- ** First report for update ** on November 13, 2015
- ** Second report for update ** on xxx
- ** Next update... **

## To-do Lists

1. Get the list of functions
2. Shape analysis
   - script
   - function with parameter input
   - function with body
   - primitives
   - builtins 
3. Process for-loop
4. Process if-else inside for-loop
5. Apply dataflow equation
6. Generate transformed code - format in vectorization
7. Generate transformed code - format in parfor
8. Benchmark tests
9. Write up reports

## File Structure

`./`

- ws (main workspace)
  * code
    - demo files
    - demo args
  * Java files: autoVector.java (main) and so on
- README.md