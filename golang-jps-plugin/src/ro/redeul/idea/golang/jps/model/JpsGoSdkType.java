/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.idea.golang.jps.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class JpsGoSdkType extends JpsSdkType<JpsGoSdkTypeProperties>
    implements JpsElementTypeWithDefaultProperties<JpsGoSdkTypeProperties> {
    public static final JpsGoSdkType INSTANCE = new JpsGoSdkType();

    @NotNull
    @Override
    public JpsGoSdkTypeProperties createDefaultProperties() {
	return new JpsGoSdkTypeProperties();
    }
}
