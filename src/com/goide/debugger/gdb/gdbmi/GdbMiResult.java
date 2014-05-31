package com.goide.debugger.gdb.gdbmi;

import org.jetbrains.annotations.NotNull;

/**
 * Class representing a single result from a GDB/MI result record.
 */
public class GdbMiResult {
  /**
   * Name of the variable.
   */
  public String variable;

  /**
   * Value of the variable.
   */
  @NotNull public GdbMiValue value = new GdbMiValue();

  /**
   * Constructor.
   *
   * @param variable The name of the variable.
   */
  public GdbMiResult(String variable) {
    this.variable = variable;
  }

  /**
   * Converts the result to a string.
   *
   * @return A string containing the name of the variable and its value.
   */
  @NotNull
  public String toString() {
    return variable + ": " + value;
  }
}
