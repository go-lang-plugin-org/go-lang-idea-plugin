package com.goide.debugger.gdb.messages;

import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import com.goide.debugger.gdb.messages.annotations.GdbMiDoneEvent;
import com.goide.debugger.gdb.messages.annotations.GdbMiField;

import java.util.List;

/**
 * A list of features supported by GDB. This is returned from a -list-features request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-list-features")
public class GdbFeatures extends GdbDoneEvent {
  /**
   * The supported features.
   */
  @GdbMiField(name = "features", valueType = GdbMiValue.Type.List)
  public List<String> features;
}
