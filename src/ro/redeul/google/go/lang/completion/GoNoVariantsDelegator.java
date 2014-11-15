package ro.redeul.google.go.lang.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;

public class GoNoVariantsDelegator extends CompletionContributor {
    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        /*if (parameters.getCompletionType() == CompletionType.BASIC) {
            if (VALID_PACKAGE_NAME_POSITION.accepts(parameters.getPosition())) {
                addAllPackageNames(result, parameters.getOriginalFile());
            }
        }*/
    }
}
