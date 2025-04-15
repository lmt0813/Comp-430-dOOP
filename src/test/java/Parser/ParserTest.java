package Parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import Tokenizer.*;

public class ParserTest {

    private Parser createParserFromString(String input) throws TokenizerException {
        List<Token> tokens = new Tokenizer(input).tokenize();
        return new Parser(tokens.toArray(new Token[0]));
    }

    @Test
    void testThisExpression() throws ParseException, TokenizerException {
        Parser p = createParserFromString("this");
        ParseResult<Exp> result = p.primaryExp(0);
        assertTrue(result.result() instanceof ThisExp);
    }

    @Test
    void testNewExpression() throws ParseException, TokenizerException {
        Parser p = createParserFromString("some = new some();");
        ParseResult<Exp> result = p.primaryExp(0);
        assertTrue(result.result() instanceof NewExp);
    }

    @Test
    public void testSingleExpression() throws ParseException, TokenizerException {
        Parser parser = createParserFromString("int x ,");
        Program program = parser.parseWholeProgram();
        assertEquals(0, program.classDefs().size());
        assertEquals(1, program.stmts().size());
    }

    @Test
    public void testVariableDeclaration() throws Exception {
        Parser parser = createParserFromString("int x; boolean flag; String name;");
        Program program = parser.parseWholeProgram();
        assertEquals(3, program.stmts().size());
    }

    @Test
    void testPrimaryExpAdd() throws ParseException, TokenizerException {
        Parser p = createParserFromString("42 + 10");
        ParseResult<Exp> result = p.primaryExp(0);
        assertTrue(result.result() instanceof IntExp);
        assertEquals(1, result.nextPos());
    }

    @Test
    void testPrimaryExpSubtract() throws ParseException, TokenizerException {
        Parser p = createParserFromString("42 - 10");
        ParseResult<Exp> result = p.primaryExp(0);
        assertTrue(result.result() instanceof IntExp);
        assertEquals(1, result.nextPos());
    }
    
    @Test
    void testAssignmentStatement() throws ParseException, TokenizerException {
        String code = "x = 5;";
        Parser p = createParserFromString(code);
        ParseResult<Stmt> result = p.stmt(0);
        assertTrue(result.result() instanceof AssignStmt);
        assertEquals(4, result.nextPos());
    }



    @Test
    void testBreakStatement() throws ParseException, TokenizerException {
        Parser p = createParserFromString("break;");
        ParseResult<Stmt> result = p.stmt(0);
        assertTrue(result.result() instanceof BreakStmt);
    }

    @Test
    void testBreakMissingSemicolon() throws TokenizerException {
        Parser p = createParserFromString("break");
        assertThrows(ParseException.class, () -> p.stmt(0));
    }
   
    @Test
    void testBlockStatement() throws ParseException, TokenizerException {
        Parser p = createParserFromString("{return;}");
        ParseResult<Stmt> result = p.stmt(0);
        assertTrue(result.result() instanceof BlockStmt);
    }

    @Test
    void testExpressionStatement() throws ParseException, TokenizerException {
        Parser p = createParserFromString("42;");
        ParseResult<Stmt> result = p.stmt(0);
        assertTrue(result.result() instanceof ExpStmt);
    }

    @Test
    void testInvalidStatement() throws TokenizerException {
        Parser p = createParserFromString("+");
        assertThrows(ParseException.class, () -> p.stmt(0));
    }

    @Test
    void testVariableDeclarationWithInvalidType() throws TokenizerException {
        Parser p = createParserFromString("+x;");
        assertThrows(ParseException.class, () -> p.stmt(0));
    }

    @Test
    void testMissingSemicolonAfterVarDecl() throws TokenizerException {
        Parser p = createParserFromString("int x");
        assertThrows(ParseException.class, () -> p.stmt(0));
    }

    @Test
    void testNestedBlockStatements() throws ParseException, TokenizerException {
        Parser p = createParserFromString("{{return;}}");
        ParseResult<Stmt> result = p.stmt(0);
        BlockStmt outer = (BlockStmt) result.result();
        BlockStmt inner = (BlockStmt) outer.stmts().get(0);
        assertEquals(1, inner.stmts().size());
    }


    @Test
    void testIntVariableDeclaration() throws ParseException, TokenizerException {
        Parser p = createParserFromString("int count;");
        ParseResult<Vardecl> result = p.vardec(0);
        
        Vardecl vardecl = result.result();
        assertEquals("int", vardecl.type());
        assertEquals("count", vardecl.varName());
        assertEquals(2, result.nextPos());
    }

    @Test
    void testBooleanVariableDeclaration() throws ParseException, TokenizerException {
        Parser p = createParserFromString("boolean isValid;");
        ParseResult<Vardecl> result = p.vardec(0);
        
        assertEquals("boolean", result.result().type());
        assertEquals("isValid", result.result().varName());
    }

    @Test
    void testMissingIdentifier() throws TokenizerException {
        Parser p = createParserFromString("int +");
        
        ParseException e = assertThrows(ParseException.class, 
            () -> p.vardec(0));
        assertTrue(e.getMessage().contains("Expected identifier after type"));
    }


    @Test
    void testEndOfInput() throws TokenizerException {
        Parser p = createParserFromString("int");
        
        assertThrows(ParseException.class, 
            () -> p.vardec(0));
    }

    @Test
    void testSingleVardecl() throws ParseException, TokenizerException {
        Parser p = createParserFromString("int x");
        ParseResult<List<Vardecl>> result = p.commaVardec(0);
        
        assertEquals(1, result.result().size());
        assertEquals("int", result.result().get(0).type());
        assertEquals("x", result.result().get(0).varName());
        assertEquals(2, result.nextPos());
    }

    @Test
    void testMultipleVardecls() throws ParseException, TokenizerException {
        
        Parser p = createParserFromString("int x , boolean y , String z;");
        ParseResult<List<Vardecl>> result = p.commaVardec(0);
        
        List<Vardecl> vardecls = result.result();
        assertEquals(3, vardecls.size());
        
        assertEquals("int", vardecls.get(0).type());
        assertEquals("x", vardecls.get(0).varName());
        
        assertEquals("boolean", vardecls.get(1).type());
        assertEquals("y", vardecls.get(1).varName());
        
        assertEquals("String", vardecls.get(2).type());
        assertEquals("z", vardecls.get(2).varName());
        
        assertEquals(8, result.nextPos());
    }

    @Test
    void testTrailingComma() throws TokenizerException {
        Parser p = createParserFromString("int x ,");
        
        assertThrows(ParseException.class, () -> p.commaVardec(0));
    }

    @Test
    void testEmptyInput() throws TokenizerException {
        Parser p = createParserFromString("");

        
        assertThrows(ParseException.class, () -> p.commaVardec(0));
    }


    @Test
    void testWithSurroundingTokens() throws ParseException, TokenizerException {
        Parser p = createParserFromString("(int x , boolean y);");
        ParseResult<List<Vardecl>> result = p.commaVardec(1);
        
        assertEquals(2, result.result().size());
        assertEquals(6, result.nextPos()); 
    }

    @Test
    void testInvalidVardeclInMiddle() throws TokenizerException {
        Parser p = createParserFromString("int x , + y");
        
        assertThrows(ParseException.class, () -> p.commaVardec(0));
    }

    
    @Test
    void testOnlyComma() throws TokenizerException {
        Parser p = createParserFromString(" ,");
        
        assertThrows(ParseException.class, () -> p.commaVardec(0));
    }


    @Test
    public void testParsePrimaryInt() throws TokenizerException, ParseException {
        Tokenizer lexer = new Tokenizer("42");
        Token[] tokens = lexer.tokenize().toArray(new Token[0]);
        Parser parser = new Parser(tokens);
        ParseResult<Exp> result = parser.primaryExp(0);
        assertEquals(new IntExp(42), result.result());
    }

    @Test
    void testSimpleIdentifier() throws ParseException, TokenizerException {
        Parser p = createParserFromString("x;");
        ParseResult<Exp> result = p.primaryExp(0);
        
        assertTrue(result.result() instanceof IdExp);
        assertEquals("x", ((IdExp)result.result()).name());
        assertEquals(1, result.nextPos());
    }

    /*@Test
    public void testIfStmtWithElse() throws ParseException, TokenizerException {

    Parser parser = createParserFromString("while(true) break;");

    ParseResult<Stmt> result = parser.stmt(0);

    assertTrue(result.result() instanceof IfStmt);
    IfStmt ifStmt = (IfStmt) result.result();
    assertTrue(ifStmt.condition() instanceof BooleanExp);
    assertTrue(ifStmt.thenBranch() instanceof BreakStmt);
    assertTrue(ifStmt.elseBranch().isPresent());
    assertTrue(ifStmt.elseBranch().get() instanceof BreakStmt);
    }
    */


    /*@Test
    void testSimpleAddition() throws ParseException, TokenizerException {
        Parser p = createParserFromString("2 + 3");
        ParseResult<Exp> result = p.addExp(0);
        
        assertTrue(result.result() instanceof BinOpExp);
        BinOpExp binOp = (BinOpExp) result.result();
        assertTrue(binOp.op() instanceof PlusOp);
        assertEquals(3, result.nextPos()); // 2 + 3
    } */






}