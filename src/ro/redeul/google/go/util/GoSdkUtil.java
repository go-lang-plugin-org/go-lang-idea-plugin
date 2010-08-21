package ro.redeul.google.go.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
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
    public static String[] testGoogleGoSdk(String path) {

        try {
            File f = FileUtil.createTempFile("google-go-sdk-detector", "");

            FileUtil.writeToFile(f, getSdkTesterScriptContents(path), false);
            f.setExecutable(true);
            f.deleteOnExit();

            Pair<String[], String> executionResult =
                    ProcessUtil.executeAndProcessOutput(
                            Arrays.asList("bash", "-c", f.getAbsolutePath()),
                            new File(path, "src"),
                            new ProcessUtil.StreamParser<String[]>() {
                                public String[] parseStream(String data) {
                                    return data.replaceAll("\n+$", "").split("\\|");
                                }
                            },
                            ProcessUtil.NULL_PARSER);

//            f.delete();
               
            return executionResult.getFirst();

        } catch (IOException e) {
            return null;
        }
    }

    private static byte[] getSdkTesterScriptContents(String path) {
        String script = "\n" +
                ". env.bash\n" +
                "echo \"$GOROOT|$GOBIN|$GOOS|$GOARCH|`$GOBIN/6g -V`\"\n";

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
        if (arch.equalsIgnoreCase("amd64")) {
            return "6g";
        }

        if (arch.equalsIgnoreCase("386")) {
            return "8g";
        }

        if (arch.equalsIgnoreCase("arm")) {
            return "5g";
        }

        return "unknown";
    }

    public static String getLinkerName(String os, String arch) {
        if (arch.equalsIgnoreCase("amd64")) {
            return "6l";
        }

        if (arch.equalsIgnoreCase("386")) {
            return "8l";
        }

        if (arch.equalsIgnoreCase("arm")) {
            return "5l";
        }

        return "unknown";
    }
}
