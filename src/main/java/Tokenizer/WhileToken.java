package Tokenizer;

public record WhileToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitWhileToken();
    }        
}
