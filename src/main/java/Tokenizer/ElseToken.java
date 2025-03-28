package Tokenizer;

public record ElseToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitElseToken();
    }        
}
