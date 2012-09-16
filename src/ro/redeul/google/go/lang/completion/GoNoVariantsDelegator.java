package ro.redeul.google.go.lang.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;

import static ro.redeul.google.go.lang.completion.GoCompletionContributor.BLOCK_STATEMENT;
import static ro.redeul.google.go.lang.completion.GoCompletionContributor.TYPE_DECLARATION;
import static ro.redeul.google.go.lang.completion.GoCompletionContributor.addAllPackageNames;

public class GoNoVariantsDelegator extends CompletionContributor {
    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        boolean empty = result.runRemainingContributors(parameters, true).isEmpty();
        if (!empty && parameters.getInvocationCount() == 0) {
            result.restartCompletionWhenNothingMatches();
        }

        if (empty) {
            delegate(parameters, result);
        }
    }

    private void delegate(CompletionParameters parameters, CompletionResultSet result) {
        if (parameters.getCompletionType() == CompletionType.BASIC) {
            if (packageNamePossible(parameters.getPosition())) {
                addAllPackageNames(result, parameters.getOriginalFile().getProject());
            }
        }
    }

    private boolean packageNamePossible(PsiElement position) {
        return TYPE_DECLARATION.accepts(position) ||
                BLOCK_STATEMENT.accepts(position);
    }
}
