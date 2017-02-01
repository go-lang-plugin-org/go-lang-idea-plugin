/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.goide.sdk.GoSdkService;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;

import javax.swing.*;

class DlvStackFrame extends XStackFrame {
  private final DlvDebugProcess myProcess;
  private final DlvApi.Location myLocation;
  private final DlvCommandProcessor myProcessor;
  private final int myId;

  public DlvStackFrame(@NotNull DlvDebugProcess process, 
                       @NotNull DlvApi.Location location, 
                       @NotNull DlvCommandProcessor processor, 
                       int id) {
    myProcess = process;
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
                           @NotNull XEvaluationCallback callback,
                           @Nullable XSourcePosition expressionPosition) {
        myProcessor.send(new DlvRequest.EvalSymbol(expression, myId))
          .done(variable -> callback.evaluated(createXValue(variable, AllIcons.Debugger.Watch)))
          .rejected(throwable -> callback.errorOccurred(throwable.getMessage()));
      }
      
      @Nullable
      private PsiElement findElementAt(@Nullable PsiFile file, int offset) {
        return file != null ? file.findElementAt(offset) : null;
      }

      @Nullable
      @Override
      public TextRange getExpressionRangeAtOffset(@NotNull Project project,
                                                  @NotNull Document document,
                                                  int offset,
                                                  boolean sideEffectsAllowed) {
        Ref<TextRange> currentRange = Ref.create(null);
        PsiDocumentManager.getInstance(project).commitAndRunReadAction(() -> {
          try {
            PsiElement elementAtCursor = findElementAt(PsiDocumentManager.getInstance(project).getPsiFile(document), offset);
            GoTypeOwner e = PsiTreeUtil.getParentOfType(elementAtCursor,
                                                        GoExpression.class,
                                                        GoVarDefinition.class,
                                                        GoConstDefinition.class,
                                                        GoParamDefinition.class);
            if (e != null) {
              currentRange.set(e.getTextRange());
            }
          }
          catch (IndexNotReadyException ignored) {
          }
        });
        return currentRange.get();
      }
    };
  }

  @NotNull
  private XValue createXValue(@NotNull DlvApi.Variable variable, @Nullable Icon icon) {
    return new DlvXValue(myProcess, variable, myProcessor, myId, icon);
  }

  @Nullable
  @Override
  public XSourcePosition getSourcePosition() {
    VirtualFile file = findFile();
    return file == null ? null : XDebuggerUtil.getInstance().createPosition(file, myLocation.line - 1);
  }

  @Nullable
  private VirtualFile findFile() {
    String url = myLocation.file;
    VirtualFile file = LocalFileSystem.getInstance().findFileByPath(url);
    if (file == null && SystemInfo.isWindows) {
      Project project = myProcess.getSession().getProject();
      RunProfile profile = myProcess.getSession().getRunProfile();
      Module module = profile instanceof ModuleBasedConfiguration ? ((ModuleBasedConfiguration)profile).getConfigurationModule().getModule() : null;
      String sdkHomePath = GoSdkService.getInstance(project).getSdkHomePath(module);
      if (sdkHomePath == null) return null;
      String newUrl = StringUtil.replaceIgnoreCase(url, "c:/go", sdkHomePath);
      return LocalFileSystem.getInstance().findFileByPath(newUrl);
    }
    return file;
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
  public void computeChildren(@NotNull XCompositeNode node) {
    send(new DlvRequest.ListLocalVars(myId)).done(variables -> {
      XValueChildrenList xVars = new XValueChildrenList(variables.size());
      for (DlvApi.Variable v : variables) xVars.add(v.name, createXValue(v, GoIcons.VARIABLE));
      send(new DlvRequest.ListFunctionArgs(myId)).done(args -> {
        for (DlvApi.Variable v : args) xVars.add(v.name, createXValue(v, GoIcons.PARAMETER));
        node.addChildren(xVars, true);
      });
    });
  }
}
