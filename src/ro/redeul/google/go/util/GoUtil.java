package ro.redeul.google.go.util;

import java.io.File;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 4:18:54 AM
 */
public class GoUtil {
    public static final String ENV_GO_ROOT = "GOROOT";

    public static String resolveGoogleGoHomePath() {
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
