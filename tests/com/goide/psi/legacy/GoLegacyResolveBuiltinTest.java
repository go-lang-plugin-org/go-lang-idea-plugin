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

import com.goide.SdkAware;
import org.jetbrains.annotations.NotNull;

@SdkAware
public class GoLegacyResolveBuiltinTest extends GoLegacyResolveTestBase {
  @NotNull
  @Override
  protected String getBasePath() { return "psi/resolve/builtin"; }

  @Override
  protected boolean allowNullDefinition() {
    return true;
  }
  
  public void testMethodName()        { doFileTest(); } 
  public void testBuiltinTypes()      { doFileTest(); } 
  public void testBuiltinConversion() { doFileTest(); } 
  public void testVarBuiltinType()    { doFileTest(); } 
  public void testVarMethodType()     { doFileTest(); } 
  public void testParameterType()     { doFileTest(); }
}
