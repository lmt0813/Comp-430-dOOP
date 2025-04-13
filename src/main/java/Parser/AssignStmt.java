package Parser;

public record AssignStmt(String name, Exp e) implements Stmt {}