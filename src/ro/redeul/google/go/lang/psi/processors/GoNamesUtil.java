package ro.redeul.google.go.lang.psi.processors;

import java.util.regex.Pattern;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/21/11
 * Time: 6:26 PM
 */
public class GoNamesUtil {

    private static Pattern RE_PUBLIC_NAME = Pattern.compile("^\\p{Lu}\\p{L}*$");

    public static boolean isPublicType(String type) {
        return RE_PUBLIC_NAME.matcher(type).matches();
    }

    public static boolean isPublicFunction(String functionName) {
        return RE_PUBLIC_NAME.matcher(functionName).matches();
    }
}
