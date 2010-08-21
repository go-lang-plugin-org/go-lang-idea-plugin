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

  private Stack <IElementType> gStringStack = new Stack<IElementType>();
  private Stack <IElementType> blockStack = new Stack<IElementType>();

  private int afterComment = YYINITIAL;
  private int afterNls = YYINITIAL;
  private int afterBrace = YYINITIAL;

  private void clearStacks(){
    gStringStack.clear();
    blockStack.clear();
  }

  private Stack<IElementType> braceCount = new Stack <IElementType>();

%}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// NewLines and spaces /////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

mNL = [\r\n] | \r\n      // NewLinE
mWS = [ \t\f]    // Whitespaces

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// Comments ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

C_STYLE_COMMENT="/*" ~"*/" 
// COMMENT_TAIL=( [^"*"]* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?

mSL_COMMENT = "//" [^\r\n]*
mML_COMMENT = {C_STYLE_COMMENT}

mLETTER = [:letter:] | "_"
mDIGIT = [:digit:]

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

// mCHAR = "'" [^'] "'"

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


%state IN_CHAR_LITERAL
%state IN_STRING_LITERAL

// %state IN_COMMENT

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

"..."                                     { return oTRIPLE_DOT; }
"."                                       { return oDOT; }

"'" . "'"                                               { return litCHAR; }
"'" \n "'"                                              { return litCHAR; }
"'\\" [abfnrtv\\\'] "'"                                 { return litCHAR; }
"'\\" {mOCT_DIGIT} {mOCT_DIGIT} {mOCT_DIGIT} "'"        { return litCHAR; }
"'\\x" {mHEX_DIGIT} {mHEX_DIGIT} "'"                    { return litCHAR; }
"'\\u" {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} "'"
                                                        { return litCHAR; }
"'\\U" {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} {mHEX_DIGIT} "'"
                                                        { return litCHAR; }

"`" [^`]* "`"                             { return litSTRING; }
"\"" ("\\\"" | [^\"])* "\""               { return litSTRING; }
"{"                                       { return pLCURCLY; }
"}"                                       { return pRCURLY; }

"["                                       { return pLBRACK; }
"]"                                       { return pRBRACK; }

"("                                       { return pLPAREN; }
")"                                       { return pRPAREN; }

":"                                       { return oCOLON; }
";"                                       { return oSEMI; }
","                                       { return oCOMMA; }

"=="                                      { return oEQ; }
"="                                       { return oASSIGN; }

"!="                                      { return oNOT_EQ; }
"!"                                       { return oNOT; }

"++"                                      { return oPLUS_PLUS; }
"+="                                      { return oPLUS_ASSIGN; }
"+"                                       { return oPLUS; }

"--"                                      { return oMINUS_MINUS; }
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


{mSL_COMMENT}                             { return( mSL_COMMENT ); }
{mML_COMMENT}                             { return( mML_COMMENT ); }

"break"                                   { return( kBREAK );  }
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
"fallthrough"                             {  return( kFALLTHROUGH ); }

"if"                                      {  return kIF ;  }
"for"                                     {  return kFOR ;  }
"return"                                  {  return kRETURN ;  }
"import"                                  {  return kIMPORT ;  }
"continue"                                {  return kCONTINUE ;  }

"range"                                   {  return kRANGE;  }
"type"                                    {  return kTYPE;  }
"var"                                     {  return kVAR;  }

{mIDENT}                                  {  return mIDENT; }

{mNUM_FLOAT}"i"                           {  return litFLOAT_I; }
{mNUM_FLOAT}                              {  return litFLOAT; }
{mDIGIT}+"i"                              {  return litDECIMAL_I; }
{mNUM_OCT}                                {  return litOCT; }
{mNUM_HEX}                                {  return litHEX; }
{mNUM_INT}                                {  return litINT; }



.                                         {  return mWRONG; }
}