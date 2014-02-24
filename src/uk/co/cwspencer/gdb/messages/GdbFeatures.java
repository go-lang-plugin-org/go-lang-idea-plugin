package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiDoneEvent;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;

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
