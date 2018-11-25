# Explain

A parser for XPLN.

## Requirements

Requires ANTLRv4. Note that this project was built using ANTLR version 4.7.1. Older versions may not be compatible.

## Usage

Build using Make:

    make

Run:

    java Explain examples/celcius.xpln

See the `examples` directory for example XPLN programs.

## Extensions to the XPLN

This section contains all changes made to XPLN when implementing the parser.


### Language specification

1. The requirement to have a statement (and not a `fun` definition) at the beginning of XPLN source has been lifted.
2. Logical expressions (arithmetic comparisons and Boolean algebra using logical connectives) have been modified;
    * Logical expressions support parenthesization.
    * Logical conjunction operator `and` has higher precedence than logical disjunction operator `or`.
    * The logical connectives are left-associative.
3. Function calls can take any `rvalue` (arithmetic expressions) as parameters, as opposed to names only.


### Errors and warnings

1. If the input file is not specified or not readable, the parser will yield a fatal error.
2. If a function was defined multiple times, the parser will yield a warning.
    * The last definition of the function is taken into account when performing argument count checks.
3. If a function is missing a return statement inside its body, the parser will yield an error.
    * As with all non-fatal errors, the parser will still output a parse tree.
4. If a function call is made to a function that was not defined, the parser will yield an error.
5. If the number of arguments in a function call mismatches the number of arguments formally defined within
   the function definition, the parser will produce a warning.
6. If the program body is missing a return statement outside of function definitions, the parser will produce
   a warning.
7. Syntax errors produced by the ANTLR parser generator has not been touched (for now) except that it supports
   pretty printing (underlines the offending token in the input file and has colored output).
