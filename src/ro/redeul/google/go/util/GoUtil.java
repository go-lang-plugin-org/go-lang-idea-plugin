package ro.redeul.google.go.util;

import com.intellij.ide.Bootstrap;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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

    public static String resolveGoogleGoHomePath() {
        // Try the bundled sdk first since it should have been tested as working, can still be overridden in the
        // project settings
        try {
            String goRoot = getIdeaHomeDir().getCanonicalPath() + "/go-sdk";
            if ( testPathExists(goRoot)) {
                return goRoot;
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Impossible to get the idea home directory", e);
        }

        String goRoot = System.getenv(ENV_GO_ROOT);
        if ( testPathExists(goRoot) ) {
            return goRoot;
        }

        if ( testPathExists("/usr/lib/go") ) {
            return "/usr/lib/go";
        }

        return null;
    }

    private static boolean testPathExists(String goRoot) {
        return goRoot != null && goRoot.trim().length() > 0 && new File(goRoot).isDirectory();
    }
}
