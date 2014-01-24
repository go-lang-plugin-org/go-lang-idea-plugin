package com.goide.debugger.gdb.messages;

import com.goide.debugger.gdb.gdbmi.GdbMiRecord;
import com.goide.debugger.gdb.messages.annotations.GdbMiEvent;

/**
 * Event fired when a request is completed.
 */
@SuppressWarnings("unused")
@GdbMiEvent(recordType = GdbMiRecord.Type.Immediate, className = {"done", "running"})
public class GdbDoneEvent extends GdbEvent {
}
