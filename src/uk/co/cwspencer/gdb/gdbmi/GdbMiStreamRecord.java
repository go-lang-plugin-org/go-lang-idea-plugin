package uk.co.cwspencer.gdb.gdbmi;

/**
 * Class representing a stream record from a GDB/MI stream.
 */
public class GdbMiStreamRecord extends GdbMiRecord {
    /**
     * The contents of the record.
     */
    public String message;

    /**
     * Constructor.
     *
     * @param type      The record type.
     * @param userToken The user token. May be null.
     */
    public GdbMiStreamRecord(Type type, Long userToken) {
        this.type = type;
        this.userToken = userToken;
    }
}
