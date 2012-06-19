package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.VarOrConstResolver;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class VarOrConstReference extends GoPsiReference<GoLiteralIdentifier> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
        psiElement(GoLiteralIdentifier.class)
            .withParent(psiElement(GoLiteralExpression.class));

    public VarOrConstReference(GoLiteralIdentifier element) {
        super(element);
    }

    @Override
    public PsiElement resolve() {
        VarOrConstResolver processor = new VarOrConstResolver(this);

        PsiScopesUtil.treeWalkUp(
            processor,
            getElement().getParent().getParent(), getElement().getContainingFile(),
            GoResolveStates.initial());

        return processor.getDeclaration();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return element.getText().equalsIgnoreCase(getElement().getText());
    }

//    private String getVisibleName(String name, ResolveState state) {
//
//        String visiblePackageName = state.get(GoResolveStates.VisiblePackageName);
//
//        if ( visiblePackageName != null ) {
//            return visiblePackageName + '.' + name;
//        }
//
//        return name;
//
//    }
//
//return false;  //To change body of implemented methods use File | Settings | File Templates.
//    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSoft() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    ////    @Override
////    public boolean isReferenceTo(PsiElement element) {
////        if (element instanceof GoTypeSpec) {
////            GoTypeSpec spec = (GoTypeSpec) element;
////
////            GoTypeNameDeclaration typeNameDecl
////                = spec.getTypeNameDeclaration();
////
////            if (typeNameDecl == null)
////                return false;
////
////            String declaredTypeName = typeNameDecl.getName();
////            String visiblePackageName = element.getUserData(
////                GoResolveStates.VisiblePackageName);
////
////            String fqm = String.format("%s%s",
////                                       visiblePackageName != null ? visiblePackageName + "." : "",
////                                       declaredTypeName);
////
////            return fqm.equals(getElement().getText());
////        }
////
////        return false;
////    }
////
//    @NotNull
//    @Override
//    public Object[] getVariants() {
//        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public boolean isSoft() {
//        return false;  //To change body of implemented methods use File | Settings | File Templates.
//    }
}
