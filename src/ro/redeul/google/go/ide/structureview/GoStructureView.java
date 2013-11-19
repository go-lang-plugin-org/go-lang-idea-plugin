package ro.redeul.google.go.ide.structureview;

import com.intellij.ide.structureView.*;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * User: jhonny
 * Date: 06/07/11
 */
public class GoStructureView implements PsiStructureViewFactory {
    @Override
    public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
        return new TreeBasedStructureViewBuilder() {
            @NotNull
            public StructureViewModel createStructureViewModel() {
                return new TextEditorBasedStructureViewModel(psiFile) {
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
