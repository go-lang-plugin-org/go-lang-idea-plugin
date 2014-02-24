package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiDoneEvent;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;

import java.util.Map;

/**
 * List of variables. This is returned from a -stack-list-variables request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-stack-list-variables")
public class GdbVariables extends GdbDoneEvent {
    // TODO: The tuple contains 'arg="1"' if the variable is an argument

    /**
     * The variables.
     */
    @SuppressWarnings("unused")
    @GdbMiField(name = "variables", valueType = GdbMiValue.Type.List)
    public Map<String, String> variables;
}
