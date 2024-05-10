package ErrorHandler;
 
public class Errors {
   public String variable_contains_keywords = "Variables cannot contain keywords within them!";
   public String variable_is_integer_but_contains_string = "Integer cannot be assigned to a variable!";
   public String printing_null_value = "Variable reference is un assigned or null!";
   public String one_to_many_params = "Specified number of params given does not match how many the function has defined";
   public String invalid_if_statement_body_missing_comparison = "Invalid if statement body missing an comparisson opperator";
   /**
    * @description - Method to throw custom errors
    * @param message - message you want to throw custom or predefined within errors class
    * @param filename - File that we are referencing
    * @param line - The line in which is at blame
    * @param at - The string of code that was messed up badly
    * @param shouldClose - Should we crash their program???
    */
   public void handler(String message, String filename,  int line, String at, Boolean shouldClose){
      System.out.print(
         message + " \n \tat: " + filename + ":" + line + "\n"
                 + "Line error -> " + at);
      if(shouldClose){
         System.exit(0);
      }
   }
}
