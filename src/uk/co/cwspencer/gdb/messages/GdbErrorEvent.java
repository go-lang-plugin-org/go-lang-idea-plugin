package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiRecord;
import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiEvent;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;

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
