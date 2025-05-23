package Tokenizer;
public record ClassToken() implements Token {
    public <A> A visit(TokenVisitor<A> v) {
        return v.visitClassToken();
    }        
}
