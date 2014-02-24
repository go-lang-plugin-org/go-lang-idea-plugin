package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiDoneEvent;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;

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
