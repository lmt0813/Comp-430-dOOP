package Typechecker;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import Parser.BooleanExp;
import Parser.Exp;
import Parser.IfStmt;
import Parser.IntExp;
import Parser.Stmt;

public class TypecheckerTest {
    @Test
    public void testValidWhileLoop() throws TypeErrorException {
        Exp guard = new TrueExp();
        Stmt body = new BlockStmt(List.of(
                new VarDecStmt(new IntType(), new Variable("x"), new IntExp(5))));
        Stmt whileStmt = new WhileStmt(guard, body);

        Map<Variable, Type> env = new HashMap<>();
        Typechecker.typecheck(whileStmt, env); // Should not throw
    }

    @Test
    public void testInvalidWhileGuard() {
        Exp guard = new IntExp(5); // Not a BoolType!
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
        Typechecker.typecheck(ifStmt, env); // Should not throw
    }

    @Test
    public void testInvalidIfGuard() {
        Exp cond = new IntExp(5); // Not a boolean!
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
        Typechecker.typecheck(ifStmt, env); // Should not throw
    }
}
