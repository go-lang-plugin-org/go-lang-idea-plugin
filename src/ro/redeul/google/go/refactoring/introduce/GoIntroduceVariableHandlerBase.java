package ro.redeul.google.go.refactoring.introduce;

import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.introduce.inplace.OccurrencesChooser;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import java.util.*;

abstract class GoIntroduceVariableHandlerBase extends GoIntroduceHandlerBase {
    static final String VARIABLE = "____INTRODUCE_VARIABLE____";
    Project project;
    Document document;
    Editor editor;
    GoFile file;

    GoPsiElement getDefaultVisitStartElement() {
        return null;
    }

    @Override
    protected synchronized void doIntroduce(Project project, Editor editor, GoFile file, int start, int end)
            throws GoRefactoringException {
        final GoExpr e = CodeInsightUtilBase.findElementInRange(file, start, end, GoExpr.class, GoLanguage.INSTANCE);
        if (!isExpressionValid(e)) {
            throw new GoRefactoringException(GoBundle.message("error.invalid.expression"));
        }

        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document == null) {
            return;
        }

        this.project = project;
        this.editor = editor;
        this.document = document;
        this.file = file;

        GoExpr[] occurrences = ExpressionOccurrenceManager.findOccurrences(e, getDefaultVisitStartElement());
        if (occurrences.length == 0) {
            throw new GoRefactoringException(GoBundle.message("error.invalid.expression"));
        }

        includeExpressionParenthesesIfPossible(occurrences);

        ReplaceChoicePass callback = new ReplaceChoicePass(e, occurrences);
        if (isVariableInplaceRenameEnabled(editor)) {
            OccurrencesChooser.simpleChooser(editor).showChooser(callback, fillChoiceMap(e, occurrences));
        } else {
            callback.pass(OccurrencesChooser.ReplaceChoice.ALL);
        }

        if (callback.error != null) {
            throw callback.error;
        }
    }

    private void includeExpressionParenthesesIfPossible(GoExpr[] occurrences) {
        for (int i = 0; i < occurrences.length; i++) {
            occurrences[i] = getParenthesisedExpression(occurrences[i]);
        }
    }

    private GoExpr getParenthesisedExpression(GoExpr expr) {
        while (expr.getParent() instanceof GoParenthesisedExpression) {
            expr = (GoExpr) expr.getParent();
        }
        return expr;
    }

    private static boolean isVariableInplaceRenameEnabled(Editor editor) {
        return editor.getSettings().isVariableInplaceRenameEnabled() &&
               !ApplicationManager.getApplication().isUnitTestMode();
    }

    static String getExpressionDeclaration(GoExpr e) {
        while (e instanceof GoParenthesisedExpression) {
            e = ((GoParenthesisedExpression) e).getInnerExpression();
        }
        return e != null ? e.getText().trim() : "";
    }

    private Map<OccurrencesChooser.ReplaceChoice, List<PsiElement>> fillChoiceMap(GoExpr current,
                                                                                  GoExpr[] occurrences) {
        Map<OccurrencesChooser.ReplaceChoice, List<PsiElement>> map =
                new LinkedHashMap<>();
        map.put(OccurrencesChooser.ReplaceChoice.NO, Collections.<PsiElement>singletonList(current));
        if (occurrences.length > 1) {
            map.put(OccurrencesChooser.ReplaceChoice.ALL, Arrays.<PsiElement>asList(occurrences));
        }
        return map;
    }

    boolean expressionIsTheWholeStatement(PsiElement element, GoStatement stmt) {
        return element.getTextRange().equals(stmt.getTextRange());
    }


    private class ReplaceChoicePass extends Pass<OccurrencesChooser.ReplaceChoice> {
        private GoExpr e;
        private final GoExpr[] occurrences;
        GoRefactoringException error = null;

        public ReplaceChoicePass(GoExpr e, GoExpr[] occurrences) {
            this.e = e;
            this.occurrences = occurrences;
        }

        @Override
        public void pass(final OccurrencesChooser.ReplaceChoice choice) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                        public void run() {
                            try {
                                e = getParenthesisedExpression(e);
                                if (occurrences.length <= 1 || choice == OccurrencesChooser.ReplaceChoice.NO) {
                                    introduceCurrentOccurrence(e);
                                } else {
                                    introduceAllOccurrence(e, occurrences);
                                }
                            } catch (GoRefactoringException exception) {
                                error = exception;
                            }
                        }
                    }, "Introduce", null);
                }
            });
        }
    }

    protected abstract void introduceAllOccurrence(GoExpr current, GoExpr[] occurrences) throws GoRefactoringException;
    protected abstract void introduceCurrentOccurrence(GoExpr current) throws GoRefactoringException;
}
