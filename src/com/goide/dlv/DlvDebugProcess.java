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

package com.goide.dlv;

import com.goide.GoConstants;
import com.goide.GoFileType;
import com.goide.dlv.breakpoint.DlvBreakpointProperties;
import com.goide.dlv.breakpoint.DlvBreakpointType;
import com.goide.dlv.protocol.DlvRequest;
import com.goide.util.GoUtil;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.io.socketConnection.ConnectionStatus;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.DebugProcessImpl;
import org.jetbrains.debugger.Location;
import org.jetbrains.debugger.StepAction;
import org.jetbrains.debugger.Vm;
import org.jetbrains.debugger.connection.VmConnection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.goide.dlv.protocol.DlvApi.*;
import static com.intellij.util.ObjectUtils.assertNotNull;
import static com.intellij.util.ObjectUtils.tryCast;

public final class DlvDebugProcess extends DebugProcessImpl<VmConnection<?>> implements Disposable {
  public static final boolean IS_DLV_DISABLED = !GoConstants.AMD64.equals(GoUtil.systemArch());

  private final static Logger LOG = Logger.getInstance(DlvDebugProcess.class);
  private final AtomicBoolean breakpointsInitiated = new AtomicBoolean();
  private final AtomicBoolean connectedListenerAdded = new AtomicBoolean();
  private static final Consumer<Throwable> THROWABLE_CONSUMER = LOG::info;

  @NotNull
  private final Consumer<DebuggerState> myStateConsumer = new Consumer<DebuggerState>() {
    @Override
    public void consume(@NotNull DebuggerState o) {
      if (o.exited) {
        stop();
        return;
      }

      XBreakpoint<DlvBreakpointProperties> find = findBreak(o.breakPoint);
      send(new DlvRequest.StacktraceGoroutine()).done(locations -> {
          DlvSuspendContext context = new DlvSuspendContext(DlvDebugProcess.this, o.currentThread.id, locations, getProcessor());
          XDebugSession session = getSession();
          if (find == null) {
            session.positionReached(context);
          }
          else {
            session.breakpointReached(find, null, context);
          }
        });
    }

    @Nullable
    private XBreakpoint<DlvBreakpointProperties> findBreak(@Nullable Breakpoint point) {
      return point == null ? null : breakpoints.get(point.id);
    }
  };

  @NotNull
  private <T> Promise<T> send(@NotNull DlvRequest<T> request) {
    return send(request, getProcessor());
  }

  @NotNull
  static <T> Promise<T> send(@NotNull DlvRequest<T> request, @NotNull DlvCommandProcessor processor) {
    return processor.send(request).rejected(THROWABLE_CONSUMER);
  }

  @NotNull
  private DlvCommandProcessor getProcessor() {
    return assertNotNull(tryCast(getVm(), DlvVm.class)).getCommandProcessor();
  }

  public DlvDebugProcess(@NotNull XDebugSession session, @NotNull VmConnection<?> connection, @Nullable ExecutionResult er) {
    super(session, connection, new MyEditorsProvider(), null, er);
  }

  @NotNull
  @Override
  protected XBreakpointHandler<?>[] createBreakpointHandlers() {
    return new XBreakpointHandler[]{new MyBreakpointHandler()};
  }

  @NotNull
  @Override
  public ExecutionConsole createConsole() {
    ExecutionResult executionResult = getExecutionResult();
    return executionResult == null ? super.createConsole() : executionResult.getExecutionConsole();
  }

  @Override
  protected boolean isVmStepOutCorrect() {
    return false;
  }

  @Override
  public void dispose() {
    // todo
  }

  @Override
  public boolean checkCanInitBreakpoints() {
    if (getConnection().getState().getStatus() == ConnectionStatus.CONNECTED) {
      // breakpointsInitiated could be set in another thread and at this point work (init breakpoints) could be not yet performed
      return initBreakpointHandlersAndSetBreakpoints(false);
    }

    if (connectedListenerAdded.compareAndSet(false, true)) {
      getConnection().addListener(status -> {
        if (status == ConnectionStatus.CONNECTED) {
          initBreakpointHandlersAndSetBreakpoints(true);
        }
      });
    }
    return false;
  }

  private boolean initBreakpointHandlersAndSetBreakpoints(boolean setBreakpoints) {
    if (!breakpointsInitiated.compareAndSet(false, true)) return false;

    Vm vm = getVm();
    assert vm != null : "Vm should be initialized";

    if (setBreakpoints) {
      doSetBreakpoints();
      resume(vm);
    }

    return true;
  }

  private void doSetBreakpoints() {
    AccessToken token = ReadAction.start();
    try {
      getSession().initBreakpoints();
    }
    finally {
      token.finish();
    }
  }

  private void command(@NotNull @MagicConstant(stringValues = {NEXT, CONTINUE, HALT, SWITCH_THREAD, STEP}) String name) {
    send(new DlvRequest.Command(name)).done(myStateConsumer);
  }

  @Nullable
  @Override
  protected Promise<?> continueVm(@NotNull Vm vm, @NotNull StepAction stepAction) {
    switch (stepAction) {
      case CONTINUE:
        command(CONTINUE);
        break;
      case IN:
        command(STEP);
        break;
      case OVER:
        command(NEXT);
        break;
      case OUT:
        // todo
        break;
    }
    return null;
  }

  @NotNull
  @Override
  public List<Location> getLocationsForBreakpoint(@NotNull XLineBreakpoint<?> breakpoint) {
    return Collections.emptyList();
  }

  @Override
  public void runToPosition(@NotNull XSourcePosition position, @Nullable XSuspendContext context) {
    // todo
  }

  @Override
  public void stop() {
    if (getVm() != null) {
      send(new DlvRequest.Detach(true));
    }
    getSession().stop();
  }

  private static class MyEditorsProvider extends XDebuggerEditorsProviderBase {
    @NotNull
    @Override
    public FileType getFileType() {
      return GoFileType.INSTANCE;
    }

    @Override
    protected PsiFile createExpressionCodeFragment(@NotNull Project project,
                                                   @NotNull String text,
                                                   @Nullable PsiElement context,
                                                   boolean isPhysical) {
      return PsiFileFactory.getInstance(project).createFileFromText("dlv-debug.txt", PlainTextLanguage.INSTANCE, text);
    }
  }

  private static final Key<Integer> ID = Key.create("DLV_BP_ID");
  private final Map<Integer, XBreakpoint<DlvBreakpointProperties>> breakpoints = ContainerUtil.newConcurrentMap();

  private class MyBreakpointHandler extends XBreakpointHandler<XLineBreakpoint<DlvBreakpointProperties>> {

    public MyBreakpointHandler() {
      super(DlvBreakpointType.class);
    }

    @Override
    public void registerBreakpoint(@NotNull XLineBreakpoint<DlvBreakpointProperties> breakpoint) {
      XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
      if (breakpointPosition == null) return;
      VirtualFile file = breakpointPosition.getFile();
      int line = breakpointPosition.getLine();
      send(new DlvRequest.CreateBreakpoint(file.getPath(), line + 1))
        .done(b -> {
          breakpoint.putUserData(ID, b.id);
          breakpoints.put(b.id, breakpoint);
          getSession().updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_verified_breakpoint, null);
        })
        .rejected(t -> {
          String message = t == null ? null : t.getMessage();
          getSession().updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_invalid_breakpoint, message);
        });
    }

    @Override
    public void unregisterBreakpoint(@NotNull XLineBreakpoint<DlvBreakpointProperties> breakpoint, boolean temporary) {
      XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
      if (breakpointPosition == null) return;
      Integer id = breakpoint.getUserData(ID);
      if (id == null) return; // obsolete
      breakpoint.putUserData(ID, null);
      breakpoints.remove(id);
      send(new DlvRequest.ClearBreakpoint(id));
    }
  }
}