/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.idea.golang.jps.model;

import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.serialization.library.JpsSdkPropertiesSerializer;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class JpsGoSdkTypePropertiesSerializer
    extends JpsSdkPropertiesSerializer<JpsGoSdkTypeProperties> {

    public JpsGoSdkTypePropertiesSerializer() {
	super("Google Go SDK", JpsGoSdkType.INSTANCE);
    }

    @NotNull
    @Override
    public JpsGoSdkTypeProperties loadProperties(@Nullable Element element) {

	JpsGoSdkTypeProperties properties = element != null
		? XmlSerializer.deserialize(element,
					    JpsGoSdkTypeProperties.class)
		: new JpsGoSdkTypeProperties();

	return properties == null ? new JpsGoSdkTypeProperties() : properties;
    }

    @Override
    public void saveProperties(@NotNull JpsGoSdkTypeProperties properties, @NotNull Element element) {
    }
}
