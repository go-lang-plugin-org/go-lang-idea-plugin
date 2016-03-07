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

public class GoLegacyResolveStructTest extends GoLegacyResolveTestBase {
  @NotNull
  @Override
  protected String getBasePath() { return "psi/resolve/struct"; }

  public void testDirectStructField()                       { doFileTest(); } 
  public void testAnonymousDirectStructField()              { doFileTest(); } 
  public void testPromotedStructField()                     { doFileTest(); } 
  public void testStructFieldInMap()                        { doFileTest(); } 
  public void testFieldVsParam()                            { doFileTest(); } 
  public void testFieldVsParam2()                           { doFileTest(); }
  public void testFieldVsParam3()                           { doFileTest(); }
  public void testFieldVsParam4()                           { doFileTest(); }

  public void testDirectExportedFieldFromImportedPackage()  { doDirTest(); } 
  public void testDirectPrivateFieldFromImportedPackage()   { doDirTest(); }
}
