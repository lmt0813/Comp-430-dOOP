package tokenization_example;

public interface Token {
    public <A> A visit(TokenVisitor<A> v);
}