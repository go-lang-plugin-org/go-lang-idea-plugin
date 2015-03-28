package yaml;

import com.intellij.appengine.AppEngineExtension;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ALL")
public class AppEngineYamlCompletionContributor extends CompletionContributor {
  public AppEngineYamlCompletionContributor() {
    //extend(CompletionType.BASIC, key(), new YamlCompletionProvider());
    //extend(CompletionType.BASIC, inSequence(), new YamlCompletionProvider());
  }

  private static class YamlCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters completionParameters,
                                  ProcessingContext processingContext,
                                  @NotNull CompletionResultSet result) {
      PsiElement position = completionParameters.getPosition();
      if (!AppEngineExtension.isAppEngineContext(position)) {
        return;
      }
    }
  }
}
