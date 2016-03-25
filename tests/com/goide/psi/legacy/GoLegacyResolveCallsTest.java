/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

import com.goide.project.GoBuildTargetSettings;
import com.goide.project.GoModuleSettings;
import org.jetbrains.annotations.NotNull;

public class GoLegacyResolveCallsTest extends GoLegacyResolveTestBase {
  @NotNull
  @Override
  protected String getBasePath() { return "psi/resolve/calls"; }

  public void testCallToLocalMethodByPointer()                           { doFileTest(); } 
  public void testCallToLocalMethod()                                    { doFileTest(); } 
  public void testCallToLocalMethodNested()                              { doFileTest(); } 
  public void testCallToLocalMethodViaMap()                              { doFileTest(); } 
  public void testCallToLocalMethodViaShortVarDeclaration()              { doFileTest(); } 
  public void testCallToLocalMethodViaSlice()                            { doFileTest(); } 
  public void testCallToLocalMethodViaTypeAssert()                       { doFileTest(); } 
  public void testCallToLocalInterfaceMethod()                           { doFileTest(); } 
  public void testCallToLocalInterfaceMethodNested()                     { doFileTest(); } 
  public void testCallToLocalInterfaceMethodViaMap()                     { doFileTest(); } 
  public void testCallToLocalInterfaceMethodViaSlice()                   { doFileTest(); } 
  public void testCallToLocalInterfaceMethodViaTypeAssert()              { doFileTest(); } 
  public void testCallToLocalFunction()                                  { doFileTest(); } 
  public void testCallToFunctionLiteral()                                { doFileTest(); } 
  public void testTypeConversionToLocalType()                            { doFileTest(); } 
  public void testRecursiveMethodCall()                                  { doFileTest(); } 
  public void testCallToMethodParameter()                                { doFileTest(); } 
  public void testCallToFunctionVariable()                               { doFileTest(); } 
  public void testDirectlyInheritedMethodSet()                           { doFileTest(); } 
  public void testGrandParentDirectlyInheritedMethodSet()                { doFileTest(); } 
  public void testCallToEmbeddedInterfaceMethod()                        { doFileTest(); } 
  public void testCallToFunctionWithSameNameAsMethod()                   { doFileTest(); } 
  public void testCallToMethodWithTheSameNameAsFunction()                { doFileTest(); } 

  public void testConversionToImportedType()                             { doDirTest(); } 
  public void testConversionToImportedFunction()                         { doDirTest(); } 
  public void testNoConversionToBlankImportedType()                      { doDirTest(); } 
  public void testConversionToLocallyImportedType()                      { doDirTest(); } 
  public void testFunctionInSamePackageDifferentFile()                   { doDirTest(); } 
  public void testRelativePackageReference()                             { doDirTest(); }
  public void testRelativePackageReferenceDeep()                         { doDirTest(); } 
  public void testCallToFunctionWithSameNameAsMethodAcrossPackages()     { doDirTest(); } 
  public void testCallToMethodViaShortVar()                              { doDirTest(); } 
  public void testImportedEmbeddedTypeMethod()                           { doDirTest(); } 
  public void testCallToMethodWithTheSameNameAsFunctionAcrossPackages()  { doDirTest(); }
  public void testCallFromTestToMethodDefinedInTestFile()                { doDirTest(); }
  public void testCallToMethodDefinedInTestFile()                        { doDirTest(); }

  public void testCallToDifferentBuildTargetFiles() {
    GoBuildTargetSettings buildTargetSettings = new GoBuildTargetSettings();
    buildTargetSettings.customFlags = new String[]{"enabled"};
    GoModuleSettings.getInstance(myFixture.getModule()).setBuildTargetSettings(buildTargetSettings);
    doDirTest();
  }
}
