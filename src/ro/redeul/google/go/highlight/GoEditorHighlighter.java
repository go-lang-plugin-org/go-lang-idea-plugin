package ro.redeul.google.go.highlight;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 3:38:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoEditorHighlighter extends LayeredLexerEditorHighlighter {
    public GoEditorHighlighter(EditorColorsScheme scheme, Project project, VirtualFile virtualFile) {
        super(new GoSyntaxHighlighter(), scheme);
    }

}
