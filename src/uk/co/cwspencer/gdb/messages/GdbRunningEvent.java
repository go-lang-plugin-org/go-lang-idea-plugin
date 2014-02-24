package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiRecord;
import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiEvent;
import uk.co.cwspencer.gdb.messages.annotations.GdbMiField;

/**
 * Event fired when the target application starts or resumes.
 */
@SuppressWarnings("unused")
@GdbMiEvent(recordType = GdbMiRecord.Type.Exec, className = "running")
public class GdbRunningEvent extends GdbEvent {
    /**
     * Flag indicating whether all threads are now running.
     */
    @GdbMiField(name = "thread-id", valueType = GdbMiValue.Type.String,
            valueProcessor = "uk.co.cwspencer.gdb.messages.GdbMiMessageConverterUtils.valueIsAll")
    public Boolean allThreads;

    /**
     * The thread of execution. This will be null if allThreads is true.
     */
    @GdbMiField(name = "thread-id", valueType = GdbMiValue.Type.String, valueProcessor =
            "uk.co.cwspencer.gdb.messages.GdbMiMessageConverterUtils.passThroughIfNotAll")
    public Integer threadId;
}
