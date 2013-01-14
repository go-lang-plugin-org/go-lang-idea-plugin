/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.idea.golang.jps.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.library.JpsOrderRootType;
import org.jetbrains.jps.model.serialization.library.JpsLibraryRootTypeSerializer;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoSdkTypeSerializer extends JpsLibraryRootTypeSerializer {

    public GoSdkTypeSerializer(@NotNull String typeId, @NotNull JpsOrderRootType type, boolean writeIfEmpty) {
	super(typeId, type, writeIfEmpty);
    }
}
