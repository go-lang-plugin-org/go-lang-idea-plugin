/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.dlv;

import com.goide.dlv.rdp.*;
import com.intellij.util.Function;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.DeclarativeScope;
import org.jetbrains.debugger.Variable;
import org.jetbrains.debugger.VariablesHost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DlvDeclarativeScope extends DeclarativeScope<DlvValueManager> {
  private final String actor;

  public DlvDeclarativeScope(@NotNull Type type,
                             @NotNull Frame.Environment environment,
                             @NotNull DlvValueManager valueManager,
                             boolean useBindingsFromFrameData) {
    super(type, getScopeDescription(environment));

    actor = environment.actor();

    childrenManager = new VariablesHost<DlvValueManager>(valueManager) {
      @NotNull
      @Override
      protected Promise<List<Variable>> load() {
        return valueManager.getVm().commandProcessor.send(DlvRequest.getBindings(actor))
          .then(new Function<Bindings, List<Variable>>() {
            @Override
            public List<Variable> fun(Bindings bindings) {
              List<Variable> variables = vmBindingsToOurVariables(valueManager, bindings);
              valueManager.promoteRecentlyAddedActorsToThreadLifetime();
              return variables;
            }
          });
      }
    };

    Bindings bindings = environment.bindings();
    if (useBindingsFromFrameData && bindings != null) {
      childrenManager.set(vmBindingsToOurVariables(valueManager, bindings));
    }
  }

  @NotNull
  private static List<Variable> vmBindingsToOurVariables(@NotNull DlvValueManager valueManager, @NotNull Bindings bindings) {
    List<Variable> variables = new ArrayList<Variable>();
    List<Map<String, PropertyDescriptor>> uglyArgsDescriptors = bindings.arguments();
    if (uglyArgsDescriptors != null && !uglyArgsDescriptors.isEmpty()) {
      Map<String, PropertyDescriptor> mapToFixUglyFxMap = new THashMap<String, PropertyDescriptor>();
      for (Map<String, PropertyDescriptor> uglyMap : uglyArgsDescriptors) {
        mapToFixUglyFxMap.putAll(uglyMap);
      }
      createProperties(mapToFixUglyFxMap, valueManager, variables);
    }

    createProperties(bindings.variables(), valueManager, variables);
    return variables;
  }

  private static void createProperties(@Nullable Map<String, PropertyDescriptor> descriptors, @NotNull DlvValueManager valueManager, @NotNull List<Variable> variables) {
    if (descriptors == null || descriptors.isEmpty()) {
      return;
    }

    for (Map.Entry<String, PropertyDescriptor> entry : descriptors.entrySet()) {
      variables.add(valueManager.createProperty(entry.getKey(), entry.getValue(), null));
    }
  }

  @Nullable
  private static String getScopeDescription(@NotNull Frame.Environment environment) {
    if (environment.function() == null) {
      Grip object = environment.object();
      return object == null ? null : object.className();
    }
    else {
      return DlvCallFrame.getFunctionName(environment.function());
    }
  }
}
