package ro.redeul.google.go.refactoring.introduce;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import java.util.concurrent.atomic.AtomicBoolean;

import static ro.redeul.google.go.editor.TemplateUtil.runTemplate;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.createSmartElementPointer;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isEnclosedByParenthesis;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class GoIntroduceConstantHandler extends GoIntroduceVariableHandlerBase {
    @Override
    protected boolean isExpressionValid(GoExpr expr) {
        return expr != null && isConstantExpression(expr);
    }

    @Override
    protected GoPsiElement getDefaultVisitStartElement() {
        return file;
    }

    @Override
    protected void introduceAllOccurrence(GoExpr current, GoExpr[] occurrences) throws GoRefactoringException {
        RangeMarker[] exprMarkers = new RangeMarker[occurrences.length];
        for (int i = 0; i < occurrences.length; i++) {
            exprMarkers[i] = document.createRangeMarker(occurrences[i].getTextRange());
        }

        String declaration = getExpressionDeclaration(current);
        GoConstDeclarations[] allConstDeclarations = file.getConsts();
        if (allConstDeclarations.length > 0) {
            GoConstDeclarations declarations = allConstDeclarations[allConstDeclarations.length - 1];
            appendConstToLastDeclaration(exprMarkers, declaration, declarations);
        } else {
            appendConstToLastImportOrPackage(exprMarkers, declaration);
        }
    }

    @Override
    protected void introduceCurrentOccurrence(GoExpr current) throws GoRefactoringException {
        introduceAllOccurrence(current, new GoExpr[]{current});
    }

    private void appendConstToLastImportOrPackage(RangeMarker[] exprMarkers, String declaration) {
        GoPsiElement lastElement;
        GoImportDeclarations[] imports = file.getImportDeclarations();
        if (imports.length != 0) {
            lastElement = imports[imports.length - 1];
        } else {
            lastElement = file.getPackage();
        }

        int offset = lastElement.getTextRange().getEndOffset();
        String stmt = "\n\nconst $" + VARIABLE + "$ = " + declaration;
        startRenaming(editor, exprMarkers, offset, stmt, null);
    }

    private void appendConstToLastDeclaration(RangeMarker[] exprMarkers, String declaration,
                                              GoConstDeclarations declarations) {
        RangeMarker originalRange = document.createRangeMarker(declarations.getTextRange());
        GoConstDeclaration[] consts = declarations.getDeclarations();
        GoConstDeclaration lastConst = consts[consts.length - 1];
        if (consts.length == 1 && !isEnclosedByParenthesis(consts[0])) {
            SmartPsiElementPointer<GoConstDeclarations> declPointer = createSmartElementPointer(declarations);
            SmartPsiElementPointer<GoConstDeclaration> lastConstPointer = createSmartElementPointer(lastConst);
            addParentheses(declarations);
            lastConst = lastConstPointer.getElement();
            declarations = declPointer.getElement();
            if (lastConst == null || declarations == null) {
                return;
            }

            originalRange = document.createRangeMarker(declarations.getTextRange());
        }

        int offset = lastConst.getTextOffset() + lastConst.getTextLength();
        String stmt = "\n$" + VARIABLE + "$ = " + declaration;

        startRenaming(editor, exprMarkers, offset, stmt, originalRange);
    }

    private void addParentheses(GoConstDeclarations declarations) {
        GoConstDeclaration[] consts = declarations.getDeclarations();
        if (consts.length == 0) {
            return;
        }

        Document document = editor.getDocument();
        RangeMarker marker = document.createRangeMarker(declarations.getTextRange());

        document.insertString(consts[consts.length - 1].getTextRange().getEndOffset(), "\n)");
        document.insertString(consts[0].getTextOffset(), "(\n");
        reformatPositions(declarations.getContainingFile(), marker);
    }

    private void startRenaming(Editor editor, RangeMarker[] exprMarkers, int offset, String stmt,
                               @Nullable RangeMarker originalStatementRange) {
        Document document = editor.getDocument();
        RangeMarker insertPoint = document.createRangeMarker(offset, offset);
        for (RangeMarker exprMarker : exprMarkers) {
            // replace expression with const
            document.replaceString(exprMarker.getStartOffset(), exprMarker.getEndOffset(), '$' + VARIABLE + '$');
        }

        document.replaceString(insertPoint.getStartOffset(), insertPoint.getEndOffset(), stmt);
        TextRange range;
        if (originalStatementRange != null) {
            range = TextRange.create(originalStatementRange);
        } else {
            range = new TextRange(insertPoint.getStartOffset(), insertPoint.getEndOffset() + stmt.length());
        }
        for (RangeMarker exprMarker : exprMarkers) {
            range = range.union(new TextRange(exprMarker.getStartOffset(), exprMarker.getEndOffset()));
        }

        runTemplate(editor, range, VARIABLE, "VALUE");
    }

    private static boolean isConstantExpression(GoExpr expr) {
        final AtomicBoolean stopped = new AtomicBoolean(false);
        new GoRecursiveElementVisitor() {
            @Override
            public void visitElement(GoPsiElement element) {
                if (!stopped.get()) {
                    super.visitElement(element);
                }
            }

            @Override
            public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
                PsiElement resolve = GoPsiUtils.resolveSafely(identifier, PsiElement.class);
                GoConstDeclarations declarations = findParentOfType(resolve, GoConstDeclarations.class);
                if (declarations == null || !(declarations.getParent() instanceof GoFile)) {
                    stopped.set(true);
                    return;
                }
            }
        }.visitElement(expr);

        return !stopped.get();
    }
}
