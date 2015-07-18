/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
 *
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

package com.goide.psi.legacy;

public class GoLegacyResolveVarsTest extends GoLegacyResolveTestBase {
  @Override
  protected String getBasePath() { return "psi/resolve/vars"; }

  public void testAnonymousFunctionInvocation()                     {
    doFileTest(false);
  } 
  public void testDeclaredInForRange1()                             {
    doFileTest(false);
  }
  public void testDeclaredInForRange2()                             {
    doFileTest(false);
  }
  public void testDeclaredInForRangeAsValue()                       {
    doFileTest(false);
  }
  public void testDeclaredInForClause()                             {
    doFileTest(false);
  }
  public void testMethodReturn()                                    {
    doFileTest(false);
  }
  public void testSimpleMethodParameter()                           {
    doFileTest(false);
  }
  public void testMethodReturn2()                                   {
    doFileTest(false);
  }
  public void testGlobalVarDeclaration()                            {
    doFileTest(false);
  }
  public void testGlobalShadowedVarDeclaration()                    {
    doFileTest(false);
  }
  public void testGlobalVarDeclarationFromBlock()                   {
    doFileTest(false);
  }
  public void testShortVarDeclaration()                             {
    doFileTest(false);
  }
  public void testShortVarRedeclaration()                           {
    doFileTest(false);
  }
  public void testShortVarDeclarationFromBlock()                    {
    doFileTest(false);
  }
  public void testGlobalConstDeclaration()                          {
    doFileTest(false);
  }
  public void testResolveToMethodName()                             {
    doFileTest(false);
  }
  public void testLocalConstDeclaration()                           {
    doFileTest(false);
  }
  public void testChainedSelector()                                 {
    doFileTest(false);
  }
  public void testVarInSwitchExpr()                                 {
    doFileTest(false);
  }
  public void testVarInSwitchExprInitialization()                   {
    doFileTest(false);
  }
  public void testVarInSwitchType()                                 {
    doFileTest(false);
  }
  public void testVarInSwitchTypeInitialization()                   {
    doFileTest(false);
  }
  public void testMultipleGlobalVars()                              {
    doFileTest(false);
  }
  public void testMultipleGlobalConsts()                            {
    doFileTest(false);
  }
  public void testDeclarationInsideLabeledStatement()               {
    doFileTest(false);
  }
  public void testStructFieldViaChannel()                           {
    doFileTest(false);
  }
  public void testShortVarDeclarationFromSelectClause()             {
    doFileTest(false);
  }
  public void testVarDeclarationInSelectCommClauseRecv()            {
    doFileTest(false);
  }
  public void testVarDeclarationInSelectCommClauseDefault()         {
    doFileTest(false);
  }
  public void testRangeExpressionVarsShouldNotResolveToRangeVars()  {
    doFileTest(false);
  }
  public void testVarDereferenceAsTypeCast()                        {
    doFileTest(false);
  }
  public void testShortAssignToReturnVar()                          {
    doFileTest(false);
  }
  public void testResolveToFunctionName()                           {
    doFileTest(false);
  }
  public void testVarInSwitchTypeWithNamedSwitchGuard()             {
    doFileTest(false);
  }
  public void testVarDeclarationInSelectCommClauseSend()            {
    doFileTest(false);
  }
  public void testVarDeclarationOutsideSwitch()                     {
    doFileTest(false);
  }
  public void testVarVsInnerTypes()                                 {
    doFileTest(false);
  }
  public void testVarTypeGuard()                                    {
    doFileTest(false);
  }

  public void testFromDefaultImportedPackage()                      { doDirTest(); }
  public void testLocalPackageDefinitionsShouldBeResolvedFirst()    { doDirTest(); }
  public void testMultipleApplications()                            { doDirTest(); }
  public void testResolveMethodReceiver()                           { doDirTest(); }
  public void testFromInjectedImportedPackage()                     { doDirTest(); }
  public void testDefaultImportDifferentPackage()                   { doDirTest(); }
  public void testFromCustomImportedPackage()                       { doDirTest(); }
}
