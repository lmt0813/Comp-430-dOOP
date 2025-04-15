package Parser;

public record WhileStmt(Exp condition, Stmt body) implements Stmt {}