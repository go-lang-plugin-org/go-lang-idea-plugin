package ro.redeul.google.go.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.*;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.*;

import static com.intellij.psi.util.PsiTreeUtil.findCommonParent;
import static com.intellij.psi.util.PsiTreeUtil.isAncestor;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class LabelUsageInspection extends AbstractWholeGoFileInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Label usage";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                checkFunction(result, declaration);
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
                checkFunction(result, declaration);
            }
        }.visitFile(file);
    }

    private static void checkFunction(final InspectionResult result, GoFunctionDeclaration function) {
        final Map<String, GoLiteralIdentifier> labelDeclarations = new HashMap<String, GoLiteralIdentifier>();
        final List<GoLiteralIdentifier> labelUsages = new ArrayList<GoLiteralIdentifier>();
        new GoRecursiveElementVisitor() {
            @Override
            public void visitLabeledStatement(GoLabeledStatement statement) {
                GoLiteralIdentifier label = statement.getLabel();
                String name = label.getName();
                if (labelDeclarations.containsKey(name)) {
                    result.addProblem(label, GoBundle.message("error.label.already.defined", name));
                } else {
                    labelDeclarations.put(name, label);
                }

                super.visitLabeledStatement(statement);
            }

            @Override
            public void visitBreakStatement(GoBreakStatement statement) {
                checkLabelUsage(statement.getLabel());
            }

            @Override
            public void visitContinueStatement(GoContinueStatement statement) {
                checkLabelUsage(statement.getLabel());
            }

            @Override
            public void visitGotoStatement(GoGotoStatement statement) {
                checkLabelUsage(statement.getLabel());
            }

            private void checkLabelUsage(GoLiteralIdentifier label) {
                if (label == null) {
                    return;
                }

                String name = label.getName();
                if (name == null || name.isEmpty()) {
                    return;
                }

                labelUsages.add(label);
            }

            @Override
            public void visitFunctionLiteral(GoLiteralFunction literal) {
                checkFunction(result, literal);
            }
        }.visitElement(function);

        Set<String> usedLabels = new HashSet<String>();
        for (GoLiteralIdentifier label : labelUsages) {
            String name = label.getName();
            if (labelDeclarations.containsKey(name)) {
                usedLabels.add(name);
            }
        }

        for (Map.Entry<String, GoLiteralIdentifier> e : labelDeclarations.entrySet()) {
            String name = e.getKey();
            if (!usedLabels.contains(name)) {
                result.addProblem(e.getValue(), GoBundle.message("error.label.defined.and.not.used", name));
            }
        }

        for (GoLiteralIdentifier label : labelUsages) {
            String name = label.getName();
            if (name == null || !labelDeclarations.containsKey(name)) {
                continue;
            }
            checkUsage(label, labelDeclarations.get(name), result);
        }
    }

    private static final TokenSet BREAK_LABEL_STATEMENT = TokenSet.orSet(
            GoElementTypes.STMTS_FOR,
            GoElementTypes.STMTS_SWITCH,
            TokenSet.create(GoElementTypes.SELECT_STATEMENT)
    );

    private static void checkUsage(GoLiteralIdentifier label, GoLiteralIdentifier declaration,
                                   InspectionResult result) {
        PsiElement usageParent = label.getParent();
        GoLabeledStatement labeledStatement = (GoLabeledStatement) declaration.getParent();
        GoStatement enclosingStatement = labeledStatement.getStatement();
        if (usageParent instanceof GoBreakStatement) {
            if (!isNodeOfType(enclosingStatement, BREAK_LABEL_STATEMENT) ||
                !isAncestor(enclosingStatement, usageParent, true)) {
                result.addProblem(label, GoBundle.message("error.invalid.break.label", label.getName()));
            }
        } else if (usageParent instanceof GoContinueStatement) {
            if (!isNodeOfType(enclosingStatement, GoElementTypes.STMTS_FOR) ||
                !isAncestor(enclosingStatement, usageParent, true)) {
                result.addProblem(label, GoBundle.message("error.invalid.continue.label", label.getName()));
            }
        } else if (usageParent instanceof GoGotoStatement) {
            GoGotoStatement gotoStatement = ((GoGotoStatement) usageParent);
            checkJumpInsideBlock(gotoStatement, labeledStatement, result);
            checkJumpOverVariableDeclaration(gotoStatement, labeledStatement, result);
        }
    }

    private static void checkJumpOverVariableDeclaration(GoGotoStatement gotoStatement,
                                                         GoLabeledStatement labeledStatement, InspectionResult result) {
        if (gotoStatement.getTextOffset() >= labeledStatement.getTextOffset()) {
            return;
        }

        PsiElement parent = findCommonParent(gotoStatement, labeledStatement);
        if (parent == null || !labeledStatement.getParent().equals(parent)) {
            return;
        }

        GoLiteralIdentifier label = gotoStatement.getLabel();
        PsiElement gotoParent = gotoStatement;
        while (gotoParent != null && !parent.equals(gotoParent.getParent())) {
            gotoParent = gotoParent.getParent();
        }
        if (gotoParent == null) {
            return;
        }

        PsiElement statement = gotoParent.getNextSibling();
        while (statement != null && !statement.equals(labeledStatement)) {
            if (statement instanceof GoShortVarDeclaration) {
                addJumpOverProblem(label, ((GoShortVarDeclaration) statement).getIdentifiers(), result);
            } else if (statement instanceof GoVarDeclaration) {
                addJumpOverProblem(label, ((GoVarDeclaration) statement).getIdentifiers(), result);
            } else if (statement instanceof GoConstDeclaration) {
                addJumpOverProblem(label, ((GoConstDeclaration) statement).getIdentifiers(), result);
            }
            statement = statement.getNextSibling();
        }
    }

    private static void addJumpOverProblem(GoLiteralIdentifier label, GoLiteralIdentifier[] identifiers,
                                           InspectionResult result) {
        GoLiteralIdentifier identifier = findFirstIdentifier(identifiers);
        if (identifier == null) {
            return;
        }

        result.addProblem(label,
                GoBundle.message("error.goto.jumps.over.declaration", label.getName(), identifier.getName()));
    }

    private static GoLiteralIdentifier findFirstIdentifier(GoLiteralIdentifier[] identifiers) {
        for (GoLiteralIdentifier identifier : identifiers) {
            if (identifier.isBlank()) {
                continue;
            }

            PsiElement resolve = resolveSafely(identifier, PsiElement.class);
            if (resolve != null && resolve.getTextOffset() != identifier.getTextOffset()) {
                continue;
            }

            return identifier;
        }
        return null;
    }

    private static void checkJumpInsideBlock(GoGotoStatement gotoStatement, GoLabeledStatement labeledStatement,
                                             InspectionResult result) {
        PsiElement parent = findCommonParent(gotoStatement, labeledStatement);
        GoLiteralIdentifier label = gotoStatement.getLabel();
        String name = label.getName();
        if (!labeledStatement.getParent().equals(parent) &&
            !labeledStatement.equals(parent)) {
            result.addProblem(label, GoBundle.message("error.goto.jumps.into.block", name));
        }
    }
}
