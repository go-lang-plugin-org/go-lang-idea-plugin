package ro.redeul.google.go.highlight;

import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.ex.DefaultColorSchemesManager;
import com.intellij.openapi.editor.colors.impl.EditorColorsSchemeImpl;
import com.intellij.openapi.editor.colors.impl.ReadOnlyColorsSchemeImpl;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 3:38:01 AM
 */
public class GoEditorHighlighter extends LayeredLexerEditorHighlighter {
    public GoEditorHighlighter(EditorColorsScheme scheme, Project project, VirtualFile virtualFile) {
        super(new GoSyntaxHighlighter(), scheme);
    }

}
