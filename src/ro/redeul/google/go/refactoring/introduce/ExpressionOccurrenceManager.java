package ro.redeul.google.go.refactoring.introduce;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static ro.redeul.google.go.lang.parser.GoElementTypes.CONST_DECLARATION;
import static ro.redeul.google.go.lang.parser.GoElementTypes.SHORT_VAR_STATEMENT;
import static ro.redeul.google.go.lang.parser.GoElementTypes.VAR_DECLARATION;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

class ExpressionOccurrenceManager {
    private static final Condition<PsiElement> ELEMENT_SIGNIFICANT_CONDITION = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement element) {
            return !GoPsiUtils.isWhiteSpaceOrComment(element);
        }
    };

    private static final TokenSet VAR_DECL_TYPE =
            TokenSet.create(VAR_DECLARATION, CONST_DECLARATION, SHORT_VAR_STATEMENT);

    private final List<GoExpr> occurrences = new ArrayList<GoExpr>();
    private final GoExpr expr;
    private final GoFunctionDeclaration parentFunction;

    // If no local variable is found in the expression, start searching for expressions from this element.
    private final GoPsiElement defaultVisitStartElement;

    private ExpressionOccurrenceManager(GoExpr expr, GoPsiElement defaultVisitStartElement) {
        this.expr = expr;
        parentFunction = findParentOfType(expr, GoFunctionDeclaration.class);
        this.defaultVisitStartElement = defaultVisitStartElement == null ? parentFunction : defaultVisitStartElement;
    }

    private void find() {
        if (defaultVisitStartElement == null) {
            return;
        }

        Map<GoLiteralIdentifier, PsiElement> localIdentifiers = getAllLocalIdentifiers();
        Set<PsiElement> parents = getParentsOfIdentifierDeclarations(localIdentifiers);
        GoPsiElement visitStartElement = defaultVisitStartElement;
        if (!parents.isEmpty()) {
            PsiElement p = expr.getParent();
            while (p != null && !parents.contains(p)) {
                p = p.getParent();
            }
            if (p instanceof GoPsiElement) {
                visitStartElement = (GoPsiElement) p;
            }
        }

        new GoRecursiveElementVisitor() {
            @Override
            public void visitElement(GoPsiElement element) {
                if (element instanceof GoExpr && areExpressionsEquivalent(expr, (GoExpr) element)) {
                    occurrences.add((GoExpr) element);
                    return;
                }
                super.visitElement(element);
            }
        }.visitElement(visitStartElement);
    }

    private Set<PsiElement> getParentsOfIdentifierDeclarations(Map<GoLiteralIdentifier, PsiElement> identifiers) {
        Set<PsiElement> parents = new HashSet<PsiElement>();
        for (PsiElement element : identifiers.values()) {
            GoStatement statement = findParentOfType(element, GoStatement.class);
            if (statement != null) {
                parents.add(statement.getParent());
            }
        }
        return parents;
    }

    private Map<GoLiteralIdentifier, PsiElement> getAllLocalIdentifiers() {
        final Map<GoLiteralIdentifier, PsiElement> identifiers = new HashMap<GoLiteralIdentifier, PsiElement>();
        final AtomicBoolean error = new AtomicBoolean(false);
        new GoRecursiveElementVisitor() {
            @Override
            public void visitElement(GoPsiElement element) {
                if (!error.get()) {
                    super.visitElement(element);
                }
            }

            @Override
            public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
                PsiElement resolve = GoPsiUtils.resolveSafely(identifier, PsiElement.class);
                if (resolve == null) {
                    error.set(true);
                    return;
                }

                if (findParentOfType(resolve, GoFunctionDeclaration.class) == parentFunction &&
                        isNodeOfType(resolve.getParent(), VAR_DECL_TYPE)) {
                    identifiers.put(identifier, resolve);
                }
            }
        }.visitElement(expr);
        return identifiers;
    }

    private static boolean areExpressionsEquivalent(GoExpr expr1, GoExpr expr2) {
        return PsiEquivalenceUtil.areElementsEquivalent(expr1, expr2, null, null, ELEMENT_SIGNIFICANT_CONDITION, false);
    }

    /**
     * Find all occurrences of the expression.
     * @param expr The expression to search for
     * @param defaultVisitStartElement If no local variable is found in the expression, start searching for expressions from this element.
     *                                 If it's null, start searching from the function which the expr belongs to.
     * @return All occurrences
     */
    public static GoExpr[] findOccurrences(GoExpr expr, GoPsiElement defaultVisitStartElement) {
        ExpressionOccurrenceManager eom = new ExpressionOccurrenceManager(expr, defaultVisitStartElement);
        eom.find();
        return eom.occurrences.toArray(new GoExpr[eom.occurrences.size()]);
    }
}
