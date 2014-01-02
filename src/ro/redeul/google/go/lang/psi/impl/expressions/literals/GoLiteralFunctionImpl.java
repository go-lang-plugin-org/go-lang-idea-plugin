package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoLiteralFunctionImpl extends GoPsiElementBase
    implements GoLiteralFunction {

    public GoLiteralFunctionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public GoFunctionDeclaration getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return Type.Function;
    }

    @Override
    public String getFunctionName() {
        return null;
    }

    @Override
    public boolean isMain() {
        return false;
    }

    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
        throws IncorrectOperationException {
        return null;
    }

    @Override
    public GoFunctionParameter[] getParameters() {
        return GoPsiUtils.getParameters(this);
    }

    @Override
    public GoFunctionParameter[] getResults() {
        PsiElement result = findChildByType(GoElementTypes.FUNCTION_RESULT);

        return GoPsiUtils.getParameters(result);
    }

    @Override
    public GoBlockStatement getBlock() {
        return findChildByClass(GoBlockStatement.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionLiteral(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        for (GoFunctionParameter functionParameter : getParameters()) {
            if ( ! processor.execute(functionParameter, state) )  {
                return false;
            }
        }

        return processor.execute(this, state);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return GoUnderlyingTypes.getFunction();
    }

    @Override
    public boolean isIdentical(GoPsiType goType) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPackageName() {
        return "";
    }

    @Override
    public String getQualifiedName() {
        return "";
    }

    @Override
    public GoPsiType[] getReturnType() {
        List<GoPsiType> types = new ArrayList<GoPsiType>();

        GoFunctionParameter[] results = getResults();
        for (GoFunctionParameter result : results) {
            GoLiteralIdentifier identifiers[] = result.getIdentifiers();

            if (identifiers.length == 0 && result.getType() != null) {
                types.add(result.getType());
            } else {
                for (GoLiteralIdentifier identifier : identifiers) {
                    types.add(result.getType());
                }
            }
        }

        return types.toArray(new GoPsiType[types.size()]);
    }

    @Override
    public boolean isDocumentationPart(PsiElement child) {
        return false;
    }

    @Override
    public List<PsiComment> getDocumentation() {
        return Collections.emptyList();
    }
}
