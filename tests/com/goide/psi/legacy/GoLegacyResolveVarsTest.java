/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

import org.jetbrains.annotations.NotNull;

public class GoLegacyResolveVarsTest extends GoLegacyResolveTestBase {
  @NotNull
  @Override
  protected String getBasePath() { return "psi/resolve/vars"; }

  public void testAnonymousFunctionInvocation()                     { doFileTest(); } 
  public void testDeclaredInForRange1()                             { doFileTest(); }
  public void testDeclaredInForRange2()                             { doFileTest(); }
  public void testDeclaredInForRangeAsValue()                       { doFileTest(); }
  public void testDeclaredInForClause()                             { doFileTest(); }
  public void testMethodReturn()                                    { doFileTest(); }
  public void testSimpleMethodParameter()                           { doFileTest(); }
  public void testMethodReturn2()                                   { doFileTest(); }
  public void testGlobalVarDeclaration()                            { doFileTest(); }
  public void testGlobalShadowedVarDeclaration()                    { doFileTest(); }
  public void testGlobalVarDeclarationFromBlock()                   { doFileTest(); }
  public void testShortVarDeclaration()                             { doFileTest(); }
  public void testShortVarRedeclaration()                           { doFileTest(); }
  public void testShortVarDeclarationFromBlock()                    { doFileTest(); }
  public void testGlobalConstDeclaration()                          { doFileTest(); }
  public void testResolveToMethodName()                             { doFileTest(); }
  public void testLocalConstDeclaration()                           { doFileTest(); }
  public void testChainedSelector()                                 { doFileTest(); }
  public void testVarInSwitchExpr()                                 { doFileTest(); }
  public void testVarInSwitchExprInitialization()                   { doFileTest(); }
  public void testVarInSwitchType()                                 { doFileTest(); }
  public void testVarInSwitchTypeInitialization()                   { doFileTest(); }
  public void testMultipleGlobalVars()                              { doFileTest(); }
  public void testMultipleGlobalConsts()                            { doFileTest(); }
  public void testDeclarationInsideLabeledStatement()               { doFileTest(); }
  public void testStructFieldViaChannel()                           { doFileTest(); }
  public void testShortVarDeclarationFromSelectClause()             { doFileTest(); }
  public void testVarDeclarationInSelectCommClauseRecv()            { doFileTest(); }
  public void testVarDeclarationInSelectCommClauseDefault()         { doFileTest(); }
  public void testRangeExpressionVarsShouldNotResolveToRangeVars()  { doFileTest(); }
  public void testVarDereferenceAsTypeCast()                        { doFileTest(); }
  public void testShortAssignToReturnVar()                          { doFileTest(); }
  public void testResolveToFunctionName()                           { doFileTest(); }
  public void testVarInSwitchTypeWithNamedSwitchGuard()             { doFileTest(); }
  public void testVarDeclarationInSelectCommClauseSend()            { doFileTest(); }
  public void testVarDeclarationOutsideSwitch()                     { doFileTest(); }
  public void testVarVsInnerTypes()                                 { doFileTest(); }
  public void testVarTypeGuard()                                    { doFileTest(); }
  public void testResolveMethodReceiver()                           { doFileTest(); }
  public void testDontProcessExpressions()                          { doFileTest(); }

  public void testFromDefaultImportedPackage()                      { doDirTest(); }
  public void testLocalPackageDefinitionsShouldBeResolvedFirst()    { doDirTest(); }
  public void testMultipleApplications()                            { doDirTest(); }
  public void testFromInjectedImportedPackage()                     { doDirTest(); }
  public void testDefaultImportDifferentPackage()                   { doDirTest(); }
  public void testFromCustomImportedPackage()                       { doDirTest(); }
}
