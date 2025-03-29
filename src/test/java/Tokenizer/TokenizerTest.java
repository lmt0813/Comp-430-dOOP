package Tokenizer;

import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

public class TokenizerTest {
    @Test
    public void testEmpty() {
        final Tokenizer tokenizer = new Tokenizer("");
        tokenizer.skipWhitespace();
        assertEquals(0, tokenizer.getPosition());
    }

    @Test
    public void testVeryWrong() {
        final Tokenizer tokenizer = new Tokenizer("@int");
        tokenizer.skipWhitespace();
        assertEquals(Optional.empty(),
        tokenizer.tryReadIdentifierOrReservedWordToken());
    }

    @Test
    public void testSingleWhitespace() {
        final Tokenizer tokenizer = new Tokenizer(" ");
        tokenizer.skipWhitespace();
        assertEquals(1, tokenizer.getPosition());
    }

    @Test
    public void testSingleWhitespaceAndA() {
        final Tokenizer tokenizer = new Tokenizer(" a");
        tokenizer.skipWhitespace();
        assertEquals(1, tokenizer.getPosition());
    }
    
    @Test
    public void testReadSingleDigitInteger() {
        final Tokenizer tokenizer = new Tokenizer("1");
        assertEquals(Optional.of(new IntegerToken(1)),
                     tokenizer.tryReadIntegerToken());
    }

    @Test
    public void testPrintToken() {
        final Tokenizer tokenizer = new Tokenizer("println");
        assertEquals(Optional.of(new PrintlnToken()),
                     tokenizer.tryReadIdentifierOrReservedWordToken());
    }

    @Test
    public void testMultiTokenize() throws TokenizerException {
        // foo bar
        final Tokenizer tokenizer = new Tokenizer("foo bar");
        final ArrayList<Token> tokens = tokenizer.tokenize();
        // assertEquals(new IdentifierToken("foo"),
        //              tokens.get(0));
        // assertEquals(new IdentifierToken("bar"),
        //              tokens.get(1));

        assertArrayEquals(new Token[]{
                new IdentifierToken("foo"),
                new IdentifierToken("bar")
            },
            tokens.toArray());
    }

    @Test
    public void testReservedWordsToken() throws TokenizerException {
        final Tokenizer tokenizer = new Tokenizer("break class boolean else if extends init method new return super while void String true false");
        final ArrayList<Token> tokens = tokenizer.tokenize();
        assertArrayEquals(new Token[]{
            new BreakToken(),
            new ClassToken(), 
            new BooleanToken(),
            new ElseToken(), 
            new IfToken(), 
            new ExtendsToken(),
            new InitToken(), 
            new MethodToken(),
            new NewToken(),
            new ReturnToken(),
            new SuperToken(), 
            new WhileToken(),
            new VoidToken(),
            new StringToken(),
            new BooleanLiteralToken(true),
            new BooleanLiteralToken(false),
        },
        tokens.toArray());
    }

    @Test
    public void testSymbol() throws TokenizerException {
        final Tokenizer tokenizer = new Tokenizer(", / * ( ) { } . - ;");
        final ArrayList<Token> tokens = tokenizer.tokenize();
        assertArrayEquals(new Token[]{
            new CommaToken(),
            new DivToken(), 
            new MultToken(),
            new LParenToken(), 
            new RParenToken(), 
            new LBraceToken(),
            new RBraceToken(), 
            new PeriodToken(),
            new MinusToken(),
            new SemiColonToken(),
        },
        tokens.toArray());
    }

    @Test
    public void testSymbols() throws TokenizerException {
        final Tokenizer tokenizer = new Tokenizer("== <= >= !=");
        final ArrayList<Token> tokens = tokenizer.tokenize();
        assertArrayEquals(new Token[]{
            new DoubleEqualToken(),
            new LessThanEqualToken(), 
            new GreaterThanEqualToken(),
            new NotEqualToken(),
        },
        tokens.toArray());
    }

    @Test
    public void testRandomInt() throws TokenizerException {
        final Tokenizer tokenizer = new Tokenizer("int x = 5 + 3;");
        final ArrayList<Token> tokens = tokenizer.tokenize();
        assertArrayEquals(new Token[]{
            new IntToken(),
            new IdentifierToken("x"), 
            new EqualsToken(),
            new IntegerToken(5), 
            new PlusToken(), 
            new IntegerToken(3),
            new SemiColonToken(), 
        },
        tokens.toArray());
    }

    

    
}