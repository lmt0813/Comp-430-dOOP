public record IfToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitIfToken();
    }        
}
