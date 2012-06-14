package ro.redeul.google.go.intentions.conversions;

import java.util.regex.Pattern;

public class ConversionUtil {

    private static final Pattern DECIMAL_INTEGER_PATTERN = Pattern.compile("[1-9][0-9]*");
    private static final Pattern HEX_INTEGER_PATTERN = Pattern.compile("0[xX][0-9]+");
    private static final Pattern OCTAL_INTEGER_PATTERN = Pattern.compile("0[0-7]+");

    public static boolean isDecimalInteger(String text) {
        return DECIMAL_INTEGER_PATTERN.matcher(text).matches();
    }

    public static boolean isHexInteger(String text) {
        return HEX_INTEGER_PATTERN.matcher(text).matches();
    }

    public static boolean isOctalInteger(String text) {
        return OCTAL_INTEGER_PATTERN.matcher(text).matches();
    }
}
