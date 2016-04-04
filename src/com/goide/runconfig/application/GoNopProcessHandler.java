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

package com.goide.runconfig.application;

import com.intellij.execution.process.ProcessHandler;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;

// todo[zolotov]: replace with built-in implementation in 2017.1
public class GoNopProcessHandler extends ProcessHandler {
  public GoNopProcessHandler() {
    destroyProcess();
  }

  @Override
  protected void destroyProcessImpl() {
    notifyProcessTerminated(0);
  }

  @Override
  protected void detachProcessImpl() {
    notifyProcessDetached();
  }

  @Override
  public boolean detachIsDefault() {
    return false;
  }

  @Nullable
  @Override
  public OutputStream getProcessInput() {
    return null;
  }
}
