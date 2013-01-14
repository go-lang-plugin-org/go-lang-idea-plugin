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
import org.jetbrains.jps.model.module.JpsModule;
import ro.redeul.idea.golang.jps.model.GoModuleType;

public class GoLangBuilder extends ModuleLevelBuilder {

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
	    if ( module.getModuleType() == GoModuleType.INSTANCE) {
		goModules.add(module);
	    }
	}

	if ( goModules.size() == 0 )
	    return ExitCode.NOTHING_DONE;

	context.getProjectDescriptor().getProjectJavaSdks()
    }

    @NotNull
    @Override
    public String getPresentableName() {
	return "golang module builder";
    }
}
