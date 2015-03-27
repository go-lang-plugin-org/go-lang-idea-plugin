/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.debugger.ideagdb.debug;

import com.goide.GoIcons;
import com.goide.debugger.gdb.Gdb;
import com.goide.debugger.gdb.gdbmi.GdbMiUtil;
import com.goide.debugger.gdb.messages.GdbErrorEvent;
import com.goide.debugger.gdb.messages.GdbEvent;
import com.goide.debugger.gdb.messages.GdbVariableObject;
import com.goide.debugger.gdb.messages.GdbVariableObjects;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.xdebugger.frame.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GdbValue extends XValue {
  private static final Icon VAR_ICON = GoIcons.VARIABLE; // todo: detect GoIcons.CONSTANT?
  private static final Logger LOG = Logger.getInstance(GdbValue.class);

  private final Gdb myGdb;
  private final GdbVariableObject myVariableObject;

  public GdbValue(@NotNull Gdb gdb, @NotNull GdbVariableObject o) {
    myGdb = gdb;
    myVariableObject = o;
  }

  @Override
  public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
    String goType = getGoObjectType(myVariableObject.type);
    Boolean hasChildren = myVariableObject.numChildren != null && myVariableObject.numChildren > 0;

    if (goType.equals("string")) {
      handleGoString(node);
      return;
    }

    node.setPresentation(VAR_ICON, goType, notNull(myVariableObject.value), hasChildren);
  }

  public static String getGoObjectType(String originalType) {
    if (originalType.contains("struct string")) {
      return originalType.replace("struct ", "");
    }

    return originalType;
  }

  @Nullable
  @Override
  public XValueModifier getModifier() {
    // TODO: Return null if we don't support editing
    return new GdbValueModifier(myGdb, myVariableObject);
  }

  @Override
  public void computeChildren(@NotNull final XCompositeNode node) {
    if (myVariableObject.numChildren == null || myVariableObject.numChildren <= 0) {
      node.addChildren(XValueChildrenList.EMPTY, true);
    }

    // Get the children from GDB
    myGdb.sendCommand("-var-list-children --all-values " +
                      GdbMiUtil.formatGdbString(myVariableObject.name), new Gdb.GdbEventCallback() {
      @Override
      public void onGdbCommandCompleted(GdbEvent event) {
        onGdbChildrenReady(event, node);
      }
    });
  }

  private void onGdbChildrenReady(GdbEvent event, XCompositeNode node) {
    if (event instanceof GdbErrorEvent) {
      node.setErrorMessage(((GdbErrorEvent) event).message);
      return;
    }
    if (!(event instanceof GdbVariableObjects)) {
      node.setErrorMessage("Unexpected data received from GDB");
      LOG.warn("Unexpected event " + event + " received from -var-list-children request");
      return;
    }

    // Inspect the data
    GdbVariableObjects variables = (GdbVariableObjects) event;
    if (variables.objects == null || variables.objects.isEmpty()) {
      // No data
      node.addChildren(XValueChildrenList.EMPTY, true);
    }

    // Build a XValueChildrenList
    XValueChildrenList children = new XValueChildrenList(variables.objects.size());
    for (GdbVariableObject variable : variables.objects) {
      children.add(variable.expression, new GdbValue(myGdb, variable));
    }
    node.addChildren(children, true);
  }

  private void handleGoString(@NotNull final XValueNode node) {
    myGdb.sendCommand("-var-list-children --all-values " + GdbMiUtil.formatGdbString(myVariableObject.name), new Gdb.GdbEventCallback() {
      @Override
      public void onGdbCommandCompleted(GdbEvent event) {
        onGoGdbStringReady(node, event);
      }
    });
  }

  private void onGoGdbStringReady(@NotNull XValueNode node, @NotNull GdbEvent event) {
    Pair<String, Boolean> pair = strValue(event);
    String value = pair.first;
    Boolean isString = pair.second;
    if (isString) {
      if (value.equals("0x0")) value = "\"\"";
      node.setPresentation(VAR_ICON, "string (" + value.length() + ")", value, false);
    }
    else {
      Boolean hasChildren = myVariableObject.numChildren != null && myVariableObject.numChildren > 0;
      node.setPresentation(VAR_ICON, "unknown", notNull(myVariableObject.value), hasChildren);
    }
  }

  private static String notNull(@Nullable String value) {
    return StringUtil.notNullize(value, "<null>");
  }

  @NotNull
  private Pair<String, Boolean> strValue(@NotNull GdbEvent event) {
    String value = "Unexpected data received from GDB";
    if (event instanceof GdbErrorEvent) {
      return Pair.create(((GdbErrorEvent)event).message, false);
    }
    else if (!(event instanceof GdbVariableObjects)) {
      LOG.warn("Unexpected event " + event + " received from -var-list-children request");
      return Pair.create(value, false);
    }
    else {
      // Inspect the data
      GdbVariableObjects variables = (GdbVariableObjects)event;
      if (variables.objects != null && !variables.objects.isEmpty()) {
        String stringSubVar = GdbMiUtil.formatGdbString(myVariableObject.name, false) + ".str";

        for (GdbVariableObject variable : variables.objects) {
          if (variable.name.equals(stringSubVar)) {
            return Pair.create(variable.value, true);
          }
        }
      }
    }
    return Pair.create(value, false);
  }
}
