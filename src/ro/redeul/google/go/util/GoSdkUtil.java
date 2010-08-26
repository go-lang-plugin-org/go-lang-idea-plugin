package ro.redeul.google.go.util;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class GoSdkUtil {

    public static final String PACKAGES = "src/pkg";

    private static final Logger LOG = Logger.getInstance("ro.redeul.google.go.util.GoSdkUtil");

    private static final String DEFAULT_MOCK_PATH = "go/default";

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
            binariesPath = path + "/bin";
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

    public static String[] getMockGoogleSdk() {
        return getMockGoogleSdk(PathManager.getHomePath() + "/" + DEFAULT_MOCK_PATH);
    }

    public static String[] getMockGoogleSdk(String path) {
        String[] strings = testGoogleGoSdk(path);
        if ( strings.length > 0 ) {
            new File(strings[1], getCompilerName(strings[2], strings[3])).setExecutable(true);
            new File(strings[1], getLinkerName(strings[2], strings[3])).setExecutable(true);
            new File(strings[1], getArchivePackerName(strings[2], strings[3])).setExecutable(true);
        }

        return strings;
    }

    private static String getArchivePackerName(String os, String arch) {
        return "gopack";
    }

    public static String getCompilerName(String os, String arch) {
        return getBinariesDesignation(os, arch) + "g";
    }

    public static String getLinkerName(String os, String arch) {
        return getBinariesDesignation(os, arch) + "l";
    }

    public static String getBinariesDesignation(String os, String arch) {

        if (arch.equalsIgnoreCase("amd64")) {
            return "6";
        }

        if (arch.equalsIgnoreCase("386")) {
            return "8";
        }

        if (arch.equalsIgnoreCase("arm")) {
            return "5";
        }

        return "unknown";
    }
}
