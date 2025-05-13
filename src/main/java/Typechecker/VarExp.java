package Typechecker;

import Parser.Exp;

public record VarExp(Variable v) implements Exp {}
