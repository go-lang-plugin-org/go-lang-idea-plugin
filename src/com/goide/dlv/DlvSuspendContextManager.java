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

import com.goide.dlv.rdp.*;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.AsyncFunction;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.CallFrame;
import org.jetbrains.debugger.DebugEventListener;
import org.jetbrains.debugger.StepAction;
import org.jetbrains.debugger.SuspendContextManagerBase;
import org.jetbrains.rpc.CommandProcessor;

import java.util.concurrent.atomic.AtomicReference;

public class DlvSuspendContextManager extends SuspendContextManagerBase<DlvSuspendContext, DlvCallFrame> {
  private final DlvVm vm;

  final AtomicReference<AsyncPromise<CompletionValueYetAnotherPoorFirefoxRdpStructure>> clientEvaluate = new AtomicReference<AsyncPromise<CompletionValueYetAnotherPoorFirefoxRdpStructure>>();

  DlvSuspendContextManager(@NotNull DlvVm vm) {
    this.vm = vm;
  }

  @Override
  protected DebugEventListener getDebugListener() {
    return vm.getDebugListener();
  }

  public void mainFrameUpdated() {
    try {
      dismissContext();
    }
    finally {
      AsyncPromise<CompletionValueYetAnotherPoorFirefoxRdpStructure> promise = clientEvaluate.getAndSet(null);
      if (promise != null) {
        promise.setError(Promise.createError("Frame updated"));
      }
    }
  }

  public void paused(@NotNull final ThreadInterrupted event) {
    if (event.why().type() == ThreadInterrupted.Reason.Type.CLIENT_EVALUATED) {
      AsyncPromise<CompletionValueYetAnotherPoorFirefoxRdpStructure> promise = clientEvaluate.getAndSet(null);
      CommandProcessor.LOG.assertTrue(promise != null);
      CompletionValueYetAnotherPoorFirefoxRdpStructure evaluatedResult = event.why().frameFinished();
      CommandProcessor.LOG.assertTrue(evaluatedResult != null);
      if (evaluatedResult.terminated()) {
        promise.setError(Promise.createError("Terminated"));
      }
      else {
        promise.setResult(evaluatedResult);
      }
      return;
    }

    final Frame topFrame = event.frame();
    if (topFrame == null) {
      vm.commandProcessor.send(DlvRequest.getFrames(vm.threadActor))
        .done(new Consumer<FramesResult>() {
          @Override
          public void consume(FramesResult result) {
            DlvValueManager valueManager = new DlvValueManager(vm);
            CallFrame[] frames = DlvSuspendContext.createFrames(result, null, valueManager);
            doPaused(event, null, frames);
          }
        });
    }
    else {
      doPaused(event, topFrame, null);
    }
  }

  private void doPaused(@NotNull ThreadInterrupted event, @Nullable Frame topFrame, @Nullable CallFrame[] frames) {
    AsyncPromise<Void> callback = suspendCallback.getAndSet(null);
    DlvSuspendContext context = new DlvSuspendContext(topFrame, frames, new DlvValueManager(vm), event, callback != null);
    setContext(context);

    if (callback != null) {
      callback.setResult(null);
    }

    vm.getDebugListener().suspended(context);
  }

  @NotNull
  @Override
  protected Promise<?> doSuspend() {
    return vm.commandProcessor.send(DlvRequest.interrupt(vm.threadActor));
  }

  @NotNull
  @Override
  public Promise<Void> continueVm(@NotNull final StepAction stepAction, int stepCount) {
    return getContextOrFail().getValueManager().release()
      .then(new AsyncFunction<Void, Void>() {
        @NotNull
        @Override
        public Promise<Void> fun(Void aVoid) {
          return dismissContextOnDone(vm.commandProcessor.send(
            DlvRequest.resumeThread(vm.threadActor, stepAction, true))); // todo: vm.commandProcessor.isBreakOnException()
        }
      });
  }
}
