package Typechecker;

import Parser.Exp;
import Parser.Stmt;

public record WhileStmt(Exp guard,
                        Stmt body) implements Stmt {}
