/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.jps.builder;

import com.goide.jps.model.JpsGoModuleProperties;
import com.goide.jps.model.JpsGoModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildTargetLoader;
import org.jetbrains.jps.builders.ModuleBasedBuildTargetType;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.module.JpsTypedModule;

import java.util.ArrayList;
import java.util.List;

public class GoTargetType extends ModuleBasedBuildTargetType<GoTarget> {
  public static final GoTargetType PRODUCTION = new GoTargetType("go-production", false);
  public static final GoTargetType TESTS = new GoTargetType("go-tests", true);
  private final boolean myTests;

  private GoTargetType(String typeId, boolean tests) {
    super(typeId);
    myTests = tests;
  }

  @NotNull
  @Override
  public List<GoTarget> computeAllTargets(@NotNull JpsModel model) {
    List<GoTarget> targets = new ArrayList<GoTarget>();
    for (JpsTypedModule<JpsSimpleElement<JpsGoModuleProperties>> module : model.getProject().getModules(JpsGoModuleType.INSTANCE)) {
      targets.add(new GoTarget(module, this));
    }
    return targets;
  }

  @NotNull
  @Override
  public BuildTargetLoader<GoTarget> createLoader(@NotNull final JpsModel model) {
    return new BuildTargetLoader<GoTarget>() {
      @Nullable
      @Override
      public GoTarget createTarget(@NotNull String targetId) {
        for (JpsTypedModule<JpsSimpleElement<JpsGoModuleProperties>> module : model.getProject().getModules(JpsGoModuleType.INSTANCE)) {
          if (module.getName().equals(targetId)) {
            return new GoTarget(module, GoTargetType.this);
          }
        }
        return null;
      }
    };
  }

  public boolean isTests() {
    return myTests;
  }
}
