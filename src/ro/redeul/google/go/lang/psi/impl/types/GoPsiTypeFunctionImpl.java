package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoPsiTypeFunctionImpl extends GoPsiTypeImpl implements GoPsiTypeFunction {

    public GoPsiTypeFunctionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionType(this);
    }

    @Override
    public GoFunctionParameter[] getParameters() {
        GoFunctionParameterList parameterList =
                findChildByClass(GoFunctionParameterList.class);
        if (parameterList == null) {
            return GoFunctionParameter.EMPTY_ARRAY;
        }
        return parameterList.getFunctionParameters();
    }

    @Override
    public GoFunctionParameter[] getResults() {
        PsiElement result = findChildByType(GoElementTypes.FUNCTION_RESULT);
        return GoPsiUtils.getParameters(result);
    }
}
