package Parser;

import java.util.List;

public record MethodDef(
    String name,
    List<Vardecl> parameters,  // Vardecl uses String type
    String returnType,        // String type name
    List<Stmt> body
) {}
