package com.goide.debugger.gdb.messages.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation applied to classes which represent 'done' events in GDB/MI messages.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface GdbMiDoneEvent {
  /**
   * The request command that causes GDB to send this type of response.
   */
  @NotNull String command();

  /**
   * If set this indicates that the object should be populated with the data from the result with
   * the given name. This avoids the need to have a 'done event' class that only contains one
   * field which points to the real data. The result must be a tuple or a list of results.
   */
  @NotNull String transpose() default "";
}
