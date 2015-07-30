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

package com.goide.refactor;

import com.goide.psi.GoExpression;
import com.goide.psi.GoVarDefinition;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.LinkedHashSet;
import java.util.List;

public class GoIntroduceOperation {
  final private Project myProject;
  final private Editor myEditor;
  final private PsiFile myFile;
  private GoExpression myExpression;
  private List<PsiElement> myOccurrences;
  private LinkedHashSet<String> mySuggestedNames;
  private String myName;
  private GoVarDefinition myVar;
  private boolean myReplaceAll;

  public GoIntroduceOperation(Project project, Editor editor, PsiFile file) {
    myProject = project;
    myEditor = editor;
    myFile = file;
  }

  public Project getProject() {
    return myProject;
  }

  public Editor getEditor() {
    return myEditor;
  }

  public PsiFile getFile() {
    return myFile;
  }

  public GoExpression getExpression() {
    return myExpression;
  }

  public void setExpression(GoExpression expression) {
    myExpression = expression;
  }

  public List<PsiElement> getOccurrences() {
    return myOccurrences;
  }

  public void setOccurrences(List<PsiElement> occurrences) {
    myOccurrences = occurrences;
  }

  public LinkedHashSet<String> getSuggestedNames() {
    return mySuggestedNames;
  }

  public void setSuggestedNames(LinkedHashSet<String> suggestedNames) {
    mySuggestedNames = suggestedNames;
  }

  public String getName() {
    return myName;
  }

  public void setName(String name) {
    myName = name;
  }

  public GoVarDefinition getVar() {
    return myVar;
  }

  public void setVar(GoVarDefinition var) {
    myVar = var;
  }

  public boolean isReplaceAll() {
    return myReplaceAll;
  }

  public void setReplaceAll(boolean replaceAll) {
    myReplaceAll = replaceAll;
  }
}
