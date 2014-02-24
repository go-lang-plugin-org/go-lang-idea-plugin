package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiRecord;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiEvent;

/**
 * Event fired when an GDB exits.
 */
@SuppressWarnings("unused")
@GdbMiEvent(recordType = GdbMiRecord.Type.Immediate, className = "exit")
public class GdbExitEvent extends GdbEvent {
}
