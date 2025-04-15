package Parser;

import java.util.List;

public record MethodDef(String methodName, List<Vardecl> result, Type result2, List<Stmt> stmts2) implements Stmt {}
