package Tokenizer;

public record IdentifierToken(String name) implements Token{
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitIdentifierToken(name);
    }
}
