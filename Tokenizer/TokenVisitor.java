public interface TokenVisitor<A> {
    public A visitPrintToken();
    public A visitPrintlnToken();
    public A visitIdentifierToken(String name);
    public A visitSemicolonToken();
}
