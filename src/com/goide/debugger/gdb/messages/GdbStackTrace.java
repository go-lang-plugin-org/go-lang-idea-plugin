package com.goide.debugger.gdb.messages;

import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import com.goide.debugger.gdb.messages.annotations.GdbMiDoneEvent;
import com.goide.debugger.gdb.messages.annotations.GdbMiField;

import java.util.List;

/**
 * A stack trace. This is returned from a -stack-list-frames request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-stack-list-frames")
public class GdbStackTrace extends GdbDoneEvent {
  /**
   * The stack frames.
   */
  @GdbMiField(name = "stack", valueType = GdbMiValue.Type.List)
  public List<GdbStackFrame> stack;
}
