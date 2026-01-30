// package dev.chufretalas.lox;

// // The same think as the other print visitor, but this one does reverse polish notation
// // Obs: Only works well for binary expressions
// class AstRpnPrinter implements Expr.Visitor<String> {

//     String print(Expr expr) {
//         return expr.accept(this);
//     }

//     @Override
//     public String visitTernaryExpr(Expr.Ternary expr) {
//         return parenthesize(
//             "?:",
//             expr.condition,
//             expr.trueExpr,
//             expr.falseExpr
//         );
//     }

//     @Override
//     public String visitBinaryExpr(Expr.Binary expr) {
//         return parenthesize(expr.operator.lexeme, expr.left, expr.right);
//     }

//     @Override
//     public String visitGroupingExpr(Expr.Grouping expr) {
//         return parenthesize("group", expr.expression);
//     }

//     @Override
//     public String visitLiteralExpr(Expr.Literal expr) {
//         // Breaks the recursive calls to parenthesize
//         if (expr.value == null) return "nil";
//         return expr.value.toString();
//     }

//     @Override
//     public String visitUnaryExpr(Expr.Unary expr) {
//         return parenthesize(expr.operator.lexeme, expr.right);
//     }

//     private String parenthesize(String name, Expr... exprs) {
//         StringBuilder builder = new StringBuilder();

//         for (Expr expr : exprs) {
//             builder.append(" ");
//             builder.append(expr.accept(this));
//         }
//         builder.append(" ").append(name);

//         return builder.toString().trim();
//     }

//     public static void main(String[] args) {
//         System.out.println("Rpn printer test");
//         Expr expression = new Expr.Binary(
//             new Expr.Binary(
//                 new Expr.Literal(1),
//                 new Token(TokenType.PLUS, "+", null, 1),
//                 new Expr.Literal(2)
//             ),
//             new Token(TokenType.STAR, "*", null, 1),
//             new Expr.Binary(
//                 new Expr.Literal(4),
//                 new Token(TokenType.MINUS, "-", null, 1),
//                 new Expr.Literal(3)
//             )
//         );

//         System.out.println(new AstRpnPrinter().print(expression));
//     }
// }
