package ro.redeul.google.go.lang.psi.processors;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/21/11
 * Time: 6:26 PM
 */
public class GoNamesUtil {
    public static boolean isExported(String name) {
        if ( name == null || name.length() == 0 )
            return false;

        return Character.isUpperCase(name.charAt(0));
    }
}
