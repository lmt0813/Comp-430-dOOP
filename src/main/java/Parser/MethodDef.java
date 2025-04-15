package Parser;

import java.util.List;

public record MethodDef(String methodName, CommaVardec commaVardec, Type returnType, List<Stmt> stmts) implements Stmt {}
