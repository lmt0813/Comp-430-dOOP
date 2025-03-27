import java.util.Optional;
import java.util.ArrayList;

public class Tokenizer {
    public final String input;
    private int position;

    public Tokenizer(final String input) {
        this.input = input;
        position = 0;
    }

    public int getPosition() {
        return position;
    }
    
    public void skipWhitespace() {
        while (position < input.length() &&
               Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }
    
    public Optional<Token> tryReadIntegerToken() {
        String digits = "";
        while (position < input.length() &&
               Character.isDigit(input.charAt(position))) {
            digits += input.charAt(position);
            position++;
        }

        if (digits.length() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(new IntegerToken(Integer.parseInt(digits)));
        }
    } // tryReadIntegerToken

    public Optional<Token> tryReadIdentifierOrReservedWordToken() {
        if (position < input.length() &&
            Character.isLetter(input.charAt(position))) {
            String chars = "" + input.charAt(position);
            position++;
            while (position < input.length() &&
                   Character.isLetterOrDigit(input.charAt(position))) {
                chars += input.charAt(position);
                position++;
            }
            if (chars.equals("println")) {
                return Optional.of(new PrintlnToken());
            } else if (chars.equals("class")) {
                return Optional.of(new ClassToken());
            } else if (chars.equals("Void")) {
                return Optional.of(new VoidToken());
            } else if (chars.equals("break")) {
                return Optional.of(new BreakToken());
            } else if (chars.equals("super")) {
                return Optional.of(new SuperToken());
            } else if (chars.equals("extends")) {
                return Optional.of(new ExtendsToken());
            } else if (chars.equals("method")) {
                return Optional.of(new MethodToken());
            } else if (chars.equals("return")) {
                return Optional.of(new ReturnToken());
            } else if (chars.equals("init")) {
                return Optional.of(new InitToken());
            } else if (chars.equals("boolean")) {
                return Optional.of(new BooleanToken());
            } else{
                return Optional.of(new IdentifierToken(chars));
            }
        } else {
            return Optional.empty();
        }
    } // tryReadIdentifierOrReservedWordToken

    public Optional<Token> tryReadSymbol(){
        if (input.startsWith("(", position)) {
            position++;
            return Optional.of(new LParenToken());
        } else if (input.startsWith(")", position)) {
            position++;
            return Optional.of(new RParenToken());
        } else {
            return Optional.empty();
        }
    }
}
