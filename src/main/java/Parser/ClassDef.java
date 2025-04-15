package Parser;

import java.util.List;
import java.util.Optional;

public record ClassDef(String className, Optional<String> extendsClass, List<Vardecl> varDecs,
List<Constructor> constructors, List<MethodDef> methodDefs) implements Stmt {}
