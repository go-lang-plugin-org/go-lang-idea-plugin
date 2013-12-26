package com.goide.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.goide.GoTypes;
import java.util.*;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;
import static com.goide.GoParserDefinition.*;

%%


%{
  public _GoLexer() {
    this((java.io.Reader)null);
  }
%}

%unicode
%class _GoLexer
%implements FlexLexer, GoTypes
%unicode
%public

%function advance
%type IElementType

%eof{
  return;
%eof}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// User code //////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

%{

  private Stack<IElementType> gStringStack = new Stack<IElementType>();
  private Stack<IElementType> blockStack = new Stack<IElementType>();

  private int afterComment = YYINITIAL;
  private int afterNls = YYINITIAL;
  private int afterBrace = YYINITIAL;

  private void clearStacks(){
    gStringStack.clear();
    blockStack.clear();
  }

  private Stack<IElementType> braceCount = new Stack<IElementType>();

%}


NL = [\r\n] | \r\n      // NewLine
WS = [ \t\f]            // Whitespaces


//C_STYLE_COMMENT="/*" [^"*/"]* "*/"
//COMMENT_TAIL=( [^"*"]* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?

LINE_COMMENT = "//" [^\r\n]*
//MULTILINE_COMMENT = "/*" "*"

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
//IDENT_NOBUCKS = {LETTER} ({LETTER} | {DIGIT})*

STR =      "\""
ESCAPES = [abfnrtv]

//STRING_NL = {ONE_NL}
//STRING_ESC = \\ [^]
//REGEX_ESC = \\ n | \\ r | \\ t | \\ b | \\ f | "\\" "\\" | \\ "$" | \\ \" | \\ \' | "\\""u"{HEX_DIGIT}{4} | "\\" [0..3] ([0..7] ([0..7])?)? | "\\" [4..7] ([0..7])? | "\\" {ONE_NL}

/// Regexes ////////////////////////////////////////////////////////////////

//ESCAPPED_REGEX_SEP = \\ "/"
//REGEX_BEGIN = "/""$" |  "/" ([^"/""$"] | {REGEX_ESC} | {ESCAPPED_REGEX_SEP})? {REGEX_CONTENT}"$"
//REGEX_CONTENT = ({REGEX_ESC}    | {ESCAPPED_REGEX_SEP} | [^"/"\r\n"$"])*

//REGEX_LITERAL = "/" ([^"/"\n\r"$"] | {REGEX_ESC} | {ESCAPPED_REGEX_SEP})? {REGEX_CONTENT} ("$""/" | "/")

////////////////////////////////////////////////////////////////////////////

//SINGLE_QUOTED_STRING_BEGIN = "\'" ( {STRING_ESC} | "\""  | [^\\\'\r\n] | "$" )*
//SINGLE_QUOTED_STRING = {SINGLE_QUOTED_STRING_BEGIN} \'
//TRIPLE_QUOTED_STRING = "\'\'\'" ({STRING_ESC} | \" | "$" | [^\'] | {STRING_NL} | \'(\')?[^\'] )* (\'\'\' | \\)?

//STRING_LITERAL = {TRIPLE_QUOTED_STRING} | {SINGLE_QUOTED_STRING}


// Single-double-quoted GStrings
//GSTRING_SINGLE_CONTENT = ({STRING_ESC} | [^\\\"\r\n"$"] | "\'" )+

// Triple-double-quoted GStrings
//GSTRING_TRIPLE_CONTENT = ({STRING_ESC} | \' | \" (\")? [^\""$"] | [^\\\""$"] | {STRING_NL})+


//GSTRING_TRIPLE_CTOR_END = {GSTRING_TRIPLE_CONTENT} \"\"\"


//GSTRING_LITERAL = \"\" | \" ([^\\\"\n\r"$"] | {STRING_ESC})? {GSTRING_SINGLE_CONTENT} \" | \"\"\" {GSTRING_TRIPLE_CTOR_END}


// %state IN_COMMENT
%state MAYBE_SEMICOLON

%%

//<IN_COMMENT> {
//!"*/"+                                      {}
//
//"*/"                                        { yybegin(YYINITIAL); return( MULTILINE_COMMENT ); }
//<<EOF>>                                     { yybegin(YYINITIAL); return( MULTILINE_COMMENT ); }
//}

<YYINITIAL> {
"|"                                       { return GO_BIT_OR; }

{WS}                                     { return GO_WS; }
{NL}+                                    { return GO_NLS; }

{LINE_COMMENT}                             { return( GO_LINE_COMMENT ); }
//{MULTILINE_COMMENT}                             { return( MULTILINE_COMMENT ); }
"/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")? { return( GO_MULTILINE_COMMENT ); }

//([^"*/"] | [\r\n])+ "*/"?

"..."                                     { return GO_TRIPLE_DOT; }
"."                                       { return GO_DOT; }

"'" . "'"                                               { yybegin(MAYBE_SEMICOLON); return GO_CHAR; }
"'" \n "'"                                              { yybegin(MAYBE_SEMICOLON); return GO_CHAR; }
"'\\" [abfnrtv\\\'] "'"                                 { yybegin(MAYBE_SEMICOLON); return GO_CHAR; }
"'\\" {OCT_DIGIT} {OCT_DIGIT} {OCT_DIGIT} "'"        { yybegin(MAYBE_SEMICOLON); return GO_CHAR; }
"'\\x" {HEX_DIGIT} {HEX_DIGIT} "'"                    { yybegin(MAYBE_SEMICOLON); return GO_CHAR; }
"'\\u" {HEX_DIGIT} {HEX_DIGIT} {HEX_DIGIT} {HEX_DIGIT} "'"
                                                        { yybegin(MAYBE_SEMICOLON); return GO_CHAR; }
"'\\U" {HEX_DIGIT} {HEX_DIGIT} {HEX_DIGIT} {HEX_DIGIT} {HEX_DIGIT} {HEX_DIGIT} {HEX_DIGIT} {HEX_DIGIT} "'"
                                                        { yybegin(MAYBE_SEMICOLON); return GO_CHAR; }

"`" [^`]* "`"?                            { yybegin(MAYBE_SEMICOLON); return GO_STRING; }
{STR} ( [^\"\\\n\r] | "\\" ("\\" | {STR} | {ESCAPES} | [0-8xuU] ) )* {STR}? { yybegin(MAYBE_SEMICOLON); return GO_STRING; }
"{"                                       { return GO_LBRACE; }
"}"                                       { yybegin(MAYBE_SEMICOLON); return GO_LBRACE; }

"["                                       { return GO_LBRACE; }
"]"                                       { yybegin(MAYBE_SEMICOLON); return GO_RBRACK; }

"("                                       { return GO_LPAREN; }
")"                                       { yybegin(MAYBE_SEMICOLON); return GO_RPAREN; }

":"                                       { return GO_COLON; }
";"                                       { return GO_SEMICOLON; }
","                                       { return GO_COMMA; }

"=="                                      { return GO_EQ; }
"="                                       { return GO_ASSIGN; }

"!="                                      { return GO_NOT_EQ; }
"!"                                       { return GO_NOT; }

"++"                                      { yybegin(MAYBE_SEMICOLON); return GO_PLUS_PLUS; }
"+="                                      { return GO_PLUS_ASSIGN; }
"+"                                       { return GO_PLUS; }

"--"                                      { yybegin(MAYBE_SEMICOLON); return GO_MINUS_MINUS; }
"-="                                      { return GO_MINUS_ASSIGN; }
"-"                                       { return GO_MINUS; }

"||"                                      { return GO_COND_OR; }
"|="                                      { return GO_BIT_OR_ASSIGN; }

"&^="                                     { return GO_BIT_CLEAR_ASSIGN; }
"&^"                                      { return GO_BIT_CLEAR; }
"&&"                                      { return GO_COND_AND; }

"&="                                      { return GO_BIT_AND_ASSIGN; }
"&"                                       { return GO_BIT_AND; }

"<<="                                     { return GO_SHIFT_LEFT_ASSIGN; }
"<<"                                      { return GO_SHIFT_LEFT; }
"<-"                                      { return GO_SEND_CHANNEL; }
"<="                                      { return GO_LESS_OR_EQUAL; }
"<"                                       { return GO_LESS; }

"^="                                      { return GO_BIT_XOR_ASSIGN; }
"^"                                       { return GO_BIT_XOR; }

"*="                                      { return GO_MUL_ASSIGN; }
"*"                                       { return GO_MUL; }

"/="                                      { return GO_QUOTIENT_ASSIGN; }
"/"                                       { return GO_QUOTIENT; }

"%="                                      { return GO_REMAINDER_ASSIGN; }
"%"                                       { return GO_REMAINDER; }

">>="                                     { return GO_SHIFT_RIGHT_ASSIGN; }
">>"                                      { return GO_SHIFT_RIGHT; }
">="                                      { return GO_GREATER_OR_EQUAL; }
">"                                       { return GO_GREATER; }

":="                                      { return GO_VAR_ASSIGN; }


"break"                                   { yybegin(MAYBE_SEMICOLON); return GO_BREAK;  }
"fallthrough"                             { yybegin(MAYBE_SEMICOLON); return GO_FALLTHROUGH; }
"return"                                  { yybegin(MAYBE_SEMICOLON); return GO_RETURN ;  }
"continue"                                { yybegin(MAYBE_SEMICOLON); return GO_CONTINUE ;  }

"default"                                 { return( GO_DEFAULT );  }
"package"                                 { return( GO_PACKAGE );  }
"func"                                    { return( GO_FUNC );  }
"interface"                               { return( GO_INTERFACE );  }
"select"                                  { return( GO_SELECT );  }

"case"                                    { return( GO_CASE );  }
"defer"                                   { return( GO_DEFER );  }
"go"                                      { return( GO_GO );  }
"map"                                     { return( GO_MAP );  }

"chan"                                    {  return( GO_CHAN );  }

"struct"                                  {  return( GO_STRUCT );  }
"else"                                    {  return( GO_ELSE );  }
"goto"                                    {  return( GO_GOTO );  }
"switch"                                  {  return( GO_SWITCH );  }
"const"                                   {  return( GO_CONST ); }

"if"                                      {  return GO_IF ;  }
"for"                                     {  return GO_FOR ;  }
"import"                                  {  return GO_IMPORT ;  }

"range"                                   {  return GO_RANGE;  }
"type"                                    {  return GO_TYPE;  }
"var"                                     {  return GO_VAR;  }

{IDENT}                                  {  yybegin(MAYBE_SEMICOLON); return GO_IDENTIFIER; }

{NUM_FLOAT}"i"                           {  yybegin(MAYBE_SEMICOLON); return GO_FLOAT_I; }
{NUM_FLOAT}                              {  yybegin(MAYBE_SEMICOLON); return GO_FLOAT; }
{DIGIT}+"i"                              {  yybegin(MAYBE_SEMICOLON); return GO_DECIMAL_I; }
{NUM_OCT}                                {  yybegin(MAYBE_SEMICOLON); return GO_OCT; }
{NUM_HEX}                                {  yybegin(MAYBE_SEMICOLON); return GO_HEX; }
{NUM_INT}                                {  yybegin(MAYBE_SEMICOLON); return GO_INT; }

.                                         {  return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<MAYBE_SEMICOLON> {
{WS}               { return GO_WS; }
{NL}               { yybegin(YYINITIAL); yypushback(yytext().length()); return GO_SEMICOLON_SYNTHETIC; }
{LINE_COMMENT}       { return GO_LINE_COMMENT; }
"/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?
                    { return GO_MULTILINE_COMMENT; }

.           { yybegin(YYINITIAL); yypushback(yytext().length()); }
}