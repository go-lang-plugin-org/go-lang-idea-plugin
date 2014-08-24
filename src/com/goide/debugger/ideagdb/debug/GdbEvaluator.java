package com.goide.debugger.ideagdb.debug;

import com.goide.debugger.gdb.Gdb;
import com.goide.debugger.gdb.messages.GdbErrorEvent;
import com.goide.debugger.gdb.messages.GdbEvent;
import com.goide.debugger.gdb.messages.GdbVariableObject;
import com.goide.debugger.gdb.messages.GdbVariableObjects;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Expression evaluator for GDB.
 */
public class GdbEvaluator extends XDebuggerEvaluator {
  private static final Logger LOG = Logger.getInstance(GdbEvaluator.class);

  // The GDB instance
  private final Gdb myGdb;

  // The evaluation context
  private final int myThread;
  private final int myFrame;

  /**
   * Constructor.
   *
   * @param gdb    Handle to the GDB instance.
   * @param thread The thread to evaluate expressions in.
   * @param frame  The frame to evaluate expressions in.
   */
  public GdbEvaluator(Gdb gdb, int thread, int frame) {
    myGdb = gdb;
    myThread = thread;
    myFrame = frame;
  }

  /**
   * Evaluates the given expression.
   *
   * @param expression         The expression to evaluate.
   * @param callback           The callback function.
   * @param expressionPosition ??
   */
  @Override
  public void evaluate(@NotNull String expression, @NotNull final XEvaluationCallback callback,
                       @Nullable XSourcePosition expressionPosition) {
    myGdb.evaluateExpression(myThread, myFrame, expression, new Gdb.GdbEventCallback() {
      @Override
      public void onGdbCommandCompleted(GdbEvent event) {
        onGdbExpressionReady(event, callback);
      }
    });
  }

  /**
   * Evaluates the given expression.
   *
   * @param expression         The expression to evaluate.
   * @param callback           The callback function.
   * @param expressionPosition ??
   * @param mode               Evaluation mode for the expression.
   */
  @Override
  public void evaluate(@NotNull String expression, @NotNull XEvaluationCallback callback,
                       @Nullable XSourcePosition expressionPosition, @Nullable EvaluationMode mode) {
    if (mode != null && mode != EvaluationMode.EXPRESSION) {
      throw new IllegalArgumentException("Unsupported expression evaluation mode");
    }

    evaluate(expression, callback, expressionPosition);
  }

  /**
   * Indicates whether we can evaluate code fragments.
   *
   * @return Whether we can evaluate code fragments.
   */
  @Override
  public boolean isCodeFragmentEvaluationSupported() {
    // TODO: Add support for this if possible
    return false;
  }

  /**
   * Callback function for when GDB has responded to our expression evaluation request.
   *
   * @param event    The event.
   * @param callback The callback passed to evaluate().
   */
  private void onGdbExpressionReady(GdbEvent event, @NotNull XEvaluationCallback callback) {
    if (event instanceof GdbErrorEvent) {
      callback.errorOccurred(((GdbErrorEvent)event).message);
      return;
    }
    if (!(event instanceof GdbVariableObjects)) {
      callback.errorOccurred("Unexpected data received from GDB");
      LOG.warn("Unexpected event " + event + " received from expression request");
      return;
    }

    GdbVariableObjects variableObjects = (GdbVariableObjects)event;
    if (variableObjects.objects.isEmpty()) {
      callback.errorOccurred("Failed to evaluate expression");
      return;
    }

    GdbVariableObject variableObject = variableObjects.objects.get(0);
    if (variableObject.value == null) {
      callback.errorOccurred("Failed to evaluate expression");
      return;
    }

    callback.evaluated(new GdbValue(myGdb, variableObject));
  }
}
