package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiRecord;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiEvent;

/**
 * Event fired when a request is completed.
 */
@SuppressWarnings("unused")
@GdbMiEvent(recordType = GdbMiRecord.Type.Immediate, className = {"done", "running"})
public class GdbDoneEvent extends GdbEvent {
}
