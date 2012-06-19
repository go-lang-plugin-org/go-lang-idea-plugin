package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.TypeNameResolver;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class TypeNameReference extends GoPsiReference<GoTypeName> {
    public static final ElementPattern<GoTypeName> MATCHER =
        psiElement(GoTypeName.class);

    public TypeNameReference(GoTypeName element) {
        super(element);
    }

    @Override
    public PsiElement resolve() {
        TypeNameResolver processor =
            new TypeNameResolver(this);

        PsiScopesUtil.treeWalkUp(
            processor,
            getElement(), getElement().getContainingFile(),
            GoResolveStates.initial());

        return processor.getDeclaration();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof GoTypeSpec) {
            GoTypeSpec spec = (GoTypeSpec) element;

            GoTypeNameDeclaration typeNameDecl
                = spec.getTypeNameDeclaration();

            if (typeNameDecl == null)
                return false;

            String declaredTypeName = typeNameDecl.getName();
            String visiblePackageName = element.getUserData(
                GoResolveStates.VisiblePackageName);

            String fqm = String.format("%s%s",
                                       visiblePackageName != null ? visiblePackageName + "." : "",
                                       declaredTypeName);

            return fqm.equals(getElement().getText());
        }

        return false;
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
