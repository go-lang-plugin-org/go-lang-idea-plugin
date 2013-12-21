package ro.redeul.google.go.ide.exception;

import com.intellij.execution.filters.ExceptionFilterFactory;
import com.intellij.execution.filters.Filter;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

public class GoExceptionFilterFactory implements ExceptionFilterFactory {
    @Override
    @NotNull
    public Filter create(@NotNull GlobalSearchScope searchScope) {
        return new GoExceptionFilter(searchScope.getProject());
    }
}
