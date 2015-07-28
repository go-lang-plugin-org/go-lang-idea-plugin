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

import com.goide.GoIcons;
import com.goide.dlv.protocol.Api;
import com.goide.dlv.protocol.DlvLocalsRequest;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.Consumer;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation;
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;

import javax.swing.*;
import java.util.List;

class DlvStackFrame extends XStackFrame {
  private final Api.Location myLocation;
  private final DlvCommandProcessor myProcessor;
  private final boolean myTop;

  public DlvStackFrame(Api.Location location, DlvCommandProcessor processor, boolean top) {
    myLocation = location;
    myProcessor = processor;
    myTop = top;
  }

  @Nullable
  @Override
  public XSourcePosition getSourcePosition() {
    final String url = myLocation.file;
    final VirtualFile file = LocalFileSystem.getInstance().findFileByPath(url);
    if (file == null) return null;
    return XDebuggerUtil.getInstance().createPosition(file, myLocation.line);
  }

  @Override
  public void customizePresentation(@NotNull ColoredTextContainer component) {
    super.customizePresentation(component);
    component.append(" at " + myLocation.function.name, SimpleTextAttributes.REGULAR_ATTRIBUTES);
    component.setIcon(AllIcons.Debugger.StackFrame);
  }

  @Override
  public void computeChildren(@NotNull final XCompositeNode node) {
    if (!myTop) {
      super.computeChildren(node);
      return;
    }
    final Promise<List<Api.Variable>> varPromise = myProcessor.send(new DlvLocalsRequest.DlvLocalVarsRequest());
    varPromise.processed(new Consumer<List<Api.Variable>>() {
      @Override
      public void consume(@NotNull List<Api.Variable> variables) {
        final XValueChildrenList xVars = new XValueChildrenList(variables.size());
        for (Api.Variable v : variables) {
          xVars.add(v.name, getVariableValue(v.name, v.value, v.type, GoIcons.VARIABLE));
        }

        final Promise<List<Api.Variable>> argsPromise = myProcessor.send(new DlvLocalsRequest.DlvFunctionArgsRequest());
        argsPromise.processed(new Consumer<List<Api.Variable>>() {
          @Override
          public void consume(List<Api.Variable> args) {
            for (Api.Variable v : args) {
              xVars.add(v.name, getVariableValue(v.name, v.value, v.type, GoIcons.PARAMETER));
            }
            node.addChildren(xVars, true);
          }
        });
        argsPromise.rejected(DlvDebugProcess.THROWABLE_CONSUMER);
      }
    });
    varPromise.rejected(DlvDebugProcess.THROWABLE_CONSUMER);
  }

  @NotNull
  private static XValue getVariableValue(@NotNull String name,
                                         @NotNull final String value,
                                         @Nullable final String type,
                                         @Nullable final Icon icon) {
    return new XNamedValue(name) {
      @Override
      public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
        final XValuePresentation presentation = getPresentation();
        if (presentation != null) {
          node.setPresentation(icon, presentation, false);
          return;
        }
        node.setPresentation(icon, type, value, false);
      }

      @Nullable
      private XValuePresentation getPresentation() {
        if ("struct string".equals(type)) return new XStringValuePresentation(value);
        if ("int".equals(type)) return new XNumericValuePresentation(value);
        return null;
      }
    };
  }
}
