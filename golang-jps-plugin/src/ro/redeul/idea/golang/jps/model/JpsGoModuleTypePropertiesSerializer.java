/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.idea.golang.jps.model;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class JpsGoModuleTypePropertiesSerializer
    extends JpsModulePropertiesSerializer<JpsDummyElement> {

    public JpsGoModuleTypePropertiesSerializer() {
	super(JpsGoModuleType.INSTANCE, "GO_MODULE", "NewModuleRootManager");
    }

    @Override
    public JpsDummyElement loadProperties(@Nullable Element componentElement) {
	return JpsElementFactory.getInstance().createDummyElement();
    }

    @Override
    public void saveProperties(@NotNull JpsDummyElement properties, @NotNull Element componentElement) {
	//To change body of implemented methods use File | Settings | File Templates.
    }
}
