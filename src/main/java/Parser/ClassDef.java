package Parser;

import java.util.List;

public record ClassDef(String className, String extendsClassName, List<Vardecl> vardecs, Constructor constructor, List<MethodDef> methodDefs) implements Stmt {}
