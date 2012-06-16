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

public class AutoImportHighlightingPassFactory extends AbstractProjectComponent
    implements TextEditorHighlightingPassFactory
{
    protected AutoImportHighlightingPassFactory(Project project, TextEditorHighlightingPassRegistrar hlRegistrar) {
        super(project);

        hlRegistrar.registerTextEditorHighlightingPass(this, new int[]{Pass.UPDATE_ALL}, null, false, -1);
    }

    @Override
    public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor) {
        if (editor.isOneLineMode() ||
                ApplicationManager.getApplication().isHeadlessEnvironment() ||
                !(file instanceof GoFile)) {
            return null;
        }

        return new AutoImportHighlightingPass(file.getProject(), (GoFile) file, editor);
    }
}
