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
import com.goide.GoIcons;
import com.goide.GoLanguage;
import com.goide.dlv.protocol.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.io.socketConnection.ConnectionStatus;
import com.intellij.util.io.socketConnection.SocketConnectionListener;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation;
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.DebugProcessImpl;
import org.jetbrains.debugger.connection.RemoteVmConnection;

import javax.swing.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DlvDebugProcess extends DebugProcessImpl<RemoteVmConnection> implements Disposable {

  public static final Consumer<Throwable> THROWABLE_CONSUMER = new Consumer<Throwable>() {
    @Override
    public void consume(@NotNull Throwable throwable) {
      throwable.printStackTrace();
    }
  };

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

    if (setBreakpoints) {
      doSetBreakpoints();
      resume();
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

  @NotNull Set<XBreakpoint<DlvLineBreakpointProperties>> breakpoints = ContainerUtil.newConcurrentSet();


  public void addBreakpoint(@NotNull final XLineBreakpoint<DlvLineBreakpointProperties> breakpoint) {
    XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
    if (breakpointPosition == null) return;
    VirtualFile file = breakpointPosition.getFile();
    int line = breakpointPosition.getLine();
    final DlvVm vm = (DlvVm)getVm();
    Promise<Api.Breakpoint> promise = vm.getCommandProcessor().send(new DlvSetBreakpoint(file.getCanonicalPath(), line + 1));
    promise.processed(new Consumer<Api.Breakpoint>() {
      @Override
      public void consume(@Nullable Api.Breakpoint b) {
        if (b != null) {
          breakpoint.putUserData(ID, b.id);
          breakpoints.add(breakpoint);
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
    breakpoints.remove(breakpoint);
    if (id == null) return;
    DlvVm vm = (DlvVm)getVm();
    Promise<Api.Breakpoint> promise = vm.getCommandProcessor().send(new DlvClearBreakpoint(id));
    promise.rejected(THROWABLE_CONSUMER);
  }

  @Override
  public void resume() {
    DlvVm vm = (DlvVm)getVm();
    final DlvCommandProcessor processor = vm.getCommandProcessor();
    Promise<Api.DebuggerState> promise = processor.send(new DlvCommandRequest(Api.CONTINUE));
    promise.processed(new Consumer<Api.DebuggerState>() {
      @Override
      public void consume(@Nullable final Api.DebuggerState o) {
        if (o == null || o.exited) {
          getSession().stop();
          return;
        }

        Api.Breakpoint bp = o.breakPoint;
        if (bp == null) return;
        final int id = bp.id;
        final XBreakpoint<DlvLineBreakpointProperties> find =
          ContainerUtil.find(breakpoints, new Condition<XBreakpoint<DlvLineBreakpointProperties>>() {
            @Override
            public boolean value(@NotNull XBreakpoint<DlvLineBreakpointProperties> b) {
              return Comparing.equal(b.getUserData(ID), id);
            }
          });
        if (find == null) return;
        final Promise<List<Api.Location>> stackPromise = processor.send(new DlvStacktraceRequest());
        stackPromise.processed(new Consumer<List<Api.Location>>() {
          @Override
          public void consume(@NotNull List<Api.Location> locations) {
            getSession().breakpointReached(find, null, new DlvSuspendContext(o.currentThread.id, locations, processor));
          }
        });
        stackPromise.rejected(THROWABLE_CONSUMER);
      }
    });
    promise.rejected(THROWABLE_CONSUMER);
  }

  private static class DlvSuspendContext extends XSuspendContext {
    @NotNull private final DlvExecutionStack myStack;

    public DlvSuspendContext(int threadId, @NotNull List<Api.Location> locations, DlvCommandProcessor processor) {
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
          if (!myStack.isEmpty()) {
            location.line -= 1; // todo: bizarre
          }
          myStack.add(new DlvStackFrame(location, myProcessor));
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

      private static class DlvStackFrame extends XStackFrame {
        private final Api.Location myLocation;
        private final DlvCommandProcessor myProcessor;

        public DlvStackFrame(Api.Location location, DlvCommandProcessor processor) {
          myLocation = location;
          myProcessor = processor;
        }

        @Nullable
        @Override
        public XSourcePosition getSourcePosition() {
          final String url = myLocation.file;
          final VirtualFile file = LocalFileSystem.getInstance().findFileByPath(url);
          if (file == null) return null;
          return XDebuggerUtil.getInstance().createPosition(file, myLocation.line);
        }

        @Override
        public void customizePresentation(@NotNull ColoredTextContainer component) {
          super.customizePresentation(component);
          component.append(" at " + myLocation.function.name, SimpleTextAttributes.REGULAR_ATTRIBUTES);
          component.setIcon(AllIcons.Debugger.StackFrame);
        }

        @Override
        public void computeChildren(@NotNull final XCompositeNode node) {
          final Promise<List<Api.Variable>> varPromise = myProcessor.send(new DlvLocalsRequest.DlvLocalVarsRequest());
          varPromise.processed(new Consumer<List<Api.Variable>>() {
            @Override
            public void consume(@NotNull List<Api.Variable> variables) {
              final XValueChildrenList xVars = new XValueChildrenList(variables.size());
              for (Api.Variable v : variables) {
                xVars.add(v.name, getVariableValue(v.name, v.value, v.type, GoIcons.VARIABLE));
              }

              final Promise<List<Api.Variable>> argsPromise = myProcessor.send(new DlvLocalsRequest.DlvFunctionArgsRequest());
              argsPromise.processed(new Consumer<List<Api.Variable>>() {
                @Override
                public void consume(List<Api.Variable> args) {
                  for (Api.Variable v : args) {
                    xVars.add(v.name, getVariableValue(v.name, v.value, v.type, GoIcons.PARAMETER));
                  }
                  node.addChildren(xVars, true);
                }
              });
              argsPromise.rejected(THROWABLE_CONSUMER);
            }
          });
          varPromise.rejected(THROWABLE_CONSUMER);
        }

        @NotNull
        private static XValue getVariableValue(@NotNull String name,
                                               @NotNull final String value,
                                               @Nullable final String type, 
                                               @Nullable final Icon icon) {
          return new XNamedValue(name) {
            @Override
            public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
              final XValuePresentation presentation = getPresentation();
              if (presentation != null) {
                node.setPresentation(icon, presentation, false);
                return;
              }
              node.setPresentation(icon, type, value, false);
            }

            @Nullable
            private XValuePresentation getPresentation() {
              if ("struct string".equals(type)) return new XStringValuePresentation(value);
              if ("int".equals(type)) return new XNumericValuePresentation(value);
              return null;
            }
          };
        }
      }
    }
  }
}