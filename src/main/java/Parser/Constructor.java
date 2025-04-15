package Parser;

import java.util.List;
import java.util.Optional;

public record Constructor(List<Vardecl> commaVardec, Optional<Exp> superExp, List<Stmt> stmts) implements Stmt {}
