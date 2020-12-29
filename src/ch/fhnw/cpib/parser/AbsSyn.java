package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.checks.AccessMode;
import ch.fhnw.cpib.checks.Scope;
import ch.fhnw.cpib.checks.Types;
import ch.fhnw.cpib.exceptions.ContextError;
import ch.fhnw.cpib.exceptions.TypeError;
import ch.fhnw.cpib.lexer.tokens.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AbsSyn {
        public static class ProcedureArgument {
        public Types type;
        public AccessMode accessMode;
        public Scope scope;

        public ProcedureArgument(Types type, AccessMode accessMode, Scope scope) {
            this.type = type;
            this.accessMode = accessMode;
            this.scope = scope;
        }
    }
    public static class ProcedureSignature {
        public List<ProcedureArgument> arguments;

        public ProcedureSignature(List<ProcedureArgument> arguments) {
            this.arguments = arguments;
        }
    }
    public static class FunctionSignature extends ProcedureSignature {
        public Types returnType;

        public FunctionSignature(List<ProcedureArgument> args, Types returnType) {
            super(args);
            this.returnType = returnType;
        }
    }
    // Basically TypedIdentifier
    public static class RecordField {
        public String name;
        public Types type;

        public RecordField(String name, Types type) {
            this.name = name;
            this.type = type;
        }
    }
    public static class RecordSignature {
        public List<RecordField> fields;

        public RecordSignature(List<RecordField> fields) {
            this.fields = fields;
        }
    }


    private static final Map<String, ProcedureSignature> procedureMap;
    private static final Map<String, RecordSignature> recordMap;

    static {
        procedureMap = new HashMap<>();
        recordMap = new HashMap<>();
    }

    public interface IType {
        public Types convertType();
    }

    public static class TraditionalType implements IType {
        public Type type;

        public TraditionalType(Type type) {
            this.type = type;
        }

        @Override
        public Types convertType() {
            return Types.allTypes.get(type.attr.toString());
        }
    }

    public static class RecordType implements IType {
        public String name;

        public RecordType(String name) {
            this.name = name;
        }

        @Override
        public Types convertType() {
            return Types.allTypes.get(name); // TODO
        }
    }

    public interface IFlowMode {
    }

    public static class InFlowMode implements IFlowMode {
    }

    public static class InOutFlowMode implements IFlowMode {
    }

    public static class OutFlowMode implements IFlowMode {
    }

    public interface IMechMode {
    }

    public static class CopyMechMode implements IMechMode {
    }

    public static class RefMechMode implements IMechMode {
    }

    public interface IChangeMode {
    }

    public static class VarChangeMode implements IChangeMode {
    }

    public static class ConstChangeMode implements IChangeMode {
    }

    public interface ITypedIdentifier {
        public String getName();

        public Types getType();
    }

    public static class TypedIdentifier implements ITypedIdentifier {
        public String name;
        public IType type;

        public TypedIdentifier(String name, IType type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Types getType() {
            return type.convertType();
        }
    }

    public interface IProgramParameter {
        public IProgramParameter check();
    }

    public static class ProgramParameter implements IProgramParameter {
        public IFlowMode flowMode;
        public IChangeMode changeMode;
        public ITypedIdentifier typedIdentifier;

        public ProgramParameter(IFlowMode flowMode, IChangeMode changeMode, ITypedIdentifier typedIdentifier) {
            this.flowMode = flowMode;
            this.changeMode = changeMode;
            this.typedIdentifier = typedIdentifier;
        }

        @Override
        public IProgramParameter check() {
            // TODO
            return this;
        }
    }

    public interface IParameter {
        public IParameter check(Map<String, Types> localScope) throws ContextError;
    }

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

        @Override
        public IParameter check(Map<String, Types> localScope) throws ContextError {
            if (localScope.containsKey(typedIdentifier.getName())) {
                // throw new ContextError(String.format("'%s' has already been declared", typedIdentifier.getName()));
            }

            return this;
        }
    }

    public interface IGlobalImport {
        public IGlobalImport check(Map<String, Types> localScope) throws ContextError;
    }

    public static class GlobalImport implements IGlobalImport {
        public IFlowMode flowMode;
        public IChangeMode changeMode;
        public String name;

        public GlobalImport(IFlowMode flowMode, IChangeMode changeMode, String name) {
            this.flowMode = flowMode;
            this.changeMode = changeMode;
            this.name = name;
        }

        @Override
        public IGlobalImport check(Map<String, Types> localScope) throws ContextError {
            if (localScope.containsKey(name))
                throw new ContextError(String.format("'%s' has already been declared", name));

            return this;
        }
    }

    public interface IDeclaration {
        public IDeclaration check(Map<String, Types> localScope) throws TypeError, ContextError;
    }

    public interface IStorageDeclaration extends IDeclaration {
    }

    public static class StorageDeclaration implements IStorageDeclaration {
        public IChangeMode changeMode;
        public ITypedIdentifier typedIdentifier;

        public StorageDeclaration(IChangeMode changeMode, ITypedIdentifier typedIdentifier) {
            this.changeMode = changeMode;
            this.typedIdentifier = typedIdentifier;
        }

        @Deprecated(since = "Don't use this")
        public IDeclaration check() throws ContextError, TypeError {
            return check(new HashMap<>());
        }

        @Override
        public IStorageDeclaration check(Map<String, Types> localScope) throws TypeError, ContextError {
            if (localScope.containsKey(typedIdentifier.getName())) {
                throw new ContextError(String.format("%s is already declared locally", typedIdentifier.getName()));
            }

            return this;
        }
    }

    public interface IFunctionDeclaration extends IDeclaration {
    }

    public static class FunctionDeclaration implements IFunctionDeclaration {
        public String name;
        public List<IParameter> parameterList;
        public IStorageDeclaration returnValue;
        public List<IGlobalImport> globalImports;
        public List<IStorageDeclaration> localImports;
        public List<ICommand> commands;

        public FunctionDeclaration(String name, List<IParameter> parameterList, IStorageDeclaration returnValue, List<IGlobalImport> globalImports, List<IStorageDeclaration> localImports, List<ICommand> commands) {
            this.name = name;
            this.parameterList = parameterList;
            this.returnValue = returnValue;
            this.globalImports = globalImports;
            this.localImports = localImports;
            this.commands = commands;
        }

        @Override
        public IDeclaration check(Map<String, Types> localScope) throws TypeError, ContextError {
            if (procedureMap.containsKey(name)) {
                throw new ContextError("Function " + name + " is already declared");
            }
            if (recordMap.containsKey(name)) {
                throw new ContextError("Function " + name + " cannot be declared, there is a record of that name.");
            }

            // Register function before checking commands to allow for recursion
            List<ProcedureArgument> args = new LinkedList<>();
            for (IParameter iParameter : parameterList) {
                Parameter param = (Parameter) iParameter;

                if (!(param.flowMode instanceof InFlowMode))
                    throw new ContextError("FlowMode in Functions can only be In. Found " + param.flowMode);

                Scope s = Scope.LOCAL;
                AccessMode m;
                // TODO Read more about this check
                if (param.mechMode instanceof RefMechMode) {
                    m = AccessMode.INDIRECT;
                } else {
                    m = AccessMode.DIRECT;
                }
                Types t = param.typedIdentifier.getType();

                args.add(new ProcedureArgument(t, m, s));
            }
            returnValue = (StorageDeclaration) returnValue.check(localScope);
            var returns = ((StorageDeclaration)returnValue).typedIdentifier;
            FunctionSignature signature = new FunctionSignature(args, returns.getType());
            // Register function signature
            procedureMap.put(name, signature);

            Map<String, Types> symbolTable = new HashMap<>(localScope);
            // TODO global imports

            symbolTable.put(returns.getName(), returns.getType());

            List<IGlobalImport> newGlobImps = new LinkedList<>();
            for (IGlobalImport gi : globalImports) {
                newGlobImps.add(gi.check(symbolTable));
                var g = (GlobalImport)gi;
                if (symbolTable.get(g.name) == null) {
                    throw new ContextError(String.format("Couldn't find '%s'", g.name));
                }
            }
            globalImports = newGlobImps;

            List<ICommand> newCmds = new LinkedList<>();
            for (ICommand c : commands) {
                newCmds.add(c.check(symbolTable));
            }
            commands = newCmds;

            return this;
        }
    }

    public interface IProcedureDeclaration extends IDeclaration {
    }

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

        @Override
        public IDeclaration check(Map<String, Types> localScope) throws TypeError, ContextError {
            if (procedureMap.containsKey(name)) {
                throw new ContextError("Procedure " + name + " is already declared");
            }
            if (recordMap.containsKey(name)) {
                throw new ContextError("Procedure " + name + " cannot be declared, there is a record of that name.");
            }

            // Add procedure to procedureMap to allow for recursion
            List<ProcedureArgument> args = new LinkedList<>();
            for (IParameter iParameter : parameters) {
                var p = (Parameter) iParameter;
                // TODO AccessMode and Scope
                var pa = new ProcedureArgument(p.typedIdentifier.getType(), AccessMode.DIRECT, Scope.LOCAL);
                args.add(pa);
            }
            procedureMap.put(name, new ProcedureSignature(args));

            // Construct symbolTable
            Map<String, Types> symbolTable = new HashMap<>(localScope);

            for (IParameter iParameter : parameters) {
                var p = (Parameter) iParameter.check(symbolTable);
                symbolTable.put(p.typedIdentifier.getName(), p.typedIdentifier.getType());
            }
            for (IGlobalImport iGlobalImport : globalImports) {
                var gi = (GlobalImport) iGlobalImport.check(symbolTable);
                symbolTable.put(gi.name, localScope.get(gi.name));
            }
            for (IStorageDeclaration localImport : localImports) {
                var li = (StorageDeclaration) localImport.check(symbolTable);
                symbolTable.put(li.typedIdentifier.getName(), li.typedIdentifier.getType());
            }

            List<ICommand> newCmds = new LinkedList<>();
            for (ICommand c : commands) newCmds.add(c.check(symbolTable));
            commands = newCmds;

            return this;
        }
    }

    public interface IRecordShapeDeclaration extends IDeclaration {
    }

    public static class RecordShapeDeclaration implements IRecordShapeDeclaration {
        public String name;
        public List<ITypedIdentifier> fields;

        public RecordShapeDeclaration(String name, List<ITypedIdentifier> fields) {
            this.name = name;
            this.fields = fields;
        }

        @Override
        public IDeclaration check(Map<String, Types> localScope) throws TypeError, ContextError {
            if (recordMap.containsKey(name)) {
                throw new ContextError("Record " + name + " is already declared");
            }
            if (procedureMap.containsKey(name)) {
                throw new ContextError("Record " + name + " cannot be declared, there is already a procedure of that name");
            }

            List<RecordField> f = fields.stream().map(ti ->
                    new RecordField(ti.getName(), ti.getType())).collect(Collectors.toList()
            );
            RecordSignature recordSignature = new RecordSignature(f);
            recordMap.put(name, recordSignature);

            var recordType = new Types("Record", name);
            Types.allTypes.put(name, recordType);

            return this;
        }
    }

    public interface IMonadicOperator {
    }

    public static class NotMonadicOperator implements IMonadicOperator {
    }

    public static class PosMonadicOperator implements IMonadicOperator {
    }

    public interface IExpression {
        public IExpression check(Map<String, Types> localScope) throws TypeError, ContextError;

        public Types getType(Map<String, Types> localScope) throws TypeError, ContextError;

        public boolean isValidLeft();

        public boolean isValidRight();
    }

    public interface ILiteralExpression extends IExpression {
    }

    public interface IBoolLiteralExpression extends ILiteralExpression {
    }

    public static class BoolLiteralExpression implements IBoolLiteralExpression {
        public boolean value;

        public BoolLiteralExpression(boolean value) {
            this.value = value;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) {
            return this;
        }

        @Override
        public Types getType(Map<String, Types> localScope) {
            return Types.BOOLEAN;
        }

        @Override
        public boolean isValidLeft() {
            return false;
        }

        @Override
        public boolean isValidRight() {
            return true;
        }
    }

    public interface IIntLiteralExpression extends ILiteralExpression {
    }

    public static class IntLiteralExpression implements IIntLiteralExpression {
        public String value; // To allow any length of number

        public IntLiteralExpression(String value) {
            this.value = value;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) {
            return this;
        }

        @Override
        public Types getType(Map<String, Types> localScope) {
            return Types.INTEGER;
        }

        @Override
        public boolean isValidLeft() {
            return false;
        }

        @Override
        public boolean isValidRight() {
            return true;
        }
    }

    public interface IStoreExpression extends IExpression {
    }

    public static class StoreExpression implements IStoreExpression {
        public String name;
        public boolean init;

        public StoreExpression(String name, boolean init) {
            this.name = name;
            this.init = init;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) {
            // TODO Check scopes
            return this;
        }

        @Override
        public Types getType(Map<String, Types> localScope) throws ContextError {
            // TODO look up in global variable map
            var type = localScope.get(name);
            if (type == null)
                throw new ContextError(String.format("Couldn't find %s in %s", name, localScope.toString()));
            return type;
        }

        @Override
        public boolean isValidLeft() {
            return true;
        }

        @Override
        public boolean isValidRight() {
            return !init; // TODO. Current thinking; R-Assignments cannot include init
        }
    }

    public interface IFunctionCallExpression extends IExpression {

        @Override
        default boolean isValidLeft() {
            return false;
        }

        @Override
        default boolean isValidRight() {
            return true;
        }
    }

    public static class FunctionCallExpression implements IFunctionCallExpression {
        public String name;
        public List<IExpression> arguments;

        public FunctionCallExpression(String name, List<IExpression> arguments) {
            this.name = name;
            this.arguments = arguments;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) throws TypeError, ContextError {

            if (!procedureMap.containsKey(name) && !recordMap.containsKey(name))
                throw new ContextError(String.format("Unknown function %s", name));

            List<IExpression> newArgs = new LinkedList<>();
            for (IExpression e : arguments) newArgs.add(e.check(localScope));
            arguments = newArgs;

            if (recordMap.containsKey(name)) {
                return new RecordCallExpression(name, arguments).check(localScope);
            }

            // TODO checks
            var desiredArguments = procedureMap.get(name).arguments;
            for (int i = 0; i < arguments.size(); i++) {
                var actualType = arguments.get(i).getType(localScope);
                var desiredType = desiredArguments.get(i).type;
                if (desiredType != actualType)
                    throw new TypeError("FunctionCallExpression", actualType.toString(), desiredType.toString());
            }

            return this;
        }

        @Override
        public Types getType(Map<String, Types> localScope) throws TypeError, ContextError {
            var signature = procedureMap.get(name);
            if (signature == null) {
                throw new ContextError(String.format("Couldn't find function '%s'", name));
            }
            if (signature instanceof FunctionSignature) {
                return ((FunctionSignature) signature).returnType;
            }
            throw new TypeError("FunctionCallExpression", "Procedure", "Function");
        }
    }

    // Only returned in static analysis of FunctionCallExpression
    public static class RecordCallExpression implements IFunctionCallExpression {
        public String name;
        public List<IExpression> arguments;

        public RecordCallExpression(String name, List<IExpression> arguments) {
            this.name = name;
            this.arguments = arguments;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) throws TypeError {
            // TODO check args
            return this;
        }

        @Override
        public Types getType(Map<String, Types> localScope) {
            return Types.allTypes.get(name);
        }
    }

    public interface IMonadicExpression extends IExpression {
        @Override
        default boolean isValidLeft() {
            return false;
        }

        @Override
        default boolean isValidRight() {
            return true;
        }
    }

    public static class MonadicExpression implements IMonadicExpression {
        public IMonadicOperator operator;
        public IExpression expression;

        public MonadicExpression(IMonadicOperator operator, IExpression expression) {
            this.operator = operator;
            this.expression = expression;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) throws TypeError, ContextError {
            if (operator instanceof NotMonadicOperator && expression.getType(localScope) != Types.BOOLEAN) {
                throw new TypeError("PosMonadicOperator", expression.getType(localScope).toString(), Types.BOOLEAN.toString());

            }
            if (operator instanceof PosMonadicOperator && expression.getType(localScope) != Types.INTEGER) {
                throw new TypeError("PosMonadicOperator", expression.getType(localScope).toString(), Types.INTEGER.toString());
            }
            return expression.check(localScope);
        }

        @Override
        public Types getType(Map<String, Types> localScope) throws TypeError, ContextError {
            return expression.getType(localScope);
        }
    }

    public interface IDyadicExpression extends IExpression {
        @Override
        default boolean isValidLeft() {
            return false;
        }

        @Override
        default boolean isValidRight() {
            return true;
        }
    }

    public interface IMultiplicationDyadicExpression extends IDyadicExpression {
    }

    public static class MultiplicationDyadicExpression implements IMultiplicationDyadicExpression {
        public MultOpr.Attr operator;
        public IExpression l;
        public IExpression r;

        public MultiplicationDyadicExpression(MultOpr.Attr operator, IExpression l, IExpression r) {
            this.operator = operator;
            this.l = l;
            this.r = r;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) throws TypeError, ContextError {
            if (l.getType(localScope) != Types.INTEGER) {
                throw new TypeError("MultiplicationDyadicExpression", l.getType(localScope).toString(), Types.INTEGER.toString());
            }
            if (r.getType(localScope) != Types.INTEGER) {
                throw new TypeError("MultiplicationDyadicExpression", r.getType(localScope).toString(), Types.INTEGER.toString());
            }
            l = l.check(localScope);
            r = r.check(localScope);
            return this;
        }

        @Override
        public Types getType(Map<String, Types> localScope) {
            return Types.INTEGER;
        }
    }

    public interface IAdditionDyadicExpression extends IDyadicExpression {
    }

    public static class AdditionDyadicExpression implements IAdditionDyadicExpression {
        public AddOpr.Attr operator;
        public IExpression l;
        public IExpression r;

        public AdditionDyadicExpression(AddOpr.Attr operator, IExpression l, IExpression r) {
            this.operator = operator;
            this.l = l;
            this.r = r;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) throws TypeError, ContextError {
            if (l.getType(localScope) != Types.INTEGER) {
                throw new TypeError("AdditionDyadicExpression", l.getType(localScope).toString(), Types.INTEGER.toString());
            }
            if (r.getType(localScope) != Types.INTEGER) {
                throw new TypeError("AdditionDyadicExpression", r.getType(localScope).toString(), Types.INTEGER.toString());
            }
            l = l.check(localScope);
            r = r.check(localScope);
            return this;
        }

        @Override
        public Types getType(Map<String, Types> localScope) {
            return Types.INTEGER;
        }
    }

    public interface IRelativeDyadicExpression extends IDyadicExpression {
    }

    public static class RelativeDyadicExpression implements IRelativeDyadicExpression {
        public RelOpr.Attr operator;
        public IExpression l;
        public IExpression r;

        public RelativeDyadicExpression(RelOpr.Attr operator, IExpression l, IExpression r) {
            this.operator = operator;
            this.l = l;
            this.r = r;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) throws TypeError, ContextError {
            switch (operator) {
                case EQ:
                case NE:
                    if (l.getType(localScope) == Types.BOOLEAN && r.getType(localScope) != Types.BOOLEAN) {
                        throw new TypeError("RelativeDyadicExpression", r.getType(localScope).toString(), Types.BOOLEAN.toString());
                    }
                case GE:
                case GT:
                case LE:
                case LT:
                    if (l.getType(localScope) != Types.INTEGER) {
                        throw new TypeError("RelativeDyadicExpression", l.getType(localScope).toString(), Types.INTEGER.toString());
                    }
                    if (r.getType(localScope) != Types.INTEGER) {
                        throw new TypeError("RelativeDyadicExpression", r.getType(localScope).toString(), Types.INTEGER.toString());
                    }
                    l = l.check(localScope);
                    r = r.check(localScope);
            }
            return this;
        }

        @Override
        public Types getType(Map<String, Types> localScope) {
            return Types.BOOLEAN;
        }
    }

    public interface IBoolDyadicExpression extends IDyadicExpression {
    }

    public static class BoolDyadicExpression implements IBoolDyadicExpression {
        public BoolOpr.Attr operator;
        public IExpression l;
        public IExpression r;

        public BoolDyadicExpression(BoolOpr.Attr operator, IExpression l, IExpression r) {
            this.operator = operator;
            this.l = l;
            this.r = r;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) throws TypeError, ContextError {
            if (l.getType(localScope) != Types.BOOLEAN) {
                throw new TypeError("RelativeDyadicExpression", l.getType(localScope).toString(), Types.BOOLEAN.toString());
            }
            if (r.getType(localScope) != Types.BOOLEAN) {
                throw new TypeError("RelativeDyadicExpression", r.getType(localScope).toString(), Types.BOOLEAN.toString());
            }
            l = l.check(localScope);
            r = r.check(localScope);
            return this;
        }

        @Override
        public Types getType(Map<String, Types> localScope) {
            return Types.BOOLEAN;
        }
    }

    public interface IRecordAccessExpression extends IExpression {
        @Override
        default boolean isValidLeft() {
            return true;
        }

        @Override
        default boolean isValidRight() {
            return true;
        }
    }

    public static class RecordAccessExpression implements IRecordAccessExpression {
        public String variableName;
        public List<String> fieldNames;

        public RecordAccessExpression(String variableName, List<String> fieldNames) {
            this.variableName = variableName;
            this.fieldNames = fieldNames;
        }

        @Override
        public IExpression check(Map<String, Types> localScope) throws TypeError, ContextError {
            getType(localScope);

            return this;
        }

        @Override
        public Types getType(Map<String, Types> localScope) throws ContextError, TypeError {
            var type = localScope.get(variableName);
            if (type == null) {
                throw new ContextError("Couldn't find record " + variableName + " in " + localScope.toString());
            }

            if (!type.getType().equals("Record")) {
                throw new TypeError("RecordAccessExpression", type.getType(), "Record");
            }
            var recordName = type.getRecordName();
            var record = recordMap.get(recordName);
            if (record == null) {
                throw new ContextError("Couldn't find record " + recordName + " in " + recordMap.toString());
            }
            Types finalType = null;
            for (String field : fieldNames) {
                var fieldOpt = record.fields.stream().filter(f -> f.name.equals(field)).findFirst();
                if (fieldOpt.isEmpty()) {
                    throw new ContextError(String.format("Couldn't find field %s on %s", field, recordName));
                }
                var nextField = fieldOpt.get();
                if (nextField.type.getRecordName() != null) {
                    record = recordMap.get(nextField.type.getRecordName());
                } else {
                    type = nextField.type;
                }
            }
            if (type == null) throw new ContextError("Invalid record");
            return type;
        }
    }

    public interface ICommand {
        public ICommand check(Map<String, Types> localScope) throws TypeError, ContextError;
    }

    public interface ISkipCommand extends ICommand {
    }

    public static class SkipCommand implements ISkipCommand {
        @Override
        public ICommand check(Map<String, Types> localScope) throws TypeError {
            // No scope checking required.
            return this;
        }
    }

    public interface IAssignmentCommand extends ICommand {
    }

    public static class AssignmentCommand implements IAssignmentCommand {
        public IExpression l;
        public IExpression r;

        public AssignmentCommand(IExpression l, IExpression r) {
            this.l = l;
            this.r = r;
        }

        @Override
        public ICommand check(Map<String, Types> localScope) throws TypeError, ContextError {
            l = l.check(localScope);
            r = r.check(localScope);


            if (!l.isValidLeft())
                throw new ContextError(String.format("%s cannot be on the left side of an assignment", l.toString()));
            if (!r.isValidRight())
                throw new ContextError(String.format("%s cannot be on the right side of an assignment", r.toString()));

            if (l.getType(localScope) != r.getType(localScope))
                throw new TypeError("AssignmentCommand", l.getType(localScope).toString(), r.getType(localScope).toString());

            return this;
        }
    }

    public interface IIfCommand extends ICommand {
    }

    public static class IfCommand implements IIfCommand {
        public IExpression condition;
        public List<ICommand> commands;
        public List<ICommand> elseCommands;

        public IfCommand(IExpression condition, List<ICommand> commands, List<ICommand> elseCommands) {
            this.condition = condition;
            this.commands = commands;
            this.elseCommands = elseCommands;
        }

        @Override
        public ICommand check(Map<String, Types> localScope) throws TypeError, ContextError {
            condition = condition.check(localScope);

            if (condition.getType(localScope) != Types.BOOLEAN)
                throw new TypeError("IfCommand condition", condition.getType(localScope).toString(), Types.BOOLEAN.toString());

            List<ICommand> newCmds = new LinkedList<>();
            for (ICommand command : commands) {
                newCmds.add(command.check(localScope));
            }
            commands = newCmds;

            List<ICommand> newElseCmds = new LinkedList<>();
            for (ICommand command : elseCommands) {
                newElseCmds.add(command.check(localScope));
            }
            elseCommands = newElseCmds;
            return this;
        }
    }

    public interface IWhileCommand extends ICommand {
    }

    public static class WhileCommand implements IWhileCommand {
        public IExpression condition;
        public List<ICommand> commands;

        public WhileCommand(IExpression condition, List<ICommand> commands) {
            this.condition = condition;
            this.commands = commands;
        }

        @Override
        public ICommand check(Map<String, Types> localScope) throws TypeError, ContextError {
            condition = condition.check(localScope);

            if (condition.getType(localScope) != Types.BOOLEAN)
                throw new TypeError("WhileCommand", condition.getType(localScope).toString(), Types.BOOLEAN.toString());

            List<ICommand> newElseCmds = new LinkedList<>();
            for (ICommand command : commands) {
                newElseCmds.add(command.check(localScope));
            }
            commands = newElseCmds;

            return this;
        }
    }

    public interface ICallCommand extends ICommand {
    }

    public static class CallCommand implements ICallCommand {
        public String name;
        public List<IExpression> arguments;
        public List<String> globalInits;

        public CallCommand(String name, List<IExpression> arguments, List<String> globalInits) {
            this.name = name;
            this.arguments = arguments;
            this.globalInits = globalInits;
        }

        @Override
        public ICommand check(Map<String, Types> localScope) throws TypeError, ContextError {
            // TODO look up in global proc table
            // TODO match types in args

            List<IExpression> newArgs = new LinkedList<>();
            for (IExpression e : arguments) {
                newArgs.add(e.check(localScope));
            }
            arguments = newArgs;

            // TODO look up global inits

            return this;
        }
    }

    public interface IDebugInCommand extends ICommand {
    }

    public static class DebugInCommand implements IDebugInCommand {
        public IExpression expression;

        public DebugInCommand(IExpression expression) {
            this.expression = expression;
        }

        @Override
        public ICommand check(Map<String, Types> localScope) throws TypeError, ContextError {
            expression = expression.check(localScope);
            return this;
        }
    }

    public interface IDebugOutCommand extends ICommand {
    }

    public static class DebugOutCommand implements IDebugOutCommand {
        public IExpression expression;

        public DebugOutCommand(IExpression expression) {
            this.expression = expression;
        }

        @Override
        public ICommand check(Map<String, Types> localScope) throws TypeError, ContextError {
            expression = expression.check(localScope);
            return this;
        }
    }

    public interface IProgram {
        public IProgram check() throws TypeError, ContextError;
    }

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

        @Override
        public IProgram check() throws TypeError, ContextError {

            Map<String, Types> symbolTable = new HashMap<>();

            List<IProgramParameter> newParams = new LinkedList<>();
            for (IProgramParameter pp : programParameters) {
                newParams.add(pp.check());
                var p = (ProgramParameter)pp;
                symbolTable.put(p.typedIdentifier.getName(), p.typedIdentifier.getType());
            }
            programParameters = newParams;

            List<IDeclaration> newGlobDecls = new LinkedList<>();
            for (IDeclaration gd : globalDeclarations) {
                newGlobDecls.add(gd.check(symbolTable));
                if (gd instanceof StorageDeclaration) {
                    var s = (StorageDeclaration) gd;
                    symbolTable.put(s.typedIdentifier.getName(), s.typedIdentifier.getType());
                }
            }
            globalDeclarations = newGlobDecls;

            List<ICommand> newCmds = new LinkedList<>();
            for (ICommand c : commands) {
                newCmds.add(c.check(symbolTable));
            }
            commands = newCmds;

            return this;
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
