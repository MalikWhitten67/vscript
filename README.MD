# VSCRIPT


What is vscript?
- A staticaly typed language built ontop of java
- A project aimed to improve skillset
- A simple language


Beginner dev?

1. What will i gain reading through the code?
 - You will be able to understand how lexers work - proper usage of Abstract Syntax Tree's (AST);
 - You will be able to understand 
    - string manipulation 
    - Arrays
    - Loops
    - Common lexing lookaheads char indexing;
    - ArrayLists
    - Type checking / string parsing
    - Lexical logic


# TODO LOG
-  Improve printing values , currently we cannot add more than one operator assignment;
-  Allow function calls  / working out a scope system
-  Refacotoring - organizing different parts of the lexer / some optimizations  
# Changelog

1. 5/2/24 
 - Added print statements 
 - Parsed integer variables by their operator type;
 - Fixed issue with whitespace within left hand assignments; 
-  Allowed being able to print a variable by name but also use left to right operator assignments for calculations!
-  Fixed improper math calculation
 

# Road Map
1. Scoped (Global|Private) - this is a must have currently int can be called within any scope - it would be effective if variables are bound to the parent container and cannot be accessed outside of it! 
2. Functions
 - Being able to invoke functions within itself but also outside 
3. If Statements - the embodiment of any logic 
4. Switch cases - performantttt
5. Rewrite a subset in c++ 