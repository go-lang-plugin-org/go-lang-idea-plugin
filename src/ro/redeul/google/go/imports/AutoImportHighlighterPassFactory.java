package ro.redeul.google.go.imports;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;

public class AutoImportHighlighterPassFactory extends AbstractProjectComponent implements TextEditorHighlightingPassFactory {
    protected AutoImportHighlighterPassFactory(Project project, TextEditorHighlightingPassRegistrar hlRegistrar) {
        super(project);

        hlRegistrar.registerTextEditorHighlightingPass(this, null, new int[]{Pass.LINE_MARKERS}, false, -1);
    }

    @Override
    public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor) {
        if (editor.isOneLineMode() ||
                ApplicationManager.getApplication().isHeadlessEnvironment() ||
                !(file instanceof GoFile)) {
            return null;
        }

        return new AutoImportHighlighterPass(file.getProject(), (GoFile) file, editor);
    }
}
