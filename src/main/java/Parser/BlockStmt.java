package Parser;

import java.util.List;

public record BlockStmt(List<Stmt> stmts) implements Stmt {}