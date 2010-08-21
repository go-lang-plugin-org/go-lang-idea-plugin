package ro.redeul.google.go.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.CommonProcessors;

import java.io.*;
import java.nio.CharBuffer;
import java.util.*;

public class GoSdkUtil {

    public static final String PACKAGES = "src/pkg";

    private static final Logger LOG = Logger.getInstance("ro.redeul.google.go.util.GoSdkUtil");

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public static String[] testGoogleGoSdk(String path) {

        File rootFile = new File(path);

        // check that the selection was a folder
        if ( ! rootFile.exists() || ! rootFile.isDirectory() ) {
            return new String[0];
        }

        // check to see if we have a %GOROOT/src folder
        File srcFolder = new File(rootFile, "src");
        if ( ! srcFolder.exists() || ! srcFolder.isDirectory() ) {
            return new String[0];
        }

        // check to see if we have a %GOROOT/pkg folder
        File pkgFolder = new File(rootFile, "pkg");
        if ( ! pkgFolder.exists() || ! pkgFolder.isDirectory() ) {
            return new String[0];
        }

        // check to see if we have a %GOROOT/pkg/~place-holder~ file        
        File placeHolder = new File(pkgFolder, "~place-holder~");
        if ( ! placeHolder.exists() || ! placeHolder.isFile() ) {
            return new String[0];
        }

        File targets[] = pkgFolder.listFiles(new FileFilter() {
            public boolean accept(File pathName) {
                return pathName.isDirectory() && ! pathName.getName().matches("\\.{1,2}");
            }
        });

        if ( targets.length != 1 || ! targets[0].getName().matches("(windows|linux|darwin|freebsd)_(386|amd64|arm)")) {
            return new String[0];
        }

        String []target = targets[0].getName().split("_");

        String compilerName = getCompilerName(target[0], target[1]);

        String binariesPath = System.getenv("GOBIN");
        if ( binariesPath == null ) {
            if ( target[0].equals("windows") ) {
                binariesPath = path + "/bin";
            } else {
                binariesPath = System.getenv("HOME") + "/bin";
            }
        }

        Pair<String, String> executionResult =
                ProcessUtil.executeAndProcessOutput(
                        Arrays.asList(binariesPath + "/" + compilerName, "-V"),
                        new File(path),
                        ProcessUtil.NULL_PARSER,
                        ProcessUtil.NULL_PARSER);

        return new String[]{path, binariesPath, target[0], target[1], executionResult.getFirst()};
    }
//
//    public static Collection<File> findGoogleSdkPackages(String homePath) {
//
//        CommonProcessors.CollectUniquesProcessor<File> processor = new CommonProcessors.CollectUniquesProcessor<File>() {
//            @Override
//            public boolean process(File file) {
//                File compiledPackage = new File(file, "_go_.6");
//                if (compiledPackage.exists() && compiledPackage.isFile()) {
//                    super.process(file);
//                }
//
//                return true;
//            }
//        };
//
//        FileUtil.processFilesRecursively(new File(homePath + "/" + PACKAGES), processor);
//
//        return processor.getResults();
//    }

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
