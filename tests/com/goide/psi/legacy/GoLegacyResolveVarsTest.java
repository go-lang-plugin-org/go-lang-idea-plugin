/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Claudiu Toader
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

  public void testAnonymousFunctionInvocation()                     { doTest(); } 
  public void testDeclaredInForRange1()                             { doTest(); }
  public void testDeclaredInForRange2()                             { doTest(); }
  public void testDeclaredInForRangeAsValue()                       { doTest(); }
  public void testDeclaredInForClause()                             { doTest(); }
  public void testMethodReturn()                                    { doTest(); }
  public void testSimpleMethodParameter()                           { doTest(); }
  public void testMethodReturn2()                                   { doTest(); }
  public void testGlobalVarDeclaration()                            { doTest(); }
  public void testGlobalShadowedVarDeclaration()                    { doTest(); }
  public void testGlobalVarDeclarationFromBlock()                   { doTest(); }
  public void testShortVarDeclaration()                             { doTest(); }
  public void testShortVarRedeclaration()                           { doTest(); }
  public void testShortVarDeclarationFromBlock()                    { doTest(); }
  public void testGlobalConstDeclaration()                          { doTest(); }
  public void testResolveToMethodName()                             { doTest(); }
  public void testLocalConstDeclaration()                           { doTest(); }
  public void testChainedSelector()                                 { doTest(); }
  public void testVarInSwitchExpr()                                 { doTest(); }
  public void testVarInSwitchExprInitialization()                   { doTest(); }
  public void testVarInSwitchType()                                 { doTest(); }
  public void testVarInSwitchTypeInitialization()                   { doTest(); }
  public void testMultipleGlobalVars()                              { doTest(); }
  public void testMultipleGlobalConsts()                            { doTest(); }
  public void testDeclarationInsideLabeledStatement()               { doTest(); }
  public void testStructFieldViaChannel()                           { doTest(); }
  public void testShortVarDeclarationFromSelectClause()             { doTest(); }
  public void testVarDeclarationInSelectCommClauseRecv()            { doTest(); }
  public void testVarDeclarationInSelectCommClauseDefault()         { doTest(); }
  public void testRangeExpressionVarsShouldNotResolveToRangeVars()  { doTest(); }
  public void testVarDereferenceAsTypeCast()                        { doTest(); }
  public void testShortAssignToReturnVar()                          { doTest(); }
  public void testResolveToFunctionName()                           { doTest(); }
  public void testVarInSwitchTypeWithNamedSwitchGuard()             { doTest(); }
  public void testVarDeclarationInSelectCommClauseSend()            { doTest(); }
  public void testVarDeclarationOutsideSwitch()                     { doTest(); }
  public void testVarVsInnerTypes()                                 { doTest(); }
  public void testVarTypeGuard()                                    { doTest(); }

  public void testFromDefaultImportedPackage()                      { doDirTest(); }
  public void testLocalPackageDefinitionsShouldBeResolvedFirst()    { doDirTest(); }
  public void testMultipleApplications()                            { doDirTest(); }
  public void testResolveMethodReceiver()                           { doDirTest(); }
  public void testFromInjectedImportedPackage()                     { doDirTest(); }
  public void testDefaultImportDifferentPackage()                   { doDirTest(); }
  public void testFromCustomImportedPackage()                       { doDirTest(); }
}
