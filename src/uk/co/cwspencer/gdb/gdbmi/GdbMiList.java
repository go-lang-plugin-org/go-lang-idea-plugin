package uk.co.cwspencer.gdb.gdbmi;

import java.util.List;

/**
 * Class representing a list read from a GDB/MI stream.
 */
public class GdbMiList {
    /**
     * Possible types of lists. GDB/MI lists may contain either results or values, but not both. If
     * the list is empty there is no way to know which was intended, so it is classified as a
     * separate type. If the list is empty, both results and values will be null.
     */
    public enum Type {
        Empty,
        Results,
        Values
    }

    /**
     * The type of list.
     */
    public Type type = Type.Empty;

    /**
     * List of results. This will be null if type is not Results.
     */
    public List<GdbMiResult> results;

    /**
     * List of values. This will be null if type is not Values.
     */
    public List<GdbMiValue> values;

    /**
     * Converts the list to a string.
     *
     * @return A string containing the contents of the list.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        switch (type) {
            case Values:
                for (int i = 0; i != values.size(); ++i) {
                    sb.append(values.get(i));
                    if (i < values.size() - 1) {
                        sb.append(", ");
                    }
                }
                break;

            case Results:
                for (int i = 0; i != results.size(); ++i) {
                    sb.append(results.get(i));
                    if (i < results.size() - 1) {
                        sb.append(", ");
                    }
                }
                break;
        }
        sb.append("]");
        return sb.toString();
    }
}
