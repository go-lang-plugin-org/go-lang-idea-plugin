package ro.redeul.google.go.formatter;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.SharedImplUtil;
import com.intellij.util.CharTable;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * TODO: Document this
 * <p/>
 * Created on Dec-30-2013 14:51
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoPostFormatProcessor implements PostFormatProcessor {
  @Override
  public PsiElement processElement(@NotNull PsiElement source, @NotNull CodeStyleSettings settings) {
    doProcess(source, TextRange.from(source.getTextRange().getStartOffset(), source.getTextLength()), settings);
    return source;
  }

  @Override
  public TextRange processText(@NotNull PsiFile source, @NotNull TextRange rangeToReformat, @NotNull CodeStyleSettings settings) {
    return doProcess(source, rangeToReformat, settings);
  }

  @NotNull
  private static TextRange doProcess(@NotNull PsiElement source, @NotNull TextRange range, @NotNull CodeStyleSettings settings) {
    ASTNode node = source.getNode();
    if (node == null) {
      return range;
    }

    Language language = source.getLanguage();
    if (language != GoLanguage.INSTANCE) {
      // We had the only complaint for tabs not being converted to spaces for now. It was for the java code which has
      // a single block for the multi-line comment. This check should be removed if it is decided to generalize
      // this logic to other languages as well.
      return range;
    }

    return processViaPsi(node.getPsi(), range);
  }

  @NotNull
  static TextRange processViaPsi(@NotNull PsiElement node,
                                 @NotNull TextRange range) {
    while (!(node instanceof GoFile) && node != null)
      node = node.getParent();

    if (node == null)
      return range;

    PsiElement lastChild = node.getLastChild();
    if (lastChild != null) {
      final CharTable charTable = SharedImplUtil.findCharTableByTree(lastChild.getNode());
      LeafElement eofWhiteSpace =
        Factory.createSingleLeafElement(ElementType.WHITE_SPACE, "\n", charTable, lastChild.getManager());

      if ( lastChild instanceof PsiWhiteSpace )
        lastChild.replace(eofWhiteSpace.getPsi());
      else
        node.addAfter(eofWhiteSpace.getPsi(), lastChild);
    }

    return node.getTextRange();
  }
}