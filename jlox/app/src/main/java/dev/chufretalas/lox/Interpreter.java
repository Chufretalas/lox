package dev.chufretalas.lox;

import static dev.chufretalas.lox.TokenType.IDENTIFIER;

import dev.chufretalas.lox.Expr.Function;
import dev.chufretalas.lox.Expr.Grouping;
import dev.chufretalas.lox.Expr.Literal;
import dev.chufretalas.lox.Expr.Ternary;
import dev.chufretalas.lox.Stmt.Break;
import dev.chufretalas.lox.Stmt.Continue;
import dev.chufretalas.lox.Stmt.For;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private class BreakException extends RuntimeException {}

    private class ContinueException extends RuntimeException {}

    final Environment globals = new Environment(); // This guy stays fixed to the outermost environment
    private Environment environment = globals; // This guys changes with scope

    private final Map<Expr, Integer> locals = new HashMap<>(); // Stores the environment depth of varibles, so that they aways go to the correct environment

    private static int anonFunctionCounter = 0; // For naming anonymous functions

    Interpreter() {
        globals.define(
            "clock",
            new LoxCallable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(
                    Interpreter interpreter,
                    List<Object> arguments
                ) {
                    return (double) System.currentTimeMillis() / 1000.0;
                }

                @Override
                public String toString() {
                    return "<native fn>";
                }
            }
        );
    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left; // SHORT-CIRCUIT
        } else {
            if (!isTruthy(left)) return left; // SHORT-CIRCUIT
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right;
            default:
            // diddly squat
        }

        // Unreachable.
        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }

                if (left instanceof String) {
                    return (String) left + stringify(right);
                }

                if (right instanceof String) {
                    return stringify(left) + (String) right;
                }

                throw new RuntimeError(
                    expr.operator,
                    "Operands must be two numbers or two strings."
                );
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if ((double) right == 0.0) {
                    throw new RuntimeError(
                        expr.operator,
                        "Can not divide by zero."
                    );
                }
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case COMMA:
                return right;
            default:
            // nothing at all
        }

        // Unreachable.
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(
                expr.paren,
                "Can only call functions and classes."
            );
        }

        LoxCallable function = (LoxCallable) callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(
                expr.paren,
                "Expected " +
                    function.arity() +
                    " arguments but got " +
                    arguments.size() +
                    "."
            );
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visitTernaryExpr(Ternary expr) {
        Object conditionObj = evaluate(expr.condition);

        if (isTruthy(conditionObj)) {
            return evaluate(expr.trueExpr);
        }

        return evaluate(expr.falseExpr);
    }

    @Override
    public Object visitFunctionExpr(Function expr) {
        String name = "anonFn" + (anonFunctionCounter++);

        Token nameToken = new Token(IDENTIFIER, name, null, 0);

        Stmt.Function functionStmt = new Stmt.Function(
            nameToken,
            expr.params,
            expr.body
        );

        LoxFunction function = new LoxFunction(functionStmt, environment);

        return function;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(
        Token operator,
        Object left,
        Object right
    ) {
        if (left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);

        throw new Return(value);
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = Environment.UNINITIALIZED;

        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch (BreakException e) {
                // Taking advantage of Java's exception system to implement break and continue
                break;
            } catch (ContinueException e) {
            }
        }
        return null;
    }

    @Override
    public Void visitForStmt(For stmt) {
        if (stmt.initializer != null) {
            execute(stmt.initializer);
        }

        while (isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
                if (stmt.increment != null) {
                    evaluate(stmt.increment);
                }
            } catch (BreakException e) {
                // Taking advantage of Java's exception system to implement break and continue
                break;
            } catch (ContinueException e) {
                if (stmt.increment != null) {
                    evaluate(stmt.increment);
                }
            }
        }
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }

        return value;
    }

    @Override
    public Void visitBreakStmt(Break stmt) throws BreakException {
        throw new BreakException();
    }

    @Override
    public Void visitContinueStmt(Continue stmt) throws ContinueException {
        throw new ContinueException();
    }
}
