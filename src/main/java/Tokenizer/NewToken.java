package Tokenizer;

public record NewToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitNewToken();
    }        
}
