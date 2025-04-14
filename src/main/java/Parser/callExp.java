package Parser;

import Tokenizer.*;

public record callExp(Exp primary,
                       Exp methodName,
                       Exp commaExp) implements Exp {} 
