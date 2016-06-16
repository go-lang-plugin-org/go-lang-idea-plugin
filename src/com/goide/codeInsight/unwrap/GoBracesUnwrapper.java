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

package com.goide.codeInsight.unwrap;

import com.goide.psi.GoStatement;
import com.goide.psi.impl.GoStatementImpl;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;

public class GoBracesUnwrapper extends GoUnwrapper {
  public GoBracesUnwrapper() {
    super("Unwrap braces");
  }

  @Override
  public boolean isApplicableTo(PsiElement e) {
    return e.getClass().equals(GoStatementImpl.class) && ((GoStatement)e).getBlock() != null;
  }

  @Override
  protected void doUnwrap(PsiElement element, Context context) throws IncorrectOperationException {
    context.extractFromBlock(((GoStatement)element).getBlock(), element);
    context.delete(element);
  }
}