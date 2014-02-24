package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiDoneEvent;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;

import java.util.List;

/**
 * A list of changed GDB variable objects. This is returned from a -var-update request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-var-update")
public class GdbVariableObjectChanges extends GdbDoneEvent {
    /**
     * The changes since the last update.
     */
    @GdbMiField(name = "changelist", valueType = GdbMiValue.Type.List)
    public List<GdbVariableObjectChange> changes;
}
