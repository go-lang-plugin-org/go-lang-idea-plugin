package com.goide.debugger.gdb.messages.annotations;

import com.goide.debugger.gdb.gdbmi.GdbMiRecord;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation applied to classes which represent events in GDB/MI messages.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface GdbMiEvent {
  /**
   * The record type.
   */
  @NotNull GdbMiRecord.Type recordType();

  /**
   * The event class name(s).
   */
  @NotNull String[] className();
}
