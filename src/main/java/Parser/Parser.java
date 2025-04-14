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

    public Token readToken(final int pos) throws ParseException {
        if (pos < 0 || pos >= tokens.length) {
            throw new ParseException("Ran out of tokens");
        } else {
            return tokens[pos];
        }
    } // readToken

    // public abstract class OperatorParser {
    //     public abstract ParseResult<Exp> oneLevelUp(final int startPos) throws ParseException;
    //     public abstract Optional<Op> theOperator(final Token token);

    //     public ParseResult<Exp> parse(final int startPos) throws ParseException {
    //         final ParseResult<Exp> m = oneLevelUp(startPos);
    //         Exp result = m.result;
    //         boolean shouldRun = true;
    //         int pos = m.nextPos;
    //         while (shouldRun) {
    //             try {
    //                 final Token t = getToken(pos);
    //                 final Optional<Op> op = theOperator(t);
    //                 if (op.isEmpty()) {
    //                     throw new ParseException("Wrong operator");
    //                 }
    //                 final ParseResult<Exp> m2 = oneLevelUp(pos + 1);
    //                 result = new BinOpExp(result, op, m2.result);
    //                 pos = m2.nextPos;
    //             } catch (ParseException e) {
    //                 shouldRun = false;
    //             }
    //         }
    //         return new ParseResult<Exp>(result, pos);
    //     } // parse
    // } // OperatorParser

    // public class MultExpParser extends OperatorParser {
    //     public ParseResult<Exp> oneLevelUp(final int startPos) throws ParseException {
    //         return primaryExp(startPos);
    //     }
    //     public Optional<Op> theOperator(final Token token) {
    //         if (token instanceof PlusToken) {
    //             return Optional.of(new PlusOp());
    //         } else if (token instanceof MinusToken) {
    //             return Optional.of(new MinusOp());
    //         } else {
    //             return Optional.empty();
    //         }
    //     }
    // } // MultExpParser

    // public ParseResult<Exp> multExp(final int startPos) throws ParseException {
    //     return new MultExpParser().parse(startPos);
    // }

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
            return new ParseResult<Exp>(new IdExp(id.name), startPos + 1);
        } else if (t instanceof IntegerToken i) {
            return new ParseResult<Exp>(new IntExp(i.value), startPos + 1);
        } else if (t instanceof LParenToken) {
            final ParseResult<Exp> e = exp(startPos + 1);
            assertTokenIs(e.nextPos(), new RParenToken());
            return new ParseResult<Exp>(e.result(), e.nextPos() + 1);
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
                final Token t = getToken(pos);
            } catch (ParseException e) {
                shouldRun = false;
            }
        }
        return new ParseResult<Exp>(result, pos);
    }//callExp

    // mult_exp ::= call_exp ((`*` | `/`) call_exp)*
    public ParseResult<Exp> multExp(final int startPos) throws ParseException {
        final ParseResult<Exp> m = primaryExp(startPos);
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
                final ParseResult<Exp> m2 = primaryExp(pos + 1);
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
                result = new BinOpExp(result, op, m2.result);
                pos = m2.nextPos();
            } catch (ParseException e) {
                shouldRun = false;
            }
        }
        return new ParseResult<Exp>(result, pos);
    } // addExp
    
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
        final Token token = readToken(startPos);
        if (token instanceof IdentifierToken id) {
            String name = id.name;
            assertTokenIs(startPos + 1, new EqualsToken());
            ParseResult<Exp> expression = exp(startPos + 2);
            assertTokenIs(expression.nextPos(), new SemiColonToken());
            AssignStmt assign = new AssignStmt(name, expression.result());
            return new ParseResult<Stmt>(assign, expression.nextPos() + 1);
        } else if (token instanceof PrintToken) {
            ParseResult<Exp> expression = exp(startPos + 1);
            assertTokenIs(expression.nextPos(), new SemiColonToken());
            PrintStmt print = new PrintStmt(expression.result());
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
        } else {
            throw new ParseException("Expected statement; got: " + token);
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