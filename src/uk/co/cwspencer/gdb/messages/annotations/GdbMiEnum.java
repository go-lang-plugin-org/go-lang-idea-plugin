package uk.co.cwspencer.gdb.messages.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation applied to enums which represent strings in GDB/MI messages.
 * GDB/MI strings are converted to enum values by stripping hyphens and capitalising the first
 * letter of each word. For instance, "breakpoint-hit" would become "BreakpointHit" in the enum.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface GdbMiEnum {
}
