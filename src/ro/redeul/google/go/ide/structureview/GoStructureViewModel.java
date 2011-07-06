package ro.redeul.google.go.ide.structureview;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * User: jhonny
 * Date: 06/07/11
 */
public class GoStructureViewModel extends TextEditorBasedStructureViewModel implements StructureViewModel {

    public GoStructureViewModel(@NotNull PsiFile psiFile) {
        super(psiFile);
    }

    @NotNull
    @Override
    public StructureViewTreeElement getRoot() {
        return new GoStructureViewElement((GoFile) this.getPsiFile(), true);
    }
}
