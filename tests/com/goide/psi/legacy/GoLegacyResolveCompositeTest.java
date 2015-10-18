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

public class GoLegacyResolveCompositeTest extends GoLegacyResolveTestBase {
  @NotNull
  @Override
  protected String getBasePath() { return "psi/resolve/composite"; }

  public void testTypeName()                { doFileTest(); } 
  public void testTypeStruct()              { doFileTest(); } 
  public void testTypeStructArray()         { doFileTest(); } 
  public void testTypeStructSlice()         { doFileTest(); } 
  public void testTypeStructMap()           { doFileTest(); } 
  public void testTypeNameArray()           { doFileTest(); } 
  public void testTypeNameMap()             { doFileTest(); } 
  public void testTypeNameSlice()           { doFileTest(); } 
  public void testNestedStruct()            { doFileTest(); } 
  public void testNestedNamedStruct()       { doFileTest(); } 
  public void testKeyAsConstantExpression() { doFileTest(); } 
  public void testExpressionKey()           { doFileTest(); } 
  public void testPromotedAnonymousField1() { doFileTest(); } 
  public void testPromotedAnonymousField2() { doFileTest(); } 
  public void testPromotedAnonymousField3() { doFileTest(); } 
  public void testPromotedAnonymousField4() { doFileTest(); } 
  public void testTypeSwitch()              { doFileTest(); }
}
