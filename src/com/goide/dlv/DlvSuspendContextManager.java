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
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.*;

public class DlvSuspendContextManager extends SuspendContextManagerBase<SuspendContextBase, CallFrame> {
  @NotNull private final DlvVm vm;

  DlvSuspendContextManager(@NotNull DlvVm vm) {
    this.vm = vm;
  }

  @NotNull
  @Override
  protected DebugEventListener getDebugListener() {
    return vm.getDebugListener();
  }

  @NotNull
  @Override
  protected Promise<?> doSuspend() {
    return Promise.resolve(null);
  }

  @NotNull
  @Override
  public Promise<Void> continueVm(@NotNull final StepAction stepAction, int stepCount) {
    return Promise.DONE;
  }
}
