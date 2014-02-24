package uk.co.cwspencer.gdb.gdbmi;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Parser for GDB/MI output.
 */
public class GdbMiParser {
    // Possible states of the parser FSM
    private enum FsmState {
        Idle,                      // Ready to read a message
        Record,                    // Ready to read a record of any type
        ResultRecord,              // Reading a result record
        ResultRecordResults,       // Ready to read results from a result record
        ResultRecordResult,        // Reading a result from a result record
        ResultRecordResultEquals,  // Ready to read the '=' after a variable name
        ResultRecordResultValue,   // Ready to read a result value
        StreamRecord,              // Reading a stream output record
        String,                    // Reading a string
        StringEscape,              // Reading an escaped value from a string
        StringEscapeHex,           // Reading an escaped hexadecimal value from a string
        Tuple,                     // Reading a tuple
        TupleSeparator,            // Between results in a tuple
        TupleItem,                 // Ready to read a new item from a tuple
        List,                      // Reading a list
        ListValueSeparator,        // Between items in a list of values
        ListValueItem,             // Ready to read a new item from a list of values
        ListResultSeparator,       // Between items in a list of results
        ListResultItem,            // Ready to read a new item from a list of results
        StreamRecordSuffix,        // Ready to read a new line at the end of a stream record
        MessageSuffix              // Ready to read a new line at the end of a message
    }

    // State of the parser FSM
    private Stack<FsmState> m_state;

    // Lexer
    private GdbMiLexer m_lexer = new GdbMiLexer();

    // Partially processed record
    private GdbMiResultRecord m_resultRecord;
    private GdbMiStreamRecord m_streamRecord;
    private Stack<GdbMiValue> m_valueStack = new Stack<GdbMiValue>();
    private Long m_userToken;
    private StringBuilder m_sb;

    // List of unprocessed records
    private List<GdbMiRecord> m_records = new ArrayList<GdbMiRecord>();

    /**
     * Constructor.
     */
    public GdbMiParser() {
        m_state = new Stack<FsmState>();
        m_state.push(FsmState.Idle);
    }

    /**
     * Returns a list of unprocessed records. The caller should erase items from this list as they
     * are processed.
     *
     * @return A list of unprocessed records.
     */
    public List<GdbMiRecord> getRecords() {
        return m_records;
    }

    /**
     * Processes the given data.
     *
     * @param data Data read from the GDB process.
     */
    public void process(byte[] data) {
        process(data, data.length);
    }

    /**
     * Processes the given data.
     *
     * @param data   Data read from the GDB process.
     * @param length Number of bytes from data to process.
     */
    public void process(byte[] data, int length) {
        // Run the data through the lexer first
        m_lexer.process(data, length);

        // Parse the data
        List<GdbMiToken> tokens = m_lexer.getTokens();
        for (GdbMiToken token : tokens) {
            if (m_state.isEmpty()) {
                throw new IllegalArgumentException("Mismatched tuple or list detected");
            }

            switch (m_state.lastElement()) {
                case Idle:
                    // Legal tokens:
                    // UserToken
                    // ResultRecordPrefix
                    // ExecAsyncOutputPrefix
                    // StatusAsyncOutputPrefix
                    // NotifyAsyncOutputPrefix
                    // ConsoleStreamOutputPrefix
                    // TargetStreamOutputPrefix
                    // LogStreamOutputPrefix
                    // GdbSuffix
                    switch (token.type) {
                        case UserToken:
                            m_userToken = Long.parseLong(token.value);
                            setState(FsmState.Record);
                            break;

                        case ResultRecordPrefix:
                            m_resultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Immediate, m_userToken);
                            m_userToken = null;
                            setState(FsmState.ResultRecord);
                            break;

                        case StatusAsyncOutputPrefix:
                            m_resultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Status, m_userToken);
                            m_userToken = null;
                            setState(FsmState.ResultRecord);
                            break;

                        case ExecAsyncOutputPrefix:
                            m_resultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Exec, m_userToken);
                            m_userToken = null;
                            setState(FsmState.ResultRecord);
                            break;

                        case NotifyAsyncOutputPrefix:
                            m_resultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Notify, m_userToken);
                            m_userToken = null;
                            setState(FsmState.ResultRecord);
                            break;

                        case ConsoleStreamOutputPrefix:
                            m_streamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Console, m_userToken);
                            m_userToken = null;
                            setState(FsmState.StreamRecord);
                            break;

                        case TargetStreamOutputPrefix:
                            m_streamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Target, m_userToken);
                            m_userToken = null;
                            setState(FsmState.StreamRecord);
                            break;

                        case LogStreamOutputPrefix:
                            m_streamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Log, m_userToken);
                            m_userToken = null;
                            setState(FsmState.StreamRecord);
                            break;

                        case GdbSuffix:
                            setState(FsmState.MessageSuffix);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case Record:
                    // Legal tokens:
                    // ResultRecordPrefix
                    // ExecAsyncOutputPrefix
                    // StatusAsyncOutputPrefix
                    // NotifyAsyncOutputPrefix
                    // ConsoleStreamOutputPrefix
                    // TargetStreamOutputPrefix
                    // LogStreamOutputPrefix
                    switch (token.type) {
                        case ResultRecordPrefix:
                            m_resultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Immediate, m_userToken);
                            m_userToken = null;
                            setState(FsmState.ResultRecord);
                            break;

                        case StatusAsyncOutputPrefix:
                            m_resultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Status, m_userToken);
                            m_userToken = null;
                            setState(FsmState.ResultRecord);
                            break;

                        case ExecAsyncOutputPrefix:
                            m_resultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Exec, m_userToken);
                            m_userToken = null;
                            setState(FsmState.ResultRecord);
                            break;

                        case NotifyAsyncOutputPrefix:
                            m_resultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Notify, m_userToken);
                            m_userToken = null;
                            setState(FsmState.ResultRecord);
                            break;

                        case ConsoleStreamOutputPrefix:
                            m_streamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Console, m_userToken);
                            m_userToken = null;
                            setState(FsmState.StreamRecord);
                            break;

                        case TargetStreamOutputPrefix:
                            m_streamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Target, m_userToken);
                            m_userToken = null;
                            setState(FsmState.StreamRecord);
                            break;

                        case LogStreamOutputPrefix:
                            m_streamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Log, m_userToken);
                            m_userToken = null;
                            setState(FsmState.StreamRecord);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case ResultRecord:
                    // Legal tokens:
                    // Identifier
                    switch (token.type) {
                        case Identifier:
                            m_resultRecord.className = token.value;
                            setState(FsmState.ResultRecordResults);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case ResultRecordResults:
                    // Legal tokens:
                    // ResultSeparator
                    // NewLine
                    switch (token.type) {
                        case ResultSeparator:
                            m_state.push(FsmState.ResultRecordResult);
                            break;

                        case NewLine:
                            m_records.add(m_resultRecord);
                            m_resultRecord = null;
                            setState(FsmState.Idle);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case ResultRecordResult:
                    // Legal tokens:
                    // Identifier
                    switch (token.type) {
                        case Identifier: {
                            GdbMiResult result = new GdbMiResult(token.value);
                            m_valueStack.push(result.value);
                            m_resultRecord.results.add(result);
                        }
                        setState(FsmState.ResultRecordResultEquals);
                        break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case ResultRecordResultEquals:
                    // Legal tokens:
                    // Equals
                    switch (token.type) {
                        case Equals:
                            setState(FsmState.ResultRecordResultValue);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case ResultRecordResultValue:
                    // Legal tokens:
                    // StringPrefix
                    // TuplePrefix
                    // ListPrefix
                    switch (token.type) {
                        case StringPrefix:
                            m_valueStack.lastElement().type = GdbMiValue.Type.String;
                            m_sb = new StringBuilder();
                            setState(FsmState.String);
                            break;

                        case TuplePrefix:
                            m_valueStack.lastElement().type = GdbMiValue.Type.Tuple;
                            m_valueStack.lastElement().tuple = new ArrayList<GdbMiResult>();
                            setState(FsmState.Tuple);
                            break;

                        case ListPrefix:
                            m_valueStack.lastElement().type = GdbMiValue.Type.List;
                            m_valueStack.lastElement().list = new GdbMiList();
                            setState(FsmState.List);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case StreamRecord:
                    // Legal tokens:
                    // StringPrefix
                    switch (token.type) {
                        case StringPrefix:
                            m_sb = new StringBuilder();
                            setState(FsmState.String);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case String:
                    // Legal tokens:
                    // StringFragment
                    // StringEscapePrefix
                    // StringSuffix
                    switch (token.type) {
                        case StringFragment:
                            m_sb.append(token.value);
                            break;

                        case StringEscapePrefix:
                            setState(FsmState.StringEscape);
                            break;

                        case StringSuffix:
                            assert !m_valueStack.isEmpty() || m_streamRecord != null;
                            assert !(!m_valueStack.isEmpty() && m_streamRecord != null);

                            if (!m_valueStack.isEmpty()) {
                                // Currently reading a value
                                GdbMiValue value = m_valueStack.pop();
                                assert value.type == GdbMiValue.Type.String;
                                value.string = m_sb.toString();
                                m_state.pop();
                            } else {
                                m_streamRecord.message = m_sb.toString();
                                setState(FsmState.StreamRecordSuffix);
                            }
                            m_sb = null;
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case StringEscape:
                    // Legal tokens:
                    // StringEscapeApostrophe
                    // StringEscapeQuote
                    // StringEscapeQuestion
                    // StringEscapeBackslash
                    // StringEscapeAlarm
                    // StringEscapeBackspace
                    // StringEscapeFormFeed
                    // StringEscapeNewLine
                    // StringEscapeCarriageReturn
                    // StringEscapeHorizontalTab
                    // StringEscapeVerticalTab
                    // StringEscapeHexPrefix
                    // StringEscapeOctValue
                    switch (token.type) {
                        case StringEscapeApostrophe:
                            m_sb.append('\'');
                            setState(FsmState.String);
                            break;

                        case StringEscapeQuote:
                            m_sb.append('"');
                            setState(FsmState.String);
                            break;

                        case StringEscapeQuestion:
                            m_sb.append('?');
                            setState(FsmState.String);
                            break;

                        case StringEscapeBackslash:
                            m_sb.append('\\');
                            setState(FsmState.String);
                            break;

                        case StringEscapeAlarm:
                            m_sb.append('\u0007');
                            setState(FsmState.String);
                            break;

                        case StringEscapeBackspace:
                            m_sb.append('\b');
                            setState(FsmState.String);
                            break;

                        case StringEscapeFormFeed:
                            m_sb.append('\f');
                            setState(FsmState.String);
                            break;

                        case StringEscapeNewLine:
                            m_sb.append('\n');
                            setState(FsmState.String);
                            break;

                        case StringEscapeCarriageReturn:
                            m_sb.append('\r');
                            setState(FsmState.String);
                            break;

                        case StringEscapeHorizontalTab:
                            m_sb.append('\t');
                            setState(FsmState.String);
                            break;

                        case StringEscapeVerticalTab:
                            m_sb.append('\u000b');
                            setState(FsmState.String);
                            break;

                        case StringEscapeHexPrefix:
                            setState(FsmState.StringEscapeHex);
                            break;

                        case StringEscapeOctValue:
                            // Octal values can be up to three characters long, which has a maximum value of
                            // 0x1ff. As such, we need to parse it as an integer and then truncate it to
                            // 8 bits before casting it to a 16-bit char to match the behaviour of C strings
                        {
                            int ch = Integer.parseInt(token.value, 8) & 0xff;
                            m_sb.append((char) ch);
                        }
                        setState(FsmState.String);
                        break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case StringEscapeHex:
                    // Legal tokens:
                    // StringEscapeHexValue
                    switch (token.type) {
                        case StringEscapeHexValue:
                            // Hex values are not limited in length, so we need to truncate it to the last
                            // two characters to prevent Integer.parseInt from throwing an exception if it
                            // is too long
                        {
                            int tokenLen = token.value.length();
                            if (tokenLen > 2) {
                                token.value = token.value.substring(tokenLen - 2, tokenLen);
                            }
                        }
                        {
                            int ch = Integer.parseInt(token.value, 16);
                            m_sb.append((char) ch);
                        }
                        setState(FsmState.String);
                        break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case Tuple:
                    // Legal tokens:
                    // TupleSuffix
                    // Identifier
                    switch (token.type) {
                        case TupleSuffix:
                            m_valueStack.pop();
                            m_state.pop();
                            break;

                        case Identifier: {
                            GdbMiResult result = new GdbMiResult(token.value);
                            m_valueStack.lastElement().tuple.add(result);
                            m_valueStack.push(result.value);
                        }
                        m_state.pop();
                        m_state.push(FsmState.TupleSeparator);
                        m_state.push(FsmState.ResultRecordResultEquals);
                        break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case TupleSeparator:
                    // Legal tokens:
                    // TupleSuffix
                    // ResultSeparator
                    switch (token.type) {
                        case TupleSuffix:
                            m_valueStack.pop();
                            m_state.pop();
                            break;

                        case ResultSeparator:
                            setState(FsmState.TupleItem);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case TupleItem:
                    // Legal tokens:
                    // Identifier
                    switch (token.type) {
                        case Identifier: {
                            GdbMiResult result = new GdbMiResult(token.value);
                            m_valueStack.lastElement().tuple.add(result);
                            m_valueStack.push(result.value);
                        }
                        m_state.pop();
                        m_state.push(FsmState.TupleSeparator);
                        m_state.push(FsmState.ResultRecordResultEquals);
                        break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case List:
                    // Legal tokens:
                    // ListSuffix
                    // StringPrefix
                    // TuplePrefix
                    // ListPrefix
                    // Identifier
                    switch (token.type) {
                        case ListSuffix:
                            m_valueStack.pop();
                            m_state.pop();
                            break;

                        case StringPrefix: {
                            GdbMiList list = m_valueStack.lastElement().list;
                            list.type = GdbMiList.Type.Values;
                            list.values = new ArrayList<GdbMiValue>();
                            GdbMiValue value = new GdbMiValue(GdbMiValue.Type.String);
                            list.values.add(value);
                            m_valueStack.push(value);
                        }
                        m_state.pop();
                        m_state.push(FsmState.ListValueSeparator);
                        m_state.push(FsmState.String);
                        m_sb = new StringBuilder();
                        break;

                        case TuplePrefix: {
                            GdbMiList list = m_valueStack.lastElement().list;
                            list.type = GdbMiList.Type.Values;
                            list.values = new ArrayList<GdbMiValue>();
                            GdbMiValue value = new GdbMiValue(GdbMiValue.Type.Tuple);
                            value.tuple = new ArrayList<GdbMiResult>();
                            list.values.add(value);
                            m_valueStack.push(value);
                        }
                        m_state.pop();
                        m_state.push(FsmState.ListValueSeparator);
                        m_state.push(FsmState.Tuple);
                        break;

                        case Identifier: {
                            GdbMiList list = m_valueStack.lastElement().list;
                            list.type = GdbMiList.Type.Results;
                            list.results = new ArrayList<GdbMiResult>();
                            GdbMiResult result = new GdbMiResult(token.value);
                            list.results.add(result);
                            m_valueStack.push(result.value);
                        }
                        m_state.pop();
                        m_state.push(FsmState.ListResultSeparator);
                        m_state.push(FsmState.ResultRecordResultEquals);
                        break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case ListValueSeparator:
                    // Legal tokens:
                    // ListSuffix
                    // ResultSeparator
                    switch (token.type) {
                        case ListSuffix:
                            m_valueStack.pop();
                            m_state.pop();
                            break;

                        case ResultSeparator:
                            setState(FsmState.ListValueItem);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case ListValueItem:
                    // Legal tokens:
                    // StringPrefix
                    // TuplePrefix
                    // ListPrefix
                    switch (token.type) {
                        case StringPrefix: {
                            GdbMiValue value = new GdbMiValue(GdbMiValue.Type.String);
                            m_valueStack.lastElement().list.values.add(value);
                            m_valueStack.push(value);
                        }
                        m_state.pop();
                        m_state.push(FsmState.ListValueSeparator);
                        m_state.push(FsmState.String);
                        m_sb = new StringBuilder();
                        break;

                        case TuplePrefix: {
                            GdbMiValue value = new GdbMiValue(GdbMiValue.Type.Tuple);
                            value.tuple = new ArrayList<GdbMiResult>();
                            m_valueStack.lastElement().list.values.add(value);
                            m_valueStack.push(value);
                        }
                        m_state.pop();
                        m_state.push(FsmState.ListValueSeparator);
                        m_state.push(FsmState.Tuple);
                        break;

                        case ListPrefix: {
                            GdbMiValue value = new GdbMiValue(GdbMiValue.Type.List);
                            value.list = new GdbMiList();
                            m_valueStack.lastElement().list.values.add(value);
                            m_valueStack.push(value);
                        }
                        m_state.pop();
                        m_state.push(FsmState.ListValueSeparator);
                        m_state.push(FsmState.List);
                        break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case ListResultSeparator:
                    // Legal tokens:
                    // ListSuffix
                    // ResultSeparator
                    switch (token.type) {
                        case ListSuffix:
                            m_valueStack.pop();
                            m_state.pop();
                            break;

                        case ResultSeparator:
                            setState(FsmState.ListResultItem);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case ListResultItem:
                    // Legal tokens:
                    // Identifier
                    switch (token.type) {
                        case Identifier: {
                            GdbMiList list = m_valueStack.lastElement().list;
                            GdbMiResult result = new GdbMiResult(token.value);
                            list.results.add(result);
                            m_valueStack.push(result.value);
                        }
                        m_state.pop();
                        m_state.push(FsmState.ListResultSeparator);
                        m_state.push(FsmState.ResultRecordResultEquals);
                        break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case StreamRecordSuffix:
                    // Legal tokens:
                    // NewLine
                    switch (token.type) {
                        case NewLine:
                            m_records.add(m_streamRecord);
                            m_streamRecord = null;
                            setState(FsmState.Idle);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                case MessageSuffix:
                    // Legal tokens:
                    // NewLine
                    switch (token.type) {
                        case NewLine:
                            setState(FsmState.Idle);
                            break;

                        default:
                            throw new IllegalArgumentException("Unexpected token of type " + token.type);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unexpected parser FSM state: " +
                            m_state.lastElement());
            }
        }
        tokens.clear();
    }

    /**
     * Sets the state of the parser FSM.
     *
     * @param state The new state.
     */
    private void setState(FsmState state) {
        m_state.pop();
        m_state.push(state);
    }
}
