package ro.redeul.google.go.config.sdk;

/**
* Author: Toader Mihai Claudiu <mtoader@gmail.com>
* <p/>
* Date: 8/10/11
* Time: 8:23 AM
*/
public enum GoTargetArch {
    _386("386"), _amd64("amd64"), _arm("arm");

    String name;

    GoTargetArch(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static GoTargetArch fromString(String string) {

        if ( string == null )
            return null;

        for (GoTargetArch arch : GoTargetArch.values()) {
            if  (arch.getName().equalsIgnoreCase(string) ) {
                return arch;
            }
        }

        return null;
    }
}
