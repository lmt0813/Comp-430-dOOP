public record ReturnToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitReturnToken();
    }        
}
