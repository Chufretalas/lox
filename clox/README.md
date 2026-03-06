# clox

## OPCODES

[ OP_CONSTANT (1) ] [ CONSTANT_IDX (1) ]

[ OP_CONSTANT_LONG (1) ] [ CONSTANT_IDX (3) ]

[ OP_RETURN  (1) ]

## Things to implement

**Ternary**
- Scanner for TOKEN_QUESTION and TOKEN_COLON
- Parser with a parser function something that looks like this and putting the TOKEN_QUESTION as a infix operator in the table tha refers to it (the rule for TOKEN_COLON must be empty, since there's no operation that starts with it). Somewhat following the jlox grammar.
```
ternary → logic_or ( "?" expression ":" ternary )? ;
```

```c
[TOKEN_QUESTION]      = {NULL,     ternary, PREC_TERNARY}
```

```c
static void ternary() {
    parsePrecedence(PREC_EXPRESSION); 

    consume(TOKEN_COLON, "Expect ':' after '?' expression.");
    
    parsePrecedence(PREC_TERNARY);

    // TODO: emit bytecode
}
```
- VM interpretation (but I guess the future if code might handle the same bytecode).