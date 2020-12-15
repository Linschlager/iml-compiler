package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.lexer.tokens.Type;

import java.util.List;

public class AbsSyn {
    interface IType {}
    static class TraditionalType implements IType {
        Type type;
        public TraditionalType(Type type) {
            this.type = type;
        }
    }
    static class RecordType implements IType {
        String name;

        public RecordType(String name) {
            this.name = name;
        }
    }

    interface IFlowMode {}
    static class InFlowMode implements IFlowMode {}
    static class InOutFlowMode implements IFlowMode {}
    static class OutFlowMode implements IFlowMode {}

    interface IMechMode {}
    static class CopyMechMode implements IMechMode {}
    static class RefMechMode implements IMechMode {}

    interface IChangeMode {}
    static class VarChangeMode implements IChangeMode {}
    static class ConstChangeMode implements IChangeMode {}

    interface ITypedIdentifier {}
    static class TypedIdentifier implements ITypedIdentifier {
        String name;
        IType type;
        public TypedIdentifier (String name, IType type) {
            this.name = name;
            this.type = type;
        }
    }

    interface IProgramParameter {}
    static class ProgramParameter implements IProgramParameter {
        IFlowMode flowMode;
        IChangeMode changeMode;
        ITypedIdentifier typedIdentifier;

        public ProgramParameter(IFlowMode flowMode, IChangeMode changeMode, ITypedIdentifier typedIdentifier) {
            this.flowMode = flowMode;
            this.changeMode = changeMode;
            this.typedIdentifier = typedIdentifier;
        }
    }

    interface IParameter {}
    static class Parameter implements IParameter {
        IFlowMode flowMode;
        IMechMode mechMode;
        IChangeMode changeMode;
        ITypedIdentifier typedIdentifier;

        public Parameter(IFlowMode flowMode, IMechMode mechMode, IChangeMode changeMode, ITypedIdentifier typedIdentifier) {
            this.flowMode = flowMode;
            this.mechMode = mechMode;
            this.changeMode = changeMode;
            this.typedIdentifier = typedIdentifier;
        }
    }

    interface IGlobalImport {}
    static class GlobalImport implements IGlobalImport {
        IFlowMode flowMode;
        IChangeMode changeMode;
        String name;

        public GlobalImport(IFlowMode flowMode, IChangeMode changeMode, String name) {
            this.flowMode = flowMode;
            this.changeMode = changeMode;
            this.name = name;
        }
    }

    interface IDeclaration {}
    interface IStorageDeclaration extends IDeclaration {}
    static class StorageDeclaration implements IStorageDeclaration {
        IChangeMode changeMode;
        ITypedIdentifier typedIdentifier;

        public StorageDeclaration(IChangeMode changeMode, ITypedIdentifier typedIdentifier) {
            this.changeMode = changeMode;
            this.typedIdentifier = typedIdentifier;
        }
    }
    interface IFunctionDeclaration extends IDeclaration {}
    static class FunctionDeclaration implements IFunctionDeclaration {
        String name;
        List<IParameter> parameterList;
        IStorageDeclaration storageDeclaration;
        List<IGlobalImport> globalImports;
        List<IStorageDeclaration> localImports;
        List<ICommand> commands;

        public FunctionDeclaration(String name, List<IParameter> parameterList, IStorageDeclaration storageDeclaration, List<IGlobalImport> globalImports, List<IStorageDeclaration> localImports, List<ICommand> commands) {
            this.name = name;
            this.parameterList = parameterList;
            this.storageDeclaration = storageDeclaration;
            this.globalImports = globalImports;
            this.localImports = localImports;
            this.commands = commands;
        }
    }
    interface IProcedureDeclaration extends IDeclaration {}
    static class ProcedureDeclaration implements IProcedureDeclaration {
        String name;
        List<IParameter> parameters;
        List<IGlobalImport> globalImports;
        List<IStorageDeclaration> localImports;
        List<ICommand> commands;

        public ProcedureDeclaration(String name, List<IParameter> parameters, List<IGlobalImport> globalImports, List<IStorageDeclaration> localImports, List<ICommand> commands) {
            this.name = name;
            this.parameters = parameters;
            this.globalImports = globalImports;
            this.localImports = localImports;
            this.commands = commands;
        }
    }
    interface IRecordShapeDeclaration extends IDeclaration {}
    static class RecordShapeDeclaration implements IRecordShapeDeclaration {
        String name;
        List<ITypedIdentifier> fields;

        public RecordShapeDeclaration(String name, List<ITypedIdentifier> fields) {
            this.name = name;
            this.fields = fields;
        }
    }

    interface IMonadicOperator {}
    static class NotMonadicOperator implements IMonadicOperator {}
    static class PosMonadicOperator implements IMonadicOperator {}

    interface IExpression {}

    interface ICommand {}
    interface ISkipCommand extends ICommand {}
    static class SkipCommand implements ISkipCommand {}
    interface IAssignmentCommand extends ICommand {}
    static class AssignmentCommand implements IAssignmentCommand {
        IExpression l;
        IExpression r;

        public AssignmentCommand(IExpression l, IExpression r) {
            this.l = l;
            this.r = r;
        }
    }
    interface IIfCommand extends ICommand {}
    static class IfCommand implements IIfCommand {
        IExpression condition;
        List<ICommand> commands;
        List<ICommand> elseCommands;

        public IfCommand(IExpression condition, List<ICommand> commands, List<ICommand> elseCommands) {
            this.condition = condition;
            this.commands = commands;
            this.elseCommands = elseCommands;
        }
    }
    interface IWhileCommand extends ICommand {}
    static class WhileCommand implements IWhileCommand {
        IExpression condition;
        List<ICommand> commands;

        public WhileCommand(IExpression condition, List<ICommand> commands) {
            this.condition = condition;
            this.commands = commands;
        }
    }
    interface ICallCommand extends ICommand {}
    static class CallCommand implements ICallCommand {
        String name;
        List<IExpression> expressions;
        List<String> globalInits;

        public CallCommand(String name, List<IExpression> expressions, List<String> globalInits) {
            this.name = name;
            this.expressions = expressions;
            this.globalInits = globalInits;
        }
    }
    interface IDebugInCommand extends ICommand {}
    static class DebugInCommand implements IDebugInCommand {
        IExpression expression;

        public DebugInCommand(IExpression expression) {
            this.expression = expression;
        }
    }
    interface IDebugOutCommand extends ICommand {}
    static class DebugOutCommand implements IDebugOutCommand {
        IExpression expression;

        public DebugOutCommand(IExpression expression) {
            this.expression = expression;
        }
    }

    interface IProgram {}
    static class Program implements IProgram {
        String name;
        List<IProgramParameter> programParameters;
        List<IDeclaration> globalDeclarations;
        List<ICommand> commands;

        public Program(String name, List<IProgramParameter> programParameters, List<IDeclaration> globalDeclarations, List<ICommand> commands) {
            this.name = name;
            this.programParameters = programParameters;
            this.globalDeclarations = globalDeclarations;
            this.commands = commands;
        }
    }
}
/*
    Recursive to List template
            List<AbsSyn.I_Some_Type> list = new LinkedList<>();
            list.add(someType.toAbsSyn());
            List<AbsSyn.I_Some_Type> next = cps_Some_Type.toAbsSyn();
            list.addAll(next);
            return list;
 */
