import java.util.*;
import java.util.stream.Collectors;

import ErrorHandler.Errors;
import keywords.*;
import java.io.*;

class StringMethods {
    public Boolean isString(String value1) {
        if (value1.startsWith("\"") && value1.endsWith("\"") || value1.startsWith("'") && value1.endsWith("'")) {
            return true;
        } else if (value1.contains("'") || value1.contains("\""))
            return true;
        return false;
    }

    public boolean containsOperator(String code) {
        keyword keys = new keyword();
        Boolean hasoperator = false;
        for (char kw : keys.op_keywords) {
            if (code.contains(String.valueOf(kw))) {
                hasoperator = true;
            }
        }
        return hasoperator;
    }

    public boolean isFunction(String code){
        boolean yes = false;
        if(!new StringMethods().isString(code) && !new StringMethods().containsOperator(code)
        && code.contains("(") && code.contains(")")
        ){
           yes = true;
        }else{
            yes = false;
        }
        return yes;
    }

    public String parse(String code){
        return code.replaceAll("\"", "").replaceAll("'", "").replace("\\n",  " ");
    }

}

class Utils{
    public void runFunction(AstObject childAstObject){
        
        AstObject tree2 =  new AstObject();
        tree2.name = childAstObject.Variable_Name + "function_call";
        tree2.children = childAstObject.children;
        tree2.opperands = childAstObject.opperands;
        tree2.params = childAstObject.params;
        tree2.rawParams = childAstObject.rawParams;
        new GenerateAstTree().generateTree(childAstObject.fullvalue, tree2, true, childAstObject); 
        new Transpiler().transpile(tree2, childAstObject.fullvalue.trim(), tree2.name);
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
    int index = 0;
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
     * @description boolean value to chek if a variable is a function returning the
     *              type
     */
    Boolean isFunc = false;
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
    ArrayList params = null;

    ArrayList<AstObject> rawParams = new ArrayList<>();
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
    public void generateTree(String data, AstObject tree, Boolean isInFunction, AstObject function ) {
        class Search {
            public AstObject child(String name) {
                for (AstObject child : tree.children) {
                    if (child.isVariable && child.name.equals(name)) { 
                        return child;
                    }
                }
                return null;
            }
            
        } 
        int i = 0;
        while (i < data.length()) {
            String token = data.substring(i, Math.min(i + 3, data.length()));
            if (token.equals("int") && !data.substring(i, Math.min(i + 5, data.length())).equals("int32")) {

                i += 3;
                AstObject _int = new AstObject(); 
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
                } else {
                    _int.Scoped = true;
                    _int.isFunc = true;
                    int braceCount = 1;
                    StringBuilder paramData = new StringBuilder();
                    while (i < data.length() && data.charAt(i) != '(') {
                        char c = data.charAt(i);
                        _int.Variable_Name += c;
                        i++;
                    }
                    while (i < data.length() && braceCount > 0 && data.charAt(i) != '{') {
                        char c = data.charAt(i);
                        if (c == '(') {
                            braceCount++;
                        } else if (c == ')') {
                            braceCount--;
                            if (braceCount == 0)
                                break;
                        }
                        paramData.append(c);
                        i++;
                    } 

                    StringBuilder body = new StringBuilder();
                    int bracketCount = 1;
                    i++; 
                    while (i < data.length() && bracketCount > 0 && data.charAt(i) != '}') {
                        char c = data.charAt(i);
                         
                        if (c == '{') {
                            braceCount++;
                        } else if (c == '}') { 
                            bracketCount--;
                            if (bracketCount == 0)
                                break;
                        } 
                        body.append(c);
                        i++;
                    }   

                    String functionBody = "";
                    if(body.toString().contains("return")){
                        functionBody = "return" + body.toString().split("return")[1].split(";")[0].trim();
                    } 
                    _int.returnedValue = functionBody;
                     
                    if(new StringMethods().containsOperator(functionBody)){    
                       AstObject d = parseOperatorStatement(functionBody, tree, true);  
                       _int.children.add(d);
                       _int.opperands = d.opperands;
                    }  
                    _int.fullvalue = body.toString(); 
                    String name = ""; 
                    Boolean inParentheses = false;
                    for (int _params = 0; _params <= paramData.length(); _params++) {
                        char _token = _params < paramData.length() ? paramData.toString().charAt(_params) : ','; // Add ',' to process the last parameter 
                        if (_token == '('){ 
                            inParentheses = true; 
                        }
                        else if (_token == ')')
                            inParentheses = false;
                        
                        
                        if (_token != ',' && _token != ')' && _token != '(' && !Character.isWhitespace(token.charAt(0)) ) {  
                            name += _token; 
                        } else {
                            if (_token == ',') {  
                                AstObject p = new AstObject(); 
                                if(name.contains("int")){
                                    name = name.split("int")[1];
                                }
                                p.name = name.trim(); 
                                p.type = "function_param";
                                p.Scoped = true;
                                p.isGlobal = false;
                                p.function = _int;
                                p.value = String.valueOf(0);  
                                _int.rawParams.add(p); 
                                _int.children.add(p);
                                name = ""; 
                            }  
                        }
                    }

                }  

                // parse printbody
                
                if(!_int.isFunc){
                    _int.name = "int32_variable";
                }else{
                    _int.name = "int32_function";
                }
                _int.lines.add(i); 
                _int.value = _int.value.trim();
                _int.parentBind = "root";
                if(isInFunction){
                    _int.Scoped = true;
                    _int.parentBind = function.Variable_Name;
                    function.children.add(_int);
                }else{ 
                   tree.children.add(_int);
                }

            } else if (data.substring(i, Math.min(i + 5, data.length())).equals("print")) {
                keyword keys = new keyword();
                AstObject _print_statement = new AstObject();
                _print_statement.fullvalue += data.substring(i, Math.min(i + 5, data.length()));
                i += 5;
                _print_statement.type = "print_statement";
                _print_statement.name = "print";
                _print_statement.parentBind = "root";
                if(isInFunction){
                    _print_statement.Scoped = true; 
                    _print_statement.function = function;
                    _print_statement.parentBind = function.Variable_Name; 
                }
                StringBuilder _print_body = new StringBuilder();
                char currentChar;
                
                if (data.charAt(i) == '(') {
                    _print_statement.fullvalue += '(';
                    i++;
                }
                
                while (i < data.length() && (currentChar = data.charAt(i)) != ')') {
                    _print_body.append(currentChar);
                    _print_statement.fullvalue += currentChar;
                    i++;
                }

                while (i < data.length() && i != data.lastIndexOf(')')) {
                    _print_body.append(data.charAt(i));
                    _print_statement.fullvalue += data.charAt(i);
                    i++; 
                }    
                Boolean hasOperator = false;
                for (char k : keys.op_keywords) {
                    StringBuilder str = new StringBuilder();
                    str.append(k);
                    if (_print_body.toString().contains(str.toString())) {
                        hasOperator = true;
                    }
                } 
                if(isInFunction){
                    _print_statement.Scoped = true;
                    _print_statement.function = tree;
                }
                Boolean CntainsFunction = false;
                for(AstObject ch : tree.children){ 
                    if(!new StringMethods().isString(_print_body.toString()) && ch.name.equals("int32_function") && _print_body.toString().contains(ch.Variable_Name)){
                       CntainsFunction = true;
                    }
                }  
                if (hasOperator) {
                    Boolean isstring = new StringMethods().isString(_print_body.toString());  
                    _print_statement.children.add(parseOperatorStatement(_print_body.toString(), tree, isstring));
                } 
                else{
                    _print_statement.value = _print_body.toString();
                    Boolean hasFunction = false;
                    int index = 0;
                    for (AstObject _child : tree.children) {
                        if (!new StringMethods().isString(_print_body.toString()) && _print_body.toString().contains(_child.Variable_Name) && _child.name.contains("function")) {
                            ArrayList<AstObject> fpArrayList = new ArrayList<>();
                            String valueString = "";
                            int it = _child.Variable_Name.length(); // Start from the end of the function name
                    
                            while (it < _print_body.length()) {
                                char t = _print_body.charAt(it); 
                                if (t == ')' || t == '(' ) {
                                    it++;  
                                    continue;
                                }
                    
                                if (t != ',') {
                                    valueString += t; // Accumulate characters for valueString
                                } else { 
                                    index++;    
                                    AstObject print_AstObject = new AstObject();
                                    print_AstObject.type = "print_function_param";
                                    print_AstObject.value = valueString.trim(); // Remove leading/trailing spaces
                                    print_AstObject.index = index - 1;
                                    print_AstObject.function = _child;  
                                    print_AstObject.Scoped = true;
                                    _print_statement.children.add(print_AstObject); 
                                    valueString = "";
                                }
                                it++;  
                            }
                     
                            if (!valueString.isEmpty()) {
                                index++;
                                AstObject print_AstObject = new AstObject();
                                print_AstObject.type = "print_function_param";
                                print_AstObject.value = valueString.trim();
                                print_AstObject.index = index - 1; 
                                print_AstObject.function = _child; 
                                print_AstObject.Scoped = true;
                                _print_statement.children.add(print_AstObject);
                            }
                        }
                    }
                }
                for (AstObject child : tree.children) {
                    if (child.isVariable && _print_body.toString().contains(child.Variable_Name)) {
                        _print_statement.children.add(child);
                    }
                } 
               
                
                if (!tree.children.contains(_print_statement))  tree.children.add(_print_statement);

                i++;
            } 

            else if (data.substring(i, Math.min(i + 6, data.length())).equals("import")) {
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
            }  else{
                StringBuilder str = new StringBuilder(); 
                for(AstObject childAstObject : tree.children){  
                    if(childAstObject.name.contains("function")){
                        String name = String.valueOf(data.substring(i, Math.min(i + childAstObject.Variable_Name.length(), data.length())));
                        ArrayList<String> params = new ArrayList<>();
                        if(name.equals(childAstObject.Variable_Name)){
                            String code =  ""; 
                            while (i < data.length() && data.charAt(i) != ';') {
                                code += data.charAt(i);
                                i++;
                            }   
                            for(String p : code.replace(name, "").split(",")){
                                  p = p.trim().replace("(", "").replace(")", "");
                                 params.add(p);
                            }
                            if(new StringMethods().isFunction(code.toString())){    
                                childAstObject.params = params;
                                new Utils().runFunction(childAstObject); 
                            }
                        }
                    }
                }
                i++;
            }
        }
    }

    public AstObject parseOperatorStatement(String statement, AstObject tree, Boolean include_whitespaces) { 
        statement = statement.trim();
        keyword key = new keyword();
        AstObject node = new AstObject();
        node.name = "operator_statement";
        node.type = new StringMethods().isString(statement) ? "$op_string" : "$op";
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
        if (!new StringMethods().containsOperator(statement)){
            node.value = statement; 
            return node;
        }
        ArrayList<String> Stack = new ArrayList<>();
        int j = 0; 
        if(statement.substring(j, Math.min(j + 6, statement.length())).equals("return")){
            statement = statement.trim().substring(6, statement.length());
        }
        
        for (int jj = 0; jj < statement.length(); jj++) {
            char current = statement.charAt(jj); 
            if (!include_whitespaces && Character.isWhitespace(current)) { 
                continue;
            } else{

                Stack.add(String.valueOf(current).trim());
            } 

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

    private String parseOperandFunctionAsString(ArrayList<String> opperands, AstObject function){
          // todo make this parse operands as functions then return the proper value types;
          return "";
    }
    private String parseOperandFunctionAsInteger(ArrayList<String> opperands, AstObject funObject, AstObject tree){ 
        int fullValue  = 0;
        if(opperands.size() > 0){  
            ArrayList<String> results = new ArrayList<>();
            String ss = ""; 
            for(int i = 0; i < opperands.size(); i++){
                String item = opperands.get(i);
                if(isOperator(item)){
                    if(!ss.isEmpty()){
                        results.add(ss);
                        ss = "";
                    } 
                    results.add(item);
                }
                else{
                    ss += item;
                }
                if (i == opperands.size() - 1) {
                    results.add(ss);
                }
            } 
            int current = 0;
            int index = 0; 
            while (current < results.size()) {
                String value = results.get(current); 
                int intValue = 0; 
                if(!new StringMethods().isString(value)){
                    index++; 
                    intValue = Integer.parseInt(String.valueOf(funObject.params.toArray()[index - 1]));
                    for(AstObject child : tree.children){
                        if(child.Variable_Name.equals(funObject.name)){
                            AstObject c = child.children.get(index);
                            c.value = String.valueOf(intValue);
                        }
                    } 
                } 
                if (current == 0) {
                    fullValue = intValue;
                } else { 
                    String operator = results.get(current - 1);    

                    switch (operator) {
                        case "+":
                            fullValue += intValue;
                            break;
                        case "-":
                            fullValue -= intValue;
                            break;
                        case "*":
                            fullValue *= intValue;
                            break;
                        case "/":
                            if (intValue != 0)
                                fullValue /= intValue;
                            else
                                System.out.println("Division by zero error!");
                            break;
                        case "%":
                            if (intValue != 0)
                                fullValue %= intValue;
                            else
                                System.out.println("Supressed by zero error!");
                            break;
                        default:
                            break;
                    }
                }
                current += 2; 
            }
        }
        return String.valueOf(fullValue);
  }
    private String ParseOperandAsInteger(ArrayList<String> operands, AstObject node) {
        int fullValue = 0;
        if (operands.size() > 0) {
            ArrayList<String> results = new ArrayList<>();
            String ss = "";

            for (int i = 0; i < operands.size(); i++) {
                String item = operands.get(i);

                if (isOperator(item)) {
                    if (!ss.isEmpty()) {
                        results.add(ss);
                        ss = "";
                    }
                    results.add(item);
                } else {
                    ss += item;
                }

                if (i == operands.size() - 1) {
                    results.add(ss);
                }
                
            }
            int current = 0;
 
            while (current < results.size()) {
                String value = results.get(current); 
                int intValue = 0;
                for (AstObject childAstObject : node.children) {
                    if (childAstObject.isVariable && childAstObject.Variable_Name.equals(value)) {
                        value = childAstObject.value; 
                        break;
                    }  
                    if(childAstObject.type.equals("print_function_param") && childAstObject.name.trim().equals(value.trim())){ 
                        value = childAstObject.value; 
                        break;
                    }
                } 
                intValue = Integer.parseInt(value); 
                if (current == 0) {
                    fullValue = intValue;
                } else {
                    String operator = results.get(current - 1);

                    switch (operator) {
                        case "+":
                            fullValue += intValue;
                            break;
                        case "-":
                            fullValue -= intValue;
                            break;
                        case "*":
                            fullValue *= intValue;
                            break;
                        case "/":
                            if (intValue != 0)
                                fullValue /= intValue;
                            else
                                System.out.println("Division by zero error!");
                            break;
                        case "%":
                            if (intValue != 0)
                                fullValue %= intValue;
                            else
                                System.out.println("Supressed by zero error!");
                            break;
                        default:
                            break;
                    }
                }
                current += 2;
            }
        } 
        return String.valueOf(Math.abs(fullValue)).trim();
    }

    /**
     * @description - Allows you to parse an asignment arraylist to a value
     * @param opperands
     * @param node
     * @param include_white_spaces
     * @return
     */
    private String ParseOperandAsString(ArrayList<String> opperands, AstObject node) {
        String fullvalue = "";
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
                } else if (!item.isEmpty()) {
                    ss += item;
                }
                if (i == opperands.size() - 1
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
        if (fullvalue.contains("\\n")) {
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
            if(c.name == "int32_function"){ 
            }
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
                } else if (!new StringMethods().isString(c.value)) {
                    for (AstObject ch : node.children) {
                        if (ch.Variable_Name.equals(c.value)) {
                            c.value = ch.value;
                        }
                    }
                    c.value = String.valueOf(Integer.parseInt(c.value));
                } else {
                    System.out.println(c.value);
                }
            } else if (c.type == "print_statement") {     
                if(!c.children.isEmpty() && c.children.get(0).name.contains("int32_function") && !c.parentBind.equals("root")){
                    AstObject child = c.children.get(0);   
                    if(child.children.get(0).type.equals("$op")){
                        AstObject operatorStatements = child.children.get(0);
                        List<String> operands = child.children.get(0).opperands; 
                        int value = 0;         
                        if(new StringMethods().isString(String.valueOf(child.returnedValue))){
                            System.out.println(ParseOperandAsString(operatorStatements.opperands, node).trim());
                        }else{
                           System.out.print(ParseOperandAsInteger(operatorStatements.opperands, child).trim());
                        } 
                         
                    }else if(child.children.get(0).type.equals("$op_string")){ 
                        AstObject operatorStatements = child.children.get(0);
                        System.out.println(ParseOperandAsString(operatorStatements.opperands, node).trim());
                    }else{
                         
                    } 
                    new Utils().runFunction(child);
                } 
                else if (!c.children.isEmpty() && c.children.get(0).type.equals("$op")  &&   !c.parentBind.equals("root")) {
                    AstObject child = c.children.get(0);  
                    if (child.type == "$op" && !new StringMethods().isString(c.value)) { 
                        String fullString = "";

                        for(int i = 0; i < c.children.get(0).opperands.size(); i++){
                            String v = String.valueOf(c.children.get(0).opperands.toArray()[i]);
                            fullString += v;
                        }
                        if (!new StringMethods().isString(fullString) && fullString.contains("(")) { 
                            List<String> operands = c.children.get(0).opperands; 
                            ArrayList<List<String>> paramLists = new ArrayList<>();
                            ArrayList<String> currentParams = new ArrayList<>();
                            boolean inFunction = false;
                            int braceCount = 0;
                            String functionName = "";
                            String currentFunctionName = "";
                            String currentOperator = "";
                            for (int i = 0; i < operands.size(); i++) {
                                String token = operands.get(i).trim(); 
                                if (token.equals("(")) { 
                                    inFunction = true;
                                    braceCount++; 
                                    currentFunctionName = functionName;
                                    functionName = "";
                                } else if (token.equals(")")) { 
                                    braceCount--;
                                    if (braceCount == 0) {   
                                        AstObject FunctionD = new AstObject();
                                        FunctionD.type = "operand_function";
                                        FunctionD.name = currentFunctionName; 
                                        FunctionD.params = new ArrayList<>(currentParams);
                                        currentParams.clear();
                                        child.children.add(FunctionD);
                                        inFunction = false; 
                                        currentFunctionName = "";
                                    }
                                } else if (inFunction) { 
                                    if (!token.equals(",") && !Character.isWhitespace(token.charAt(0))) {
                                        currentParams.add(token);
                                    }
                                } else if(!new StringMethods().containsOperator(token)){ 
                                    if(!Character.isDigit(token.charAt(0))){
                                      functionName += token;
                                    }else{
                                        
                                    } 
                                }else{
                                    currentOperator = token.trim();
                                }
                            }   
                            int value = 0;
                            for (AstObject _child : child.children) { 
                                for(AstObject _child2 : node.children){ 
                                    if(_child2.Variable_Name.equals(_child.name)){  
                                        for(int k = 0; k < _child2.children.subList(1, _child2.children.size()).toArray().length; k++){
                                             AstObject _child3 = _child2.children.subList(1, _child2.children.size()).get(k);
                                            if(_child3.params != null){
                                                _child3.value = String.valueOf(_child.params.toArray()[k]);
                                            }
                                        }
                                    }
                                } 
                                for(AstObject _child2 : node.children){ 
                                    if(_child2.Variable_Name.equals(_child.name)){ 
                                        List<String> ob = _child2.children.get(0).opperands;
                                        ArrayList<String> list = new ArrayList<>(); 
                                        for(String k : ob){   
                                            list.add(String.valueOf(k).trim());
                                        }     
                                        value += Integer.parseInt(parseOperandFunctionAsInteger(list, _child, node));
                                    }
                                } 

                            }

                            System.out.println(value);
                        }
                        else{   
                            System.out.println(ParseOperandAsInteger(c.children.get(0).opperands, node));
                        } 
 
                    }   
                    
                }  
                else  if(!c.children.isEmpty() && c.children.get(0).type.equals("$op_string"))  System.out.println(ParseOperandAsString(c.children.get(0).opperands, node));
                  
                else{   
                    ArrayList<AstObject> params = new ArrayList<>();
                    if(c.children.size() > 0){
                        for(AstObject cAstObject : c.children){
                            if(cAstObject.Variable_Name.equals(c.value)){   
                                System.out.println(cAstObject.value);
                            } else if(cAstObject.type.equals("print_function_param")){ 
                                params.add(cAstObject);
                            }else if(cAstObject.name.equals("int32_function")){  
                                ArrayList<String> Opperands = new ArrayList<>();
                                if(params.size() > 0 ){
                                    int i = 0;
                                    for(AstObject param : params){ 
                                        i++;
                                        try {
                                          cAstObject.children.get(i).value = String.valueOf(param.value);
                                          Opperands.add(param.value);
                                        } catch (Exception e) {
                                           err.handler(err.one_to_many_params, filename, code.indexOf(c.fullvalue), filename, true);
                                        }
                                    }
                                }      
                                cAstObject.params = Opperands;
                                System.out.println(parseOperandFunctionAsInteger(cAstObject.opperands, cAstObject, node));
                                new Utils().runFunction(cAstObject);
                            }
                        } 
                    }else{ 
                       int index = 0; 
                       boolean exists = false;
                       for(AstObject child : c.function.rawParams){
                          index++; 
                          String val = String.valueOf(c.function.params.toArray()[index - 1]);
                          if(child.name.equals(c.value.trim())){
                            System.out.println(val);
                            exists = true;
                          }
                       } 

                       if(!exists){
                        err.handler(err.printing_null_value, filename, code.indexOf(c.fullvalue), filename, true);
                       }
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
                AST.generateTree(data, tree, false, null);
                new Transpiler().transpile(tree, code, arg);
            }
        }
    }
}
