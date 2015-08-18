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

package com.goide;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

public class GoCommenter implements CodeDocumentationAwareCommenter {
  @Nullable
  @Override
  public String getLineCommentPrefix() {
    return "//";
  }

  @Nullable
  @Override
  public String getBlockCommentPrefix() {
    return "/*";
  }

  @Nullable
  @Override
  public String getBlockCommentSuffix() {
    return "*/";
  }

  @Nullable
  @Override
  public String getCommentedBlockCommentPrefix() {
    return null;
  }

  @Nullable
  @Override
  public String getCommentedBlockCommentSuffix() {
    return null;
  }

  @Nullable
  @Override
  public IElementType getLineCommentTokenType() {
    return GoParserDefinition.LINE_COMMENT;
  }

  @Nullable
  @Override
  public IElementType getBlockCommentTokenType() {
    return GoParserDefinition.MULTILINE_COMMENT;
  }

  @Nullable
  @Override
  public IElementType getDocumentationCommentTokenType() {
    return null;
  }

  @Nullable
  @Override
  public String getDocumentationCommentPrefix() {
    return null;
  }

  @Nullable
  @Override
  public String getDocumentationCommentLinePrefix() {
    return null;
  }

  @Nullable
  @Override
  public String getDocumentationCommentSuffix() {
    return null;
  }

  @Override
  public boolean isDocumentationComment(PsiComment comment) {
    return false;
  }
}
