package Tokenizer;

public record VarToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitVarToken();
    }        
}
