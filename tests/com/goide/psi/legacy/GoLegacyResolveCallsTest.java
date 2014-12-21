/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

public class GoLegacyResolveCallsTest extends GoLegacyResolveTestBase {
  @Override
  protected String getBasePath() { return "psi/resolve/calls"; }

  public void testCallToLocalMethodByPointer() { doTest(); } 
  public void testCallToLocalMethod() { doTest(); } 
  public void testCallToLocalMethodNested() { doTest(); } 
  public void testCallToLocalMethodViaMap() { doTest(); } 
  public void testCallToLocalMethodViaShortVarDeclaration() { doTest(); } 
  public void testCallToLocalMethodViaSlice() { doTest(); } 
  public void testCallToLocalMethodViaTypeAssert() { doTest(); } 
  public void testCallToLocalInterfaceMethod() { doTest(); } 
  public void testCallToLocalInterfaceMethodNested() { doTest(); } 
  public void testCallToLocalInterfaceMethodViaMap() { doTest(); } 
  public void testCallToLocalInterfaceMethodViaSlice() { doTest(); } 
  public void testCallToLocalInterfaceMethodViaTypeAssert() { doTest(); } 
  public void testCallToLocalFunction() { doTest(); } 
  public void testCallToFunctionLiteral() { doTest(); } 
  public void testTypeConversionToLocalType() { doTest(); } 
  public void testRecursiveMethodCall() { doTest(); } 
  public void testCallToMethodParameter() { doTest(); } 
  public void testCallToFunctionVariable() { doTest(); } 
  public void testDirectlyInheritedMethodSet() { doTest(); } 
  public void testGrandParentDirectlyInheritedMethodSet() { doTest(); } 
  public void testCallToEmbeddedInterfaceMethod() { doTest(); } 
  public void testCallToFunctionWithSameNameAsMethod() { doTest(); } 
  public void testCallToMethodWithTheSameNameAsFunction() { doTest(); } 
  
  public void testConversionToImportedType() { doDirTest(); } 
  public void testConversionToImportedFunction() { doDirTest(); } 
  public void testNoConversionToBlankImportedType() { doDirTest(); } 
  public void testConversionToLocallyImportedType() { doDirTest(); } 
  public void testFunctionInSamePackageDifferentFile() { doDirTest(); } 
  public void testRelativePackageReference() { doDirTest(); }
  public void testRelativePackageReferenceDeep() { doDirTest(); } 
  public void testCallToFunctionWithSameNameAsMethodAcrossPackages() { doDirTest(); } 
  public void testCallToMethodViaShortVar() { doDirTest(); } 
  public void testImportedEmbeddedTypeMethod() { doDirTest(); } 
  public void testCallToMethodWithTheSameNameAsFunctionAcrossPackages() { doDirTest(); }
}
