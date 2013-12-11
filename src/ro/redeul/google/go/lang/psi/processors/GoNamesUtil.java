package ro.redeul.google.go.lang.psi.processors;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/21/11
 * Time: 6:26 PM
 */
public class GoNamesUtil {

    private static final Pattern RE_PUBLIC_NAME = Pattern.compile("^\\p{Lu}.*$");
    private static final Set<String> PREDEFINED_CONSTANT = new HashSet<String>();

    static {
        PREDEFINED_CONSTANT.add("true");
        PREDEFINED_CONSTANT.add("false");
        PREDEFINED_CONSTANT.add("nil");
    }

    public static boolean isPublicType(String type) {
        if (type.length() == 0 ){
            return false;
        }
        char first = type.charAt(0);
        return first >= 'A' && first <= 'Z';
    }

    public static boolean isExportedName(String name) {
        if (name.length() == 0 ){
            return false;
        }
        char first = name.charAt(0);
        return first >= 'A' && first <= 'Z';
    }

    public static boolean isPredefinedConstant(String variable) {
        return PREDEFINED_CONSTANT.contains(variable);
    }
}
