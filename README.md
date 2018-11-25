# Explain

A parser for [XPLN], written by Berk Özbalcı.

[XPLN]: https://github.com/bozsahin/ceng444/blob/master/project-material/xpl-specs-fall2018.pdf

## Requirements

Requires ANTLRv4. Note that this project was built using ANTLR version 4.7.1. Older versions may not be compatible.

## Usage

Build using Make:

    make

Run:

    java Explain examples/celcius/celcius.xpln

See the `examples` directory for example XPLN programs.

## Extensions to XPLN

This section contains all changes made to XPLN when implementing the parser.


### Language Specification

1. The requirement to have a statement (and not a `fun` definition) at the beginning of XPLN source has been lifted.
2. Logical expressions (arithmetic comparisons and Boolean algebra using logical connectives) have been modified;
    * Logical expressions support parenthesization.
    * Logical conjunction operator `and` has higher precedence than logical disjunction operator `or`.
    * The logical connectives are left-associative.
3. Function calls can take any `rvalue` (arithmetic expressions) as parameters, as opposed to names only.


### Errors and Warnings

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

## Examples

Here is a list of all the example programs bundled with Explain, with instructions on where to find them, expected
results and additional notes.


### Celcius

A very simple XPLN program that converts temperature units.

Run with:

    java Example examples/celcius/celcius.xpln

Expected output is at `examples/celcius/output`.


### All Features

This example features all of the standard language features of XPLN (complete coverage).

Run with:

    java Example examples/all_features/all_features.xpln

Expected output is at `examples/all_features/output`.


### Extensions

This example tests all of the added features to the XPLN specification (source files beginning with a `fun`
definition, logical expression parenthesization, precedence and associativity; `rvalue` function call
parameters).

Run with:

    java Example examples/extensions/extensions.xpln

Expected output is at `examples/extensions/output`.


### Multiple Function Definitions

This example defines a function `foo` two times. To demonstrate that the last definition is persisted, a
call is made to the second one (the argument count would mismatch the first one and generate another warning).

Run with:

    java Example examples/multiple_defs/multiple_defs.xpln

This should output the following to the standard error:

    examples/multiple_defs/multiple_defs.xpln:5:0: warning: function with name foo was already defined at line 1
    1 warning generated.

Expected output (parse tree) is at `examples/multiple_defs/output`.


### Functions Without Return Statements

This example defines three functions: `direct`, `nested`, and `none`. The first two have `return` statements
inside the function body, and the last one does not. `nested` has a `return` statement that is not reachable,
but we are not performing data-flow analysis so it will not produce an error.

Run with:

    java Example examples/no_return_func/no_return_func.xpln

This should output the following to the standard error:

    examples/no_return_func/no_return_func.xpln:11:0: error: function none is missing a return statement
    1 error generated.

Expected output (parse tree) is at `examples/no_return_func/output`.


### Call to Undefined Function

This example defines a function, `fib`, that has recursive definition. In the program body, `fib` is mistyped
as `ffib`, and there is no matching function definition for `ffib` so we expect an error. This program also
demonstrates that XPLN supports explicit recursion (but not mutual recursion yet).

Run with:

    java Example examples/call_undefined/call_undefined.xpln

This should output the following to the standard error:

    examples/call_undefined/call_undefined.xpln:9:10: error: call to undefined function ffib
    1 error generated.

Expected output (parse tree) is at `examples/call_undefined/output`.


### Argument Count Mismatch

This example defines a function `foo`, then calls it twice (once with too few arguments, once with too many
arguments).

Run with:

    java Example examples/argcount/argcount.xpln

This should output the following to the standard error:

    examples/argcount/argcount.xpln:5:7: warning: too few arguments passed to foo, expected 3, have 2
    examples/argcount/argcount.xpln:5:19: warning: too many arguments passed to foo, expected 3, have 4
    2 warnings generated.

Expected output (parse tree) is at `examples/argcount/output`.


### Program Body Without Return Statement

This example is a program without a return statement. It is expected to produce an error.

Run with:

    java Example examples/no_return_prog/no_return_prog.xpln

This should output the following to the standard error:

    examples/no_return_prog/no_return_prog.xpln: error: program body is missing a return statement
    1 error generated.

Expected output (parse tree) is at `examples/no_return_prog/output`.


### Good Tokens, Ill-Formed Structure

This example contains a myriad of syntax errors (incorrect usage of keywords, unmatched `if` and `endi`,
unmatched `else` block, etc.)

Run with:

    java Example examples/ill_formed/ill_formed.xpln

This should output the following to the standard error:

    examples/ill_formed/ill_formed.xpln:1:5: error: mismatched input 'fun' expecting ID
    fun fun(fun)
        ^
    examples/ill_formed/ill_formed.xpln:1:8: error: missing ID at '('
    fun fun(fun)
           ^
    examples/ill_formed/ill_formed.xpln:1:9: error: extraneous input 'fun' expecting {')', ID}
    fun fun(fun)
            ^
    examples/ill_formed/ill_formed.xpln:2:12: error: mismatched input 'fun' expecting {'(', ID, NUM}
        return fun;
               ^
    examples/ill_formed/ill_formed.xpln:6:1: error: extraneous input 'and' expecting {<EOF>, K_FUN, K_WHILE, K_IF, K_RETURN, K_INPUT, K_OUTPUT, ID}
    and := 3;
    ^
    examples/ill_formed/ill_formed.xpln:8:4: error: mismatched input 'and' expecting {'(', '!', ID, NUM}
    if and == 3
       ^
    examples/ill_formed/ill_formed.xpln:12:1: error: extraneous input 'else' expecting {<EOF>, K_FUN, K_WHILE, K_IF, K_RETURN, K_INPUT, K_OUTPUT, ID}
    else and == 5
    ^
    examples/ill_formed/ill_formed.xpln:14:1: error: extraneous input 'endi' expecting {<EOF>, K_FUN, K_WHILE, K_IF, K_RETURN, K_INPUT, K_OUTPUT, ID}
    endi;
    ^
    examples/ill_formed/ill_formed.xpln:16:8: error: mismatched input 'return' expecting {'(', ID, NUM}
    return return;
           ^
    9 errors generated.

This program does not output a parse tree since the input program has ill-formed structure.


### Bad Tokens

This program has a few bad tokens (misspelled keyword, number in scientific format, unrecognized symbol)

Run with:

    java Example examples/bad_tokens/bad_tokens.xpln

This should output the following to the standard error:

    examples/bad_tokens/bad_tokens.xpln:1:5: error: missing ':=' at 'foo'
    fum foo(a, b)
        ^
    examples/bad_tokens/bad_tokens.xpln:2:5: error: missing ';' at 'return'
        return 1e3;
        ^
    examples/bad_tokens/bad_tokens.xpln:2:13: error: missing ';' at 'e'
        return 1e3;
                ^
    examples/bad_tokens/bad_tokens.xpln:2:14: error: missing ':=' at '3'
        return 1e3;
                 ^
    examples/bad_tokens/bad_tokens.xpln:3:1: error: extraneous input 'endf' expecting {<EOF>, K_FUN, K_WHILE, K_IF, K_RETURN, K_INPUT, K_OUTPUT, ID}
    endf
    ^
    line 5:15 token recognition error at: '$'
    examples/bad_tokens/bad_tokens.xpln:6:1: error: missing ';' at 'return'
    return x;
    ^
    6 errors generated.

This program does not output a parse tree since the input program has bad tokens did not yield a clean parse.
