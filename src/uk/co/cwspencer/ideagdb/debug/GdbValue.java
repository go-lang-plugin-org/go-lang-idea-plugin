package uk.co.cwspencer.ideagdb.debug;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.PlatformIcons;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoIcons;
import uk.co.cwspencer.gdb.Gdb;
import uk.co.cwspencer.gdb.gdbmi.GdbMiUtil;
import uk.co.cwspencer.gdb.messages.GdbErrorEvent;
import uk.co.cwspencer.gdb.messages.GdbEvent;
import uk.co.cwspencer.gdb.messages.GdbVariableObject;
import uk.co.cwspencer.gdb.messages.GdbVariableObjects;
import uk.co.cwspencer.ideagdb.debug.go.GoGdbUtil;

/**
 * Class for providing information about a value from GDB.
 */
public class GdbValue extends XValue {
    private static final Logger m_log =
            Logger.getInstance("#uk.co.cwspencer.ideagdb.debug.GdbValue");

    // The GDB instance
    private Gdb m_gdb;

    // The variable object we are showing the value of
    private GdbVariableObject m_variableObject;

    /**
     * Constructor.
     *
     * @param gdb            Handle to the GDB instance.
     * @param variableObject The variable object to show the value of.
     */
    public GdbValue(Gdb gdb, GdbVariableObject variableObject) {
        m_gdb = gdb;
        m_variableObject = variableObject;
    }

    /**
     * Computes the presentation for the variable.
     *
     * @param node  The node to display the value in.
     * @param place Where the node will be shown.
     */
    @Override
    public void computePresentation(@NotNull final XValueNode node, @NotNull XValuePlace place) {
        final String goType = GoGdbUtil.getGoObjectType(m_variableObject.type);
        final boolean hasChildren = m_variableObject.numChildren != null && m_variableObject.numChildren > 0;

        if (goType.equals("string")) {
            handleGoString(node);
            return;
        }

        node.setPresentation(getGdbVarIcon(), goType, m_variableObject.value, hasChildren);
    }

    /**
     * Returns a modifier which can be used to change the value.
     *
     * @return The modifier, or null if the value cannot be modified.
     */
    @Nullable
    @Override
    public XValueModifier getModifier() {
        if (!GoGdbUtil.supportsEditing(m_variableObject.type)) {
            return null;
        }

        return new GdbValueModifier(m_gdb, m_variableObject);
    }

    /**
     * Computes the children on this value, if any.
     *
     * @param node The node to display the children in.
     */
    @Override
    public void computeChildren(@NotNull final XCompositeNode node) {
        if (m_variableObject.numChildren == null || m_variableObject.numChildren <= 0) {
            node.addChildren(XValueChildrenList.EMPTY, true);
        }

        // Get the children from GDB
        m_gdb.sendCommand("-var-list-children --all-values " +
                GdbMiUtil.formatGdbString(m_variableObject.name), new Gdb.GdbEventCallback() {
            @Override
            public void onGdbCommandCompleted(GdbEvent event) {
                onGdbChildrenReady(event, node);
            }
        });
    }

    /**
     * Callback function for when GDB has responded to our children request.
     *
     * @param event The event.
     * @param node  The node passed to computeChildren().
     */
    private void onGdbChildrenReady(GdbEvent event, final XCompositeNode node) {
        if (event instanceof GdbErrorEvent) {
            node.setErrorMessage(((GdbErrorEvent) event).message);
            return;
        }
        if (!(event instanceof GdbVariableObjects)) {
            node.setErrorMessage("Unexpected data received from GDB");
            m_log.warn("Unexpected event " + event + " received from -var-list-children request");
            return;
        }

        // Inspect the data
        GdbVariableObjects variables = (GdbVariableObjects) event;
        if (variables.objects == null || variables.objects.isEmpty()) {
            // No data
            node.addChildren(XValueChildrenList.EMPTY, true);
        }

        // Build a XValueChildrenList
        XValueChildrenList children = new XValueChildrenList(variables.objects.size());
        for (GdbVariableObject variable : variables.objects) {
            children.add(variable.expression, new GdbValue(m_gdb, variable));
        }
        node.addChildren(children, true);
    }

    private void handleGoString(@NotNull final XValueNode node) {
        final String nodeName = ((XValueNodeImpl) node).getName();

        m_gdb.sendCommand("-var-list-children --all-values " + GdbMiUtil.formatGdbString(m_variableObject.name), new Gdb.GdbEventCallback() {
            @Override
            public void onGdbCommandCompleted(GdbEvent event) {
                onGoGdbStringReady(node, event);
            }
        });
    }

    private void onGoGdbStringReady(@NotNull final XValueNode node, GdbEvent event) {
        String value;

        if (event instanceof GdbErrorEvent) {
            value = ((GdbErrorEvent) event).message;
        } else if (!(event instanceof GdbVariableObjects)) {
            value = "Unexpected data received from GDB";
            m_log.warn("Unexpected event " + event + " received from -var-list-children request");
        } else {
            // Inspect the data
            GdbVariableObjects variables = (GdbVariableObjects) event;
            if (variables.objects == null || variables.objects.isEmpty()) {
                // No data
                value = "Unexpected data received from GDB";
            } else {
                value = "Unexpected data received from GDB";
                String stringSubVar = GdbMiUtil.formatGdbString(m_variableObject.name, false) + ".str";

                XValueChildrenList children = new XValueChildrenList(variables.objects.size());
                for (GdbVariableObject variable : variables.objects) {
                    if (variable.name.equals(stringSubVar)) {
                        value = variable.value;
                        break;
                    }
                }
            }
        }

        if (value.equals("0x0")) {
            value = "\"\"";
        } else {
            value = value.substring(value.indexOf(" "));
        }

        node.setPresentation(getGdbVarIcon(), "string (" + (value.length() - 2) + ")", value, false);
    }

    private javax.swing.Icon getGdbVarIcon() {
        // TODO: Obviously it would be nice to return GoIcons.CONST_ICON
        // when we can detect a certain variable is actually a constant
        // but currently m_variableObject.isDynamic == null unfortunately

        return PlatformIcons.VARIABLE_ICON;
    }
}
