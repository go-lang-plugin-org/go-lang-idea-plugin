/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.idea.golang.jps.incremental;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.BuilderCategory;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ModuleBuildTarget;
import org.jetbrains.jps.incremental.ModuleLevelBuilder;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.library.JpsTypedLibrary;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.library.sdk.JpsSdkReference;
import org.jetbrains.jps.model.module.JpsModule;
import ro.redeul.idea.golang.jps.model.JpsGoModuleType;
import ro.redeul.idea.golang.jps.model.JpsGoSdkType;
import ro.redeul.idea.golang.jps.model.JpsGoSdkTypeProperties;

public class GoLangBuilder extends ModuleLevelBuilder {

    public static final String BUILDER_NAME = "golang.org builder";

    private static final Logger LOG =
	Logger.getInstance(
	    "#ro.redeul.idea.golang.jps.incremental.GoLangBuilder");

    public GoLangBuilder() {
	super(BuilderCategory.OVERWRITING_TRANSLATOR);
	LOG.error("Called");
    }

    @Override
    public ExitCode build(CompileContext context, ModuleChunk chunk, DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder, OutputConsumer outputConsumer)
	throws ProjectBuildException, IOException {

	Set<JpsModule> modules = chunk.getModules();
	Set<JpsModule> goModules = new HashSet<JpsModule>();

	for (JpsModule module : modules) {
	    if (module.getModuleType() == JpsGoModuleType.INSTANCE) {
		goModules.add(module);
	    }
	}

	if (goModules.size() == 0)
	    return ExitCode.NOTHING_DONE;

	JpsSdkReference<JpsGoSdkTypeProperties> goSdkPropertiesRef =
	    context.getProjectDescriptor()
		   .getProject()
		   .getSdkReferencesTable()
		   .getSdkReference(JpsGoSdkType.INSTANCE);

	if (goSdkPropertiesRef == null) {
	    reportError(context,
			"Project JDK is not specified or not of type go sdk");
	    throw new ProjectBuildException();
	}
	JpsTypedLibrary<JpsSdk<JpsGoSdkTypeProperties>> sdkJpsTypedLibrary =
	    goSdkPropertiesRef.resolve();

	if ( sdkJpsTypedLibrary == null ) {
	    reportError(context, "Project JDK is not specified or not of type go sdk");
	    throw new ProjectBuildException();
	}

	JpsGoSdkTypeProperties properties = sdkJpsTypedLibrary.getProperties().getSdkProperties();

	String binPath = properties.getGoBinPath();

	return ExitCode.ABORT;
    }

    @NotNull
    @Override
    public String getPresentableName() {
	return "golang module builder";
    }

    private void reportError(CompileContext context, final String text) {
	context.processMessage(
	    new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR, text));
    }
}
