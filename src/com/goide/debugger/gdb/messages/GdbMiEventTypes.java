package com.goide.debugger.gdb.messages;

import org.jetbrains.annotations.NotNull;

/**
 * Class which holds a reference to all the available GDB event type wrappers.
 */
public class GdbMiEventTypes {
  /**
   * An array of the event classes.
   */
  @NotNull public static Class<?>[] classes = {
    GdbDoneEvent.class,
    GdbConnectedEvent.class,
    GdbErrorEvent.class,
    GdbExitEvent.class,
    GdbRunningEvent.class,
    GdbStoppedEvent.class};

  /**
   * An array of types of 'done' events.
   */
  @NotNull public static Class<?>[] doneEventTypes = {
    GdbBreakpoint.class,
    GdbFeatures.class,
    GdbStackTrace.class,
    GdbThreadInfo.class,
    GdbVariableObject.class,
    GdbVariableObjectChanges.class,
    GdbVariableObjects.class,
    GdbVariables.class};
}
