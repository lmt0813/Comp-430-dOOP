package Typechecker;

import java.util.List;

import Parser.Exp;

public record NewExp(String name, List<Exp> args) implements Exp{}
