package ro.redeul.google.go.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.refactoring.RefactoringActionHandler;
import ro.redeul.google.go.refactoring.introduce.GoIntroduceVariableHandler;

public class GoRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public RefactoringActionHandler getIntroduceVariableHandler() {
        return new GoIntroduceVariableHandler();
    }
}
