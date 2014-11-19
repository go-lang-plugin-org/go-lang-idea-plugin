package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
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

    private final GoTypeName type;
    private final PsiFile srcFile;
    private final GoPackage srcPackage;
    private Set<GoTypeName> baseTypes;

    public MethodReference(@NotNull GoLiteralIdentifier element, @NotNull GoTypeName type) {
        this(element, type, element.getContainingFile());
    }
    public MethodReference(PsiFile srcFile, GoTypeName type) {
        this(null, type, srcFile);
    }

    private MethodReference(@Nullable GoLiteralIdentifier identifier, GoTypeName type, PsiFile srcFile) {
        super(identifier);
        this.type = type;
        this.srcFile = srcFile;
        this.srcPackage = GoPackages.getPackageFor(srcFile);
    }
    @Override
    protected MethodReference self() {
        return this;
    }

    @Override
    public MethodSolver newSolver() {
        return new MethodSolver(this);
    }

    @Override
    public void walkSolver(MethodSolver solver) {
        Set<GoTypeName> baseTypes = findEmbeddedTypes(type);

        for (GoTypeName typeName : baseTypes) {

            GoPackage goPackage = GoPackages.getTargetPackageIfDifferent(getElement(), typeName.getDefinition());

            if (goPackage != null) {
                GoPsiScopesUtil.walkPackageExports(solver, srcFile.getLastChild(), goPackage);
            } else {
                GoPsiScopesUtil.walkPackage(solver, ResolveState.initial(), srcFile.getLastChild(), srcPackage);
            }
        }
    }

    public GoTypeName getTypeName() {
        return type;
    }

    public Set<GoTypeName> findEmbeddedTypes(GoTypeName type) {
        if (baseTypes == null) {
            baseTypes = new HashSet<GoTypeName>();

            Queue<GoTypeName> typeNamesToExplore = new LinkedList<GoTypeName>();
            typeNamesToExplore.offer(type);

            while ( ! typeNamesToExplore.isEmpty() ) {
                GoTypeName currentTypeName = typeNamesToExplore.poll();

                baseTypes.add(currentTypeName);

                GoType underlyingType = currentTypeName.underlyingType();
                if ( !(underlyingType instanceof GoTypeStruct) )
                    continue;

                GoTypeStruct typeStruct = (GoTypeStruct) underlyingType;
                for (GoTypeStructAnonymousField field : typeStruct.getPsiType().getAnonymousFields()) {
                    GoPsiType psiType = field.getType();
                    if ( psiType == null)
                        continue;
                    if ( psiType instanceof GoPsiTypePointer) {
                        psiType = ((GoPsiTypePointer) psiType).getTargetType();
                    }

                    GoType embeddedType = GoTypes.fromPsi(psiType);
                    if (!(embeddedType instanceof GoTypeName))
                        continue;

                    GoTypeName embeddedTypeName = (GoTypeName) embeddedType;
                    if (! baseTypes.contains(embeddedTypeName) )
                        typeNamesToExplore.offer(embeddedTypeName);

                    baseTypes.add(embeddedTypeName);
                }
            }
        }

        return baseTypes;
    }
}
