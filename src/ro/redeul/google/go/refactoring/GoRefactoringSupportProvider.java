package ro.redeul.google.go.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.refactoring.introduce.GoIntroduceConstantHandler;
import ro.redeul.google.go.refactoring.introduce.GoIntroduceVariableHandler;

public class GoRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public RefactoringActionHandler getIntroduceVariableHandler() {
        return new GoIntroduceVariableHandler();
    }

    @Override
    public RefactoringActionHandler getIntroduceConstantHandler() {
        return new GoIntroduceConstantHandler();
    }

    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof GoPsiElementBase && element.getUseScope() instanceof LocalSearchScope;
    }
}
