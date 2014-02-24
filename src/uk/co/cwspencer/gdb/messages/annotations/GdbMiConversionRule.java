package uk.co.cwspencer.gdb.messages.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation applied to GDB/MI conversion rule functions.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface GdbMiConversionRule {
}
