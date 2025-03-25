package tokenizer;

public interface Token {
    public <A> A visit(TokenVisitor<A> v);
}