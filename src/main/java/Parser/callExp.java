package Parser;

public record callExp(Exp primary,
                       String methodName,
                       Exp commaExp) implements Exp {} 
