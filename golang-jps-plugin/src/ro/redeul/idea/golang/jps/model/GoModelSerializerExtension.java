/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.idea.golang.jps.model;

import java.util.Arrays;
import java.util.List;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.library.JpsLibrary;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;
import org.jetbrains.jps.model.serialization.library.JpsLibraryRootTypeSerializer;
import org.jetbrains.jps.model.serialization.library.JpsSdkPropertiesSerializer;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoModelSerializerExtension extends JpsModelSerializerExtension {

    @NotNull
    @Override
    public List<? extends JpsModulePropertiesSerializer<?>> getModulePropertiesSerializers() {
	return Arrays.asList(new GoModulePropertiesSerializer());
    }

    @NotNull
    @Override
    public List<JpsLibraryRootTypeSerializer> getSdkRootTypeSerializers() {
	// see here: /Users/mtoader/Work/Personal/idea/jps/model-serialization/src/org/jetbrains/jps/model/serialization/library/JpsSdkTableSerializer.java:73

	LOG.debug("Loading " + typeId + " SDK '" + name + "'");
	JpsSdkPropertiesSerializer<?> serializer = getSdkPropertiesSerializer(typeId);
	final JpsLibrary library = createSdk(name, serializer, sdkElement);
	final Element roots = sdkElement.getChild(ROOTS_TAG);

	//
	return super.getSdkRootTypeSerializers();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
