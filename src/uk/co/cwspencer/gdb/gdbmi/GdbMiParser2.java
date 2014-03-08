package uk.co.cwspencer.gdb.gdbmi;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for GDB/MI output version 2.
 *
 * @author Florin Patan <florinpatan@gmail.com>
 */
public class GdbMiParser2 {

    private static final Set<String> START_TOKENS = new HashSet<String>(Arrays.asList(
            new String[]{"*", "+", "=", "~", "@", "&"}
    ));

    // Partially processed record
    private GdbMiResultRecord m_resultRecord;
    private GdbMiStreamRecord m_streamRecord;

    private ConsoleView rawConsole;

    // List of unprocessed records
    private List<GdbMiRecord> m_records = new ArrayList<GdbMiRecord>();
    private Long currentToken;

    public GdbMiParser2(@Nullable ConsoleView rawConsole) {
        this.rawConsole = rawConsole;
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
        String[] buffer = convertGoOutput(data);

        for (String line : buffer) {
            if (line.isEmpty() ||
                    line.matches("@\u0000*")) {
                continue;
            }

            GdbMiRecord parsedLine = parseLine(line);
            if (parsedLine == null) {
                continue;
            }

            m_records.add(parsedLine);
        }
    }

    private String[] convertGoOutput(byte[] data) {
        String buff;

        try {
            buff = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            return new String[]{};
        }

        String[] lines = buff.split("\n");
        List<String> result = new ArrayList<String>();

        List<Pattern> p = new ArrayList<Pattern>();
        p.add(Pattern.compile("(~\"\\[(?:.*?)\\]\\\\n\")$"));
        p.add(Pattern.compile("(=breakpoint\\-modified(?:.*))$"));
        p.add(Pattern.compile("(=thread\\-exited(?:.*))$"));
        p.add(Pattern.compile("(=thread\\-created(?:.*))$"));
        p.add(Pattern.compile("(\\*stopped(?:.*))$"));
        p.add(Pattern.compile("(\\*running(?:.*))$"));
        Matcher m;
        Boolean additionalLineFound = false;
        for (String line : lines) {
            if (isGdbMiLine(line)) {
                result.add(line);
                continue;
            }

            line = "@" + line;

            for (Pattern aP : p) {
                m = aP.matcher(line);
                if (m.find()) {
                    result.add(line.replaceAll(aP.pattern(), ""));
                    result.add(m.group(1));
                    additionalLineFound = true;
                    break;
                }
            }

            if (!additionalLineFound) {
                result.add(line);
            }
        }

        return result.toArray(new String[result.size()]);
    }

    private Boolean isGdbMiLine(String line) {
        if (line.length() < 1) {
            return false;
        }
        if (START_TOKENS.contains(line.substring(0, 1))) {
            return true;
        }

        if (line.matches("\\d+\\^.*")) {
            return true;
        }

        if (line.startsWith("(gdb)")) {
            return true;
        }

        return false;
    }

    private void printUnhandledLine(String line) {
        if (rawConsole != null) {
            rawConsole.print("[[[ go.gdb.internal ]]] " + line + "\n", ConsoleViewContentType.ERROR_OUTPUT);
        }
    }

    @Nullable
    private GdbMiRecord parseLine(String line) {
        if (rawConsole != null) {
            rawConsole.print(line + "\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        }

        GdbMiRecord result;

        if (line.matches("\\d+\\^.*")) {
            currentToken = Long.parseLong(line.substring(0, line.indexOf('^')), 10);

            result = new GdbMiResultRecord(GdbMiRecord.Type.Immediate, currentToken);
            result = parseImmediateLine(line, (GdbMiResultRecord) result);
            return result;
        }

        // Skip boring lines
        if (line.startsWith("(gdb)")) {
            return null;
        }

        switch (line.charAt(0)) {
            case '*':
                result = new GdbMiResultRecord(GdbMiRecord.Type.Exec, currentToken);
                result = parseExecLine(line, (GdbMiResultRecord) result);
                currentToken = null;
                break;

            case '+':
                result = new GdbMiStreamRecord(GdbMiRecord.Type.Log, currentToken);
                ((GdbMiStreamRecord) result).message = line.concat("\n");
                currentToken = null;
                break;

            case '=':
                result = new GdbMiResultRecord(GdbMiRecord.Type.Notify, currentToken);
                result = parseNotifyLine(line, (GdbMiResultRecord) result);
                currentToken = null;
                break;

            case '~':
                result = new GdbMiStreamRecord(GdbMiRecord.Type.Console, currentToken);
                line = line.substring(2, line.length() - 1)
                        .replace("\\n", "\n")
                        .replace("\\t", "    ")
                        .replace("\\\"", "\"")
                        .replaceAll("<http(.*)>", "http$1");

                ((GdbMiStreamRecord) result).message = line;
                currentToken = null;
                break;

            case '@':
                result = new GdbMiStreamRecord(GdbMiRecord.Type.Target, currentToken);
                ((GdbMiStreamRecord) result).message = line.substring(1).concat("\n");
                currentToken = null;
                break;

            case '&':
                result = new GdbMiStreamRecord(GdbMiRecord.Type.Log, currentToken);
                line = line.substring(2, line.length() - 1)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"")
                        .replaceAll("<http(.*)>", "http$1");

                ((GdbMiStreamRecord) result).message = line;
                currentToken = null;
                break;

            default:
                result = new GdbMiStreamRecord(GdbMiRecord.Type.Log, currentToken);
                ((GdbMiStreamRecord) result).message = line.concat("\n");
        }

        return result;
    }

    private GdbMiResultRecord parseNotifyLine(String line, GdbMiResultRecord result) {
        result.className = line.substring(1, line.indexOf(','));

        line = line.substring(line.indexOf(',') + 1);
        if (line.startsWith("bkpt")) {
            result.results.add(parseBreakpointLine(line));
            return result;
        }

        Pattern p = Pattern.compile("([a-z-]+)=(?:\"([^\"]+?)\")+");
        Matcher m = p.matcher(line);
        while (m.find()) {
            GdbMiResult subRes = new GdbMiResult(m.group(1));
            subRes.value = new GdbMiValue(GdbMiValue.Type.String);
            subRes.value.string = m.group(2).replace("\\\\t", "    ");
            result.results.add(subRes);
        }

        return result;
    }

    private GdbMiResultRecord parseExecLine(String line, GdbMiResultRecord result) {
        if (line.indexOf(',') < 0) {
            result.className = line.substring(1);
            return result;
        }

        result.className = line.substring(1, line.indexOf(','));

        line = line.substring(line.indexOf(',') + 1);
        if (result.className.equals("stopped")) {
            if (line.startsWith("reason=\"breakpoint-hit\"")) {
                result.results.addAll(parseBreakpointHitLine(line));
            } else if (line.startsWith("reason=\"end-stepping-range\"")) {
                result.results.addAll(parseEndSteppingRangeLine(line));
            } else if (line.startsWith("reason=\"signal-received\"")) {
                result.results.addAll(parseSignalReceivedLine(line));
            } else if (line.startsWith("reason=\"function-finished\"")) {
                result.results.addAll(parseFunctionFinishedLine(line));
            } else if (line.startsWith("reason=\"location-reached\"")) {
                result.results.addAll(parseLocationReachedLine(line));
            } else if (line.startsWith("reason=\"exited\"")) {
                result.results.addAll(parseStoppedExitedLine(line));
            } else if (line.startsWith("reason=\"exited-normally\"")) {
                GdbMiResult reasonVal = new GdbMiResult("reason");
                reasonVal.value = new GdbMiValue(GdbMiValue.Type.String);
                reasonVal.value.string = "exited-normally";
                result.results.add(reasonVal);
            } else if (line.startsWith("frame=")) {
                result.results.addAll(parseStoppedFrameLine(line));
            } else {
                printUnhandledLine(line);
            }

            return result;
        }

        if (result.className.equals("running")) {
            if (line.startsWith("thread-id")) {
                result.results.add(parseRunningThreadId(line));
            } else {
                printUnhandledLine(line);
            }

            return result;
        }

        printUnhandledLine(line);
        return result;
    }

    private GdbMiResultRecord parseImmediateLine(String line, GdbMiResultRecord result) {
        if (line.indexOf(',') < 0) {
            result.className = line.substring(line.indexOf('^') + 1);
            return result;
        }

        result.className = line.substring(line.indexOf('^') + 1, line.indexOf(','));
        line = line.substring(line.indexOf(',') + 1);

        // Check for breakpoint
        if (line.startsWith("bkpt=")) {
            result.results.add(parseBreakpointLine(line));
            return result;
        } else if (line.startsWith("stack=")) {
            result.results.add(parseStackListLine(line));
            return result;
        } else if (line.startsWith("variables=")) {
            result.results.add(parseStackListVariablesLine(line));
            return result;
        } else if (line.startsWith("name=\"var")) {
            result.results.addAll(parseVarCreateLine(line));
            return result;
        } else if (line.startsWith("changelist=")) {
            result.results.add(parseChangelistLine(line));
            return result;
        } else if (line.startsWith("msg=")) {
            result.results.add(parseMsgLine(line));
            return result;
        } else if (line.startsWith("numchild=")) {
            result.results.addAll(parseNumChildLine(line));
            return result;
        } else if (line.startsWith("features=")) {
            result.results.add(parseFeaturesLine(line));
            return result;
        } else if (line.startsWith("value=")) {
            GdbMiResult valueVal = new GdbMiResult("value");
            valueVal.value = new GdbMiValue(GdbMiValue.Type.String);
            valueVal.value.string = line.substring(7, line.length()-1);
            result.results.add(valueVal);
            return result;
        } else if (line.startsWith("thread-ids=")) {
            result.results.addAll(parseThreadIdsLine(line));
            return result;
        }  else if (line.startsWith("new-thread-id=")) {
            result.results.addAll(parseNewThreadIdLine(line));
            return result;
        }  else if (line.startsWith("threads=")) {
            result.results.addAll(parseThreadsLine(line));
            return result;
        }

        printUnhandledLine(line);
        return result;
    }

    private GdbMiResult parseBreakpointLine(String line) {

        Pattern p = Pattern.compile("addr=\"<PENDING>\"");
        Matcher m = p.matcher(line);
        if (m.find()) {
            return parsePendingBreakpoint(line);
        }

        p = Pattern.compile("addr=\"<MULTIPLE>\"");
        m = p.matcher(line);
        if (m.find()) {
            return parseMultipleBreakpointLine(line);
        }

        GdbMiResult subRes = new GdbMiResult("bkpt");
        GdbMiValue bkptVal = new GdbMiValue(GdbMiValue.Type.Tuple);

        p = Pattern.compile("thread-groups=");
        m = p.matcher(line);
        Boolean hasThreadGroups = m.find();

        String pattern = "(?:number=\"([^\"]+)\")," +
                "(?:type=\"([^\"]+)\")," +
                "(?:disp=\"([^\"]+)\")," +
                "(?:enabled=\"([^\"]+)\")," +
                "(?:addr=\"([^\"]+)\")," +
                "(?:func=\"([^\"]+)\")," +
                "(?:file=\"([^\"]+)\")," +
                "(?:fullname=\"([^\"]+)\")," +
                "(?:line=\"([^\"]+)\")";

        if (hasThreadGroups) {
            pattern += ",(?:thread-groups=\\[\"([^\"]+)\"\\])";
        }

        pattern += ",(?:times=\"(\\d+)\")," +
                "(?:original-location=\"([^\"]+)\")";

        p = Pattern.compile(pattern);
        m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return subRes;
        }

        Integer matchGroup = 0;

        // number="1"
        GdbMiResult numVal = new GdbMiResult("number");
        numVal.value.type = GdbMiValue.Type.String;
        numVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(numVal);

        // type="breakpoint"
        GdbMiResult typeVal = new GdbMiResult("type");
        typeVal.value.type = GdbMiValue.Type.String;
        typeVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(typeVal);

        // disp="keep"
        GdbMiResult dispVal = new GdbMiResult("disp");
        dispVal.value.type = GdbMiValue.Type.String;
        dispVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(dispVal);

        // enabled="y"
        GdbMiResult enabledVal = new GdbMiResult("enabled");
        enabledVal.value.type = GdbMiValue.Type.String;
        enabledVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(enabledVal);

        // addr="0x0000000000400c57"
        GdbMiResult addrVal = new GdbMiResult("addr");
        addrVal.value.type = GdbMiValue.Type.String;
        addrVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(addrVal);

        // func="main.main"
        GdbMiResult funcVal = new GdbMiResult("func");
        funcVal.value.type = GdbMiValue.Type.String;
        funcVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(funcVal);

        // file="/var/www/personal/untitled4/src/untitled4.go"
        GdbMiResult fileVal = new GdbMiResult("file");
        fileVal.value.type = GdbMiValue.Type.String;
        fileVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(fileVal);

        // fullname="/var/www/personal/untitled4/src/untitled4.go"
        GdbMiResult fullnameVal = new GdbMiResult("fullname");
        fullnameVal.value.type = GdbMiValue.Type.String;
        fullnameVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(fullnameVal);

        // line="17"
        GdbMiResult lineVal = new GdbMiResult("line");
        lineVal.value.type = GdbMiValue.Type.String;
        lineVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(lineVal);

        // thread-groups=["i1"]
        GdbMiResult threadGroupVal = new GdbMiResult("thread-groups");
        threadGroupVal.value.type = GdbMiValue.Type.List;
        threadGroupVal.value.list = new GdbMiList();
        threadGroupVal.value.list.type = GdbMiList.Type.Values;
        threadGroupVal.value.list.values = new ArrayList<GdbMiValue>();

        if (hasThreadGroups) {
            String[] threadGroupIds = m.group(++matchGroup).split(",");
            for (String threadGroupId : threadGroupIds) {
                GdbMiValue tgiVal = new GdbMiValue(GdbMiValue.Type.String);
                tgiVal.string = threadGroupId;
                threadGroupVal.value.list.values.add(tgiVal);
            }
        } else {
            GdbMiValue tgiVal = new GdbMiValue(GdbMiValue.Type.String);
            tgiVal.string = "i1";
            threadGroupVal.value.list.values.add(tgiVal);
        }
        bkptVal.tuple.add(threadGroupVal);

        // times="0"
        GdbMiResult timesVal = new GdbMiResult("times");
        timesVal.value.type = GdbMiValue.Type.String;
        timesVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(timesVal);

        // original-location="/var/www/personal/untitled4/src/untitled4.go:17"
        GdbMiResult originalLocationVal = new GdbMiResult("original-location");
        originalLocationVal.value.type = GdbMiValue.Type.String;
        originalLocationVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(originalLocationVal);

        subRes.value = bkptVal;
        return subRes;
    }

    private GdbMiResult parsePendingBreakpoint(String line) {
        GdbMiResult subRes = new GdbMiResult("bkpt");
        GdbMiValue bkptVal = new GdbMiValue(GdbMiValue.Type.Tuple);

        String pattern = "(?:number=\"([^\"]+)\")," +
                "(?:type=\"([^\"]+)\")," +
                "(?:disp=\"([^\"]+)\")," +
                "(?:enabled=\"([^\"]+)\")," +
                "(?:addr=\"([^\"]+)\")," +
                "(?:pending=\"([^\"]+)\")," +
                "(?:times=\"(\\d+)\")," +
                "(?:original-location=\"([^\"]+)\")";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return subRes;
        }

        Integer matchGroup = 0;

        // number="1"
        GdbMiResult numVal = new GdbMiResult("number");
        numVal.value.type = GdbMiValue.Type.String;
        numVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(numVal);

        // type="breakpoint"
        GdbMiResult typeVal = new GdbMiResult("type");
        typeVal.value.type = GdbMiValue.Type.String;
        typeVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(typeVal);

        // disp="keep"
        GdbMiResult dispVal = new GdbMiResult("disp");
        dispVal.value.type = GdbMiValue.Type.String;
        dispVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(dispVal);

        // enabled="y"
        GdbMiResult enabledVal = new GdbMiResult("enabled");
        enabledVal.value.type = GdbMiValue.Type.String;
        enabledVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(enabledVal);

        // addr="0x0000000000400c57"
        GdbMiResult addrVal = new GdbMiResult("addr");
        addrVal.value.type = GdbMiValue.Type.String;
        addrVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(addrVal);

        // pending="/var/www/personal/untitled4/src/untitled4.go:45"
        GdbMiResult pendingVal = new GdbMiResult("pending");
        pendingVal.value.type = GdbMiValue.Type.String;
        pendingVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(pendingVal);

        // times="0"
        GdbMiResult timesVal = new GdbMiResult("times");
        timesVal.value.type = GdbMiValue.Type.String;
        timesVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(timesVal);

        // original-location="/var/www/personal/untitled4/src/untitled4.go:17"
        GdbMiResult originalLocationVal = new GdbMiResult("original-location");
        originalLocationVal.value.type = GdbMiValue.Type.String;
        originalLocationVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(originalLocationVal);

        subRes.value = bkptVal;
        return subRes;
    }

    private GdbMiResult parseMultipleBreakpointLine(String line) {
        GdbMiResult subRes = new GdbMiResult("bkpt");
        subRes.value.type = GdbMiValue.Type.List;
        subRes.value.list = new GdbMiList();
        subRes.value.list.type = GdbMiList.Type.Results;
        subRes.value.list.results = new ArrayList<GdbMiResult>();

        String pattern = "(?:number=\"(\\d+)\")," +
                "(?:type=\"([^\"]+)\")," +
                "(?:disp=\"([^\"]+)\")," +
                "(?:enabled=\"([^\"]+)\")," +
                "(?:addr=\"([^\"]+)\")," +
                "(?:times=\"(\\d+)\")," +
                "(?:original-location=\"([^\"]+)\")";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return subRes;
        }

        Integer matchGroup = 0;

        GdbMiValue bkptVal = new GdbMiValue(GdbMiValue.Type.Tuple);

        // number="1"
        GdbMiResult numVal = new GdbMiResult("number");
        numVal.value.type = GdbMiValue.Type.String;
        numVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(numVal);

        // type="breakpoint"
        GdbMiResult typeVal = new GdbMiResult("type");
        typeVal.value.type = GdbMiValue.Type.String;
        typeVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(typeVal);

        // disp="keep"
        GdbMiResult dispVal = new GdbMiResult("disp");
        dispVal.value.type = GdbMiValue.Type.String;
        dispVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(dispVal);

        // enabled="y"
        GdbMiResult enabledVal = new GdbMiResult("enabled");
        enabledVal.value.type = GdbMiValue.Type.String;
        enabledVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(enabledVal);

        // addr="0x0000000000400c57"
        GdbMiResult addrVal = new GdbMiResult("addr");
        addrVal.value.type = GdbMiValue.Type.String;
        addrVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(addrVal);

        // times="0"
        GdbMiResult timesVal = new GdbMiResult("times");
        timesVal.value.type = GdbMiValue.Type.String;
        timesVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(timesVal);

        // original-location="/var/www/personal/untitled4/src/untitled4.go:17"
        GdbMiResult originalLocationVal = new GdbMiResult("original-location");
        originalLocationVal.value.type = GdbMiValue.Type.String;
        originalLocationVal.value.string = m.group(++matchGroup);
        bkptVal.tuple.add(originalLocationVal);

        GdbMiResult smallSubRes = new GdbMiResult("bkpt");
        smallSubRes.value = bkptVal;
        subRes.value.list.results.add(smallSubRes);

        // bkpt={
        //      number="5.1",
        //      enabled="y",
        //      addr="0x0000000000400f2a",
        //      func="main.main",
        //      file="/var/www/personal/untitled4/src/untitled4.go",
        //      fullname="/var/www/personal/untitled4/src/untitled4.go",
        //      line="44",
        //      thread-groups=["i1"]
        // }

        line = line.substring(line.indexOf("},{") + 2);

        p = Pattern.compile("thread-groups=");
        m = p.matcher(line);
        Boolean hasThreadGroups = m.find();

        pattern = "\\{(?:number=\"([^\"]+)\")," +
                "(?:enabled=\"([^\"]+)\")," +
                "(?:addr=\"([^\"]+)\")," +
                "(?:func=\"([^\"]+)\")," +
                "(?:file=\"([^\"]+)\")," +
                "(?:fullname=\"([^\"]+)\")," +
                "(?:line=\"([^\"]+)\")";

        if (hasThreadGroups) {
            pattern += ",(?:thread-groups=\\[\"([^\"]+)\"\\])";
        }

        pattern += "\\}";

        p = Pattern.compile(pattern);
        m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return subRes;
        }

        m.reset();
        while (m.find()) {
            matchGroup = 0;
            bkptVal = new GdbMiValue(GdbMiValue.Type.Tuple);

            // number="1"
            numVal = new GdbMiResult("number");
            numVal.value.type = GdbMiValue.Type.String;
            numVal.value.string = m.group(++matchGroup);
            bkptVal.tuple.add(numVal);

            // type="breakpoint"
            typeVal = new GdbMiResult("type");
            typeVal.value.type = GdbMiValue.Type.String;
            typeVal.value.string = "breakpoint";
            bkptVal.tuple.add(typeVal);

            // enabled="y"
            enabledVal = new GdbMiResult("enabled");
            enabledVal.value.type = GdbMiValue.Type.String;
            enabledVal.value.string = m.group(++matchGroup);
            bkptVal.tuple.add(enabledVal);

            // addr="0x0000000000400c57"
            addrVal = new GdbMiResult("addr");
            addrVal.value.type = GdbMiValue.Type.String;
            addrVal.value.string = m.group(++matchGroup);
            bkptVal.tuple.add(addrVal);

            // func="main.main"
            GdbMiResult funcVal = new GdbMiResult("func");
            funcVal.value.type = GdbMiValue.Type.String;
            funcVal.value.string = m.group(++matchGroup);
            bkptVal.tuple.add(funcVal);

            // file="/var/www/personal/untitled4/src/untitled4.go"
            GdbMiResult fileVal = new GdbMiResult("file");
            fileVal.value.type = GdbMiValue.Type.String;
            fileVal.value.string = m.group(++matchGroup);
            bkptVal.tuple.add(fileVal);

            // fullname="/var/www/personal/untitled4/src/untitled4.go"
            GdbMiResult fullnameVal = new GdbMiResult("fullname");
            fullnameVal.value.type = GdbMiValue.Type.String;
            fullnameVal.value.string = m.group(++matchGroup);
            bkptVal.tuple.add(fullnameVal);

            // line="17"
            GdbMiResult lineVal = new GdbMiResult("line");
            lineVal.value.type = GdbMiValue.Type.String;
            lineVal.value.string = m.group(++matchGroup);
            bkptVal.tuple.add(lineVal);

            // thread-groups=["i1"]
            GdbMiResult threadGroupVal = new GdbMiResult("thread-groups");
            threadGroupVal.value.type = GdbMiValue.Type.List;
            threadGroupVal.value.list = new GdbMiList();
            threadGroupVal.value.list.type = GdbMiList.Type.Values;
            threadGroupVal.value.list.values = new ArrayList<GdbMiValue>();

            if (hasThreadGroups) {
                String[] threadGroupIds = m.group(++matchGroup).split(",");
                for (String threadGroupId : threadGroupIds) {
                    GdbMiValue tgiVal = new GdbMiValue(GdbMiValue.Type.String);
                    tgiVal.string = threadGroupId;
                    threadGroupVal.value.list.values.add(tgiVal);
                }
            } else {
                GdbMiValue tgiVal = new GdbMiValue(GdbMiValue.Type.String);
                tgiVal.string = "i1";
                threadGroupVal.value.list.values.add(tgiVal);
            }
            bkptVal.tuple.add(threadGroupVal);

            smallSubRes = new GdbMiResult("bkpt");
            smallSubRes.value = bkptVal;
            subRes.value.list.results.add(smallSubRes);
        }

        return subRes;
    }

    private Collection<GdbMiResult> parseBreakpointHitLine(String line) {
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile("(?:core=\"(\\d+)\")");
        Matcher m = p.matcher(line);
        Boolean hasCore = m.find();

        String pattern = "(?:reason=\"([^\"]+)\")," +
                "(?:disp=\"([^\"]+)\")," +
                "(?:bkptno=\"(\\d+)\")," +
                "(?:frame=\\{([^\\}].+)\\})," +
                "(?:thread-id=\"([^\"]+)\")," +
                "(?:stopped-threads=\"([^\"]+)\")";

        if (hasCore) {
            pattern += ",(?:core=\"(\\d+)\")";
        }

        p = Pattern.compile(pattern);
        m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return result;
        }

        // reason="breakpoint-hit"
        GdbMiResult reasonVal = new GdbMiResult("reason");
        reasonVal.value.type = GdbMiValue.Type.String;
        reasonVal.value.string = m.group(1);
        result.add(reasonVal);

        // disp="keep"
        GdbMiResult dispVal = new GdbMiResult("disp");
        dispVal.value.type = GdbMiValue.Type.String;
        dispVal.value.string = m.group(2);
        result.add(dispVal);

        // bkptno="1"
        GdbMiResult bkptNoVal = new GdbMiResult("bkptno");
        bkptNoVal.value.type = GdbMiValue.Type.String;
        bkptNoVal.value.string = m.group(3);
        result.add(bkptNoVal);

        // frame={*}
        result.add(parseBreakpointHitLineFrameLine(m.group(4)));

        // thread-id="1"
        GdbMiResult threadIdVal = new GdbMiResult("thread-id");
        threadIdVal.value.type = GdbMiValue.Type.String;
        threadIdVal.value.string = m.group(5);
        result.add(threadIdVal);

        // stopped-threads="all"
        GdbMiResult stoppedThreadsVal = new GdbMiResult("stopped-threads");
        stoppedThreadsVal.value.type = GdbMiValue.Type.String;
        stoppedThreadsVal.value.string = m.group(6);
        result.add(stoppedThreadsVal);

        // core="6"
        GdbMiResult coreVal = new GdbMiResult("core");
        coreVal.value.type = GdbMiValue.Type.String;
        if (hasCore) {
            coreVal.value.string = m.group(7);
        } else {
            coreVal.value.string = "1";
        }
        result.add(coreVal);

        return result;
    }

    private static GdbMiResult parseBreakpointHitLineFrameLine(String line) {
        line = "{" + line + "}";
        Collection<GdbMiResult> results = parseFrameLine(line);
        GdbMiResult[] result = results.toArray(new GdbMiResult[results.size()]);
        return result[0];
    }

    private static GdbMiResult parseStackListLine(String line) {
        GdbMiResult subRes = new GdbMiResult("stack");
        GdbMiValue stackListVal = new GdbMiValue(GdbMiValue.Type.List);

        stackListVal.list.results = new ArrayList<GdbMiResult>();
        stackListVal.list.type = GdbMiList.Type.Results;
        stackListVal.list.results.addAll(parseFrameLine(line));

        subRes.value = stackListVal;
        return subRes;
    }

    private static Collection<GdbMiResult> parseFrameLine(String line) {
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile("args=\\[");
        Matcher m = p.matcher(line);
        Boolean hasArgs = m.find();

        String pattern = "\\{" +
                "(?:level=\"(\\d+)\")?,?" +
                "(?:addr=\"([^\"]+)\")?,?" +
                "(?:func=\"([^\"]+)\")?,?";

        if (hasArgs) {
            pattern += "(?:args=\\[(.*?)\\])?,?";
        }

        pattern += "(?:file=\"([^\"]+)\")?,?" +
                "(?:fullname=\"([^\"]+)\")?,?" +
                "(?:line=\"(\\d+)\")?" +
                "\\}";

        p = Pattern.compile(pattern);
        m = p.matcher(line);

        while (m.find()) {
            Integer matchGroup = 0;
            GdbMiResult subRes = new GdbMiResult("frame");
            GdbMiValue frameVal = new GdbMiValue(GdbMiValue.Type.Tuple);

            // level="0"
            if (m.group(++matchGroup) != null) {
                GdbMiResult levelVal = new GdbMiResult("level");
                levelVal.value.type = GdbMiValue.Type.String;
                levelVal.value.string = m.group(matchGroup);
                frameVal.tuple.add(levelVal);
            }

            // addr="0x0000000000400c57"
            if (m.group(++matchGroup) != null) {
                GdbMiResult addrVal = new GdbMiResult("addr");
                addrVal.value.type = GdbMiValue.Type.String;
                addrVal.value.string = m.group(matchGroup);
                frameVal.tuple.add(addrVal);
            }

            // func="main.main"
            if (m.group(++matchGroup) != null) {
                GdbMiResult funcVal = new GdbMiResult("func");
                funcVal.value.type = GdbMiValue.Type.String;
                funcVal.value.string = m.group(matchGroup);
                frameVal.tuple.add(funcVal);
            }

            if (hasArgs && m.group(++matchGroup) != null) {
                frameVal.tuple.add(parseArgsLine(m.group(matchGroup)));
            }

            // file="/var/www/personal/untitled4/src/untitled4.go"
            if (m.group(++matchGroup) != null) {
                GdbMiResult fileVal = new GdbMiResult("file");
                fileVal.value.type = GdbMiValue.Type.String;
                fileVal.value.string = m.group(matchGroup);
                frameVal.tuple.add(fileVal);
            }

            // fullname="/var/www/personal/untitled4/src/untitled4.go"
            if (m.group(++matchGroup) != null) {
                GdbMiResult fullnameVal = new GdbMiResult("fullname");
                fullnameVal.value.type = GdbMiValue.Type.String;
                fullnameVal.value.string = m.group(matchGroup);
                frameVal.tuple.add(fullnameVal);
            }

            // line="17"
            if (m.group(++matchGroup) != null) {
                GdbMiResult lineVal = new GdbMiResult("line");
                lineVal.value.type = GdbMiValue.Type.String;
                lineVal.value.string = m.group(matchGroup);
                frameVal.tuple.add(lineVal);
            }

            subRes.value = frameVal;
            result.add(subRes);
        }

        return result;
    }

    private static GdbMiResult parseStackListVariablesLine(String line) {
        GdbMiResult subRes = new GdbMiResult("variables");
        GdbMiValue stackListVarsVal = new GdbMiValue(GdbMiValue.Type.List);
        stackListVarsVal.list.type = GdbMiList.Type.Values;
        stackListVarsVal.list.values = new ArrayList<GdbMiValue>();

        Pattern p = Pattern.compile("\\{(?:name=\"([^\"]+)\")(?:,arg=\"([^\"]+)\")?\\}");
        Matcher m = p.matcher(line);

        while (m.find()) {
            GdbMiValue varVal = new GdbMiValue(GdbMiValue.Type.Tuple);
            varVal.tuple = new ArrayList<GdbMiResult>();

            GdbMiResult varNameVal = new GdbMiResult("name");
            varNameVal.value.type = GdbMiValue.Type.String;
            varNameVal.value.string = m.group(1);
            varVal.tuple.add(varNameVal);

            if (m.group(2) != null) {
                GdbMiResult argVal = new GdbMiResult("arg");
                argVal.value.type = GdbMiValue.Type.String;
                argVal.value.string = m.group(2);
                varVal.tuple.add(argVal);
            }

            stackListVarsVal.list.values.add(varVal);
        }

        subRes.value = stackListVarsVal;
        return subRes;
    }

    private Collection<GdbMiResult> parseVarCreateLine(String line) {
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile("(?:thread-id=\"([^\"]+)\"),");
        Matcher m = p.matcher(line);
        Boolean hasThreadId = m.find();

        String pattern = "(?:name=\"([^\"]+)\")," +
                "(?:numchild=\"([^\"]+)\")," +
                "(?:value=\"(.*?)\")," +
                "(?:type=\"([^\"]+)\"),";

        if (hasThreadId) {
            pattern += "(?:thread-id=\"([^\"]+)\"),";
        }

        pattern += "(?:has_more=\"([^\"]+)\")";

        p = Pattern.compile(pattern);
        m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return result;
        }

        Integer matchGroup = 0;

        // name="var1"
        GdbMiResult nameVal = new GdbMiResult("name");
        nameVal.value.type = GdbMiValue.Type.String;
        nameVal.value.string = m.group(++matchGroup);
        result.add(nameVal);

        // numchild="0"
        GdbMiResult numChildVal = new GdbMiResult("numchild");
        numChildVal.value.type = GdbMiValue.Type.String;
        numChildVal.value.string = m.group(++matchGroup);
        result.add(numChildVal);

        // value="false"
        GdbMiResult valueVal = new GdbMiResult("value");
        valueVal.value.type = GdbMiValue.Type.String;
        valueVal.value.string = m.group(++matchGroup);
        result.add(valueVal);

        // type="bool"
        GdbMiResult typeVal = new GdbMiResult("type");
        typeVal.value.type = GdbMiValue.Type.String;
        typeVal.value.string = m.group(++matchGroup);
        result.add(typeVal);

        if (hasThreadId) {
            // thread-id="1"
            GdbMiResult threadIdVal = new GdbMiResult("thread-id");
            threadIdVal.value.type = GdbMiValue.Type.String;
            threadIdVal.value.string = m.group(++matchGroup);
            result.add(threadIdVal);
        }

        // has_more="0"
        GdbMiResult hasMoreVal = new GdbMiResult("has_more");
        hasMoreVal.value.type = GdbMiValue.Type.String;
        hasMoreVal.value.string = m.group(++matchGroup);
        result.add(hasMoreVal);

        return result;
    }

    private static GdbMiResult parseChangelistLine(String line) {
        GdbMiResult result = new GdbMiResult("changelist");
        result.value.type = GdbMiValue.Type.List;
        result.value.list = new GdbMiList();

        Pattern p = Pattern.compile(
                "(?:\\{name=\"([^\"]+)\"," +
                        "value=\"(.*?)\"," +
                        "in_scope=\"([^\"]+)\"," +
                        "type_changed=\"([^\"]+)\"," +
                        "has_more=\"([^\"]+)\"\\})+"
        );
        Matcher m = p.matcher(line);
        if (m.find()) {
            parseChangelistLineReal(line, result, true);
        }

        p = Pattern.compile(
                "(?:\\{name=\"([^\"]+)\"," +
                        "in_scope=\"([^\"]+)\"," +
                        "type_changed=\"([^\"]+)\"," +
                        "has_more=\"([^\"]+)\"\\})+"
        );
        m = p.matcher(line);
        if (m.find()) {
            parseChangelistLineReal(line, result, false);
        }

        return result;
    }

    private static void parseChangelistLineReal(String line, GdbMiResult result, Boolean includeValue) {
        String regex = "(?:\\{name=\"([^\"]+)\",";

        if (includeValue) {
            regex += "value=\"(.*?)\",";
        }

        regex += "in_scope=\"([^\"]+?)\"," +
                "type_changed=\"([^\"].+?)\"," +
                "(?:new_type=\"([^\"]+?)\")?,?" +
                "(?:new_num_children=\"([^\"]+?)\")?,?" +
                "has_more=\"([^\"]+?)\"\\})";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(line);

        while (m.find()) {
            Integer matchGroup = 0;
            GdbMiValue changeVal = new GdbMiValue(GdbMiValue.Type.Tuple);

            // name: "var5"
            GdbMiResult nameVal = new GdbMiResult("name");
            nameVal.value.type = GdbMiValue.Type.String;
            nameVal.value.string = m.group(++matchGroup);
            changeVal.tuple.add(nameVal);

            if (includeValue) {
                // value: "3,3300000000000001"
                GdbMiResult valueVal = new GdbMiResult("value");
                valueVal.value.type = GdbMiValue.Type.String;
                valueVal.value.string = m.group(++matchGroup);
                changeVal.tuple.add(valueVal);
            }

            // in_scope: "true"
            GdbMiResult inScopeVal = new GdbMiResult("in_scope");
            inScopeVal.value.type = GdbMiValue.Type.String;
            inScopeVal.value.string = m.group(++matchGroup);
            changeVal.tuple.add(inScopeVal);

            // type_changed: "false"
            GdbMiResult typeChangedVal = new GdbMiResult("type_changed");
            typeChangedVal.value.type = GdbMiValue.Type.String;
            typeChangedVal.value.string = m.group(++matchGroup);
            changeVal.tuple.add(typeChangedVal);

            if (m.group(++matchGroup) != null) {
                // new_type="error"
                GdbMiResult newTypeVal = new GdbMiResult("new_type");
                newTypeVal.value.type = GdbMiValue.Type.String;
                newTypeVal.value.string = m.group(matchGroup);
                changeVal.tuple.add(newTypeVal);
            }

            if (m.group(++matchGroup) != null) {
                // new_num_children="2"
                GdbMiResult newNumChildrenVal = new GdbMiResult("new_num_children");
                newNumChildrenVal.value.type = GdbMiValue.Type.String;
                newNumChildrenVal.value.string = m.group(matchGroup);
                changeVal.tuple.add(newNumChildrenVal);
            }

            // has_more: "0"
            GdbMiResult hasMoreVal = new GdbMiResult("has_more");
            hasMoreVal.value.type = GdbMiValue.Type.String;
            hasMoreVal.value.string = m.group(++matchGroup);
            changeVal.tuple.add(hasMoreVal);

            if (result.value.list.values == null) {
                result.value.list.type = GdbMiList.Type.Values;
                result.value.list.values = new ArrayList<GdbMiValue>();
            }

            result.value.list.values.add(changeVal);
        }
    }

    private static GdbMiResult parseMsgLine(String line) {
        // msg="No frames found."
        GdbMiResult result = new GdbMiResult("msg");
        result.value.type = GdbMiValue.Type.String;
        result.value.string = line.substring(5, line.length() - 1);

        return result;
    }

    private static GdbMiResult parseRunningThreadId(String line) {
        Pattern p = Pattern.compile("(?:thread-id=\"([^\"]+)\")");
        Matcher m = p.matcher(line);

        // thread-id="all"
        GdbMiResult result = new GdbMiResult("thread-id");
        result.value.type = GdbMiValue.Type.String;

        if (m.find()) {
            result.value.string = m.group(1);
        }

        return result;
    }

    private Collection<GdbMiResult> parseEndSteppingRangeLine(String line) {
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile("(?:core=\"(\\d+)\")");
        Matcher m = p.matcher(line);
        Boolean hasCore = m.find();
        String pattern = "(?:reason=\"([^\"]+)\")," +
                "(?:frame=\\{([^\\}].+)\\})," +
                "(?:thread-id=\"([^\"]+)\")," +
                "(?:stopped-threads=\"([^\"]+)\")";

        if (hasCore) {
            pattern += ",(?:core=\"(\\d+)\")";
        }

        p = Pattern.compile(pattern);
        m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return result;
        }

        // reason="end-stepping-range"
        GdbMiResult reasonVal = new GdbMiResult("reason");
        reasonVal.value.type = GdbMiValue.Type.String;
        reasonVal.value.string = m.group(1);
        result.add(reasonVal);

        // frame={*}
        result.add(parseBreakpointHitLineFrameLine(m.group(2)));

        // thread-id="1"
        GdbMiResult threadIdVal = new GdbMiResult("thread-id");
        threadIdVal.value.type = GdbMiValue.Type.String;
        threadIdVal.value.string = m.group(3);
        result.add(threadIdVal);

        // stopped-threads="all"
        GdbMiResult stoppedThreadsVal = new GdbMiResult("stopped-threads");
        stoppedThreadsVal.value.type = GdbMiValue.Type.String;
        stoppedThreadsVal.value.string = m.group(4);
        result.add(stoppedThreadsVal);

        // core="6"
        GdbMiResult coreVal = new GdbMiResult("core");
        coreVal.value.type = GdbMiValue.Type.String;
        if (hasCore) {
            coreVal.value.string = m.group(5);
        } else {
            coreVal.value.string = "1";
        }
        result.add(coreVal);

        return result;
    }

    private static GdbMiResult parseNumChildChildsLine(String line) {
        GdbMiResult result = new GdbMiResult("children");
        result.value.type = GdbMiValue.Type.List;
        result.value.list = new GdbMiList();
        result.value.list.type = GdbMiList.Type.Results;
        result.value.list.results = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile("thread-id");
        Matcher m = p.matcher(line);
        Boolean hasThreadId = m.find();

        String pattern = "(?:child=\\{" +
                "(?:name=\"([^\"]+)\")," +
                "(?:exp=\"([^\"]+)\")," +
                "(?:numchild=\"(\\d+)\")," +
                "(?:value=\"(.*?)\")," +
                "(?:type=\"([^\"]+)\")";

        if (hasThreadId) {
            pattern += ",(?:thread-id=\"([^\"]+)\")";
        }

        pattern += "\\})";

        p = Pattern.compile(pattern);
        m = p.matcher(line);

        Pattern stringP = Pattern.compile("0x\\w+\\s(?:<(?:[^>].+?)>\\s)?\\\\\"(.*)");
        Matcher stringM;

        while (m.find()) {
            GdbMiResult childVal = new GdbMiResult("child");
            childVal.value.type = GdbMiValue.Type.Tuple;
            childVal.value.tuple = new ArrayList<GdbMiResult>();

            GdbMiResult nameVal = new GdbMiResult("name");
            nameVal.value.type = GdbMiValue.Type.String;
            nameVal.value.string = m.group(1);
            childVal.value.tuple.add(nameVal);

            GdbMiResult expVal = new GdbMiResult("exp");
            expVal.value.type = GdbMiValue.Type.String;
            expVal.value.string = m.group(2);
            childVal.value.tuple.add(expVal);

            GdbMiResult numChildVal = new GdbMiResult("numchild");
            numChildVal.value.type = GdbMiValue.Type.String;
            numChildVal.value.string = m.group(3);
            childVal.value.tuple.add(numChildVal);

            GdbMiResult valueVal = new GdbMiResult("value");
            valueVal.value.type = GdbMiValue.Type.String;
            valueVal.value.string = m.group(4);
            stringM = stringP.matcher(valueVal.value.string);
            if (stringM.find()) {
                valueVal.value.string = stringM.group(1).substring(0, stringM.group(1).length() - 2);
            }
            childVal.value.tuple.add(valueVal);

            GdbMiResult typeVal = new GdbMiResult("type");
            typeVal.value.type = GdbMiValue.Type.String;
            typeVal.value.string = m.group(5);
            childVal.value.tuple.add(typeVal);

            GdbMiResult threadIdVal = new GdbMiResult("thread-id");
            threadIdVal.value.type = GdbMiValue.Type.String;
            if (hasThreadId) {
                threadIdVal.value.string = m.group(6);
            } else {
                threadIdVal.value.string = "1";
            }
            childVal.value.tuple.add(threadIdVal);

            result.value.list.results.add(childVal);
        }

        return result;
    }

    private Collection<GdbMiResult> parseNumChildLine(String line) {
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile(
                "(?:numchild=\"([^\"]+)\")," +
                        "(?:children=\\[((?!\\],has_more).+?)\\])," +
                        "(?:has_more=\"([^\"]+)\")"
        );
        Matcher m = p.matcher(line);

        // numchild="2"
        if (!m.find()) {
            printUnhandledLine(line);
            return result;
        }

        GdbMiResult numChildVal = new GdbMiResult("numchild");
        numChildVal.value.type = GdbMiValue.Type.String;
        numChildVal.value.string = m.group(1);
        result.add(numChildVal);

        result.add(parseNumChildChildsLine(m.group(2)));

        // has_more="0"
        GdbMiResult hasMoreVal = new GdbMiResult("has_more");
        hasMoreVal.value.type = GdbMiValue.Type.String;
        hasMoreVal.value.string = m.group(3);
        result.add(hasMoreVal);

        return result;
    }

    private static GdbMiResult parseArgsLine(String line) {
        // args=[{name="i",value="0x0"}]

        GdbMiResult result = new GdbMiResult("args");
        result.value.type = GdbMiValue.Type.List;
        result.value.list = new GdbMiList();
        result.value.list.type = GdbMiList.Type.Values;
        result.value.list.values = new ArrayList<GdbMiValue>();

        Pattern p = Pattern.compile(
                "(?:\\{(?:name=\"([^\"]+)\")," +
                        "(?:value=\"(.*?)\")" +
                        "\\})+"
        );
        Matcher m = p.matcher(line);

        while (m.find()) {
            GdbMiValue varVal = new GdbMiValue(GdbMiValue.Type.Tuple);
            varVal.tuple = new ArrayList<GdbMiResult>();

            GdbMiResult varNameVal = new GdbMiResult("name");
            varNameVal.value.type = GdbMiValue.Type.String;
            varNameVal.value.string = m.group(1);
            varVal.tuple.add(varNameVal);


            GdbMiResult valueVal = new GdbMiResult("value");
            valueVal.value.type = GdbMiValue.Type.String;
            valueVal.value.string = m.group(2);
            varVal.tuple.add(valueVal);

            result.value.list.values.add(varVal);
        }

        return result;
    }

    private Collection<GdbMiResult> parseSignalReceivedLine(String line) {
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile(
                "(?:reason=\"([^\"]+)\")," +
                        "(?:signal-name=\"([^\"]+)\")," +
                        "(?:signal-meaning=\"([^\"]+)\")," +
                        "(?:frame=\\{([^\\}].+?)\\})," +
                        "(?:thread-id=\"([^\"]+)\")," +
                        "(?:stopped-threads=\"([^\"]+)\")," +
                        "(?:core=\"(\\d+)\")"
        );
        Matcher m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return result;
        }

        // reason="signal-received",
        GdbMiResult reasonVal = new GdbMiResult("reason");
        reasonVal.value.type = GdbMiValue.Type.String;
        reasonVal.value.string = m.group(1);
        result.add(reasonVal);

        // signal-name="SIGSEGV",
        GdbMiResult signalNameVal = new GdbMiResult("signal-name");
        signalNameVal.value.type = GdbMiValue.Type.String;
        signalNameVal.value.string = m.group(2);
        result.add(signalNameVal);

        // signal-meaning="Segmentation fault",
        GdbMiResult signalMeaningVal = new GdbMiResult("signal-meaning");
        signalMeaningVal.value.type = GdbMiValue.Type.String;
        signalMeaningVal.value.string = m.group(3);
        result.add(signalMeaningVal);

        // frame={*}
        result.add(parseBreakpointHitLineFrameLine(m.group(4)));

        // thread-id="1",
        GdbMiResult threadIdVal = new GdbMiResult("thread-id");
        threadIdVal.value.type = GdbMiValue.Type.String;
        threadIdVal.value.string = m.group(5);
        result.add(threadIdVal);

        // stopped-threads="all",
        GdbMiResult stoppedThreadsVal = new GdbMiResult("stopped-threads");
        stoppedThreadsVal.value.type = GdbMiValue.Type.String;
        stoppedThreadsVal.value.string = m.group(6);
        result.add(stoppedThreadsVal);

        // core="1"
        GdbMiResult coreVal = new GdbMiResult("core");
        coreVal.value.type = GdbMiValue.Type.String;
        coreVal.value.string = m.group(7);
        result.add(coreVal);

        return result;
    }

    private Collection<GdbMiResult> parseFunctionFinishedLine(String line) {
        return parseEndSteppingRangeLine(line);
    }

    private Collection<GdbMiResult> parseLocationReachedLine(String line) {
        return parseEndSteppingRangeLine(line);
    }

    private Collection<GdbMiResult> parseStoppedFrameLine(String line) {
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile(
                "(?:frame=\\{([^\\}].+?)\\})," +
                        "(?:thread-id=\"([^\"]+)\")," +
                        "(?:stopped-threads=\"([^\"]+)\")," +
                        "(?:core=\"(\\d+)\")"
        );
        Matcher m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return result;
        }

        // frame={*}
        result.add(parseBreakpointHitLineFrameLine(m.group(1)));

        // thread-id="1",
        GdbMiResult threadIdVal = new GdbMiResult("thread-id");
        threadIdVal.value.type = GdbMiValue.Type.String;
        threadIdVal.value.string = m.group(2);
        result.add(threadIdVal);

        // stopped-threads="all",
        GdbMiResult stoppedThreadsVal = new GdbMiResult("stopped-threads");
        stoppedThreadsVal.value.type = GdbMiValue.Type.String;
        stoppedThreadsVal.value.string = m.group(3);
        result.add(stoppedThreadsVal);

        // core="1"
        GdbMiResult coreVal = new GdbMiResult("core");
        coreVal.value.type = GdbMiValue.Type.String;
        coreVal.value.string = m.group(4);
        result.add(coreVal);

        return result;
    }

    private Collection<GdbMiResult> parseStoppedExitedLine(String line) {
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile(
                "(?:reason=\"([^\"]+)\")," +
                        "(?:exit-code=\"([^\"]+)\")"
        );
        Matcher m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return result;
        }

        // reason="exited",
        GdbMiResult reasonVal = new GdbMiResult("reason");
        reasonVal.value.type = GdbMiValue.Type.String;
        reasonVal.value.string = m.group(1);
        result.add(reasonVal);

        // exit-code="02",
        GdbMiResult exitCodeVal = new GdbMiResult("exit-code");
        exitCodeVal.value.type = GdbMiValue.Type.String;
        exitCodeVal.value.string = m.group(2);
        result.add(exitCodeVal);

        return result;
    }

    private GdbMiResult parseFeaturesLine(String line) {
        GdbMiResult result = new GdbMiResult("features");
        result.value.type = GdbMiValue.Type.List;
        result.value.list = new GdbMiList();
        result.value.list.type = GdbMiList.Type.Values;
        result.value.list.values = new ArrayList<GdbMiValue>();

        // features=["frozen-varobjs","pending-breakpoints","thread-info","data-read-memory-bytes","breakpoint-notifications","ada-task-info","python"]
        Pattern p = Pattern.compile("(?:\"([^\"]+)\")");
        Matcher m = p.matcher(line);

        while (m.find()) {
            GdbMiValue varVal = new GdbMiValue(GdbMiValue.Type.String);
            varVal.string = m.group(1);
            result.value.list.values.add(varVal);
        }

        return result;
    }

    private Collection<GdbMiResult> parseThreadIdsLine(String line) {
        //thread-ids={thread-id="4",thread-id="3",thread-id="2",thread-id="1"},current-thread-id="1",number-of-threads="4"
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile("thread-id=\"(\\d+)\"");
        Matcher m = p.matcher(line);

        GdbMiResult threadIds = new GdbMiResult("thread-ids");
        threadIds.value.type = GdbMiValue.Type.Tuple;
        threadIds.value.tuple = new ArrayList<GdbMiResult>();

        while (m.find()) {
            GdbMiResult threadId = new GdbMiResult("thread-id");
            threadId.value.type = GdbMiValue.Type.String;
            threadId.value.string = m.group(1);
            threadIds.value.tuple.add(threadId);
        }
        result.add(threadIds);

        p = Pattern.compile("current-thread-id=\"(\\d+)\",number-of-threads=\"(\\d+)\"");
        m = p.matcher(line);

        if (m.find()) {
            GdbMiResult currentThreadId = new GdbMiResult("current-thread-id");
            currentThreadId.value.type = GdbMiValue.Type.String;
            currentThreadId.value.string = m.group(1);
            result.add(currentThreadId);

            GdbMiResult numberOfThreads = new GdbMiResult("number-of-threads");
            numberOfThreads.value.type = GdbMiValue.Type.String;
            numberOfThreads.value.string = m.group(2);
            result.add(numberOfThreads);
        }

        return result;
    }

    private Collection<GdbMiResult> parseNewThreadIdLine(String line) {
        // new-thread-id="4",frame={level="0",addr="0x00007ffff7bc3cd0",func="__GI___nptl_create_event",args=[],file="events.c",fullname="/build/buildd/eglibc-2.17/nptl/events.c",line="25"}
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        Pattern p = Pattern.compile("new-thread-id=\"(\\d+)\",(?:frame=\\{([^\\}].+)\\})");
        Matcher m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return result;
        }

        GdbMiResult newThreadId = new GdbMiResult("new-thread-id");
        newThreadId.value.type = GdbMiValue.Type.String;
        newThreadId.value.string = m.group(1);
        result.add(newThreadId);

        // frame={*}
        result.add(parseBreakpointHitLineFrameLine(m.group(2)));

        return result;
    }

    private Collection<GdbMiResult> parseThreadsLine(String line) {
        // threads=[
        //      {id="4",target-id="Thread 0x7fffe67e7700 (LWP 25320)",name="seraph",frame={level="0",addr="0x00000000004356e3",func="runtime.futex",args=[],file="/usr/local/go/src/pkg/runtime/sys_linux_amd64.s",fullname="/usr/lib/go1.2/src/pkg/runtime/sys_linux_amd64.s",line="267"},state="stopped",core="7"},
        //      {id="3",target-id="Thread 0x7fffe6fe8700 (LWP 25319)",name="seraph",frame={level="0",addr="0x00000000004356e3",func="runtime.futex",args=[],file="/usr/local/go/src/pkg/runtime/sys_linux_amd64.s",fullname="/usr/lib/go1.2/src/pkg/runtime/sys_linux_amd64.s",line="267"},state="stopped",core="2"},
        //      {id="2",target-id="Thread 0x7fffe77e9700 (LWP 25318)",name="seraph",frame={level="0",addr="0x000000000043541d",func="runtime.usleep",args=[{name="usec",value="void"}],file="/usr/local/go/src/pkg/runtime/sys_linux_amd64.s",fullname="/usr/lib/go1.2/src/pkg/runtime/sys_linux_amd64.s",line="76"},state="stopped",core="5"},
        //      {id="1",target-id="Thread 0x7ffff7fd0740 (LWP 25314)",name="seraph",frame={level="0",addr="0x0000000000408462",func="main.checkAudioConfigStream",args=[{name="langChan",value="0xc2000c2060"}],file="/var/www/fork/seraph/src/seraph.go",fullname="/var/www/fork/seraph/src/seraph.go",line="984"},state="stopped",core="7"}
        // ],
        // current-thread-id="1"
        Collection<GdbMiResult> result = new ArrayList<GdbMiResult>();

        if (line.equals("threads=[]")) {
            return result;
        }

        Pattern p = Pattern.compile(
                "\\{(?:id=\"(\\d+)\"),"+
                        "(?:target-id=\"([^\"]+)\"),"+
                        "(?:name=\"([^\"]+)\"),"+
                        "(?:frame=\\{([^\\}].+?)\\})," +
                        "(?:state=\"([^\"]+)\"),"+
                        "(?:core=\"(\\d+)\")" +
                        "\\}"
        );
        Matcher m = p.matcher(line);

        if (!m.find()) {
            printUnhandledLine(line);
            return result;
        }

        GdbMiResult threads = new GdbMiResult("threads");
        threads.value.type = GdbMiValue.Type.List;
        threads.value.list = new GdbMiList();
        threads.value.list.type = GdbMiList.Type.Results;
        threads.value.list.results = new ArrayList<GdbMiResult>();

        m.reset();
        while(m.find()) {
            GdbMiResult thread = new GdbMiResult("thread");
            GdbMiValue threadVal = new GdbMiValue(GdbMiValue.Type.Tuple);

            // id="4"
            GdbMiResult idVal = new GdbMiResult("id");
            idVal.value.type = GdbMiValue.Type.String;
            idVal.value.string = m.group(1);
            threadVal.tuple.add(idVal);

            // target-id="Thread 0x7fffe67e7700 (LWP 25320)"
            GdbMiResult targetId = new GdbMiResult("target-id");
            targetId.value.type = GdbMiValue.Type.String;
            targetId.value.string = m.group(2);
            threadVal.tuple.add(targetId);

            // name="seraph"
            GdbMiResult nameVal = new GdbMiResult("name");
            nameVal.value.type = GdbMiValue.Type.String;
            nameVal.value.string = m.group(3);
            threadVal.tuple.add(nameVal);

            // frame={*}
            threadVal.tuple.add(parseBreakpointHitLineFrameLine(m.group(4)));

            // state="stopped"
            GdbMiResult stateVal = new GdbMiResult("state");
            stateVal.value.type = GdbMiValue.Type.String;
            stateVal.value.string = m.group(5);
            threadVal.tuple.add(stateVal);

            // core="7"
            GdbMiResult coreVal = new GdbMiResult("core");
            coreVal.value.type = GdbMiValue.Type.String;
            coreVal.value.string = m.group(6);
            threadVal.tuple.add(coreVal);

            thread.value = threadVal;
            threads.value.list.results.add(thread);
        }

        result.add(threads);

        p = Pattern.compile("current-thread-id=\"(\\d+)\"");
        m = p.matcher(line);

        if (m.find()) {
            GdbMiResult currentThreadId = new GdbMiResult("current-thread-id");
            currentThreadId.value.type = GdbMiValue.Type.String;
            currentThreadId.value.string = m.group(1);
            result.add(currentThreadId);
        }

        return result;
    }

}
