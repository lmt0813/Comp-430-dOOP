package Tokenizer;

public record MethodToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitMethodToken();
    }        
}
