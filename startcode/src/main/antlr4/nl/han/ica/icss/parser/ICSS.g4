grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE' | 'true' | 'True';
FALSE: 'FALSE' | 'false' | 'False';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];


//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//Operators
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER ---//
stylesheet: (variabeleToewijzing | stijlRegel)* EOF;
stijlRegel: selector OPEN_BRACE (declaratie | variabeleToewijzing)* CLOSE_BRACE;
variabeleToewijzing: CAPITAL_IDENT ASSIGNMENT_OPERATOR expressie+ SEMICOLON;
selector: LOWER_IDENT
        | CLASS_IDENT
        | ID_IDENT;
declaratie: LOWER_IDENT COLON expressie SEMICOLON;
expressie: literal
        | expressie MUL expressie
        |expressie PLUS | MIN expressie;
literal: TRUE
        | FALSE
        | COLOR
        | PIXELSIZE
        | CAPITAL_IDENT;











