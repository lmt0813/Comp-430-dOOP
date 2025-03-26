public record InitToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitInitToken();
    }        
}
