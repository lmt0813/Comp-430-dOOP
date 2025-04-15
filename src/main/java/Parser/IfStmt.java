package Parser;

import java.util.Optional;

public record IfStmt(Exp condition, Stmt thenBranch, Optional<Stmt> elseBranch) implements Stmt {}