package uk.co.cwspencer.gdb.messages.annotations;

import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;

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
    String name();

    /**
     * The supported GDB/MI value types.
     */
    GdbMiValue.Type[] valueType();

    /**
     * Name of the function to use to convert the value from the GDB format to the variable format.
     * May be the name of a function on the parent class, or a fully-qualified name to a static
     * function.
     */
    String valueProcessor() default "";
}
