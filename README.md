### Project: Automatic Vectorization for MATLAB

---
Hanfeng Chen

The details of the note for project development can be found in the folder `ws` ([Click here](ws/readme.md)).

## Timelines

- **Project proposal** on November 4, 2015 (Submitted)
- **1st project update** on November 13, 2015 (Submitted)
- **2nd project update** on November 20, 2015 (Submitted)
- **Project presentation** on December 2, 2015 (Submitted)
- **Final project report** on December 14, 2015 (Submitted)

## How to reproduce experiments in the final report

There are two ways.

- Eclipse:
  + Import the whole project into Eclipse
  + Find command lines in [commands.txt](ws/commands.txt)
  + Put each command into |Run Configurations| -> |Arguments| as arguments
- Command lines:
  + Tested on Ubuntu
  + Commands:
    1. mkdir project && cd project && move hanfeng-project01.zip project/
    2. unzip hanfeng-project01.zip
    3. make
  + The vectorized code can be found under each benchmark folder
    - e.g. BlackScholes:  mybenchmark/blackscholes/

After code is generated in the folder `mybenchmark`, it is better to read the `ReadMe.md` file to understand the usage of each benchamrk.

If you find any problem about the experiments, please send me email directly.

## To-do lists

1. Get the list of functions
2. Type and shape analysis (in Tamer framework)
3. Implement for-loop transformation
4. Implement if-else transformation
5. Generate transformed code - format in vector form
6. Generate transformed code - format in parfor
7. Test benchmark
8. Write up reports

## Project folder structure

```
+ ws (main workspace)
  + code
    - demo files
    - demo args
  - autoVector.java (main)
  - other Java files
- README.md
```
