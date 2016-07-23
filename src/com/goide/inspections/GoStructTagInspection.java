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

package com.goide.inspections;

import com.goide.psi.GoFieldDeclaration;
import com.goide.psi.GoStructType;
import com.goide.psi.GoTag;
import com.goide.psi.GoVisitor;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Implements <a href="https://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues/1983"/>, an
 * inspector that warns if a go StructTag is not well-formed according to Go language conventions.
 */
public class GoStructTagInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitStructType(@NotNull GoStructType o) {
        for (GoFieldDeclaration field : o.getFieldDeclarationList()) {
          GoTag tag = field.getTag();
          if (tag == null) continue;
          if (!isValidTag(tag)) {
            holder.registerProblem(tag, "Bad syntax for struct tag value");
          }
        }
      }
    };
  }

  // Implementation based on validateStructTag from the go vet tool:
  // https://github.com/golang/tools/blob/master/cmd/vet/structtag.go.
  private static boolean isValidTag(@NotNull GoTag tag) {
    StringBuilder tagText = new StringBuilder(GoPsiImplUtil.unquote(tag.getText()));
    if (tagText.length() != tag.getText().length() - 2) {
      // We already have a parsing error in this case, so no need to add an additional warning.
      return true;
    }
    while (tagText.length() > 0) {
      int i = 0;

      // Skip leading spaces.
      while (i < tagText.length() && tagText.charAt(i) == ' ') {
        i++;
      }

      tagText.delete(0, i);
      if (tagText.length() == 0) return true;

      // Scan to colon. A space, a quote or a control character is a syntax error.
      // Strictly speaking, control chars include the range [0x7f, 0x9f], not just
      // [0x00, 0x1f], but in practice, we ignore the multi-byte control characters
      // as it is simpler to inspect the tag's bytes than the tag's runes.
      i = 0;
      while (i < tagText.length() &&
             tagText.charAt(i) > ' ' &&
             tagText.charAt(i) != ':' &&
             tagText.charAt(i) != '"' &&
             tagText.charAt(i) != 0x7f) {
        i++;
      }
      if (i == 0 || i + 1 > tagText.length() || tagText.charAt(i) != ':') return false;
      tagText.delete(0, i + 1);

      // Scan quoted string to find value.
      i = 1;
      while (i < tagText.length() && tagText.charAt(i) != '"') {
        if (tagText.charAt(i) == '\\') {
          i++;
        }
        i++;
      }

      if (i >= tagText.length()) return false;

      String unquotedValue = GoPsiImplUtil.unquote(tagText.substring(0, i + 1));
      if (unquotedValue.length() != i - 1) {
        // The value was not correctly quoted (two quote chars were not removed by unquote).
        // TODO(sjr): this check is not equivalent to strconv.Unquote, so this inspector is
        // more lenient than the go vet tool.
        return false;
      }

      tagText.delete(0, i + 1);
    }

    return true;
  }
}
