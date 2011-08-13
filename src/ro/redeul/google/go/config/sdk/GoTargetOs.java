package ro.redeul.google.go.config.sdk;

/**
* Author: Toader Mihai Claudiu <mtoader@gmail.com>
* <p/>
* Date: 8/10/11
* Time: 8:23 AM
*/
public enum GoTargetOs {

    Windows("windows"), Linux("linux"), Darwin("darwin"), FreeBsd("freebsd");

    String name;

    GoTargetOs(String name) {
        this.name = name;
    }

    public static GoTargetOs fromString(String name) {

        if ( name == null )
            return null;

        name = name.toLowerCase();

        for (GoTargetOs os : GoTargetOs.values()) {
            if ( os.getName().equalsIgnoreCase(name) ) {
                return os;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }
}
