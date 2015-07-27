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

import com.goide.dlv.rdp.Bindings;
import com.goide.dlv.rdp.Frame;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.CallFrameBase;
import org.jetbrains.debugger.ObjectScope;
import org.jetbrains.debugger.Scope;
import org.jetbrains.debugger.Variable;
import org.jetbrains.debugger.values.ObjectValue;
import org.jetbrains.rpc.CommandProcessor;

import java.util.List;

public class DlvCallFrame extends CallFrameBase {
  @Nullable final String scriptUrl;
  @Nullable final String scriptId;

  @Nullable private final Variable thisObject;

  public DlvCallFrame(@NotNull final DlvValueManager valueManager,
                      @NotNull final Frame frame,
                      final boolean useBindingsFromFrameData) {
    super(getFunctionName(frame.environment().function()), frame.where().line() - 1, frame.where().column(),
          new DlvEvaluateContext(frame.actor(), valueManager));

    if (frame.source() == null) {
      scriptId = null;
      scriptUrl = frame.where().url();
    }
    else {
      //noinspection ConstantConditions
      scriptId = frame.source().actor();
      scriptUrl = null;
    }

    if (frame.receiver() == null) {
      thisObject = null;
    }
    else {
      thisObject = valueManager.createVariable(RECEIVER_NAME, frame.receiver(), null);
    }

    final Frame.Environment environment = frame.environment();
    hasOnlyGlobalScope = environment.parent() == null;
    scopes = new NotNullLazyValue<List<Scope>>() {
      @NotNull
      @Override
      protected List<Scope> compute() {
        List<Scope> scopes = new SmartList<Scope>();
        scopes.add(createScope(environment, valueManager, true, useBindingsFromFrameData));

        Frame.Environment parent = environment;
        while ((parent = parent.parent()) != null) {
          scopes.add(createScope(parent, valueManager, false, useBindingsFromFrameData));
        }
        valueManager.promoteRecentlyAddedActorsToThreadLifetime();
        return scopes;
      }
    };
  }

  @NotNull
  private static Scope createScope(@NotNull Frame.Environment environment,
                                   @NotNull DlvValueManager valueManager,
                                   boolean local,
                                   boolean useBindingsFromFrameData) {
    Scope.Type type;
    switch (environment.type()) {
      case OBJECT:
        type = environment.parent() == null ? Scope.Type.GLOBAL : Scope.Type.WITH;
        break;
      case WITH:
        type = Scope.Type.WITH;
        break;
      default:
        type = local ? Scope.Type.LOCAL : Scope.Type.CLOSURE;
        break;
    }

    Bindings bindings = environment.bindings();
    if (bindings == null) {
      ObjectValue value = (ObjectValue)valueManager.createValue(environment.object(), null, null);
      CommandProcessor.LOG.assertTrue(value != null);
      return new ObjectScope(type, value);
    }
    else {
      return new DlvDeclarativeScope(type, environment, valueManager, useBindingsFromFrameData);
    }
  }

  @Nullable
  static String getFunctionName(@Nullable Frame.Function function) {
    if (function == null) {
      return null;
    }
    else if (StringUtil.isEmpty(function.userDisplayName())) {
      return StringUtil.isEmpty(function.displayName()) ? StringUtil.nullize(function.name()) : function.displayName();
    }
    else {
      return function.userDisplayName();
    }
  }

  @NotNull
  @Override
  public Promise<Variable> getReceiverVariable() {
    return Promise.resolve(thisObject);
  }

  @Nullable
  @Override
  public Object getEqualityObject() {
    return ObjectUtils.chooseNotNull(scriptId, scriptUrl) + "#" + getFunctionName();
  }
}