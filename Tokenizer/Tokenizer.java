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
        switch (chars) {
            case "println": return Optional.of(new PrintlnToken());
            case "class": return Optional.of(new ClassToken());
            case "Void": return Optional.of(new VoidToken());
            case "break": return Optional.of(new BreakToken());
            case "super": return Optional.of(new SuperToken());
            case "extends": return Optional.of(new ExtendsToken());
            case "method": return Optional.of(new MethodToken());
            case "return": return Optional.of(new ReturnToken());
            case "init": return Optional.of(new InitToken());
            case "String": return Optional.of(new StringToken());
            case "boolean": return Optional.of(new BooleanToken());
            case "true": return Optional.of(new BooleanLiteralToken(true));
            case "false": return Optional.of(new BooleanLiteralToken(false));
            default: return Optional.of(new IdentifierToken(chars)); // If not reserved, it's an identifier
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
        } else if (input.startsWith("{", position)) {
            position++;
            return Optional.of(new LBraceToken());
        } else if (input.startsWith("}", position)) {
            position++;
            return Optional.of(new RBraceToken());
        } else if (input.startsWith(";", position)) {
            position++;
            return Optional.of(new SemiColonToken());
        } else if (input.startsWith(".", position)) {
            position++;
            return Optional.of(new PeriodToken());
        } else if (input.startsWith("+", position)) {
            position++;
            return Optional.of(new PlusToken());
        } else if (input.startsWith("-", position)) {
            position++;
            return Optional.of(new MinusToken());
        } else if (input.startsWith("*", position)) {
            position++;
            return Optional.of(new MultToken());
        } else if (input.startsWith("/", position)) {
            position++;
            return Optional.of(new DivToken());
        } else if (input.startsWith("=", position)) {
            position++;
            return Optional.of(new EqualsToken());
        } else if (input.startsWith("==", position)) {
            position += 2;
            return Optional.of(new DoubleEqualToken());
        } else if (input.startsWith("!=", position)) {
            position += 2;
            return Optional.of(new NotEqualToken());
        } else if (input.startsWith(">=", position)) {
            position += 2;
            return Optional.of(new GreaterThanEqualToken());
        } else if (input.startsWith("<=", position)) {
            position += 2;
            return Optional.of(new LessThanEqualToken());
        } else {
            return Optional.empty();
        }
    }
}
