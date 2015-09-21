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

import com.goide.GoIcons;
import com.goide.dlv.protocol.DlvApi;
import com.goide.dlv.protocol.DlvRequest;
import com.goide.psi.*;
import com.intellij.debugger.impl.DebuggerUtilsEx;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation;
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;

import javax.swing.*;
import java.util.List;
import java.util.Set;

class DlvStackFrame extends XStackFrame {
  private final DlvApi.Location myLocation;
  private final DlvCommandProcessor myProcessor;
  private final int myId;
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

  public DlvStackFrame(DlvApi.Location location, DlvCommandProcessor processor, int id) {
    myLocation = location;
    myProcessor = processor;
    myId = id;
  }

  @Nullable
  @Override
  public XDebuggerEvaluator getEvaluator() {
    return new XDebuggerEvaluator() {
      @Override
      public void evaluate(@NotNull String expression,
                           @NotNull final XEvaluationCallback callback,
                           @Nullable XSourcePosition expressionPosition) {
        myProcessor.send(new DlvRequest.EvalSymbol(expression, myId))
          .done(new Consumer<DlvApi.Variable>() {
            @Override
            public void consume(@NotNull DlvApi.Variable variable) {
              callback.evaluated(createXValue(variable, AllIcons.Debugger.Watch));
            }
          })
          .rejected(new Consumer<Throwable>() {
            @Override
            public void consume(@NotNull Throwable throwable) {
              callback.errorOccurred(throwable.getMessage());
            }
          });
      }

      @Nullable
      @Override
      public TextRange getExpressionRangeAtOffset(@NotNull final Project project,
                                                  @NotNull final Document document,
                                                  final int offset,
                                                  boolean sideEffectsAllowed) {
        final Ref<TextRange> currentRange = Ref.create(null);
        PsiDocumentManager.getInstance(project).commitAndRunReadAction(new Runnable() {
          @Override
          public void run() {
            try {
              PsiElement elementAtCursor = DebuggerUtilsEx.findElementAt(PsiDocumentManager.getInstance(project).getPsiFile(document), offset);
              GoTypeOwner e = PsiTreeUtil.getParentOfType(elementAtCursor,
                                                          GoReferenceExpression.class,
                                                          GoVarDefinition.class,
                                                          GoConstDefinition.class,
                                                          GoParamDefinition.class);
              if (e instanceof GoReferenceExpression && ((GoReferenceExpression)e).getQualifier() == null ||
                  e != null && !(e instanceof GoReferenceExpression)) {
                currentRange.set(e.getTextRange());
              }
            }
            catch (IndexNotReadyException ignored) {
            }
          }
        });
        return currentRange.get();
      }
    };
  }

  @NotNull
  private static XValue createXValue(@NotNull final DlvApi.Variable variable, @Nullable final Icon icon) {
    return new XNamedValue(variable.name) {
      @Override
      public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
        XValuePresentation presentation = getPresentation();
        if (presentation != null) {
          node.setPresentation(icon, presentation, false);
          return;
        }
        node.setPresentation(icon, variable.type, variable.value, false);
      }

      @Nullable
      private XValuePresentation getPresentation() {
        String type = variable.type;
        final String value = variable.value;
        if (NUMBERS.contains(type)) return new XNumericValuePresentation(value);
        if ("struct string".equals(type)) return new XStringValuePresentation(value);
        if ("bool".equals(type)) {
          return new XValuePresentation() {
            @Override
            public void renderValue(@NotNull XValueTextRenderer renderer) {
              renderer.renderValue(value);
            }
          };
        }
        return null;
      }
    };
  }

  @Nullable
  @Override
  public XSourcePosition getSourcePosition() {
    String url = myLocation.file;
    VirtualFile file = LocalFileSystem.getInstance().findFileByPath(url);
    if (file == null) return null;
    return XDebuggerUtil.getInstance().createPosition(file, myLocation.line - 1);
  }

  @Override
  public void customizePresentation(@NotNull ColoredTextContainer component) {
    super.customizePresentation(component);
    component.append(" at " + myLocation.function.name, SimpleTextAttributes.REGULAR_ATTRIBUTES);
    component.setIcon(AllIcons.Debugger.StackFrame);
  }

  @NotNull
  private <T> Promise<T> send(@NotNull DlvRequest<T> request) {
    return DlvDebugProcess.send(request, myProcessor);
  }

  @Override
  public void computeChildren(@NotNull final XCompositeNode node) {
    send(new DlvRequest.ListLocalVars(myId)).done(new Consumer<List<DlvApi.Variable>>() {
      @Override
      public void consume(@NotNull List<DlvApi.Variable> variables) {
        final XValueChildrenList xVars = new XValueChildrenList(variables.size());
        for (DlvApi.Variable v : variables) xVars.add(v.name, createXValue(v, GoIcons.VARIABLE));
        send(new DlvRequest.ListFunctionArgs(myId)).done(new Consumer<List<DlvApi.Variable>>() {
          @Override
          public void consume(@NotNull List<DlvApi.Variable> args) {
            for (DlvApi.Variable v : args) xVars.add(v.name, createXValue(v, GoIcons.PARAMETER));
            node.addChildren(xVars, true);
          }
        });
      }
    });
  }
}
