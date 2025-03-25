package Tokenizer;

public interface Token {
    public <A> A visit(TokenVisitor<A> v);
}