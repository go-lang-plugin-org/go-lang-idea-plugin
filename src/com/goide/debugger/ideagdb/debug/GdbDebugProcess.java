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

package com.goide.debugger.ideagdb.debug;

import com.goide.debugger.gdb.Gdb;
import com.goide.debugger.gdb.GdbListener;
import com.goide.debugger.gdb.gdbmi.GdbMiResultRecord;
import com.goide.debugger.gdb.gdbmi.GdbMiStreamRecord;
import com.goide.debugger.gdb.messages.*;
import com.goide.debugger.ideagdb.debug.breakpoints.GdbBreakpointHandler;
import com.goide.debugger.ideagdb.debug.breakpoints.GdbBreakpointProperties;
import com.goide.debugger.ideagdb.run.GdbExecutionResult;
import com.goide.debugger.ideagdb.run.GdbRunConfiguration;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.content.Content;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.ui.XDebugTabLayouter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GdbDebugProcess extends XDebugProcess implements GdbListener {
  private static final Logger LOG = Logger.getInstance(GdbDebugProcess.class);

  private final GdbDebuggerEditorsProvider myEditorsProvider = new GdbDebuggerEditorsProvider();
  @NotNull private final ConsoleView myConsole;
  private final GdbRunConfiguration myConfiguration;
  @NotNull private final GdbConsoleView myGdbConsole;
  @NotNull private final Gdb myGdb;
  @NotNull private final GdbBreakpointHandler myBreakpointHandler;
  private final SimpleDateFormat myTimeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

  /**
   * Constructor; launches GDB.
   */
  public GdbDebugProcess(@NotNull XDebugSession session, @NotNull GdbExecutionResult executionResult) {
    super(session);
    myConfiguration = executionResult.getConfiguration();
    myConsole = (ConsoleView)executionResult.getExecutionConsole();

    // TODO: Make this an option on the run configuration
    String workingDirectory = new File(myConfiguration.APP_PATH).getParent();
    myGdb = new Gdb(myConfiguration.GDB_PATH, workingDirectory, this);
    myGdbConsole = new GdbConsoleView(myGdb, session.getProject());
    myBreakpointHandler = new GdbBreakpointHandler(myGdb, this);
    myGdb.start();
  }

  @NotNull
  @Override
  public XBreakpointHandler<?>[] getBreakpointHandlers() {
    return new GdbBreakpointHandler[]{myBreakpointHandler};
  }

  @NotNull
  @Override
  public XDebuggerEditorsProvider getEditorsProvider() {
    return myEditorsProvider;
  }

  /**
   * Steps over the next line.
   */
  @Override
  public void startStepOver() {
    myGdb.sendCommand("-exec-next");
  }

  @Override
  public void startPausing() {
    // TODO: GDB doesn't support handling commands when the target is running; we should use
    // asynchronous mode if the target supports it. I'm not really sure how to deal with this on
    // other targets (e.g., Windows)
    LOG.warn("startPausing: stub");
  }

  /**
   * Steps into the next line.
   */
  @Override
  public void startStepInto() {
    myGdb.sendCommand("-exec-step");
  }

  /**
   * Steps out of the current function.
   */
  @Override
  public void startStepOut() {
    myGdb.sendCommand("-exec-finish");
  }

  /**
   * Stops program execution and exits GDB.
   */
  @Override
  public void stop() {
    myGdb.sendCommand("-gdb-exit");
  }

  /**
   * Resumes program execution.
   */
  @Override
  public void resume() {
    myGdb.sendCommand("-exec-continue --all");
  }

  @Override
  public void runToPosition(@NotNull XSourcePosition position) {
    LOG.warn("runToPosition: stub");
  }

  @NotNull
  @Override
  public ExecutionConsole createConsole() {
    return myConsole;
  }

  @NotNull
  @Override
  public XDebugTabLayouter createTabLayouter() {
    return new XDebugTabLayouter() {
      @Override
      public void registerAdditionalContent(@NotNull RunnerLayoutUi ui) {
        Content gdbConsoleContent = ui.createContent("GdbConsoleContent",
                                                     myGdbConsole.getComponent(), "GDB Console", AllIcons.Debugger.Console,
                                                     myGdbConsole.getPreferredFocusableComponent());
        gdbConsoleContent.setCloseable(false);

        // Create the actions
        final DefaultActionGroup consoleActions = new DefaultActionGroup();
        AnAction[] actions = myGdbConsole.getConsole().createConsoleActions();
        for (AnAction action : actions) {
          consoleActions.add(action);
        }
        gdbConsoleContent.setActions(consoleActions, ActionPlaces.DEBUGGER_TOOLBAR,
                                     myGdbConsole.getConsole().getPreferredFocusableComponent());

        ui.addContent(gdbConsoleContent, 2, PlaceInGrid.bottom, false);
      }
    };
  }

  /**
   * Called when a GDB error occurs.
   *
   * @param ex The exception
   */
  @Override
  public void onGdbError(final Throwable ex) {
    LOG.error("GDB error", ex);
  }

  /**
   * Called when GDB has started.
   */
  @Override
  public void onGdbStarted() {
    // Send startup commands
    String[] commandsArray = myConfiguration.STARTUP_COMMANDS.split("\\r?\\n");
    for (String command : commandsArray) {
      command = command.trim();
      if (!command.isEmpty()) {
        myGdb.sendCommand(command);
      }
    }
  }

  /**
   * Called whenever a command is sent to GDB.
   *
   * @param command The command that was sent.
   * @param token   The token the command was sent with.
   */
  @Override
  public void onGdbCommandSent(String command, long token) {
    myGdbConsole.getConsole().print(myTimeFormat.format(new Date()) + " " + token + "> " +
                                    command + "\n", ConsoleViewContentType.USER_INPUT);
  }

  /**
   * Called when a GDB event is received.
   *
   * @param event The event.
   */
  @Override
  public void onGdbEventReceived(GdbEvent event) {
    if (event instanceof GdbStoppedEvent) {
      // Target has stopped
      onGdbStoppedEvent((GdbStoppedEvent)event);
    }
    else if (event instanceof GdbRunningEvent) {
      // Target has started
      getSession().sessionResumed();
    }
  }

  /**
   * Handles a 'target stopped' event from GDB.
   *
   * @param event The event
   */
  private void onGdbStoppedEvent(@NotNull final GdbStoppedEvent event) {
    if (myGdb.hasCapability("thread-info")) {
      // Get information about the threads
      myGdb.sendCommand("-thread-info", new Gdb.GdbEventCallback() {
        @Override
        public void onGdbCommandCompleted(GdbEvent threadInfoEvent) {
          onGdbThreadInfoReady(threadInfoEvent, event);
        }
      });
    }
    else {
      // Handle it immediately without any thread data
      handleTargetStopped(event, null);
    }
  }

  /**
   * Called when a stream record is received.
   *
   * @param record The record.
   */
  @Override
  public void onStreamRecordReceived(@NotNull GdbMiStreamRecord record) {
    // Log the record
    switch (record.type) {
      case Console:
        StringBuilder sb = new StringBuilder();
        if (record.userToken != null) {
          sb.append("<");
          sb.append(record.userToken);
          sb.append(" ");
        }
        sb.append(record.message);
        myGdbConsole.getConsole().print(sb.toString(), ConsoleViewContentType.NORMAL_OUTPUT);
        break;

      case Target:
        myConsole.print(record.message, ConsoleViewContentType.NORMAL_OUTPUT);
        break;

      case Log:
        myGdbConsole.getConsole().print(record.message, ConsoleViewContentType.SYSTEM_OUTPUT);
        break;
    }
  }

  /**
   * Called when a result record is received.
   *
   * @param record The record.
   */
  @Override
  public void onResultRecordReceived(@NotNull GdbMiResultRecord record) {
    // Log the record
    StringBuilder sb = new StringBuilder();
    sb.append(myTimeFormat.format(new Date()));
    sb.append(" ");
    if (record.userToken != null) {
      sb.append("<");
      sb.append(record.userToken);
      sb.append(" ");
    }
    else {
      sb.append("< ");
    }

    switch (record.type) {
      case Immediate:
        sb.append("[immediate] ");
        break;

      case Exec:
        sb.append("[exec] ");
        break;

      case Notify:
        sb.append("[notify] ");
        break;

      case Status:
        sb.append("[status] ");
        break;
    }

    sb.append(record);
    sb.append("\n");
    myGdbConsole.getConsole().print(sb.toString(), ConsoleViewContentType.SYSTEM_OUTPUT);
  }

  /**
   * Callback function for when GDB has responded to our thread information request.
   *
   * @param threadInfoEvent The event.
   * @param stoppedEvent    The 'target stopped' event that caused us to make the request.
   */
  private void onGdbThreadInfoReady(GdbEvent threadInfoEvent, @NotNull GdbStoppedEvent stoppedEvent) {
    List<GdbThread> threads = null;

    if (threadInfoEvent instanceof GdbErrorEvent) {
      LOG.warn("Failed to get thread information: " +
               ((GdbErrorEvent)threadInfoEvent).message);
    }
    else if (!(threadInfoEvent instanceof GdbThreadInfo)) {
      LOG.warn("Unexpected event " + threadInfoEvent + " received from -thread-info " +
               "request");
    }
    else {
      threads = ((GdbThreadInfo)threadInfoEvent).threads;
    }

    // Handle the event
    handleTargetStopped(stoppedEvent, threads);
  }

  /**
   * Handles a 'target stopped' event.
   *
   * @param stoppedEvent The event.
   * @param threads      Thread information, if available.
   */
  private void handleTargetStopped(@NotNull GdbStoppedEvent stoppedEvent, List<GdbThread> threads) {
    GdbSuspendContext suspendContext = new GdbSuspendContext(myGdb, stoppedEvent, threads);

    // Find the breakpoint if necessary
    XBreakpoint<GdbBreakpointProperties> breakpoint = null;
    if (stoppedEvent.reason == GdbStoppedEvent.Reason.BreakpointHit &&
        stoppedEvent.breakpointNumber != null) {
      breakpoint = myBreakpointHandler.findBreakpoint(stoppedEvent.breakpointNumber);
    }

    if (breakpoint != null) {
      // TODO: Support log expressions
      boolean suspendProcess = getSession().breakpointReached(breakpoint, null,
                                                              suspendContext);
      if (!suspendProcess) {
        // Resume execution
        resume();
      }
    }
    else {
      getSession().positionReached(suspendContext);
    }
  }

  @NotNull
  public Gdb getGdb() {
    return myGdb;
  }
}
