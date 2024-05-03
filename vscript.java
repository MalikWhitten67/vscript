import java.util.*;

import javax.print.DocFlavor.STRING;

import ErrorHandler.Errors;
import keywords.*;
import java.io.*;

class StringMethods{
    public Boolean isString(String value1) {
        if (value1.startsWith("\"") && value1.endsWith("\"") || value1.startsWith("'") && value1.endsWith("'")) {
            return true;
        } else if (value1.contains("'") || value1.contains("\""))
            return true;
        return false;
    }
}
class ReadFile {
    public String open(String file) {
        try {
            File data = new File(file);
            Scanner reader = new Scanner(data);
            StringBuilder filedata = new StringBuilder(); // StringBuilder for better performance

            while (reader.hasNextLine()) {
                filedata.append(reader.nextLine()).append('\n'); // Use StringBuilder's append method
            }

            reader.close(); // Close the Scanner when done

            return filedata.toString(); // Convert StringBuilder to String and return

        } catch (FileNotFoundException e) {
            throw new Error("File not found: " + e.getMessage()); // Properly handle FileNotFoundException
        }
    }

    public static void main(String[] args) {

    }
}

class FunctionParams {
    String name = "";
    String type = "";
    String callable = "";
    String reurnable = "";
}

abstract class AstChildObject {
}

/**
 * @method AstObject
 */
class AstObject {
    String type = "";
    Boolean isGlobal = false;
    Boolean isVariable = false;
    String referencing = "";
    String left = "";
    String fullvalue = "";
    String right = "";
    AstObject function;
    char operator;
    ArrayList<String> opperands = new ArrayList<>();
    String name = "";
    String Variable_Name = "";
    String return_type = "";
    /**
     * @description multi use return value
     */
    Object returnedValue;
    /**
     * @description when set to true check the parentBind to see what it is bound to
     *              then only parse it in that scopes reference
     */
    Boolean Scoped = false;
    /**
     * @description - the objects reference name in which the current item is bound
     *              to
     */
    String parentBind = "";
    /**
     * @description - Used for error tracking
     */
    ArrayList<Integer> lines = new ArrayList<>();
    /**
     * @description list of function parameters
     */
    ArrayList<FunctionParams> params = new ArrayList<>();
    /**
     * @description multipurpose return value or simply a value;
     */
    String value = "";
    /**
     * @description list of child objects pertaining to an node;
     */
    ArrayList<AstObject> children = new ArrayList<>();
}

class OperatorAssignmentNode {
    ArrayList<String> indexs = new ArrayList<>();
    ArrayList<String> operators = new ArrayList<>();
    ArrayList<OperatorNode> lefts_rights = new ArrayList<>();
}

/**
 * @decription - Ast Node used to hold Operator declarations
 */
class OperatorNode {
    String left = "";
    String right = "";
    AstObject function;
    ArrayList<OperatorAssignmentNode> Assignments = new ArrayList<>();
    char operator;
}

class GenerateAstTree {
    public void generateTree(String data, AstObject tree) {
        int i = 0;
        while (i < data.length()) {
            String token = data.substring(i, Math.min(i + 3, data.length()));
            if (token.equals("int") && !data.substring(i, Math.min(i + 5, data.length())).equals("int32")) {

                i += 3;
                AstObject _int = new AstObject();
                _int.name = "int32_variable";
                _int.type = "int32";
                _int.Variable_Name = "";
                _int.fullvalue += "int";
                _int.value = "";
                _int.lines.add(i);
                _int.isVariable = true;
                if (data.charAt(i) == ':') {
                    i++;
                    while (i < data.length() && !Character.isWhitespace(data.charAt(i))) {
                        if (data.substring(i, Math.min(i + 6, data.length())).equals("global")) {
                            _int.isGlobal = true;
                        } else if (data.substring(i, Math.min(i + 7, data.length())).equals("private")) {
                            _int.isGlobal = false;
                        }
                        i++;
                    }
                }

                // Find variable name
                while (i < data.length() && Character.isWhitespace(data.charAt(i))) {
                    _int.fullvalue += data.charAt(i);
                    i++;
                }
                while (i < data.length() && Character.isLetterOrDigit(data.charAt(i))) {
                    _int.Variable_Name += data.charAt(i);
                    _int.fullvalue += data.charAt(i);
                    _int.lines.add(i);
                    i++;
                }
                // Find value
                while (i < data.length() && Character.isWhitespace(data.charAt(i))) {
                    _int.fullvalue += data.charAt(i);
                    i++;
                }

                if (i < data.length() && data.charAt(i) == '=') {
                    _int.fullvalue += data.charAt(i);
                    i++;
                    while (i < data.length() && data.charAt(i) != ';') {
                        _int.value += data.charAt(i);
                        _int.fullvalue += data.charAt(i);
                        _int.lines.add(i);
                        i++;
                    }
                }
                _int.lines.add(i);
                _int.value = _int.value.trim();
                tree.children.add(_int);

            } else if (data.substring(i, Math.min(i + 5, data.length())).equals("print")) {   
                keyword keys = new keyword();
                AstObject _print_statement = new AstObject(); 
                _print_statement.fullvalue += data.substring(i, Math.min(i + 5, data.length())); 
                i += 5;
                _print_statement.type = "print_statement";
                _print_statement.name = "print";
                String _print_body = "";
                if (data.charAt(i) == '(')
                    _print_statement.fullvalue += data.charAt(i);
                    i++;
                while (i < data.length() && data.charAt(i) != ')') {
                    _print_body += data.charAt(i); 
                    _print_statement.fullvalue += data.charAt(i);
                    
                    i++;
                } 
                _print_statement.fullvalue += data.charAt(i);
                Boolean hasOperator = false;
                for (char k : keys.op_keywords) {
                    StringBuilder str = new StringBuilder();
                    str.append(k);
                    if (_print_body.contains(str.toString())) {
                        hasOperator = true;
                    }
                    ;
                }
                if (hasOperator) {
                    Boolean isstring = new StringMethods().isString(_print_body); 
                    _print_statement.children.add(parseOperatorStatement(_print_body, tree, isstring));
                }
                for (AstObject child : tree.children) {
                    if (child.isVariable && _print_body.contains(child.Variable_Name)) {
                        _print_statement.children.add(child);
                    }
                }
                _print_statement.value = _print_body;
                if (!tree.children.contains(_print_statement))
                    tree.children.add(_print_statement);

                i++;
            }

            else if (data.substring(i, Math.min(i + 6, data.length())).equals("import")) {
                System.out.println("yok");
                i += 6;
                AstObject _import = new AstObject();
                _import.type = "es4_import";
                _import.name = "import";
                _import.value = "";

                // Find value
                while (i < data.length() && Character.isWhitespace(data.charAt(i))) {
                    i++;
                }
                while (i < data.length() && data.charAt(i) != ';') {
                    _import.value += data.charAt(i++);
                    _import.lines.add(i);
                }

                tree.children.add(_import);
            } else if (data.substring(i, Math.min(i + 8, data.length())).equals("function")) {
                AstObject functionAstObject = new AstObject();
                functionAstObject.type = "function";
                i += 8;
                while (i < data.length() && Character.isWhitespace(data.charAt(i))) {
                    i++;
                }
                while (i < data.length() && Character.isLetterOrDigit(data.charAt(i))) {

                    functionAstObject.name += data.charAt(i);
                    functionAstObject.lines.add(i);
                    i++;
                }

                if (data.charAt(i) == '(') {
                    functionAstObject.lines.add(i);
                    i++;
                    while (i < data.length() && data.charAt(i) != ')') {
                        FunctionParams params = new FunctionParams();
                        while (i < data.length() && Character.isWhitespace(data.charAt(i))) {
                            i++;
                        }
                        while (i < data.length() && Character.isLetterOrDigit(data.charAt(i))
                                && data.charAt(i) != ',') {
                            params.type += data.charAt(i);
                            functionAstObject.lines.add(i);
                            i++;
                        }

                        while (i < data.length() && Character.isWhitespace(data.charAt(i))) {
                            i++;
                        }
                        if (data.charAt(i) != ',') {
                            params.name += data.charAt(i);
                            functionAstObject.lines.add(i);
                        }
                        i++;
                        functionAstObject.params.add(params);
                    }
                    i++;
                }
                while (i < data.length() && Character.isWhitespace(data.charAt(i))) {
                    i++;
                }
                if (data.charAt(i) == '{') {
                    StringBuilder body = new StringBuilder();
                    i++;
                    while (i < data.length() && Character.isWhitespace(data.charAt(i))) {
                        i++;
                    }
                    int bracketCount = 1;
                    while (i < data.length() && bracketCount > 0) {
                        char c = data.charAt(i);
                        if (c == '{') {
                            bracketCount++;
                        } else if (c == '}') {
                            bracketCount--;
                            if (bracketCount == 0) {
                                break;
                            }
                        }
                        body.append(c);
                        i++;
                    }

                    String _func_data_ = body.toString();
                    int u = 0;
                    while (u < _func_data_.length()) {
                        char _charString = _func_data_.charAt(u);
                        if (_func_data_.substring(u, Math.min(u + 6, _func_data_.length())).equals("return")) {
                            u += 6;
                            while (u < _func_data_.length() && Character.isWhitespace(_func_data_.charAt(u))) {
                                u++;
                            }
                            StringBuilder returnExpression = new StringBuilder();
                            while (u < _func_data_.length() && _func_data_.charAt(u) != ';') {
                                returnExpression.append(_func_data_.charAt(u));
                                u++;
                            }

                            String returnStatement = returnExpression.toString().trim();
                            AstObject node = parseOperatorStatement(returnStatement, tree, new StringMethods().isString(returnStatement));
                            functionAstObject.params.forEach((p) -> {
                                if (node.left.equals(p.name) || node.right.equals(p.name)) {
                                    node.function = functionAstObject;
                                }
                            });
                            AstObject _return = new AstObject();
                            _return.type = "return_statement";
                            _return.Scoped = true;
                            _return.parentBind = functionAstObject.name;
                            _return.name = "return_statement";
                            _return.children.add(node);
                            functionAstObject.children.add(_return);
                        }
                        u++;

                    }

                    tree.children.add(functionAstObject);
                    this.generateTree(_func_data_, tree);

                }
                i++;
            } else {
                i++;
            }
        }
    }

    public AstObject parseOperatorStatement(String statement, AstObject tree, Boolean include_whitespaces) {
        keyword key = new keyword();
        AstObject node = new AstObject();
        node.name = "operator_statement";
        node.type = "$op";
        char op = '+'; // Default operator
        int operatorIndex = -1; // Default index
        StringBuilder left = new StringBuilder();
        ArrayList<String> indexs = new ArrayList<>(Collections.nCopies(statement.length(), null));
        Boolean hasOperator = false;
        for (int kk = 0; kk < key.op_keywords.length; kk++) {
            String i = "";
            i += key.op_keywords[kk];

            if (statement.contains(i)) {
                indexs.set(kk, i);
                hasOperator = true;
            }
        }
        if (!hasOperator) {
            node.value = statement;
            return node;
        }
        ArrayList<String> Stack = new ArrayList<>();
        for (int jj = 0; jj < statement.length(); jj++) {
            char current = statement.charAt(jj);
            int shouldSkip = 0;
            for (int kk = 0; kk < key.op_keywords.length; kk++) {
                if (current == key.op_keywords[kk]) {
                    shouldSkip = 1;
                    break;
                } else {
                    shouldSkip = 0;
                }
            }
            if (!include_whitespaces && Character.isWhitespace(current))
                continue;
            Stack.add(String.valueOf(current));

        }
        node.opperands = Stack;
        node.value = statement;

        return node;
    }

}

/**
 * Class to transpile ast tree and run it;
 */

class Transpiler {
     

    public boolean isOperator(String item) {
        keyword keys = new keyword();
        Boolean isoperator = false;
        for (char kw : keys.op_keywords) {
            if (String.valueOf(kw).equals(item)) {
                isoperator = true;
            }
        }
        return isoperator;
    }

    /*
     * @description simple way to check type of string
     */
    public String Typeof(String value) {
        String type = "string";
        try {
            Float.parseFloat(value);
            if (value.contains(String.valueOf('.')))
                type = "float";
            else
                type = "integer";
            return type;
        } catch (Exception e) {
        }
        try {
            Double.parseDouble(value);
            type = "double";
            return type;
        } catch (Exception e) {
        }
        try {
            Boolean.parseBoolean(value);
            type = "bool";
            return type;
        } catch (Exception e) {
        }
        return type;
    }

    private String ParseOperandAsInteger(ArrayList<String> opperands, AstObject node){  
        int fullvalue = 0; 
        if (opperands.size() > 0) { 
            int lastvalue = 0; 
            String operator = ""; 
            ArrayList<String> results = new ArrayList<>(); // Store results of each group

            String ss = "";
            for (int i = 0; i < opperands.size(); i++) {
                String item = opperands.get(i);

                if (isOperator(item)) {
                    operator = item;
                    if (!ss.isEmpty()) {
                        results.add(ss);
                        ss = "";
                    }
                } else {
                    ss += item;
                }
                if (i ==  opperands.size() - 1
                        || isOperator(opperands.get(i + 1))) {
                    results.add(ss);
                    ss = "";
                }
            }
            int current = 1;

            for (int i = 0; i < results.size(); i++) {
                String value = String.valueOf(results.toArray()[i]);
                for (AstObject childAstObject : node.children) {
                    if (childAstObject.isVariable && childAstObject.Variable_Name.equals(value)) {
                        value = childAstObject.value;
                    }
                }
                int parsed = Integer.parseInt(value);

                switch (operator) {
                    case "*":
                        current *= parsed;
                        lastvalue = parsed;
                        break;
                    case "/":
                        if (i == 0) {
                            current = parsed;
                        } else {
                            if (parsed != 0) {
                                current /= Math.abs(parsed);
                            } else {
                                break;
                            }
                        }
                        break;
                    case "-":
                        current -= parsed;
                        break;
                    case "+":
                        current += parsed;
                    case "%":
                    if (i == 0) {
                        current = parsed;
                    }else{

                        current %= parsed;
                    } 
                    case ">":  
                    if (i == 0) {
                        current = parsed;
                    }else{

                       if( current >= parsed) current = 1;
                       else current = 0;
                    } 
                    break;
                    case "<":
                    if (i == 0) {
                        current = parsed;
                    }else{

                       if( current <= parsed) current = 1;
                       else current = 0;
                    } 
                    default:
                        break;
                }
            }
            fullvalue = current;
             
        } 
        return  String.valueOf(Math.abs(fullvalue));
    }
    /**
     * @description - Allows you to parse an  asignment arraylist to a value
     * @param opperands
     * @param node
     * @param include_white_spaces
     * @return
     */
    private String ParseOperandAsString(ArrayList<String> opperands, AstObject node){  
        String fullvalue =  ""; 
        if (opperands.size() > 0) { 
            int lastvalue = 0; 
            String operator = ""; 
            ArrayList<String> results = new ArrayList<>(); // Store results of each group

            String ss = "";
            for (int i = 0; i < opperands.size(); i++) {
                String item = opperands.get(i); 
                if (isOperator(item)) {
                    operator = item; 
                    results.add(ss);
                    ss = "";
                } else if(!item.isEmpty()){ 
                    ss += item;
                }
                if (i ==  opperands.size() - 1
                        || isOperator(opperands.get(i + 1))) {
                    results.add(ss);
                    ss = "";
                }
            }
            String current = "";

            for (int i = 0; i < results.size(); i++) {
                String value = String.valueOf(results.toArray()[i]);
                for (AstObject childAstObject : node.children) {
                    if (childAstObject.isVariable && childAstObject.Variable_Name.equals(value.trim())) {
                        value = childAstObject.value;
                    }
                } 
                switch (operator) {
                    case "*": 
                         // throw an error
                        break;
                    case "/":
                         // maybe able to calculate length devided by second string length?
                        break;
                    case "-": 
                        break;
                    case "+":
                        current += "" + value;
                    case "%":
                     
                    case ">":  
                     
                    break;
                    case "<": 
                    default:
                        break;
                }
            }
            fullvalue = current;
             
        } 
        fullvalue = fullvalue.replaceAll("\"", "").replaceAll("'", "").trim();
        if(fullvalue.contains("\\n")){
            fullvalue = fullvalue.replace("\\n", "\n");
        }
        return fullvalue;
    }

    /**
     * @description - Transpile an ast tree down to java spec
     * @param node
     */
    public void transpile(AstObject node, String code, String filename) {
        keyword _keywords = new keyword();
        Errors err = new Errors();
        node.children.forEach((c) -> {
            if (c.name == "int32_variable") {
                GenerateAstTree g = new GenerateAstTree(); 
                AstObject parsedAstObjectInteger = g.parseOperatorStatement(c.value, c, false);
                for (String k : _keywords.system_keywords) {
                    if (c.value.contains(k)) {
                        int index = code.indexOf(c.fullvalue);
                        err.handler(err.variable_contains_keywords, filename, index, filename, true); 
                    }
                }
                if (new StringMethods().isString(c.value)) {
                    int index = code.indexOf(c.fullvalue);
                    err.handler(err.variable_is_integer_but_contains_string, filename, index, filename, true); 
                }
                if (parsedAstObjectInteger.opperands.size() > 0 && !new StringMethods().isString(c.value)) { 
                    c.value = ParseOperandAsInteger(parsedAstObjectInteger.opperands, node); 
                } else if(!new StringMethods().isString(c.value)){
                    for(AstObject ch : node.children){
                         if(ch.Variable_Name.equals(c.value)){ 
                            c.value = ch.value;
                         } 
                    }
                    c.value = String.valueOf(Integer.parseInt(c.value));
                }else{
                    System.out.println(c.value);
                }
            } else if (c.type == "print_statement") {

                if (!c.children.isEmpty() && c.children.get(0).type.equals("$op")) {
                    AstObject child = c.children.get(0);  
                    if(child.type == "$op" && !new StringMethods().isString(c.value)){
                        System.out.println(ParseOperandAsInteger(c.children.get(0).opperands, node));
                    }else{
                        System.out.println(ParseOperandAsString(c.children.get(0).opperands, node));
                    }
                } else {
                    if(c.children.size() > 0){
                        for (AstObject ch : c.children) { 
                            if (ch.isVariable && c.value.equals(ch.Variable_Name)) {
                                System.out.println(ch.value);
                            }
                             
                        }
                    }else{ 
                        err.handler(err.printing_null_value, filename, code.indexOf(c.fullvalue), c.fullvalue, true);
                    }
                     
                }

            }
        });
    }
}

class vscript {

    public static void main(String[] args) {
        ReadFile read = new ReadFile();
        GenerateAstTree AST = new GenerateAstTree();
        AstObject tree = new AstObject();
        tree.type = "main";
        tree.name = "root";
        String code = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toString();
            if (arg.endsWith(".v")) {
                String data = read.open(arg);
                code = data;
                AST.generateTree(data, tree);
                new Transpiler().transpile(tree, code, arg);
            }
        }
    }
}
