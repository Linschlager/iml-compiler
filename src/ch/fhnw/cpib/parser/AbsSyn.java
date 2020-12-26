package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.lexer.tokens.*;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray.CodeTooSmallError;

import java.util.List;

public class AbsSyn {

    interface IAbsSynNode {

        /**
         * Creates the code in the VM and returns the new location.
         *
         * @throws CodeTooSmallError
         *           Thrown when the code segment of the VM is full.
         */
        default int code(ICodeArray codeArray, int location) throws CodeTooSmallError {
            throw new RuntimeException("Not yet implemented");
        }
    }
    public interface IType extends IAbsSynNode {}
    public static class TraditionalType implements IType {
        public Type type;
        public TraditionalType(Type type) {
            this.type = type;
        }
    }
    public static class RecordType implements IType {
        public String name;

        public RecordType(String name) {
            this.name = name;
        }
    }

    public interface IFlowMode extends IAbsSynNode {}
    public static class InFlowMode implements IFlowMode {}
    public static class InOutFlowMode implements IFlowMode {}
    public static class OutFlowMode implements IFlowMode {}

    public interface IMechMode extends IAbsSynNode {}
    public static class CopyMechMode implements IMechMode {}
    public static class RefMechMode implements IMechMode {}

    public interface IChangeMode extends IAbsSynNode {}
    public static class VarChangeMode implements IChangeMode {}
    public static class ConstChangeMode implements IChangeMode {}

    public interface ITypedIdentifier extends IAbsSynNode {}
    public static class TypedIdentifier implements ITypedIdentifier {
        public String name;
        public IType type;
        public TypedIdentifier (String name, IType type) {
            this.name = name;
            this.type = type;
        }
    }

    public interface IProgramParameter extends IAbsSynNode {}
    public static class ProgramParameter implements IProgramParameter {
        public IFlowMode flowMode;
        public IChangeMode changeMode;
        public ITypedIdentifier typedIdentifier;

        public ProgramParameter(IFlowMode flowMode, IChangeMode changeMode, ITypedIdentifier typedIdentifier) {
            this.flowMode = flowMode;
            this.changeMode = changeMode;
            this.typedIdentifier = typedIdentifier;
        }
    }

    public interface IParameter extends IAbsSynNode {}
    public static class Parameter implements IParameter {
        public IFlowMode flowMode;
        public IMechMode mechMode;
        public IChangeMode changeMode;
        public ITypedIdentifier typedIdentifier;

        public Parameter(IFlowMode flowMode, IMechMode mechMode, IChangeMode changeMode, ITypedIdentifier typedIdentifier) {
            this.flowMode = flowMode;
            this.mechMode = mechMode;
            this.changeMode = changeMode;
            this.typedIdentifier = typedIdentifier;
        }
    }

    public interface IGlobalImport extends IAbsSynNode {}
    public static class GlobalImport implements IGlobalImport {
        public IFlowMode flowMode;
        public IChangeMode changeMode;
        public String name;

        public GlobalImport(IFlowMode flowMode, IChangeMode changeMode, String name) {
            this.flowMode = flowMode;
            this.changeMode = changeMode;
            this.name = name;
        }
    }

    public interface IDeclaration extends IAbsSynNode {}
    public interface IStorageDeclaration extends IDeclaration {}
    public static class StorageDeclaration implements IStorageDeclaration {
        public IChangeMode changeMode;
        public ITypedIdentifier typedIdentifier;

        public StorageDeclaration(IChangeMode changeMode, ITypedIdentifier typedIdentifier) {
            this.changeMode = changeMode;
            this.typedIdentifier = typedIdentifier;
        }
    }
    public interface IFunctionDeclaration extends IDeclaration {}
    public static class FunctionDeclaration implements IFunctionDeclaration {
        public String name;
        public List<IParameter> parameterList;
        public IStorageDeclaration storageDeclaration;
        public List<IGlobalImport> globalImports;
        public List<IStorageDeclaration> localImports;
        public List<ICommand> commands;

        public FunctionDeclaration(String name, List<IParameter> parameterList, IStorageDeclaration storageDeclaration, List<IGlobalImport> globalImports, List<IStorageDeclaration> localImports, List<ICommand> commands) {
            this.name = name;
            this.parameterList = parameterList;
            this.storageDeclaration = storageDeclaration;
            this.globalImports = globalImports;
            this.localImports = localImports;
            this.commands = commands;
        }
    }
    public interface IProcedureDeclaration extends IDeclaration {}
    public static class ProcedureDeclaration implements IProcedureDeclaration {
        public String name;
        public List<IParameter> parameters;
        public List<IGlobalImport> globalImports;
        public List<IStorageDeclaration> localImports;
        public List<ICommand> commands;

        public ProcedureDeclaration(String name, List<IParameter> parameters, List<IGlobalImport> globalImports, List<IStorageDeclaration> localImports, List<ICommand> commands) {
            this.name = name;
            this.parameters = parameters;
            this.globalImports = globalImports;
            this.localImports = localImports;
            this.commands = commands;
        }
    }
    public interface IRecordShapeDeclaration extends IDeclaration {}
    public static class RecordShapeDeclaration implements IRecordShapeDeclaration {
        public String name;
        public List<ITypedIdentifier> fields;

        public RecordShapeDeclaration(String name, List<ITypedIdentifier> fields) {
            this.name = name;
            this.fields = fields;
        }
    }

    public interface IMonadicOperator extends IAbsSynNode {}
    public static class NotMonadicOperator implements IMonadicOperator {}
    public static class PosMonadicOperator implements IMonadicOperator {}

    public interface IExpression extends IAbsSynNode {}
    public interface ILiteralExpression extends IExpression {}
    public interface IBoolLiteralExpression extends ILiteralExpression {}
    public static class BoolLiteralExpression implements IBoolLiteralExpression {
        public boolean value;

        public BoolLiteralExpression(boolean value) {
            this.value = value;
        }
    }
    public interface IIntLiteralExpression extends ILiteralExpression {}
    public static class IntLiteralExpression implements IIntLiteralExpression {
        public String value; // To allow any length of number

        public IntLiteralExpression(String value) {
            this.value = value;
        }
    }
    public interface IStoreExpression extends IExpression {}
    public static class StoreExpression implements IStoreExpression {
        public String name;
        public boolean init;

        public StoreExpression(String name, boolean init) {
            this.name = name;
            this.init = init;
        }
    }
    public interface IFunctionCallExpression extends IExpression {}
    public static class FunctionCallExpression implements IFunctionCallExpression {
        public String name;
        public List<IExpression> arguments;

        public FunctionCallExpression(String name, List<IExpression> arguments) {
            this.name = name;
            this.arguments = arguments;
        }
    }
    public interface IMonadicExpression extends IExpression {}
    public static class MonadicExpression implements IMonadicExpression {
        public IMonadicOperator operator;
        public IExpression expression;

        public MonadicExpression(IMonadicOperator operator, IExpression expression) {
            this.operator = operator;
            this.expression = expression;
        }
    }
    public interface IDyadicExpression extends IExpression {}
    public interface IMultiplicationDyadicExpression extends IDyadicExpression {}
    public static class MultiplicationDyadicExpression implements IMultiplicationDyadicExpression {
        public MultOpr.Attr operator;
        public IExpression l;
        public IExpression r;

        public MultiplicationDyadicExpression(MultOpr.Attr operator, IExpression l, IExpression r) {
            this.operator = operator;
            this.l = l;
            this.r = r;
        }
    }
    public interface IAdditionDyadicExpression extends IDyadicExpression {}
    public static class AdditionDyadicExpression implements IAdditionDyadicExpression {
        public AddOpr.Attr operator;
        public IExpression l;
        public IExpression r;

        public AdditionDyadicExpression(AddOpr.Attr operator, IExpression l, IExpression r) {
            this.operator = operator;
            this.l = l;
            this.r = r;
        }
    }
    public interface IRelativeDyadicExpression extends IDyadicExpression {}
    public static class RelativeDyadicExpression implements IRelativeDyadicExpression {
        public RelOpr.Attr operator;
        public IExpression l;
        public IExpression r;

        public RelativeDyadicExpression(RelOpr.Attr operator, IExpression l, IExpression r) {
            this.operator = operator;
            this.l = l;
            this.r = r;
        }
    }
    public interface IBoolDyadicExpression extends IDyadicExpression {}
    public static class BoolDyadicExpression implements IBoolDyadicExpression {
        public BoolOpr.Attr operator;
        public IExpression l;
        public IExpression r;

        public BoolDyadicExpression(BoolOpr.Attr operator, IExpression l, IExpression r) {
            this.operator = operator;
            this.l = l;
            this.r = r;
        }
    }

    public interface IRecordAccessExpression extends IExpression {}
    public static class RecordAccessExpression implements IRecordAccessExpression {
        public String recordName;
        public List<String> fieldNames;

        public RecordAccessExpression(String recordName, List<String> fieldNames) {
            this.recordName = recordName;
            this.fieldNames = fieldNames;
        }
    }

    public interface ICommand extends IAbsSynNode {}
    public interface ISkipCommand extends ICommand {}
    public static class SkipCommand implements ISkipCommand {}
    public interface IAssignmentCommand extends ICommand {}
    public static class AssignmentCommand implements IAssignmentCommand {
        public IExpression l;
        public IExpression r;

        public AssignmentCommand(IExpression l, IExpression r) {
            this.l = l;
            this.r = r;
        }
    }
    public interface IIfCommand extends ICommand {}
    public static class IfCommand implements IIfCommand {
        public IExpression condition;
        public List<ICommand> commands;
        public List<ICommand> elseCommands;

        public IfCommand(IExpression condition, List<ICommand> commands, List<ICommand> elseCommands) {
            this.condition = condition;
            this.commands = commands;
            this.elseCommands = elseCommands;
        }
    }
    public interface IWhileCommand extends ICommand {}
    public static class WhileCommand implements IWhileCommand {
        public IExpression condition;
        public List<ICommand> commands;

        public WhileCommand(IExpression condition, List<ICommand> commands) {
            this.condition = condition;
            this.commands = commands;
        }
    }
    public interface ICallCommand extends ICommand {}
    public static class CallCommand implements ICallCommand {
        public String name;
        public List<IExpression> expressions;
        public List<String> globalInits;

        public CallCommand(String name, List<IExpression> expressions, List<String> globalInits) {
            this.name = name;
            this.expressions = expressions;
            this.globalInits = globalInits;
        }
    }
    public interface IDebugInCommand extends ICommand {}
    public static class DebugInCommand implements IDebugInCommand {
        public IExpression expression;

        public DebugInCommand(IExpression expression) {
            this.expression = expression;
        }
    }
    public interface IDebugOutCommand extends ICommand {}
    public static class DebugOutCommand implements IDebugOutCommand {
        public IExpression expression;

        public DebugOutCommand(IExpression expression) {
            this.expression = expression;
        }
    }

    public interface IProgram extends IAbsSynNode {}
    public static class Program implements IProgram {
        public String name;
        public List<IProgramParameter> programParameters;
        public List<IDeclaration> globalDeclarations;
        public List<ICommand> commands;

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
