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
}