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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.CallFrame;
import org.jetbrains.debugger.Script;
import org.jetbrains.debugger.ScriptBase;
import org.jetbrains.debugger.ScriptManagerBaseEx;
import org.jetbrains.debugger.values.FunctionValue;

public class DlvScriptManager extends ScriptManagerBaseEx<ScriptBase> {
  @Override
  public boolean containsScript(@NotNull Script script) {
    return true;
  }

  @NotNull
  @Override
  public Promise setSourceOnRemote(@NotNull Script script, @NotNull CharSequence newSource, boolean preview) {
    return Promise.DONE;
  }

  @NotNull
  @Override
  public Promise<Script> getScript(@NotNull FunctionValue function) {
    return Promise.resolve(null);
  }

  @Nullable
  @Override
  public Script getScript(@NotNull CallFrame frame) {
    return null;
  }

  @NotNull
  @Override
  protected Promise<String> loadScriptSource(@NotNull ScriptBase script) {
    return Promise.resolve("");
  }
}