package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiDoneEvent;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;

import java.util.List;

/**
 * Information about the execution threads in the target. This is returned from a -thread-info
 * request.
 */
@SuppressWarnings("unused")
@GdbMiDoneEvent(command = "-thread-info")
public class GdbThreadInfo extends GdbDoneEvent {
    /**
     * The threads.
     */
    @GdbMiField(name = "threads", valueType = GdbMiValue.Type.List)
    public List<GdbThread> threads;

    /**
     * The GDB ID of the current thread.
     */
    @GdbMiField(name = "current-thread-id", valueType = GdbMiValue.Type.String)
    public Integer currentThreadId;
}
