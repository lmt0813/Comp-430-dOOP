package Typechecker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import Parser.*;
import Tokenizer.*;

public class TypecheckerTest {
    @Test
    public void testValidWhileLoop() throws TypeErrorException {
        Exp guard = new BinOpExp(new IntExp(5), new LessThanOp(), new IntExp(3));
        Stmt body = new BlockStmt(List.of(
                new VarDecStmt(new IntType(), new Variable("x"), new IntExp(5))));
        Stmt whileStmt = new WhileStmt(guard, body);

        Map<Variable, Type> env = new HashMap<>();
        Typechecker.typecheck(whileStmt, env);
    }

    @Test
    public void testInvalidWhileGuard() {
        Exp guard = new IntExp(5);
        Stmt body = new BlockStmt(List.of());
        Stmt whileStmt = new WhileStmt(guard, body);

        Map<Variable, Type> env = new HashMap<>();

        assertThrows(TypeErrorException.class, () -> {
            Typechecker.typecheck(whileStmt, env);
        });
    }

    @Test
    public void testValidIfElse() throws TypeErrorException {
        Exp cond = new TrueExp();
        Stmt thenBranch = new VarDecStmt(new IntType(), new Variable("a"), new IntExp(1));
        Stmt elseBranch = new VarDecStmt(new BoolType(), new Variable("b"), new FalseExp());

        Stmt ifStmt = new IfStmt(cond, thenBranch, Optional.of(elseBranch));

        Map<Variable, Type> env = new HashMap<>();
        Typechecker.typecheck(ifStmt, env);
    }

    @Test
    public void testInvalidIfGuard() {
        Exp cond = new BinOpExp(new TrueExp(), new PlusOp(), new FalseExp());
        Stmt thenBranch = new BlockStmt(List.of());
        Stmt ifStmt = new IfStmt(cond, thenBranch, Optional.empty());

        Map<Variable, Type> env = new HashMap<>();

        assertThrows(TypeErrorException.class, () -> {
            Typechecker.typecheck(ifStmt, env);
        });
    }

    @Test
    public void testValidIfNoElse() throws TypeErrorException {
        Exp cond = new TrueExp();
        Stmt thenBranch = new BlockStmt(List.of(
                new VarDecStmt(new IntType(), new Variable("y"), new IntExp(10))));
        Stmt ifStmt = new IfStmt(cond, thenBranch, Optional.empty());

        Map<Variable, Type> env = new HashMap<>();
        Typechecker.typecheck(ifStmt, env);
    }

    @Test
    public void testLessThanIntInt() throws TypeErrorException {
        BinOpExp e = new BinOpExp(new IntExp(5), new LessThanOp(), new IntExp(3));
        Type result = Typechecker.typeOf(e, new HashMap<>());
        assertTrue(result instanceof BoolType);
    }

    @Test
    public void testAndBoolBool() throws TypeErrorException {
        BinOpExp e = new BinOpExp(new TrueExp(), new AndOp(), new FalseExp());
        Type result = Typechecker.typeOf(e, new HashMap<>());
        assertTrue(result instanceof BoolType);
    }

    @Test
    public void testInvalidPlusIntBool() {
        BinOpExp e = new BinOpExp(new IntExp(5), new PlusOp(), new TrueExp());
        assertThrows(TypeErrorException.class, () -> {
            Typechecker.typeOf(e, new HashMap<>());
        });
    }

    @Test
    public void testValidProgramVarDecl() throws TypeErrorException {
        Stmt stmt = new VarDecStmt(new IntType(), new Variable("x"), new IntExp(5));
        Program p = new Program(stmt);
        Typechecker.typechecks(p);
    }

    @Test
    public void testInvalidProgramTypeMismatch() {
        Stmt stmt = new VarDecStmt(new BoolType(), new Variable("flag"), new IntExp(1));
        Program p = new Program(stmt);
        assertThrows(TypeErrorException.class, () -> {
            Typechecker.typechecks(p);
        });
    }

    @Test
    public void testVarExpInScope() throws TypeErrorException {
        Variable x = new Variable("x");
        Exp e = new VarExp(x);

        Map<Variable, Type> env = new HashMap<>();
        env.put(x, new IntType());

        Type result = Typechecker.typeOf(e, env);
        assertTrue(result instanceof IntType);
    }

    @Test
    public void testVarExpNotInScope() {
        Variable x = new Variable("x");
        Exp e = new VarExp(x);

        Map<Variable, Type> env = new HashMap<>(); 

        assertThrows(TypeErrorException.class, () -> {
            Typechecker.typeOf(e, env);
        });
    }

    @Test
    public void testValidNewExp() throws TypeErrorException {
        NewExp e = new NewExp("A", List.of(new IntExp(5), new TrueExp()));

        Constructor actor = new Constructor(List.of(new IntType(), new BoolType()));
        ClassDef classA = new ClassDef("A", actor);
        Typechecker.classEnv.put("A", classA);

        Type result = Typechecker.typeOf(e, new HashMap<>());
        assertEquals(new ClassType("A"), result);
    }

    private void defineClass(String name, List<Type> paramTypes) {
        Constructor actor = new Constructor(paramTypes);
        ClassDef def = new ClassDef(name, actor);
        Typechecker.classEnv.put(name, def);
    }

    @Test
    public void testNewExpWrongArity() {
        defineClass("A", List.of(new IntType(), new BoolType()));

        NewExp e = new NewExp("A", List.of(
                new IntExp(5)));

        assertThrows(TypeErrorException.class, () -> {
            Typechecker.typeOf(e, new HashMap<>());
        });
    }

    @Test
    public void testNewExpWrongType() {
        defineClass("A", List.of(new IntType(), new BoolType()));

        NewExp e = new NewExp("A", List.of(
                new TrueExp(),
                new FalseExp()));

        assertThrows(TypeErrorException.class, () -> {
            Typechecker.typeOf(e, new HashMap<>());
        });
    }

    @Test
    public void testNewExpUnknownClass() {

        NewExp e = new NewExp("A", List.of(
                new IntExp(5),
                new BooleanExp(true)));

        assertThrows(TypeErrorException.class, () -> {
            Typechecker.typeOf(e, new HashMap<>());
        });
    }

}
