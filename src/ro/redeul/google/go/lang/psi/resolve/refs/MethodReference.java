package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class MethodReference extends ReferenceWithSolver<GoLiteralIdentifier, MethodSolver, MethodReference> {

    private Set<GoTypeName> receiverTypes;
    private GoTypeName type;

    public MethodReference(GoLiteralIdentifier element, @NotNull GoTypeName type) {
        super(element);
        this.type = type;
    }

    @Override
    protected MethodReference self() {
        return this;
    }

    @Override
    public MethodSolver newSolver() {
        return new MethodSolver(self());
    }

    @Override
    public void walkSolver(MethodSolver solver) {
        PsiElement toFindPackagePsi;
        if (type.getDefinition() instanceof GoTypeName){
            toFindPackagePsi = ((GoTypeName) type.getDefinition()).getPsiType();
        }else{
            toFindPackagePsi = type.getPsiType();
        }
        GoPackage goPackage = GoPackages.getTargetPackageIfDifferent(getElement(), toFindPackagePsi);

        if ( goPackage != null) {
            GoPsiScopesUtil.walkPackage(solver, getElement(), goPackage);
        } else {
            GoPsiScopesUtil.treeWalkUp(
                    solver,
                    getElement().getContainingFile().getLastChild(),
                    getElement().getContainingFile(),
                    ResolveStates.initial());
        }
    }

    @NotNull
    public Set<GoTypeName> resolveBaseReceiverTypes() {
        if ( receiverTypes != null )
            return receiverTypes;

        receiverTypes = new HashSet<GoTypeName>();

        Queue<GoTypeName> typeNamesToExplore = new LinkedList<GoTypeName>();
        typeNamesToExplore.offer(type);

        while ( ! typeNamesToExplore.isEmpty() ) {
            GoTypeName currentTypeName = typeNamesToExplore.poll();

            receiverTypes.add(currentTypeName);

            if ( !(currentTypeName.getDefinition() instanceof GoTypeStruct) )
                continue;

            GoTypeStruct typeStruct = (GoTypeStruct) currentTypeName.getDefinition();
            for (GoTypeStructAnonymousField field : typeStruct.getPsiType().getAnonymousFields()) {
                GoPsiType psiType = field.getType();
                if ( psiType == null)
                    continue;
                if ( psiType instanceof GoPsiTypePointer) {
                    psiType = ((GoPsiTypePointer) psiType).getTargetType();
                }

                GoType embeddedType = GoTypes.fromPsiType(psiType);
                if (embeddedType == null || !(embeddedType instanceof GoTypeName))
                    continue;

                GoTypeName embeddedTypeName = (GoTypeName) embeddedType;
                if (! receiverTypes.contains(embeddedTypeName) )
                    typeNamesToExplore.offer(embeddedTypeName);

                receiverTypes.add(embeddedTypeName);
            }
        }

        return receiverTypes;
    }

}
