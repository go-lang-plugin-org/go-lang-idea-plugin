package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiDoneEvent;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiEnum;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;

import java.util.List;

/**
 * A breakpoint. This is returned from a -break-insert request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-break-insert", transpose = "bkpt")
public class GdbBreakpoint extends GdbDoneEvent {
    /**
     * Possible breakpoint types.
     */
    @GdbMiEnum
    public enum Type {
        Breakpoint,
        Watchpoint,
        Catchpoint
    }

    /**
     * Possible catchpoint types.
     */
    @GdbMiEnum
    public enum CatchType {
        Load,
        Unload
    }

    /**
     * Possible breakpoint dispositions.
     */
    @GdbMiEnum
    public enum BreakpointDisposition {
        /**
         * The breakpoint will not be deleted.
         */
        Keep,
        /**
         * The breakpoint will be deleted at the next stop.
         */
        Del
    }

    /**
     * Possible address availability types.
     */
    public enum AddressAvailability {
        /**
         * The breakpoint address is available.
         */
        Available,
        /**
         * The breakpoint is pending.
         */
        Pending,
        /**
         * The breakpoint has multiple locations.
         */
        Multiple
    }

    /**
     * Possible breakpoint condition evaluators.
     */
    @GdbMiEnum
    public enum ConditionEvaluator {
        Host,
        Target
    }

    /**
     * The breakpoint number. If this is one location of a multi-location breakpoint this will be
     * the number of the main breakpoint and instanceNumber will be filled with the instance number.
     */
    @GdbMiField(name = "number", valueType = GdbMiValue.Type.String,
            valueProcessor = "processNumber")
    public Integer number;

    /**
     * The instance number for multi-location breakpoints.
     */
    @GdbMiField(name = "number", valueType = GdbMiValue.Type.String,
            valueProcessor = "processInstanceNumber")
    public Integer instanceNumber;

    /**
     * The type of breakpoint.
     */
    @GdbMiField(name = "type", valueType = GdbMiValue.Type.String)
    public Type type;

    /**
     * The type of catchpoint. This will be null if type is not Catchpoint.
     */
    @GdbMiField(name = "catch-type", valueType = GdbMiValue.Type.String)
    public CatchType catchType;

    /**
     * The breakpoint disposition.
     */
    @GdbMiField(name = "disp", valueType = GdbMiValue.Type.String)
    public BreakpointDisposition disposition;

    /**
     * Whether the breakpoint is enabled.
     */
    @GdbMiField(name = "enabled", valueType = GdbMiValue.Type.String)
    public Boolean enabled;

    /**
     * Availability of the breakpoint address.
     */
    @GdbMiField(name = "addr", valueType = GdbMiValue.Type.String,
            valueProcessor = "processAddressAvailability")
    public AddressAvailability addressAvailability;

    /**
     * The breakpoint address. This will be null if addressAvailability is not Available.
     */
    @GdbMiField(name = "addr", valueType = GdbMiValue.Type.String,
            valueProcessor = "processAddress")
    public Long address;

    /**
     * The name of the function the breakpoint is in, if known.
     */
    @GdbMiField(name = "func", valueType = GdbMiValue.Type.String)
    public String function;

    /**
     * The relative path to the file the breakpoint is in, if known.
     */
    @GdbMiField(name = "file", valueType = GdbMiValue.Type.String)
    public String fileRelative;

    /**
     * The absolute path to the file the breakpoint is in, if known.
     */
    @GdbMiField(name = "fullname", valueType = GdbMiValue.Type.String)
    public String fileAbsolute;

    /**
     * The line number the breakpoint is on, if known.
     */
    @GdbMiField(name = "line", valueType = GdbMiValue.Type.String)
    public Integer line;

    /**
     * If the source file is not known this may hold the address of the breakpoint, possibly
     * followed by a symbol name.
     */
    @GdbMiField(name = "at", valueType = GdbMiValue.Type.String)
    public String at;

    /**
     * If the breakpoint is pending this field holds the text used to set the breakpoint.
     */
    @GdbMiField(name = "pending", valueType = GdbMiValue.Type.String)
    public String pending;

    /**
     * Where the breakpoint's condition is evaluated.
     */
    @GdbMiField(name = "evaluated-by", valueType = GdbMiValue.Type.String)
    public ConditionEvaluator conditionEvaluator;

    /**
     * If this is a thread-specific breakpoint, this identifies the thread in which the breakpoint
     * can trigger.
     */
    @GdbMiField(name = "thread", valueType = GdbMiValue.Type.String)
    public Integer thread;

    /**
     * The thread groups to which this breakpoint applies.
     */
    @GdbMiField(name = "thread-groups", valueType = GdbMiValue.Type.List)
    public List<String> threadGroups;

    /**
     * The task identifier of the Ada task this breakpoint is restricted to, if any.
     */
    @GdbMiField(name = "task", valueType = GdbMiValue.Type.String)
    public String adaTask;

    /**
     * The condition expression, if any.
     */
    @GdbMiField(name = "cond", valueType = GdbMiValue.Type.String)
    public String condition;

    /**
     * The ignore count of the breakpoint.
     */
    @GdbMiField(name = "ignore", valueType = GdbMiValue.Type.String)
    public Integer ignoreCount;

    /**
     * The enable count of the breakpoint.
     */
    @GdbMiField(name = "enable", valueType = GdbMiValue.Type.String)
    public Integer enableCount;

    /**
     * The name of the static tracepoint marker, if any.
     */
    @GdbMiField(name = "static-tracepoint-marker-string-id", valueType = GdbMiValue.Type.String)
    public String staticTracepointMarker;

    /**
     * The watchpoint mask, if set.
     */
    @GdbMiField(name = "mask", valueType = GdbMiValue.Type.String)
    public String watchpointMask;

    /**
     * The tracepoint's pass count.
     */
    @GdbMiField(name = "pass", valueType = GdbMiValue.Type.String)
    public Integer tracepointPassCount;

    /**
     * The original location of the breakpoint.
     */
    @GdbMiField(name = "original-location", valueType = GdbMiValue.Type.String)
    public String originalLocation;

    /**
     * The number of times the breakpoint has been hit.
     */
    @GdbMiField(name = "times", valueType = GdbMiValue.Type.String)
    public Integer hitCount;

    /**
     * Whether the tracepoint is installed.
     */
    @GdbMiField(name = "installed", valueType = GdbMiValue.Type.String)
    public Boolean tracepointInstalled;

    /**
     * Unspecified extra data.
     */
    @GdbMiField(name = "what", valueType = GdbMiValue.Type.String)
    public String extra;

    /**
     * Value processor for number.
     */
    public Integer processNumber(GdbMiValue value) {
        if (value.type != GdbMiValue.Type.String) {
            return null;
        }

        int dotIndex = value.string.indexOf('.');
        if (dotIndex == -1) {
            return Integer.parseInt(value.string);
        } else {
            return Integer.parseInt(value.string.substring(0, dotIndex));
        }
    }

    /**
     * Value processor for instanceNumber.
     */
    public Integer processInstanceNumber(GdbMiValue value) {
        if (value.type != GdbMiValue.Type.String) {
            return null;
        }

        int dotIndex = value.string.indexOf('.');
        if (dotIndex == -1) {
            return null;
        } else {
            return Integer.parseInt(value.string.substring(dotIndex + 1));
        }
    }

    /**
     * Value processor for addressAvailability.
     */
    public AddressAvailability processAddressAvailability(GdbMiValue value) {
        if (value.type != GdbMiValue.Type.String) {
            return null;
        }

        if (value.string.equals("<PENDING>")) {
            return AddressAvailability.Pending;
        }
        if (value.string.equals("<MULTIPLE>")) {
            return AddressAvailability.Multiple;
        }

        // Make sure the string is a valid integer
        if (value.string.startsWith("0x")) {
            try {
                Long.parseLong(value.string.substring(2), 16);
                return AddressAvailability.Available;
            } catch (Throwable ex) {
                return null;
            }
        }

        return null;
    }

    /**
     * Value processor for address.
     */
    public Long processAddress(GdbMiValue value) {
        if (value.type != GdbMiValue.Type.String || value.string.equals("<PENDING>") ||
                value.string.equals("<MULTIPLE>")) {
            return null;
        }
        return GdbMiMessageConverterUtils.hexStringToLong(value);
    }
}
