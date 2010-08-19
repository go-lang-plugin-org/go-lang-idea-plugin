package ro.redeul.google.go.util;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 4:18:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoUtil {
    public static final String ENV_GO_ROOT = "GOROOT";

    public static String resolveGoogleGoHomePath() {
        return System.getenv(ENV_GO_ROOT);
    }
}
