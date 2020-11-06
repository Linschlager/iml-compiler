(* based on https://gitlab.com/rijkt/cpib/-/blob/main/src/main/resources/grammar.sml *)

datatype term
  = ADDOPR
  | BECOMES
  | BOOLOPR
  | CALL
  | CHANGEMODE
  | COLON
  | COMMA
  | DEBUGIN
  | DEBUGOUT
  | DO
  | ELSE
  | ENDFUN
  | ENDIF
  | ENDPROC
  | ENDPROGRAM
  | ENDWHILE
  | FUN
  | FLOWMODE
  | GLOBAL
  | IDENT
  | IF
  | INIT
  | LITERAL
  | LOCAL
  | LPAREN
  | MECHMODE
  | MULTOPR
  | NOTOPR
  | PROC
  | PROGRAM
  | RELOPR
  | RETURNS
  | RPAREN
  | SKIP
  | SEMICOLON
  | SENTINEL
  | THEN
  | TYPE
  | WHILE
  | RECORD
  | ACCESSOPR

val string_of_term =
  fn ADDOPR  => "ADDOPR"
   | BECOMES => "BECOMES"
   | BOOLOPR => "BOOLOPR"
   | CALL => "CALL"
   | CHANGEMODE => "CHANGEMODE"
   | COLON => "COLON"
   | COMMA => "COMMA"
   | DEBUGIN => "DEBUGIN"
   | DEBUGOUT => "DEBUGOUT"
   | DO => "DO"
   | ELSE => "ELSE"
   | ENDFUN => "ENDFUN"
   | ENDIF => "ENDIF"
   | ENDPROC => "ENDPROC"
   | ENDPROGRAM => "ENDPROGRAM"
   | ENDWHILE => "ENDWHILE"
   | FUN => "FUN"
   | FLOWMODE => "FLOWMODE"
   | GLOBAL => "GLOBAL"
   | IDENT => "IDENT"
   | IF => "IF"
   | INIT => "INIT"
   | LITERAL => "LITERAL"
   | LOCAL => "LOCAL"
   | LPAREN => "LPAREN"
   | MECHMODE => "MECHMODE"
   | MULTOPR => "MULTOPR"
   | NOTOPR => "NOTOPR"
   | PROC => "PROC"
   | PROGRAM => "PROGRAM"
   | RELOPR => "RELOPR"
   | RETURNS => "RETURNS"
   | RPAREN => "RPAREN"
   | SKIP => "SKIP"
   | SEMICOLON => "SEMICOLON"
   | SENTINEL => "SENTINEL"
   | THEN => "THEN"
   | TYPE => "TYPE"
   | WHILE => "WHILE"
   | RECORD => "RECORD"
   | ACCESSOPR => "ACCESSOPR"

datatype nonterm
  = cmd
  | cpsCmd
  | cpsDecl
  | cpsStoDecl
  | decl
  | expr
  | exprList
  | factor
  | funDecl
  | globImp
  | globImps
  | monadicOpr
  | optChangemode
  | optElseCpsCmd
  | optExprRepCommaExpr
  | optFlowmode
  | optGlobalCpsDecl
  | optGlobalGlobImps
  | optGlobInits (* only occurs optionally, so no non-optional nts was made *)
  | optInitOrExprListOrRecordAccess (* From Implementation View *)
  | optLocalCpsStoDecl
  | optMechmode
  | optParamRepCommaParam
  | optProgParamRepCommaProgParam
  | optRelOprTerm2
  | param
  | paramList
  | procDecl
  | progParam
  | progParamList
  | program
  | repAddOprTerm3
  | repBoolOprTerm1 (* From Implementation View: /\?, \/? *)
  | repCommaExpr
  | repCommaParam
  | repCommaProgParam
  | repCommaGlobImp
  | repMultOprFactor
  | repSemicolonCmd
  | repSemicolonDecl
  | repCommaIdent
  | repSemicolonStoDecl
  | stoDecl
  | term1
  | term2
  | term3
  | typedIdent
  | typeOrRecord
  | recordShapeDecl
  | repCommaTypedIdent
  | recordAccess
  | optRecordAccess

val string_of_nonterm =
  fn cmd => "cmd"
       | cpsCmd => "cpsCmd"
       | cpsDecl => "cpsDecl"
       | cpsStoDecl => "cpsStoDecl"
       | decl => "decl"
       | expr => "expr"
       | exprList => "exprList"
       | factor => "factor"
       | funDecl => "funDecl"
       | globImp => "globImp"
       | globImps => "globImps"
       | monadicOpr => "monadicOpr"
       | optChangemode => "optChangemode"
       | optElseCpsCmd => "optElseCpsCmd"
       | optExprRepCommaExpr => "optExprRepCommaExpr"
       | optFlowmode => "optFlowmode"
       | optGlobalCpsDecl => "optGlobalCpsDecl"
       | optGlobalGlobImps => "optGlobalGlobImps"
       | optGlobInits => "optGlobInits"
       | optInitOrExprListOrRecordAccess => "optInitOrExprListOrRecordAccess"
       | optLocalCpsStoDecl => "optLocalCpsStoDecl"
       | optMechmode => "optMechmode"
       | optParamRepCommaParam => "optParamRepCommaParam"
       | optProgParamRepCommaProgParam => "optProgParamRepCommaProgParam"
       | optRelOprTerm2 => "optRelOprTerm2"
       | param => "param"
       | paramList => "paramList"
       | procDecl => "procDecl"
       | progParam => "progParam"
       | progParamList => "progParamList"
       | program => "program"
       | repAddOprTerm3 => "repAddOprTerm3"
       | repBoolOprTerm1 => "repBoolOprTerm1"
       | repCommaExpr => "repCommaExpr"
       | repCommaParam => "repCommaParam"
       | repCommaProgParam => "repCommaProgParam"
       | repCommaGlobImp => "repCommaGlobImp"
       | repMultOprFactor => "repMultOprFactor"
       | repSemicolonCmd => "repSemicolonCmd"
       | repSemicolonDecl => "repSemicolonDecl"
       | repCommaIdent => "repCommaIdent"
       | repSemicolonStoDecl => "repSemicolonStoDecl"
       | stoDecl => "stoDecl"
       | term1 => "term1"
       | term2 => "term2"
       | term3 => "term3"
       | typedIdent => "typedIdent"
       | typeOrRecord => "typeOrRecord"
       | recordShapeDecl => "recordShapeDecl"
       | repCommaTypedIdent => "repCommaTypedIdent"
       | recordAccess => "recordAccess"
       | optRecordAccess => "optRecordAccess"

val string_of_gramsym = (string_of_term, string_of_nonterm)

local
  open FixFoxi.FixFoxiCore
in

val productions =
[
(program,
    [[T PROGRAM, T IDENT, N progParamList, N optGlobalCpsDecl, T DO, N cpsCmd, T ENDPROGRAM]]),
(progParamList,
    [[T LPAREN, N optProgParamRepCommaProgParam, T RPAREN]]),
(optProgParamRepCommaProgParam,
    [[N progParam, N repCommaProgParam],
    []]),
(progParam,
    [[N optFlowmode, N optChangemode, N typedIdent]]),
(optFlowmode,
    [[T FLOWMODE],
     []]),
(optChangemode,
    [[T CHANGEMODE],
     []]),
(typedIdent,
    [[T IDENT, T COLON, N typeOrRecord]]),
(typeOrRecord,
    [[T TYPE],
     [T IDENT]]),
(repCommaProgParam,
    [[T COMMA, N progParam, N repCommaProgParam],
    []]),
(optGlobalCpsDecl,
    [[T GLOBAL, N cpsDecl],
     []]),
(cpsDecl,
    [[N decl, N repSemicolonDecl]]),
(decl,
    [[N stoDecl],
    [N funDecl],
    [N procDecl],
    [N recordShapeDecl]]),
(stoDecl,
    [[N optChangemode, N typedIdent]]),
(funDecl,
    [[T FUN, T IDENT, N paramList, T RETURNS, N stoDecl, N optGlobalGlobImps, N optLocalCpsStoDecl, T DO, N cpsCmd, T ENDFUN]]),
(paramList,
    [[T LPAREN, N optParamRepCommaParam, T RPAREN]]),
(optParamRepCommaParam,
    [[N param, N repCommaParam],
    []]),
(repCommaParam,
    [[T COMMA, N param, N repCommaParam],
     []]),
(param,
    [[N optFlowmode, N optMechmode, N optChangemode, N typedIdent]]),
(optMechmode,
    [[T MECHMODE],
    []]),
(optGlobalGlobImps,
    [[T GLOBAL, N globImps],
     []]),
(globImps,
    [[N globImp, N repCommaGlobImp]]),
(globImp,
    [[N optFlowmode, N optChangemode, T IDENT]]),
(repCommaGlobImp,
    [[T COMMA, N globImps],
     []]),
(optLocalCpsStoDecl,
    [[T LOCAL, N cpsStoDecl],
     []]),
(cpsStoDecl,
    [[N stoDecl, N repSemicolonStoDecl]]),
(repSemicolonStoDecl,
    [[T SEMICOLON, N stoDecl, N repSemicolonStoDecl],
     []]),
(procDecl,
    [[T PROC, T IDENT, N paramList, N optGlobalGlobImps, N optLocalCpsStoDecl, T DO, N cpsCmd, T ENDPROC]]),
(repSemicolonDecl,
    [[T SEMICOLON, N decl, N repSemicolonDecl],
     []]),
(cmd,
    [[T SKIP],
     [N expr, T BECOMES, N expr],
     [T IF, N expr, T THEN, N cpsCmd, N optElseCpsCmd, T ENDIF],
     [T WHILE, N expr, T DO, N cpsCmd, T ENDWHILE],
     [T CALL, T IDENT, N exprList, N optGlobInits],
     [T DEBUGIN, N expr],
     [T DEBUGOUT, N expr]]),
(optElseCpsCmd,
    [[T ELSE, N cpsCmd],
    []]),
(cpsCmd,
    [[N cmd, N repSemicolonCmd]]),
(repSemicolonCmd,
    [[T SEMICOLON, N cmd, N repSemicolonCmd],
     []]),
(optGlobInits,
    [[T INIT, T IDENT, N repCommaIdent],
     []]),
(repCommaIdent,
    [[T COMMA, T IDENT, N repCommaIdent],
     []]),
(expr,
    [[N term1, N repBoolOprTerm1]]),
(repBoolOprTerm1,
    [[T BOOLOPR, N term1, N repBoolOprTerm1],
     []]),
(term1,
    [[N term2, N optRelOprTerm2]]),
(optRelOprTerm2,
    [[T RELOPR, N term2],
     []]),
(term2,
    [[N term3, N repAddOprTerm3]]),
(repAddOprTerm3,
    [[T ADDOPR, N term3, N repAddOprTerm3],
     []]),
(term3,
    [[N factor, N repMultOprFactor]]),
(repMultOprFactor,
    [[T MULTOPR, N factor, N repMultOprFactor],
     []]),
(factor,
    [[T LITERAL],
     [T IDENT, N optInitOrExprListOrRecordAccess],
     [N monadicOpr, N factor],
     [T LPAREN, N expr, T RPAREN]]),
(optInitOrExprListOrRecordAccess,
    [[T INIT],
     [N exprList],
     [N recordAccess],
     []]),
(exprList,
    [[T LPAREN, N optExprRepCommaExpr, T RPAREN]]),
(optExprRepCommaExpr,
    [[N expr, N repCommaExpr],
     []]),
(repCommaExpr,
    [[T COMMA, N expr, N repCommaExpr],
     []]),
(monadicOpr,
    [[T NOTOPR],
     [T ADDOPR]]),

(recordAccess,
    [[T ACCESSOPR, T IDENT, N optRecordAccess]]),
(optRecordAccess,
    [[T ACCESSOPR, T IDENT, N optRecordAccess],
     []]),
(recordShapeDecl,
    [[T RECORD, T IDENT, T LPAREN, N typedIdent, N repCommaTypedIdent, T RPAREN]]),
(repCommaTypedIdent,
    [[T COMMA, N typedIdent, N repCommaTypedIdent],
    []])
] (* no comma on last entry *)

val S = program

val result = fix_foxi productions S string_of_gramsym

end (* local *)
