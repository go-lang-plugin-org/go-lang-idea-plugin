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

import com.goide.GoFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DlvLineBreakpointType extends XLineBreakpointType<DlvLineBreakpointProperties> {
  public static final String ID = "DlvLineBreakpoint";
  public static final String NAME = "Line breakpoint";

  protected DlvLineBreakpointType() {
    super(ID, NAME);
  }

  @Nullable
  @Override
  public DlvLineBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
    return new DlvLineBreakpointProperties();
  }

  @Override
  public int getPriority() {
    return 100;
  }

  @Override
  public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
    if (file.getFileType() != GoFileType.INSTANCE) return false;
    return isLineBreakpointAvailable(file, line, project);
  }

  // it should return true for lines matching "Executable Lines"
  // description at http://www.Dlv.org/doc/apps/debugger/debugger_chapter.html
  // and, ideally, it should return false otherwise
  private static boolean isLineBreakpointAvailable(@NotNull VirtualFile file, int line, @NotNull Project project) {
    Document document = FileDocumentManager.getInstance().getDocument(file);
    if (document == null) return false;
    LineBreakpointAvailabilityProcessor canPutAtChecker = new LineBreakpointAvailabilityProcessor();
    XDebuggerUtil.getInstance().iterateLine(project, document, line, canPutAtChecker);
    return canPutAtChecker.isLineBreakpointAvailable();
  }

  private static final class LineBreakpointAvailabilityProcessor implements Processor<PsiElement> {
    private boolean myIsLineBreakpointAvailable = true;

    @Override
    public boolean process(PsiElement psiElement) {
      //if (DlvPsiImplUtil.isWhitespaceOrComment(psiElement) ||
      //  psiElement.getNode().getElementType() == DlvTypes.ERL_DOT ||
      //  psiElement.getNode().getElementType() == DlvTypes.ERL_ARROW) return true;
      //@SuppressWarnings("unchecked")
      //DlvCompositeElement nonExecutableParent = PsiTreeUtil.getParentOfType(psiElement,
      //    DlvGuard.class,
      //    DlvArgumentDefinition.class,
      //    DlvAttribute.class,
      //    DlvRecordDefinition.class,
      //    DlvIncludeLib.class,
      //    DlvInclude.class,
      //    DlvMacrosDefinition.class,
      //    DlvTypeDefinition.class);
      //if (nonExecutableParent != null) return true;
      //DlvClauseBody executableParent = PsiTreeUtil.getParentOfType(psiElement, DlvClauseBody.class);
      //if (executableParent != null) {
      //  myIsLineBreakpointAvailable = true;
      //  return false;
      //}
      //return true;
      return false;
    }

    public boolean isLineBreakpointAvailable() {
      return myIsLineBreakpointAvailable;
    }
  }
}
