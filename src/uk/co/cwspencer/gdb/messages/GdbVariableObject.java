package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiDoneEvent;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiEnum;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiObject;

/**
 * A GDB variable object. This is returned from a -var-create request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-var-create")
@GdbMiObject
public class GdbVariableObject extends GdbDoneEvent {
    /**
     * Possible display hints.
     */
    @GdbMiEnum
    public enum DisplayHint {
        Array,
        Map,
        String
    }

    /**
     * The name of the variable object. Note this is NOT the name of the variable.
     */
    @GdbMiField(name = "name", valueType = GdbMiValue.Type.String)
    public String name;

    /**
     * The expression which the variable object represents.
     */
    @GdbMiField(name = "exp", valueType = GdbMiValue.Type.String)
    public String expression;

    /**
     * The number of children of the object. This is not necessarily reliable for dynamic variable
     * objects, in which case you must use the hasMore field.
     */
    @GdbMiField(name = "numchild", valueType = GdbMiValue.Type.String)
    public Integer numChildren;

    /**
     * The scalar value of the variable. This should not be relied upon for aggregate types
     * (structs, etc.) or for dynamic variable objects.
     */
    @GdbMiField(name = "value", valueType = GdbMiValue.Type.String)
    public String value;

    /**
     * The type of the variable. Note this is the derived type of the object, which does not
     * necessarily match the declared type.
     */
    @GdbMiField(name = "type", valueType = GdbMiValue.Type.String)
    public String type;

    /**
     * The thread the variable is bound to, if any.
     */
    @GdbMiField(name = "thread-id", valueType = GdbMiValue.Type.String)
    public Integer threadId;

    /**
     * Whether the variable object is frozen.
     */
    @GdbMiField(name = "frozen", valueType = GdbMiValue.Type.String)
    public Boolean isFrozen;

    /**
     * For dynamic objects this specifies whether there appear to be any more children available.
     */
    @GdbMiField(name = "has_more", valueType = GdbMiValue.Type.String)
    public Boolean hasMore;

    /**
     * Whether this is a dynamic variable object.
     */
    @GdbMiField(name = "dynamic", valueType = GdbMiValue.Type.String)
    public Boolean isDynamic;

    /**
     * A hint about how to display the value.
     */
    @GdbMiField(name = "displayhint", valueType = GdbMiValue.Type.String)
    public DisplayHint displayHint;
}
