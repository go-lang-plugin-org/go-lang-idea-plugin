package ro.redeul.google.go.sdk;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/10/11
 * Time: 7:13 AM
 */
public class GoSdkValidationResult {

    public enum Os {

        Windows, Linux, Darwin, FreeBsd;

        public static Os fromString(String name) {

            if ( name == null )
                return null;

            name = name.toLowerCase();

            if ( name.equals("linux") )
                return Linux;

            if ( name.equals("darwin") )
                return Darwin;

            if ( name.equals("windows") )
                return Windows;

            if ( name.equals("freebsd") )
                return FreeBsd;

            return null;
        }
    }

    public enum Arch {
        _386, _amd64;

        public static Arch fromString(String string) {

            if ( string == null )
                return null;

            if ( string.equals("386") )
                return _386;

            if ( string.equals("amd64") )
                return _amd64;

            return null;
        }

    }

    public GoSdkValidationResult() {
    }
}

