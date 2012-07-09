package ro.redeul.google.go.ide.exception;

import com.intellij.execution.filters.ExceptionFilterFactory;
import com.intellij.execution.filters.Filter;
import com.intellij.psi.search.GlobalSearchScope;

public class GoExceptionFilterFactory implements ExceptionFilterFactory {
    @Override
    public Filter create(GlobalSearchScope searchScope) {
        return new GoExceptionFilter(searchScope.getProject());
    }
}
