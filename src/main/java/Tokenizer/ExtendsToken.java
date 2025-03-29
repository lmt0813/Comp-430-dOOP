package Tokenizer;

public record ExtendsToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitExtendsToken();
    }        
}
