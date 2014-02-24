package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiObject;

import java.util.Map;

/**
 * Class representing information about a stack frame from GDB.
 */
@SuppressWarnings("unused")
@GdbMiObject
public class GdbStackFrame {
    /**
     * The position of the frame within the stack, where zero is the top of the stack.
     */
    @GdbMiField(name = "level", valueType = GdbMiValue.Type.String)
    public Integer level;

    /**
     * The execution address.
     */
    @GdbMiField(name = "addr", valueType = GdbMiValue.Type.String,
            valueProcessor = "uk.co.cwspencer.gdb.messages.GdbMiMessageConverterUtils.hexStringToLong")
    public Long address;

    /**
     * The name of the function.
     */
    @GdbMiField(name = "func", valueType = GdbMiValue.Type.String, valueProcessor =
            "uk.co.cwspencer.gdb.messages.GdbMiMessageConverterUtils.passThroughIfNotQQ")
    public String function;

    /**
     * The arguments to the function.
     */
    @GdbMiField(name = "args", valueType = GdbMiValue.Type.List)
    public Map<String, String> arguments;

    /**
     * The relative path to the file being executed.
     */
    @GdbMiField(name = "file", valueType = GdbMiValue.Type.String)
    public String fileRelative;

    /**
     * The absolute path to the file being executed.
     */
    @GdbMiField(name = "fullname", valueType = GdbMiValue.Type.String)
    public String fileAbsolute;

    /**
     * The line number being executed.
     */
    @GdbMiField(name = "line", valueType = GdbMiValue.Type.String)
    public Integer line;

    /**
     * The module where the function is defined.
     */
    @GdbMiField(name = "from", valueType = GdbMiValue.Type.String)
    public String module;
}
