package ro.redeul.google.go.highlight;

import com.intellij.lexer.StringLiteralLexer;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 3:38:01 AM
 */
public class GoEditorHighlighter extends LayeredLexerEditorHighlighter {
  public GoEditorHighlighter(EditorColorsScheme scheme) {
    super(new GoSyntaxHighlighter(), scheme);
  }

}
