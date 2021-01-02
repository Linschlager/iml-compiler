package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.exceptions.GrammarError;
import ch.fhnw.cpib.lexer.ITokenList;
import ch.fhnw.cpib.lexer.tokens.*;

import java.util.Iterator;
import java.util.List;

public class Parser implements IParser {

    private final Iterator<Token> iterator;
    private Token token;

    public Parser(ITokenList tokens) {
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
    public ConcSyn.IProgram parse() throws GrammarError {
        // parsing the start symbol ...
        ConcSyn.IProgram program = program();
        // ... and then consuming the SENTINEL
        consume(Terminal.SENTINEL);
        return program;
    }

    public ConcSyn.IProgram program() throws GrammarError {
        if (token.terminal == Terminal.PROGRAM) {
            // PROGRAM IDENT <progParamList> <optGlobalCpsDecl> DO <cpsCmd> ENDPROGRAM
            ConcSyn.Program program = new ConcSyn.Program();
            consume(Terminal.PROGRAM);
            program.identifier = (Identifier) consume(Terminal.IDENT);
            program.progParamList = progParamList();
            program.optGlobalCpsDecl = optGlobalCpsDecl();
            consume(Terminal.DO);
            program.cpsCmd = cpsCmd();
            consume(Terminal.ENDPROGRAM);
            return program;
        } else throw new GrammarError("program");
    }

    private ConcSyn.IOptGlobalCpsDecl optGlobalCpsDecl() throws GrammarError {
        /*
          terminal GLOBAL
            GLOBAL <cpsDecl>
          terminal DO

         */
        if (token.terminal == Terminal.GLOBAL) {
            ConcSyn.OptGlobalCpsDecl optGlobalCpsDecl = new ConcSyn.OptGlobalCpsDecl();
            consume(Terminal.GLOBAL);
            optGlobalCpsDecl.cpsDecl = cpsDecl();
            return optGlobalCpsDecl;
        } else if (token.terminal == Terminal.DO) {
            // epsilon
            return new ConcSyn.OptGlobalCpsDeclEpsilon();
        } else throw new GrammarError("optGlobalCpsDecl");
    }

    private ConcSyn.ICpsDecl cpsDecl() throws GrammarError {
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
        if (List.of(Terminal.RECORD, Terminal.PROC, Terminal.FUN, Terminal.IDENT, Terminal.CHANGEMODE).contains(token.terminal)) {
            ConcSyn.CpsDecl cpsDecl = new ConcSyn.CpsDecl();
            cpsDecl.decl = decl();
            cpsDecl.cpsDecl = repSemicolonDecl();
            return cpsDecl;
        } else throw new GrammarError("cpsDecl");
    }

    private ConcSyn.ICpsDecl repSemicolonDecl() throws GrammarError {
        /*
          terminal SEMICOLON
            SEMICOLON <decl> <repSemicolonDecl>
          terminal DO

         */
        if (token.terminal == Terminal.SEMICOLON) {
            consume(Terminal.SEMICOLON);
            return cpsDecl();
        } else if (token.terminal == Terminal.DO) {
            // epsilon
            return new ConcSyn.CpsDeclEpsilon();
        } else throw new GrammarError("repSemicolonDecl");
    }

    private ConcSyn.IDecl decl() throws GrammarError {
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
            return stoDecl();
        } else if (token.terminal == Terminal.FUN) {
            return funDecl();
        } else if (token.terminal == Terminal.PROC) {
            return procDecl();
        } else if (token.terminal == Terminal.RECORD) {
            return recordShapeDecl();
        } else throw new GrammarError("decl");
    }

    private ConcSyn.ICpsTypedIdent cpsTypedIdent() throws GrammarError {
        if (token.terminal == Terminal.IDENT) {
            ConcSyn.CpsTypedIdent cpsTypedIdent = new ConcSyn.CpsTypedIdent();
            cpsTypedIdent.typedIdent = typedIdent();
            cpsTypedIdent.cpsTypedIdent = repCommaTypedIdent();
            return cpsTypedIdent;
        } else throw new GrammarError("cpsTypedIdent");
    }

    private ConcSyn.IRecordShapeDecl recordShapeDecl() throws GrammarError {
        /*
          terminal RECORD
            RECORD IDENT LPAREN <typedIdent> <repCommaTypedIdent> RPAREN
         */
        if (token.terminal == Terminal.RECORD) {
            ConcSyn.RecordShapeDecl recordShapeDecl = new ConcSyn.RecordShapeDecl();
            consume(Terminal.RECORD);
            recordShapeDecl.name = (Identifier) consume(Terminal.IDENT);
            consume(Terminal.LPAREN);
            recordShapeDecl.cpsTypedIdent = cpsTypedIdent();
            consume(Terminal.RPAREN);
            return recordShapeDecl;
        } else throw new GrammarError("recordShapeDecl");
    }

    private ConcSyn.ICpsTypedIdent repCommaTypedIdent() throws GrammarError {
        /*
          terminal COMMA
            COMMA <typedIdent> <repCommaTypedIdent>
          terminal RPAREN

         */
        if (token.terminal == Terminal.COMMA) {
            ConcSyn.CpsTypedIdent cpsTypedIdent = new ConcSyn.CpsTypedIdent();
            consume(Terminal.COMMA);
            cpsTypedIdent.typedIdent = typedIdent();
            cpsTypedIdent.cpsTypedIdent = repCommaTypedIdent();
            return cpsTypedIdent;
        } else if (token.terminal == Terminal.RPAREN) {
            return new ConcSyn.CpsTypedIdentEpsilon();
        } else throw new GrammarError("recordShapeDecl");
    }

    private ConcSyn.ITypedIdent typedIdent() throws GrammarError {
        /*
          terminal IDENT
            IDENT COLON <typeOrRecord>
         */
        if (token.terminal == Terminal.IDENT) {
            ConcSyn.TypedIdent typedIdent = new ConcSyn.TypedIdent();
            typedIdent.identifier = (Identifier) consume(Terminal.IDENT);
            consume(Terminal.COLON);
            typedIdent.type = typeOrRecord();
            return typedIdent;
        } else throw new GrammarError("typedIdent");
    }

    private ConcSyn.IType typeOrRecord() throws GrammarError {
        /*
        <typeOrRecord>
          terminal TYPE
            TYPE
          terminal IDENT
            IDENT
         */
        if (token.terminal == Terminal.TYPE) {
            ConcSyn.TraditionalType type = new ConcSyn.TraditionalType();
            type.type = (Type) consume(Terminal.TYPE);
            return type;
        } else if (token.terminal == Terminal.IDENT) {
            ConcSyn.RecordType type = new ConcSyn.RecordType();
            type.name = (Identifier) consume(Terminal.IDENT);
            return type;
        } else throw new GrammarError("typeOrRecord");
    }

    private ConcSyn.IProcDecl procDecl() throws GrammarError {
        /*
          terminal PROC
            PROC IDENT <paramList> <optGlobalGlobImps> <optLocalCpsStoDecl> DO <cpsCmd> ENDPROC
         */
        if (token.terminal == Terminal.PROC) {
            ConcSyn.ProcDecl procDecl = new ConcSyn.ProcDecl();
            consume(Terminal.PROC);
            procDecl.name = (Identifier) consume(Terminal.IDENT);
            procDecl.paramList = paramList();
            procDecl.optGlobalGlobImps = optGlobalGlobImps();
            procDecl.optLocalCpsStoDecl = optLocalCpsStoDecl();
            consume(Terminal.DO);
            procDecl.cpsCmd = cpsCmd();
            consume(Terminal.ENDPROC);
            return procDecl;
        } else throw new GrammarError("procDecl");
    }

    private ConcSyn.IParamList paramList() throws GrammarError {
        /*
          terminal LPAREN
            LPAREN <optParamRepCommaParam> RPAREN
         */
        if (token.terminal == Terminal.LPAREN) {
            ConcSyn.ParamList paramList = new ConcSyn.ParamList();
            consume(Terminal.LPAREN);
            paramList.optParamRepCommaParam = optParamRepCommaParam();
            consume(Terminal.RPAREN);
            return paramList;
        } else throw new GrammarError("paramList");
    }

    private ConcSyn.IOptParamRepCommaParam optParamRepCommaParam() throws GrammarError {
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
        if (List.of(Terminal.IDENT, Terminal.CHANGEMODE, Terminal.MECHMODE, Terminal.FLOWMODE).contains(token.terminal)) {
            ConcSyn.OptParamRepCommaParam optParamRepCommaParam = new ConcSyn.OptParamRepCommaParam();
            optParamRepCommaParam.param = param();
            optParamRepCommaParam.optParamRepCommaParam = repCommaParam();
            return optParamRepCommaParam;
        } else if (token.terminal == Terminal.RPAREN) {
            // epsilon
            return new ConcSyn.OptParamRepCommaParamEpsilon();
        } else throw new GrammarError("optParamRepCommaParam");
    }

    private ConcSyn.IOptParamRepCommaParam repCommaParam() throws GrammarError {
        /*
          terminal COMMA
            COMMA <param> <repCommaParam>
          terminal RPAREN

         */
        if (token.terminal == Terminal.COMMA) {
            ConcSyn.OptParamRepCommaParam optParamRepCommaParam = new ConcSyn.OptParamRepCommaParam();
            consume(Terminal.COMMA);
            optParamRepCommaParam.param = param();
            optParamRepCommaParam.optParamRepCommaParam = repCommaParam();
            return optParamRepCommaParam;
        } else if (token.terminal == Terminal.RPAREN) {
            // epsilon
            return new ConcSyn.OptParamRepCommaParamEpsilon();
        } else throw new GrammarError("repCommaParam");
    }

    private ConcSyn.IParam param() throws GrammarError {
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
        if (List.of(Terminal.IDENT, Terminal.CHANGEMODE, Terminal.MECHMODE, Terminal.FLOWMODE).contains(token.terminal)) {
            ConcSyn.Param param = new ConcSyn.Param();
            param.optFlowmode = optFlowmode();
            param.optMechmode = optMechmode();
            param.optChangemode = optChangemode();
            param.typedIdent = typedIdent();
            return param;
        } else if (token.terminal == Terminal.RPAREN) {
            return new ConcSyn.ParamEpsilon();
        } else throw new GrammarError("param");
    }

    private ConcSyn.IOptChangemode optChangemode() throws GrammarError {
        /*
          terminal CHANGEMODE
            CHANGEMODE
          terminal IDENT

         */
        if (token.terminal == Terminal.CHANGEMODE) {
            ConcSyn.OptChangemode changemode = new ConcSyn.OptChangemode();
            changemode.changemode = (Changemode) consume(Terminal.CHANGEMODE);
            return changemode;
        } else if (token.terminal == Terminal.IDENT) {
            return new ConcSyn.OptChangemodeEpsilon();
        } else throw new GrammarError("optChangemode");
    }

    private ConcSyn.IOptMechmode optMechmode() throws GrammarError {
        /*
          terminal MECHMODE
            MECHMODE
          terminal IDENT

          terminal CHANGEMODE

         */
        if (token.terminal == Terminal.MECHMODE) {
            ConcSyn.OptMechmode mechmode = new ConcSyn.OptMechmode();
            mechmode.mechmode = (Mechmode) consume(Terminal.MECHMODE);
            return mechmode;
        } else if (List.of(Terminal.IDENT, Terminal.CHANGEMODE).contains(token.terminal)) {
            return new ConcSyn.OptMechmodeEpsilon();
        } else throw new GrammarError("optMechmode");
    }

    private ConcSyn.IOptFlowmode optFlowmode() throws GrammarError {
        /*
          terminal FLOWMODE
            FLOWMODE
          terminal MECHMODE

          terminal IDENT

          terminal CHANGEMODE

         */
        if (token.terminal == Terminal.FLOWMODE) {
            ConcSyn.OptFlowmode flowmode = new ConcSyn.OptFlowmode();
            flowmode.flowmode = (Flowmode) consume(Terminal.FLOWMODE);
            return flowmode;
        } else if (List.of(Terminal.MECHMODE, Terminal.IDENT, Terminal.CHANGEMODE).contains(token.terminal)) {
            // epsilon
            return new ConcSyn.OptFlowmodeEpsilon();
        } else throw new GrammarError("optFlowmode");
    }

    private ConcSyn.FunDecl funDecl() throws GrammarError {
        /*
          terminal FUN
            FUN IDENT <paramList> RETURNS <stoDecl> <optGlobalGlobImps> <optLocalCpsStoDecl> DO <cpsCmd> ENDFUN
         */
        if (token.terminal == Terminal.FUN) {
            ConcSyn.FunDecl funDecl = new ConcSyn.FunDecl();
            consume(Terminal.FUN);
            funDecl.name = (Identifier) consume(Terminal.IDENT);
            funDecl.paramList = paramList();
            consume(Terminal.RETURNS);
            funDecl.stoDecl = stoDecl();
            funDecl.optGlobalGlobImps = optGlobalGlobImps();
            funDecl.optLocalCpsStoDecl = optLocalCpsStoDecl();
            consume(Terminal.DO);
            funDecl.cpsCmd = cpsCmd();
            consume(Terminal.ENDFUN);
            return funDecl;

        } else throw new GrammarError("funDecl");
    }

    private ConcSyn.ICpsCmd cpsCmd() throws GrammarError {
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
            ConcSyn.CpsCmd cpsCmd = new ConcSyn.CpsCmd();
            cpsCmd.cmd = cmd();
            cpsCmd.cpsCmd = repSemicolonCmd();
            return cpsCmd;
        } else throw new GrammarError("cpsCmd");
    }

    private ConcSyn.ICpsCmd repSemicolonCmd() throws GrammarError {
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
            return cpsCmd();
        } else if (List.of(Terminal.ENDWHILE, Terminal.ENDIF, Terminal.ELSE,
                Terminal.ENDPROC,
                Terminal.ENDFUN,
                Terminal.ENDPROGRAM).contains(token.terminal)) {
            return new ConcSyn.CpsCmdEpsilon();
        } else throw new GrammarError("repSemicolonCmd");
    }

    private ConcSyn.ICmd cmd() throws GrammarError {
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
            return new ConcSyn.SkipCmd();
        } else if (List.of(Terminal.LPAREN, Terminal.ADDOPR, Terminal.NOTOPR, Terminal.IDENT, Terminal.LITERAL).contains(token.terminal)) {
            ConcSyn.AssignmentCmd assignmentCmd = new ConcSyn.AssignmentCmd();
            assignmentCmd.expr1 = expr();
            consume(Terminal.BECOMES);
            assignmentCmd.expr2 = expr();
            return assignmentCmd;
        } else if (token.terminal == Terminal.IF) {
            ConcSyn.IfCmd ifCmd = new ConcSyn.IfCmd();
            consume(Terminal.IF);
            ifCmd.conditionExpr = expr();
            consume(Terminal.THEN);
            ifCmd.cpsCmd = cpsCmd();
            ifCmd.optElseCpsCmd = optElseCpsCmd();
            consume(Terminal.ENDIF);
            return ifCmd;
        } else if (token.terminal == Terminal.WHILE) {
            ConcSyn.WhileCmd whileCmd = new ConcSyn.WhileCmd();
            consume(Terminal.WHILE);
            whileCmd.conditionExpr = expr();
            consume(Terminal.DO);
            whileCmd.cpsCmd = cpsCmd();
            consume(Terminal.ENDWHILE);
            return whileCmd;
        } else if (token.terminal == Terminal.CALL) {
            ConcSyn.CallCmd callCmd = new ConcSyn.CallCmd();
            consume(Terminal.CALL);
            callCmd.identifier = (Identifier) consume(Terminal.IDENT);
            callCmd.exprList = exprList();
            callCmd.optGlobInits = optGlobInits();
            return callCmd;
        } else if (token.terminal == Terminal.DEBUGIN) {
            ConcSyn.DebugInCmd debugInCmd = new ConcSyn.DebugInCmd();
            consume(Terminal.DEBUGIN);
            debugInCmd.expr = expr();
            return debugInCmd;
        } else if (token.terminal == Terminal.DEBUGOUT) {
            ConcSyn.DebugOutCmd debugOutCmd = new ConcSyn.DebugOutCmd();
            consume(Terminal.DEBUGOUT);
            debugOutCmd.expr = expr();
            return debugOutCmd;
        } else throw new GrammarError("cmd");
    }

    private ConcSyn.IExprListFactor exprListFactor() throws GrammarError {
        /*
          terminal LPAREN
            LPAREN <optExprRepCommaExpr> RPAREN
         */
        if (token.terminal == Terminal.LPAREN) {
            ConcSyn.ExprListFactor factor = new ConcSyn.ExprListFactor();
            factor.exprList = exprList();
            return factor;
        } else throw new GrammarError("exprList");
    }

    private ConcSyn.IExprList exprList() throws GrammarError {
        /*
          terminal LPAREN
            LPAREN <optExprRepCommaExpr> RPAREN
         */
        if (token.terminal == Terminal.LPAREN) {
            ConcSyn.ExprList exprList = new ConcSyn.ExprList();
            consume(Terminal.LPAREN);
            exprList.optExprRepCommaExpr = optExprRepCommaExpr();
            consume(Terminal.RPAREN);
            return exprList;
        } else throw new GrammarError("exprList");
    }

    private ConcSyn.IOptExprRepCommaExpr optExprRepCommaExpr() throws GrammarError {
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
        if (List.of(Terminal.LPAREN, Terminal.ADDOPR, Terminal.NOTOPR, Terminal.IDENT, Terminal.LITERAL).contains(token.terminal)) {
            ConcSyn.OptExprRepCommaExpr optExprRepCommaExpr = new ConcSyn.OptExprRepCommaExpr();
            optExprRepCommaExpr.expression = expr();
            optExprRepCommaExpr.optExprRepCommaExpr = repCommaExpr();
            return optExprRepCommaExpr;
        } else if (token.terminal == Terminal.RPAREN) {
            return new ConcSyn.OptExprRepCommaExprEpsilon();
        } else throw new GrammarError("optExprRepCommaExpr");
    }

    private ConcSyn.IOptExprRepCommaExpr repCommaExpr() throws GrammarError {
        /*
          terminal COMMA
            COMMA <expr> <repCommaExpr>
          terminal RPAREN

         */
        if (token.terminal == Terminal.COMMA) {
            consume(Terminal.COMMA);
            return optExprRepCommaExpr();
        } else if (token.terminal == Terminal.RPAREN) {
            return new ConcSyn.OptExprRepCommaExprEpsilon();
        } else throw new GrammarError("optExprRepCommaExpr");
    }

    private ConcSyn.IOptGlobInits optGlobInits() throws GrammarError {
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
            ConcSyn.OptGlobInits optGlobInits = new ConcSyn.OptGlobInits();
            consume(Terminal.INIT);
            optGlobInits.identifier = (Identifier) consume(Terminal.IDENT);
            optGlobInits.repCommaIdents = repCommaIdents();
            return optGlobInits;
        } else if (List.of(Terminal.ENDWHILE, Terminal.ENDIF, Terminal.ELSE, Terminal.ENDPROC, Terminal.ENDFUN, Terminal.ENDPROGRAM, Terminal.SEMICOLON).contains(token.terminal)) {
            return new ConcSyn.OptGlobInitsEpsilon();
        } else throw new GrammarError("optGlobInits");
    }

    private ConcSyn.IRepCommaIdents repCommaIdents() throws GrammarError {
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
            ConcSyn.RepCommaIdents repCommaIdents = new ConcSyn.RepCommaIdents();
            consume(Terminal.COMMA);
            repCommaIdents.identifier = (Identifier) consume(Terminal.IDENT);
            repCommaIdents.repCommaIdents = repCommaIdents();
            return repCommaIdents;
        } else if(List.of(Terminal.ENDWHILE,Terminal.ENDIF,Terminal.ELSE,Terminal.ENDPROC,Terminal.ENDFUN,Terminal.ENDPROGRAM,Terminal.SEMICOLON).contains(token.terminal)) {
            // epsilon
            return new ConcSyn.RepCommaIdentsEpsilon();
        } else throw new GrammarError("repCommaIdent");
    }

    private ConcSyn.IOptElseCpsCmd optElseCpsCmd() throws GrammarError {
        /*
          terminal ELSE
            ELSE <cpsCmd>
          terminal ENDIF

         */
        if (token.terminal == Terminal.ELSE) {
            ConcSyn.OptElseCpsCmd optElseCpsCmd = new ConcSyn.OptElseCpsCmd();
            consume(Terminal.ELSE);
            optElseCpsCmd.cpsCmd = cpsCmd();
            return optElseCpsCmd;
        } else if (token.terminal == Terminal.ENDIF) {
            // epsilon
            return new ConcSyn.OptElseCpsCmdEpsilon();
        } else throw new GrammarError("optElseCpsCmd");
    }

    private ConcSyn.IOptLocalCpsStoDecl optLocalCpsStoDecl() throws GrammarError {
        /*
          terminal LOCAL
            LOCAL <cpsStoDecl>
          terminal DO

         */
        if (token.terminal == Terminal.LOCAL) {
            ConcSyn.OptLocalCpsStoDecl optLocalCpsStoDecl = new ConcSyn.OptLocalCpsStoDecl();
            consume(Terminal.LOCAL);
            optLocalCpsStoDecl.cpsStoDecl = cpsStoDecl();
            return optLocalCpsStoDecl;
        } else if (token.terminal == Terminal.DO) {
            // epsilon
            return new ConcSyn.OptLocalCpsStoDeclEpsilon();
        } else throw new GrammarError("optLocalCpsStoDecl");
    }

    private ConcSyn.ICpsStoDecl cpsStoDecl() throws GrammarError {
        /*
          terminal IDENT
            <stoDecl> <repSemicolonStoDecl>
          terminal CHANGEMODE
            <stoDecl> <repSemicolonStoDecl>
         */
        if (token.terminal == Terminal.IDENT || token.terminal == Terminal.CHANGEMODE) {
            ConcSyn.CpsStoDecl cpsStoDecl = new ConcSyn.CpsStoDecl();
            cpsStoDecl.stoDecl = stoDecl();
            cpsStoDecl.cpsStoDecl = repSemicolonStoDecls();
            return cpsStoDecl;
        } else throw new GrammarError("cpsStoDecl");
    }

    private ConcSyn.ICpsStoDecl repSemicolonStoDecls() throws GrammarError {
        /*
          terminal SEMICOLON
            SEMICOLON <stoDecl>
          terminal DO

         */
        if (token.terminal == Terminal.SEMICOLON) {
            consume(Terminal.SEMICOLON);
            return cpsStoDecl();
        } else if (token.terminal == Terminal.DO) {
            // epsilon
            return new ConcSyn.CpsStoDeclEpsilon();
        } else throw new GrammarError("repSemicolonStoDecl");
    }

    private ConcSyn.IOptGlobalGlobImps optGlobalGlobImps() throws GrammarError {
        /*
          terminal GLOBAL
            GLOBAL <globImps>
          terminal DO

          terminal LOCAL

         */
        if (token.terminal == Terminal.GLOBAL) {
            ConcSyn.OptGlobalGlobImps globalGlobImps = new ConcSyn.OptGlobalGlobImps();
            consume(Terminal.GLOBAL);
            globalGlobImps.globImps = globImps();
            return globalGlobImps;
        } else if (token.terminal == Terminal.DO || token.terminal == Terminal.LOCAL) {
            // epsilon
            return new ConcSyn.OptGlobalGlobImpsEpsilon();
        } else throw new GrammarError("optGlobalGlobImps");
    }

    private ConcSyn.IGlobImps globImps() throws GrammarError {
        /*
          terminal IDENT
            <globImp> <repCommaGlobImp>
          terminal CHANGEMODE
            <globImp> <repCommaGlobImp>
          terminal FLOWMODE
            <globImp> <repCommaGlobImp>
         */
        if (List.of(Terminal.IDENT, Terminal.CHANGEMODE, Terminal.FLOWMODE).contains(token.terminal)) {
            ConcSyn.GlobImps globImps = new ConcSyn.GlobImps();
            globImps.globImp = globImp();
            globImps.globImps = repCommaGlobImps();
            return globImps;
        } else throw new GrammarError("globImps");
    }

    private ConcSyn.IGlobImps repCommaGlobImps() throws GrammarError {
        /*
          terminal COMMA
            COMMA <globImps>
          terminal DO

          terminal LOCAL

         */
        if (token.terminal == Terminal.COMMA) {
            consume(Terminal.COMMA);
            return globImps();
        } else if (token.terminal == Terminal.DO || token.terminal == Terminal.LOCAL) {
            // epsilon
            return new ConcSyn.GlobImpsEpsilon();
        } else throw new GrammarError("repCommaGlobImp");
    }

    private ConcSyn.IGlobImp globImp() throws GrammarError {
        /*
          terminal IDENT
            <optFlowmode> <optChangemode> IDENT
          terminal CHANGEMODE
            <optFlowmode> <optChangemode> IDENT
          terminal FLOWMODE
            <optFlowmode> <optChangemode> IDENT
         */
        if (List.of(Terminal.IDENT, Terminal.CHANGEMODE, Terminal.FLOWMODE).contains(token.terminal)) {
            ConcSyn.GlobImp globImp = new ConcSyn.GlobImp();
            globImp.optFlowmode = optFlowmode();
            globImp.optChangemode = optChangemode();
            globImp.identifier = (Identifier) consume(Terminal.IDENT);
            return globImp;
        } else throw new GrammarError("globImp");
    }

    private ConcSyn.StoDecl stoDecl() throws GrammarError {
        /*
          terminal IDENT
            <optChangemode> <typedIdent>
          terminal CHANGEMODE
            <optChangemode> <typedIdent>
         */
        if (token.terminal == Terminal.IDENT || token.terminal == Terminal.CHANGEMODE) {
            ConcSyn.StoDecl stoDecl = new ConcSyn.StoDecl();
            stoDecl.optChangemode = optChangemode();
            stoDecl.typedIdent = typedIdent();
            return stoDecl;
        } else throw new GrammarError("stoDecl");
    }

    private ConcSyn.IProgParamList progParamList() throws GrammarError {
        if (token.terminal == Terminal.LPAREN) {
            ConcSyn.ProgParamList progParamList = new ConcSyn.ProgParamList();
            // LPAREN <optProgParamRepCommaProgParam> RPAREN
            consume(Terminal.LPAREN);
            progParamList.optProgParamRepCommaProgParam = optProgParamRepCommaProgParam();
            consume(Terminal.RPAREN);
            return progParamList;
        } else throw new GrammarError("progParamList");
    }

    private ConcSyn.IOptProgParamRepCommaProgParam optProgParamRepCommaProgParam() throws GrammarError {
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
            ConcSyn.OptProgParamRepCommaProgParam optProgParamRepCommaProgParam = new ConcSyn.OptProgParamRepCommaProgParam();
            optProgParamRepCommaProgParam.progParam = progParam();
            optProgParamRepCommaProgParam.optProgParamRepCommaProgParam = repCommaProgParam();
            return optProgParamRepCommaProgParam;
        } else if (token.terminal == Terminal.RPAREN) {
            return new ConcSyn.OptProgParamRepCommaProgParamEpsilon();
        } else throw new GrammarError("optProgParamRepCommaProgParam");
    }

    private ConcSyn.IOptProgParamRepCommaProgParam repCommaProgParam() throws GrammarError {
        /*
          terminal COMMA
            COMMA <progParam> <repCommaProgParam>
          terminal RPAREN

         */
        if (token.terminal == Terminal.COMMA) {
            ConcSyn.OptProgParamRepCommaProgParam optProgParamRepCommaProgParam = new ConcSyn.OptProgParamRepCommaProgParam();
            consume(Terminal.COMMA);
            optProgParamRepCommaProgParam.progParam = progParam();
            optProgParamRepCommaProgParam.optProgParamRepCommaProgParam = repCommaProgParam();
            return optProgParamRepCommaProgParam;
        } else if (token.terminal == Terminal.RPAREN) {
            return new ConcSyn.OptProgParamRepCommaProgParamEpsilon();
        } else throw new GrammarError("repCommaProgParam");
    }

    private ConcSyn.IProgParam progParam() throws GrammarError {
        /*
          terminal IDENT
            <optFlowmode> <optChangemode> <typedIdent>
          terminal CHANGEMODE
            <optFlowmode> <optChangemode> <typedIdent>
          terminal FLOWMODE
            <optFlowmode> <optChangemode> <typedIdent>
         */
        if (List.of(Terminal.IDENT, Terminal.CHANGEMODE, Terminal.FLOWMODE).contains(token.terminal)) {
            ConcSyn.ProgParam progParam = new ConcSyn.ProgParam();
            progParam.optFlowmode = optFlowmode();
            progParam.optChangemode = optChangemode();
            progParam.typedIdent = typedIdent();
            return progParam;
        } else throw new GrammarError("progParam");
    }

    private ConcSyn.IExpr expr() throws GrammarError {
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
        if (List.of(Terminal.LPAREN, Terminal.ADDOPR, Terminal.NOTOPR, Terminal.IDENT, Terminal.LITERAL).contains(token.terminal)) {
            ConcSyn.Expr expression = new ConcSyn.Expr();
            expression.term1 = term1();
            expression.repBoolOprTerm1 = repBoolOprTerm1();
            return expression;
        } else throw new GrammarError("expr");
    }

    private ConcSyn.ITerm1 term1() throws GrammarError {
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
        if (List.of(Terminal.LPAREN, Terminal.ADDOPR, Terminal.NOTOPR, Terminal.IDENT, Terminal.LITERAL).contains(token.terminal)) {
            ConcSyn.Term1 term1 = new ConcSyn.Term1();
            term1.term2 = term2();
            term1.optRelOprTerm2 = optRelOprTerm2();
            return term1;
        } else throw new GrammarError("term1");
    }

    private ConcSyn.IOptRelOprTerm2 optRelOprTerm2() throws GrammarError {
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
            ConcSyn.OptRelOprTerm2 optRelOprTerm2 = new ConcSyn.OptRelOprTerm2();
            optRelOprTerm2.relOpr = (RelOpr) consume(Terminal.RELOPR);
            optRelOprTerm2.term2 = term2();
            return optRelOprTerm2;
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
            return new ConcSyn.OptRelOprTerm2Epsilon();
        } else throw new GrammarError("optRelOprTerm2");
    }

    private ConcSyn.ITerm2 term2() throws GrammarError {
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
        if (List.of(Terminal.LPAREN, Terminal.ADDOPR, Terminal.NOTOPR, Terminal.IDENT, Terminal.LITERAL).contains(token.terminal)) {
            ConcSyn.Term2 term2 = new ConcSyn.Term2();
            term2.term3 = term3();
            term2.repAddOprTerm3 = repAddOprTerm3();
            return term2;
        } else throw new GrammarError("term2");
    }

    private ConcSyn.IRepAddOprTerm3 repAddOprTerm3() throws GrammarError {
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
            ConcSyn.RepAddOprTerm3 repAddOprTerm3 = new ConcSyn.RepAddOprTerm3();
            repAddOprTerm3.addOpr = (AddOpr) consume(Terminal.ADDOPR);
            repAddOprTerm3.term3 = term3();
            repAddOprTerm3.repAddOprTerm3 = repAddOprTerm3();
            return repAddOprTerm3;
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
            return new ConcSyn.RepAddOprTerm3Epsilon();
        } else throw new GrammarError("repAddOprTerm3");
    }

    private ConcSyn.ITerm3 term3() throws GrammarError {
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
        if (List.of(Terminal.LPAREN, Terminal.ADDOPR, Terminal.NOTOPR, Terminal.IDENT, Terminal.LITERAL).contains(token.terminal)) {
            ConcSyn.Term3 term3 = new ConcSyn.Term3();
            term3.factor = factor();
            term3.repMultOprFactor = repMultOprFactor();
            return term3;
        } else throw new GrammarError("term3");
    }

    private ConcSyn.IRepMultOprFactor repMultOprFactor() throws GrammarError {
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
            ConcSyn.RepMultOprFactor repMultOprFactor = new ConcSyn.RepMultOprFactor();
            repMultOprFactor.multOpr = (MultOpr) consume(Terminal.MULTOPR);
            repMultOprFactor.factor = factor();
            repMultOprFactor.repMultOprFactor = repMultOprFactor();
            return repMultOprFactor;
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
            return new ConcSyn.RepMultOprFactorEpsilon();
        } else throw new GrammarError("repMultOprFactor");
    }

    private ConcSyn.IFactor factor() throws GrammarError {
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
            ConcSyn.LiteralFactor literalFactor = new ConcSyn.LiteralFactor();
            literalFactor.literal = (Literal) consume(Terminal.LITERAL);
            return literalFactor;
        } else if (token.terminal == Terminal.IDENT) {
            ConcSyn.IdentFactor identFactor = new ConcSyn.IdentFactor();
            identFactor.identifier = (Identifier) consume(Terminal.IDENT);
            identFactor.optInitOrExpressionListOrRecordAccess = optInitOrExprListOrRecordAccess();
            return identFactor;
        } else if (token.terminal == Terminal.ADDOPR || token.terminal == Terminal.NOTOPR) {
            ConcSyn.MonadicFactor monadicFactor = new ConcSyn.MonadicFactor();
            monadicFactor.monadicOpr = monadicOpr();
            monadicFactor.factor = factor();
            return monadicFactor;
        } else if (token.terminal == Terminal.LPAREN) {
            ConcSyn.ExpressionFactor expressionFactor = new ConcSyn.ExpressionFactor();
            consume(Terminal.LPAREN);
            expressionFactor.expression = expr();
            consume(Terminal.RPAREN);
            return expressionFactor;
        } else throw new GrammarError("factor");
    }

    private ConcSyn.IOptInitOrExpressionListOrRecordAccess optInitOrExprListOrRecordAccess() throws GrammarError {
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
            return new ConcSyn.Init();
        } else if (token.terminal == Terminal.LPAREN) {
            return exprListFactor();
        } else if (token.terminal == Terminal.ACCESSOPR) {
            return recordAccess();
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
            return new ConcSyn.OptInitOrExpressionListOrRecordAccessEpsilon();
        } else throw new GrammarError("optInitOrExprListOrRecordAccess");
    }

    private ConcSyn.IRecordAccess recordAccess() throws GrammarError {
        /*
          terminal ACCESSOPR
            ACCESSOPR IDENT <optRecordAccess>
         */
        if (token.terminal == Terminal.ACCESSOPR) {
            ConcSyn.RecordAccess recordAccess = new ConcSyn.RecordAccess();
            consume(Terminal.ACCESSOPR);
            recordAccess.identifier = (Identifier) consume(Terminal.IDENT);
            recordAccess.optRecordAccess = optRecordAccess();
            return recordAccess;
        } else throw new GrammarError("recordAccess");
    }

    private ConcSyn.IRecordAccess optRecordAccess() throws GrammarError {
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
            return recordAccess();
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
            return new ConcSyn.RecordAccessEpsilon();
        } else throw new GrammarError("optRecordAccess");
    }

    private ConcSyn.IMonadicOpr monadicOpr() throws GrammarError {
        /*
          terminal NOTOPR
            NOTOPR
          terminal ADDOPR
            ADDOPR
         */
        if (token.terminal == Terminal.NOTOPR) {
            consume(Terminal.NOTOPR);
            return new ConcSyn.NotMonadicOpr();
        } else if (token.terminal == Terminal.ADDOPR) {
            AddOpr addOpr = (AddOpr) this.token;
            consume(Terminal.ADDOPR);
            return new ConcSyn.PosMonadicOpr(addOpr);
        } else throw new GrammarError("monadicOpr");
    }

    private ConcSyn.IRepBoolOprTerm1 repBoolOprTerm1() throws GrammarError {
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
            ConcSyn.RepBoolOprTerm1 repBoolOprTerm1 = new ConcSyn.RepBoolOprTerm1();
            repBoolOprTerm1.boolOpr = (BoolOpr) consume(Terminal.BOOLOPR);
            repBoolOprTerm1.term1 = term1();
            repBoolOprTerm1.repBoolOprTerm1 = repBoolOprTerm1();
            return repBoolOprTerm1;
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
            return new ConcSyn.RepBoolOprTerm1Epsilon();
        } else throw new GrammarError("repBoolOprTerm1");
    }
}
