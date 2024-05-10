package StringMethods;
import keywords.keyword;
public class StringMethods {
    public Boolean isString(String value1) {
        if (value1.startsWith("\"") && value1.endsWith("\"") || value1.startsWith("'") && value1.endsWith("'")) {
            return true;
        } else if (value1.contains("'") || value1.contains("\""))
            return true;
        return false;
    }

    public Boolean isInteger(String val){
        try {
            Integer.parseInt(val); 
            return true;
        } catch (Exception e) {
           return false;
        }
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

    public boolean containsComparison(String code) {
        keyword keys = new keyword();
        Boolean hasoperator = false;
        for (String kw : keys.comparison) {
            if (code.contains(kw)) {
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