/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.idea.golang.jps.incremental;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Consumer;
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
import org.jetbrains.jps.incremental.messages.ProgressMessage;
import org.jetbrains.jps.model.library.JpsTypedLibrary;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.library.sdk.JpsSdkReference;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.service.SharedThreadPool;
import ro.redeul.idea.golang.jps.model.JpsGoModuleType;
import ro.redeul.idea.golang.jps.model.JpsGoSdkType;
import ro.redeul.idea.golang.jps.model.JpsGoSdkTypeProperties;
import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class GoLangBuilder extends ModuleLevelBuilder {

    public static final String BUILDER_NAME = "golang.org builder";

    private static final Logger LOG =
        getInstance("#ro.redeul.idea.golang.jps.incremental.GoLangBuilder");

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

        if (sdkJpsTypedLibrary == null) {
            reportError(context, "Project JDK is not specified or not of type go sdk");
            throw new ProjectBuildException();
        }

        JpsGoSdkTypeProperties properties =
            sdkJpsTypedLibrary.getProperties().getSdkProperties();

        if (properties == null) {
            reportError(context, "Project JDK is not specified or not of type go sdk");
            throw new ProjectBuildException();
        }

        try {
            for (JpsModule goModule : goModules) {
                GoBuildOsProcessHandler handler = executeGoBuildCompilation(properties, goModule, context);

                for (CompilerMessage message : handler.getCompilerMessages()) {
                    context.processMessage(message);
                }
            }

            return ExitCode.OK;
        } catch (Exception e) {
            throw new ProjectBuildException(e);
        }
    }

    private GoBuildOsProcessHandler executeGoBuildCompilation(JpsGoSdkTypeProperties properties, JpsModule goModule, final CompileContext context)
        throws IOException {
        final List<String> cmd = new ArrayList<String>();

        cmd.add(properties.getGoBinPath());
        cmd.add("build");
        cmd.add("./...");

        final List<String> env = new ArrayList<String>();

        List<String> contentRoots = goModule.getContentRootsList().getUrls();

        if (contentRoots.size() != 1) {
            return null;
        }

        String contentRoot = contentRoots.get(0);
        contentRoot = contentRoot.replaceAll("file://", "");

        env.add(String.format("GOPATH=%s", contentRoot));

        final Process process = Runtime.getRuntime().exec(
            ArrayUtil.toStringArray(cmd),
            ArrayUtil.toStringArray(env),
            new File(contentRoot, "src"));

        final Consumer<String> updater = new Consumer<String>() {
            public void consume(String s) {
                context.processMessage(new ProgressMessage(s));
            }
        };

        final GoBuildOsProcessHandler handler = new GoBuildOsProcessHandler(process, updater) {
            @Override
            protected Future<?> executeOnPooledThread(Runnable task) {
                return SharedThreadPool.getInstance().executeOnPooledThread(task);
            }
        };

        handler.startNotify();
        handler.waitFor();

        return handler;
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
