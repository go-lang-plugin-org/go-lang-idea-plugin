package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;

public abstract class VisitingReferenceSolver<
        R extends ReferenceWithSolver<?, S, R>,
        S extends VisitingReferenceSolver<R, S>> extends DefaultReferenceSolver<R, S> {

    GoElementVisitorWithData<ResolveState> visitor = null;

    protected void solveWithVisitor(GoElementVisitorWithData<ResolveState> visitor) {
        this.visitor = visitor;
    }

    @Override
    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
        if ( element instanceof GoPsiElement ) {
            GoPsiElement psiElement = (GoPsiElement) element;
            visitor.setData(state);
            psiElement.accept(visitor);
            return shouldContinueSolving();
        }

        return true;
    }
}
