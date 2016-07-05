package com.goide.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.goide.GoTypes;
import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.goide.GoParserDefinition.*;

%%

%{
  public _GoLexer() {
    this((java.io.Reader)null);
 }
%}

%class _GoLexer
%implements FlexLexer, GoTypes
%unicode
%public

%function advance
%type IElementType

NL = \R
WS = [ \t\f]

LINE_COMMENT = "//" [^\r\n]*
MULTILINE_COMMENT = "/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?

LETTER = [:letter:] | "_"
DIGIT =  [:digit:]

HEX_DIGIT = [0-9A-Fa-f]
INT_DIGIT = [0-9]
OCT_DIGIT = [0-7]

NUM_INT = "0" | ([1-9] {INT_DIGIT}*)
NUM_HEX = ("0x" | "0X") {HEX_DIGIT}+
NUM_OCT = "0" {OCT_DIGIT}+

FLOAT_EXPONENT = [eE] [+-]? {DIGIT}+
NUM_FLOAT = ( ( ({DIGIT}+ "." {DIGIT}*) | ({DIGIT}* "." {DIGIT}+) ) {FLOAT_EXPONENT}?) | ({DIGIT}+ {FLOAT_EXPONENT})

IDENT = {LETTER} ({LETTER} | {DIGIT} )*

STR =      "\""
STRING = {STR} ( [^\"\\\n\r] | "\\" ("\\" | {STR} | {ESCAPES} | [0-8xuU] ) )* {STR}?
ESCAPES = [abfnrtv]

%state MAYBE_SEMICOLON

%%

<YYINITIAL> {
{WS}                                      { return WS; }
{NL}+                                     { return NLS; }

{LINE_COMMENT}                            { return LINE_COMMENT; }
{MULTILINE_COMMENT}                       { return MULTILINE_COMMENT; }

{STRING}                                  { yybegin(MAYBE_SEMICOLON); return STRING; }

"'\\'"                                    { yybegin(MAYBE_SEMICOLON); return BAD_CHARACTER; }
"'" [^\\] "'"?                            { yybegin(MAYBE_SEMICOLON); return CHAR; }
"'" \n "'"?                               { yybegin(MAYBE_SEMICOLON); return CHAR; }
"'\\" [abfnrtv\\\'] "'"?                  { yybegin(MAYBE_SEMICOLON); return CHAR; }
"'\\"  {OCT_DIGIT} {3} "'"?               { yybegin(MAYBE_SEMICOLON); return CHAR; }
"'\\x" {HEX_DIGIT} {2} "'"?               { yybegin(MAYBE_SEMICOLON); return CHAR; }
"'\\u" {HEX_DIGIT} {4} "'"?               { yybegin(MAYBE_SEMICOLON); return CHAR; }
"'\\U" {HEX_DIGIT} {8} "'"?               { yybegin(MAYBE_SEMICOLON); return CHAR; }

"`" [^`]* "`"?                            { yybegin(MAYBE_SEMICOLON); return RAW_STRING; }
"..."                                     { return TRIPLE_DOT; }
"."                                       { return DOT; }
"|"                                       { return BIT_OR; }
"{"                                       { return LBRACE; }
"}"                                       { yybegin(MAYBE_SEMICOLON); return RBRACE; }

"["                                       { return LBRACK; }
"]"                                       { yybegin(MAYBE_SEMICOLON); return RBRACK; }

"("                                       { return LPAREN; }
")"                                       { yybegin(MAYBE_SEMICOLON); return RPAREN; }

":"                                       { return COLON; }
";"                                       { return SEMICOLON; }
","                                       { return COMMA; }

"=="                                      { return EQ; }
"="                                       { return ASSIGN; }

"!="                                      { return NOT_EQ; }
"!"                                       { return NOT; }

"++"                                      { yybegin(MAYBE_SEMICOLON); return PLUS_PLUS; }
"+="                                      { return PLUS_ASSIGN; }
"+"                                       { return PLUS; }

"--"                                      { yybegin(MAYBE_SEMICOLON); return MINUS_MINUS; }
"-="                                      { return MINUS_ASSIGN; }
"-"                                       { return MINUS; }

"||"                                      { return COND_OR; }
"|="                                      { return BIT_OR_ASSIGN; }

"&^="                                     { return BIT_CLEAR_ASSIGN; }
"&^"                                      { return BIT_CLEAR; }
"&&"                                      { return COND_AND; }

"&="                                      { return BIT_AND_ASSIGN; }
"&"                                       { return BIT_AND; }

"<<="                                     { return SHIFT_LEFT_ASSIGN; }
"<<"                                      { return SHIFT_LEFT; }
"<-"                                      { return SEND_CHANNEL; }
"<="                                      { return LESS_OR_EQUAL; }
"<"                                       { return LESS; }

"^="                                      { return BIT_XOR_ASSIGN; }
"^"                                       { return BIT_XOR; }

"*="                                      { return MUL_ASSIGN; }
"*"                                       { return MUL; }

"/="                                      { return QUOTIENT_ASSIGN; }
"/"                                       { return QUOTIENT; }

"%="                                      { return REMAINDER_ASSIGN; }
"%"                                       { return REMAINDER; }

">>="                                     { return SHIFT_RIGHT_ASSIGN; }
">>"                                      { return SHIFT_RIGHT; }
">="                                      { return GREATER_OR_EQUAL; }
">"                                       { return GREATER; }

":="                                      { return VAR_ASSIGN; }

"break"                                   { yybegin(MAYBE_SEMICOLON); return BREAK; }
"fallthrough"                             { yybegin(MAYBE_SEMICOLON); return FALLTHROUGH; }
"return"                                  { yybegin(MAYBE_SEMICOLON); return RETURN ; }
"continue"                                { yybegin(MAYBE_SEMICOLON); return CONTINUE ; }

"default"                                 { return DEFAULT; }
"package"                                 { return PACKAGE; }
"func"                                    { return FUNC; }
"interface"                               { return INTERFACE; }
"select"                                  { return SELECT; }

"case"                                    { return CASE; }
"defer"                                   { return DEFER; }
"go"                                      { return GO; }
"map"                                     { return MAP; }

"chan"                                    { return CHAN; }

"struct"                                  { return STRUCT; }
"else"                                    { return ELSE; }
"goto"                                    { return GOTO; }
"switch"                                  { return SWITCH; }
"const"                                   { return CONST; }

"if"                                      { return IF ; }
"for"                                     { return FOR ; }
"import"                                  { return IMPORT ; }

"range"                                   { return RANGE; }
"type"                                    { return TYPE_; }
"var"                                     { return VAR; }

{IDENT}                                   { yybegin(MAYBE_SEMICOLON); return IDENTIFIER; }

{NUM_FLOAT}"i"                            { yybegin(MAYBE_SEMICOLON); return FLOATI; }
{NUM_FLOAT}                               { yybegin(MAYBE_SEMICOLON); return FLOAT; }
{DIGIT}+"i"                               { yybegin(MAYBE_SEMICOLON); return DECIMALI; }
{NUM_OCT}                                 { yybegin(MAYBE_SEMICOLON); return OCT; }
{NUM_HEX}                                 { yybegin(MAYBE_SEMICOLON); return HEX; }
{NUM_INT}                                 { yybegin(MAYBE_SEMICOLON); return INT; }

.                                         { return BAD_CHARACTER; }
}

<MAYBE_SEMICOLON> {
{WS}                                      { return WS; }
{NL}                                      { yybegin(YYINITIAL); yypushback(yytext().length()); return SEMICOLON_SYNTHETIC; }
{LINE_COMMENT}                            { return LINE_COMMENT; }
{MULTILINE_COMMENT}                       { return MULTILINE_COMMENT; }
.                                         { yybegin(YYINITIAL); yypushback(yytext().length()); }
}