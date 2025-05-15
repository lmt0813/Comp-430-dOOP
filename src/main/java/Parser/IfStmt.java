package Parser;

import java.util.Optional;

public record IfStmt(Exp guard, Stmt thenBranch, Optional<Stmt> elseBranch) implements Stmt {}