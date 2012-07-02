package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.Collection;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.MethodOrTypeNameResolver;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class BuiltinCallOrConversionReference extends CallOrConversionReference {

    public static ElementPattern<GoLiteralIdentifier> MATCHER =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(psiElement(GoBuiltinCallExpression.class))
                    .atStartOf(psiElement(GoBuiltinCallExpression.class)));

    public BuiltinCallOrConversionReference(GoLiteralIdentifier identifier) {
        super(identifier);
    }

    @Override
    public PsiElement resolve() {

        PsiElement element = getElement();
        if (element == null)
            return null;

        MethodOrTypeNameResolver processor =
            new MethodOrTypeNameResolver(this);

        GoNamesCache namesCache = GoNamesCache.getInstance(element.getProject());

        // get the file included in the imported package name
        Collection<GoFile> files = namesCache.getBuiltinPackageFiles();


        for (GoFile file : files) {
            ResolveState newState = GoResolveStates.imported("builtin", "");

            if (!file.processDeclarations(processor, newState, null, element))  {
                break;
            }
        }

        return processor.getDeclaration();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
