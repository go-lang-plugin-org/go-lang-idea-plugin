package ro.redeul.google.go.inspection;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

public class UnresolvedSymbols extends AbstractWholeGoFileInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Highlights unresolved symbols";
    }

    @Override
    protected List<ProblemDescriptor> doCheckFile(@NotNull GoFile file,
                                                  @NotNull final InspectionManager manager,
                                                  final boolean isOnTheFly) {
//        if (isOnTheFly) {
//            return null;
//        }

        final List<ProblemDescriptor> problems = new ArrayList<ProblemDescriptor>();

        new GoRecursiveElementVisitor() {
            @Override
            public void visitIdentifier(GoLiteralIdentifier identifier) {
                PsiReference reference = identifier.getReference();

                if (!identifier.isIota() && reference != null &&
                    reference.resolve() == null) {

                    problems.add(
                        manager.createProblemDescriptor(
                            identifier,
                            GoBundle.message("warning.unresolved.symbol", identifier.getName()),
                            (LocalQuickFix) null,
                            ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                            isOnTheFly
                        )
                    );
                }
            }
        }.visitElement(file);

        return problems;
    }
}
