package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.processors.GoNamesUtil;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getGlobalElementSearchScope;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 26, 2010
 * Time: 2:33:51 PM
 */
public class GoFunctionDeclarationImpl extends GoPsiElementBase
    implements GoFunctionDeclaration {

    public GoFunctionDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String getFunctionName() {
        PsiElement identifier = findChildByType(GoTokenTypes.mIDENT);

        return identifier != null ? identifier.getText() : "";
    }

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

    public GoBlockStatement getBlock() {
        return findChildByClass(GoBlockStatement.class);
    }

    @Override
    public GoFunctionParameter[] getParameters() {
        GoFunctionParameterList parameterList =
            findChildByClass(GoFunctionParameterList.class);

        if ( parameterList == null ) {
            return GoFunctionParameter.EMPTY_ARRAY;
        }

        return parameterList.getFunctionParameters();
    }

    @Override
    public GoFunctionParameter[] getResults() {
        PsiElement result = findChildByType(GoElementTypes.FUNCTION_RESULT);

        return GoPsiUtils.getParameters(result);
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

        if (! "builtin".equals(state.get(GoResolveStates.PackageName)) &&
            ! state.get(GoResolveStates.IsOriginalPackage) &&
            ! GoNamesUtil.isExportedName(getName()))
            return true;

        if (!processor.execute(this, state))
            return false;

        // if we are not coming from a child then we should not expose the
        // parameters as declarations
        if (lastParent == null)
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
    public PsiElement getNameIdentifier() {
        return findChildByType(GoTokenTypes.mIDENT);
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return getGlobalElementSearchScope(this, getName());
    }

    @NotNull
    @Override
    public String getPresentationText() {
        return getName();
    }

    @Override
    public String getPresentationTailText() {
        StringBuilder presentationText = new StringBuilder();

        presentationText.append("(");
        GoFunctionParameter[] parameters = getParameters();
        for (int i = 0; i < parameters.length; i++) {
            GoFunctionParameter parameter = parameters[i];
            presentationText.append(parameter.getPresentationTailText());
            if ( i < parameters.length - 1) {
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
            presentationText.append(parameter.getPresentationTailText());
            if ( i < results.length - 1) {
                presentationText.append(",");
            }
        }

        presentationText.append(")");

        return presentationText.toString();
    }

    @Override
    public String getPresentationTypeText() {
        return "";
    }

    //    @Override
//    public LookupElementBuilder getCompletionPresentation() {
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
