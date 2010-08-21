package ro.redeul.google.go.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;

import java.io.*;
import java.nio.CharBuffer;
import java.util.List;

@SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
public class ProcessUtil {

    private static final Logger LOG = Logger.getInstance("ro.redeul.google.go.util.ProcessUtil");

    public interface StreamParser<T> {
        T parseStream(String data);
    }

    public static StreamParser<String> NULL_PARSER = new StreamParser<String>() {
        public String parseStream(String data) {
            return data;
        }
    };

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})

    public static <T1, T2> Pair<T1, T2> executeAndProcessOutput(List<String> command, File startingFolder, StreamParser<T1> stdParser, StreamParser<T2> errParser) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(startingFolder);

        final Ref<Pair<T1, T2>> result = Ref.create(null);

        startProcessAndWatchOutput(processBuilder, result, stdParser, errParser);

        synchronized (result) {
            while (result.isNull()) {
                try {
                    result.wait(200);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        return result.get();
    }


    private static <T1, T2> void startProcessAndWatchOutput(ProcessBuilder processBuilder, final Ref<Pair<T1, T2>> result, final StreamParser<T1> stdParser, final StreamParser<T2> errParser) {
        final Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            LOG.info("Exception launching process: ", e);
            synchronized (result) {
                result.set(new Pair<T1, T2>(null, null));
                result.notifyAll();
            }
            return;
        }

        LOG.info("process started");
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    eatOutStreams(process, result, stdParser, errParser);
                }
                catch (Exception e) {
                    LOG.info("exception: ", e);
                    synchronized (result) {
                        result.set(new Pair<T1, T2>(null, null));
                        result.notifyAll();
                    }
                }
            }
        }, "Google Go Sdk detecting script output streams reader");

        thread.setDaemon(true);

        thread.start();
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    private static <T1, T2> void eatOutStreams(Process process, Ref<Pair<T1, T2>> result, StreamParser<T1> stdParser, StreamParser<T2> errParser) throws Exception {
        final InputStream stdError = process.getErrorStream();
        final InputStream stdOutput = process.getInputStream();

        final Reader errReader = new InputStreamReader(stdError);
        final Reader outReader = new InputStreamReader(stdOutput);

        final StringBuilder err = new StringBuilder();
        final StringBuilder out = new StringBuilder();

        final CharBuffer buffer = CharBuffer.allocate(1024);

        long startTime = System.currentTimeMillis();
        boolean dataRead, processIsStillRunning;
        do {

            LOG.info("draining");
            dataRead = false;
            processIsStillRunning = false;

            while (stdError.available() > 0) {
                LOG.info("Error output stream data");
                final int count = errReader.read(buffer);
                err.append(buffer.array(), 0, count);
                dataRead = true;
            }

            while (stdOutput.available() > 0) {
                LOG.info("stdout output stream data");
                final int count = outReader.read(buffer);
                out.append(buffer.array(), 0, count);
                dataRead = true;
            }

            try {
                process.exitValue();
            } catch (IllegalThreadStateException e) {
                processIsStillRunning = true;
                Thread.sleep(50L);
            }
        } while (dataRead || (processIsStillRunning && (System.currentTimeMillis() - startTime < 2000)));

        synchronized (result) {
            result.set(
                    Pair.create(
                            stdParser != null ? stdParser.parseStream(out.toString()) : null,
                            errParser != null ? errParser.parseStream(err.toString()) : null
                    ));

            err.setLength(0);
            out.setLength(0);

            LOG.info("notifying" + result.get());
            result.notifyAll();
        }

        try {
            LOG.info("getting process exit code");
            process.exitValue();
        } catch (IllegalThreadStateException e) {

            LOG.info("exception .. waiting");
            Thread.sleep(1000L);

            LOG.info("Destroying process");
            process.destroy();
        }
    }
}
