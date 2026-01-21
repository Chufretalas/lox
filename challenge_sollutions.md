## Chapter 5.1 Desugaring
expr → expr ( "(" ( expr ( "," expr )* )? ")" | "." IDENTIFIER )+
     | IDENTIFIER
     | NUMBER
     
expr -> NUMBER
expr -> IDENTIFIER

expr -> expr call

call -> "(" ")"
call -> "(" param_list ")"

param_list -> expr 
param_list -> expr extra_params

extra_params -> "," expr
extra_params -> "," expr extra_params

expr -> expr access

access -> "." IDENTIFIER