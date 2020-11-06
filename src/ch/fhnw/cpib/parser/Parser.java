package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.exceptions.GrammarError;
import ch.fhnw.cpib.tokens.Terminal;
import ch.fhnw.cpib.tokens.Token;

import java.util.Iterator;
import java.util.List;

public class Parser implements ParserInterface {

    private final Iterator<Token> iterator;
    private Token token;

    public Parser(List<Token> tokens) {
        iterator = tokens.iterator();
        token = iterator.next();
    }

    private Token consume(Terminal expectedTerminal) throws GrammarError {
        if (token.terminal == expectedTerminal) {
            Token consumedToken = token;

            if (token.terminal != Terminal.SENTINEL) {
                token = iterator.next();
            }
            return consumedToken;
        } else {
            throw new GrammarError("terminal expected: " + expectedTerminal + ", terminal found: " + token.terminal);
        }
    }

    @Override
    public void parse() throws GrammarError {
        // parsing the start symbol ...
        /*ConcSyn.IProgram program = */program();
        // ... and then consuming the SENTINEL
        consume(Terminal.SENTINEL);
        //return program;
    }

    public void program() throws GrammarError {
        if (token.terminal == Terminal.PROGRAM) {
            // PROGRAM IDENT <progParamList> <optGlobalCpsDecl> DO <cpsCmd> ENDPROGRAM
            consume(Terminal.PROGRAM);
            consume(Terminal.IDENT);
            progParamList();
            optGlobalCpsDecl();
            consume(Terminal.DO);
            cpsCmd();
            consume(Terminal.ENDPROGRAM);
        } else throw new GrammarError("program");
    }

    private void optGlobalCpsDecl() throws GrammarError {
        /*
          terminal GLOBAL
            GLOBAL <cpsDecl>
          terminal DO

         */
        if (token.terminal == Terminal.GLOBAL) {
            consume(Terminal.GLOBAL);
            cpsDecl();
        } else if (token.terminal == Terminal.DO) {
            // epsilon
        } else throw new GrammarError("optGlobalCpsDecl");
    }

    private void cpsDecl() throws GrammarError {
        /*
          terminal RECORD
            <decl> <repSemicolonDecl>
          terminal PROC
            <decl> <repSemicolonDecl>
          terminal FUN
            <decl> <repSemicolonDecl>
          terminal IDENT
            <decl> <repSemicolonDecl>
          terminal CHANGEMODE
            <decl> <repSemicolonDecl>
         */
        if (List.of(Terminal.RECORD,Terminal.PROC,Terminal.FUN,Terminal.IDENT,Terminal.CHANGEMODE).contains(token.terminal)) {
            decl();
            repSemicolonDecl();
        } else throw new GrammarError("cpsDecl");
    }

    private void repSemicolonDecl() throws GrammarError {
        /*
          terminal SEMICOLON
            SEMICOLON <decl> <repSemicolonDecl>
          terminal DO

         */
        if (token.terminal == Terminal.SEMICOLON) {
            consume(Terminal.SEMICOLON);
            decl();
            repSemicolonDecl();
        } else if (token.terminal == Terminal.DO) {
            // epsilon
        } else throw new GrammarError("repSemicolonDecl");
    }

    private void decl() throws GrammarError {
        /*
          terminal IDENT
            <stoDecl>
          terminal CHANGEMODE
            <stoDecl>
          terminal FUN
            <funDecl>
          terminal PROC
            <procDecl>
          terminal RECORD
            <recordShapeDecl>
         */
        if (token.terminal == Terminal.IDENT || token.terminal == Terminal.CHANGEMODE) {
            stoDecl();
        } else if (token.terminal == Terminal.FUN) {
            funDecl();
        } else if (token.terminal == Terminal.PROC) {
            procDecl();
        } else if (token.terminal == Terminal.RECORD) {
            recordShapeDecl();
        } else throw new GrammarError("decl");
    }

    private void recordShapeDecl() throws GrammarError {
        /*
          terminal RECORD
            RECORD IDENT LPAREN <typedIdent> <repCommaTypedIdent> RPAREN
         */
        if (token.terminal == Terminal.RECORD) {
            consume(Terminal.RECORD);
            consume(Terminal.IDENT);
            consume(Terminal.LPAREN);
            typedIdent();
            repCommaTypedIdent();
            consume(Terminal.RPAREN);
        } else throw new GrammarError("recordShapeDecl");
    }

    private void repCommaTypedIdent() throws GrammarError {
        /*
          terminal COMMA
            COMMA <typedIdent> <repCommaTypedIdent>
          terminal RPAREN

         */
        if (token.terminal == Terminal.COMMA) {
            consume(Terminal.COMMA);
            typedIdent();
            repCommaTypedIdent();
        } else if (token.terminal == Terminal.RPAREN) {
            // epsilon
        } else throw new GrammarError("recordShapeDecl");
    }

    private void typedIdent() throws GrammarError {
        /*
          terminal IDENT
            IDENT COLON <typeOrRecord>
         */
        if (token.terminal == Terminal.IDENT) {
            consume(Terminal.IDENT);
            consume(Terminal.COLON);
            typeOrRecord();
        } else throw new GrammarError("typedIdent");
    }

    private void typeOrRecord() throws GrammarError {
        /*
        <typeOrRecord>
          terminal TYPE
            TYPE
          terminal IDENT
            IDENT
         */
        if (token.terminal == Terminal.TYPE) {
            consume(Terminal.TYPE);
        } else if (token.terminal == Terminal.IDENT) {
            consume(Terminal.IDENT);
        } else throw new GrammarError("typeOrRecord");
    }

    private void procDecl() throws GrammarError {
        /*
          terminal PROC
            PROC IDENT <paramList> <optGlobalGlobImps> <optLocalCpsStoDecl> DO <cpsCmd> ENDPROC
         */
        if (token.terminal == Terminal.PROC) {
            consume(Terminal.PROC);
            consume(Terminal.IDENT);
            paramList();
            optGlobalGlobImps();
            optLocalCpsStoDecl();
            consume(Terminal.DO);
            cpsCmd();
            consume(Terminal.ENDPROC);
        } else throw new GrammarError("procDecl");
    }

    private void paramList() throws GrammarError {
        /*
          terminal LPAREN
            LPAREN <optParamRepCommaParam> RPAREN
         */
        if (token.terminal == Terminal.LPAREN) {
            consume(Terminal.LPAREN);
            optParamRepCommaParam();
            consume(Terminal.RPAREN);
        } else throw new GrammarError("paramList");
    }

    private void optParamRepCommaParam() throws GrammarError {
        /*
          terminal IDENT
            <param> <repCommaParam>
          terminal CHANGEMODE
            <param> <repCommaParam>
          terminal MECHMODE
            <param> <repCommaParam>
          terminal FLOWMODE
            <param> <repCommaParam>
          terminal RPAREN

         */
        if (List.of(Terminal.IDENT,Terminal.CHANGEMODE,Terminal.MECHMODE,Terminal.FLOWMODE).contains(token.terminal)) {
            param();
            repCommaParam();
        } else if(token.terminal == Terminal.RPAREN) {
            // epsilon
        } else throw new GrammarError("optParamRepCommaParam");
    }

    private void repCommaParam() throws GrammarError {
        /*
          terminal COMMA
            COMMA <param> <repCommaParam>
          terminal RPAREN

         */
        if(token.terminal == Terminal.COMMA) {
            consume(Terminal.COMMA);
            param();
            repCommaParam();
        } else if (token.terminal == Terminal.RPAREN) {
            // epsilon
        } else throw new GrammarError("repCommaParam");
    }

    private void param() throws GrammarError {
        /*
          terminal IDENT
            <optFlowmode> <optMechmode> <optChangemode> <typedIdent>
          terminal CHANGEMODE
            <optFlowmode> <optMechmode> <optChangemode> <typedIdent>
          terminal MECHMODE
            <optFlowmode> <optMechmode> <optChangemode> <typedIdent>
          terminal FLOWMODE
            <optFlowmode> <optMechmode> <optChangemode> <typedIdent>
         */
        if (List.of(Terminal.IDENT,Terminal.CHANGEMODE,Terminal.MECHMODE,Terminal.FLOWMODE).contains(token.terminal)) {
            optFlowmode();
            optMechmode();
            optChangemode();
            typedIdent();
        } else if(token.terminal == Terminal.RPAREN) {
            // epsilon
        } else throw new GrammarError("param");
    }

    private void optChangemode() throws GrammarError {
        /*
          terminal CHANGEMODE
            CHANGEMODE
          terminal IDENT

         */
        if (token.terminal == Terminal.CHANGEMODE) {
            consume(Terminal.CHANGEMODE);
        } else if(token.terminal == Terminal.IDENT) {
            // epsilon
        } else throw new GrammarError("optChangemode");
    }

    private void optMechmode() throws GrammarError {
        /*
          terminal MECHMODE
            MECHMODE
          terminal IDENT

          terminal CHANGEMODE

         */
        if (token.terminal == Terminal.MECHMODE) {
            consume(Terminal.MECHMODE);
        } else if(List.of(Terminal.IDENT,Terminal.CHANGEMODE).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("optMechmode");
    }

    private void optFlowmode() throws GrammarError {
        /*
          terminal FLOWMODE
            FLOWMODE
          terminal MECHMODE

          terminal IDENT

          terminal CHANGEMODE

         */
        if (token.terminal == Terminal.FLOWMODE) {
            consume(Terminal.FLOWMODE);
        } else if(List.of(Terminal.MECHMODE,Terminal.IDENT,Terminal.CHANGEMODE).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("optFlowmode");
    }

    private void funDecl() throws GrammarError {
        /*
          terminal FUN
            FUN IDENT <paramList> RETURNS <stoDecl> <optGlobalGlobImps> <optLocalCpsStoDecl> DO <cpsCmd> ENDFUN
         */
        if (token.terminal == Terminal.FUN) {
            consume(Terminal.FUN);
            consume(Terminal.IDENT);
            paramList();
            consume(Terminal.RETURNS);
            stoDecl();
            optGlobalGlobImps();
            optLocalCpsStoDecl();
            consume(Terminal.DO);
            cpsCmd();
            consume(Terminal.ENDFUN);
        } else throw new GrammarError("funDecl");
    }

    private void cpsCmd() throws GrammarError {
        /*
          terminal DEBUGOUT
            <cmd> <repSemicolonCmd>
          terminal DEBUGIN
            <cmd> <repSemicolonCmd>
          terminal CALL
            <cmd> <repSemicolonCmd>
          terminal WHILE
            <cmd> <repSemicolonCmd>
          terminal IF
            <cmd> <repSemicolonCmd>
          terminal LPAREN
            <cmd> <repSemicolonCmd>
          terminal ADDOPR
            <cmd> <repSemicolonCmd>
          terminal NOTOPR
            <cmd> <repSemicolonCmd>
          terminal IDENT
            <cmd> <repSemicolonCmd>
          terminal LITERAL
            <cmd> <repSemicolonCmd>
          terminal SKIP
            <cmd> <repSemicolonCmd>
         */
        if (
                List.of(Terminal.DEBUGOUT, Terminal.DEBUGIN, Terminal.CALL, Terminal.WHILE, Terminal.IF, Terminal.LPAREN,
                    Terminal.ADDOPR, Terminal.NOTOPR, Terminal.IDENT, Terminal.LITERAL, Terminal.SKIP)
                        .contains(token.terminal)
        ) {
            cmd();
            repSemicolonCmd();
        } else throw new GrammarError("cpsCmd");
    }

    private void repSemicolonCmd() throws GrammarError {
        /*
          terminal SEMICOLON
            SEMICOLON <cmd> <repSemicolonCmd>
          terminal ENDWHILE

          terminal ENDIF

          terminal ELSE

          terminal ENDPROC

          terminal ENDFUN

          terminal ENDPROGRAM

         */
        if (token.terminal == Terminal.SEMICOLON) {
            consume(Terminal.SEMICOLON);
            cmd();
            repSemicolonCmd();
        } else if(List.of(Terminal.ENDWHILE,Terminal.ENDIF,Terminal.ENDPROC,Terminal.ENDFUN,Terminal.ENDPROGRAM).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("repSemicolonCmd");
    }

    private void cmd() throws GrammarError {
        /*
          terminal SKIP
            SKIP
          terminal LPAREN
            <expr> BECOMES <expr>
          terminal ADDOPR
            <expr> BECOMES <expr>
          terminal NOTOPR
            <expr> BECOMES <expr>
          terminal IDENT
            <expr> BECOMES <expr>
          terminal LITERAL
            <expr> BECOMES <expr>
          terminal IF
            IF <expr> THEN <cpsCmd> <optElseCpsCmd> ENDIF
          terminal WHILE
            WHILE <expr> DO <cpsCmd> ENDWHILE
          terminal CALL
            CALL IDENT <exprList> <optGlobInits>
          terminal DEBUGIN
            DEBUGIN <expr>
          terminal DEBUGOUT
            DEBUGOUT <expr>
         */
        if (token.terminal == Terminal.SKIP) {
            consume(Terminal.SKIP);
        } else if (List.of(Terminal.LPAREN, Terminal.ADDOPR, Terminal.NOTOPR, Terminal.IDENT, Terminal.LITERAL).contains(token.terminal)) {
            expr();
            consume(Terminal.BECOMES);
            expr();
        } else if (token.terminal == Terminal.IF) {
            consume(Terminal.IF);
            expr();
            consume(Terminal.THEN);
            cpsCmd();
            optElseCpsCmd();
            consume(Terminal.ENDIF);
        } else if (token.terminal == Terminal.WHILE) {
            consume(Terminal.WHILE);
            expr();
            consume(Terminal.DO);
            cpsCmd();
            consume(Terminal.ENDWHILE);
        } else if (token.terminal == Terminal.CALL) {
            consume(Terminal.CALL);
            consume(Terminal.IDENT);
            exprList();
            optGlobInits();
        } else if (token.terminal == Terminal.DEBUGIN) {
            consume(Terminal.DEBUGIN);
            expr();
        } else if (token.terminal == Terminal.DEBUGOUT) {
            consume(Terminal.DEBUGOUT);
            expr();
        } else throw new GrammarError("cmd");
    }

    private void exprList() throws GrammarError {
        /*
          terminal LPAREN
            LPAREN <optExprRepCommaExpr> RPAREN
         */
        if (token.terminal == Terminal.LPAREN) {
            consume(Terminal.LPAREN);
            optExprRepCommaExpr();
            consume(Terminal.RPAREN);
        } else throw new GrammarError("exprList");
    }

    private void optExprRepCommaExpr() throws GrammarError {
        /*
          terminal LPAREN
            <expr> <repCommaExpr>
          terminal ADDOPR
            <expr> <repCommaExpr>
          terminal NOTOPR
            <expr> <repCommaExpr>
          terminal IDENT
            <expr> <repCommaExpr>
          terminal LITERAL
            <expr> <repCommaExpr>
          terminal RPAREN

         */
        if (List.of(Terminal.LPAREN,Terminal.ADDOPR,Terminal.NOTOPR,Terminal.IDENT,Terminal.LITERAL).contains(token.terminal)) {
            expr();
            repCommaExpr();
        } else if(token.terminal == Terminal.RPAREN) {
            // epsilon
        } else throw new GrammarError("optExprRepCommaExpr");
    }

    private void repCommaExpr() throws GrammarError {
        /*
          terminal COMMA
            COMMA <expr> <repCommaExpr>
          terminal RPAREN

         */
        if (token.terminal == Terminal.COMMA) {
            consume(Terminal.COMMA);
            expr();
            repCommaExpr();
        } else if(token.terminal == Terminal.RPAREN) {
            // epsilon
        } else throw new GrammarError("optExprRepCommaExpr");
    }

    private void optGlobInits() throws GrammarError {
        /*
          terminal INIT
            INIT IDENT <repCommaIdent>
          terminal ENDWHILE

          terminal ENDIF

          terminal ELSE

          terminal ENDPROC

          terminal ENDFUN

          terminal ENDPROGRAM

          terminal SEMICOLON

         */
        if (token.terminal == Terminal.INIT) {
            consume(Terminal.INIT);
            consume(Terminal.IDENT);
            repCommaIdent();
        } else if(List.of(Terminal.ENDWHILE,Terminal.ENDIF,Terminal.ELSE,Terminal.ENDPROC,Terminal.ENDFUN,Terminal.ENDPROGRAM,Terminal.SEMICOLON).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("optGlobInits");
    }

    private void repCommaIdent() throws GrammarError {
        /*
          terminal COMMA
            COMMA IDENT <repCommaIdent>
          terminal ENDWHILE

          terminal ENDIF

          terminal ELSE

          terminal ENDPROC

          terminal ENDFUN

          terminal ENDPROGRAM

          terminal SEMICOLON

         */
        if (token.terminal == Terminal.COMMA) {
            consume(Terminal.COMMA);
            consume(Terminal.IDENT);
            repCommaIdent();
        } else if(List.of(Terminal.ENDWHILE,Terminal.ENDIF,Terminal.ELSE,Terminal.ENDPROC,Terminal.ENDFUN,Terminal.ENDPROGRAM,Terminal.SEMICOLON).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("repCommaIdent");
    }

    private void optElseCpsCmd() throws GrammarError {
        /*
          terminal ELSE
            ELSE <cpsCmd>
          terminal ENDIF

         */
        if (token.terminal == Terminal.ELSE) {
            consume(Terminal.ELSE);
            cpsCmd();
        } else if (token.terminal == Terminal.ENDIF) {
            // epsilon
        } else throw new GrammarError("optElseCpsCmd");
    }

    private void optLocalCpsStoDecl() throws GrammarError {
        /*
          terminal LOCAL
            LOCAL <cpsStoDecl>
          terminal DO

         */
        if (token.terminal == Terminal.LOCAL) {
            consume(Terminal.LOCAL);
            cpsStoDecl();
        } else if (token.terminal == Terminal.DO) {
            // epsilon
        } else throw new GrammarError("optLocalCpsStoDecl");
    }

    private void cpsStoDecl() throws GrammarError {
        /*
          terminal IDENT
            <stoDecl> <repSemicolonStoDecl>
          terminal CHANGEMODE
            <stoDecl> <repSemicolonStoDecl>
         */
        if (token.terminal == Terminal.IDENT || token.terminal == Terminal.CHANGEMODE) {
            stoDecl();
            repSemicolonStoDecl();
        } else throw new GrammarError("cpsStoDecl");
    }

    private void repSemicolonStoDecl() throws GrammarError {
        /*
          terminal SEMICOLON
            SEMICOLON <stoDecl>
          terminal DO

         */
        if (token.terminal == Terminal.SEMICOLON) {
            consume(Terminal.SEMICOLON);
            stoDecl();
        } else if (token.terminal == Terminal.DO) {
            // epsilon
        } else throw new GrammarError("repSemicolonStoDecl");
    }

    private void optGlobalGlobImps() throws GrammarError {
        /*
          terminal GLOBAL
            GLOBAL <globImps>
          terminal DO

          terminal LOCAL

         */
        if (token.terminal == Terminal.GLOBAL) {
            consume(Terminal.GLOBAL);
            globImps();
        } else if (token.terminal == Terminal.DO || token.terminal == Terminal.LOCAL) {
            // epsilon
        } else throw new GrammarError("optGlobalGlobImps");
    }

    private void globImps() throws GrammarError {
        /*
          terminal IDENT
            <globImp> <repCommaGlobImp>
          terminal CHANGEMODE
            <globImp> <repCommaGlobImp>
          terminal FLOWMODE
            <globImp> <repCommaGlobImp>
         */
        if (List.of(Terminal.IDENT,Terminal.CHANGEMODE,Terminal.FLOWMODE).contains(token.terminal)) {
            globImp();
            repCommaGlobImp();
        } else throw new GrammarError("globImps");
    }

    private void repCommaGlobImp() throws GrammarError {
        /*
          terminal COMMA
            COMMA <globImps>
          terminal DO

          terminal LOCAL

         */
        if (token.terminal == Terminal.COMMA) {
            consume(Terminal.COMMA);
            globImps();
        } else if (token.terminal == Terminal.DO || token.terminal == Terminal.LOCAL) {
            // epsilon
        } else throw new GrammarError("repCommaGlobImp");
    }

    private void globImp() throws GrammarError {
        /*
          terminal IDENT
            <optFlowmode> <optChangemode> IDENT
          terminal CHANGEMODE
            <optFlowmode> <optChangemode> IDENT
          terminal FLOWMODE
            <optFlowmode> <optChangemode> IDENT
         */
        if (List.of(Terminal.IDENT,Terminal.CHANGEMODE,Terminal.FLOWMODE).contains(token.terminal)) {
            optFlowmode();
            optChangemode();
            consume(Terminal.IDENT);
        } else throw new GrammarError("globImp");
    }

    private void stoDecl() throws GrammarError {
        /*
          terminal IDENT
            <optChangemode> <typedIdent>
          terminal CHANGEMODE
            <optChangemode> <typedIdent>
         */
        if (token.terminal == Terminal.IDENT || token.terminal == Terminal.CHANGEMODE) {
            optChangemode();
            typedIdent();
        } else throw new GrammarError("stoDecl");
    }

    private void progParamList() throws GrammarError {
        if (token.terminal == Terminal.LPAREN) {
            // LPAREN <optProgParamRepCommaProgParam> RPAREN
            consume(Terminal.LPAREN);
            optProgParamRepCommaProgParam();
            consume(Terminal.RPAREN);
        } else throw new GrammarError("progParamList");
    }

    private void optProgParamRepCommaProgParam() throws GrammarError {
        /*
          terminal IDENT
            <progParam> <repCommaProgParam>
          terminal CHANGEMODE
            <progParam> <repCommaProgParam>
          terminal FLOWMODE
            <progParam> <repCommaProgParam>
          terminal RPAREN

         */
        if (token.terminal == Terminal.IDENT || token.terminal == Terminal.CHANGEMODE || token.terminal == Terminal.FLOWMODE) {
            progParam();
            repCommaProgParam();
        } else if (token.terminal == Terminal.RPAREN) {
            // epsilon
        } else throw new GrammarError("optProgParamRepCommaProgParam");
    }

    private void repCommaProgParam() throws GrammarError {
        /*
          terminal COMMA
            COMMA <progParam> <repCommaProgParam>
          terminal RPAREN

         */
        if (token.terminal == Terminal.COMMA) {
            consume(Terminal.COMMA);
            progParam();
            repCommaProgParam();
        } else if (token.terminal == Terminal.RPAREN) {
            // epsilon
        } else throw new GrammarError("repCommaProgParam");
    }

    private void progParam() throws GrammarError {
        /*
          terminal IDENT
            <optFlowmode> <optChangemode> <typedIdent>
          terminal CHANGEMODE
            <optFlowmode> <optChangemode> <typedIdent>
          terminal FLOWMODE
            <optFlowmode> <optChangemode> <typedIdent>
         */
        if (List.of(Terminal.IDENT,Terminal.CHANGEMODE,Terminal.FLOWMODE).contains(token.terminal)) {
            optFlowmode();
            optChangemode();
            typedIdent();
        } else throw new GrammarError("progParam");
    }

    private void expr() throws GrammarError {
        /*
          terminal LPAREN
            <term1> <repBoolOprTerm1>
          terminal ADDOPR
            <term1> <repBoolOprTerm1>
          terminal NOTOPR
            <term1> <repBoolOprTerm1>
          terminal IDENT
            <term1> <repBoolOprTerm1>
          terminal LITERAL
            <term1> <repBoolOprTerm1>
         */
        if (List.of(Terminal.LPAREN,Terminal.ADDOPR,Terminal.NOTOPR,Terminal.IDENT,Terminal.LITERAL).contains(token.terminal)) {
            term1();
            repBoolOprTerm1();
        } else throw new GrammarError("expr");
    }

    private void term1() throws GrammarError {
        /*
          terminal LPAREN
            <term2> <optRelOprTerm2>
          terminal ADDOPR
            <term2> <optRelOprTerm2>
          terminal NOTOPR
            <term2> <optRelOprTerm2>
          terminal IDENT
            <term2> <optRelOprTerm2>
          terminal LITERAL
            <term2> <optRelOprTerm2>
         */
        if (List.of(Terminal.LPAREN,Terminal.ADDOPR,Terminal.NOTOPR,Terminal.IDENT,Terminal.LITERAL).contains(token.terminal)) {
            term2();
            optRelOprTerm2();
        } else throw new GrammarError("term1");
    }

    private void optRelOprTerm2() throws GrammarError {
        /*
          terminal RELOPR
            RELOPR <term2>
          terminal COMMA

          terminal RPAREN

          terminal DO

          terminal THEN

          terminal ENDWHILE

          terminal ENDIF

          terminal ELSE

          terminal ENDPROC

          terminal ENDFUN

          terminal ENDPROGRAM

          terminal SEMICOLON

          terminal BECOMES

          terminal BOOLOPR

         */
        if (token.terminal == Terminal.RELOPR) {
            consume(Terminal.RELOPR);
            term2();
        } else if (List.of(
                Terminal.COMMA,
                Terminal.RPAREN,
                Terminal.DO,
                Terminal.THEN,
                Terminal.ENDWHILE,
                Terminal.ENDIF,
                Terminal.ELSE,
                Terminal.ENDPROC,
                Terminal.ENDFUN,
                Terminal.ENDPROGRAM,
                Terminal.SEMICOLON,
                Terminal.BECOMES,
                Terminal.BOOLOPR).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("optRelOprTerm2");
    }

    private void term2() throws GrammarError {
        /*
          terminal LPAREN
            <term3> <repAddOprTerm3>
          terminal ADDOPR
            <term3> <repAddOprTerm3>
          terminal NOTOPR
            <term3> <repAddOprTerm3>
          terminal IDENT
            <term3> <repAddOprTerm3>
          terminal LITERAL
            <term3> <repAddOprTerm3>
         */
        if (List.of(Terminal.LPAREN,Terminal.ADDOPR,Terminal.NOTOPR,Terminal.IDENT,Terminal.LITERAL).contains(token.terminal)) {
            term3();
            repAddOprTerm3();
        } else throw new GrammarError("term2");
    }

    private void repAddOprTerm3() throws GrammarError {
        /*
          terminal ADDOPR
            ADDOPR <term3> <repAddOprTerm3>
          terminal COMMA

          terminal RPAREN

          terminal DO

          terminal THEN

          terminal ENDWHILE

          terminal ENDIF

          terminal ELSE

          terminal ENDPROC

          terminal ENDFUN

          terminal ENDPROGRAM

          terminal SEMICOLON

          terminal BECOMES

          terminal BOOLOPR

          terminal RELOPR

         */
        if (token.terminal == Terminal.ADDOPR) {
            consume(Terminal.ADDOPR);
            term3();
            repAddOprTerm3();
        } else if (List.of(
                Terminal.COMMA,
                Terminal.RPAREN,
                Terminal.DO,
                Terminal.THEN,
                Terminal.ENDWHILE,
                Terminal.ENDIF,
                Terminal.ELSE,
                Terminal.ENDPROC,
                Terminal.ENDFUN,
                Terminal.ENDPROGRAM,
                Terminal.SEMICOLON,
                Terminal.BECOMES,
                Terminal.BOOLOPR,
                Terminal.RELOPR).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("repAddOprTerm3");
    }

    private void term3() throws GrammarError {
        /*
          terminal LPAREN
            <factor> <repMultOprFactor>
          terminal ADDOPR
            <factor> <repMultOprFactor>
          terminal NOTOPR
            <factor> <repMultOprFactor>
          terminal IDENT
            <factor> <repMultOprFactor>
          terminal LITERAL
            <factor> <repMultOprFactor>
         */
        if (List.of(Terminal.LPAREN,Terminal.ADDOPR,Terminal.NOTOPR,Terminal.IDENT,Terminal.LITERAL).contains(token.terminal)) {
            factor();
            repMultOprFactor();
        } else throw new GrammarError("term3");
    }

    private void repMultOprFactor() throws GrammarError {
        /*
          terminal MULTOPR
            MULTOPR <factor> <repMultOprFactor>
          terminal COMMA

          terminal RPAREN

          terminal DO

          terminal THEN

          terminal ENDWHILE

          terminal ENDIF

          terminal ELSE

          terminal ENDPROC

          terminal ENDFUN

          terminal ENDPROGRAM

          terminal SEMICOLON

          terminal BECOMES

          terminal BOOLOPR

          terminal RELOPR

          terminal ADDOPR

         */
        if (token.terminal == Terminal.MULTOPR) {
            consume(Terminal.MULTOPR);
            factor();
            repMultOprFactor();
        } else if (List.of(Terminal.COMMA,
                Terminal.RPAREN,
                Terminal.DO,
                Terminal.THEN,
                Terminal.ENDWHILE,
                Terminal.ENDIF,
                Terminal.ELSE,
                Terminal.ENDPROC,
                Terminal.ENDFUN,
                Terminal.ENDPROGRAM,
                Terminal.SEMICOLON,
                Terminal.BECOMES,
                Terminal.BOOLOPR,
                Terminal.RELOPR,
                Terminal.ADDOPR).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("repMultOprFactor");
    }

    private void factor() throws GrammarError {
        /*
          terminal LITERAL
            LITERAL
          terminal IDENT
            IDENT <optInitOrExprListOrRecordAccess>
          terminal ADDOPR
            <monadicOpr> <factor>
          terminal NOTOPR
            <monadicOpr> <factor>
          terminal LPAREN
            LPAREN <expr> RPAREN
         */
        if (token.terminal == Terminal.LITERAL) {
            consume(Terminal.LITERAL);
        } else if (token.terminal == Terminal.IDENT) {
            consume(Terminal.IDENT);
            optInitOrExprListOrRecordAccess();
        } else if (token.terminal == Terminal.ADDOPR || token.terminal == Terminal.NOTOPR) {
            monadicOpr();
            factor();
        } else if (token.terminal == Terminal.LPAREN) {
            consume(Terminal.LPAREN);
            expr();
            consume(Terminal.RPAREN);
        } else throw new GrammarError("factor");
    }

    private void optInitOrExprListOrRecordAccess() throws GrammarError {
        /*
          terminal INIT
            INIT
          terminal LPAREN
            <exprList>
          terminal ACCESSOPR
            <recordAccess>
          terminal COMMA

          terminal RPAREN

          terminal DO

          terminal THEN

          terminal ENDWHILE

          terminal ENDIF

          terminal ELSE

          terminal ENDPROC

          terminal ENDFUN

          terminal ENDPROGRAM

          terminal SEMICOLON

          terminal BECOMES

          terminal BOOLOPR

          terminal MULTOPR

          terminal RELOPR

          terminal ADDOPR

         */
        if (token.terminal == Terminal.INIT) {
            consume(Terminal.INIT);
        } else if (token.terminal == Terminal.LPAREN) {
            exprList();
        } else if (token.terminal == Terminal.ACCESSOPR) {
            recordAccess();
        } else if (List.of(Terminal.COMMA,
                Terminal.RPAREN,
                Terminal.DO,
                Terminal.THEN,
                Terminal.ENDWHILE,
                Terminal.ENDIF,
                Terminal.ELSE,
                Terminal.ENDPROC,
                Terminal.ENDFUN,
                Terminal.ENDPROGRAM,
                Terminal.SEMICOLON,
                Terminal.BECOMES,
                Terminal.BOOLOPR,
                Terminal.MULTOPR,
                Terminal.RELOPR,
                Terminal.ADDOPR).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("optInitOrExprListOrRecordAccess");
    }

    private void recordAccess() throws GrammarError {
        /*
          terminal ACCESSOPR
            ACCESSOPR IDENT <optRecordAccess>
         */
        if (token.terminal == Terminal.ACCESSOPR) {
            consume(Terminal.ACCESSOPR);
            consume(Terminal.IDENT);
            optRecordAccess();
        } else throw new GrammarError("recordAccess");
    }

    private void optRecordAccess() throws GrammarError {
        /*
          terminal ACCESSOPR
            ACCESSOPR IDENT <optRecordAccess>
          terminal COMMA

          terminal RPAREN

          terminal DO

          terminal THEN

          terminal ENDWHILE

          terminal ENDIF

          terminal ELSE

          terminal ENDPROC

          terminal ENDFUN

          terminal ENDPROGRAM

          terminal SEMICOLON

          terminal BECOMES

          terminal BOOLOPR

          terminal RELOPR

          terminal ADDOPR

          terminal MULTOPR

         */
        if (token.terminal == Terminal.ACCESSOPR) {
            consume(Terminal.ACCESSOPR);
            consume(Terminal.IDENT);
            optRecordAccess();
        } else if (List.of(Terminal.COMMA,
                Terminal.RPAREN,
                Terminal.DO,
                Terminal.THEN,
                Terminal.ENDWHILE,
                Terminal.ENDIF,
                Terminal.ELSE,
                Terminal.ENDPROC,
                Terminal.ENDFUN,
                Terminal.ENDPROGRAM,
                Terminal.SEMICOLON,
                Terminal.BECOMES,
                Terminal.BOOLOPR,
                Terminal.RELOPR,
                Terminal.ADDOPR,
                Terminal.MULTOPR).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("optRecordAccess");
    }

    private void monadicOpr() throws GrammarError {
        /*
          terminal NOTOPR
            NOTOPR
          terminal ADDOPR
            ADDOPR
         */
        if (token.terminal == Terminal.NOTOPR) {
            consume(Terminal.NOTOPR);
        } else if (token.terminal == Terminal.ADDOPR) {
            consume(Terminal.ADDOPR);
        } else throw new GrammarError("monadicOpr");
    }

    private void repBoolOprTerm1() throws GrammarError {
        /*
          terminal BOOLOPR
            BOOLOPR <term1> <repBoolOprTerm1>
          terminal COMMA

          terminal RPAREN

          terminal DO

          terminal THEN

          terminal ENDWHILE

          terminal ENDIF

          terminal ELSE

          terminal ENDPROC

          terminal ENDFUN

          terminal ENDPROGRAM

          terminal SEMICOLON

          terminal BECOMES

         */
        if (token.terminal == Terminal.BOOLOPR) {
            consume(Terminal.BOOLOPR);
            term1();
            repBoolOprTerm1();
        } else if (List.of(Terminal.COMMA,
                Terminal.RPAREN,
                Terminal.DO,
                Terminal.THEN,
                Terminal.ENDWHILE,
                Terminal.ENDIF,
                Terminal.ELSE,
                Terminal.ENDPROC,
                Terminal.ENDFUN,
                Terminal.ENDPROGRAM,
                Terminal.SEMICOLON,
                Terminal.BECOMES).contains(token.terminal)) {
            // epsilon
        } else throw new GrammarError("repBoolOprTerm1");
    }
}
