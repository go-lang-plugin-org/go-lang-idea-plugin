package ro.redeul.google.go.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.CommonProcessors;

import java.io.*;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GoSdkUtil {

    public static final String PACKAGES = "src/pkg";

    private static final Logger LOG = Logger.getInstance("ro.redeul.google.go.util.GoSdkUtil");

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public static List<String> testGoogleGoSdk(String path) {

        try {
            File f = FileUtil.createTempFile("google-go-sdk-detector", "");

            FileUtil.writeToFile(f, getSdkTesterScriptContents(path), false);
            f.setExecutable(true);
            f.deleteOnExit();

            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", f.getAbsolutePath());
            processBuilder.directory(new File(path, "src"));

            final Ref<List<String>> result = Ref.create(null);

            doParseProcessOutput(processBuilder, result);

            synchronized (result) {
                while (result.isNull()) {
                    try {
                        result.wait(200);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }

            f.delete();
            
            return result.isNull() ? null : result.get();

        } catch (IOException e) {
            return null;
        }
    }

    private static void doParseProcessOutput(ProcessBuilder processBuilder, final Ref<List<String>> result) throws IOException {
        final Process process = processBuilder.start();
        LOG.info("process started");
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    eatOutStreams(process, result);
                }
                catch (Exception e) {
                    LOG.info("exception: ", e);
                    synchronized (result) {
                        result.set(Collections.<String>emptyList());
                        result.notifyAll();
                    }
                }
            }
        }, "Google Go Sdk detecting script output streams reader");

        thread.setDaemon(true);
        
        thread.start();
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    private static void eatOutStreams(Process process, Ref<List<String>> result) throws Exception {
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
        } while (dataRead || (processIsStillRunning && (System.currentTimeMillis() - startTime < 2000)) );

        synchronized (result) {
            if (err.length() > 0) {
                System.out.println(err);
                result.set(Collections.<String>emptyList());
            } else {
                result.set(Arrays.asList(out.toString().replaceAll("\n+$", "").split("\\|")));
            }

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

    private static byte[] getSdkTesterScriptContents(String path) {
        String script = "\n" +
                ". env.bash\n" +
                "echo \"$GOROOT|$GOBIN|$GOOS|$GOARCH|6006 release release.2010-08-11\"\n";

        return script.getBytes();
    }

    public static Collection<File> findGoogleSdkPackages(String homePath) {

        CommonProcessors.CollectUniquesProcessor<File> processor = new CommonProcessors.CollectUniquesProcessor<File>() {
            @Override
            public boolean process(File file) {
                File compiledPackage = new File(file, "_go_.6");
                if (compiledPackage.exists() && compiledPackage.isFile()) {
                    super.process(file);
                }

                return true;
            }
        };
        FileUtil.processFilesRecursively(new File(homePath + "/" + PACKAGES), processor);

        return processor.getResults();
    }

    public static String getCompilerName(String os, String arch) {
        if ( arch.equalsIgnoreCase("amd64") ) {
            return "6g";
        }

        if ( arch.equalsIgnoreCase("386") ) {
            return "8g";
        }

        if ( arch.equalsIgnoreCase("arm") ) {
            return "5g";
        }

        return "unknown";
    }

    public static String getLinkerName(String os, String arch) {
        if ( arch.equalsIgnoreCase("amd64") ) {
            return "6l";
        }

        if ( arch.equalsIgnoreCase("386") ) {
            return "8l";
        }

        if ( arch.equalsIgnoreCase("arm") ) {
            return "5l";
        }

        return "unknown";
    }
}
