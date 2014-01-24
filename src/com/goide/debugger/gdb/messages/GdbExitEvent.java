package com.goide.debugger.gdb.messages;

import com.goide.debugger.gdb.gdbmi.GdbMiRecord;
import com.goide.debugger.gdb.messages.annotations.GdbMiEvent;

/**
 * Event fired when an GDB exits.
 */
@SuppressWarnings("unused")
@GdbMiEvent(recordType = GdbMiRecord.Type.Immediate, className = "exit")
public class GdbExitEvent extends GdbEvent {
}
