package com.goide.debugger.gdb.messages.annotations;

import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation applied to fields in classes which represent fields in GDB/MI messages.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface GdbMiField {
  /**
   * The name of the field.
   */
  @NotNull String name();

  /**
   * The supported GDB/MI value types.
   */
  @NotNull GdbMiValue.Type[] valueType();

  /**
   * Name of the function to use to convert the value from the GDB format to the variable format.
   * May be the name of a function on the parent class, or a fully-qualified name to a static
   * function.
   */
  @NotNull String valueProcessor() default "";
}
