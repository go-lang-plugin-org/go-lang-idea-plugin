package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.resolve.MethodOrTypeNameResolver;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.util.LookupElementUtil.createLookupElement;

public class BuiltinCallOrConversionReference extends AbstractCallOrConversionReference<BuiltinCallOrConversionReference> {

    public static final ElementPattern<GoLiteralExpression> MATCHER =
                psiElement(GoLiteralExpression.class)
                    .withParent(psiElement(GoBuiltinCallExpression.class))
                    .atStartOf(psiElement(GoBuiltinCallExpression.class));

    public BuiltinCallOrConversionReference(GoLiteralExpression identifier) {
        super(identifier, RESOLVER);
    }

    @Override
    protected BuiltinCallOrConversionReference self() {
        return this;
    }

    private static final ResolveCache.AbstractResolver<BuiltinCallOrConversionReference, GoResolveResult> RESOLVER =
        new ResolveCache.AbstractResolver<BuiltinCallOrConversionReference, GoResolveResult>() {
            @Override
            public GoResolveResult resolve(@NotNull BuiltinCallOrConversionReference psiReference, boolean incompleteCode) {
                PsiElement element = psiReference.getElement();

                MethodOrTypeNameResolver processor =
                    new MethodOrTypeNameResolver(psiReference);

                GoNamesCache namesCache = GoNamesCache.getInstance(element.getProject());

                // get the file included in the imported package name
                Collection<GoFile> files = namesCache.getBuiltinPackageFiles();

                for (GoFile file : files) {
                    ResolveState newState = GoResolveStates.imported("builtin", "");
                    if (!file.processDeclarations(processor, newState, null, element))  {
                        break;
                    }
                }

                return GoResolveResult.fromElement(processor.getChildDeclaration());
            }

        };

//    @Override
//    public PsiElement resolve() {
//        return ResolveCache.getInstance(getElement().getProject())
//                    .resolveWithCaching(this, RESOLVER, false, false);
//    }


    @NotNull
    @Override
    public Object[] getVariants() {

        PsiElement element = getElement();

        final List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();

        MethodOrTypeNameResolver processor = new MethodOrTypeNameResolver(this) {
            @Override
            protected boolean addDeclaration(PsiElement declaration, PsiElement child) {

                String name = PsiUtilCore.getName(child);

                GoPsiElement goPsi = (GoPsiElement) declaration;
                GoPsiElement goChildPsi = (GoPsiElement) child;
                variants.add(createLookupElement(goPsi, name, goChildPsi).withTypeText("builtin", true));
                return true;
            }
        };

        GoNamesCache namesCache = GoNamesCache.getInstance(element.getProject());

        // get the file included in the imported package name
        Collection<GoFile> files = namesCache.getBuiltinPackageFiles();

        for (GoFile file : files) {
            ResolveState newState = GoResolveStates.imported("builtin", "");
            if (!file.processDeclarations(processor, newState, null, element))  {
                break;
            }
        }

        return variants.toArray(new LookupElementBuilder[variants.size()]);
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
