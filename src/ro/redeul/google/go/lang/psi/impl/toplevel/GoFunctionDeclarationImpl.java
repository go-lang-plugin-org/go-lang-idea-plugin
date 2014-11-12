package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.processors.GoNamesUtil;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.processors.ResolveStates.Key;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.utils.GoFunctionDeclarationUtils;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.util.GoUtil;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getGlobalElementSearchScope;
import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 26, 2010
 * Time: 2:33:51 PM
 */
public class GoFunctionDeclarationImpl extends GoPsiElementBase
        implements GoFunctionDeclaration {

    @Override
    public boolean isIdentical(GoPsiType goType) {
        if (goType instanceof GoPsiTypeName) {
            goType = resolveToFinalType(goType);
        }
        if (!(goType instanceof GoPsiTypeFunction))
            return false;

        return GoUtil.CompareFnTypeToDecl((GoPsiTypeFunction) goType, this);
    }

    public GoFunctionDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public String getFunctionName() {
        GoLiteralIdentifier nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getName() : "";
    }

    @NotNull
    @Override
    public String getName() {
        return getFunctionName();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        return null;
    }

    public boolean isMain() {
        return getFunctionName().equals("main");
    }

    @Override
    public boolean isInit() {
        return getFunctionName().equals("init");
    }

    public GoBlockStatement getBlock() {
        return findChildByClass(GoBlockStatement.class);
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

    @NotNull
    @Override
    public GoType[] getParameterTypes() {
        return GoFunctionDeclarationUtils.getParameterTypes(getParameters());
    }

    @NotNull
    @Override
    public GoType[] getReturnTypes() {
        return GoFunctionDeclarationUtils.getParameterTypes(getResults());
    }

    public String toString() {
        return "FunctionDeclaration(" + getFunctionName() + ")";
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionDeclaration(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {

        if ((ResolveStates.get(state, Key.JustExports) && !GoNamesUtil.isExported(getName())))
            return true;

        if (!processor.execute(this, state))
            return false;

        // if we are not coming from a child then we should not expose the
        // parameters as declarations
        if (lastParent != getBlock())
            return true;

        for (GoFunctionParameter functionParameter : getParameters()) {
            if (!processor.execute(functionParameter, state)) {
                return false;
            }
        }

        for (GoFunctionParameter returnParameter : getResults()) {
            if (!processor.execute(returnParameter, state)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public GoLiteralIdentifier getNameIdentifier() {
        return findChildByClass(GoLiteralIdentifier.class);
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return getGlobalElementSearchScope(this, getName());
    }

    @Override
    public String getLookupTailText() {
        StringBuilder presentationText = new StringBuilder();

        presentationText.append("(");
        GoFunctionParameter[] parameters = getParameters();
        for (int i = 0; i < parameters.length; i++) {
            GoFunctionParameter parameter = parameters[i];
            presentationText.append(parameter.getLookupTailText());
            if (i < parameters.length - 1) {
                presentationText.append(",");
            }
        }

        presentationText.append(")");

        GoFunctionParameter[] results = getResults();

        if (results.length == 0)
            return presentationText.toString();

        presentationText.append(" (");
        for (int i = 0; i < results.length; i++) {
            GoFunctionParameter parameter = results[i];
            presentationText.append(parameter.getLookupTailText());
            if (i < results.length - 1) {
                presentationText.append(",");
            }
        }

        presentationText.append(")");

        return presentationText.toString();
    }

    @Override
    public String getLookupTypeText() {
        return "func";
    }

    @Override
    public LookupElementBuilder getLookupPresentation() {
        return getLookupPresentation(getNameIdentifier());
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        GoLiteralIdentifier nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier : this;
    }

    //    @Override
//    public LookupElementBuilder getLookupPresentation() {
//
//        StringBuilder presentationText = new StringBuilder();
//
//        if ( getName() != null ) {
//            presentationText.append(getName()).append("(");
//        }
//
//        for (GoFunctionParameter parameter : getParameters()) {
//            for (GoLiteralIdentifier identifier : parameter.getIdentifiers()) {
//                presentationText.append(identifier.getName()).append(", ");
//            }
//
//            presentationText.append(parameter.getType().toString()).append(", ");
//        }
//
//        return LookupElementUtil.createLookupElement(this);
//    }
}
