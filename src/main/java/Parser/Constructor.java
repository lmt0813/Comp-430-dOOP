package Parser;

import java.util.List;

public record Constructor(CommaVardec commaVardec, Exp superExp, List<Stmt> stmts) implements Stmt {}
