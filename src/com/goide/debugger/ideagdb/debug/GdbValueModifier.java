package com.goide.debugger.ideagdb.debug;

import com.goide.debugger.gdb.Gdb;
import com.goide.debugger.gdb.messages.GdbDoneEvent;
import com.goide.debugger.gdb.messages.GdbErrorEvent;
import com.goide.debugger.gdb.messages.GdbEvent;
import com.goide.debugger.gdb.messages.GdbVariableObject;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.frame.XValueModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Value modifier for GDB variables.
 */
public class GdbValueModifier extends XValueModifier {
  private static final Logger LOG = Logger.getInstance(GdbValueModifier.class);

  // Handle to the GDB instance
  private final Gdb myGdb;
  private final GdbVariableObject myVariableObject;

  /**
   * Constructor.
   *
   * @param gdb            Handle to the GDB instance.
   * @param variableObject The variable object to modify.
   */
  public GdbValueModifier(Gdb gdb, GdbVariableObject variableObject) {
    myGdb = gdb;
    myVariableObject = variableObject;
  }

  /**
   * Sets the new value.
   *
   * @param expression The expression to evaluate to set the value.
   * @param callback   The callback for when the operation is complete.
   */
  @Override
  public void setValue(@NotNull String expression, @NotNull final XModificationCallback callback) {
    // TODO: Format the expression properly
    myGdb.sendCommand("-var-assign " + myVariableObject.name + " " + expression,
                      new Gdb.GdbEventCallback() {
                        @Override
                        public void onGdbCommandCompleted(GdbEvent event) {
                          onGdbNewValueReady(event, callback);
                        }
                      });
  }

  /**
   * Returns the initial value to show in the editor.
   *
   * @return The initial value to show in the editor.
   */
  @Nullable
  @Override
  public String getInitialValueEditorText() {
    return myVariableObject.value;
  }

  /**
   * Callback function for when GDB has responded to our variable change request.
   *
   * @param event    The event.
   * @param callback The callback passed to setValue().
   */
  private static void onGdbNewValueReady(GdbEvent event, XModificationCallback callback) {
    if (event instanceof GdbErrorEvent) {
      callback.errorOccurred(((GdbErrorEvent)event).message);
      return;
    }
    if (!(event instanceof GdbDoneEvent)) {
      callback.errorOccurred("Unexpected data received from GDB");
      LOG.warn("Unexpected event " + event + " received from -var-assign request");
      return;
    }

    // Notify the caller
    callback.valueModified();
  }
}
