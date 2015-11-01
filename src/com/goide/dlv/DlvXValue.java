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
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation;
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation;
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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
    boolean hasChildren = myVariable.children.length > 0;
    node.setPresentation(myIcon, presentation, hasChildren);
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

  @NotNull
  private XValuePresentation getPresentation() {
    final String value = myVariable.value;
    if (myVariable.isNumber()) return new XNumericValuePresentation(value);
    if (myVariable.isString()) return new XStringValuePresentation(value);
    if (myVariable.isBool()) return new XValuePresentation() {
        @Override
        public void renderValue(@NotNull XValueTextRenderer renderer) {
          renderer.renderValue(value);
        }
      };
    String type = myVariable.type;
    String prefix = myVariable.type + " ";
    return new XRegularValuePresentation(StringUtil.startsWith(value, prefix) ? value.replaceFirst(Pattern.quote(prefix), "") : value, type);
  }
}
