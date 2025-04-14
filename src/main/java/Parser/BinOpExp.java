package Parser;

import Tokenizer.*;

public record BinOpExp(Exp left,
                       Op op,
                       Exp right) implements Exp {} 