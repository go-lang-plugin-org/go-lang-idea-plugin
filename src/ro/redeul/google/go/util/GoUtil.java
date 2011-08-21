package ro.redeul.google.go.util;

import com.intellij.ide.Bootstrap;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SystemProperties;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;
import ro.redeul.google.go.config.sdk.GoSdkType;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 4:18:54 AM
 */
public class GoUtil {
    public static final String ENV_GO_ROOT = "GOROOT";


    /**
     * Gets the idea home directory.
     * Note: Copied over from IDEA Main class but the home directory doesn't seem to be properly made available to
     * plugins.
     * @return The idea home directory
     * @throws IOException If error when building the path
     */
    private static File getIdeaHomeDir() throws IOException {
        URL url = Bootstrap.class.getResource("");
        if (url == null || !"jar".equals(url.getProtocol())) return null;

        String path = url.getPath();

        int start = path.indexOf("file:/");
        int end = path.indexOf("!/");
        if (start == -1 || end == -1) return null;

        String jarFileUrl = path.substring(start, end);

        try {
            File bootstrapJar = new File(new URI(jarFileUrl));
            return bootstrapJar.getParentFile().getParentFile();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static String resolvePotentialGoogleGoAppEngineHomePath() {

        if ( ! isSdkRegistered(PathManager.getHomePath() + "/bundled/go-appengine-sdk", GoAppEngineSdkType.getInstance()) ) {
            return PathManager.getHomePath() + "/bundled/go-appengine-sdk";
        }

        String path = System.getenv("PATH");
        if ( path == null ) {
            return null;
        }

        String []parts = path.split("[:;]+");
        for (String part : parts) {
            if ( ! isSdkRegistered(part, GoAppEngineSdkType.getInstance()) ) {
                return part;
            }
        }

        return SystemProperties.getUserHome();
    }

    public static String resolvePotentialGoogleGoHomePath() {

        if ( ! isSdkRegistered(PathManager.getHomePath() + "/bundled/go-sdk", GoSdkType.getInstance()) ) {
            return PathManager.getHomePath() + "/bundled/go-sdk";
        }

        String goRoot = System.getenv(ENV_GO_ROOT);
        if ( goRoot != null && ! isSdkRegistered(goRoot, GoSdkType.getInstance()) ) {
            return goRoot;
        }

        if ( testPathExists("/usr/lib/go") ) {
            return "/usr/lib/go";
        }

        return SystemProperties.getUserHome();
    }

    private static boolean isSdkRegistered(String homePath, SdkType sdkType) {

        VirtualFile homePathAsVirtualFile;
        try {
            homePathAsVirtualFile = VfsUtil.findFileByURL(new URL(VfsUtil.pathToUrl(homePath)));
        } catch (MalformedURLException e) {
            return true;
        }

        if ( homePathAsVirtualFile == null || ! homePathAsVirtualFile.isDirectory() ) {
            return true;
        }

        ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();

        List<Sdk> registeredSdks = jdkTable.getSdksOfType(sdkType);

        for (Sdk registeredSdk : registeredSdks) {
            if ( homePathAsVirtualFile.equals(registeredSdk.getHomeDirectory()) ) {
                return true;
            }
        }

        return false;
    }

    private static boolean testPathExists(String goRoot) {
        return goRoot != null && goRoot.trim().length() > 0 && new File(goRoot).isDirectory();
    }

    private final static Pattern RE_PACKAGE_TARGET = Pattern.compile("^TARG=([^\\s]+)\\s*$", Pattern.MULTILINE);

    /**
     * Returns a string if there is a TARG=xxx specified in the provided makefile and null if there is no such file.
     *
     * @param makefile the file we want to test (can be null)
     *
     * @return the specified target or null
     */
    public static String getTargetFromMakefile(VirtualFile makefile) {
        if ( makefile == null ) {
            return null;
        }

        try {
            String content = new String(makefile.contentsToByteArray(), "UTF-8");

            Matcher matcher = RE_PACKAGE_TARGET.matcher(content);
            if ( matcher.find() ) {
                return matcher.group(1);
            }
        } catch (IOException e) {
            //
        }

        return null;
    }
}

