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

import com.goide.dlv.protocol.DlvApi;
import com.goide.dlv.protocol.DlvRequest;
import com.goide.psi.GoNamedElement;
import com.goide.psi.GoTopLevelDeclaration;
import com.goide.psi.GoTypeSpec;
import com.goide.stubs.index.GoTypesIndex;
import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ThreeState;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation;
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation;
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

class DlvXValue extends XNamedValue {
  @NotNull
  private final DlvApi.Variable myVariable;
  private final Icon myIcon;
  private final DlvDebugProcess myProcess;
  private final DlvCommandProcessor myProcessor;
  private final int myFrameId;

  public DlvXValue(@NotNull DlvDebugProcess process,
                   @NotNull DlvApi.Variable variable,
                   @NotNull DlvCommandProcessor processor, 
                   int frameId, 
                   @Nullable Icon icon) {
    super(variable.name);
    myProcess = process;
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
    if (children.length == 0) {
      super.computeChildren(node);
    }
    else {
      XValueChildrenList list = new XValueChildrenList();
      for (DlvApi.Variable child : children) {
        list.add(child.name, new DlvXValue(myProcess, child, myProcessor, myFrameId, AllIcons.Nodes.Field));
      }
      node.addChildren(list, true);
    }
  }

  @Nullable
  @Override
  public XValueModifier getModifier() {
    return new XValueModifier() {
      @Override
      public void setValue(@NotNull String newValue, @NotNull XModificationCallback callback) {
        myProcessor.send(new DlvRequest.SetSymbol(myVariable.name, newValue, myFrameId))
          .processed(o -> {
            if (o != null) {
              callback.valueModified();
            }
          })
          .rejected(throwable -> callback.errorOccurred(throwable.getMessage()));
      }
    };
  }

  @NotNull
  private XValuePresentation getPresentation() {
    String value = myVariable.value;
    if (myVariable.isNumber()) return new XNumericValuePresentation(value);
    if (myVariable.isString()) return new XStringValuePresentation(value);
    if (myVariable.isBool()) {
      return new XValuePresentation() {
        @Override
        public void renderValue(@NotNull XValueTextRenderer renderer) {
          renderer.renderValue(value);
        }
      };
    }
    String type = myVariable.type;
    boolean isSlice = myVariable.isSlice();
    boolean isArray = myVariable.isArray();
    if (isSlice || isArray) {
      return new XRegularValuePresentation("len:" + myVariable.len + (isSlice ? ", cap:" + myVariable.cap : ""),
                                           type.replaceFirst("struct ", ""));
    }
    String prefix = myVariable.type + " ";
    return new XRegularValuePresentation(StringUtil.startsWith(value, prefix) ? value.replaceFirst(Pattern.quote(prefix), "") : value,
                                         type);
  }

  @Nullable
  private static PsiElement findTargetElement(@NotNull Project project,
                                              @NotNull XSourcePosition position,
                                              @NotNull Editor editor,
                                              @NotNull String name) {
    PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
    if (file == null || !file.getVirtualFile().equals(position.getFile())) return null;
    ASTNode leafElement = file.getNode().findLeafElementAt(position.getOffset());
    if (leafElement == null) return null;
    GoTopLevelDeclaration topLevel = PsiTreeUtil.getTopmostParentOfType(leafElement.getPsi(), GoTopLevelDeclaration.class);
    SyntaxTraverser<PsiElement> traverser = SyntaxTraverser.psiTraverser(topLevel)
      .filter(e -> e instanceof GoNamedElement && Comparing.equal(name, ((GoNamedElement)e).getName()));
    Iterator<PsiElement> iterator = traverser.iterator();
    return iterator.hasNext() ? iterator.next() : null;
  }

  @Override
  public void computeSourcePosition(@NotNull XNavigatable navigatable) {
    readActionInPooledThread(new Runnable() {
      @Override
      public void run() {
        navigatable.setSourcePosition(findPosition());
      }

      @Nullable
      private XSourcePosition findPosition() {
        XDebugSession debugSession = getSession();
        if (debugSession == null) return null;
        XStackFrame stackFrame = debugSession.getCurrentStackFrame();
        if (stackFrame == null) return null;
        Project project = debugSession.getProject();
        XSourcePosition position = debugSession.getCurrentPosition();
        Editor editor = ((FileEditorManagerImpl)FileEditorManager.getInstance(project)).getSelectedTextEditor(true);
        if (editor == null || position == null) return null;
        String name = myName.startsWith("&") ? myName.replaceFirst("\\&", "") : myName;
        PsiElement resolved = findTargetElement(project, position, editor, name);
        if (resolved == null) return null;
        VirtualFile virtualFile = resolved.getContainingFile().getVirtualFile();
        return XDebuggerUtil.getInstance().createPositionByOffset(virtualFile, resolved.getTextOffset());
      }
    });
  }

  private static void readActionInPooledThread(@NotNull Runnable runnable) {
    ApplicationManager.getApplication().executeOnPooledThread(() -> ApplicationManager.getApplication().runReadAction(runnable));
  }

  @Nullable
  private Project getProject() {
    XDebugSession session = getSession();
    return session != null ? session.getProject() : null;
  }

  @Nullable
  private XDebugSession getSession() {
    return myProcess.getSession();
  }

  @NotNull
  @Override
  public ThreeState computeInlineDebuggerData(@NotNull XInlineDebuggerDataCallback callback) {
    computeSourcePosition(callback::computed);
    return ThreeState.YES;
  }

  @Override
  public boolean canNavigateToSource() {
    return true; // for the future compatibility
  }

  @Override
  public boolean canNavigateToTypeSource() {
    return (myVariable.isStructure() || myVariable.isPtr()) && getProject() != null;
  }

  @Override
  public void computeTypeSourcePosition(@NotNull XNavigatable navigatable) {
    readActionInPooledThread(() -> {
      boolean isStructure = myVariable.isStructure();
      boolean isPtr = myVariable.isPtr();
      if (!isStructure && !isPtr) return;
      Project project = getProject();
      if (project == null) return;
      String dlvType = myVariable.type;
      String fqn = dlvType.replaceFirst(isPtr ? "\\*struct " : "struct ", "");
      List<String> split = StringUtil.split(fqn, ".");
      boolean noFqn = split.size() == 1;
      if (split.size() == 2 || noFqn) {
        String name = ContainerUtil.getLastItem(split);
        assert name != null;
        Collection<GoTypeSpec> types = GoTypesIndex.find(name, project, GlobalSearchScope.allScope(project), null);
        for (GoTypeSpec type : types) {
          if (noFqn || Comparing.equal(fqn, type.getQualifiedName())) {
            navigatable.setSourcePosition(XDebuggerUtil.getInstance().createPositionByOffset(
              type.getContainingFile().getVirtualFile(), type.getTextOffset()));
            return;
          }
        }
      }
    });
  }
}
