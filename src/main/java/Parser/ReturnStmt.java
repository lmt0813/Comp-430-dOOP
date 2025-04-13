package Parser; 

import java.util.Optional;

public record ReturnStmt(Optional<Exp> exp) implements Stmt {}