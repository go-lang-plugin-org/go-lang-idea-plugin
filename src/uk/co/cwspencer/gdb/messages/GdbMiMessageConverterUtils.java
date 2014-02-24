package uk.co.cwspencer.gdb.messages;

import uk.co.cwspencer.gdb.gdbmi.GdbMiValue;

/**
 * Utility functions for use with the message converter.
 */
@SuppressWarnings("unused")
public class GdbMiMessageConverterUtils {
    /**
     * Converts a hexadecimal string to a long.
     */
    public static Long hexStringToLong(GdbMiValue value) {
        Long longValue = null;
        if (value.type == GdbMiValue.Type.String && value.string.substring(0, 2).equals("0x")) {
            longValue = Long.parseLong(value.string.substring(2), 16);
        }
        return longValue;
    }

    /**
     * Returns true if value is equal to "all".
     */
    public static Boolean valueIsAll(GdbMiValue value) {
        return value.type == GdbMiValue.Type.String && value.string.equals("all");
    }

    /**
     * Returns null if value is equal to "all", or otherwise requests normal processing for the
     * value.
     */
    public static Object passThroughIfNotAll(GdbMiValue value) {
        if (valueIsAll(value)) {
            return null;
        }
        return GdbMiMessageConverter.ValueProcessorPassThrough;
    }

    /**
     * Returns null if value is equal to "??", or otherwise requests normal processing for the
     * value.
     */
    public static Object passThroughIfNotQQ(GdbMiValue value) {
        if (value.type == GdbMiValue.Type.String && value.string.equals("??")) {
            return null;
        }
        return GdbMiMessageConverter.ValueProcessorPassThrough;
    }

}
