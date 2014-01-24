package com.goide.debugger.gdb.messages;

import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import com.goide.debugger.gdb.messages.annotations.GdbMiDoneEvent;
import com.goide.debugger.gdb.messages.annotations.GdbMiField;

import java.util.List;

/**
 * A list of GDB variable objects. This is returned from a -var-list-children request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-var-list-children")
public class GdbVariableObjects extends GdbDoneEvent {
  /**
   * The objects.
   */
  @GdbMiField(name = "children", valueType = GdbMiValue.Type.List)
  public List<GdbVariableObject> objects;
}
