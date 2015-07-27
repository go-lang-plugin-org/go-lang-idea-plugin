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

import com.goide.GoFileType;
import com.goide.GoLanguage;
import com.goide.dlv.protocol.Breakpoint;
import com.goide.dlv.protocol.DlvClearBreakpoint;
import com.goide.dlv.protocol.DlvSetBreakpoint;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.Consumer;
import com.intellij.util.io.socketConnection.ConnectionStatus;
import com.intellij.util.io.socketConnection.SocketConnectionListener;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.DebugProcessImpl;
import org.jetbrains.debugger.connection.RemoteVmConnection;

import java.util.concurrent.atomic.AtomicBoolean;

public final class DlvDebugProcess extends DebugProcessImpl<RemoteVmConnection> implements Disposable {
  public DlvDebugProcess(@NotNull XDebugSession session,
                         @NotNull RemoteVmConnection connection) {
    super(session, connection, new XDebuggerEditorsProviderBase() {
      @NotNull
      @Override
      public FileType getFileType() {
        return GoFileType.INSTANCE;
      }

      @Override
      protected PsiFile createExpressionCodeFragment(@NotNull Project project,
                                                     @NotNull String text,
                                                     PsiElement context,
                                                     boolean isPhysical) {
        return PsiFileFactory.getInstance(project).createFileFromText("a.go", GoLanguage.INSTANCE, text);
      }
    }, null, null);
    breakpointHandlers = new XBreakpointHandler[]{new DlvLineBreakpointHandler(this)};
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
  public void resume() {
    // todo
  }

  @Override
  public void runToPosition(@NotNull XSourcePosition position) {
    // todo
  }

  @Override
  public void stop() {
    // todo:
  }

  private final AtomicBoolean breakpointsInitiated = new AtomicBoolean();
  private final AtomicBoolean connectedListenerAdded = new AtomicBoolean();

  
  @Override
  public boolean checkCanInitBreakpoints() {
    if (connection.getState().getStatus() == ConnectionStatus.CONNECTED) {
      // breakpointsInitiated could be set in another thread and at this point work (init breakpoints) could be not yet performed
      return initBreakpointHandlersAndSetBreakpoints(false);
    }

    if (connectedListenerAdded.compareAndSet(false, true)) {
      connection.addListener(new SocketConnectionListener() {
        @Override
        public void statusChanged(@NotNull ConnectionStatus status) {
          if (status == ConnectionStatus.CONNECTED) {
            initBreakpointHandlersAndSetBreakpoints(true);
          }
        }
      });
    }
    return false;
  }

  private boolean initBreakpointHandlersAndSetBreakpoints(boolean setBreakpoints) {
    if (!breakpointsInitiated.compareAndSet(false, true)) {
      return false;
    }

    assert getVm() != null : "Vm should be initialized";
    //BreakpointManager bm = getVm().getBreakpointManager();
    //bm.getBreakpoints()
    //LineBreakpointHandler lineBreakpointHandler = new LineBreakpointHandler(JavaScriptBreakpointType.class, bm, false);
    //exceptionBreakpointHandler = new ChromeExceptionBreakpointHandler(this);
    //
    //Set<Pair<Class<? extends XLineBreakpointType<?>>, Boolean>> additionalHandlers = null;
    //
    //breakpointHandlers = new XBreakpointHandler<?>[]{lineBreakpointHandler, exceptionBreakpointHandler};
    //
    //if (finder instanceof RemoteDebuggingFileFinder) {
    //  preloadedSourceMaps = new SourceMapCollector(this).collect(((RemoteDebuggingFileFinder)finder).getMappings());
    //}

    if (setBreakpoints) {
      doSetBreakpoints();
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

  public static final Key<Integer> ID = Key.create("ID");

  public void addBreakpoint(@NotNull final XLineBreakpoint<DlvLineBreakpointProperties> breakpoint) {
    XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
    if (breakpointPosition == null) return;
    VirtualFile file = breakpointPosition.getFile();
    int line = breakpointPosition.getLine();
    DlvVm vm = (DlvVm)getVm();
    Promise<Breakpoint> promise = vm.getCommandProcessor().send(new DlvSetBreakpoint(file.getCanonicalPath(), line + 1));
    promise.processed(new Consumer<Breakpoint>() {
      @Override
      public void consume(@Nullable Breakpoint b) {
        if (b != null) {
          breakpoint.putUserData(ID, b.id);
          getSession().updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_verified_breakpoint, null);
        }
      }
    });
    promise.rejected(new Consumer<Throwable>() {
      @Override
      public void consume(@Nullable Throwable t) {
        getSession().updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_invalid_breakpoint, t == null ? null : t.getMessage());
      }
    });
  }

  public void removeBreakpoint(@NotNull XLineBreakpoint<DlvLineBreakpointProperties> breakpoint) {
    XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
    if (breakpointPosition == null) return;
    Integer id = breakpoint.getUserData(ID);
    if (id == null) return;
    DlvVm vm = (DlvVm)getVm();
    Promise<Breakpoint> promise = vm.getCommandProcessor().send(new DlvClearBreakpoint(id));
    promise.processed(new Consumer<Breakpoint>() {
      @Override
      public void consume(Breakpoint b) {
        System.out.println(b);
      }
    });
    promise.rejected(new Consumer<Throwable>() {
      @Override
      public void consume(@NotNull Throwable throwable) {
        throwable.printStackTrace();
      }
    });
  }
}