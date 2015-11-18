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

package com.goide.dlv;

import com.goide.dlv.protocol.DlvApi;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class DlvSuspendContext extends XSuspendContext {
  @NotNull private final DlvExecutionStack myStack;

  public DlvSuspendContext(@NotNull DlvDebugProcess process,
                           int threadId,
                           @NotNull List<DlvApi.Location> locations,
                           @NotNull DlvCommandProcessor processor) {
    myStack = new DlvExecutionStack(process, threadId, locations, processor);
  }

  @Nullable
  @Override
  public XExecutionStack getActiveExecutionStack() {
    return myStack;
  }

  @NotNull
  @Override
  public XExecutionStack[] getExecutionStacks() {
    return new XExecutionStack[]{myStack};
  }

  private static class DlvExecutionStack extends XExecutionStack {
    @NotNull private final DlvDebugProcess myProcess;
    @NotNull private final List<DlvApi.Location> myLocations;
    @NotNull private final DlvCommandProcessor myProcessor;
    @NotNull private final List<DlvStackFrame> myStack;

    public DlvExecutionStack(@NotNull DlvDebugProcess process,
                             int threadId,
                             @NotNull List<DlvApi.Location> locations,
                             @NotNull DlvCommandProcessor processor) {
      super("Thread #" + threadId);
      myProcess = process;
      myLocations = locations;
      myProcessor = processor;
      myStack = ContainerUtil.newArrayListWithCapacity(locations.size());
      for (int i = 0; i < myLocations.size(); i++) {
        myStack.add(new DlvStackFrame(myProcess, myLocations.get(i), myProcessor, i));
      }
    }

    @Nullable
    @Override
    public XStackFrame getTopFrame() {
      return ContainerUtil.getFirstItem(myStack);
    }

    @Override
    public void computeStackFrames(int firstFrameIndex, @NotNull XStackFrameContainer container) {
      container.addStackFrames(myStack, true);
    }
  }
}
