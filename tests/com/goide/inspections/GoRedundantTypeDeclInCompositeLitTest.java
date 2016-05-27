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

package com.goide.inspections;

import com.goide.quickfix.GoQuickFixTestBase;
import org.jetbrains.annotations.NotNull;

public class GoRedundantTypeDeclInCompositeLitTest extends GoQuickFixTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoRedundantTypeDeclInCompositeLit.class);
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  //Test Composite literals
  public void testCompositeLitWithOneSimpleRedundantTypeDecl() {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  public void testCompositeLitWithKeysAndSimpleRedundantTypeDecl() {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  public void testCompositeLitWithInterface() {
    doTestNoFix(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  public void testCompositeLitWithArrays() {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  public void testCompositeLitWithArraysInBrackets() {
    doTestNoFix(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  public void testCompositeLitWithMap() {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  /*public void testCompositeLitWithInsertedArrays () {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  public void testCompositeLitWithStruct() {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  public void testCompositeLitWithMapOfStruct() {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  public void testCompositeLitWithTwoDimensionalArray() {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }*/

  //Test Composite literals with pointers *T == &T
  public void testCompositeLitWithPointers() {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  public void testCompositeLitWithPointersNoFix() {
    doTestNoFix(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  /*public void testCompositeLitWithPointerAndStructAndInsertedElement() {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }

  public void testCompositeLitWithMapWithPointerAndStruct() {
    doTest(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME, true);
  }*/

  @NotNull
  @Override
  protected String getBasePath() {
    return "inspections/go-simplify";
  }
}
