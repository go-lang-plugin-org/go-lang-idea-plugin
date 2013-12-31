package uk.co.cwspencer.gdb;

import com.intellij.openapi.diagnostic.Logger;
import uk.co.cwspencer.gdb.gdbmi.*;
import uk.co.cwspencer.gdb.messages.*;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Class for interacting with GDB.
 */
public class Gdb {
    private static final Logger m_log = Logger.getInstance("#uk.co.cwspencer.gdb.Gdb");

    /**
     * Interface for callbacks for results from completed GDB commands.
     */
    public interface GdbEventCallback {
        /**
         * Called when a response to the command is received.
         *
         * @param event The event.
         */
        public void onGdbCommandCompleted(GdbEvent event);
    }

    // Information about a command that is awaiting processing
    private class CommandData {
        // The command
        String command;
        // The user provided callback; may be null
        GdbEventCallback callback;

        CommandData(String command, GdbEventCallback callback) {
            this.command = command;
            this.callback = callback;
        }
    }

    // Handle to the ASCII character set
    private static Charset m_ascii = Charset.forName("US-ASCII");

    // The listener
    private GdbListener m_listener;

    // Handle for the GDB process
    private Process m_process;

    // Threads which read/write data from GDB
    private Thread m_readThread;
    private Thread m_writeThread;

    // Flag indicating whether we are stopping
    private Boolean m_stopping = false;

    // Flag indicating whether we have received the first record from GDB yet
    private boolean m_firstRecord = true;

    // Token which the next GDB command will be sent with
    private long m_token = 1;

    // Commands that are waiting to be sent
    private List<CommandData> m_queuedCommands = new ArrayList<CommandData>();

    // Commands that have been sent to GDB and are awaiting a response
    private final Map<Long, CommandData> m_pendingCommands = new HashMap<Long, CommandData>();

    // GDB variable objects
    private final Map<String, GdbVariableObject> m_variableObjectsByExpression =
            new HashMap<String, GdbVariableObject>();
    private final Map<String, GdbVariableObject> m_variableObjectsByName =
            new HashMap<String, GdbVariableObject>();

    // List of capabilities supported by GDB
    private Set<String> m_capabilities;

    /**
     * Constructor; prepares GDB.
     *
     * @param gdbPath          The path to the GDB executable.
     * @param workingDirectory Working directory to launch the GDB process in. May be null.
     * @param listener         Listener that is to receive GDB events.
     */
    public Gdb(final String gdbPath, final String workingDirectory, GdbListener listener) {
        // Prepare GDB
        m_listener = listener;
        m_readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runGdb(gdbPath, workingDirectory);
            }
        });
    }

    /**
     * Finalizer. Kills GDB and the I/O threads.
     */
    @Override
    protected synchronized void finalize() throws Throwable {
        super.finalize();

        // Terminate the I/O threads
        if (m_readThread != null) {
            m_readThread.interrupt();
            m_readThread.join();
        }
        if (m_writeThread != null) {
            m_stopping = true;
            m_writeThread.notify();
            m_writeThread.interrupt();
            m_writeThread.join();
        }

        // Kill the process
        if (m_process != null) {
            m_process.destroy();
        }
    }

    /**
     * Starts GDB.
     */
    public void start() {
        if (m_readThread.isAlive()) {
            throw new IllegalStateException("GDB has already been started");
        }
        m_readThread.start();
    }

    /**
     * Sends an arbitrary command to GDB.
     *
     * @param command The command to send. This may be a normal CLI command or a GDB/MI command. It
     *                should not contain any line breaks.
     */
    public void sendCommand(String command) {
        sendCommand(command, null);
    }

    /**
     * Sends an arbitrary command to GDB and requests a completion callback.
     *
     * @param command  The command to send. This may be a normal CLI command or a GDB/MI command. It
     *                 should not contain any line breaks.
     * @param callback The callback function.
     */
    public synchronized void sendCommand(String command, GdbEventCallback callback) {
        // Queue the command
        m_queuedCommands.add(new CommandData(command, callback));
        notify();
    }

    /**
     * Indicates whether GDB has the given capability.
     *
     * @param capability The capability to check for.
     * @return Whether GDB has the capability.
     */
    public synchronized boolean hasCapability(String capability) {

        final String cap = capability;

        if (m_capabilities == null) {
            new Runnable() {
                public void run() {
                    m_log.warn("Capabilities list is null; returning 'unsupported' for capability " + cap);
                }
            };

            return false;
        }

        return m_capabilities.contains(capability);
    }

    /**
     * Gets information about the local variables for the given stack frame.
     *
     * @param thread   The thread on which the frame resides.
     * @param frame    The frame number.
     * @param callback The callback function. This is passed a GdbVariableObjects value on success,
     *                 or GdbErrorEvent on failure.
     */
    public void getVariablesForFrame(final int thread, final int frame,
                                     final GdbEventCallback callback) {
        // Get a list of local variables
        String command = "-stack-list-variables --thread " + thread + " --frame " + frame +
                " --no-values";
        sendCommand(command, new GdbEventCallback() {
            @Override
            public void onGdbCommandCompleted(GdbEvent event) {
                onGdbVariablesReady(event, thread, frame, callback);
            }
        });
    }

    /**
     * Evaluates the given expression in the given context.
     *
     * @param thread     The thread to evaluate the expression in.
     * @param frame      The frame to evaluate the expression in.
     * @param expression The expression to evaluate.
     * @param callback   The callback function.
     */
    public void evaluateExpression(int thread, int frame, final String expression,
                                   final GdbEventCallback callback) {
        // TODO: Make this more efficient

        // Create a new variable object if necessary
        GdbVariableObject variableObject = m_variableObjectsByExpression.get(expression);
        if (variableObject == null) {
            String command = "-var-create --thread " + thread + " --frame " + frame + " - @ " +
                    GdbMiUtil.formatGdbString(expression);
            sendCommand(command, new GdbEventCallback() {
                @Override
                public void onGdbCommandCompleted(GdbEvent event) {
                    onGdbNewVariableObjectReady(event, expression, callback);
                }
            });
        }

        // Update existing variable objects
        sendCommand("-var-update --thread " + thread + " --frame " + frame + " --all-values *",
                new GdbEventCallback() {
                    @Override
                    public void onGdbCommandCompleted(GdbEvent event) {
                        HashSet<String> expressions = new HashSet<String>();
                        expressions.add(expression);
                        onGdbVariableObjectsUpdated(event, expressions, callback);
                    }
                });
    }

    /**
     * Launches the GDB process and starts listening for data.
     *
     * @param gdbPath          Path to the GDB executable.
     * @param workingDirectory Working directory to launch the GDB process in. May be null.
     */
    private void runGdb(String gdbPath, String workingDirectory) {
        try {
            // Launch the process
            final String[] commandLine = {
                    gdbPath,
                    "--interpreter=mi2"};
            File workingDirectoryFile = null;
            if (workingDirectory != null) {
                workingDirectoryFile = new File(workingDirectory);
            }
            Process process = Runtime.getRuntime().exec(commandLine, null, workingDirectoryFile);
            InputStream stream = process.getInputStream();

            // Queue startup commands
            sendCommand("-list-features", new GdbEventCallback() {
                @Override
                public void onGdbCommandCompleted(GdbEvent event) {
                    onGdbCapabilitiesReady(event);
                }
            });

            // Save a reference to the process and launch the writer thread
            synchronized (this) {
                m_process = process;
                m_writeThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        processWriteQueue();
                    }
                });
                m_writeThread.start();
            }

            // Start listening for data
            GdbMiParser parser = new GdbMiParser();
            byte[] buffer = new byte[4096];
            int bytes;
            while ((bytes = stream.read(buffer)) != -1) {
                // Process the data
                try {
                    parser.process(buffer, bytes);
                } catch (IllegalArgumentException ex) {
                    m_log.error("GDB/MI parsing error. Current buffer contents: \"" +
                            new String(buffer, 0, bytes) + "\"", ex);
                    m_listener.onGdbError(ex);
                    return;
                }

                // Handle the records
                List<GdbMiRecord> records = parser.getRecords();
                for (GdbMiRecord record : records) {
                    handleRecord(record);
                }
                records.clear();
            }
        } catch (Throwable ex) {
            m_listener.onGdbError(ex);
        }
    }

    /**
     * Thread function for processing the write queue.
     */
    private void processWriteQueue() {
        try {
            OutputStream stream;
            List<CommandData> queuedCommands = new ArrayList<CommandData>();
            while (true) {
                synchronized (this) {
                    // Wait for some data to process
                    while (m_queuedCommands.isEmpty()) {
                        wait();
                    }

                    // Exit cleanly if we are stopping
                    if (m_stopping) {
                        return;
                    }

                    // Do the processing we need before dropping out of synchronised mode
                    stream = m_process.getOutputStream();

                    // Insert the commands into the pending queue
                    long token = m_token;
                    for (CommandData command : m_queuedCommands) {
                        m_pendingCommands.put(token++, command);
                    }

                    // Swap the queues
                    List<CommandData> tmp = queuedCommands;
                    queuedCommands = m_queuedCommands;
                    m_queuedCommands = tmp;
                }

                // Send the queued commands to GDB
                StringBuilder sb = new StringBuilder();
                for (CommandData command : queuedCommands) {
                    // Construct the message
                    long token = m_token++;
                    m_listener.onGdbCommandSent(command.command, token);

                    sb.append(token);
                    sb.append(command.command);
                    sb.append("\r\n");
                }
                queuedCommands.clear();

                // Send the messages
                byte[] message = sb.toString().getBytes(m_ascii);
                stream.write(message);
                stream.flush();
            }
        } catch (InterruptedException ex) {
            // We are exiting
        } catch (Throwable ex) {
            m_listener.onGdbError(ex);
        }
    }

    /**
     * Handles the given GDB/MI record.
     *
     * @param record The record.
     */
    private void handleRecord(GdbMiRecord record) {
        switch (record.type) {
            case Target:
            case Console:
            case Log:
                handleStreamRecord((GdbMiStreamRecord) record);
                break;

            case Immediate:
            case Exec:
            case Notify:
            case Status:
                handleResultRecord((GdbMiResultRecord) record);
                break;
        }

        // If this is the first record we have received we know we are fully started, so notify the
        // listener
        if (m_firstRecord) {
            m_firstRecord = false;
            m_listener.onGdbStarted();
        }
    }

    /**
     * Handles the given GDB/MI stream record.
     *
     * @param record The record.
     */
    private void handleStreamRecord(GdbMiStreamRecord record) {
        // Notify the listener
        m_listener.onStreamRecordReceived(record);
    }

    /**
     * Handles the given GDB/MI result record.
     *
     * @param record The record.
     */
    private void handleResultRecord(GdbMiResultRecord record) {
        // Notify the listener
        m_listener.onResultRecordReceived(record);

        // Find the pending command data
        CommandData pendingCommand = null;
        String commandType = null;
        if (record.userToken != null) {
            synchronized (this) {
                pendingCommand = m_pendingCommands.remove(record.userToken);
            }
            if (pendingCommand != null) {
                // Get the command type
                int separatorIndex = pendingCommand.command.indexOf(' ');
                commandType = separatorIndex == -1 ? pendingCommand.command :
                        pendingCommand.command.substring(0, separatorIndex);
            }
        }

        // Process the event into something more useful
        GdbEvent event = GdbMiMessageConverter.processRecord(record, commandType);
        if (event != null) {
            // Notify the listener
            m_listener.onGdbEventReceived(event);
            if (pendingCommand != null && pendingCommand.callback != null) {
                pendingCommand.callback.onGdbCommandCompleted(event);
            }
        }
    }

    /**
     * Callback function for when GDB has responded to our stack variables request.
     *
     * @param event    The event.
     * @param thread   The thread on which the frame resides.
     * @param frame    The frame number.
     * @param callback The user-provided callback function.
     */
    private void onGdbVariablesReady(GdbEvent event, int thread, int frame,
                                     final GdbEventCallback callback) {
        if (event instanceof GdbErrorEvent) {
            callback.onGdbCommandCompleted(event);
            return;
        }
        if (!(event instanceof GdbVariables)) {
            GdbErrorEvent errorEvent = new GdbErrorEvent();
            errorEvent.message = "Unexpected data received from GDB";
            callback.onGdbCommandCompleted(errorEvent);
            m_log.warn("Unexpected event " + event + " received from -stack-list-variables " +
                    "request");
            return;
        }

        // Create variable objects for each of the variables if we haven't done so already
        final GdbVariables variables = (GdbVariables) event;
        for (final String variable : variables.variables.keySet()) {
            GdbVariableObject variableObject = m_variableObjectsByExpression.get(variable);
            if (variableObject == null) {
                String command = "-var-create --thread " + thread + " --frame " + frame + " - @ " +
                        variable;
                sendCommand(command, new GdbEventCallback() {
                    @Override
                    public void onGdbCommandCompleted(GdbEvent event) {
                        onGdbNewVariableObjectReady(event, variable, callback);
                    }
                });
            }
        }

        // Update any existing variable objects
        sendCommand("-var-update --thread " + thread + " --frame " + frame + " --all-values *",
                new GdbEventCallback() {
                    @Override
                    public void onGdbCommandCompleted(GdbEvent event) {
                        onGdbVariableObjectsUpdated(event, variables.variables.keySet(), callback);
                    }
                });
    }

    /**
     * Callback function for when GDB has responded to our new variable object request.
     *
     * @param event      The event.
     * @param expression The expression used to create the variable object.
     * @param callback   The user-provided callback function.
     */
    private void onGdbNewVariableObjectReady(GdbEvent event, String expression,
                                             GdbEventCallback callback) {
        if (event instanceof GdbErrorEvent) {
            callback.onGdbCommandCompleted(event);
            return;
        }
        if (!(event instanceof GdbVariableObject)) {
            GdbErrorEvent errorEvent = new GdbErrorEvent();
            errorEvent.message = "Unexpected data received from GDB";
            callback.onGdbCommandCompleted(errorEvent);
            m_log.warn("Unexpected event " + event + " received from -var-create request");
            return;
        }

        GdbVariableObject variableObject = (GdbVariableObject) event;
        if (variableObject.name == null) {
            GdbErrorEvent errorEvent = new GdbErrorEvent();
            errorEvent.message = "Unexpected data received from GDB";
            callback.onGdbCommandCompleted(errorEvent);
            m_log.warn("Variable object returned by GDB does not have a name");
            return;
        }

        // Save the new variable object
        variableObject.expression = expression;
        m_variableObjectsByExpression.put(expression, variableObject);
        m_variableObjectsByName.put(variableObject.name, variableObject);
    }

    /**
     * Callback function for when GDB has responded to our variable objects update request.
     *
     * @param event     The event.
     * @param variables The variables the user requested.
     * @param callback  The user-provided callback function.
     */
    private void onGdbVariableObjectsUpdated(GdbEvent event, Set<String> variables,
                                             GdbEventCallback callback) {
        if (event instanceof GdbErrorEvent) {
            callback.onGdbCommandCompleted(event);
            return;
        }
        if (!(event instanceof GdbVariableObjectChanges)) {
            GdbErrorEvent errorEvent = new GdbErrorEvent();
            errorEvent.message = "Unexpected data received from GDB";
            callback.onGdbCommandCompleted(errorEvent);
            m_log.warn("Unexpected event " + event + " received from -var-create request");
            return;
        }

        // Update variable objects with changes
        GdbVariableObjectChanges changes = (GdbVariableObjectChanges) event;
        if (changes.changes != null) {
            for (GdbVariableObjectChange change : changes.changes) {
                if (change.name == null) {
                    m_log.warn("Received a GDB variable object change with no name");
                    continue;
                }

                GdbVariableObject variableObject = m_variableObjectsByName.get(change.name);
                if (variableObject == null) {
                    m_log.warn("Received a GDB variable object change for a variable object " +
                            "that does not exist");
                    continue;
                }

                // Set the new value
                switch (change.inScope) {
                    case True:
                        // Update the value
                        variableObject.value = change.value;
                        break;

                    case False:
                        // Reset the value
                        variableObject.value = null;
                        break;

                    default:
                        // TODO: Delete the variable object
                        variableObject.value = null;
                }

                // Set the new type
                if (change.typeChanged && change.newType != null) {
                    variableObject.type = change.newType;
                }
            }
        }

        // Construct the list of variable object the user requested
        GdbVariableObjects list = new GdbVariableObjects();
        list.objects = new ArrayList<GdbVariableObject>();
        for (String expression : variables) {
            GdbVariableObject object = m_variableObjectsByExpression.get(expression);
            if (object != null) {
                list.objects.add(object);
            }
        }

        callback.onGdbCommandCompleted(list);
    }

    /**
     * Callback function for when GDB has responded to our list capabilities request.
     *
     * @param event The event.
     */
    private void onGdbCapabilitiesReady(GdbEvent event) {
        if (event instanceof GdbErrorEvent) {
            m_log.warn("Failed to get GDB capabilities list: " + ((GdbErrorEvent) event).message);
            return;
        }
        if (!(event instanceof GdbFeatures)) {
            m_log.warn("Unexpected event " + event + " received from -list-features request");
            return;
        }

        // Save the list
        GdbFeatures features = (GdbFeatures) event;
        if (features.features != null) {
            Set<String> capabilities = new HashSet<String>(features.features);
            synchronized (this) {
                m_capabilities = capabilities;
            }
        }
    }
}
