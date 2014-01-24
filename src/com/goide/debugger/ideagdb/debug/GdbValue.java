package com.goide.debugger.ideagdb.debug;

import com.goide.debugger.gdb.Gdb;
import com.goide.debugger.gdb.gdbmi.GdbMiUtil;
import com.goide.debugger.gdb.messages.GdbErrorEvent;
import com.goide.debugger.gdb.messages.GdbEvent;
import com.goide.debugger.gdb.messages.GdbVariableObject;
import com.goide.debugger.gdb.messages.GdbVariableObjects;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.PlatformIcons;
import com.intellij.xdebugger.frame.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for providing information about a value from GDB.
 */
public class GdbValue extends XValue {
  private static final Logger LOG = Logger.getInstance(GdbValue.class);

  // The GDB instance
  private Gdb myGdb;

  // The variable object we are showing the value of
  private GdbVariableObject myVariableObject;

  /**
   * Constructor.
   *
   * @param gdb            Handle to the GDB instance.
   * @param variableObject The variable object to show the value of.
   */
  public GdbValue(Gdb gdb, GdbVariableObject variableObject) {
    myGdb = gdb;
    myVariableObject = variableObject;
  }

  /**
   * Computes the presentation for the variable.
   *
   * @param node  The node to display the value in.
   * @param place Where the node will be shown.
   */
  @Override
  public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
    node.setPresentation(PlatformIcons.VARIABLE_ICON, myVariableObject.type,
                         myVariableObject.value, myVariableObject.numChildren != null &&
                                                 myVariableObject.numChildren > 0);
  }

  /**
   * Returns a modifier which can be used to change the value.
   *
   * @return The modifier, or null if the value cannot be modified.
   */
  @Nullable
  @Override
  public XValueModifier getModifier() {
    // TODO: Return null if we don't support editing
    return new GdbValueModifier(myGdb, myVariableObject);
  }

  /**
   * Computes the children on this value, if any.
   *
   * @param node The node to display the children in.
   */
  @Override
  public void computeChildren(@NotNull final XCompositeNode node) {
    if (myVariableObject.numChildren == null || myVariableObject.numChildren <= 0) {
      node.addChildren(XValueChildrenList.EMPTY, true);
    }

    // Get the children from GDB
    myGdb.sendCommand("-var-list-children --all-values " +
                      GdbMiUtil.formatGdbString(myVariableObject.name), new Gdb.GdbEventCallback() {
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
      node.setErrorMessage(((GdbErrorEvent)event).message);
      return;
    }
    if (!(event instanceof GdbVariableObjects)) {
      node.setErrorMessage("Unexpected data received from GDB");
      LOG.warn("Unexpected event " + event + " received from -var-list-children request");
      return;
    }

    // Inspect the data
    GdbVariableObjects variables = (GdbVariableObjects)event;
    if (variables.objects == null || variables.objects.isEmpty()) {
      // No data
      node.addChildren(XValueChildrenList.EMPTY, true);
    }

    // Build a XValueChildrenList
    XValueChildrenList children = new XValueChildrenList(variables.objects.size());
    for (GdbVariableObject variable : variables.objects) {
      children.add(variable.expression, new GdbValue(myGdb, variable));
    }
    node.addChildren(children, true);
  }
}