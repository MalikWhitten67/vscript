import java.util.*; 
import ErrorHandler.Errors;
import keywords.*; 
import java.io.*;

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
                i += 5;
                keyword keys = new keyword();
                AstObject _print_statement = new AstObject();
                _print_statement.type = "print_statement";
                _print_statement.name = "print";
                String _print_body = "";
                if (data.charAt(i) == '(')
                    i++;
                while (i < data.length() && data.charAt(i) != ')') {
                    _print_body += data.charAt(i);
                    i++;
                }
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
                    _print_statement.children.add(parseOperatorStatement(_print_body, tree));
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
                            AstObject node = parseOperatorStatement(returnStatement, tree);
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

    public AstObject parseOperatorStatement(String statement, AstObject tree) {
        keyword key = new keyword();
        AstObject node = new AstObject();
        node.name = "operator_statement";
        node.type = "$op";
        char op = '+'; // Default operator
        int operatorIndex = -1; // Default index
        StringBuilder left = new StringBuilder();
        OperatorAssignmentNode node2 = new OperatorAssignmentNode();
        /**
         * @todo  5/3/24
         * @manage - Possibly make it possible to add multiple operators
         */
        Boolean hasOperator = false;
        for(int kk = 0; kk < key.op_keywords.length; kk++){
           String i = "";
           i+= key.op_keywords[kk];
           
           if(statement.contains(i)){ 
              node2.indexs.add(kk - 2,i);
              hasOperator = true;
           }
        }  
        if(!hasOperator){
            node.value = statement;
            return node;
        }
        for (int i = 0; i < statement.length(); i++) {
            char current = statement.charAt(i);

            for (int kk = 0; kk < key.op_keywords.length; kk++) {
                if (current == key.op_keywords[kk]) {
                    op = key.op_keywords[kk];
                    operatorIndex = i; // Update the operator index
                    i++; 
                    continue;
                }else{
                    continue;
                }
            } 
            left.append(current);

        }  
        node2.indexs.forEach((i)->{ 
            for (int jj = 0; jj < statement.length(); jj++) {
                char current = statement.charAt(jj);
                int shouldSkip = 0;
                ArrayList<String> Indices = new ArrayList<>();
                for (int kk = 0; kk < key.op_keywords.length; kk++) {
                    if (current == key.op_keywords[kk]) {  
                        shouldSkip = 1;  
                        break;
                    } else{
                        shouldSkip = 0;
                    }
                }  

                if(shouldSkip == 1){  
                    Indices.add(jj -2, String.valueOf(current));
                    continue;
                }
                
                System.out.println(current);
                OperatorNode nOperatorNode = new OperatorNode(); 
    
            }  
        });
        StringBuilder right = new StringBuilder();
        for (int i = statement.indexOf(op) + 1; i < statement.length(); i++) {
            char current = statement.charAt(i);
            right.append(current);
        }
        node.right = right.toString().trim();
        node.left = left.toString().trim(); 
        // check if current left or right assignment is a variable call;
        for (AstObject c : tree.children) {
            if (c.Variable_Name.equals(node.right.trim()) || c.Variable_Name.equals(node.left.trim()))
                node.isVariable = true;
        }
        node.operator = op;
        node.value = statement;
        
        return node;
    }

}

/**
 * Class to transpile ast tree and run it;
 */

class Transpiler {
    public Boolean isString(String value1) {
        if (value1.startsWith("\"") && value1.endsWith("\"") || value1.startsWith("'") && value1.endsWith("'")) {
            return true;
        } else if (value1.contains("'") || value1.contains("\""))
            return true;
        return false;
    }


    /*
     * @description simple way to check type of string
     */
    public String Typeof(String value){
        String type = "string";
        try {
            Float.parseFloat(value); 
            if(value.contains(String.valueOf('.'))) type = "float";
            else type = "integer"; 
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
                AstObject parsedAstObject = g.parseOperatorStatement(c.value, c); 
                for (String k : _keywords.system_keywords) {
                    if (c.value.contains(k)) {
                        int index = code.indexOf(c.fullvalue);
                        System.out.print(err.variable_contains_keywords + " \n \tat: " + filename + ":" + index + "\n"
                                + "Line error -> " + c.fullvalue);
                        System.exit(0);
                    }
                }
                if (isString(c.value)) {
                    int index = code.indexOf(c.fullvalue);
                    System.out.print(
                            err.variable_is_integer_but_contains_string + " \n \tat: " + filename + ":" + index + "\n"
                                    + "Line error -> " + c.fullvalue);
                    System.exit(0);
                }
                if(String.valueOf(parsedAstObject.operator).length() > 0 ){
                    for (AstObject childObject : node.children) {
                        Boolean isString = isString(childObject.value);
                        if (childObject.isVariable && parsedAstObject.left.trim().contains(childObject.Variable_Name.trim())  && String.valueOf(parsedAstObject.operator).length() > 0) { 
                            for (AstObject variable : node.children) {
                                if (variable.Variable_Name.equals(parsedAstObject.left.trim())) { 
                                    parsedAstObject.left = variable.value;
                                }
                            } 
                        } else if (childObject.isVariable && parsedAstObject.right.equals(childObject.Variable_Name.trim())) { 
                            if (isString)
                                childObject.value = childObject.value.replaceAll("'", "").replaceAll("\"", "");
                            for (AstObject variable : node.children) {
                                if (variable.Variable_Name.equals(parsedAstObject.right.trim())) {
                                    parsedAstObject.right = variable.value;
                                }
                            }
                        }
    
                    }  
                    if (String.valueOf(parsedAstObject.operator).trim().length() > 0) { 
                        switch (parsedAstObject.operator) {
                            case '+':
                                c.value = String.valueOf(
                                        Integer.parseInt(parsedAstObject.left) + Integer.parseInt(parsedAstObject.right)); 
                                break;
                            case '-':
                                c.value = String.valueOf(
                                        Integer.parseInt(parsedAstObject.left) - Integer.parseInt(parsedAstObject.right));
                                break;
                            case '*':
                                c.value = String.valueOf(
                                        Integer.parseInt(parsedAstObject.left) * Integer.parseInt(parsedAstObject.right));
                                break;
                            case '/':    
                            c.value = String.valueOf(Integer.parseInt(parsedAstObject.left) / Integer.parseInt(parsedAstObject.right)); 
                                break;
                            case '%':
                             c.value = String.valueOf(Math.floor(Integer.parseInt(parsedAstObject.left) * Integer.parseInt(parsedAstObject.right) / 100));  
                            default:
                                break;
                        }  
                    }else{ 
                        c.value = String.valueOf(Integer.parseInt(c.value));
                    }
                }else{ 
                }
            } else if (c.type == "print_statement") {
 
                if (!c.children.isEmpty() && c.children.get(0).type.equals("$op")) {
                    AstObject child = c.children.get(0); 
                    for (char kw : _keywords.op_keywords) { 
                        if(child.value.contains(String.valueOf(kw))){
                            GenerateAstTree g = new GenerateAstTree();
                            AstObject parsedAstObject = g.parseOperatorStatement(child.value, c);  
                            for(AstObject _chilAstObject : c.children){ 
                                if(_chilAstObject.isVariable && _chilAstObject.Variable_Name.equals(parsedAstObject.left)){    
                                    parsedAstObject.left = _chilAstObject.value;
                                }else if (_chilAstObject.isVariable && c.Variable_Name.equals(parsedAstObject.right)){ 
                                    parsedAstObject.right = _chilAstObject.value;
                                } 

                                
                            }   
                            
                            String typeofLeft = Typeof(parsedAstObject.left);
                            String typeofRight = Typeof(parsedAstObject.right); 
                            System.out.println(typeofLeft);
                            // will clean up later
                            switch (child.operator) {
                                case '+':  
                                    if(typeofLeft.equals("integer") && typeofRight.equals("integer")){
                                        System.out.println(Integer.parseInt(parsedAstObject.left) + Integer.parseInt(parsedAstObject.right));
                                    }else if(typeofLeft.equals("float") && typeofRight == "integer" || typeofRight == "float" && typeofLeft == "integer"){
                                        float left = typeofLeft == "integer"  ? Integer.parseInt(parsedAstObject.left) : Float.parseFloat(parsedAstObject.left);
                                        float right = typeofRight == "integer" ? Integer.parseInt(parsedAstObject.right) : Float.parseFloat(parsedAstObject.right);
                                        System.out.println(left + right);
                                    }
                                    break;
                                case '-':
                                    if(typeofLeft.equals("integer") && typeofRight.equals("integer")){
                                        System.out.println(Integer.parseInt(parsedAstObject.left) - Integer.parseInt(parsedAstObject.right));
                                    }else if(typeofLeft.equals("float") && typeofRight == "integer" || typeofRight == "float" && typeofLeft == "integer"){
                                        float left = typeofLeft == "integer"  ? Integer.parseInt(parsedAstObject.left) : Float.parseFloat(parsedAstObject.left);
                                        float right = typeofRight == "integer" ? Integer.parseInt(parsedAstObject.right) : Float.parseFloat(parsedAstObject.right);
                                        System.out.println(left - right);
                                    }
                                    break;
                                case '*':
                                if(typeofLeft.equals("integer") && typeofRight.equals("integer")){
                                    System.out.println(Integer.parseInt(parsedAstObject.left) * Integer.parseInt(parsedAstObject.right));
                                }else if(typeofLeft.equals("float") && typeofRight == "integer" || typeofRight == "float" && typeofLeft == "integer"){
                                    float left = typeofLeft == "integer"  ? Integer.parseInt(parsedAstObject.left) : Float.parseFloat(parsedAstObject.left);
                                    float right = typeofRight == "integer" ? Integer.parseInt(parsedAstObject.right) : Float.parseFloat(parsedAstObject.right);
                                    System.out.println(left * right);
                                }

                                default:
                                    break;
                            }
                            
                        }
                    }
                } else {
                    for (AstObject ch : c.children) {
                        if (ch.isVariable && c.value.equals(ch.Variable_Name)) {
                            System.out.println(ch.value);
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
                AST.generateTree(data, tree);
                new Transpiler().transpile(tree, code, arg);
            }
        }
    }
}
