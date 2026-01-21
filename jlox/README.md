```bash
gradle console:run -q --console=plain
```

```bash
javac app/src/main/java/dev/chufretalas/tool/GenerateAst.java && java -cp app/src/main/java/ dev.chufretalas.tool.GenerateAst app/src/main/java/dev/chufretalas/lox
```

# jlox grammar
expression     → literal
               | unary
               | binary
               | grouping ;

literal        → NUMBER | STRING | "true" | "false" | "nil" ;

grouping       → "(" expression ")" ;

unary          → ( "-" | "!" ) expression ;

binary         → expression operator expression ;

operator       → "==" | "!=" | "<" | "<=" | ">" | ">="
               | "+"  | "-"  | "*" | "/" ;