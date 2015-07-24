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

import com.goide.dlv.rdp.DlvRequest;
import com.goide.dlv.rdp.Frame;
import com.goide.dlv.rdp.FramesResult;
import com.goide.dlv.rdp.ThreadInterrupted;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.PromiseManager;
import org.jetbrains.debugger.Breakpoint;
import org.jetbrains.debugger.CallFrame;
import org.jetbrains.debugger.ExceptionData;
import org.jetbrains.debugger.SuspendContextBase;
import org.jetbrains.rpc.CommandProcessor;

import java.util.Collections;
import java.util.List;

public class DlvSuspendContext extends SuspendContextBase<DlvValueManager> {
  private static final Logger LOG = Logger.getInstance(CommandProcessor.class);

  private static final PromiseManager<DlvSuspendContext, CallFrame[]>
    CALL_FRAMES_LOADER = new PromiseManager<DlvSuspendContext, CallFrame[]>(DlvSuspendContext.class) {
    @NotNull
    @Override
    public Promise<CallFrame[]> load(@NotNull final DlvSuspendContext context) {
      DlvVm vm = context.getValueManager().getVm();
      return vm.commandProcessor.send(DlvRequest.getFrames(vm.threadActor, 1))
        .then(new Function<FramesResult, CallFrame[]>() {
          @Override
          public CallFrame[] fun(FramesResult result) {
            return createFrames(result, context.topFrame, context.valueManager);
          }
        });
    }
  };

  @NotNull
  static CallFrame[] createFrames(@NotNull FramesResult result, @Nullable CallFrame topFrame, @NotNull DlvValueManager valueManager) {
    int offset = topFrame == null ? 0 : 1;
    CallFrame[] frames = new DlvCallFrame[result.frames().size() + offset];
    if (topFrame != null) {
      frames[0] = topFrame;
    }
    List<Frame> vmFrames = result.frames();
    // useBindingsFromFrameData only for top frame
    boolean useBindingsFromFrameData = topFrame == null;
    for (int i = 0, n = vmFrames.size(); i < n; i++) {
      frames[i + offset] = new DlvCallFrame(valueManager, vmFrames.get(i), useBindingsFromFrameData);
      useBindingsFromFrameData = false;
    }
    return frames;
  }

  private final CallFrame topFrame;

  @SuppressWarnings("UnusedDeclaration")
  @Nullable
  volatile Promise<CallFrame[]> frames;

  private final List<Breakpoint> breakpointsHit;

  DlvSuspendContext(@Nullable Frame topVmFrame,
                    @Nullable CallFrame[] frames,
                    @NotNull DlvValueManager valueManager,
                    @NotNull ThreadInterrupted event,
                    boolean explicitPaused) {
    super(valueManager, explicitPaused);

    if (frames == null) {
      LOG.assertTrue(topVmFrame != null);
      topFrame = new DlvCallFrame(valueManager, topVmFrame, true);
    }
    else {
      topFrame = frames[0];
      CALL_FRAMES_LOADER.set(this, frames);
    }

    ThreadInterrupted.Reason.Type reason = event.why().type();
    if (reason == ThreadInterrupted.Reason.Type.BREAKPOINT) {
      breakpointsHit = valueManager.getVm().getBreakpointManager().findRelatedBreakpoints(event.why().actors());
    }
    else {
      breakpointsHit = Collections.emptyList();
      if (reason != ThreadInterrupted.Reason.Type.RESUME_LIMIT && reason != ThreadInterrupted.Reason.Type.DEBUGGER_STATEMENT) {
        CommandProcessor.LOG.error("Unsupported paused event: " + reason);
      }
    }
  }

  @Nullable
  @Override
  public ExceptionData getExceptionData() {
    return null;
  }

  @Nullable
  @Override
  public CallFrame getTopFrame() {
    return topFrame;
  }

  @NotNull
  @Override
  public Promise<CallFrame[]> getFrames() {
    return CALL_FRAMES_LOADER.get(this);
  }

  @NotNull
  @Override
  public List<Breakpoint> getBreakpointsHit() {
    return breakpointsHit;
  }
}
