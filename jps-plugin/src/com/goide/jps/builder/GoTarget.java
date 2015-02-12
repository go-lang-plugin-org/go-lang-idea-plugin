package com.goide.jps.builder;

import com.goide.jps.model.JpsGoModuleType;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.*;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.indices.IgnoredFileIndex;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.java.JavaSourceRootProperties;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.java.JpsJavaClasspathKind;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsTypedModuleSourceRoot;

import java.io.File;
import java.util.*;

public class GoTarget extends ModuleBasedTarget<GoSourceRootDescriptor> {
  public GoTarget(@NotNull JpsModule module, GoTargetType targetType) {
    super(targetType, module);
  }

  @NotNull
  @Override
  public String getId() {
    return myModule.getName();
  }

  @NotNull
  @Override
  public Collection<BuildTarget<?>> computeDependencies(BuildTargetRegistry targetRegistry, TargetOutputIndex outputIndex) {
    List<BuildTarget<?>> dependencies = new ArrayList<BuildTarget<?>>();
    Set<JpsModule> modules = JpsJavaExtensionService.dependencies(myModule).includedIn(JpsJavaClasspathKind.compile(isTests())).getModules();
    for (JpsModule module : modules) {
      if (module.getModuleType() == JpsGoModuleType.INSTANCE) {
        dependencies.add(new GoTarget(module, getGoTargetType()));
      }
    }
    if (isTests()) {
      dependencies.add(new GoTarget(myModule, GoTargetType.PRODUCTION));
    }
    return dependencies;
  }

  @NotNull
  @Override
  public List<GoSourceRootDescriptor> computeRootDescriptors(JpsModel model, ModuleExcludeIndex index, IgnoredFileIndex ignoredFileIndex, BuildDataPaths dataPaths) {
    List<GoSourceRootDescriptor> result = new ArrayList<GoSourceRootDescriptor>();
    JavaSourceRootType type = isTests() ? JavaSourceRootType.TEST_SOURCE : JavaSourceRootType.SOURCE;
    for (JpsTypedModuleSourceRoot<JavaSourceRootProperties> root : myModule.getSourceRoots(type)) {
      result.add(new GoSourceRootDescriptor(root.getFile(), this));
    }
    return result;
  }

  @Nullable
  @Override
  public GoSourceRootDescriptor findRootDescriptor(@NotNull String rootId, @NotNull BuildRootIndex rootIndex) {
    return ContainerUtil.getFirstItem(rootIndex.getRootDescriptors(new File(rootId), Collections.singletonList(getGoTargetType()), null));
  }

  @NotNull
  @Override
  public String getPresentableName() {
    return "Go '" + myModule.getName() + "' " + (isTests() ? "tests" : "production");
  }

  @NotNull
  @Override
  public Collection<File> getOutputRoots(CompileContext context) {
    return ContainerUtil.createMaybeSingletonList(JpsJavaExtensionService.getInstance().getOutputDirectory(myModule, isTests()));
  }
  
  @Override
  public boolean isTests() {
    return getGoTargetType().isTests();
  }

  @NotNull
  public GoTargetType getGoTargetType() {
    return (GoTargetType) getTargetType();
  }
}
