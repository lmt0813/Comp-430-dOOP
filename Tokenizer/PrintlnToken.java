public record PrintlnToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitPrintlnToken();
    }        
}
