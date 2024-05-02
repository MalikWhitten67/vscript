import java.util.*;
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
}

class FunctionParams {
    String name = "";
    String type = "";
    String callable = "";
    String reurnable = "";
}


abstract class AstChildObject {}


/**
 * @method AstObject
 */
class AstObject {
    String type = "";
    Boolean isGlobal = false;
    Boolean isVariable = false;
    String left = "";
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
     * @description when set to true check the parentBind to see what it is bound to then only parse it in that scopes reference
     */
    Boolean Scoped = false;
    /**
     * @description - the objects reference name in which the current item is bound to
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

class OperatorNode {
    String left = "";
    String right = "";
    AstObject function;
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
                _int.value = "";
                _int.lines.add(i);
                _int.isVariable = true;
                if(data.charAt(i) == ':'){
                    i++;
                    while (i < data.length() && !Character.isWhitespace(data.charAt(i))) { 
                        if(data.substring(i, Math.min(i + 6, data.length())).equals("global")){
                            _int.isGlobal = true;
                        }
                        else if(data.substring(i, Math.min(i + 7, data.length())).equals("private")){
                            _int.isGlobal = false;
                        }
                        i++;
                    }
                }

                // Find variable name
                while (i < data.length() && Character.isWhitespace(data.charAt(i))) {
                    i++;
                }
                while (i < data.length() && Character.isLetterOrDigit(data.charAt(i))) {
                    _int.Variable_Name += data.charAt(i++);
                    _int.lines.add(i);
                }

                // Find value
                while (i < data.length() && Character.isWhitespace(data.charAt(i))) {
                    i++;
                }

                if (i < data.length() && data.charAt(i) == '=') {
                    i++;
                    while (i < data.length() && data.charAt(i) != ';') {
                        _int.value += data.charAt(i++);
                        _int.lines.add(i);
                    }
                }
                _int.lines.add(i);
                _int.value = _int.value.trim();
                tree.children.add(_int);

            } 
            else if(data.substring(i, Math.min(i + 5, data.length())).equals("print")){
                i+= 5;
                AstObject _print_statement = new AstObject();
                _print_statement.type = "print_statement";
                _print_statement.name = "print";
                if(data.charAt(i) == '(') i++;
                while (i < data.length() && data.charAt(i) != ')'){
                    for(AstObject k : tree.children){
                        StringBuilder v = new StringBuilder(); 
                        v.append(data.charAt(i));
                        if(k.name.contains(v.toString()) && k.isVariable){
                            _print_statement.value = k.value; 
                        }
                    } 
                    i++;
                }
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
                            AstObject node = parseReturnStatement(returnStatement); 
                            functionAstObject.params.forEach((p)->{ 
                                if(node.left.equals(p.name) || node.right.equals(p.name)){
                                    node.function = functionAstObject;
                                }
                            }); 
                            AstObject  _return = new AstObject();
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

    private AstObject parseReturnStatement(String statement){
        statement  = statement.trim().replaceAll("\\s+", ""); 
        AstObject node = new AstObject(); 
        char[] keywords = {'+', '-', '*', '/', '<', '>', '%', '&'};
        for(int i = 0; i < statement.length(); i++){
            char current = statement.charAt(i);
            for(char kw : keywords){
                if(current == kw){
                  String left = statement.substring(0,i);
                  String right = statement.substring(i + 1); 
                  node.left = left;
                  node.right = right;
                  node.operator = kw; 
                }
            }
        }
        return node;
    }
}

class Transpiler{
    public void transpile(AstObject node){
       node.children.forEach((c)->{ 
         if(c.type == "int32"){
            System.out.println(c.value);
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
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toString();
            if (arg.endsWith(".v")) {
                String data = read.open(arg);
                AST.generateTree(data, tree);
            }
        }
        new Transpiler().transpile(tree);
    }
}