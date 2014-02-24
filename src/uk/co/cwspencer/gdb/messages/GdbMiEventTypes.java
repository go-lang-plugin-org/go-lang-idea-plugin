package uk.co.cwspencer.gdb.messages;

/**
 * Class which holds a reference to all the available GDB event type wrappers.
 */
public class GdbMiEventTypes {
    /**
     * An array of the event classes.
     */
    public static Class<?>[] classes = {
            GdbDoneEvent.class,
            GdbConnectedEvent.class,
            GdbErrorEvent.class,
            GdbExitEvent.class,
            GdbRunningEvent.class,
            GdbStoppedEvent.class};

    /**
     * An array of types of 'done' events.
     */
    public static Class<?>[] doneEventTypes = {
            GdbBreakpoint.class,
            GdbFeatures.class,
            GdbStackTrace.class,
            GdbThreadInfo.class,
            GdbVariableObject.class,
            GdbVariableObjectChanges.class,
            GdbVariableObjects.class,
            GdbVariables.class};
}
