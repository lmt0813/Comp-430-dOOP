package Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Tokenizer.*;

public class Parser {
    public final Token[] tokens;
    public Parser(final Token[] tokens) {
        this.tokens = tokens;
    }

    public Token getToken(final int pos) throws ParseException {
        if (pos < 0 || pos >= tokens.length) {
            throw new ParseException("Ran out of tokens");
        } else {
            return tokens[pos];
        }
    } // readToken

    //comma_exp ::= [exp (`,` exp)*]
    public ParseResult<Exp> commaExp(final int startPos) throws ParseException{
        final ParseResult<Exp> m = exp(startPos);
        List<Exp> result = new ArrayList<>();
        result.add(m.result());
        boolean shouldRun = true;
        int pos = m.nextPos();
        while(shouldRun) {
            try {
                Token t = getToken(pos);
                if (t instanceof CommaToken) {
                    final ParseResult<Exp> m2 = commaExp(pos + 1);
                    result.add(m2.result());
                    pos = m2.nextPos();
                }
            } catch (ParseException e) {
                shouldRun = false;
            }
        }
        Exp finalExp = new commaExp(result);
        return new ParseResult<Exp>(finalExp, pos);
        }//commaExp

    /*primary_exp ::=
        var | str | i | Variables, strings, and integers are expressions
        `(` exp `)` | Parenthesized expressions
        `this` | Refers to my instance
        `true` | `false` | Booleans
        `println` `(` exp `)` | Prints something to the terminal
        `new` classname `(` comma_exp `)` Creates a new object
     */
    public ParseResult<Exp> primaryExp(final int startPos) throws ParseException {
        final Token t = getToken(startPos);
        if (t instanceof IdentifierToken id) {
            if(getToken(startPos + 1) instanceof LParenToken){
                final ParseResult<Exp> e = commaExp(startPos + 1);
                assertTokenIs(e.nextPos(), new RParenToken());
                return new ParseResult<Exp>(e.result(), e.nextPos() + 1);
            }else {
                return new ParseResult<Exp>(new IdExp(id.name()), startPos + 1);
            }
        } else if (t instanceof IntegerToken i) {
            return new ParseResult<Exp>(new IntExp(i.value()), startPos + 1);
        } else if (t instanceof LParenToken) {
            final ParseResult<Exp> e = exp(startPos + 1);
            assertTokenIs(e.nextPos(), new RParenToken());
            return new ParseResult<Exp>(e.result(), e.nextPos() + 1);
        } else if (t instanceof ThisToken) {
            return new ParseResult<Exp>(new ThisExp(), startPos + 1);
        } else if (t instanceof BooleanLiteralToken b) {
            if(b.value() == true){
                return new ParseResult<Exp>(new BooleanExp(true), startPos + 1);
            } else {
                return new ParseResult<Exp>(new BooleanExp(false), startPos + 1);
            }
        } else if (t instanceof PrintlnToken) {
            assertTokenIs(startPos + 1, new LParenToken());
            final ParseResult<Exp> print = exp(startPos + 2);
            assertTokenIs(print.nextPos(), new RParenToken());
            return new ParseResult<Exp>(new PrintExp(print.result()), print.nextPos() + 1);
        } else if (t instanceof NewToken) {
            if (!(getToken(startPos + 1) instanceof IdentifierToken id)) {
                throw new ParseException("Expected class name after `new`");
            }
            assertTokenIs(startPos + 2, new LParenToken());
            final ParseResult<Exp> m = commaExp(startPos + 3);
            assertTokenIs(m.nextPos(), new RParenToken());
            return new ParseResult<Exp>(new NewExp(id.name(), m.result()),m.nextPos() + 1);
        } else {
            throw new ParseException("Expected primary expression at position: " + startPos);
        }
    } // primaryExp
    
    //call_exp ::= primary_exp (`.` methodname `(` comma_exp `)`)*
    public ParseResult<Exp> callExp(final int startPos) throws ParseException{
        final ParseResult<Exp> m = primaryExp(startPos);
        Exp result = m.result();
        boolean shouldRun = true;
        int pos = m.nextPos();
        while (shouldRun) {
            try {
                if(getToken(pos + 1) instanceof PeriodToken){
                    final IdentifierToken method = (IdentifierToken)getToken(pos + 1);
                    if(getToken(pos + 2) instanceof LParenToken){
                        ParseResult<Exp> m2 = commaExp(pos + 3);
                        assertTokenIs(m2.nextPos(), new RParenToken());
                        result = new callExp(result, method.name(), m2.result());
                        pos = m2.nextPos() + 1;
                    }
                }
            } catch (ParseException e) {
                shouldRun = false;
            }
        }
        return new ParseResult<Exp>(result, pos);
    }//callExp

    // mult_exp ::= call_exp ((`*` | `/`) call_exp)*
    public ParseResult<Exp> multExp(final int startPos) throws ParseException {
        final ParseResult<Exp> m = callExp(startPos);
        Exp result = m.result();
        boolean shouldRun = true;
        int pos = m.nextPos();
        while (shouldRun) {
            try {
                final Token t = getToken(pos);
                final Op op;
                if (t instanceof MultToken) {
                    op = new MultOp();
                } else if (t instanceof DivToken) {
                    op = new DivOp();
                } else {
                    throw new ParseException("Expected * or /");
                }
                final ParseResult<Exp> m2 = callExp(pos + 1);
                result = new BinOpExp(result, op, m2.result());
                pos = m2.nextPos();
            } catch (ParseException e) {
                shouldRun = false;
            }
        }
        return new ParseResult<Exp>(result, pos);
    }        

    // add_exp ::= mult_exp ((`+` | `-`) mult_exp)*
    public ParseResult<Exp> addExp(final int startPos) throws ParseException {
        final ParseResult<Exp> m = multExp(startPos);
        Exp result = m.result();
        boolean shouldRun = true;
        int pos = m.nextPos();
        while (shouldRun) {
            try {
                final Token t = getToken(pos);
                final Op op;
                if (t instanceof PlusToken) {
                    op = new PlusOp();
                } else if (t instanceof MinusToken) {
                    op = new MinusOp();
                } else {
                    throw new ParseException("Expected + or -");
                }
                final ParseResult<Exp> m2 = multExp(pos + 1);
                result = new BinOpExp(result, op, m2.result());
                pos = m2.nextPos();
            } catch (ParseException e) {
                shouldRun = false;
            }
        }
        return new ParseResult<Exp>(result, pos);
    } // addExp
    
    //exp ::= add_exp
    public ParseResult<Exp> exp(final int startPos) throws ParseException {
        return addExp(startPos);
    }

    public void assertTokenIs(final int pos, final Token expected) throws ParseException {
        final Token received = getToken(pos);
        if (!expected.equals(received)) {
            throw new ParseException("Expected: " + expected.toString() +
                                     "; received: " + received.toString());
        }
    } // assertTokenIs
        
    /*stmt ::= exp `;` | Expression statements
     	vardec `;` | Variable declaration
     	var `=` exp `;` | Assignment
     	`while` `(` exp `)` stmt | while loops
     	`break` `;` | break
     	`return` [exp] `;` | return, possibly void
     	if with optional else
     	`if` `(` exp `)` stmt [`else` stmt] |
     	`{` stmt* `}` Block     
    */
    public ParseResult<Stmt> stmt(final int startPos) throws ParseException {
        final Token token = getToken(startPos);
        if (token instanceof IdentifierToken id) {
            String name = id.name();
            assertTokenIs(startPos + 1, new EqualsToken());
            ParseResult<Exp> expression = exp(startPos + 2);
            assertTokenIs(expression.nextPos(), new SemiColonToken());
            AssignStmt assign = new AssignStmt(name, expression.result());
            return new ParseResult<Stmt>(assign, expression.nextPos() + 1);
        } else if (token instanceof PrintlnToken) {
            ParseResult<Exp> expression = exp(startPos + 1);
            assertTokenIs(expression.nextPos(), new SemiColonToken());
            PrintlnStmt print = new PrintlnStmt(expression.result());
            return new ParseResult<Stmt>(print, expression.nextPos() + 1);
        } else if (token instanceof ReturnToken) {
            ParseResult<Optional<Exp>> opExpression;
            try {
                ParseResult<Exp> expression = exp(startPos + 1);
                opExpression = new ParseResult<Optional<Exp>>(Optional.of(expression.result()),
                                                              expression.nextPos());
            } catch (ParseException e) {
                opExpression = new ParseResult<Optional<Exp>>(Optional.empty(),
                                                              startPos + 1);
            }
            assertTokenIs(opExpression.nextPos(), new SemiColonToken());
            return new ParseResult<Stmt>(new ReturnStmt(opExpression.result()),
                                         opExpression.nextPos() + 1);
        } else if (token instanceof BreakToken &&
            getToken(startPos + 1) instanceof SemiColonToken) {
            return new ParseResult<>(new BreakStmt(), startPos + 2);
        } else if (token instanceof IfToken) {
            assertTokenIs(startPos + 1, new LParenToken());
            final ParseResult<Exp> cond = exp(startPos + 2);
            assertTokenIs(cond.nextPos(), new RParenToken());
            final ParseResult<Stmt> thenStmt = stmt(cond.nextPos() + 1);
        
            final Token maybeElse = getToken(thenStmt.nextPos());
            if (maybeElse instanceof ElseToken) {
                final ParseResult<Stmt> elseStmt = stmt(thenStmt.nextPos() + 1);
                return new ParseResult<>(new IfStmt(cond.result(), thenStmt.result(), Optional.of(elseStmt.result())), elseStmt.nextPos());
            } else {
                return new ParseResult<>(new IfStmt(cond.result(), thenStmt.result(), Optional.empty()), thenStmt.nextPos());
            }
        } else if (token instanceof WhileToken) {
            assertTokenIs(startPos + 1, new LParenToken());
            final ParseResult<Exp> cond = exp(startPos + 2);
            assertTokenIs(cond.nextPos(), new RParenToken());
            final ParseResult<Stmt> body = stmt(cond.nextPos() + 1);
            return new ParseResult<>(new WhileStmt(cond.result(), body.result()), body.nextPos());
        } else if (token instanceof LBraceToken) {
            List<Stmt> stmts = new ArrayList<>();
            int current = startPos + 1;
            while (!(getToken(current) instanceof RBraceToken)) {
                final ParseResult<Stmt> s = stmt(current);
                stmts.add(s.result());
                current = s.nextPos();
            }
            return new ParseResult<>(new BlockStmt(stmts), current + 1);
        } else if (token instanceof IntToken ||
        token instanceof BooleanToken ||
        token instanceof StringToken) {

            final String type;
            if (token instanceof IntToken) type = "int";
            else if (token instanceof BooleanToken) type = "Boolean";
            else if (token instanceof StringToken) type = "String";
            else throw new ParseException("Unexpected type token");

            final Token next = getToken(startPos + 1);
            if (!(next instanceof IdentifierToken id)) {
                throw new ParseException("Expected identifier after type");
            }
            assertTokenIs(startPos + 2, new SemiColonToken());
            return new ParseResult<>(new VarDeclStmt(type, id.name()), startPos + 3);
        } else {
            try {
                ParseResult<Exp> expression = exp(startPos);
                assertTokenIs(expression.nextPos(), new SemiColonToken());
                return new ParseResult<>(new ExpStmt(expression.result()), expression.nextPos() + 1);
            } catch (ParseException e) {
                throw new ParseException("Expected statement; got: " + token);
            }
        }
    } // stmt

    // program ::= stmt*
    public ParseResult<Program> program(final int startPos) {
        final List<Stmt> stmts = new ArrayList<Stmt>();
        int pos = startPos;
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final ParseResult<Stmt> stmtRes = stmt(pos);
                stmts.add(stmtRes.result());
                pos = stmtRes.nextPos();
            } catch (ParseException e) {
                shouldRun = false;
            }
        }
        return new ParseResult<Program>(new Program(stmts), pos);
    } // program

    public Program parseWholeProgram() throws ParseException {
        final ParseResult<Program> p = program(0);
        if (p.nextPos() == tokens.length) {
            return p.result();
        } else {
            throw new ParseException("Invalid token at position: " + p.nextPos());
        }
    } // parseWholeProgram
}