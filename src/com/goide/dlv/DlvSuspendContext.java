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

import com.goide.dlv.protocol.Api;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class DlvSuspendContext extends XSuspendContext {
  @NotNull private final DlvExecutionStack myStack;

  public DlvSuspendContext(int threadId, @NotNull List<Api.Location> locations, @NotNull DlvCommandProcessor processor) {
    myStack = new DlvExecutionStack(threadId, locations, processor);
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
    @NotNull private final List<Api.Location> myLocations;
    private final DlvCommandProcessor myProcessor;
    @NotNull private final List<DlvStackFrame> myStack;

    public DlvExecutionStack(int threadId, @NotNull List<Api.Location> locations, DlvCommandProcessor processor) {
      super("Thread #" + threadId);
      myLocations = locations;
      myProcessor = processor;
      myStack = ContainerUtil.newArrayListWithCapacity(locations.size());
      for (Api.Location location : myLocations) {
        boolean top = myStack.isEmpty();
        if (!top) {
          location.line -= 1; // todo: bizarre
        }
        myStack.add(new DlvStackFrame(location, myProcessor, top));
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
