/*
 * Copyright 2000-2010 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ro.redeul.google.go.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import java.util.*;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;

%%

%unicode
%class _GoLexer
%implements FlexLexer, GoTokenTypes
%unicode
%public

%function advance
%type IElementType

%eof{ return;
%eof}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// User code //////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

%{

  private Stack <IElementType> gStringStack = new Stack<>();
  private Stack <IElementType> blockStack = new Stack<>();

  private int afterComment = YYINITIAL;
  private int afterNls = YYINITIAL;
  private int afterBrace = YYINITIAL;

  private void clearStacks(){
    gStringStack.clear();
    blockStack.clear();
  }

  private Stack<IElementType> braceCount = new Stack <>();

%}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// NewLines and spaces /////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mNL = [\r\n] | \r\n      // NewLinE
mWS = [ \t\f]    // Whitespaces

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// Comments ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//C_STYLE_COMMENT="/*" [^"*/"]* "*/"
//COMMENT_TAIL=( [^"*"]* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?

mSL_COMMENT = "//" [^\r\n]*
//mML_COMMENT = "/*" "*"

mLETTER = [:letter:] | "_"
mDIGIT =  [:digit:]

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////      integers and floats     /////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mHEX_DIGIT = [0-9A-Fa-f]
mINT_DIGIT = [0-9]
mOCT_DIGIT = [0-7]

mNUM_INT = "0" | ([1-9] {mINT_DIGIT}*)
mNUM_HEX = ("0x" | "0X") {mHEX_DIGIT}+
mNUM_OCT = "0" {mOCT_DIGIT}+

mFLOAT_EXPONENT = [eE] [+-]? {mDIGIT}+
mNUM_FLOAT = ( ( ({mDIGIT}+ "." {mDIGIT}*) | ({mDIGIT}* "." {mDIGIT}+) ) {mFLOAT_EXPONENT}?) | ({mDIGIT}+ {mFLOAT_EXPONENT})

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////      identifiers      ////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mIDENT = {mLETTER} ({mLETTER} | {mDIGIT} )*
//mIDENT_NOBUCKS = {mLETTER} ({mLETTER} | {mDIGIT})*

mSLASH =    "\\"
mSTR =      "\""
mESCAPES = [abfnrtv]

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// String & regexprs ///////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//mSTRING_NL = {mONE_NL}
//mSTRING_ESC = \\ [^]
//mREGEX_ESC = \\ n | \\ r | \\ t | \\ b | \\ f | "\\" "\\" | \\ "$" | \\ \" | \\ \' | "\\""u"{mHEX_DIGIT}{4} | "\\" [0..3] ([0..7] ([0..7])?)? | "\\" [4..7] ([0..7])? | "\\" {mONE_NL}

/// Regexes ////////////////////////////////////////////////////////////////

//ESCAPPED_REGEX_SEP = \\ "/"
//mREGEX_BEGIN = "/""$" |  "/" ([^"/""$"] | {mREGEX_ESC} | {ESCAPPED_REGEX_SEP})? {mREGEX_CONTENT}"$"
//mREGEX_CONTENT = ({mREGEX_ESC}    | {ESCAPPED_REGEX_SEP} | [^"/"\r\n"$"])*

//mREGEX_LITERAL = "/" ([^"/"\n\r"$"] | {mREGEX_ESC} | {ESCAPPED_REGEX_SEP})? {mREGEX_CONTENT} ("$""/" | "/")

////////////////////////////////////////////////////////////////////////////

//mSINGLE_QUOTED_STRING_BEGIN = "\'" ( {mSTRING_ESC} | "\""  | [^\\\'\r\n] | "$" )*
//mSINGLE_QUOTED_STRING = {mSINGLE_QUOTED_STRING_BEGIN} \'
//mTRIPLE_QUOTED_STRING = "\'\'\'" ({mSTRING_ESC} | \" | "$" | [^\'] | {mSTRING_NL} | \'(\')?[^\'] )* (\'\'\' | \\)?

//mSTRING_LITERAL = {mTRIPLE_QUOTED_STRING} | {mSINGLE_QUOTED_STRING}


// Single-double-quoted GStrings
//mGSTRING_SINGLE_CONTENT = ({mSTRING_ESC} | [^\\\"\r\n"$"] | "\'" )+

// Triple-double-quoted GStrings
//mGSTRING_TRIPLE_CONTENT = ({mSTRING_ESC} | \' | \" (\")? [^\""$"] | [^\\\""$"] | {mSTRING_NL})+


//mGSTRING_TRIPLE_CTOR_END = {mGSTRING_TRIPLE_CONTENT} \"\"\"


//mGSTRING_LITERAL = \"\" | \" ([^\\\"\n\r"$"] | {mSTRING_ESC})? {mGSTRING_SINGLE_CONTENT} \" | \"\"\" {mGSTRING_TRIPLE_CTOR_END}


// %state IN_COMMENT
%state MAYBE_SEMI

%%

//<X> {

//"}"                                       {  if (!braceCount.isEmpty() && mLCURLY == braceCount.peek()) {
//                                               braceCount.pop();
//                                             }
//                                             return(mRCURLY);  }

//}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////// White spaces & NewLines //////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// {mONE_NL}                                 {  return wsWS; }
//{wsWS}                                     {  return wsWS; }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////Comments //////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//{mSL_COMMENT}                             {  return mSL_COMMENT; }
//{mML_COMMENT}                             {  return mML_COMMENT; }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////// Integers and floats //////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////// Strings & regular expressions ////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Java strings
//{mSTRING_LITERAL}                                          {  return mSTRING_LITERAL; }
//{mSINGLE_QUOTED_STRING_BEGIN}                              {  return mSTRING_LITERAL; }

// GStrings
//\"\"\"                                                     {  yybegin(IN_TRIPLE_GSTRING);
//                                                              gStringStack.push(mLBRACK);
//                                                              return mGSTRING_BEGIN; }

//\"                                                         {  yybegin(IN_SINGLE_GSTRING);
//                                                              gStringStack.push(mLPAREN);
//                                                              return mGSTRING_BEGIN; }

//{mGSTRING_LITERAL}                                         {  return mGSTRING_LITERAL; }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////// Reserved shorthands //////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//<IN_COMMENT> {
//!"*/"+                                      {}
//
//"*/"                                        { yybegin(YYINITIAL); return( mML_COMMENT ); }
//<<EOF>>                                     { yybegin(YYINITIAL); return( mML_COMMENT ); }
//}

<YYINITIAL> {
"|"                                       { return oBIT_OR; }

{mWS}                                     { return wsWS; }
{mNL}+                                    { return wsNLS; }

{mSL_COMMENT}                             { return( mSL_COMMENT ); }
//{mML_COMMENT}                             { return( mML_COMMENT ); }
"/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")? { return( mML_COMMENT ); }

//([^"*/"] | [\r\n])+ "*/"?

"..."                                     { return oTRIPLE_DOT; }
"."                                       { return oDOT; }

"'" [^\\] "'"                                           { yybegin(MAYBE_SEMI); return litCHAR; }
"'" \n "'"                                              { yybegin(MAYBE_SEMI); return litCHAR; }
"'\\" [abfnrtv\\\'] "'"                                 { yybegin(MAYBE_SEMI); return litCHAR; }
"'\\'"                                                  { yybegin(MAYBE_SEMI); return mWRONG; }
"'\\" {mOCT_DIGIT} {mOCT_DIGIT} {mOCT_DIGIT} "'"        { yybegin(MAYBE_SEMI); return litCHAR; }
"'\\x" {mHEX_DIGIT} {mHEX_DIGIT} "'"                    { yybegin(MAYBE_SEMI); return litCHAR; }
"'\\u" {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} "'"
                                                        { yybegin(MAYBE_SEMI); return litCHAR; }
"'\\U" {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} "'"
                                                        { yybegin(MAYBE_SEMI); return litCHAR; }

"`" [^`]* "`"?                            { yybegin(MAYBE_SEMI); return litSTRING; }
{mSTR}
    (
        [^\"\\\n\r] | "\\" ("\\" | {mSTR} | {mESCAPES} | [0-8xuU] )
    )*
{mSTR}?                                   { yybegin(MAYBE_SEMI); return litSTRING; }
"{"                                       { return pLCURLY; }
"}"                                       { yybegin(MAYBE_SEMI); return pRCURLY; }

"["                                       { return pLBRACK; }
"]"                                       { yybegin(MAYBE_SEMI); return pRBRACK; }

"("                                       { return pLPAREN; }
")"                                       { yybegin(MAYBE_SEMI); return pRPAREN; }

":"                                       { return oCOLON; }
";"                                       { return oSEMI; }
","                                       { return oCOMMA; }

"=="                                      { return oEQ; }
"="                                       { return oASSIGN; }

"!="                                      { return oNOT_EQ; }
"!"                                       { return oNOT; }

"++"                                      { yybegin(MAYBE_SEMI); return oPLUS_PLUS; }
"+="                                      { return oPLUS_ASSIGN; }
"+"                                       { return oPLUS; }

"--"                                      { yybegin(MAYBE_SEMI); return oMINUS_MINUS; }
"-="                                      { return oMINUS_ASSIGN; }
"-"                                       { return oMINUS; }

"||"                                      { return oCOND_OR; }
"|="                                      { return oBIT_OR_ASSIGN; }


"&^="                                     { return oBIT_CLEAR_ASSIGN; }
"&^"                                      { return oBIT_CLEAR; }
"&&"                                      { return oCOND_AND; }

"&="                                      { return oBIT_AND_ASSIGN; }
"&"                                       { return oBIT_AND; }

"<<="                                     { return oSHIFT_LEFT_ASSIGN; }
"<<"                                      { return oSHIFT_LEFT; }
"<-"                                      { return oSEND_CHANNEL; }
"<="                                      { return oLESS_OR_EQUAL; }
"<"                                       { return oLESS; }

"^="                                      { return oBIT_XOR_ASSIGN; }
"^"                                       { return oBIT_XOR; }

"*="                                      { return oMUL_ASSIGN; }
"*"                                       { return oMUL; }

"/="                                      { return oQUOTIENT_ASSIGN; }
"/"                                       { return oQUOTIENT; }

"%="                                      { return oREMAINDER_ASSIGN; }
"%"                                       { return oREMAINDER; }

">>="                                     { return oSHIFT_RIGHT_ASSIGN; }
">>"                                      { return oSHIFT_RIGHT; }
">="                                      { return oGREATER_OR_EQUAL; }
">"                                       { return oGREATER; }

":="                                      { return oVAR_ASSIGN; }


"break"                                   { yybegin(MAYBE_SEMI); return( kBREAK );  }
"fallthrough"                             { yybegin(MAYBE_SEMI); return( kFALLTHROUGH ); }
"return"                                  { yybegin(MAYBE_SEMI); return kRETURN ;  }
"continue"                                { yybegin(MAYBE_SEMI); return kCONTINUE ;  }

"default"                                 { return( kDEFAULT );  }
"package"                                 { return( kPACKAGE );  }
"func"                                    { return( kFUNC );  }
"interface"                               { return( kINTERFACE );  }
"select"                                  { return( kSELECT );  }

"case"                                    { return( kCASE );  }
"defer"                                   { return( kDEFER );  }
"go"                                      { return( kGO );  }
"map"                                     { return( kMAP );  }

"chan"                                    {  return( kCHAN );  }

"struct"                                  {  return( kSTRUCT );  }
"else"                                    {  return( kELSE );  }
"goto"                                    {  return( kGOTO );  }
"switch"                                  {  return( kSWITCH );  }
"const"                                   {  return( kCONST ); }

"if"                                      {  return kIF ;  }
"for"                                     {  return kFOR ;  }
"import"                                  {  return kIMPORT ;  }

"range"                                   {  return kRANGE;  }
"type"                                    {  return kTYPE;  }
"var"                                     {  return kVAR;  }

{mIDENT}                                  {  yybegin(MAYBE_SEMI); return mIDENT; }

{mNUM_FLOAT}"i"                           {  yybegin(MAYBE_SEMI); return litFLOAT_I; }
{mNUM_FLOAT}                              {  yybegin(MAYBE_SEMI); return litFLOAT; }
{mDIGIT}+"i"                              {  yybegin(MAYBE_SEMI); return litDECIMAL_I; }
{mNUM_OCT}                                {  yybegin(MAYBE_SEMI); return litOCT; }
{mNUM_HEX}                                {  yybegin(MAYBE_SEMI); return litHEX; }
{mNUM_INT}                                {  yybegin(MAYBE_SEMI); return litINT; }

.                                         {  return mWRONG; }
}

<MAYBE_SEMI> {
{mWS}               { return wsWS; }
{mNL}               { yybegin(YYINITIAL); yypushback(yytext().length()); return oSEMI_SYNTHETIC; }
{mSL_COMMENT}       { return mSL_COMMENT; }
"/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?
                    { return mML_COMMENT; }

.           { yybegin(YYINITIAL); yypushback(yytext().length()); }
}