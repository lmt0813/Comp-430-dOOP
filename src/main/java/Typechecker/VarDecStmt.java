package Typechecker;

import Parser.Exp;
import Parser.Stmt;

public record VarDecStmt(Type type,
                         Variable var,
                         Exp exp) implements Stmt {}

                         
