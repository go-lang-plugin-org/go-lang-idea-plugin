package ro.redeul.google.go.ide.structureview;

import com.intellij.ide.structureView.*;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: jhonny
 * Date: 06/07/11
 */
public class GoStructureView implements PsiStructureViewFactory {
    @Override
    public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
        return new TreeBasedStructureViewBuilder() {
            @NotNull
            public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
                return new TextEditorBasedStructureViewModel(editor, psiFile) {
                    @NotNull
                    @Override
                    public StructureViewTreeElement getRoot() {
                        return new GoStructureViewElement(getPsiFile());
                    }
                };
            }

            public boolean isRootNodeShown() {
                return false;
            }
        };
    }
}
