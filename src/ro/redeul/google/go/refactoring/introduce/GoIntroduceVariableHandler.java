package ro.redeul.google.go.refactoring.introduce;

import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.editor.TemplateUtil;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.editor.TemplateUtil.createTemplate;
import static ro.redeul.google.go.editor.TemplateUtil.getTemplateVariableExpression;
import static ro.redeul.google.go.editor.TemplateUtil.runTemplate;
import static ro.redeul.google.go.editor.TemplateUtil.setTemplateVariableValues;
import static ro.redeul.google.go.lang.psi.utils.GoExpressionUtils.resolveToFunctionDeclaration;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class GoIntroduceVariableHandler extends GoIntroduceVariableHandlerBase {
    @Override
    protected boolean isValidExpression(GoExpr expr) {
        return expr != null;
    }

    protected void introduceCurrentOccurrence(GoExpr e) throws GoRefactoringException {
        int start = e.getTextOffset();
        int end = start + e.getTextLength();
        GoStatement stmt = findParentOfType(e, GoStatement.class);
        int lineStart = document.getLineStartOffset(document.getLineNumber(stmt.getTextOffset()));
        String declaration = getExpressionDeclaration(e);

        RangeMarker range = document.createRangeMarker(lineStart, end);
        editor.getCaretModel().moveToOffset(end);

        String originalText = document.getText(new TextRange(lineStart, start));
        String text;
        if (expressionIsTheWholeStatement(e, stmt)) {
            if (introduceExpressionStatement(e, declaration)) {
                return;
            }
            text = String.format("$%s$ := %s", VARIABLE, declaration);
        } else {
            text = String.format("$%s$ := %s\n%s$%s$", VARIABLE, declaration, originalText, VARIABLE);
        }
        document.replaceString(lineStart, end, text);
        runTemplate(editor, TextRange.create(range), VARIABLE, "value");
    }

    // If it's possible to analyse the result information (like result count or even result names)
    // of the expression, we introduce variables according to the information, and return true.
    // otherwise, return false.
    private boolean introduceExpressionStatement(PsiElement element, String declaration)
        throws GoRefactoringException {
        GoFunctionDeclaration function = resolveToFunctionDeclaration(element);
        if (function == null) {
            return false;
        }

        List<String> resultNames = getFunctionResultNames(function);
        if (resultNames.isEmpty()) {
            throw new GoRefactoringException(
                GoBundle.message("error.expression.has.void.return.type"));
        }

        TextRange range = element.getTextRange();
        editor.getDocument()
              .deleteString(range.getStartOffset(), range.getEndOffset());

        String text = getTemplateVariableExpression(resultNames.size(), ", ");
        text += " := " + declaration;

        TemplateImpl template = createTemplate(text);
        setTemplateVariableValues(template, resultNames);
        TemplateManager.getInstance(project).startTemplate(editor, template);
        return true;
    }

    private List<String> getFunctionResultNames(GoFunctionDeclaration function) {
        int index = 0;
        List<String> parameterNames = new ArrayList<String>();
        for (GoFunctionParameter fp : function.getResults()) {
            GoLiteralIdentifier[] identifiers = fp.getIdentifiers();
            // unnamed parameter
            if (identifiers.length == 0) {
                parameterNames.add("v" + index++);
            } else {
                // get names of named parameters
                for (GoLiteralIdentifier identifier : identifiers) {
                    String name = identifier.getName();
                    if (name != null && !name.isEmpty()) {
                        parameterNames.add(name);
                    } else {
                        parameterNames.add("v" + index);
                    }
                    index++;
                }
            }
        }
        return parameterNames;
    }

    protected void introduceAllOccurrence(GoExpr current, GoExpr[] occurrences) throws GoRefactoringException {
        RangeMarker[] exprRangeMarkers = new RangeMarker[occurrences.length];
        GoStatement[] statements = new GoStatement[occurrences.length];
        for (int i = 0; i < occurrences.length; i++) {
            statements[i] = findParentOfType(occurrences[i], GoStatement.class);
            if (statements[i] == null) {
                throw new GoRefactoringException("Cannot find corresponding statement");
            }
            exprRangeMarkers[i] = document.createRangeMarker(occurrences[i].getTextRange());
        }

        int insertionPoint = findInsertionPoint(statements);

        String variable = "$" + VARIABLE + "$";
        String decl = String.format("%s := %s\n", variable, getExpressionDeclaration(current));
        document.insertString(insertionPoint,  decl);

        for (RangeMarker marker : exprRangeMarkers) {
            document.replaceString(marker.getStartOffset(), marker.getEndOffset(), variable);
        }

        int endOffset = getMergedRange(exprRangeMarkers).getEndOffset();
        TextRange range = new TextRange(insertionPoint, endOffset);
        TemplateUtil.runTemplate(editor, range, VARIABLE, "value");
    }

    private TextRange getMergedRange(RangeMarker[] markers) {
        TextRange result = new TextRange(markers[0].getStartOffset(), markers[0].getEndOffset());
        for (RangeMarker marker : markers) {
            TextRange range = new TextRange(marker.getStartOffset(), marker.getEndOffset());
            result = result.union(range);
        }
        return result;
    }

    private int findInsertionPoint(GoStatement[] statements) throws GoRefactoringException {
        PsiElement parent = PsiTreeUtil.findCommonParent(statements);
        if (parent != null && !(parent instanceof GoBlockStatement)) {
            parent = parent.getParent();
        }

        if (!(parent instanceof GoBlockStatement)) {
            throw new GoRefactoringException("Unknown case");
        }

        int minOffset = statements[0].getTextOffset();
        for (GoStatement statement : statements) {
            minOffset = Math.min(minOffset, statement.getTextOffset());
        }

        int insertionPoint = 0;
        for (GoStatement statement : ((GoBlockStatement) parent).getStatements()) {
            if (statement.getTextOffset() > minOffset) {
                break;
            }
            insertionPoint = statement.getTextOffset();
        }
        return insertionPoint;
    }
}
