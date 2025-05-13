package Typechecker;

import java.util.List;

import Parser.Stmt;

public record BlockStmt(List<Stmt> stmts) implements Stmt {}
