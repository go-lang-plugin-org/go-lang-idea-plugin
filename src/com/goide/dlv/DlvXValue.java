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

package com.goide.dlv;

import com.goide.dlv.protocol.DlvApi;
import com.goide.dlv.protocol.DlvRequest;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation;
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Set;
import java.util.regex.Pattern;

class DlvXValue extends XNamedValue {
  private final DlvApi.Variable myVariable;
  private final Icon myIcon;
  private final DlvCommandProcessor myProcessor;
  private final int myFrameId;

  public DlvXValue(DlvApi.Variable variable, Icon icon, DlvCommandProcessor processor, int frameId) {
    super(variable.name);
    myVariable = variable;
    myIcon = icon;
    myProcessor = processor;
    myFrameId = frameId;
  }

  @Override
  public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
    XValuePresentation presentation = getPresentation();
    boolean children = myVariable.children.length > 0;
    if (presentation != null) {
      node.setPresentation(myIcon, presentation, children);
      return;
    }
    String value = myVariable.value;
    String prefix = myVariable.type + " ";
    node.setPresentation(myIcon, myVariable.type,
                         StringUtil.startsWith(value, prefix) ? value.replaceFirst(Pattern.quote(prefix), "") : value, children);
  }

  @Override
  public void computeChildren(@NotNull XCompositeNode node) {
    DlvApi.Variable[] children = myVariable.children;
    if (children.length == 0) super.computeChildren(node);
    else {
      XValueChildrenList list = new XValueChildrenList();
      for (DlvApi.Variable child : children) {
        list.add(child.name, new DlvXValue(child, AllIcons.Nodes.Field, myProcessor, myFrameId));
      }
      node.addChildren(list, true);
    }
  }

  @Nullable
  @Override
  public XValueModifier getModifier() {
    return new XValueModifier() {
      @Override
      public void setValue(@NotNull String newValue, @NotNull final XModificationCallback callback) {
        myProcessor.send(new DlvRequest.SetSymbol(myVariable.name, newValue, myFrameId))
          .processed(new Consumer<Object>() {
          @Override
          public void consume(Object o) {
            if (o != null) {
              callback.valueModified();
            }
          }
        })
          .rejected(new Consumer<Throwable>() {
            @Override
            public void consume(Throwable throwable) {
              callback.errorOccurred(throwable.getMessage());
            }
          });
      }
    };
  }

  @Nullable
  private XValuePresentation getPresentation() {
    String type = myVariable.type;
    final String value = myVariable.value;
    if (NUMBERS.contains(type)) return new XNumericValuePresentation(value);
    if ("struct string".equals(type)) return new XStringValuePresentation(value);
    if ("bool".equals(type)) return new XValuePresentation() {
        @Override
        public void renderValue(@NotNull XValueTextRenderer renderer) {
          renderer.renderValue(value);
        }
      };
    return null;
  }

  private static final Set<String> NUMBERS = ContainerUtil.newTroveSet(
    "int8",
    "uint8",
    "uint8",
    "int16",
    "uint16",
    "int32",
    "uint32",
    "int32",
    "float32",
    "float64",
    "int32",
    "int64",
    "uint64",
    "complex64",
    "complex128",
    "int",
    "uint",
    "uintptr",
    "byte",
    "rune"
  );

}
