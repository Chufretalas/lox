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

declaration      → varDecl | statement ;

varDecl          → "var" IDENTIFIER ( "=" expression )? ";" ;

statement        → exprStmt | printStmt | block ;

block            → "{" declaration* "}" ;

printStmt        → "print" expression ";" ;
exprStmt         → expression ";" ;

expression       → comma_expression ;
comma_expression → assignment ( "," assignment )* ;
assignment       → IDENTIFIER "=" assignment | ternary ;
ternary          → equality ( "?" expression ":" ternary )? ;
equality         → comparison ( ( "!=" | "==" ) comparison )* ;
comparison       → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term             → factor ( ( "-" | "+" ) factor )* ;
factor           → unary ( ( "/" | "*" ) unary )* ;
unary            → ( "!" | "-" ) unary | primary ;
primary          → "true" | "false" | "nil"
                  | NUMBER | STRING
                  | "(" expression ")"
                  | IDENTIFIER ;
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
