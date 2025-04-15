package Parser;

import java.util.List;

public record Program(List<ClassDef> classDefs, List<Stmt> stmts) {}