/*
 * Grammar for Xpln
 */

grammar Xpln;

/* start symbol */
file_input
  : entry* EOF
  ;

entry
  : (stmt | funcdef) SCOL
  ;

stmt
  : assn_stmt
  | if_stmt
  | while_stmt
  | return_stmt
  | io_stmt
  ;

block_stmt
  : (stmt SCOL)*
  ;

funcdef
  : K_FUN ID arglist block_stmt K_ENDF
  ;

assn_stmt
  : ID ASSIGN expr
  ;

if_stmt
  : K_IF cond_expr block_stmt else_block? K_ENDI
  ;

else_block
  : K_ELSE block_stmt
  ;

while_stmt
  : K_WHILE cond_expr block_stmt K_ENDW
  ;

return_stmt
  : K_RETURN expr
  ;

io_stmt
  : (K_INPUT | K_OUTPUT) ID
  ;

expr
  : expr (PLUS | MINUS) term
  | term
  ;

term
  : term (TIMES | DIV) factor
  | factor
  ;

factor
  : OPEN_PAR expr CLOSE_PAR
  | funccall
  | ID
  | NUM
  ;            

funccall
  : ID arglist_call
  ;

cond_expr
  : OPEN_PAR cond_expr CLOSE_PAR                 # CondExprParen
  | NOT cond_expr                                # CondExprLogicalNot
  | left=cond_expr op=K_AND right=cond_expr      # CondExprLogicalAnd
  | left=cond_expr op=K_OR right=cond_expr       # CondExprLogicalOr
  | left=expr op=comp_op right=expr              # CondExprArithmetic
  ;

arglist
  : OPEN_PAR (ID (COMMA ID)*)? CLOSE_PAR
  ;

arglist_call
  : OPEN_PAR (expr (COMMA expr)*)? CLOSE_PAR
  ;
         
comp_op
  : LT
  | LT_EQ
  | EQ
  | GT_EQ
  | GT
  ;

/* symbols */
SCOL      : ';';
DOT       : '.';
OPEN_PAR  : '(';
CLOSE_PAR : ')';
COMMA     : ',';
ASSIGN    : ':=';
PLUS      : '+';
MINUS     : '-';
TIMES     : '*';
DIV       : '/';
LT        : '<';
LT_EQ     : '<=';
EQ        : '==';
GT_EQ     : '>=';
GT        : '>';
NOT       : '!';

/* keywords, case-insensitive */
K_FUN    : F U N;
K_ENDF   : E N D F;
K_WHILE  : W H I L E;
K_ENDW   : E N D W;
K_IF     : I F;
K_ELSE   : E L S E;
K_ENDI   : E N D I;
K_RETURN : R E T U R N;
K_AND    : A N D;
K_OR     : O R;
K_INPUT  : I N P U T;
K_OUTPUT : O U T P U T;

/* general lexical rules */
ID    : [a-zA-Z]+;
NUM   : INT | FLOAT;
INT   : DIGIT+;
FLOAT : DIGIT* DOT DIGIT*;

/* miscellaneous lexical rules */
WS  : [ \t\n\r]+ -> skip;

fragment DIGIT
  : [0-9]
  ;

/* required for case-insensitive lexing,
 * see https://github.com/antlr/antlr4/blob/master/doc/case-insensitive-lexing.md
 */
fragment A : [Aa];
fragment B : [Bb];
fragment C : [Cc];
fragment D : [Dd];
fragment E : [Ee];
fragment F : [Ff];
fragment G : [Gg];
fragment H : [Hh];
fragment I : [Ii];
fragment J : [Jj];
fragment K : [Kk];
fragment L : [Ll];
fragment M : [Mm];
fragment N : [Nn];
fragment O : [Oo];
fragment P : [Pp];
fragment Q : [Qq];
fragment R : [Rr];
fragment S : [Ss];
fragment T : [Tt];
fragment U : [Uu];
fragment V : [Vv];
fragment W : [Ww];
fragment X : [Xx];
fragment Y : [Yy];
fragment Z : [Zz];
