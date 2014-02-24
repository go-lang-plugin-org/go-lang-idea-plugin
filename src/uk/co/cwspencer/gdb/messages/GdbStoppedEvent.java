package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiRecord;
import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiEnum;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiEvent;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;

import java.util.List;

/**
 * Event fired when the target application stops.
 */
@SuppressWarnings("unused")
@GdbMiEvent(recordType = GdbMiRecord.Type.Exec, className = "stopped")
public class GdbStoppedEvent extends GdbEvent {
    /**
     * Possible reasons that can cause the program to stop.
     */
    @GdbMiEnum
    public enum Reason {
        BreakpointHit,
        WatchpointTrigger,
        ReadWatchpointTrigger,
        AccessWatchpointTrigger,
        FunctionFinished,
        LocationReached,
        WatchpointScope,
        EndSteppingRange,
        ExitedSignalled,
        Exited,
        ExitedNormally,
        SignalReceived,
        SolibEvent,
        Fork,
        Vfork,
        SyscallEntry,
        Exec
    }

    /**
     * The reason the target stopped.
     */
    @GdbMiField(name = "reason", valueType = GdbMiValue.Type.String)
    public Reason reason;

    /**
     * The breakpoint disposition.
     */
    @GdbMiField(name = "disp", valueType = GdbMiValue.Type.String)
    public GdbBreakpoint.BreakpointDisposition breakpointDisposition;

    /**
     * The breakpoint number.
     */
    @GdbMiField(name = "bkptno", valueType = GdbMiValue.Type.String)
    public Integer breakpointNumber;

    /**
     * The current point of execution.
     */
    @GdbMiField(name = "frame", valueType = GdbMiValue.Type.Tuple)
    public GdbStackFrame frame;

    /**
     * The thread of execution.
     */
    @GdbMiField(name = "thread-id", valueType = GdbMiValue.Type.String)
    public Integer threadId;

    /**
     * Flag indicating whether all threads were stopped. If false, stoppedThreads contains a list of
     * the threads that were stopped.
     */
    @GdbMiField(name = "stopped-threads",
            valueType = {GdbMiValue.Type.String, GdbMiValue.Type.List},
            valueProcessor = "uk.co.cwspencer.gdb.messages.GdbMiMessageConverterUtils.valueIsAll")
    public Boolean allStopped;

    /**
     * A list of the threads that were stopped. This will be null if allStopped is true.
     */
    @GdbMiField(name = "stopped-threads",
            valueType = {GdbMiValue.Type.String, GdbMiValue.Type.List}, valueProcessor =
            "uk.co.cwspencer.gdb.messages.GdbMiMessageConverterUtils.passThroughIfNotAll")
    public List<Integer> stoppedThreads;
}
