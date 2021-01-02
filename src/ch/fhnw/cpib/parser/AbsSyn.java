package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.checks.*;
import ch.fhnw.cpib.codeGen.Environment;
import ch.fhnw.cpib.exceptions.ContextError;
import ch.fhnw.cpib.exceptions.TypeError;
import ch.fhnw.cpib.lexer.tokens.*;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray.CodeTooSmallError;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AbsSyn {

    interface IAbsSynNode {

        /**
         * Creates the code in the VM and returns the new location.
         *
         * @throws CodeTooSmallError
         *           Thrown when the code segment of the VM is full.
         */
        default int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            // todo remove this default impl and check all missing implementations (maybe they dont need to implement this interface?)
            throw new RuntimeException("Not yet implemented");
        }
    }

    private static Map<String, ProcedureSignature> procedureMap;
    private static Map<String, RecordSignature> recordMap;

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
        Flowmode.Attr getFlowMode();
    }

    public static class InFlowMode implements IFlowMode {

        @Override
        public Flowmode.Attr getFlowMode() {
            return Flowmode.Attr.IN;
        }
    }

    public static class InOutFlowMode implements IFlowMode {

        @Override
        public Flowmode.Attr getFlowMode() {
            return Flowmode.Attr.INOUT;
        }
    }

    public static class OutFlowMode implements IFlowMode {

        @Override
        public Flowmode.Attr getFlowMode() {
            return Flowmode.Attr.OUT;
        }
    }

    public interface IMechMode {
        Mechmode.Attr getMechMode();
    }

    public static class CopyMechMode implements IMechMode {
        @Override
        public Mechmode.Attr getMechMode() {
            return Mechmode.Attr.COPY;
        }
    }

    public static class RefMechMode implements IMechMode {
        @Override
        public Mechmode.Attr getMechMode() {
            return Mechmode.Attr.REF;
        }
    }

    public interface IChangeMode {
        Changemode.Attr getChangeMode();
    }

    public static class VarChangeMode implements IChangeMode {
        @Override
        public Changemode.Attr getChangeMode() {
            return Changemode.Attr.VAR;
        }
    }

    public static class ConstChangeMode implements IChangeMode {
        @Override
        public Changemode.Attr getChangeMode() {
            return Changemode.Attr.CONST;
        }
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
        public String getName();
        public IProgramParameter check(Map<String, VariableSignature> parentScope) throws ContextError;
        public VariableSignature getSignature();
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
        public String getName() {
            return typedIdentifier.getName();
        }

        @Override
        public IProgramParameter check(Map<String, VariableSignature> parentScope) throws ContextError {
            if (parentScope.containsKey(typedIdentifier.getName())) {
                throw new ContextError(String.format("'%s' is already declared", typedIdentifier.getName()));
            }
            return this;
        }

        @Override
        public VariableSignature getSignature() {
            return new VariableSignature(typedIdentifier.getType(), flowMode.getFlowMode(), changeMode.getChangeMode(), null);
        }
    }

    public interface IParameter {
        public IParameter check(Map<String, VariableSignature> parentScope) throws ContextError;
        public String getName();
        public VariableSignature getSignature();
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
        public IParameter check(Map<String, VariableSignature> parentScope) throws ContextError {
            if (parentScope.containsKey(typedIdentifier.getName())) {
                // throw new ContextError(String.format("'%s' has already been declared", typedIdentifier.getName()));
            }

            return this;
        }

        @Override
        public String getName() {
            return typedIdentifier.getName();
        }

        @Override
        public VariableSignature getSignature() {
            return new VariableSignature(typedIdentifier.getType(), flowMode.getFlowMode(), changeMode.getChangeMode(), mechMode.getMechMode());
        }
    }

    public interface IGlobalImport {
        public IGlobalImport check(Map<String, VariableSignature> parentScope) throws ContextError;
        public String getName();
        public VariableSignature getSignature(VariableSignature outside);
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
        public IGlobalImport check(Map<String, VariableSignature> parentScope) throws ContextError {
            // TODO check what to do here
            // Proposal; Pass parent scope and current scope -> Needs to be in parent scope but not in current scope
            if (parentScope.containsKey(name))
                throw new ContextError(String.format("'%s' has already been declared", name));

            return this;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public VariableSignature getSignature(VariableSignature outside) {
            return new VariableSignature(outside.getType(), flowMode.getFlowMode(), changeMode.getChangeMode(), outside.getMechMode());
        }
    }

    public interface IDeclaration {
        public IDeclaration check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError;
    }

    public interface IStorageDeclaration extends IDeclaration {
        public String getName();
        public IStorageDeclaration check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError;
        public VariableSignature getSignature();
    }

    public static class StorageDeclaration implements IStorageDeclaration {
        public IChangeMode changeMode;
        public ITypedIdentifier typedIdentifier;

        public StorageDeclaration(IChangeMode changeMode, ITypedIdentifier typedIdentifier) {
            this.changeMode = changeMode;
            this.typedIdentifier = typedIdentifier;
        }

        @Override
        public IStorageDeclaration check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            return this;
        }

        @Override
        public String getName() {
            return typedIdentifier.getName();
        }

        @Override
        public VariableSignature getSignature() {
            return new VariableSignature(typedIdentifier.getType(), null, changeMode.getChangeMode(), null);
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
        public IDeclaration check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            if (procedureMap.containsKey(name)) {
                throw new ContextError("Function " + name + " is already declared");
            }
            if (recordMap.containsKey(name)) {
                throw new ContextError("Function " + name + " cannot be declared, there is a record of that name.");
            }

            Map<String, VariableSignature> symbolTable = new HashMap<>();

            // Register function before checking commands to allow for recursion
            List<VariableSignature> args = new LinkedList<>();
            for (IParameter iParameter : parameterList) {
                Parameter p = (Parameter) iParameter;

                if (!(p.flowMode instanceof InFlowMode))
                    throw new ContextError("FlowMode in Functions can only be In. Found " + p.flowMode);

                symbolTable.put(p.getName(), p.getSignature()); // Add parameter to scope
                args.add(p.getSignature());

            }
            returnValue = returnValue.check(parentScope);
            FunctionSignature signature = new FunctionSignature(args, returnValue.getSignature().getType());
            procedureMap.put(name, signature);

            symbolTable.put(returnValue.getName(), returnValue.getSignature());

            List<IGlobalImport> newGlobImps = new LinkedList<>();
            for (IGlobalImport gi : globalImports) {
                var newGi = gi.check(parentScope);
                var outside = parentScope.get(newGi.getName());
                if (outside == null) {
                    throw new ContextError(String.format("Couldn't find '%s'", newGi.getName()));
                }
                symbolTable.put(newGi.getName(), newGi.getSignature(outside));
                newGlobImps.add(newGi);
            }
            globalImports = newGlobImps;

            List<IStorageDeclaration> newLocalImps = new LinkedList<>();
            for (IStorageDeclaration sd : localImports) {
                var newSd = sd.check(parentScope);
                newLocalImps.add(newSd);
                symbolTable.put(newSd.getName(), newSd.getSignature());
            }
            localImports = newLocalImps;

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
        public IDeclaration check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            if (procedureMap.containsKey(name)) {
                throw new ContextError("Procedure " + name + " is already declared");
            }
            if (recordMap.containsKey(name)) {
                throw new ContextError("Procedure " + name + " cannot be declared, there is a record of that name.");
            }

            // Add procedure to procedureMap to allow for recursion
            List<VariableSignature> args = new LinkedList<>();
            for (IParameter iParameter : parameters) {
                var p = (Parameter) iParameter;
                args.add(p.getSignature());
            }
            procedureMap.put(name, new ProcedureSignature(args));

            Map<String, VariableSignature> symbolTable = new HashMap<>();

            List<IParameter> newParams = new LinkedList<>();
            for (IParameter iParameter : parameters) {
                var p = iParameter.check(symbolTable);
                symbolTable.put(p.getName(), p.getSignature());
                newParams.add(p);
            }
            parameters = newParams;

            List<IGlobalImport> newGlobImps = new LinkedList<>();
            for (IGlobalImport iGlobalImport : globalImports) {
                var gi = iGlobalImport.check(symbolTable);
                var outside = parentScope.get(gi.getName());
                if (outside == null)
                    throw new ContextError(String.format("Couldn't find %s", gi.getName()));
                symbolTable.put(gi.getName(), gi.getSignature(outside));
                newGlobImps.add(gi);
            }
            globalImports = newGlobImps;

            List<IStorageDeclaration> newLocalImps = new LinkedList<>();
            for (IStorageDeclaration localImport : localImports) {
                var li = localImport.check(symbolTable);
                symbolTable.put(li.getName(), li.getSignature());
                newLocalImps.add(li);
            }
            localImports = newLocalImps;

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
        public IDeclaration check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            if (recordMap.containsKey(name)) {
                throw new ContextError("Record " + name + " is already declared");
            }
            if (procedureMap.containsKey(name)) {
                throw new ContextError("Record " + name + " cannot be declared, there is already a procedure of that name");
            }

            List<RecordSignature.RecordField> f = new LinkedList<>();
            for (ITypedIdentifier ti : fields) {
                var newField = new RecordSignature.RecordField(ti.getName(), ti.getType());
                f.add(newField);
            }
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
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError;

        public Types getType(Map<String, VariableSignature> parentScope) throws TypeError, ContextError;

        public boolean isValidLeft();

        public boolean isValidRight();

        int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError;
        int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError;
    }
    public interface ILiteralExpression extends IExpression {}
    public interface IBoolLiteralExpression extends ILiteralExpression {}
    public static class BoolLiteralExpression implements IBoolLiteralExpression {
        public boolean value;

        public BoolLiteralExpression(boolean value) {
            this.value = value;
        }

        @Override
        public IExpression check(Map<String, VariableSignature> parentScope) {
            return this;
        }

        @Override
        public Types getType(Map<String, VariableSignature> parentScope) {
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

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) {
            throw new RuntimeException("boolean literal cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            codeArray.put(location++, new IInstructions.LoadImInt(value ? 1 : 0));
            return location;
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
        public IExpression check(Map<String, VariableSignature> parentScope) {
            return this;
        }

        @Override
        public Types getType(Map<String, VariableSignature> parentScope) {
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

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("int literal cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            codeArray.put(location++, new IInstructions.LoadImInt(Integer.parseInt(value)));
            return location;
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

        private boolean isNotInScope(Map<String, VariableSignature> parentScope) {
            return !parentScope.containsKey(name);
        }

        @Override
        public IExpression check(Map<String, VariableSignature> parentScope) throws ContextError {
            if (isNotInScope(parentScope))
                throw new ContextError(String.format("Couldn't find %s in %s", name, parentScope.toString()));

            /*
            TODO fix
            if (init) {
                if (!parentScope.get(name).isInitialized()) {
                    parentScope.get(name).initialize();
                } else {
                    throw new ContextError(String.format("'%s' is already initialized", name));
                }
            } else {
                if (!parentScope.get(name).isInitialized()) {
                    throw new ContextError(String.format("'%s' is not initialized", name));
                }
            }
            */

            return this;
        }

        @Override
        public Types getType(Map<String, VariableSignature> parentScope) throws ContextError {
            if (isNotInScope(parentScope)) {
                throw new ContextError(String.format("Couldn't find %s in %s", name, parentScope.toString()));
            }
            return parentScope.get(name).getType();
        }

        @Override
        public boolean isValidLeft() {
            return true;
        }

        @Override
        public boolean isValidRight() {
            return !init;
        }

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            Environment.IdentifierInfo info = env.getIdentifierInfo(name);
            if (!info.isLocalScope && info.isDirectAccess) {
                codeArray.put(location++, new IInstructions.LoadImInt(info.addr));
            } else if (info.isLocalScope && info.isDirectAccess) {
                codeArray.put(location++, new IInstructions.LoadAddrRel(info.addr));
            } else if (info.isLocalScope && !info.isDirectAccess) {
                codeArray.put(location++, new IInstructions.LoadAddrRel(info.addr));
                codeArray.put(location++, new IInstructions.Deref());
            } else {
                throw new RuntimeException("invalid identifier info found for var: " + name);
            }

            return location;
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            location = codeLValue(codeArray, location, env);
            codeArray.put(location++, new IInstructions.Deref());
            return location;
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
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {

            if (!procedureMap.containsKey(name) && !recordMap.containsKey(name))
                throw new ContextError(String.format("Unknown function %s", name));

            List<IExpression> newArgs = new LinkedList<>();
            for (IExpression e : arguments) newArgs.add(e.check(parentScope));
            arguments = newArgs;

            if (recordMap.containsKey(name)) {
                return new RecordCallExpression(name, arguments).check(parentScope);
            }

            // TODO checks
            var desiredArguments = procedureMap.get(name).arguments;
            for (int i = 0; i < arguments.size(); i++) {
                var actualType = arguments.get(i).getType(parentScope);
                var desiredType = desiredArguments.get(i).getType();
                if (desiredType != actualType)
                    throw new TypeError("FunctionCallExpression", actualType.toString(), desiredType.toString());
            }

            return this;
        }

        @Override
        public Types getType(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            var signature = procedureMap.get(name);
            if (signature == null) {
                throw new ContextError(String.format("Couldn't find function '%s'", name));
            }
            if (signature instanceof FunctionSignature) {
                return ((FunctionSignature) signature).returnType;
            }
            throw new TypeError("FunctionCallExpression", "Procedure", "Function");
        }

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("FunctionCallExpression cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("Not yet implemented");
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
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {

            var desiredArguments = recordMap.get(name).fields;
            for (int i = 0; i < arguments.size(); i++) {
                var actualType = arguments.get(i).getType(parentScope);
                var desiredType = desiredArguments.get(i).type;
                if (desiredType != actualType)
                    throw new TypeError("RecordCallExpression", actualType.toString(), desiredType.toString());
            }

            return this;
        }

        @Override
        public Types getType(Map<String, VariableSignature> parentScope) {
            return Types.allTypes.get(name);
        }


        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("FunctionCallExpression cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("Not yet implemented");
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
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            if (operator instanceof NotMonadicOperator && expression.getType(parentScope) != Types.BOOLEAN) {
                throw new TypeError("PosMonadicOperator", expression.getType(parentScope).toString(), Types.BOOLEAN.toString());

            }
            if (operator instanceof PosMonadicOperator && expression.getType(parentScope) != Types.INTEGER) {
                throw new TypeError("PosMonadicOperator", expression.getType(parentScope).toString(), Types.INTEGER.toString());
            }
            return expression.check(parentScope);
        }

        @Override
        public Types getType(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            return expression.getType(parentScope);
        }
        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("MonadicExpression cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            if (operator instanceof NotMonadicOperator) {
                codeArray.put(location++, new IInstructions.LoadImInt(1));
                location = expression.codeRValue(codeArray, location, env); // assumed condition: this expression evaluates to a boolean
                codeArray.put(location++, new IInstructions.SubInt()); // 1 - boolean = !boolean
            } else if (operator instanceof PosMonadicOperator) {
                location = expression.codeRValue(codeArray, location, env);
                // todo do we need to do anything?
            } else {
                throw new RuntimeException("Invalid operator");
            }

            return location;
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
    }public interface IMultiplicationDyadicExpression extends IDyadicExpression {}
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
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            if (l.getType(parentScope) != Types.INTEGER) {
                throw new TypeError("MultiplicationDyadicExpression", l.getType(parentScope).toString(), Types.INTEGER.toString());
            }
            if (r.getType(parentScope) != Types.INTEGER) {
                throw new TypeError("MultiplicationDyadicExpression", r.getType(parentScope).toString(), Types.INTEGER.toString());
            }
            l = l.check(parentScope);
            r = r.check(parentScope);
            return this;
        }

        @Override
        public Types getType(Map<String, VariableSignature> parentScope) {
            return Types.INTEGER;
        }

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("MultiplicationDyadicExpression cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            location = l.codeRValue(codeArray, location, env);
            location = r.codeRValue(codeArray, location, env);
            switch (operator) {
                case TIMES -> codeArray.put(location++, new IInstructions.MultInt());
                case DIV_T -> codeArray.put(location++, new IInstructions.DivTruncInt());
                case MOD_T -> codeArray.put(location++, new IInstructions.ModTruncInt());
                default -> throw new RuntimeException("NOT yet implemented:" + operator);
            }

            return location;
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
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            if (l.getType(parentScope) != Types.INTEGER) {
                throw new TypeError("AdditionDyadicExpression", l.getType(parentScope).toString(), Types.INTEGER.toString());
            }
            if (r.getType(parentScope) != Types.INTEGER) {
                throw new TypeError("AdditionDyadicExpression", r.getType(parentScope).toString(), Types.INTEGER.toString());
            }
            l = l.check(parentScope);
            r = r.check(parentScope);
            return this;
        }

        @Override
        public Types getType(Map<String, VariableSignature> parentScope) {
            return Types.INTEGER;
        }

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("AdditionDyadicExpression cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            location = l.codeRValue(codeArray, location, env);
            location = r.codeRValue(codeArray, location, env);
            switch (operator) {
                case PLUS -> codeArray.put(location++, new IInstructions.AddInt());
                case MINUS -> codeArray.put(location++, new IInstructions.SubInt());
            }

            return location;
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
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            switch (operator) {
                case EQ:
                case NE:
                    if (l.getType(parentScope) == Types.BOOLEAN && r.getType(parentScope) != Types.BOOLEAN) {
                        throw new TypeError("RelativeDyadicExpression", r.getType(parentScope).toString(), Types.BOOLEAN.toString());
                    }
                case GE:
                case GT:
                case LE:
                case LT:
                    if (l.getType(parentScope) != Types.INTEGER) {
                        throw new TypeError("RelativeDyadicExpression", l.getType(parentScope).toString(), Types.INTEGER.toString());
                    }
                    if (r.getType(parentScope) != Types.INTEGER) {
                        throw new TypeError("RelativeDyadicExpression", r.getType(parentScope).toString(), Types.INTEGER.toString());
                    }
                    l = l.check(parentScope);
                    r = r.check(parentScope);
            }
            return this;
        }

        @Override
        public Types getType(Map<String, VariableSignature> parentScope) {
            return Types.BOOLEAN;
        }

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("RelativeDyadicExpression cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            location = l.codeRValue(codeArray, location, env);
            location = r.codeRValue(codeArray, location, env);

            switch (operator) {
                case EQ -> codeArray.put(location++, new IInstructions.EqInt());
                case GE -> codeArray.put(location++, new IInstructions.GeInt());
                case GT -> codeArray.put(location++, new IInstructions.GtInt());
                case LE -> codeArray.put(location++, new IInstructions.LeInt());
                case LT -> codeArray.put(location++, new IInstructions.LtInt());
                case NE -> codeArray.put(location++, new IInstructions.NeInt());
            }
            return location;
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
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            if (l.getType(parentScope) != Types.BOOLEAN) {
                throw new TypeError("RelativeDyadicExpression", l.getType(parentScope).toString(), Types.BOOLEAN.toString());
            }
            if (r.getType(parentScope) != Types.BOOLEAN) {
                throw new TypeError("RelativeDyadicExpression", r.getType(parentScope).toString(), Types.BOOLEAN.toString());
            }
            l = l.check(parentScope);
            r = r.check(parentScope);
            return this;
        }

        @Override
        public Types getType(Map<String, VariableSignature> parentScope) {
            return Types.BOOLEAN;
        }

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("BoolDyadicExpression cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            // todo conditional evaluation
            // implement with conditional jumps to after all expressions, and evaluate current boolean result continuously
            throw new RuntimeException("Not yet implemented");
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
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            getType(parentScope);

            return this;
        }

        // TODO clean this shit up
        @Override
        public Types getType(Map<String, VariableSignature> parentScope) throws ContextError, TypeError {
            var signature = parentScope.get(variableName);
            if (signature == null) {
                throw new ContextError("Couldn't find record " + variableName + " in " + parentScope.toString());
            }
            var type = signature.getType();

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
                }
                finalType = nextField.type;
            }
            if (finalType == null) throw new ContextError("Couldn't resolve fields in record");
            return finalType;
        }

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("Not yet implemented");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            location = codeLValue(codeArray, location, env);
            codeArray.put(location++, new IInstructions.Deref());
            return location;
        }
    }

    public interface ICommand extends IAbsSynNode  {
        public ICommand check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError;
    }
    public interface ISkipCommand extends ICommand {}
    public static class SkipCommand implements ISkipCommand {

        @Override
        public ICommand check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            return this;
        }
    }
    public interface IAssignmentCommand extends ICommand {}
    public static class AssignmentCommand implements IAssignmentCommand {
        public IExpression l;
        public IExpression r;

        public AssignmentCommand(IExpression l, IExpression r) {
            this.l = l;
            this.r = r;
        }

        @Override
        public ICommand check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            l = l.check(parentScope);
            r = r.check(parentScope);

            if (!l.isValidLeft())
                throw new ContextError(String.format("%s cannot be on the left side of an assignment", l.toString()));
            if (!r.isValidRight())
                throw new ContextError(String.format("%s cannot be on the right side of an assignment", r.toString()));

            if (l.getType(parentScope) != r.getType(parentScope))
                throw new TypeError("AssignmentCommand", l.getType(parentScope).toString(), r.getType(parentScope).toString());

            return this;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            location = l.codeLValue(codeArray, location, env);
            location = r.codeRValue(codeArray, location, env);
            codeArray.put(location++, new IInstructions.Store());

            return location;
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
        public ICommand check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            condition = condition.check(parentScope);

            if (condition.getType(parentScope) != Types.BOOLEAN)
                throw new TypeError("IfCommand condition", condition.getType(parentScope).toString(), Types.BOOLEAN.toString());

            List<ICommand> newCmds = new LinkedList<>();
            for (ICommand command : commands) {
                newCmds.add(command.check(parentScope));
            }
            commands = newCmds;

            List<ICommand> newElseCmds = new LinkedList<>();
            for (ICommand command : elseCommands) {
                newElseCmds.add(command.check(parentScope));
            }
            elseCommands = newElseCmds;
            return this;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            location = condition.codeRValue(codeArray, location, env);
            int jumpLocation = location++;
            for (ICommand command : commands) {
                location = command.code(codeArray, location, env);
            }
            int afterIfBodyJumpLocation = location++;
            int elseBodyStartLocation = location;
            for (ICommand command : elseCommands) {
                location = command.code(codeArray, location, env);
            }
            int afterElseBodyLocation = location;

            codeArray.put(jumpLocation, new IInstructions.CondJump(elseBodyStartLocation));
            codeArray.put(afterIfBodyJumpLocation, new IInstructions.UncondJump(afterElseBodyLocation));
            return location;
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
        public ICommand check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            condition = condition.check(parentScope);

            if (condition.getType(parentScope) != Types.BOOLEAN)
                throw new TypeError("WhileCommand", condition.getType(parentScope).toString(), Types.BOOLEAN.toString());

            List<ICommand> newElseCmds = new LinkedList<>();
            for (ICommand command : commands) {
                newElseCmds.add(command.check(parentScope));
            }
            commands = newElseCmds;

            return this;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            int conditionLocation = location;
            location = condition.codeRValue(codeArray, location, env);
            int jumpLocation = location++;
            for (ICommand command : commands) {
                location = command.code(codeArray, location, env);
            }
            codeArray.put(location++, new IInstructions.UncondJump(conditionLocation));
            int afterBodyLocation = location;
            codeArray.put(jumpLocation, new IInstructions.CondJump(afterBodyLocation));

            return location;
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
        public ICommand check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            if (!procedureMap.containsKey(name)) {
                throw new ContextError(String.format("Couldn't find procedure '%s'", name));
            }

            List<IExpression> newArgs = new LinkedList<>();
            for (IExpression e : arguments) {
                newArgs.add(e.check(parentScope));
            }
            arguments = newArgs;

            var desiredArguments = procedureMap.get(name).arguments;
            for (int i = 0; i < arguments.size(); i++) {
                var actualType = arguments.get(i).getType(parentScope);
                var desiredType = desiredArguments.get(i).getType();
                if (desiredType != actualType)
                    throw new TypeError("CallCommand", actualType.toString(), desiredType.toString());
            }

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
        public ICommand check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            expression = expression.check(parentScope);
            if (!expression.isValidLeft()) {
                throw new ContextError("DebugIn can only accept L-Expressions");
            }
            if (expression.getType(parentScope).getType().equals("Record")) {
                throw new ContextError("DebugIn doesn't support record types (yet?)");
            }
            return this;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            location = expression.codeLValue(codeArray, location, env);
            // todo differentiate between bool and int expressions, and maybe add better label for input
            codeArray.put(location++, new IInstructions.InputInt(expression.toString()));
            return location;
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
        public ICommand check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            expression = expression.check(parentScope);
            if (!expression.isValidRight()) {
                throw new ContextError("DebugOut can only accept R-Expressions");
            }
            return this;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            location = expression.codeRValue(codeArray, location, env);
            // todo differentiate between bool and int expressions, and maybe add better label for input
            codeArray.put(location++, new IInstructions.OutputInt(expression.toString()));
            return location;
        }
    }

    public interface IProgram extends IAbsSynNode  {
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
            procedureMap = new HashMap<>();
            recordMap = new HashMap<>();

            Map<String, VariableSignature> symbolTable = new HashMap<>();

            List<IProgramParameter> newParams = new LinkedList<>();
            for (IProgramParameter pp : programParameters) {
                newParams.add(pp.check(symbolTable));
                symbolTable.put(pp.getName(), pp.getSignature());
            }
            programParameters = newParams;

            List<IDeclaration> newGlobDecls = new LinkedList<>();
            for (IDeclaration gd : globalDeclarations) {
                newGlobDecls.add(gd.check(symbolTable));
                if (gd instanceof StorageDeclaration) {
                    var s = (IStorageDeclaration) gd;
                    symbolTable.put(s.getName(), s.getSignature());
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

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            if (!programParameters.isEmpty()) throw new RuntimeException("program params are not yet implemented");

            for (IDeclaration declaration : globalDeclarations) {
                if (declaration instanceof StorageDeclaration) {
                    //StorageDeclaration storageDeclaration = (StorageDeclaration) declaration;
                    codeArray.put(location++, new IInstructions.AllocBlock(1));
                    // todo maybe sum up all store decls and produce only one allocBlock instruction?
                } else {
                    // todo, maybe we can ignore some other declarations (like record shape) as they produce no code?
                    throw new RuntimeException("Global declaration not yet implemented:" + declaration.getClass().getSimpleName());
                }
            }

            for (ICommand command : commands) {
                location = command.code(codeArray, location, env);
            }
            codeArray.put(location++, new IInstructions.Stop());

            return location;
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
