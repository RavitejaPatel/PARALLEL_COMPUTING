# Executing the scripts
## Sequential:
compile: javac sequential.java
execute: java sequential <N>
 - <N>: the length of the chess board. if no value is provided, shall default to 4


## Parallel:
compile: javac parallel.java
execute: java parallel <N> <max threads>
 - <N>: the length of the chess board. if no value is provided, shall default to 4
 - <max threads>: number of max threads for the program to use. if not value is provided, shall default to 13.