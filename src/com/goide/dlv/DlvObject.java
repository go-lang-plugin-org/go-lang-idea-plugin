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
import com.intellij.util.Consumer;
import com.intellij.util.Function;
import com.intellij.util.SmartList;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Obsolescent;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.EvaluateContext;
import org.jetbrains.debugger.Variable;
import org.jetbrains.debugger.VariablesHost;
import org.jetbrains.debugger.values.ObjectValueBase;
import org.jetbrains.debugger.values.ValueType;

import java.util.*;

class DlvObject extends ObjectValueBase<DlvValueManager> {
  @Nullable private final String className;
  @Nullable private final String valueString;
  @Nullable private final String actor;

  private final int ownPropertiesLength;

  @Nullable private List<Variable> preloadedProperties;

  public DlvObject(@NotNull ValueType type, @NotNull final Grip valueData, @NotNull DlvValueManager valueManager) {
    super(type);

    className = valueData.className();

    Grip.Preview preview = valueData.preview();
    valueString = valueToString(valueData, preview);

    if (preview == null) {
      ownPropertiesLength = -1;
    }
    else {
      ownPropertiesLength = preview.ownPropertiesLength();
      Map<String, PropertyDescriptor> ownPropertiesPreview = preview.ownProperties();
      if (ownPropertiesPreview != null && !ownPropertiesPreview.isEmpty() && ownPropertiesPreview.size() <= ownPropertiesLength) {
        preloadedProperties = valueManager.createProperties(ownPropertiesPreview, null, preview.safeGetterValues());
      }
    }

    actor = valueData.actor();
    assert actor != null;
    childrenManager = new VariablesHost<DlvValueManager>(valueManager) {
      @NotNull
      @Override
      protected Promise<List<Variable>> load() {
        if (preloadedProperties == null) {
          return valueManager.getVm().getCommandProcessor().send(DlvRequest.getPrototypeAndProperties(actor))
            .then(new Function<PrototypeAndPropertiesResult, List<Variable>>() {
              @NotNull
              @Override
              public List<Variable> fun(@NotNull PrototypeAndPropertiesResult result) {
                List<Variable> properties = valueManager.createProperties(result.ownProperties(), result.prototype(), result.safeGetterValues());
                valueManager.promoteRecentlyAddedActorsToThreadLifetime();
                return properties;
              }
            });
        }
        else {
          return valueManager.getVm().getCommandProcessor().send(DlvRequest.getPrototype(actor))
            .then(new Function<PrototypeResult, List<Variable>>() {
              @NotNull
              @Override
              public List<Variable> fun(@NotNull PrototypeResult prototypeResult) {
                List<Variable> list = preloadedProperties;
                preloadedProperties = null;
                Variable protoVariable = valueManager.createProtoVariable(prototypeResult.prototype());
                if (list.isEmpty()) {
                  return Collections.singletonList(protoVariable);
                }
                else {
                  list = new ArrayList<Variable>(list);
                  list.add(protoVariable);
                  valueManager.promoteRecentlyAddedActorsToThreadLifetime();
                  return list;
                }
              }
            });
        }
      }
    };
  }

  @NotNull
  @Override
  public ThreeState hasProperties() {
    return ownPropertiesLength == -1 ? ThreeState.UNSURE : ThreeState.fromBoolean(ownPropertiesLength != 0);
  }

  @Nullable
  private static String valueToString(@NotNull Grip valueData, @Nullable Grip.Preview preview) {
    if (valueData.displayString() != null) {
      return valueData.displayString();
    }
    else if (preview != null) {
      if (preview.timestamp() != -1) {
        // todo Chrome date format
        return new Date(preview.timestamp()).toString();
      }
      else if (preview.kind() == Grip.Preview.Kind.ERROR) {
        return preview.name() + ": " + preview.message();
      }
    }
    return valueData.className();
  }

  @Nullable
  @Override
  public String getClassName() {
    return className;
  }

  @Nullable
  @Override
  public String getValueString() {
    return valueString;
  }

  @NotNull
  @Override
  public Promise<List<Variable>> getProperties(@NotNull List<String> names,
                                               @NotNull EvaluateContext evaluateContext,
                                               @NotNull Obsolescent obsolescent) {
    if (childrenManager.getState() != null) {
      // properties list will be loaded, so, use default implementation
      return super.getProperties(names, evaluateContext, obsolescent);
    }

    if (preloadedProperties != null) {
      return getSpecifiedProperties(preloadedProperties, names, evaluateContext);
    }

    final List<Variable> list = new SmartList<Variable>();
    List<Promise<?>> promises = new SmartList<Promise<?>>();
    for (final String name : names) {
      promises.add(childrenManager.valueManager.getVm().getCommandProcessor().send(DlvRequest.getProperty(actor, name))
                     .done(new Consumer<PropertyResult>() {
                       @Override
                       public void consume(@Nullable PropertyResult result) {
                         PropertyDescriptor descriptor = result == null ? null : result.descriptor();
                         if (descriptor != null) {
                           if (descriptor.get() != null) {
                             // ignore for now
                             return;
                           }

                           list.add(childrenManager.valueManager.createProperty(name, descriptor, null));
                         }
                       }
                     }));
    }
    return Promise.all(promises, list).done(new Consumer<List<Variable>>() {
      @Override
      public void consume(List<Variable> variables) {
        childrenManager.valueManager.promoteRecentlyAddedActorsToThreadLifetime();
      }
    });
  }
}
