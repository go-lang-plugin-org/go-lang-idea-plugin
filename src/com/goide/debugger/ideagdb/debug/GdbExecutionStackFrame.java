package com.goide.debugger.ideagdb.debug;

import com.goide.debugger.gdb.Gdb;
import com.goide.debugger.gdb.messages.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebuggerBundle;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GdbExecutionStackFrame extends XStackFrame {
  private static final Logger LOG = Logger.getInstance(GdbExecutionStackFrame.class);
  private final Gdb myGdb;
  private final int myThread;
  private final GdbStackFrame myFrame;
  private final int myFrameNo;
  private final GdbEvaluator myEvaluator;

  /**
   * Constructor.
   *
   * @param gdb    Handle to the GDB instance.
   * @param thread The thread the frame is in.
   * @param frame  The GDB stack frame to wrap.
   */
  public GdbExecutionStackFrame(Gdb gdb, int thread, GdbStackFrame frame) {
    myGdb = gdb;
    myThread = thread;
    myFrame = frame;

    // The top frame doesn't have a level set
    myFrameNo = myFrame.level == null ? 0 : myFrame.level;
    myEvaluator = new GdbEvaluator(myGdb, myThread, myFrameNo); // todo: was lazy, but not now, please check
  }

  /**
   * Returns an object that can be used to determine if two stack frames after a debugger step
   * are the same.
   *
   * @return The equality object.
   */
  @Nullable
  @Override
  public Object getEqualityObject() {
    // TODO: This would be better if we could actually determine if two frames represent the
    // same position, but it doesn't really matter, and this is good enough to stop the debugger
    // from collapsing variable trees when we step the debugger
    return GdbExecutionStackFrame.class;
  }

  /**
   * Returns an expression evaluator in the context of this stack frame.
   */
  @Nullable
  @Override
  public XDebuggerEvaluator getEvaluator() {
    return myEvaluator;
  }

  /**
   * Gets the source position of the stack frame, if available.
   *
   * @return The source position, or null if it is not available.
   */
  @Nullable
  @Override
  public XSourcePosition getSourcePosition() {
    if (myFrame.fileAbsolute == null || myFrame.line == null) {
      return null;
    }

    String path = myFrame.fileAbsolute.replace(File.separatorChar, '/');
    VirtualFile file = LocalFileSystem.getInstance().findFileByPath(path);
    if (file == null) {
      return null;
    }

    return XDebuggerUtil.getInstance().createPosition(file, myFrame.line - 1);
  }

  /**
   * Controls the presentation of the frame in the stack trace.
   *
   * @param component The stack frame visual component.
   */
  @Override
  public void customizePresentation(ColoredTextContainer component) {
    if (myFrame.address == null) {
      component.append(XDebuggerBundle.message("invalid.frame"),
                       SimpleTextAttributes.ERROR_ATTRIBUTES);
      return;
    }

    // Format the frame information
    XSourcePosition sourcePosition = getSourcePosition();
    if (myFrame.function != null) {
      // Strip any arguments from the function name
      String function = myFrame.function;
      int parenIndex = function.indexOf('(');
      if (parenIndex != -1) {
        function = function.substring(0, parenIndex);
      }

      if (sourcePosition != null) {
        component.append(function + "():" + (sourcePosition.getLine() + 1),
                         SimpleTextAttributes.REGULAR_ATTRIBUTES);
        component.append(" (" + sourcePosition.getFile().getName() + ")",
                         SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
      }
      else {
        component.append(function + "()", SimpleTextAttributes.GRAY_ATTRIBUTES);
        component.append(" (", SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
        if (myFrame.module != null) {
          component.append(myFrame.module + ":",
                           SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
        }
        component.append("0x" + Long.toHexString(myFrame.address) + ")",
                         SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
      }
    }
    else if (sourcePosition != null) {
      component.append(
        sourcePosition.getFile().getName() + ":" + (sourcePosition.getLine() + 1),
        SimpleTextAttributes.REGULAR_ATTRIBUTES);
    }
    else {
      String addressStr = "0x" + Long.toHexString(myFrame.address);
      component.append(addressStr, SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
    }
    component.setIcon(AllIcons.Debugger.StackFrame);
  }

  /**
   * Gets the variables available on this frame. This passes the request and returns immediately;
   * the data is supplied to node asynchronously.
   *
   * @param node The node into which the variables are inserted.
   */
  @Override
  public void computeChildren(@NotNull final XCompositeNode node) {
    // TODO: This can be called multiple times if the user changes the value of a variable. We
    // shouldn't really call -stack-list-variables more than once in this case (i.e., only call
    // -var-update after the first call)
    myGdb.getVariablesForFrame(myThread, myFrameNo, new Gdb.GdbEventCallback() {
      @Override
      public void onGdbCommandCompleted(GdbEvent event) {
        onGdbVariablesReady(event, node);
      }
    });
  }

  /**
   * Callback function for when GDB has responded to our stack variables request.
   *
   * @param event The event.
   * @param node  The node passed to computeChildren().
   */
  private void onGdbVariablesReady(GdbEvent event, final XCompositeNode node) {
    if (event instanceof GdbErrorEvent) {
      node.setErrorMessage(((GdbErrorEvent)event).message);
      return;
    }
    if (!(event instanceof GdbVariableObjects)) {
      node.setErrorMessage("Unexpected data received from GDB");
      LOG.warn("Unexpected event " + event + " received from variable objects request");
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
