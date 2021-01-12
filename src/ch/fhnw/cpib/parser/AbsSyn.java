package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.checks.*;
import ch.fhnw.cpib.checks.types.BoolCodeType;
import ch.fhnw.cpib.checks.types.ICodeType;
import ch.fhnw.cpib.checks.types.IntCodeType;
import ch.fhnw.cpib.checks.types.RecordCodeType;
import ch.fhnw.cpib.codeGen.Environment;
import ch.fhnw.cpib.exceptions.ContextError;
import ch.fhnw.cpib.exceptions.TypeError;
import ch.fhnw.cpib.lexer.tokens.*;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray.CodeTooSmallError;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbsSyn {

    interface IAbsSynNode {

        /**
         * Creates the code in the VM and returns the new location.
         *
         * @throws CodeTooSmallError
         *           Thrown when the code segment of the VM is full.
         */
        int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError, ContextError, TypeError;
    }

    private static Map<String, ProcedureSignature> procedureMap;
    private static Map<String, RecordSignature> recordMap;

    public interface IType {
        ICodeType convertType();
    }
    public static class TraditionalType implements IType {
        public Type type;

        public TraditionalType(Type type) {
            this.type = type;
        }

        @Override
        public ICodeType convertType() {
            return Types.allTypes.get(type.attr.toString());
        }
    }

    public static class RecordType implements IType {
        public String name;

        public RecordType(String name) {
            this.name = name;
        }

        @Override
        public ICodeType convertType() {
            return Types.allTypes.get(name);
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
        String getName();
        ICodeType getType();
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
        public ICodeType getType() {
            return type.convertType();
        }
    }

    public interface IProgramParameter {
        String getName();
        IProgramParameter check(Map<String, VariableSignature> parentScope) throws ContextError;
        VariableSignature getSignature();
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
            return new VariableSignature(typedIdentifier.getType(), flowMode.getFlowMode(), changeMode.getChangeMode(), null, AccessMode.DIRECT, Scope.GLOBAL);
        }
    }

    public interface IParameter {
        IParameter check(Map<String, VariableSignature> parentScope) throws ContextError;
        String getName();
        VariableSignature getSignature(Scope scope);
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
            if (this.flowMode instanceof InFlowMode && this.mechMode instanceof RefMechMode && this.changeMode instanceof VarChangeMode) {
                throw new ContextError("IN REF VAR is invalid!");
            }

            return this;
        }

        @Override
        public String getName() {
            return typedIdentifier.getName();
        }

        @Override
        public VariableSignature getSignature(Scope scope) {
            AccessMode am = mechMode.getMechMode() == Mechmode.Attr.REF ? AccessMode.INDIRECT : AccessMode.DIRECT;
            return new VariableSignature(typedIdentifier.getType(), flowMode.getFlowMode(), changeMode.getChangeMode(), mechMode.getMechMode(), am, scope);
        }
    }

    public interface IGlobalImport {
        IGlobalImport check(Map<String, VariableSignature> parentScope) throws ContextError;
        String getName();
        VariableSignature getSignature(VariableSignature outside);
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
            return new VariableSignature(outside.getType(), flowMode.getFlowMode(), changeMode.getChangeMode(), outside.getMechMode(), AccessMode.DIRECT, Scope.GLOBAL);
        }
    }

    public interface IDeclaration {
        IDeclaration check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError;
    }

    public interface IStorageDeclaration extends IDeclaration {
        String getName();
        IStorageDeclaration check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError;
        VariableSignature getSignature(Scope scope);
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
        public VariableSignature getSignature(Scope scope) {
            return new VariableSignature(typedIdentifier.getType(), null, changeMode.getChangeMode(), null, AccessMode.DIRECT, scope);
        }
    }

    public interface IFunctionDeclaration extends IDeclaration {
    }

    public static class FunctionDeclaration implements IFunctionDeclaration {
        public String name;
        public List<IParameter> parameterList;
        public IStorageDeclaration returnValue;
        public List<IGlobalImport> globalImports;
        public List<IStorageDeclaration> localImports; // todo rename this to local variables or whatever
        public List<ICommand> commands;

        public Map<String, VariableSignature> symbolTable = null;

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

            symbolTable = new HashMap<>();

            // Register function before checking commands to allow for recursion
            List<VariableSignature> args = new LinkedList<>();
            for (IParameter iParameter : parameterList) {
                Parameter p = (Parameter) iParameter;

                if (!(p.flowMode instanceof InFlowMode))
                    throw new ContextError("FlowMode in Functions can only be In. Found " + p.flowMode);

                symbolTable.put(p.getName(), p.getSignature(Scope.LOCAL)); // Add parameter to scope
                args.add(p.getSignature(Scope.LOCAL));

            }

            List<VariableSignature> gis = new LinkedList<>();
            for (IGlobalImport iGlobalImport : globalImports) {
                var g = (GlobalImport)iGlobalImport;
                var o = parentScope.get(g.name);
                var v = g.getSignature(o);
                gis.add(v);
            }

            returnValue = returnValue.check(parentScope);
            FunctionSignature signature = new FunctionSignature(gis, args, symbolTable, returnValue.getSignature(Scope.LOCAL).getType());
            procedureMap.put(name, signature);

            symbolTable.put(returnValue.getName(), returnValue.getSignature(Scope.LOCAL));

            List<IGlobalImport> newGlobImps = new LinkedList<>();
            for (IGlobalImport gi : globalImports) {
                var newGi = gi.check(symbolTable);
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
                symbolTable.put(newSd.getName(), newSd.getSignature(Scope.LOCAL));
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

        public Map<String, VariableSignature> symbolTable = null;

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
                args.add(p.getSignature(Scope.LOCAL));
            }
            List<VariableSignature> gi = new LinkedList<>();
            for (IGlobalImport iGlobalImport : globalImports) {
                var g = (GlobalImport)iGlobalImport;
                var o = parentScope.get(g.name);
                var v = g.getSignature(o);
                gi.add(v);
            }

            symbolTable = new HashMap<>();
            procedureMap.put(name, new ProcedureSignature(gi, args, symbolTable));

            List<IParameter> newParams = new LinkedList<>();
            for (IParameter iParameter : parameters) {
                var p = iParameter.check(symbolTable);
                var sign = p.getSignature(Scope.LOCAL);
                if ((sign.getFlowMode() == Flowmode.Attr.INOUT ||
                    sign.getFlowMode() == Flowmode.Attr.OUT) &&
                    sign.getMechMode() == Mechmode.Attr.COPY
                ) {
                    throw new ContextError("(INOUT | OUT) COPY are currently not supported");
                }

                symbolTable.put(p.getName(), p.getSignature(Scope.LOCAL));
                newParams.add(p);
            }
            parameters = newParams;

            List<IGlobalImport> newGlobImps = new LinkedList<>();
            for (IGlobalImport iGlobalImport : globalImports) {
                var g = iGlobalImport.check(symbolTable);
                var outside = parentScope.get(g.getName());
                if (outside == null)
                    throw new ContextError(String.format("Couldn't find %s", g.getName()));
                symbolTable.put(g.getName(), g.getSignature(outside));
                newGlobImps.add(g);
            }
            globalImports = newGlobImps;

            List<IStorageDeclaration> newLocalImps = new LinkedList<>();
            for (IStorageDeclaration localImport : localImports) {
                var li = localImport.check(symbolTable);
                symbolTable.put(li.getName(), li.getSignature(Scope.LOCAL));
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
            Types.allTypes.put(name, new RecordCodeType(name, f));

            return this;
        }
    }

    public interface IMonadicOperator {
    }

    public static class NotMonadicOperator implements IMonadicOperator {
    }

    public static class PosMonadicOperator implements IMonadicOperator {
    }

    public static class NegMonadicOperator implements IMonadicOperator {
    }

    public interface IExpression {
        IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError;

        ICodeType getType(Map<String, VariableSignature> parentScope) throws TypeError, ContextError;

        boolean isValidLeft();

        boolean isValidRight();

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
        public ICodeType getType(Map<String, VariableSignature> parentScope) {
            return new BoolCodeType();
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
        public String toString() {
            return "IntLiteralExpression{" +
                    "value='" + value + '\'' +
                    '}';
        }

        @Override
        public IExpression check(Map<String, VariableSignature> parentScope) {
            return this;
        }

        @Override
        public ICodeType getType(Map<String, VariableSignature> parentScope) {
            return new IntCodeType(64); // TODO allow for all ints
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
        public String toString() {
            return "StoreExpression{" +
                    "name='" + name + '\'' +
                    ",init=" + init +
                    '}';
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
        public ICodeType getType(Map<String, VariableSignature> parentScope) throws ContextError {
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
            if (info.isLocalScope) {
                if (info.isDirectAccess) {
                    codeArray.put(location++, new IInstructions.LoadAddrRel(info.addr));
                } else {
                    codeArray.put(location++, new IInstructions.LoadAddrRel(info.addr));
                    codeArray.put(location++, new IInstructions.Deref());
                }
            } else {
                if (info.isDirectAccess) {
                    codeArray.put(location++, new IInstructions.LoadImInt(info.addr));
                } else {
                    throw new RuntimeException("Invalid identifier info found for var: " + name);
                }
            }

            return location;
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            // todo deduplicate with record access
            try {
                ICodeType type = getType(env.getSymbolTable());
                if (type instanceof RecordCodeType) {
                    for (int i = 0; i < type.getSize(); i++) {
                        location = codeLValue(codeArray, location, env); // hack: evaluate lValue multiple times
                        codeArray.put(location++, new IInstructions.LoadImInt(i));
                        codeArray.put(location++, new IInstructions.AddInt());
                        codeArray.put(location++, new IInstructions.Deref());
                    }
                } else {
                    location = codeLValue(codeArray, location, env);
                    codeArray.put(location++, new IInstructions.Deref());
                }
                return location;
            } catch (ContextError err) {
                err.printStackTrace();
            }
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
        public String toString() {
            return "FunctionCallExpression{" +
                    "name='" + name + '\'' +
                    //", arguments=" + arguments +
                    '}';
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

            var desiredArguments = procedureMap.get(name).arguments;
            if (arguments.size() != desiredArguments.size())
                throw new ContextError(name + ": expected " + desiredArguments.size() + " arguments. Found " + arguments.size());
            for (int i = 0; i < arguments.size(); i++) {
                var actualType = arguments.get(i).getType(parentScope);
                var desiredType = desiredArguments.get(i).getType();
                if (!desiredType.equals(actualType))
                    throw new TypeError("FunctionCallExpression", actualType.toString(), desiredType.toString());
            }

            return this;
        }

        @Override
        public ICodeType getType(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
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
            FunctionSignature signature = (FunctionSignature)procedureMap.get(name);

            int returnTypeSize = signature.returnType.getSize();
            codeArray.put(location++, new IInstructions.AllocBlock(returnTypeSize));

            //

            for (int i = 0; i < arguments.size(); i++) {
                var argument = arguments.get(i);
                var param = signature.arguments.get(i);
                if (param.getMechMode() == Mechmode.Attr.REF) {
                    location = argument.codeLValue(codeArray, location, env);
                } else {
                    if (param.getFlowMode() == Flowmode.Attr.OUT) {
                        codeArray.put(location++, new IInstructions.AllocBlock(param.getType().getSize()));
                    } else {
                        location = argument.codeRValue(codeArray, location, env);
                    }
                }
            }

            signature.temporaryAddressLocations.add(location);
            codeArray.put(location++, new IInstructions.Call(-1)); // this will be replaced later

            return location;
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
            if (arguments.size() != desiredArguments.size())
                throw new ContextError(name + ": expected " + desiredArguments.size() + " arguments. Found " + arguments.size());
            for (int i = 0; i < arguments.size(); i++) {
                var actualType = arguments.get(i).getType(parentScope);
                var desiredType = desiredArguments.get(i).type;
                if (!desiredType.equals(actualType))
                    throw new TypeError("RecordCallExpression", actualType.toString(), desiredType.toString());
            }

            return this;
        }

        @Override
        public ICodeType getType(Map<String, VariableSignature> parentScope) {
            return Types.allTypes.get(name);
        }


        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("RecordCallExpression cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            for (IExpression expression : arguments) {
                location =  expression.codeRValue(codeArray, location, env);
            }
            return location;
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
            if (operator instanceof NotMonadicOperator && !(expression.getType(parentScope) instanceof BoolCodeType)) {
                throw new TypeError("NotMonadicOperator", expression.getType(parentScope).toString(), BoolCodeType.typeString);

            }
            if (operator instanceof PosMonadicOperator && !(expression.getType(parentScope) instanceof IntCodeType)) {
                throw new TypeError("PosMonadicOperator", expression.getType(parentScope).toString(), IntCodeType.typeString);
            }
            if (operator instanceof NegMonadicOperator && !(expression.getType(parentScope) instanceof IntCodeType)) {
                throw new TypeError("NegMonadicOperator", expression.getType(parentScope).toString(), IntCodeType.typeString);
            }
            expression = expression.check(parentScope);
            return this;
        }

        @Override
        public ICodeType getType(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
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
            } else if (operator instanceof NegMonadicOperator) {
                location = expression.codeRValue(codeArray, location, env);
                codeArray.put(location++, new IInstructions.LoadImInt(-1));
                codeArray.put(location++, new IInstructions.MultInt());
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
    }

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

        @Override
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            if (!(l.getType(parentScope) instanceof IntCodeType)) {
                throw new TypeError("MultiplicationDyadicExpression", l.getType(parentScope).toString(), IntCodeType.typeString);
            }
            if (!(r.getType(parentScope) instanceof IntCodeType)) {
                throw new TypeError("MultiplicationDyadicExpression", r.getType(parentScope).toString(), IntCodeType.typeString);
            }
            l = l.check(parentScope);
            r = r.check(parentScope);
            return this;
        }

        @Override
        public ICodeType getType(Map<String, VariableSignature> parentScope) throws ContextError, TypeError {
            // Currently not used, future-proof; Use larger int type
            IntCodeType lType = (IntCodeType)l.getType(parentScope);
            IntCodeType rType = (IntCodeType)r.getType(parentScope);
            int bits = Math.max(lType.getBits(), rType.getBits());
            return new IntCodeType(bits);
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
            if (!(l.getType(parentScope) instanceof IntCodeType)) {
                throw new TypeError("AdditionDyadicExpression", l.getType(parentScope).toString(), IntCodeType.typeString);
            }
            if (!(r.getType(parentScope) instanceof IntCodeType)) {
                throw new TypeError("AdditionDyadicExpression", r.getType(parentScope).toString(), IntCodeType.typeString);
            }
            l = l.check(parentScope);
            r = r.check(parentScope);
            return this;
        }

        @Override
        public ICodeType getType(Map<String, VariableSignature> parentScope) throws ContextError, TypeError {
            // Currently not used, future-proof; Use larger int type
            IntCodeType lType = (IntCodeType)l.getType(parentScope);
            IntCodeType rType = (IntCodeType)r.getType(parentScope);
            int bits = Math.max(lType.getBits(), rType.getBits());
            return new IntCodeType(bits);
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
                    if (l.getType(parentScope) instanceof BoolCodeType && !(r.getType(parentScope) instanceof BoolCodeType)) {
                        throw new TypeError("RelativeDyadicExpression", r.getType(parentScope).toString(), BoolCodeType.typeString);
                    }
                case GE:
                case GT:
                case LE:
                case LT:
                    if (!(l.getType(parentScope) instanceof IntCodeType)) {
                        throw new TypeError("RelativeDyadicExpression", l.getType(parentScope).toString(), IntCodeType.typeString);
                    }
                    if (!(r.getType(parentScope) instanceof IntCodeType)) {
                        throw new TypeError("RelativeDyadicExpression", r.getType(parentScope).toString(), IntCodeType.typeString);
                    }
                    l = l.check(parentScope);
                    r = r.check(parentScope);
            }
            return this;
        }

        @Override
        public ICodeType getType(Map<String, VariableSignature> parentScope) {
            return new BoolCodeType();
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
            if (!(l.getType(parentScope) instanceof BoolCodeType)) {
                throw new TypeError("RelativeDyadicExpression", l.getType(parentScope).toString(), BoolCodeType.typeString);
            }
            if (!(r.getType(parentScope) instanceof BoolCodeType)) {
                throw new TypeError("RelativeDyadicExpression", r.getType(parentScope).toString(), BoolCodeType.typeString);
            }
            l = l.check(parentScope);
            r = r.check(parentScope);
            return this;
        }

        @Override
        public ICodeType getType(Map<String, VariableSignature> parentScope) {
            return new BoolCodeType();
        }

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            throw new RuntimeException("BoolDyadicExpression cant be used as l-value");
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            switch (operator) {
                case CAND -> {
                    location = l.codeRValue(codeArray, location, env);
                    int jumpLocation = location++;
                    location = r.codeRValue(codeArray, location, env); // eval r otherwise
                    int afterIfBodyJumpLocation = location++;
                    int elseAddr = location;
                    codeArray.put(location++, new IInstructions.LoadImInt(0)); // else evaluate to false
                    int afterElseBodyLocation = location;

                    codeArray.put(jumpLocation, new IInstructions.CondJump(elseAddr)); // jump to 0 if 0
                    codeArray.put(afterIfBodyJumpLocation, new IInstructions.UncondJump(afterElseBodyLocation)); // jump to after zero
                }
                case COR -> {
                    // calculate 1-l for cndJump
                    codeArray.put(location++, new IInstructions.LoadImInt(1));
                    location = l.codeRValue(codeArray, location, env);
                    codeArray.put(location++, new IInstructions.SubInt());


                    int jumpLocation = location++;
                    location = r.codeRValue(codeArray, location, env); // eval r otherwise
                    int afterIfBodyJumpLocation = location++;
                    int elseAddr = location;
                    codeArray.put(location++, new IInstructions.LoadImInt(1)); // else evaluate to true
                    int afterElseBodyLocation = location;

                    codeArray.put(jumpLocation, new IInstructions.CondJump(elseAddr)); // jump to 0 if 0
                    codeArray.put(afterIfBodyJumpLocation, new IInstructions.UncondJump(afterElseBodyLocation)); // jump to after zero
                }
            }
            return location;
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
        public String toString() {
            return "RecordAccessExpression{" +
                    "variableName='" + variableName + '\'' +
                    ", fieldNames=" + fieldNames +
                    '}';
        }

        @Override
        public IExpression check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            getType(parentScope);

            return this;
        }

        private ICodeType getRecordType(String recordName, String field, List<String> moreFields) throws ContextError {
            var recordSignature = recordMap.get(recordName);
            if (recordSignature == null)
                throw new ContextError(String.format("Couldn't find record %s", recordName));

            var correctField = recordSignature.fields.stream().filter(f -> f.name.equals(field)).findFirst();
            if (correctField.isEmpty())
                throw new ContextError(String.format("Couldn't find field '%s' on '%s'", field, recordName));

            var fieldType = correctField.get().type;
            if (!(fieldType instanceof RecordCodeType)) {
                if (moreFields.size() > 0)
                    throw new ContextError(String.format("Invalid fields: %s", moreFields.toString()));

                return fieldType;
            } else {
                if (moreFields.size() == 0) {
                    return fieldType;
                }
            }

            return getRecordType(fieldType.getName(), moreFields.get(0), moreFields.subList(1, moreFields.size()));
        }

        @Override
        public ICodeType getType(Map<String, VariableSignature> parentScope) throws ContextError, TypeError {
            var record = parentScope.get(variableName);
            if (record == null)
                throw new ContextError(String.format("Couldn't find %s in %s", variableName, parentScope.toString()));

            if (!(record.getType() instanceof RecordCodeType))
                throw new ContextError(String.format("%s isn't a record", variableName));

            var recordName = record.getType().getName();
            return getRecordType(recordName, fieldNames.get(0), fieldNames.subList(1, fieldNames.size()));
        }

        @Override
        public int codeLValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            Environment.IdentifierInfo info = env.getIdentifierInfo(variableName);
            var recordType = (RecordCodeType)(env.getSymbolTable().get(variableName).getType());
            int fieldOffset = recordType.calculateFieldOffset(fieldNames);

            if (info.isLocalScope) {
                if (info.isDirectAccess) {
                    codeArray.put(location++, new IInstructions.LoadAddrRel(info.addr + fieldOffset));
                } else {
                    codeArray.put(location++, new IInstructions.LoadAddrRel(info.addr));
                    codeArray.put(location++, new IInstructions.Deref());
                    codeArray.put(location++, new IInstructions.LoadImInt(fieldOffset));
                    codeArray.put(location++, new IInstructions.AddInt());
                }
            } else {
                if (info.isDirectAccess) {
                    codeArray.put(location++, new IInstructions.LoadImInt(info.addr+fieldOffset));
                } else {
                    throw new RuntimeException("invalid identifier info found for record: " + variableName);
                }
            }

            return location;
        }

        @Override
        public int codeRValue(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError {
            try {
                ICodeType type = getType(env.getSymbolTable());
                if (type instanceof RecordCodeType) {
                    for (int i = 0; i < type.getSize(); i++) {
                        location = codeLValue(codeArray, location, env); // hack: evaluate lValue multiple times
                        codeArray.put(location++, new IInstructions.LoadImInt(i));
                        codeArray.put(location++, new IInstructions.AddInt());
                        codeArray.put(location++, new IInstructions.Deref());
                    }
                } else {
                    location = codeLValue(codeArray, location, env);
                    codeArray.put(location++, new IInstructions.Deref());
                }
                return location;
            } catch (TypeError | ContextError err) {
                // Cannot occurr
                err.printStackTrace();
            }
            return location;
        }
    }

    public interface ICommand extends IAbsSynNode  {
        ICommand check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError;
    }
    public interface ISkipCommand extends ICommand {}
    public static class SkipCommand implements ISkipCommand {

        @Override
        public ICommand check(Map<String, VariableSignature> parentScope) throws TypeError, ContextError {
            return this;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) {
            return location;
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

            if (!l.getType(parentScope).equals(r.getType(parentScope)))
                throw new TypeError("AssignmentCommand", l.getType(parentScope).toString(), r.getType(parentScope).toString());

            if (l instanceof StoreExpression) {
                var se = (StoreExpression)l;
                var sign = parentScope.get(se.name);
                if (!se.init && sign.getChangeMode() == Changemode.Attr.CONST) {
                    throw new ContextError("Cannot re-assign constant values");
                }
            }
            if (l instanceof RecordAccessExpression) {
                var rae = (RecordAccessExpression)l;
                var sign = parentScope.get(rae.variableName);
                if (sign.getChangeMode() == Changemode.Attr.CONST) {
                    throw new ContextError("Cannot re-assign fields on constant records");
                }
            }

            return this;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError, ContextError, TypeError {
            ICodeType rType = r.getType(env.getSymbolTable());

            if (rType instanceof RecordCodeType) {
                location = r.codeRValue(codeArray, location, env);
                for (int i = rType.getSize(); i > 0; i--) {
                    location = l.codeLValue(codeArray, location, env); // hack: calculate lValue multiple times
                    codeArray.put(location++, new IInstructions.LoadImInt(i-1)); // add offset for current field
                    codeArray.put(location++, new IInstructions.AddInt());

                    codeArray.put(location++, new IInstructions.StoreRev()); // store reversed
                }
            } else {
                location = l.codeLValue(codeArray, location, env);
                location = r.codeRValue(codeArray, location, env);
                codeArray.put(location++, new IInstructions.Store());
            }

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

            if (!(condition.getType(parentScope) instanceof BoolCodeType))
                throw new TypeError("IfCommand condition", condition.getType(parentScope).toString(), BoolCodeType.typeString);

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
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError, ContextError, TypeError {
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

            if (!(condition.getType(parentScope) instanceof BoolCodeType))
                throw new TypeError("WhileCommand", condition.getType(parentScope).toString(), BoolCodeType.typeString);

            List<ICommand> newElseCmds = new LinkedList<>();
            for (ICommand command : commands) {
                newElseCmds.add(command.check(parentScope));
            }
            commands = newElseCmds;

            return this;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError, ContextError, TypeError {
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
            if (arguments.size() != desiredArguments.size())
                throw new ContextError(name + ": expected " + desiredArguments.size() + " arguments. Found " + arguments.size());

            for (int i = 0; i < arguments.size(); i++) {
                var actualType = arguments.get(i).getType(parentScope);
                var desiredType = desiredArguments.get(i).getType();
                if (desiredType != actualType)
                    throw new TypeError("CallCommand", actualType.toString(), desiredType.toString());
            }

            return this;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError, ContextError, TypeError {
            ProcedureSignature signature = procedureMap.get(name);

            for (int i = 0; i < arguments.size(); i++) {
                var argument = arguments.get(i);
                var param = signature.arguments.get(i);
                if (param.getMechMode() == Mechmode.Attr.REF) {
                    location = argument.codeLValue(codeArray, location, env);
                } else {
                    if (param.getFlowMode() == Flowmode.Attr.OUT) {
                        codeArray.put(location++, new IInstructions.AllocBlock(param.getType().getSize()));
                    } else {
                        location = argument.codeRValue(codeArray, location, env);
                    }
                }
            }

            signature.temporaryAddressLocations.add(location);
            codeArray.put(location++, new IInstructions.Call(-1)); // this will be replaced later

            return location;
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
            if (expression.getType(parentScope) instanceof RecordCodeType) {
                throw new ContextError("DebugIn doesn't support record types (yet?)");
            }
            return this;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError, ContextError, TypeError {
            location = expression.codeLValue(codeArray, location, env);
            ICodeType type = expression.getType(env.getSymbolTable());

            if (type instanceof IntCodeType) {
                codeArray.put(location++, new IInstructions.InputInt(expression.toString()));
            } else if (type instanceof BoolCodeType) {
                codeArray.put(location++, new IInstructions.InputBool(expression.toString()));
            }

            // todo maybe add better label for input
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
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError, ContextError, TypeError {
            location = expression.codeRValue(codeArray, location, env);
            ICodeType type = expression.getType(env.getSymbolTable());

            if (type instanceof IntCodeType) {
                codeArray.put(location++, new IInstructions.OutputInt(expression.toString()));
            } else if (type instanceof BoolCodeType) {
                codeArray.put(location++, new IInstructions.OutputBool(expression.toString()));
            } else if (type instanceof RecordCodeType) {
                List<FieldDesc> fieldNames = getRecordFieldNames(type.getName());
                for (int i = fieldNames.size() - 1; i >= 0; i--) {
                    var fieldDesc = fieldNames.get(i);
                    if (fieldDesc.isInt) {
                        codeArray.put(location++, new IInstructions.OutputInt(fieldDesc.name));
                    } else {
                        codeArray.put(location++, new IInstructions.OutputBool(fieldDesc.name));
                    }
                }
            } else {
                throw new RuntimeException("type not yet implemented for debugout: " + type.getName());
            }

            // todo maybe add better label for input

            return location;
        }

        private static class FieldDesc {
            public boolean isInt;
            public String name;

            public FieldDesc(boolean isInt, String name) {
                this.isInt = isInt;
                this.name = name;
            }
        }

        private List<FieldDesc> getRecordFieldNames(String recordName) {
            RecordSignature signature = recordMap.get(recordName);

            return signature.fields.stream().flatMap(field -> {
                if (field.type instanceof RecordCodeType) {
                    return getRecordFieldNames(field.type.getName()).stream().peek(str -> str.name = field.name + "." + str.name);
                } else {
                    return Stream.of(new FieldDesc(field.type instanceof IntCodeType, field.name));
                }
            }).collect(Collectors.toList());
        }
    }

    public interface IProgram extends IAbsSynNode  {
        IProgram check() throws TypeError, ContextError;
        Map<String, VariableSignature> getSymbolTable();
    }

    public static class Program implements IProgram {
        public String name;
        public List<IProgramParameter> programParameters;
        public List<IDeclaration> globalDeclarations;
        public List<ICommand> commands;

        public Map<String, VariableSignature> symbolTable = null;

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
            symbolTable = new HashMap<>();

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
                    symbolTable.put(s.getName(), s.getSignature(Scope.GLOBAL));
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
        public Map<String, VariableSignature> getSymbolTable() {
            return symbolTable;
        }

        @Override
        public int code(ICodeArray codeArray, int location, Environment env) throws CodeTooSmallError, ContextError, TypeError {
            if (!programParameters.isEmpty()) throw new RuntimeException("program params are not yet implemented");

            int globalStoreAddress = 0;
            for (IDeclaration declaration : globalDeclarations) {
                if (declaration instanceof StorageDeclaration) {
                    VariableSignature signature = env.getSymbolTable().get(((StorageDeclaration) declaration).getName());
                    int storeSize = signature.getType().getSize();

                    signature.setAddr(globalStoreAddress);
                    codeArray.put(location++, new IInstructions.AllocBlock(storeSize));
                    globalStoreAddress += storeSize;
                    // todo maybe sum up all store decls and produce only one allocBlock instruction?
                } else if (declaration instanceof RecordShapeDeclaration) {
                    // skip record decls
                } else if (declaration instanceof FunctionDeclaration) {
                    // skip function decls here, they will be generated after main body
                } else if (declaration instanceof ProcedureDeclaration) {
                    // skip proc decls here, they will be generated after main body
                } else {
                    // todo, maybe we can ignore some other declarations (like record shape) as they produce no code?
                    throw new RuntimeException("Global declaration not yet implemented:" + declaration.getClass().getSimpleName());
                }
            }

            for (ICommand command : commands) {
                location = command.code(codeArray, location, env);
            }
            codeArray.put(location++, new IInstructions.Stop());

            for (IDeclaration declaration : globalDeclarations) {
                if (declaration instanceof FunctionDeclaration) {
                    FunctionDeclaration declaration1 = (FunctionDeclaration) declaration;
                    procedureMap.get(declaration1.name).address = location;

                    int relAddress = 0;

                    // set addresses for global imports
                    for (int i = declaration1.globalImports.size() - 1; i >= 0; i--) {
                        IGlobalImport imp = declaration1.globalImports.get(i);
                        VariableSignature globalSignature = getSymbolTable().get(imp.getName());
                        VariableSignature signature = declaration1.symbolTable.get(imp.getName());
                        signature.setAddr(globalSignature.getAddr());
                    }

                    // set addresses for params
                    for (int i = declaration1.parameterList.size() - 1; i >= 0; i--) {
                        IParameter parameter = declaration1.parameterList.get(i);
                        VariableSignature signature = declaration1.symbolTable.get(parameter.getName());
                        int size = signature.getType().getSize();
                        if (signature.getMechMode() == Mechmode.Attr.REF) {
                            size = 1; // All relative addresses have size 1 (just the address)
                        }
                        relAddress -= size;
                        signature.setAddr(relAddress);
                    }

                    // set address for return type
                    IStorageDeclaration returnValue = declaration1.returnValue;
                    VariableSignature returnSignature = declaration1.symbolTable.get(returnValue.getName());
                    int returnSize = returnSignature.getType().getSize();

                    returnSignature.setAddr(relAddress - returnSize);

                    // local vars
                    int localStoreOffset = 3; // +2 for stored references of ep and pc above fp
                    for (IStorageDeclaration localStore : declaration1.localImports) {
                        VariableSignature signature = declaration1.symbolTable.get(localStore.getName());
                        int storeSize = signature.getType().getSize();

                        signature.setAddr(localStoreOffset);
                        codeArray.put(location++, new IInstructions.AllocBlock(storeSize));
                        localStoreOffset += storeSize;
                    }

                    var localEnv = new Environment(declaration1.symbolTable);


                    for (ICommand command : declaration1.commands) {
                        location = command.code(codeArray, location, localEnv);
                    }
                    codeArray.put(location++, new IInstructions.Return(-relAddress));
                } else if (declaration instanceof ProcedureDeclaration) {
                    ProcedureDeclaration declaration1 = (ProcedureDeclaration) declaration;
                    procedureMap.get(declaration1.name).address = location;

                    int relAddress = 0;

                    // set addresses for global imports
                    for (int i = declaration1.globalImports.size() - 1; i >= 0; i--) {
                        IGlobalImport imp = declaration1.globalImports.get(i);
                        VariableSignature globalSignature = getSymbolTable().get(imp.getName());
                        VariableSignature signature = declaration1.symbolTable.get(imp.getName());
                        signature.setAddr(globalSignature.getAddr());
                    }

                    // set addresses for params
                    for (int i = declaration1.parameters.size() - 1; i >= 0; i--) {
                        IParameter parameter = declaration1.parameters.get(i);
                        VariableSignature signature = declaration1.symbolTable.get(parameter.getName());
                        int size = signature.getType().getSize();
                        if (signature.getMechMode() == Mechmode.Attr.REF) {
                            size = 1; // All relative addresses have size 1 (just the address)
                        }
                        relAddress -= size;
                        signature.setAddr(relAddress);
                    }

                    // local vars
                    int localStoreOffset = 3; // +2 for stored references of ep and pc above fp
                    for (IStorageDeclaration localStore : declaration1.localImports) {
                        VariableSignature signature = declaration1.symbolTable.get(localStore.getName());
                        int storeSize = signature.getType().getSize();

                        signature.setAddr(localStoreOffset);
                        codeArray.put(location++, new IInstructions.AllocBlock(storeSize));
                        localStoreOffset += storeSize;
                    }

                    var localEnv = new Environment(declaration1.symbolTable);

                    for (ICommand command : declaration1.commands) {
                        location = command.code(codeArray, location, localEnv);
                    }
                    codeArray.put(location++, new IInstructions.Return(-relAddress));
                }
            }

            // replace placeholder function calls
            for (Map.Entry<String, ProcedureSignature> signatureEntry : procedureMap.entrySet()) {
                for (Integer temporaryAddressLocation : signatureEntry.getValue().temporaryAddressLocations) {
                    codeArray.put(temporaryAddressLocation, new IInstructions.Call(signatureEntry.getValue().address));
                }
            }

            return location;
        }
    }
}
