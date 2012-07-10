package ro.redeul.google.go.lang.parameterInfo;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoHandler;
import com.intellij.lang.parameterInfo.ParameterInfoUIContext;
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;

import java.util.List;

import static ro.redeul.google.go.lang.documentation.DocumentUtil.getFunctionParameterRangeInText;
import static ro.redeul.google.go.lang.documentation.DocumentUtil.getFunctionPresentationText;
import static ro.redeul.google.go.lang.psi.utils.GoExpressionUtils.getCallParenthesesTextRange;
import static ro.redeul.google.go.lang.psi.utils.GoExpressionUtils.resolveToFunctionDeclaration;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildrenOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class GoParameterInfoHandler implements ParameterInfoHandler<GoPsiElement, GoParameterInfoHandler.ParameterInfo> {
    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Override
    public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
        return new Object[0];
    }

    @Override
    public Object[] getParametersForDocumentation(ParameterInfo p, ParameterInfoContext context) {
        return new Object[0];
    }

    @Override
    public GoPsiElement findElementForParameterInfo(CreateParameterInfoContext context) {
        int offset = context.getEditor().getCaretModel().getOffset();
        ParameterInfo pi = findAnchorElement(offset, context.getFile());
        if (pi == null) {
            return null;
        }
        context.setItemsToShow(new Object[]{pi});
        return pi.call;
    }

    @Override
    public GoPsiElement findElementForUpdatingParameterInfo(UpdateParameterInfoContext context) {
        int offset = context.getEditor().getCaretModel().getOffset();
        ParameterInfo pi = findAnchorElement(offset, context.getFile());
        if (pi == null) {
            return null;
        }

        Object[] psi = context.getObjectsToView();
        if (psi == null || psi.length == 0 || !(psi[0] instanceof ParameterInfo)) {
            return null;
        }

        ParameterInfo oldPi = (ParameterInfo) psi[0];
        if (pi.call != oldPi.call || pi.currentIndex < 0) {
            return null;
        }

        oldPi.currentIndex = pi.currentIndex;
        return pi.call;
    }

    private GoCallOrConvExpression findFunctionCallParent(PsiElement element, int offset) {
        GoCallOrConvExpression call = findParentOfType(element, GoCallOrConvExpression.class);
        if (call == null) {
            return null;
        }

        List<GoExpr> exprs = findChildrenOfType(call, GoExpr.class);
        if (exprs.isEmpty()) {
            return null;
        }

        PsiElement child = exprs.get(0);
        if (!(child instanceof GoLiteralExpression)) {
            return null;
        }

        // If the caret is on function name, current function parameters info shouldn't be shown.
        // Let's see if the function call is a parameter of another function call.
        if (child.getTextRange().contains(offset)) {
            return findFunctionCallParent(call.getParent(), offset);
        }
        return call;
    }

    private ParameterInfo findAnchorElement(int offset, PsiFile file) {
        PsiElement element = file.findElementAt(offset);
        GoCallOrConvExpression call = findFunctionCallParent(element, offset);
        GoFunctionDeclaration func = resolveToFunctionDeclaration(call);
        if (func == null) {
            return null;
        }

        GoFunctionParameter[] parameters = func.getParameters();
        if (parameters == null || parameters.length == 0) {
            return null;
        }

        PsiElement expressionList = findChildOfType(call, GoElementTypes.EXPRESSION_LIST);
        List<PsiElement> commas = findChildrenOfType(expressionList, GoElementTypes.oCOMMA);
        int currentIndex = commas.size();
        for (int i = 0; i < commas.size(); i++) {
            if (offset < commas.get(i).getTextRange().getEndOffset()) {
                currentIndex = i;
                break;
            }
        }

        if (!getCallParenthesesTextRange(call).contains(offset)) {
            currentIndex = -1;
        }

        return new ParameterInfo(func, call, currentIndex);
    }

    @Override
    public void showParameterInfo(@NotNull GoPsiElement element, CreateParameterInfoContext context) {
        context.showHint(element, element.getTextRange().getStartOffset(), this);
    }

    @Override
    public void updateParameterInfo(@NotNull GoPsiElement o, UpdateParameterInfoContext context) {
        Object[] psi = context.getObjectsToView();
        if (psi == null || psi.length == 0 || !(psi[0] instanceof ParameterInfo)) {
            return;
        }
        ParameterInfo pi = (ParameterInfo) psi[0];
        context.setCurrentParameter(pi.currentIndex);
    }

    @Override
    public void updateUI(ParameterInfo p, ParameterInfoUIContext context) {
        String text = getFunctionPresentationText(p.functionDeclaration);
        int index = context.getCurrentParameterIndex();
        TextRange range = getFunctionParameterRangeInText(p.functionDeclaration, index);
        context.setupUIComponentPresentation(text,
                range.getStartOffset(), range.getEndOffset(),
                false, false, true, context.getDefaultParameterColor()
        );
    }

    @Override
    public String getParameterCloseChars() {
        return null;
    }

    @Override
    public boolean tracksParameterIndex() {
        return false;
    }

    public static class ParameterInfo {
        public final GoFunctionDeclaration functionDeclaration;

        public final GoCallOrConvExpression call;
        public int currentIndex;

        private ParameterInfo(GoFunctionDeclaration functionDeclaration, GoCallOrConvExpression call,
                              int currentIndex) {
            this.functionDeclaration = functionDeclaration;
            this.call = call;
            this.currentIndex = currentIndex;
        }
    }
}
