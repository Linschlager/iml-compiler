package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.lexer.tokens.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ConcSyn {
    interface IType {
        public AbsSyn.IType toAbsSyn();
    }
    static class TraditionalType implements IType {
        public Type type;

        @Override
        public AbsSyn.TraditionalType toAbsSyn() {
            return new AbsSyn.TraditionalType(type);
        }
    }
    static class RecordType implements IType {
        public Identifier name;

        @Override
        public AbsSyn.RecordType toAbsSyn() {
            return new AbsSyn.RecordType(name.ident);
        }
    }

    interface IOptFlowmode {
        public AbsSyn.IFlowMode toAbsSyn();
    }
    static class OptFlowmode implements IOptFlowmode {
        public Flowmode flowmode;

        @Override
        public AbsSyn.IFlowMode toAbsSyn() {
            return switch (flowmode.attr) {
                case IN -> new AbsSyn.InFlowMode();
                case INOUT -> new AbsSyn.InOutFlowMode();
                case OUT -> new AbsSyn.OutFlowMode();
            };
        }
    }
    static class OptFlowmodeEpsilon implements IOptFlowmode {
        @Override
        public AbsSyn.IFlowMode toAbsSyn() {
            return new AbsSyn.InFlowMode(); // Default FlowMode
        }
    }

    interface IOptMechmode {
        public AbsSyn.IMechMode toAbsSyn();
    }
    static class OptMechmode implements IOptMechmode {
        public Mechmode mechmode;

        @Override
        public AbsSyn.IMechMode toAbsSyn() {
            return switch (mechmode.attr) {
                case COPY -> new AbsSyn.CopyMechMode();
                case REF -> new AbsSyn.RefMechMode();
            };
        }
    }
    static class OptMechmodeEpsilon implements IOptMechmode {
        @Override
        public AbsSyn.IMechMode toAbsSyn() {
            return new AbsSyn.CopyMechMode(); // Default MechMode
        }
    }

    interface IOptChangemode {
        public AbsSyn.IChangeMode toAbsSyn();
    }
    static class OptChangemode implements IOptChangemode {
        public Changemode changemode;

        @Override
        public AbsSyn.IChangeMode toAbsSyn() {
            return switch (changemode.attr) {
                case VAR -> new AbsSyn.VarChangeMode();
                case CONST -> new AbsSyn.ConstChangeMode();
            };
        }
    }
    static class OptChangemodeEpsilon implements IOptChangemode {
        @Override
        public AbsSyn.IChangeMode toAbsSyn() {
            return new AbsSyn.ConstChangeMode(); // Default ChangeMode
        }
    }

    interface ITypedIdent {
        public AbsSyn.ITypedIdentifier toAbsSyn();
    }
    static class TypedIdent implements ITypedIdent {
        public Identifier identifier;
        public IType type;

        @Override
        public AbsSyn.ITypedIdentifier toAbsSyn() {
            return new AbsSyn.TypedIdentifier(identifier.ident, type.toAbsSyn());
        }
    }

    interface IProgParam {
        public AbsSyn.IProgramParameter toAbsSyn();
    }
    static class ProgParam implements IProgParam {
        public IOptFlowmode optFlowmode;
        public IOptChangemode optChangemode;
        public ITypedIdent typedIdent;

        @Override
        public AbsSyn.IProgramParameter toAbsSyn() {
            return new AbsSyn.ProgramParameter(
                    optFlowmode.toAbsSyn(),
                    optChangemode.toAbsSyn(),
                    typedIdent.toAbsSyn()
            );
        }
    }
    interface IOptProgParamRepCommaProgParam {
        public List<AbsSyn.IProgramParameter> toAbsSyn();
    }
    static class OptProgParamRepCommaProgParam implements IOptProgParamRepCommaProgParam {
        public IProgParam progParam;
        public IOptProgParamRepCommaProgParam optProgParamRepCommaProgParam;

        @Override
        public List<AbsSyn.IProgramParameter> toAbsSyn() {
            List<AbsSyn.IProgramParameter> list = new LinkedList<>();
            list.add(progParam.toAbsSyn());
            List<AbsSyn.IProgramParameter> next = optProgParamRepCommaProgParam.toAbsSyn();
            list.addAll(next);
            return list;
        }
    }
    static class OptProgParamRepCommaProgParamEpsilon implements IOptProgParamRepCommaProgParam {
        @Override
        public List<AbsSyn.IProgramParameter> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface IProgParamList {
        public List<AbsSyn.IProgramParameter> toAbsSyn();
    }
    static class ProgParamList implements IProgParamList {
        public IOptProgParamRepCommaProgParam optProgParamRepCommaProgParam;

        @Override
        public List<AbsSyn.IProgramParameter> toAbsSyn() {
            return optProgParamRepCommaProgParam.toAbsSyn();
        }
    }

    interface IParam {
        public AbsSyn.IParameter toAbsSyn();
    }
    static class Param implements IParam {
        IOptFlowmode optFlowmode;
        IOptMechmode optMechmode;
        IOptChangemode optChangemode;
        ITypedIdent typedIdent;

        @Override
        public AbsSyn.IParameter toAbsSyn() {
            return new AbsSyn.Parameter(
                    optFlowmode.toAbsSyn(),
                    optMechmode.toAbsSyn(),
                    optChangemode.toAbsSyn(),
                    typedIdent.toAbsSyn()
            );
        }
    }
    static class ParamEpsilon implements IParam {
        @Override
        public AbsSyn.IParameter toAbsSyn() {return null;}
    }

    interface IOptParamRepCommaParam {
        public List<AbsSyn.IParameter> toAbsSyn();
    }
    static class OptParamRepCommaParam implements IOptParamRepCommaParam {
        IParam param;
        IOptParamRepCommaParam optParamRepCommaParam;

        @Override
        public List<AbsSyn.IParameter> toAbsSyn() {
            List<AbsSyn.IParameter> list = new LinkedList<>();
            list.add(param.toAbsSyn());
            List<AbsSyn.IParameter> next = optParamRepCommaParam.toAbsSyn();
            list.addAll(next);
            return list;
        }
    }
    static class OptParamRepCommaParamEpsilon implements IOptParamRepCommaParam {
        @Override
        public List<AbsSyn.IParameter> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface IParamList {
        public List<AbsSyn.IParameter> toAbsSyn();
    }
    static class ParamList implements IParamList {
        IOptParamRepCommaParam optParamRepCommaParam;

        @Override
        public List<AbsSyn.IParameter> toAbsSyn() {
            return optParamRepCommaParam.toAbsSyn();
        }
    }

    interface IGlobImp {
        public AbsSyn.IGlobalImport toAbsSyn();
    }
    static class GlobImp implements IGlobImp {
        IOptFlowmode optFlowmode;
        IOptChangemode optChangemode;
        Identifier identifier;

        @Override
        public AbsSyn.IGlobalImport toAbsSyn() {
            return new AbsSyn.GlobalImport(
                    optFlowmode.toAbsSyn(),
                    optChangemode.toAbsSyn(),
                    identifier.ident
            );
        }
    }

    interface IGlobImps {
        public List<AbsSyn.IGlobalImport> toAbsSyn();
    }
    static class GlobImps implements IGlobImps {
        IGlobImp globImp;
        IGlobImps globImps;

        @Override
        public List<AbsSyn.IGlobalImport> toAbsSyn() {
            List<AbsSyn.IGlobalImport> list = new LinkedList<>();
            list.add(globImp.toAbsSyn());
            List<AbsSyn.IGlobalImport> next = globImps.toAbsSyn();
            list.addAll(next);
            return list;
        }
    }
    static class GlobImpsEpsilon implements IGlobImps {
        @Override
        public List<AbsSyn.IGlobalImport> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface IOptGlobalGlobImps {
        public List<AbsSyn.IGlobalImport> toAbsSyn();
    }
    static class OptGlobalGlobImps implements IOptGlobalGlobImps {
        IGlobImps globImps;

        @Override
        public List<AbsSyn.IGlobalImport> toAbsSyn() {
            return globImps.toAbsSyn();
        }
    }
    static class OptGlobalGlobImpsEpsilon implements IOptGlobalGlobImps {
        @Override
        public List<AbsSyn.IGlobalImport> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface ICpsStoDecl {
        public List<AbsSyn.IStorageDeclaration> toAbsSyn();
    }
    static class CpsStoDecl implements ICpsStoDecl {
        IStoDecl stoDecl;
        ICpsStoDecl cpsStoDecl;

        @Override
        public List<AbsSyn.IStorageDeclaration> toAbsSyn() {
            List<AbsSyn.IStorageDeclaration> list = new LinkedList<>();
            list.add(stoDecl.toAbsSyn());
            List<AbsSyn.IStorageDeclaration> next = cpsStoDecl.toAbsSyn();
            list.addAll(next);
            return list;
        }
    }
    static class CpsStoDeclEpsilon implements ICpsStoDecl {
        @Override
        public List<AbsSyn.IStorageDeclaration> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface IOptLocalCpsStoDecl {
        public List<AbsSyn.IStorageDeclaration> toAbsSyn();
    }
    static class OptLocalCpsStoDecl implements IOptLocalCpsStoDecl {
        ICpsStoDecl cpsStoDecl;

        @Override
        public List<AbsSyn.IStorageDeclaration> toAbsSyn() {
            return cpsStoDecl.toAbsSyn();
        }
    }
    static class OptLocalCpsStoDeclEpsilon implements IOptLocalCpsStoDecl {
        @Override
        public List<AbsSyn.IStorageDeclaration> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface ICpsTypedIdent {
        public List<AbsSyn.ITypedIdentifier> toAbsSyn();
    }
    static class CpsTypedIdent implements ICpsTypedIdent {
        ITypedIdent typedIdent;
        ICpsTypedIdent cpsTypedIdent;

        @Override
        public List<AbsSyn.ITypedIdentifier> toAbsSyn() {
            List<AbsSyn.ITypedIdentifier> list = new LinkedList<>();
            list.add(typedIdent.toAbsSyn());
            List<AbsSyn.ITypedIdentifier> next = cpsTypedIdent.toAbsSyn();
            list.addAll(next);
            return list;
        }
    }
    static class CpsTypedIdentEpsilon implements ICpsTypedIdent {
        @Override
        public List<AbsSyn.ITypedIdentifier> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface IDecl {
        public AbsSyn.IDeclaration toAbsSyn();
    }
    interface IStoDecl extends IDecl {
        @Override
        public AbsSyn.IStorageDeclaration toAbsSyn();
    }
    static class StoDecl implements IStoDecl {
        public IOptChangemode optChangemode;
        public ITypedIdent typedIdent;

        @Override
        public AbsSyn.IStorageDeclaration toAbsSyn() {
            return new AbsSyn.StorageDeclaration(
                    optChangemode.toAbsSyn(),
                    typedIdent.toAbsSyn()
            );
        }
    }
    interface IFunDecl extends IDecl {
        @Override
        public AbsSyn.IFunctionDeclaration toAbsSyn();
    }
    static class FunDecl implements IFunDecl {
        Identifier name;
        IParamList paramList;
        IStoDecl stoDecl;
        IOptGlobalGlobImps optGlobalGlobImps;
        IOptLocalCpsStoDecl optLocalCpsStoDecl;
        ICpsCmd cpsCmd;

        @Override
        public AbsSyn.IFunctionDeclaration toAbsSyn() {
            return new AbsSyn.FunctionDeclaration(
                    name.ident,
                    paramList.toAbsSyn(),
                    stoDecl.toAbsSyn(),
                    optGlobalGlobImps.toAbsSyn(),
                    optLocalCpsStoDecl.toAbsSyn(),
                    cpsCmd.toAbsSyn()
            );
        }
    }
    interface IProcDecl extends IDecl {
        @Override
        AbsSyn.IProcedureDeclaration toAbsSyn();
    }
    static class ProcDecl implements IProcDecl {
        Identifier name;
        IParamList paramList;
        IOptGlobalGlobImps optGlobalGlobImps;
        IOptLocalCpsStoDecl optLocalCpsStoDecl;
        ICpsCmd cpsCmd;

        @Override
        public AbsSyn.IProcedureDeclaration toAbsSyn() {
            return new AbsSyn.ProcedureDeclaration(
                    name.ident,
                    paramList.toAbsSyn(),
                    optGlobalGlobImps.toAbsSyn(),
                    optLocalCpsStoDecl.toAbsSyn(),
                    cpsCmd.toAbsSyn()
            );
        }
    }
    interface IRecordShapeDecl extends IDecl {
        @Override
        AbsSyn.IRecordShapeDeclaration toAbsSyn();
    }
    static class RecordShapeDecl implements IRecordShapeDecl {
        Identifier name;
        ICpsTypedIdent cpsTypedIdent;

        @Override
        public AbsSyn.IRecordShapeDeclaration toAbsSyn() {
            return new AbsSyn.RecordShapeDeclaration(
                    name.ident,
                    cpsTypedIdent.toAbsSyn()
            );
        }
    }

    interface ICpsDecl {
        public List<AbsSyn.IDeclaration> toAbsSyn();
    }
    static class CpsDecl implements ICpsDecl {
        public IDecl decl;
        public ICpsDecl cpsDecl;

        @Override
        public List<AbsSyn.IDeclaration> toAbsSyn() {
            List<AbsSyn.IDeclaration> list = new LinkedList<>();
            list.add(decl.toAbsSyn());
            List<AbsSyn.IDeclaration> next = cpsDecl.toAbsSyn();
            list.addAll(next);
            return list;
        }
    }
    static class CpsDeclEpsilon implements ICpsDecl {
        @Override
        public List<AbsSyn.IDeclaration> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface IOptGlobalCpsDecl {
        public List<AbsSyn.IDeclaration> toAbsSyn();
    }
    static class OptGlobalCpsDecl implements IOptGlobalCpsDecl {
        public ICpsDecl cpsDecl;

        @Override
        public List<AbsSyn.IDeclaration> toAbsSyn() {
            return cpsDecl.toAbsSyn();
        }
    }
    static class OptGlobalCpsDeclEpsilon implements IOptGlobalCpsDecl {
        @Override
        public List<AbsSyn.IDeclaration> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface IOptExprRepCommaExpr {
        public List<AbsSyn.IExpression> toAbsSyn();
    }
    static class OptExprRepCommaExpr implements IOptExprRepCommaExpr {
        IExpr expression;
        IOptExprRepCommaExpr optExprRepCommaExpr;

        @Override
        public List<AbsSyn.IExpression> toAbsSyn() {
            List<AbsSyn.IExpression> list = new LinkedList<>();
            list.add(expression.toAbsSyn());
            List<AbsSyn.IExpression> next = optExprRepCommaExpr.toAbsSyn();
            list.addAll(next);
            return list;
        }
    }
    static class OptExprRepCommaExprEpsilon implements IOptExprRepCommaExpr {
        @Override
        public List<AbsSyn.IExpression> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface IMonadicOpr {
        public AbsSyn.IMonadicOperator toAbsSyn();
    }
    static class NotMonadicOpr implements IMonadicOpr {
        @Override
        public AbsSyn.IMonadicOperator toAbsSyn() {
            return new AbsSyn.NotMonadicOperator();
        }
    }
    static class PosMonadicOpr implements IMonadicOpr {
        @Override
        public AbsSyn.IMonadicOperator toAbsSyn() {
            return new AbsSyn.PosMonadicOperator();
        }
    }

    interface IOptInitOrExpressionListOrRecordAccess {
        public AbsSyn.IExpression toAbsSyn(String name);
    }
    interface IInit extends IOptInitOrExpressionListOrRecordAccess {
        @Override
        public AbsSyn.IExpression toAbsSyn(String name);
    }
    static class Init implements IInit {
        @Override
        public AbsSyn.IExpression toAbsSyn(String name) {
            return new AbsSyn.StoreExpression(name, true);
        }
    }
    interface IExprListFactor extends IOptInitOrExpressionListOrRecordAccess {}
    static class ExprListFactor implements IExprListFactor {
        IExprList exprList;

        @Override
        public AbsSyn.IExpression toAbsSyn(String name) {
            return new AbsSyn.FunctionCallExpression(name, exprList.toAbsSyn());
        }
    }
    interface IExprList {
        public List<AbsSyn.IExpression> toAbsSyn();
    }
    static class ExprList implements IExprList {
        IOptExprRepCommaExpr optExprRepCommaExpr;

        @Override
        public List<AbsSyn.IExpression> toAbsSyn() {
            return optExprRepCommaExpr.toAbsSyn();
        }
    }
    interface IRecordAccess extends IOptInitOrExpressionListOrRecordAccess {
        public AbsSyn.IRecordAccessExpression toAbsSyn(String recordName);
    }
    static class RecordAccess implements IRecordAccess {
        Identifier identifier;
        IRecordAccess optRecordAccess;

        @Override
        public AbsSyn.IRecordAccessExpression toAbsSyn(String name) {
            List<String> list = new LinkedList<>();
            list.add(identifier.ident);

            List<String> next = ((AbsSyn.RecordAccessExpression)(optRecordAccess.toAbsSyn(name))).fieldNames;
            list.addAll(next);
            return new AbsSyn.RecordAccessExpression(name, list);
        }
    }
    static class RecordAccessEpsilon implements IRecordAccess {
        @Override
        public AbsSyn.IRecordAccessExpression toAbsSyn(String recordName) {
            return new AbsSyn.RecordAccessExpression(recordName, new LinkedList<>());
        }
    }
    static class OptInitOrExpressionListOrRecordAccessEpsilon implements IOptInitOrExpressionListOrRecordAccess {
        @Override
        public AbsSyn.IExpression toAbsSyn(String name) {
            return new AbsSyn.StoreExpression(name, false);
        }
    }

    interface IFactor {
        public AbsSyn.IExpression toAbsSyn();
    }
    interface ILiteralFactor extends IFactor {}
    static class LiteralFactor implements ILiteralFactor {
        Literal literal;

        @Override
        public AbsSyn.IExpression toAbsSyn() {
            return switch (literal.attr) {
                case BOOL -> new AbsSyn.BoolLiteralExpression(literal.boolValue);
                case NUMBER -> new AbsSyn.IntLiteralExpression(literal.numberValue);
            };
        }
    }
    interface IIdentFactor extends IFactor {}
    static class IdentFactor implements IIdentFactor {
        Identifier identifier;
        IOptInitOrExpressionListOrRecordAccess optInitOrExpressionListOrRecordAccess;

        @Override
        public AbsSyn.IExpression toAbsSyn() {
            return optInitOrExpressionListOrRecordAccess.toAbsSyn(identifier.ident);
        }
    }
    interface IMonadicFactor extends IFactor {}
    static class MonadicFactor implements IMonadicFactor {
        IMonadicOpr monadicOpr;
        IFactor factor;

        @Override
        public AbsSyn.IMonadicExpression toAbsSyn() {
            return new AbsSyn.MonadicExpression(
                    monadicOpr.toAbsSyn(),
                    factor.toAbsSyn()
            );
        }
    }
    interface IExpressionFactor extends IFactor {}
    static class ExpressionFactor implements IExpressionFactor {
        IExpr expression;

        @Override
        public AbsSyn.IExpression toAbsSyn() {
            return expression.toAbsSyn();
        }
    }

    interface IRepMultOprFactor {
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression factor);
    }
    static class RepMultOprFactor implements IRepMultOprFactor {
        MultOpr multOpr;
        IFactor factor;
        IRepMultOprFactor repMultOprFactor;

        @Override
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression lastFactor) {
            if (Objects.equals(repMultOprFactor.toAbsSyn(factor.toAbsSyn()), factor.toAbsSyn())) {
                return new AbsSyn.MultiplicationDyadicExpression(
                        multOpr.attr,
                        lastFactor,
                        factor.toAbsSyn()
                );
            } else {
                var left = new AbsSyn.MultiplicationDyadicExpression(
                        multOpr.attr,
                        lastFactor,
                        factor.toAbsSyn()
                );
                return repMultOprFactor.toAbsSyn(left);
            }
        }
    }
    static class RepMultOprFactorEpsilon implements IRepMultOprFactor {
        @Override
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression factor) {
            return factor;
        }
    }

    interface ITerm3 {
        public AbsSyn.IExpression toAbsSyn();
    }
    static class Term3 implements ITerm3 {
        IFactor factor;
        IRepMultOprFactor repMultOprFactor;

        @Override
        public AbsSyn.IExpression toAbsSyn() {
            if (Objects.equals(repMultOprFactor.toAbsSyn(factor.toAbsSyn()), factor.toAbsSyn())) {
                return factor.toAbsSyn();
            } else {
                return repMultOprFactor.toAbsSyn(factor.toAbsSyn());
            }
        }
    }

    interface IRepAddOprTerm3 {
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression lastFactor);
    }
    static class RepAddOprTerm3 implements IRepAddOprTerm3 {
        AddOpr addOpr;
        ITerm3 term3;
        IRepAddOprTerm3 repAddOprTerm3;

        @Override
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression lastFactor) {
            if (Objects.equals(repAddOprTerm3.toAbsSyn(term3.toAbsSyn()), term3.toAbsSyn())) {
                return new AbsSyn.AdditionDyadicExpression(
                        addOpr.attr,
                        lastFactor,
                        term3.toAbsSyn()
                );
            } else {
                var left = new AbsSyn.AdditionDyadicExpression(
                        addOpr.attr,
                        lastFactor,
                        term3.toAbsSyn()
                );
                return repAddOprTerm3.toAbsSyn(left);
            }
        }
    }
    static class RepAddOprTerm3Epsilon implements IRepAddOprTerm3 {
        @Override
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression lastFactor) {
            return lastFactor;
        }
    }

    interface ITerm2 {
        public AbsSyn.IExpression toAbsSyn();
    }
    static class Term2 implements ITerm2 {
        ITerm3 term3;
        IRepAddOprTerm3 repAddOprTerm3;

        @Override
        public AbsSyn.IExpression toAbsSyn() {
            return repAddOprTerm3.toAbsSyn(term3.toAbsSyn());
        }
    }

    interface IOptRelOprTerm2 {
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression lastFactor);
    }
    static class OptRelOprTerm2 implements IOptRelOprTerm2 {
        RelOpr relOpr;
        ITerm2 term2;

        @Override
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression lastFactor) {
            return new AbsSyn.RelativeDyadicExpression(
                    relOpr.attr,
                    lastFactor,
                    term2.toAbsSyn()
            );
        }
    }
    static class OptRelOprTerm2Epsilon implements IOptRelOprTerm2 {

        @Override
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression lastFactor) {
            return lastFactor;
        }
    }

    interface ITerm1 {
        public AbsSyn.IExpression toAbsSyn();
    }
    static class Term1 implements ITerm1 {
        ITerm2 term2;
        IOptRelOprTerm2 optRelOprTerm2;

        @Override
        public AbsSyn.IExpression toAbsSyn() {
            return optRelOprTerm2.toAbsSyn(term2.toAbsSyn());
        }
    }

    interface IRepBoolOprTerm1 {
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression lastFactor);
    }
    static class RepBoolOprTerm1 implements IRepBoolOprTerm1 {
        BoolOpr boolOpr;
        ITerm1 term1;
        IRepBoolOprTerm1 repBoolOprTerm1;

        @Override
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression lastFactor) {
            if (repBoolOprTerm1.toAbsSyn(term1.toAbsSyn()) == term1.toAbsSyn()) {
                return new AbsSyn.BoolDyadicExpression(
                        boolOpr.attr,
                        lastFactor,
                        term1.toAbsSyn()
                );
            } else {
                var left = new AbsSyn.BoolDyadicExpression(
                        boolOpr.attr,
                        lastFactor,
                        term1.toAbsSyn()
                );
                return repBoolOprTerm1.toAbsSyn(left);
            }
        }
    }
    static class RepBoolOprTerm1Epsilon implements IRepBoolOprTerm1 {
        @Override
        public AbsSyn.IExpression toAbsSyn(AbsSyn.IExpression lastFactor) {
            return lastFactor;
        }
    }

    interface IExpr {
        public AbsSyn.IExpression toAbsSyn();
    }
    static class Expr implements IExpr {
        ITerm1 term1;
        IRepBoolOprTerm1 repBoolOprTerm1;

        @Override
        public AbsSyn.IExpression toAbsSyn() {
            return repBoolOprTerm1.toAbsSyn(term1.toAbsSyn());
        }
    }

    interface IOptElseCpsCmd {
        public List<AbsSyn.ICommand> toAbsSyn();
    }
    static class OptElseCpsCmd implements IOptElseCpsCmd {
        ICpsCmd cpsCmd;

        @Override
        public List<AbsSyn.ICommand> toAbsSyn() {
            return cpsCmd.toAbsSyn();
        }
    }
    static class OptElseCpsCmdEpsilon implements IOptElseCpsCmd {
        @Override
        public List<AbsSyn.ICommand> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface IRepCommaIdents {
        public List<String> toAbsSyn();
    }
    static class RepCommaIdents implements IRepCommaIdents {
        Identifier identifier;
        IRepCommaIdents repCommaIdents;

        @Override
        public List<String> toAbsSyn() {
            List<String> list = new LinkedList<>();
            list.add(identifier.ident);
            List<String> next = repCommaIdents.toAbsSyn();
            list.addAll(next);
            return list;
        }
    }
    static class RepCommaIdentsEpsilon implements IRepCommaIdents {
        @Override
        public List<String> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface IOptGlobInits {
        public List<String> toAbsSyn();
    }
    static class OptGlobInits implements IOptGlobInits {
        Identifier identifier;
        IRepCommaIdents repCommaIdents;

        @Override
        public List<String> toAbsSyn() {
            List<String> list = new LinkedList<>();
            list.add(identifier.ident);
            List<String> next = repCommaIdents.toAbsSyn();
            list.addAll(next);
            return list;
        }
    }
    static class OptGlobInitsEpsilon implements IOptGlobInits {
        @Override
        public List<String> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    interface ICmd {
        public AbsSyn.ICommand toAbsSyn();
    }
    static class SkipCmd implements ICmd {
        @Override
        public AbsSyn.ISkipCommand toAbsSyn() {
            return new AbsSyn.SkipCommand();
        }
    }
    interface IAssignmentCmd extends ICmd {
        @Override
        public AbsSyn.IAssignmentCommand toAbsSyn();
    }
    static class AssignmentCmd implements IAssignmentCmd {
        IExpr expr1;
        IExpr expr2;

        @Override
        public AbsSyn.IAssignmentCommand toAbsSyn() {
            return new AbsSyn.AssignmentCommand(
                    expr1.toAbsSyn(),
                    expr2.toAbsSyn()
            );
        }
    }
    interface IIfCmd extends ICmd {
        @Override
        public AbsSyn.IIfCommand toAbsSyn();
    }
    static class IfCmd implements IIfCmd {
        IExpr conditionExpr;
        ICpsCmd cpsCmd;
        IOptElseCpsCmd optElseCpsCmd;

        @Override
        public AbsSyn.IIfCommand toAbsSyn() {
            return new AbsSyn.IfCommand(
                    conditionExpr.toAbsSyn(),
                    cpsCmd.toAbsSyn(),
                    optElseCpsCmd.toAbsSyn()
            );
        }
    }
    interface IWhileCmd extends ICmd {
        @Override
        public AbsSyn.IWhileCommand toAbsSyn();
    }
    static class WhileCmd implements IWhileCmd {
        IExpr conditionExpr;
        ICpsCmd cpsCmd;

        @Override
        public AbsSyn.IWhileCommand toAbsSyn() {
            return new AbsSyn.WhileCommand(
                    conditionExpr.toAbsSyn(),
                    cpsCmd.toAbsSyn()
            );
        }
    }
    interface ICallCmd extends ICmd {
        @Override
        public AbsSyn.ICallCommand toAbsSyn();
    }
    static class CallCmd implements ICallCmd {
        Identifier identifier;
        IExprList exprList;
        IOptGlobInits optGlobInits;

        @Override
        public AbsSyn.ICallCommand toAbsSyn() {
            return new AbsSyn.CallCommand(
                    identifier.ident,
                    exprList.toAbsSyn(),
                    optGlobInits.toAbsSyn()
            );
        }
    }
    interface IDebugInCmd extends ICmd {
        @Override
        public AbsSyn.IDebugInCommand toAbsSyn();
    }
    static class DebugInCmd implements IDebugInCmd {
        IExpr expr;

        @Override
        public AbsSyn.IDebugInCommand toAbsSyn() {
            return new AbsSyn.DebugInCommand(expr.toAbsSyn());
        }
    }
    interface IDebugOutCmd extends ICmd {
        @Override
        public AbsSyn.IDebugOutCommand toAbsSyn();
    }
    static class DebugOutCmd implements IDebugOutCmd {
        IExpr expr;

        @Override
        public AbsSyn.IDebugOutCommand toAbsSyn() {
            return new AbsSyn.DebugOutCommand(expr.toAbsSyn());
        }
    }

    interface ICpsCmd {
        List<AbsSyn.ICommand> toAbsSyn();
    }
    static class CpsCmd implements ICpsCmd {
        ICmd cmd;
        ICpsCmd cpsCmd;

        @Override
        public List<AbsSyn.ICommand> toAbsSyn() {
            List<AbsSyn.ICommand> list = new LinkedList<>();
            list.add(cmd.toAbsSyn());
            List<AbsSyn.ICommand> next = cpsCmd.toAbsSyn();
            list.addAll(next);
            return list;
        }
    }
    static class CpsCmdEpsilon implements ICpsCmd {
        @Override
        public List<AbsSyn.ICommand> toAbsSyn() {
            return new LinkedList<>();
        }
    }

    public interface IProgram {
        public AbsSyn.IProgram toAbsSyn();
    }
    static class Program implements IProgram {
        public Identifier identifier;
        public IProgParamList progParamList;
        public IOptGlobalCpsDecl optGlobalCpsDecl;
        public ICpsCmd cpsCmd;

        @Override
        public AbsSyn.IProgram toAbsSyn() {
            return new AbsSyn.Program(
                    identifier.ident,
                    progParamList.toAbsSyn(),
                    optGlobalCpsDecl.toAbsSyn(),
                    cpsCmd.toAbsSyn()
            );
        }
    }
}
