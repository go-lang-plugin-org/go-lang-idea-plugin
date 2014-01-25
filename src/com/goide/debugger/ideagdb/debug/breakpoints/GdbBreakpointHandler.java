package com.goide.debugger.ideagdb.debug.breakpoints;

import com.goide.debugger.gdb.Gdb;
import com.goide.debugger.gdb.messages.GdbBreakpoint;
import com.goide.debugger.gdb.messages.GdbErrorEvent;
import com.goide.debugger.gdb.messages.GdbEvent;
import com.goide.debugger.ideagdb.debug.GdbDebugProcess;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.containers.BidirectionalMap;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GdbBreakpointHandler extends XBreakpointHandler<XLineBreakpoint<GdbBreakpointProperties>> {
  private static final Logger LOG = Logger.getInstance(GdbBreakpointHandler.class);

  private final Gdb myGdb;
  private final GdbDebugProcess myDebugProcess;

  // The breakpoints that have been set and their GDB breakpoint numbers
  private final BidirectionalMap<Integer, XLineBreakpoint<GdbBreakpointProperties>> myBreakpoints = new BidirectionalMap<Integer, XLineBreakpoint<GdbBreakpointProperties>>();

  public GdbBreakpointHandler(Gdb gdb, GdbDebugProcess debugProcess) {
    super(GdbBreakpointType.class);
    myGdb = gdb;
    myDebugProcess = debugProcess;
  }

  /**
   * Registers the given breakpoint with GDB.
   *
   * @param breakpoint The breakpoint.
   */
  @Override
  public void registerBreakpoint(
    @NotNull final XLineBreakpoint<GdbBreakpointProperties> breakpoint) {
    // TODO: I think we can use tracepoints here if the suspend policy isn't to stop the process

    // Check if the breakpoint already exists
    Integer number = findBreakpointNumber(breakpoint);
    if (number != null) {
      // Re-enable the breakpoint
      myGdb.sendCommand("-break-enable " + number);
    }
    else {
      // Set the breakpoint
      XSourcePosition sourcePosition = breakpoint.getSourcePosition();
      String command = "-break-insert -f " + sourcePosition.getFile().getPath() + ":" +
                       (sourcePosition.getLine() + 1);
      myGdb.sendCommand(command, new Gdb.GdbEventCallback() {
        @Override
        public void onGdbCommandCompleted(GdbEvent event) {
          onGdbBreakpointReady(event, breakpoint);
        }
      });
    }
  }

  /**
   * Unregisters the given breakpoint with GDB.
   *
   * @param breakpoint The breakpoint.
   * @param temporary  Whether we are deleting the breakpoint or temporarily disabling it.
   */
  @Override
  public void unregisterBreakpoint(@NotNull XLineBreakpoint<GdbBreakpointProperties> breakpoint,
                                   boolean temporary) {
    Integer number = findBreakpointNumber(breakpoint);
    if (number == null) {
      LOG.error("Cannot remove breakpoint; could not find it in breakpoint table");
      return;
    }

    if (!temporary) {
      // Delete the breakpoint
      myGdb.sendCommand("-break-delete " + number);
      synchronized (myBreakpoints) {
        myBreakpoints.remove(number);
      }
    }
    else {
      // Disable the breakpoint
      myGdb.sendCommand("-break-disable " + number);
    }
  }

  /**
   * Finds a breakpoint by its GDB number.
   *
   * @param number The GDB breakpoint number.
   * @return The breakpoint, or null if it could not be found.
   */
  public XLineBreakpoint<GdbBreakpointProperties> findBreakpoint(int number) {
    synchronized (myBreakpoints) {
      return myBreakpoints.get(number);
    }
  }

  /**
   * Finds a breakpoint's GDB number.
   *
   * @param breakpoint The breakpoint to search for.
   * @return The breakpoint number, or null if it could not be found.
   */
  public Integer findBreakpointNumber(XLineBreakpoint<GdbBreakpointProperties> breakpoint) {
    List<Integer> numbers;
    synchronized (myBreakpoints) {
      numbers = myBreakpoints.getKeysByValue(breakpoint);
    }

    if (numbers == null || numbers.isEmpty()) {
      return null;
    }
    else if (numbers.size() > 1) {
      LOG.warn("Found multiple breakpoint numbers for breakpoint; only returning the " +
                 "first");
    }
    return numbers.get(0);
  }

  /**
   * Callback function for when GDB has responded to our breakpoint request.
   *
   * @param event      The event.
   * @param breakpoint The breakpoint we tried to set.
   */
  private void onGdbBreakpointReady(GdbEvent event,
                                    XLineBreakpoint<GdbBreakpointProperties> breakpoint) {
    if (event instanceof GdbErrorEvent) {
      myDebugProcess.getSession().updateBreakpointPresentation(breakpoint,
                                                               AllIcons.Debugger.Db_invalid_breakpoint, ((GdbErrorEvent)event).message);
      return;
    }
    if (!(event instanceof GdbBreakpoint)) {
      myDebugProcess.getSession().updateBreakpointPresentation(breakpoint,
                                                               AllIcons.Debugger.Db_invalid_breakpoint,
                                                               "Unexpected data received from GDB");
      LOG.warn("Unexpected event " + event + " received from -break-insert request");
      return;
    }

    // Save the breakpoint
    GdbBreakpoint gdbBreakpoint = (GdbBreakpoint)event;
    if (gdbBreakpoint.number == null) {
      myDebugProcess.getSession().updateBreakpointPresentation(breakpoint,
                                                               AllIcons.Debugger.Db_invalid_breakpoint,
                                                               "No breakpoint number received from GDB");
      LOG.warn("No breakpoint number received from GDB after -break-insert request");
      return;
    }

    synchronized (myBreakpoints) {
      myBreakpoints.put(gdbBreakpoint.number, breakpoint);
    }

    // Mark the breakpoint as set
    // TODO: Don't do this yet if the breakpoint is pending
    myDebugProcess.getSession().updateBreakpointPresentation(breakpoint,
                                                             AllIcons.Debugger.Db_verified_breakpoint, null);
  }
}
