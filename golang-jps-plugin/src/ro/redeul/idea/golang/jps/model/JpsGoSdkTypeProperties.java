/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.idea.golang.jps.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.ex.JpsElementBase;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class JpsGoSdkTypeProperties
    extends JpsElementBase<JpsGoSdkTypeProperties> implements JpsElement {

    public String GO_HOME_PATH = "";
    public String GO_BIN_PATH = "";

    public String VERSION_MAJOR = "";
    public String VERSION_MINOR = "";

    public JpsGoSdkTypeProperties() { }

    public JpsGoSdkTypeProperties(@NotNull String goHomePath, @NotNull String goBinPath,
				  @NotNull String goVersionMajor, @NotNull String goVersionMinor) {
	this.GO_HOME_PATH = goHomePath;
	this.GO_BIN_PATH = goBinPath;
	this.VERSION_MAJOR = goVersionMajor;
	this.VERSION_MINOR = goVersionMinor;
    }

    public JpsGoSdkTypeProperties(JpsGoSdkTypeProperties original) {
	this.GO_HOME_PATH = original.GO_HOME_PATH;
	this.GO_BIN_PATH = original.GO_BIN_PATH;
	this.VERSION_MAJOR = original.VERSION_MAJOR;
	this.VERSION_MINOR = original.VERSION_MINOR;
    }

    @NotNull
    @Override
    public JpsGoSdkTypeProperties createCopy() {
	return new JpsGoSdkTypeProperties(this);
    }

    public void applyChanges(@NotNull JpsGoSdkTypeProperties modified) {
    }

    @NotNull
    public String getGoHomePath() {
	return GO_HOME_PATH;
    }

    @NotNull
    public String getGoBinPath() {
	return GO_BIN_PATH;
    }

//    public GoTargetOs TARGET_OS = null;
//    public GoTargetArch TARGET_ARCH = null;

    @NotNull
    public String getVersionMajor() {
	return VERSION_MAJOR;
    }

    @NotNull
    public String getVersionMinor() {
	return VERSION_MINOR;
    }
}
