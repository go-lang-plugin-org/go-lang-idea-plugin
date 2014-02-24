package uk.co.cwspencer.gdb.gdbmi;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a result record from a GDB/MI stream.
 */
public class GdbMiResultRecord extends GdbMiRecord {
    /**
     * The result/async class.
     */
    public String className;

    /**
     * The results.
     */
    public List<GdbMiResult> results = new ArrayList<GdbMiResult>();

    /**
     * Constructor.
     *
     * @param type      The record type.
     * @param userToken The user token. May be null.
     */
    public GdbMiResultRecord(Type type, Long userToken) {
        this.type = type;
        this.userToken = userToken;
    }

    /**
     * Converts the record to a string.
     *
     * @return A string containing the class name and any results.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(className);
        if (!results.isEmpty()) {
            sb.append(": [");
            for (int i = 0; i != results.size(); ++i) {
                sb.append(results.get(i));
                if (i < results.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
