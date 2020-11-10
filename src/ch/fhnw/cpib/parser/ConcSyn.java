package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.lexer.tokens.*;

public class ConcSyn {
    interface IType {}
    static class TraditionalType implements IType {
        public Type type;
    }
    static class RecordType implements IType {
        public Identifier name;
    }

    interface IOptFlowmode {}
    static class OptFlowmode implements IOptFlowmode {
        public Flowmode flowmode;
    }
    static class OptFlowmodeEpsilon implements IOptFlowmode {}

    interface IOptMechmode {}
    static class OptMechmode implements IOptMechmode {
        public Mechmode mechmode;
    }
    static class OptMechmodeEpsilon implements IOptMechmode {}

    interface IOptChangemode {}
    static class OptChangemode implements IOptChangemode {
        public Changemode changemode;
    }
    static class OptChangemodeEpsilon implements IOptChangemode {}

    interface ITypedIdent {}
    static class TypedIdent implements ITypedIdent {
        public Identifier identifier;
        public IType type;
    }

    interface IProgParam {}
    static class ProgParam implements IProgParam {
        public IOptFlowmode optFlowmode;
        public IOptChangemode optChangemode;
        public ITypedIdent typedIdent;
    }
    interface IOptProgParamRepCommaProgParam {}
    static class OptProgParamRepCommaProgParam implements IOptProgParamRepCommaProgParam {
        public IProgParam progParam;
        public IOptProgParamRepCommaProgParam optProgParamRepCommaProgParam;
    }
    static class OptProgParamRepCommaProgParamEpsilon implements IOptProgParamRepCommaProgParam {}
    interface IProgParamList {}
    static class ProgParamList implements IProgParamList {
        public IOptProgParamRepCommaProgParam optProgParamRepCommaProgParam;
    }

    interface IParam {}
    static class Param implements IParam {
        IOptFlowmode optFlowmode;
        IOptMechmode optMechmode;
        IOptChangemode optChangemode;
        ITypedIdent typedIdent;
    }
    static class ParamEpsilon implements IParam {}

    interface IOptParamRepCommaParam {}
    static class OptParamRepCommaParam implements IOptParamRepCommaParam {
        IParam param;
        IOptParamRepCommaParam optParamRepCommaParam;
    }
    static class OptParamRepCommaParamEpsilon implements IOptParamRepCommaParam {}

    interface IParamList {}
    static class ParamList implements IParamList {
        IOptParamRepCommaParam optParamRepCommaParam;
    }

    interface IGlobImp {}
    static class GlobImp implements IGlobImp {
        IOptFlowmode optFlowmode;
        IOptChangemode optChangemode;
        Identifier identifier;
    }

    interface IGlobImps {}
    static class GlobImps implements IGlobImps {
        IGlobImp globImp;
        IGlobImps globImps;
    }
    static class GlobImpsEpsilon implements IGlobImps {}

    interface IOptGlobalGlobImps {}
    static class OptGlobalGlobImps implements IOptGlobalGlobImps {
        IGlobImps globImps;
    }
    static class OptGlobalGlobImpsEpsilon implements IOptGlobalGlobImps {}

    interface ICpsStoDecl {}
    static class CpsStoDecl implements ICpsStoDecl {
        IStoDecl stoDecl;
        ICpsStoDecl cpsStoDecl;
    }
    static class CpsStoDeclEpsilon implements ICpsStoDecl {}

    interface IOptLocalCpsStoDecl {}
    static class OptLocalCpsStoDecl implements IOptLocalCpsStoDecl {
        ICpsStoDecl cpsStoDecl;
    }
    static class OptLocalCpsStoDeclEpsilon implements IOptLocalCpsStoDecl {}

    interface ICpsTypedIdent {}
    static class CpsTypedIdent implements ICpsTypedIdent {
        ITypedIdent typedIdent;
        ICpsTypedIdent cpsTypedIdent;
    }
    static class CpsTypedIdentEpsilon implements ICpsTypedIdent {}

    interface IDecl {}
    interface IStoDecl extends IDecl {}
    static class StoDecl implements IStoDecl {
        public IOptChangemode optChangemode;
        public ITypedIdent typedIdent;
    }
    interface IFunDecl extends IDecl {}
    static class FunDecl implements IFunDecl {
        Identifier name;
        IParamList paramList;
        IStoDecl stoDecl;
        IOptGlobalGlobImps optGlobalGlobImps;
        IOptLocalCpsStoDecl optLocalCpsStoDecl;
        ICpsCmd cpsCmd;
    }
    interface IProcDecl extends IDecl {}
    static class ProcDecl implements IProcDecl {
        Identifier name;
        IParamList paramList;
        IOptGlobalGlobImps optGlobalGlobImps;
        IOptLocalCpsStoDecl optLocalCpsStoDecl;
        ICpsCmd cpsCmd;
    }
    interface IRecordShapeDecl extends IDecl {}
    static class RecordShapeDecl implements IRecordShapeDecl {
        Identifier name;
        ICpsTypedIdent cpsTypedIdent;
    }

    interface ICpsDecl {}
    static class CpsDecl implements ICpsDecl {
        public IDecl decl;
        public ICpsDecl cpsDecl;
    }
    static class CpsDeclEpsilon implements ICpsDecl {}

    interface IOptGlobalCpsDecl {}
    static class OptGlobalCpsDecl implements IOptGlobalCpsDecl {
        public ICpsDecl cpsDecl;
    }
    static class OptGlobalCpsDeclEpsilon implements IOptGlobalCpsDecl {}

    interface IOptExprRepCommaExpr {}
    static class OptExprRepCommaExpr implements IOptExprRepCommaExpr {
        IExpr expression;
        IOptExprRepCommaExpr optExprRepCommaExpr;
    }
    static class OptExprRepCommaExprEpsilon implements IOptExprRepCommaExpr {}

    interface IMonadicOpr {}
    static class NotMonadicOpr implements IMonadicOpr {}
    static class AddMonadicOpr implements IMonadicOpr {}

    interface IOptInitOrExpressionListOrRecordAccess {}
    static class Init implements IOptInitOrExpressionListOrRecordAccess {}
    interface IExprList extends IOptInitOrExpressionListOrRecordAccess {}
    static class ExprList implements IExprList {
        IOptExprRepCommaExpr optExprRepCommaExpr;
    }
    interface IRecordAccess extends IOptInitOrExpressionListOrRecordAccess {}
    static class RecordAccess implements IRecordAccess {
        Identifier identifier;
        IRecordAccess optRecordAccess;
    }
    static class RecordAccessEpsilon implements IRecordAccess {}
    static class OptInitOrExpressionListOrRecordAccessEpsilon implements IOptInitOrExpressionListOrRecordAccess {}

    interface IFactor {}
    interface ILiteralFactor extends IFactor {}
    static class LiteralFactor implements ILiteralFactor {
        Literal literal;
    }
    interface IIdentFactor extends IFactor {}
    static class IdentFactor implements IIdentFactor {
        Identifier identifier;
        IOptInitOrExpressionListOrRecordAccess optInitOrExpressionListOrRecordAccess;
    }
    interface IMonadicFactor extends IFactor {}
    static class MonadicFactor implements IMonadicFactor {
        IMonadicOpr monadicOpr;
        IFactor factor;
    }
    interface IExpressionFactor extends IFactor {}
    static class ExpressionFactor implements IExpressionFactor {
        IExpr expression;
    }

    interface IRepMultOprFactor {}
    static class RepMultOprFactor implements IRepMultOprFactor {
        MultOpr multOpr;
        IFactor factor;
        IRepMultOprFactor repMultOprFactor;
    }
    static class RepMultOprFactorEpsilon implements IRepMultOprFactor {}

    interface ITerm3 {}
    static class Term3 implements ITerm3 {
        IFactor factor;
        IRepMultOprFactor repMultOprFactor;
    }

    interface IRepAddOprTerm3 {}
    static class RepAddOprTerm3 implements IRepAddOprTerm3 {
        AddOpr addOpr;
        ITerm3 term3;
        IRepAddOprTerm3 repAddOprTerm3;
    }
    static class RepAddOprTerm3Epsilon implements IRepAddOprTerm3 {}

    interface ITerm2 {}
    static class Term2 implements ITerm2 {
        ITerm3 term3;
        IRepAddOprTerm3 repAddOprTerm3;
    }

    interface IOptRelOprTerm2 {}
    static class OptRelOprTerm2 implements IOptRelOprTerm2 {
        RelOpr relOpr;
        ITerm2 term2;
    }
    static class OptRelOprTerm2Epsilon implements IOptRelOprTerm2 {}

    interface ITerm1 {}
    static class Term1 implements ITerm1 {
        ITerm2 term2;
        IOptRelOprTerm2 optRelOprTerm2;
    }

    interface IRepBoolOprTerm1 {}
    static class RepBoolOprTerm1 implements IRepBoolOprTerm1 {
        BoolOpr boolOpr;
        ITerm1 term1;
        IRepBoolOprTerm1 repBoolOprTerm1;
    }
    static class RepBoolOprTerm1Epsilon implements IRepBoolOprTerm1 {}

    interface IExpr {}
    static class Expr implements IExpr {
        ITerm1 term1;
        IRepBoolOprTerm1 repBoolOprTerm1;
    }

    interface IOptElseCpsCmd {}
    static class OptElseCpsCmd implements IOptElseCpsCmd {
        ICpsCmd cpsCmd;
    }
    static class OptElseCpsCmdEpsilon implements IOptElseCpsCmd {}

    interface IOptGlobInits {}
    static class OptGlobInits implements IOptGlobInits {
        Identifier identifier;
        IOptGlobInits optGlobInits;
    }
    static class OptGlobInitsEpsilon implements IOptGlobInits {}

    interface ICmd {}
    static class SkipCmd implements ICmd {}
    interface IAssignmentCmd extends ICmd {}
    static class AssignmentCmd implements IAssignmentCmd {
        IExpr expr1;
        IExpr expr2;
    }
    interface IIfCmd extends ICmd {}
    static class IfCmd implements IIfCmd {
        IExpr conditionExpr;
        ICpsCmd cpsCmd;
        IOptElseCpsCmd optElseCpsCmd;
    }
    interface IWhileCmd extends ICmd {}
    static class WhileCmd implements IWhileCmd {
        IExpr conditionExpr;
        ICpsCmd cpsCmd;
    }
    interface ICallCmd extends ICmd {}
    static class CallCmd implements ICallCmd {
        Identifier identifier;
        IExprList exprList;
        IOptGlobInits optGlobInits;
    }
    interface IDebugInCmd extends ICmd {}
    static class DebugInCmd implements IDebugInCmd {
        IExpr expr;
    }
    interface IDebugOutCmd extends ICmd {}
    static class DebugOutCmd implements IDebugOutCmd {
        IExpr expr;
    }

    interface ICpsCmd {}
    static class CpsCmd implements ICpsCmd {
        ICmd cmd;
        ICpsCmd cpsCmd;
    }
    static class CpsCmdEpsilon implements ICpsCmd {}

    public interface IProgram {}
    static class Program implements IProgram {
        public IProgParamList progParamList;
        public IOptGlobalCpsDecl optGlobalCpsDecl;
        public ICpsCmd cpsCmd;
    }
}
