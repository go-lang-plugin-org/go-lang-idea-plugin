package com.redeul.google.go.spellchecker.tokenizer;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.spellchecker.inspections.IdentifierSplitter;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

import java.util.ArrayList;
import java.util.List;

public class GoIdentifierTokenizerStrategy extends SpellcheckingStrategy {

  static class GoIdentifierSplitter extends IdentifierSplitter {
        private static final GoIdentifierSplitter INSTANCE =
            new GoIdentifierSplitter();

        public static GoIdentifierSplitter getInstance() {
            return INSTANCE;
        }

        @Override
        public void split(@Nullable String text, @NotNull TextRange range,
                          Consumer<TextRange> consumer) {
            if (text == null || range.getLength() < 1 || range.getStartOffset() < 0) {
                return;
            }

            List<TextRange> ranges = splitByDot(text, range);

            for (TextRange textRange : ranges) {
                super.split(text, textRange, consumer);
            }
        }

      private List<TextRange> splitByDot(String text, TextRange range) {
          List<TextRange> result = new ArrayList<TextRange>();
          int i = range.getStartOffset();
          int s = i;
          while (i < range.getEndOffset())  {
              final char ch = text.charAt(i);
              if (ch == '.') {
                  if (i != s ) {
                    result.add(new TextRange(s, i));
                  }
                  s = i + 1;
              }
              i++;
          }

          if ( i != s ) {
              result.add(new TextRange(s, i));
          }

          return result;
      }
  }

    private static final Tokenizer<GoLiteralIdentifier> GO_IDENTIFIER_TOKENIZER
        = new Tokenizer<GoLiteralIdentifier>() {
        @Override
        public void tokenize(@NotNull GoLiteralIdentifier element,
                             TokenConsumer consumer) {

            PsiElement identifier = element.getNameIdentifier();
            if (identifier == null) {
                return;
            }
            PsiElement parent = element;
            final TextRange range = identifier.getTextRange();
            if (range.isEmpty()) return;

            int offset = range.getStartOffset() - parent.getTextRange().getStartOffset();
            if(offset < 0 ) {
                parent = PsiTreeUtil.findCommonParent(identifier, element);
                if (parent == null) {
                    return;
                }

                offset = range.getStartOffset() - parent.getTextRange().getStartOffset();
            }
            String text = identifier.getText();
            consumer.consumeToken(parent, text, true, offset, TextRange.allOf(text),
                                  GoIdentifierSplitter.getInstance());
        }
    };

    @NotNull
    @Override
    public Tokenizer getTokenizer(PsiElement element) {
        if ( element instanceof GoLiteralIdentifier ) {
            return GO_IDENTIFIER_TOKENIZER;
        }

        return super.getTokenizer(element);
    }
}
