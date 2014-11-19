package ro.redeul.google.go.lang.psi.typing;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.RecursionGuard;
import com.intellij.openapi.util.RecursionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.resolve.refs.MethodReference;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

import java.util.HashMap;
import java.util.Map;

public class GoTypeName extends GoTypePsiBacked<GoTypeNameDeclaration> implements GoType {

    private static final Logger LOG = Logger.getInstance(GoTypeName.class);


    public GoTypeName(GoTypeNameDeclaration declaration) {
        super(declaration);
    }

    @Override
    public boolean isIdentical(GoType type) {
        return this == type || (type instanceof GoTypeName && getName().equals(((GoTypeName) type).getName()));
    }

    @Nullable
    public GoTypeSpec getDefinition() {
        return getPsiType().getTypeSpec();
    }

    private static final RecursionGuard underlyingGuardRecursionGuard =
            RecursionManager.createGuard("GoTypeName.underlyingType()");

    @NotNull
    @Override
    public GoType underlyingType() {
        underlyingGuardRecursionGuard.markStack();

        GoType underlyingType = underlyingGuardRecursionGuard.doPreventingRecursion(this, false, new Computable<GoType>() {
            @Override
            public GoType compute() {
                GoTypeSpec definition = getDefinition();
                if (definition == null)
                    return GoTypeName.this;

                return GoTypes.fromPsi(definition.getType()).underlyingType();
            }
        });

        return underlyingType != null ? underlyingType : this;
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitName(this);
    }

    public String getName() {
        return getPsiType().getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    @NotNull
    public Map<String, GoTypeFunction> getDeclaredMethods(@Nullable GoPackage goPackage) {
        Map<String, GoTypeFunction> methodMap = new HashMap<String, GoTypeFunction>();

        Object[] methods = new MethodReference(getPsiType().getContainingFile(), this).getVariants();
        for (Object method : methods) {
            if ( !(method instanceof GoMethodDeclaration))
                continue;

            GoMethodDeclaration methodDeclaration = (GoMethodDeclaration) method;
            methodMap.put(methodDeclaration.getFunctionName(), (GoTypeFunction) types().fromPsiType(methodDeclaration));
        }

        return methodMap;
    }
}
