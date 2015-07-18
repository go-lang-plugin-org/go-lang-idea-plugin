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

public class GoLegacyResolveCallsTest extends GoLegacyResolveTestBase {
  @Override
  protected String getBasePath() { return "psi/resolve/calls"; }

  public void testCallToLocalMethodByPointer()                           { doFileTest(false); } 
  public void testCallToLocalMethod()                                    { doFileTest(false); } 
  public void testCallToLocalMethodNested()                              { doFileTest(false); } 
  public void testCallToLocalMethodViaMap()                              { doFileTest(false); } 
  public void testCallToLocalMethodViaShortVarDeclaration()              { doFileTest(false); } 
  public void testCallToLocalMethodViaSlice()                            { doFileTest(false); } 
  public void testCallToLocalMethodViaTypeAssert()                       { doFileTest(false); } 
  public void testCallToLocalInterfaceMethod()                           { doFileTest(false); } 
  public void testCallToLocalInterfaceMethodNested()                     { doFileTest(false); } 
  public void testCallToLocalInterfaceMethodViaMap()                     { doFileTest(false); } 
  public void testCallToLocalInterfaceMethodViaSlice()                   { doFileTest(false); } 
  public void testCallToLocalInterfaceMethodViaTypeAssert()              { doFileTest(false); } 
  public void testCallToLocalFunction()                                  { doFileTest(false); } 
  public void testCallToFunctionLiteral()                                { doFileTest(false); } 
  public void testTypeConversionToLocalType()                            { doFileTest(false); } 
  public void testRecursiveMethodCall()                                  { doFileTest(false); } 
  public void testCallToMethodParameter()                                { doFileTest(false); } 
  public void testCallToFunctionVariable()                               { doFileTest(false); } 
  public void testDirectlyInheritedMethodSet()                           { doFileTest(false); } 
  public void testGrandParentDirectlyInheritedMethodSet()                { doFileTest(false); } 
  public void testCallToEmbeddedInterfaceMethod()                        { doFileTest(false); } 
  public void testCallToFunctionWithSameNameAsMethod()                   { doFileTest(false); } 
  public void testCallToMethodWithTheSameNameAsFunction()                { doFileTest(false); } 

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
}
