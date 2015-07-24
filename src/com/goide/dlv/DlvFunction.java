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

package com.goide.dlv;

import com.goide.dlv.rdp.Grip;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.Scope;
import org.jetbrains.debugger.values.FunctionValue;
import org.jetbrains.debugger.values.ValueType;

public class DlvFunction extends DlvObject implements FunctionValue {
  public DlvFunction(@NotNull Grip valueData, @NotNull DlvValueManager valueManager) {
    super(ValueType.FUNCTION, valueData, valueManager);
  }

  @NotNull
  @Override
  public Promise<FunctionValue> resolve() {
    return Promise.<FunctionValue>resolve(this);
  }

  @Override
  public int getOpenParenLine() {
    return 0;
  }

  @Override
  public int getOpenParenColumn() {
    return 0;
  }

  @Nullable
  @Override
  public Scope[] getScopes() {
    return new Scope[0];
  }

  @NotNull
  @Override
  public ThreeState hasScopes() {
    // todo
    return ThreeState.NO;
  }
}
