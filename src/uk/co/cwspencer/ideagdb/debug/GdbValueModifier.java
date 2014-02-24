package uk.co.cwspencer.ideagdb.debug;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.frame.XValueModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.cwspencer.gdb.Gdb;
import uk.co.cwspencer.gdb.messages.GdbDoneEvent;
import uk.co.cwspencer.gdb.messages.GdbErrorEvent;
import uk.co.cwspencer.gdb.messages.GdbEvent;
import uk.co.cwspencer.gdb.messages.GdbVariableObject;

/**
 * Value modifier for GDB variables.
 */
public class GdbValueModifier extends XValueModifier {
    private static final Logger m_log =
            Logger.getInstance("#uk.co.cwspencer.ideagdb.debug.GdbValueModifier");

    // Handle to the GDB instance
    Gdb m_gdb;

    // The variable object being modified
    GdbVariableObject m_variableObject;

    /**
     * Constructor.
     *
     * @param gdb            Handle to the GDB instance.
     * @param variableObject The variable object to modify.
     */
    public GdbValueModifier(Gdb gdb, GdbVariableObject variableObject) {
        m_gdb = gdb;
        m_variableObject = variableObject;
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
        m_gdb.sendCommand("-var-assign " + m_variableObject.name + " " + expression,
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
        return m_variableObject.value;
    }

    /**
     * Callback function for when GDB has responded to our variable change request.
     *
     * @param event    The event.
     * @param callback The callback passed to setValue().
     */
    private void onGdbNewValueReady(GdbEvent event, XModificationCallback callback) {
        if (event instanceof GdbErrorEvent) {
            callback.errorOccurred(((GdbErrorEvent) event).message);
            return;
        }
        if (!(event instanceof GdbDoneEvent)) {
            callback.errorOccurred("Unexpected data received from GDB");
            m_log.warn("Unexpected event " + event + " received from -var-assign request");
            return;
        }

        // Notify the caller
        callback.valueModified();
    }
}
