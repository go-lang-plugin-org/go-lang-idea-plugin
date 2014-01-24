package com.goide.debugger.gdb.messages;

import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import com.goide.debugger.gdb.messages.annotations.GdbMiDoneEvent;
import com.goide.debugger.gdb.messages.annotations.GdbMiField;

import java.util.List;

/**
 * Information about the execution threads in the target. This is returned from a -thread-info
 * request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-thread-info")
public class GdbThreadInfo extends GdbDoneEvent {
  /**
   * The threads.
   */
  @GdbMiField(name = "threads", valueType = GdbMiValue.Type.List)
  public List<GdbThread> threads;

  /**
   * The GDB ID of the current thread.
   */
  @GdbMiField(name = "current-thread-id", valueType = GdbMiValue.Type.String)
  public Integer currentThreadId;
}
