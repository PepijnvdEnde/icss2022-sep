grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';

//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
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
stijlRegel: selector OPEN_BRACE regelInhoud CLOSE_BRACE;
variabeleToewijzing: variabeleReferentie ASSIGNMENT_OPERATOR expressie+ SEMICOLON;
regelInhoud: (declaratie | ifClause | variabeleToewijzing)*;
selector: LOWER_IDENT   #tagSelector
        | CLASS_IDENT   #classSelector
        | ID_IDENT      #idSelector;
declaratie: eigenschapNaam COLON expressie SEMICOLON;
expressie: literal                         #literalExpressie
        | expressie MUL expressie          #mulExpressie
        | expressie (PLUS | MIN) expressie #plusMinExpressie;
ifClause: IF BOX_BRACKET_OPEN expressie  BOX_BRACKET_CLOSE OPEN_BRACE regelInhoud CLOSE_BRACE elseClause?;
elseClause: ELSE OPEN_BRACE regelInhoud CLOSE_BRACE;
literal: (TRUE | FALSE)         #boolLiteral
        | COLOR                 #kleurLiteral
        | PIXELSIZE             #pixelLiteral
        | SCALAR                #scalarLiteral
        | PERCENTAGE            #percentageLiteral
        | variabeleReferentie   #variabeleLiteral;
variabeleReferentie: CAPITAL_IDENT;
eigenschapNaam: LOWER_IDENT;




