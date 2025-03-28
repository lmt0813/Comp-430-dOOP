public interface TokenVisitor<A> {
    public A visitPrintToken();
    public A visitPrintlnToken();
    public A visitIdentifierToken(String name);
    public A visitSemicolonToken();
    public A visitWhileToken();
    public A visitReturnToken();
    public A visitIfToken();
    public A visitInitToken();
    public A visitVarToken();
    public A visitNewToken();
    public A visitExtendsToken();
    public A visitElseToken();
    public A visitClassToken();
    public A visitMethodToken();
}
