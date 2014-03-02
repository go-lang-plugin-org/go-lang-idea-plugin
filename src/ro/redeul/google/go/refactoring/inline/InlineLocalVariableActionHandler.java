package ro.redeul.google.go.refactoring.inline;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.lang.Language;
import com.intellij.lang.refactoring.InlineActionHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.refactoring.HelpID;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.refactoring.util.RefactoringMessageDialog;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.highlight.GoReadWriteAccessDetector;
import ro.redeul.google.go.inspection.fix.RemoveVariableFix;
import ro.redeul.google.go.lang.documentation.DocumentUtil;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.statements.*;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeStatement;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class InlineLocalVariableActionHandler extends InlineActionHandler {

    @SuppressWarnings("unchecked")
    private static final ElementPattern<GoLiteralIdentifier> LOCAL_VAR_DECLARATION =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            or(
                                    psiElement(GoShortVarDeclaration.class),
                                    psiElement(GoVarDeclaration.class)
                                            .andNot(
                                                    psiElement().withSuperParent(2, GoFile.class)
                                            )
                            )
                    );

    @Override
    public boolean isEnabledForLanguage(Language l) {
        return l == GoLanguage.INSTANCE;
    }

    @Override
    public boolean canInlineElement(PsiElement element) {
        return LOCAL_VAR_DECLARATION.accepts(element);
    }

    @Override
    public void inlineElement(final Project project, final Editor editor, final PsiElement element) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                    public void run() {
                        doInlineElement(project, editor, element);
                    }
                }, "Inline", null);
            }
        });
    }

    private void doInlineElement(Project project, Editor editor, PsiElement element) {
        GoStatement statement = findParentOfType(element, GoStatement.class);
        if (!(element instanceof GoLiteralIdentifier) || statement == null) {
            return;
        }

        PsiElement scope = null;
        if (statement instanceof GoShortVarDeclaration) {
            scope = statement.getParent();
        } else if (statement instanceof GoVarDeclaration) {
            scope = statement.getParent().getParent();
        }

        if (!(scope instanceof GoPsiElement)) {
            String message = GoBundle.message("error.unknown.refactoring.case");
            CommonRefactoringUtil.showErrorHint(project, editor, message, "Refactoring error!", null);
            return;
        }

        GoLiteralIdentifier identifier = (GoLiteralIdentifier) element;
        GoVarDeclaration declaration = (GoVarDeclaration) statement;
        try {
            inlineElement(new InlineContext(project, editor, identifier, declaration, (GoPsiElement) scope));
        } catch (GoRefactoringException e) {
            if (ApplicationManager.getApplication().isUnitTestMode()) {
                throw new RuntimeException(e);
            }
            String message = RefactoringBundle.getCannotRefactorMessage(e.getMessage());
            CommonRefactoringUtil.showErrorHint(project, editor, message, "Refactoring error!", null);
        }
    }

    private void inlineElement(InlineContext ctx) throws GoRefactoringException {
        SmartPsiElementPointer<GoPsiElement> scopePointer = createSmartElementPointer(ctx.scope);

        Usage usage = findUsage(ctx.identifierToInline, ctx.scope);
        PsiElement initializer = getIdentifierInitializer(ctx, usage);
        if (!promptToInline(ctx, usage)) {
            return;
        }

        expandAllUsage(ctx, usage, initializer);

        removeIdentifierDeclaration(ctx);

        GoPsiElement scope = scopePointer.getElement();
        if (scope != null) {
            reformatPositions(scope);
        }
    }

    private void removeIdentifierDeclaration(InlineContext ctx) throws GoRefactoringException {
        PsiElement parent = ctx.statement.getParent();
        if (ctx.statement.getIdentifiers().length > 1 ||
                parent instanceof GoVarDeclarations ||
                parent instanceof GoBlockStatement) {
            applyRemoveVariableFix(ctx);
        } else if (parent instanceof GoIfStatement) {
            deleteIfSimpleStatement(ctx.editor.getDocument(), (GoIfStatement) parent);
        } else if (parent instanceof GoSwitchExpressionStatement ||
                parent instanceof GoSwitchTypeStatement) {
            deleteSwitchSimpleStatement(ctx.editor.getDocument(), (GoStatement) parent);
        } else {
            applyRemoveVariableFix(ctx);
        }
    }

    private void expandAllUsage(InlineContext ctx, Usage usage, PsiElement initializer) throws GoRefactoringException {
        Document document = ctx.editor.getDocument();
        for (int i = usage.readUsages.length - 1; i >= 0; i--) {
            PsiElement readUsage = usage.readUsages[i];
            expandUsage(document, readUsage, initializer);
        }

        PsiDocumentManager.getInstance(ctx.project).commitDocument(document);
    }

    private PsiElement getIdentifierInitializer(InlineContext ctx, Usage usage) throws GoRefactoringException {
        String name = ctx.identifierToInline.getName();
        PsiElement initializer = getInitializer(ctx.identifierToInline);
        boolean shouldHaveInitializer = identifierShouldHaveInitializer(ctx.identifierToInline);
        if (shouldHaveInitializer && initializer == null ||
                usage.writeUsages.length == 0 && !shouldHaveInitializer) {
            throw new GoRefactoringException(RefactoringBundle.message("variable.has.no.initializer", ctx.identifierToInline.getText()));
        }

        if (usage.readUsages.length == 0) {
            String messsage = GoBundle.message("error.variable.is.never.used", name);
            throw new GoRefactoringException(messsage);
        }

        if (usage.writeUsages.length == 1 && !shouldHaveInitializer) {
            if (usage.readUsages[0].getTextOffset() > usage.writeUsages[0].getTextOffset()) {
                String messsage = GoBundle.message("error.variable.is.used.before.modification", name);
                throw new GoRefactoringException(messsage);
            }

            initializer = getInitializer((GoLiteralIdentifier) usage.writeUsages[0]);
            if (initializer == null) {
                throw new GoRefactoringException(RefactoringBundle.message("variable.has.no.initializer", ctx.identifierToInline.getText()));
            }
        }

        if (usage.writeUsages.length >= 2 ||
                usage.writeUsages.length == 1 && shouldHaveInitializer) {
            throw new GoRefactoringException(RefactoringBundle.message("variable.is.accessed.for.writing", name));
        }

        while (initializer instanceof GoParenthesisedExpression) {
            initializer = ((GoParenthesisedExpression) initializer).getInnerExpression();
        }

        return initializer;
    }

    private void applyRemoveVariableFix(InlineContext ctx) {
        new RemoveVariableFix().applyFix(ctx.identifierToInline);
    }

    private static final TokenSet MUL_TOKENS = TokenSet.create(
            GoTokenTypes.oMUL,
            GoTokenTypes.oQUOTIENT,
            GoTokenTypes.oREMAINDER,
            GoTokenTypes.oSHIFT_LEFT,
            GoTokenTypes.oSHIFT_RIGHT,
            GoTokenTypes.oBIT_AND,
            GoTokenTypes.oBIT_CLEAR
    );

    private static final TokenSet ADD_TOKENS = TokenSet.create(
            GoTokenTypes.oPLUS,
            GoTokenTypes.oMINUS,
            GoTokenTypes.oBIT_OR,
            GoTokenTypes.oBIT_XOR
    );

    private static final TokenSet REL_TOKENS = TokenSet.create(
            GoTokenTypes.oEQ,
            GoTokenTypes.oNOT_EQ,
            GoTokenTypes.oLESS,
            GoTokenTypes.oLESS_OR_EQUAL,
            GoTokenTypes.oGREATER,
            GoTokenTypes.oGREATER_OR_EQUAL
    );

    private static int getExpressionPrecedence(PsiElement element) {
        if (element instanceof GoBinaryExpression) {
            IElementType operator = ((GoBinaryExpression) element).getOperator();
            if (MUL_TOKENS.contains(operator)) {
                return 5;
            } else if (ADD_TOKENS.contains(operator)) {
                return 4;
            } else if (REL_TOKENS.contains(operator)) {
                return 3;
            } else if (operator == GoElementTypes.oCOND_AND) {
                return 2;
            } else if (operator == GoElementTypes.oCOND_OR) {
                return 1;
            }

            return -1;
        } else if (element instanceof GoUnaryExpression) {
            return 10;
        } else if (element instanceof GoSelectorExpression) {
            return 15;
        } else if (element instanceof GoLiteralIdentifier ||
                element instanceof GoLiteralExpression) {
            return 20;
        }

        return -1;
    }

    private void expandUsage(Document document, PsiElement usage, PsiElement initializer) throws GoRefactoringException {
        PsiElement usageParent = usage.getParent();
        if (usageParent instanceof GoLiteralExpression) {
            usageParent = usageParent.getParent();
        }
        int usagePrecedence = getExpressionPrecedence(usageParent);
        int initializerPrecedence = getExpressionPrecedence(initializer);
        String text = initializer.getText();

        if (initializerPrecedence == -1) {
            if (!(usageParent instanceof GoCallOrConvExpression) &&
                    !(usageParent instanceof GoParenthesisedExpression))
                unknownCase();
        } else if (initializerPrecedence < usagePrecedence) {
            text = "(" + text + ")";
        }
        DocumentUtil.replaceElementWithText(document, usage, text);
    }

    private void deleteIfSimpleStatement(Document document, GoIfStatement ifStatement) throws GoRefactoringException {
        GoSimpleStatement simpleStatement = ifStatement.getSimpleStatement();
        if (simpleStatement == null) {
            unknownCase();
            return;
        }
        int start = simpleStatement.getTextOffset();
        int end = ifStatement.getExpression().getTextOffset();
        document.deleteString(start, end);
    }

    private void deleteSwitchSimpleStatement(Document document, GoStatement statement)
            throws GoRefactoringException {
        GoSimpleStatement simpleStatement;
        if (statement instanceof GoSwitchExpressionStatement) {
            simpleStatement = ((GoSwitchExpressionStatement) statement).getSimpleStatement();
        } else {
            simpleStatement = ((GoSwitchTypeStatement) statement).getSimpleStatement();
        }

        if (simpleStatement == null) {
            unknownCase();
            return;
        }

        PsiElement endElement;
        if (statement instanceof GoSwitchExpressionStatement) {
            endElement = ((GoSwitchExpressionStatement) statement).getExpression();
        } else {
            endElement = ((GoSwitchTypeStatement) statement).getTypeGuard();
        }

        if (endElement == null) {
            endElement = findChildOfType(statement, GoTokenTypes.pLCURLY);
        }

        if (endElement == null) {
            unknownCase();
            return;
        }

        int start = simpleStatement.getTextOffset();
        document.deleteString(start, endElement.getTextOffset());
    }

    private void unknownCase() throws GoRefactoringException {
        throw new GoRefactoringException(GoBundle.message("error.unknown.refactoring.case"));
    }

    private PsiElement getInitializer(GoLiteralIdentifier identifier) {
        PsiElement parent = identifier.getParent();
        if (parent instanceof GoVarDeclaration) {
            GoVarDeclaration declaration = (GoVarDeclaration) parent;
            GoLiteralIdentifier[] identifiers = declaration.getIdentifiers();
            GoExpr[] expressions = declaration.getExpressions();
            if (expressions.length != identifiers.length) {
                return null;
            }

            for (int i = 0; i < expressions.length; i++) {
                if (identifiers[i].getTextRange().equals(identifier.getTextRange())) {
                    return expressions[i];
                }
            }
        } else if (parent instanceof GoExpressionList) {
            PsiElement grandpa = parent.getParent();
            if (grandpa instanceof GoAssignmentStatement) {
                GoAssignmentStatement assignment = (GoAssignmentStatement) grandpa;
                GoExpr[] identifiers = assignment.getLeftSideExpressions().getExpressions();
                GoExpr[] expressions = assignment.getRightSideExpressions().getExpressions();
                if (identifiers.length != expressions.length) {
                    return null;
                }

                for (int i = 0; i < expressions.length; i++) {
                    if (identifiers[i].getTextRange().equals(identifier.getTextRange())) {
                        return expressions[i];
                    }
                }
            }
        }
        return null;
    }

    private boolean identifierShouldHaveInitializer(GoLiteralIdentifier identifier) {
        PsiElement parent = identifier.getParent();

        return !(parent instanceof GoVarDeclaration && ((GoVarDeclaration) parent).getExpressions().length == 0);
    }

    private boolean promptToInline(InlineContext ctx, Usage usage) {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return true;
        }

        usage.highlight(ctx.project, ctx.editor, ctx.identifierToInline);

        int occurrencesCount = usage.writeUsages.length + usage.readUsages.length;
        String occurrencesString = RefactoringBundle.message("occurences.string", occurrencesCount);
        String name = ctx.identifierToInline.getText();
        String question = RefactoringBundle.message("inline.local.variable.prompt", name) + " " + occurrencesString;
        RefactoringMessageDialog dialog = new RefactoringMessageDialog(
                RefactoringBundle.message("inline.variable.title"),
                question,
                HelpID.INLINE_VARIABLE,
                "OptionPane.questionIcon",
                true,
                ctx.project);
        dialog.show();
        if (!dialog.isOK()) {
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(ctx.project);
            if (statusBar != null) {
                statusBar.setInfo(RefactoringBundle.message("press.escape.to.remove.the.highlighting"));
            }
            return false;
        }
        return true;
    }

    private static Usage findUsage(final GoLiteralIdentifier identifier, GoPsiElement scope) {
        final List<PsiElement> writeUsages = new ArrayList<PsiElement>();
        final List<PsiElement> readUsages = new ArrayList<PsiElement>();

        final GoReadWriteAccessDetector detector = new GoReadWriteAccessDetector();
        new GoRecursiveElementVisitor() {
            @Override
            public void visitLiteralIdentifier(GoLiteralIdentifier id) {
                if (identifier.equals(resolveSafely(id, PsiElement.class))) {
                    if (detector.getExpressionAccess(id) == ReadWriteAccessDetector.Access.Read) {
                        readUsages.add(id);
                    } else {
                        writeUsages.add(id);
                    }
                }
            }
        }.visitElement(scope);
        return new Usage(writeUsages, readUsages);
    }

    private static class InlineContext {
        final Project project;
        final Editor editor;
        final GoLiteralIdentifier identifierToInline;
        final GoVarDeclaration statement;
        final GoPsiElement scope;

        private InlineContext(Project project, Editor editor, GoLiteralIdentifier identifierToInline,
                              GoVarDeclaration statement, GoPsiElement scope) {
            this.project = project;
            this.editor = editor;
            this.identifierToInline = identifierToInline;
            this.statement = statement;
            this.scope = scope;
        }
    }

    private static class Usage {
        final PsiElement[] writeUsages;
        final PsiElement[] readUsages;

        private Usage(Collection<PsiElement> writeUsages, Collection<PsiElement> readUsages) {
            this.writeUsages = writeUsages.toArray(new PsiElement[writeUsages.size()]);
            this.readUsages = readUsages.toArray(new PsiElement[readUsages.size()]);
        }

        private void highlight(Project project, Editor editor, GoLiteralIdentifier identifierToInline) {
            HighlightManager manager = HighlightManager.getInstance(project);
            EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
            TextAttributes attributes = scheme.getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);
            TextAttributes writeAttributes = scheme.getAttributes(EditorColors.WRITE_SEARCH_RESULT_ATTRIBUTES);
            manager.addOccurrenceHighlights(editor, readUsages, attributes, true, null);
            manager.addOccurrenceHighlights(editor, writeUsages, writeAttributes, true, null);
            manager.addOccurrenceHighlights(editor, new PsiElement[]{identifierToInline}, writeAttributes, true, null);
        }
    }
}
