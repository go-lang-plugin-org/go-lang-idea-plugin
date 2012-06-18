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
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;

import java.util.List;

import static ro.redeul.google.go.lang.documentation.DocumentUtil.getFunctionParameterRangeInText;
import static ro.redeul.google.go.lang.documentation.DocumentUtil.getFunctionPresentationText;
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

    private GoCallOrConversionExpression findFunctionCallParent(PsiElement element, int offset) {
        GoCallOrConversionExpression call = findParentOfType(element, GoCallOrConversionExpression.class);
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
        GoCallOrConversionExpression call = findFunctionCallParent(element, offset);
        if (call == null) {
            return null;
        }

        List<GoExpr> exprs = findChildrenOfType(call, GoExpr.class);
        GoLiteral literal = ((GoLiteralExpression) exprs.get(0)).getLiteral();
        if (!(literal instanceof GoLiteralIdentifier)) {
            return null;
        }

        GoLiteralIdentifier id = (GoLiteralIdentifier) literal;
        PsiElement functionDeclaration = id.resolve();
        if (!(functionDeclaration instanceof GoFunctionDeclaration)) {
            return null;
        }

        GoFunctionDeclaration func = (GoFunctionDeclaration) functionDeclaration;
        GoFunctionParameter[] parameters = func.getParameters();
        if (parameters == null || parameters.length == 0) {
            return null;
        }

        List<PsiElement> commas = findChildrenOfType(call, GoElementTypes.oCOMMA);
        int currentIndex = commas.size();
        for (int i = 0; i < commas.size(); i++) {
            if (offset < commas.get(i).getTextRange().getEndOffset()) {
                currentIndex = i;
                break;
            }
        }

        if (id.getTextRange().contains(offset)) {
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

        public final GoCallOrConversionExpression call;
        public int currentIndex;

        private ParameterInfo(GoFunctionDeclaration functionDeclaration, GoCallOrConversionExpression call,
                              int currentIndex) {
            this.functionDeclaration = functionDeclaration;
            this.call = call;
            this.currentIndex = currentIndex;
        }
    }
}
