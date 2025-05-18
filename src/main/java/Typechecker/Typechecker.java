package Typechecker;

import java.util.Map;

import Parser.*;
import Tokenizer.*;

import java.util.HashMap;
import java.util.List;

public class Typechecker {
    public static Map<Variable, Type> addMap(Map<Variable, Type> env, Variable var, Type type) {
        final Map<Variable, Type> retval = new HashMap<Variable, Type>(env);
        retval.put(var, type);
        return retval;
    }

    public static Map<Variable, Type> typecheck(Stmt stmt, Map<Variable, Type> env) throws TypeErrorException {
        if (stmt instanceof WhileStmt ws) {
            if (typeOf(ws.guard(), env) instanceof BoolType) {
                typecheck(ws.body(), env);
                return env;
            } else {
                throw new TypeErrorException("Guard non-boolean");
            }
        } else if (stmt instanceof IfStmt is) {
            Type condType = typeOf(is.guard(), env);
            if (!(condType instanceof BoolType)) {
                throw new TypeErrorException("if condition must be boolean");
            }
            typecheck(is.thenBranch(), env);
            if (is.elseBranch().isPresent()) {
                typecheck(is.elseBranch().get(), env);
            }
            return env;
        } else if (stmt instanceof BlockStmt block) {
            Map<Variable, Type> innerEnv = env;
            for (Stmt innerStmt : block.stmts()) {
                innerEnv = typecheck(innerStmt, innerEnv);
            }
            return env;
        } else if (stmt instanceof VarDecStmt vds) {
            final Type receivedType = typeOf(vds.exp(), env);
            if (receivedType instanceof VoidType) {
                throw new TypeErrorException("Void cannot be used as a value");
            }
            if (receivedType.equals(vds.type())) {
                return addMap(env, vds.var(), receivedType);
            } else {
                throw new TypeErrorException("Received type: " + receivedType);
            }
        } else {
            assert false;
            throw new TypeErrorException("No such statement: " + stmt);
        }
    }

    public static Map<String, ClassDef> classEnv = new HashMap<>();

    public static Type typeOf(final Exp e, final Map<Variable, Type> env) throws TypeErrorException {
        if (e instanceof VarExp ve) {
            final Variable name = ve.v();
            if (env.containsKey(name)) {
                return env.get(name);
            } else {
                throw new TypeErrorException("Variable not in scope: " + name);
            }
        } else if (e instanceof IntExp) {
            return new IntType();
        } else if (e instanceof TrueExp || e instanceof FalseExp) {
            return new BoolType();
        } else if (e instanceof BinOpExp boe) {
            Type leftType = typeOf(boe.left(), env);
            Type rightType = typeOf(boe.right(), env);

            if (boe.op() instanceof PlusOp &&
                    leftType instanceof IntType &&
                    rightType instanceof IntType) {
                return new IntType();
            } else if (boe.op() instanceof LessThanOp &&
                    leftType instanceof IntType &&
                    rightType instanceof IntType) {
                return new BoolType();
            } else if (boe.op() instanceof AndOp &&
                    leftType instanceof BoolType &&
                    rightType instanceof BoolType) {
                return new BoolType();
            } else {
                throw new TypeErrorException("Bad operator");
            }
        } else if (e instanceof NewExp ne) {
            String className = ne.name();

            if (!classEnv.containsKey(className)) {
                throw new TypeErrorException("No such class: " + className);
            }

            ClassDef classDef = classEnv.get(className);
            Constructor constructor = classDef.constructor();

            List<Type> expectedTypes = constructor.paramTypes();
            List<Exp> actualArgs = ne.args();

            if (expectedTypes.size() != actualArgs.size()) {
                throw new TypeErrorException("Constructor for class " + className + " expects " +
                        expectedTypes.size() + " arguments, but got " + actualArgs.size());
            }

            for (int i = 0; i < expectedTypes.size(); i++) {
                Type expected = expectedTypes.get(i);
                Type actual = typeOf(actualArgs.get(i), env);
                if (!actual.equals(expected)) {
                    throw new TypeErrorException("Constructor argument " + i + " for class " + className +
                            " expected " + expected + ", but got " + actual);
                }
            }

            return new ClassType(className);
        } else {
            throw new TypeErrorException("Unknown expression");
        }
    }

    public static void typechecks(Program p) throws TypeErrorException {
        typecheck(p.stmt(), new HashMap<Variable, Type>());
    }
}
