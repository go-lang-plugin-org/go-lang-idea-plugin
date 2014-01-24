package com.goide.debugger.gdb.messages;

import com.goide.debugger.gdb.gdbmi.GdbMiRecord;
import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import com.goide.debugger.gdb.messages.annotations.GdbMiEvent;
import com.goide.debugger.gdb.messages.annotations.GdbMiField;

/**
 * Event fired when an error occurs with a request.
 */
@SuppressWarnings("unused")
@GdbMiEvent(recordType = GdbMiRecord.Type.Immediate, className = "error")
public class GdbErrorEvent extends GdbEvent {
  /**
   * The error message.
   */
  @SuppressWarnings("unused")
  @GdbMiField(name = "msg", valueType = GdbMiValue.Type.String)
  public String message;
}
