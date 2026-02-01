```bash
./gradlew --console=plain run
```

```bash
./gradlew --console=plain run --args='../../sample_scripts/script.lox'
```

```bash
javac app/src/main/java/dev/chufretalas/tool/GenerateAst.java && java -cp app/src/main/java/ dev.chufretalas.tool.GenerateAst app/src/main/java/dev/chufretalas/lox
```

# jlox grammar V1

expression → literal
| unary
| binary
| grouping ;

literal → NUMBER | STRING | "true" | "false" | "nil" ;

grouping → "(" expression ")" ;

unary → ( "-" | "!" ) expression ;

binary → expression operator expression ;

operator → "==" | "!=" | "<" | "<=" | ">" | ">="
| "+" | "-" | "\*" | "/" ;

# jlox grammar V2

```
program          → declaration* EOF ;

declaration      → funDecl | varDecl | statement ;

funDecl          → "fun" function ;

function         → IDENTIFIER "(" parameters? ")" block ;

parameters       → IDENTIFIER ( "," IDENTIFIER )* ;

varDecl          → "var" IDENTIFIER ( "=" expression )? ";" ;

statement        → exprStmt | forStmt | ifStmt | printStmt | returnStmt | whileStmt | block ;

ifStmt           → "if" "(" expression ")" statement ( "else" statement )? ;

whileStmt        → "while" "(" expression ")" statement ;

forStmt          → "for" "(" ( varDecl | exprStmt | ";" )
                   expression? ";"
                   expression? ")" statement ;

block            → "{" declaration* "}" ;

printStmt        → "print" expression ";" ;

returnStmt       → "return" expression? ";" ;

breakStmt        → "break" ";"
continueStmt     → "continue" ";"

exprStmt         → expression ";" ;

expression       → comma_expression ;
comma_expression → assignment ( "," assignment )* ;
assignment       → IDENTIFIER "=" assignment | ternary ;
ternary          → logic_or ( "?" expression ":" ternary )? ;
logic_or         → logic_and ( "or" logic_and )* ;
logic_and        → equality ( "and" equality )* ;
equality         → comparison ( ( "!=" | "==" ) comparison )* ;
comparison       → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term             → factor ( ( "-" | "+" ) factor )* ;
factor           → unary ( ( "/" | "*" ) unary )* ;
unary            → ( "!" | "-" ) unary | call ;
call             → primary ( "(" arguments? ")" )* ;
primary          → "true" | "false" | "nil"
                  | NUMBER | STRING
                  | "(" expression ")"
                  | IDENTIFIER ;
                  
arguments        → assignment ( "," assignment )* ;
```

## Error productions

```
e_leading_operator → ( "," ternary)
                    | ( "?" expression ":" ternary )
                    | ( ":" ternary )
                    | ( "!=" | "==" ) comparison
                    | ( ( ">" | ">=" | "<" | "<=" ) term )
                    | (  "+" factor )
                    | ( ( "/" | "*" ) unary );
```
