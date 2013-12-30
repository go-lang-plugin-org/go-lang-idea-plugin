package ro.redeul.google.go.findUsages;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.inspection.InspectionResult;
import ro.redeul.google.go.inspection.fix.ConvertToAssignmentFix;
import ro.redeul.google.go.inspection.fix.DeleteStmtFix;
import ro.redeul.google.go.inspection.fix.RemoveVariableFix;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeAndVarsStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeClause;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.utils.GoFileUtils;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class GoVariableUsageStatVisitor extends GoRecursiveElementVisitor {

    private static final TokenSet NEW_SCOPE_STATEMENT = TokenSet.create(
        GoElementTypes.BLOCK_STATEMENT,
        GoElementTypes.IF_STATEMENT,
        GoElementTypes.FOR_WITH_CLAUSES_STATEMENT,
        GoElementTypes.FOR_WITH_CONDITION_STATEMENT,
        GoElementTypes.FOR_WITH_RANGE_STATEMENT,
        GoElementTypes.SWITCH_EXPR_STATEMENT,
        GoElementTypes.SWITCH_TYPE_STATEMENT,
        GoElementTypes.SWITCH_EXPR_CASE,
        GoElementTypes.SELECT_STATEMENT,
        GoElementTypes.SELECT_COMM_CLAUSE_DEFAULT,
        GoElementTypes.SELECT_COMM_CLAUSE_RECV,
        GoElementTypes.SELECT_COMM_CLAUSE_SEND
    );

    private final InspectionResult result;
    private Context ctx;

    public GoVariableUsageStatVisitor(InspectionResult result) {
        this.result = result;
    }

    @Override
    public void visitFile(GoFile file) {
        HashMap<String, VariableUsage> global = new HashMap<String, VariableUsage>();
        ctx = new Context(result, global);
        getGlobalVariables(file, global);

        for (GoFunctionDeclaration fd : file.getFunctions()) {
            visitFunctionDeclaration(fd);
        }

        // A global variable could be used in different files even if it's not exported.
        // We cannot reliably check problems on global variables, so we don't check anymore.
        ctx.popLastScopeLevel();
    }

    @Override
    public void visitElement(GoPsiElement element) {
        if (!couldOpenNewScope(element)) {
            super.visitElement(element);
            return;
        }

        ctx.addNewScopeLevel();

        super.visitElement(element);

        for (VariableUsage v : ctx.popLastScopeLevel().values()) {
            if (!v.isUsed()) {
                ctx.unusedVariable(v);
            }
        }
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration declaration) {
        visitIdentifiersAndExpressions(
            declaration.getIdentifiers(), declaration.getExpressions(), false);
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        visitIdentifiersAndExpressions(
            declaration.getIdentifiers(), declaration.getExpressions(), false);
    }

    @Override
    public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
        visitIdentifiersAndExpressions(
            declaration.getIdentifiers(), declaration.getExpressions(), true);
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        getFunctionParameters(declaration);
        visitElement(declaration.getBlock());
        afterVisitGoFunctionDeclaration();
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        getFunctionParameters(declaration);
        visitElement(declaration.getBlock());
        afterVisitGoFunctionDeclaration();
    }

    @Override
    public void visitFunctionLiteral(GoLiteralFunction literal) {
        createFunctionParametersMap(literal.getParameters(), literal
            .getResults());
        visitElement(literal.getBlock());
        afterVisitGoFunctionDeclaration();
    }

    @Override
    public void visitForWithRange(GoForWithRangeStatement statement) {
        ctx.addNewScopeLevel();

        visitExpressionAsIdentifier(statement.getKey(), false);
        visitExpressionAsIdentifier(statement.getValue(), false);

        visitElement(statement.getRangeExpression());
        visitElement(statement.getBlock());

        for (VariableUsage v : ctx.popLastScopeLevel().values()) {
            if (!v.isUsed()) {
                ctx.unusedVariable(v);
            }
        }
    }

    @Override
    public void visitForWithRangeAndVars(GoForWithRangeAndVarsStatement statement) {
        ctx.addNewScopeLevel();

        visitLiteralIdentifier(statement.getKey());
        visitLiteralIdentifier(statement.getValue());

        visitElement(statement.getRangeExpression());
        visitElement(statement.getBlock());

        for (VariableUsage v : ctx.popLastScopeLevel().values()) {
            if (!v.isUsed()) {
                ctx.unusedVariable(v);
            }
        }

    }

    @Override
    public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
        if (needToCollectUsage(identifier)) {
            ctx.addUsage(identifier);
        }
    }

    private void visitIdentifiersAndExpressions(GoLiteralIdentifier[] identifiers, GoExpr[] exprs,
                                                boolean mayRedeclareVariable) {
        if (identifiers.length == 0) {
            return;
        }

        int nonBlankIdCount = 0;
        List<GoLiteralIdentifier> redeclaredIds = new ArrayList<GoLiteralIdentifier>();
        for (GoLiteralIdentifier id : identifiers) {
            if (!id.isBlank()) {
                nonBlankIdCount++;
                if (ctx.isDefinedInCurrentScope(id)) {
                    redeclaredIds.add(id);
                }
            }
        }

        if (mayRedeclareVariable) {
            String msg = "No new variables declared";
            if (nonBlankIdCount == 0) {
                PsiElement start = identifiers[0].getParent();
                ctx.addProblem(start, start, msg, ProblemHighlightType.GENERIC_ERROR, new DeleteStmtFix());
            } else if (redeclaredIds.size() == nonBlankIdCount) {
                PsiElement start = identifiers[0];
                PsiElement end = identifiers[identifiers.length - 1];
                ctx.addProblem(start, end, msg, ProblemHighlightType.GENERIC_ERROR, new ConvertToAssignmentFix());
            }
        } else {
            for (GoLiteralIdentifier redeclaredId : redeclaredIds) {
                String msg = redeclaredId.getText() + " redeclared in this block";
                ctx.addProblem(redeclaredId, redeclaredId, msg, ProblemHighlightType.GENERIC_ERROR);
            }
        }

        for (GoLiteralIdentifier id : identifiers) {
            ctx.addDefinition(id);
        }

        for (GoExpr expr : exprs) {
            visitElement(expr);
        }
    }

    private static boolean couldOpenNewScope(PsiElement element) {
        return element instanceof GoPsiElementBase && isNodeOfType(element, NEW_SCOPE_STATEMENT);

    }

    private void visitExpressionAsIdentifier(GoExpr expr, boolean declaration) {
        if (!(expr instanceof GoLiteralExpression)) {
            return;
        }

        GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
        if ( literal.getType() == GoLiteral.Type.Identifier )
        if (needToCollectUsage((GoLiteralIdentifier)literal)) {
            if (declaration) {
                ctx.addDefinition(literal);
            } else {
                ctx.addUsage(literal);
            }
        }
    }

    private boolean needToCollectUsage(GoLiteralIdentifier id) {
        return id != null && !isFunctionOrMethodCall(id) && !isTypeField(id) && !isType(id) &&
                // if there is any dots in the identifier, it could be from other packages.
                // usage collection of other package variables is not implemented yet.
                !id.getText().contains(".");
    }

    private boolean isType(GoLiteralIdentifier id) {
        PsiElement parent = id.getParent();
        return isNodeOfType(parent, GoElementTypes.BASE_TYPE_NAME) ||
                isNodeOfType(parent, GoElementTypes.REFERENCE_BASE_TYPE_NAME) || parent instanceof GoPsiTypeName;
    }

    private boolean isTypeField(GoLiteralIdentifier id) {
        return id.getParent() instanceof GoTypeStructField || isTypeFieldInitializer(id);
    }

    /**
     * Check whether id is a field name in composite literals
     * @param id GoLiteralIdentifier
     * @return boolean
     */
    private boolean isTypeFieldInitializer(GoLiteralIdentifier id) {
        if (!(id.getParent() instanceof GoLiteral)) {
            return false;
        }

        PsiElement parent = id.getParent().getParent();
        if (parent == null || parent.getNode() == null ||
                parent.getNode().getElementType() != GoElementTypes.LITERAL_COMPOSITE_ELEMENT_KEY) {
            return false;
        }

        PsiElement sibling = parent.getNextSibling();
        return sibling != null && ":".equals(sibling.getText());

    }

    private boolean isFunctionOrMethodCall(GoLiteralIdentifier id) {
        if (!(id.getParent() instanceof GoLiteralExpression)) {
            return false;
        }

        PsiElement grandpa = id.getParent().getParent();
        return grandpa.getNode().getElementType() == GoElementTypes.CALL_OR_CONVERSION_EXPRESSION &&
                id.getParent().isEquivalentTo(grandpa.getFirstChild());
    }

    private void addFunctionParametersToMap(GoFunctionParameter[] parameters,
                                            Map<String, VariableUsage> variables,
                                            boolean ignoreProblem) {
        for (GoFunctionParameter p : parameters) {
            for (GoLiteralIdentifier id : p.getIdentifiers()) {
                variables.put(id.getName(),
                        new VariableUsage(id, ignoreProblem));
            }
        }
    }

    private GoLiteralIdentifier getMethodReceiverIdentifier(
            GoMethodDeclaration md) {
        GoMethodReceiver methodReceiver = md.getMethodReceiver();
        if (methodReceiver == null) {
            return null;
        }

        return methodReceiver.getIdentifier();
    }

    private void getGlobalVariables(GoFile file, HashMap<String, VariableUsage> variables) {
        for (GoConstDeclaration cd : GoFileUtils.getConstDeclarations(file)) {
            visitConstDeclaration(cd);
        }

        for (GoVarDeclaration vd : GoFileUtils.getVarDeclarations(file)) {
            visitVarDeclaration(vd);
        }

        for (GoMethodDeclaration md : file.getMethods()) {
            variables.put(md.getFunctionName(), new VariableUsage(md));
        }

        for (GoFunctionDeclaration fd : file.getFunctions()) {
            variables.put(fd.getFunctionName(), new VariableUsage(fd));
        }

        for (GoTypeSpec spec : GoFileUtils.getTypeSpecs(file)) {
            GoPsiType type = spec.getType();
            if (type != null) {
                variables.put(type.getName(), new VariableUsage(type));
            }
        }
    }


    private void getFunctionParameters(GoFunctionDeclaration fd) {
        Map<String, VariableUsage> variables = createFunctionParametersMap(fd.getParameters(), fd.getResults());

        if (fd instanceof GoMethodDeclaration) {
            // Add method receiver to parameter list
            GoLiteralIdentifier receiver = getMethodReceiverIdentifier((GoMethodDeclaration) fd);
            if (receiver != null) {
                variables.put(receiver.getName(), new VariableUsage(receiver));
            }
        }
    }

    private Map<String, VariableUsage> createFunctionParametersMap(GoFunctionParameter[] parameters,
                                                                   GoFunctionParameter[] results) {
        Map<String, VariableUsage> variables = ctx.addNewScopeLevel();
        addFunctionParametersToMap(parameters, variables, false);

        // Don't detect usage problem on function result
        addFunctionParametersToMap(results, variables, true);
        return variables;
    }


    void afterVisitGoFunctionDeclaration() {
        for (VariableUsage v : ctx.popLastScopeLevel().values()) {
            if (!v.isUsed()) {
                ctx.unusedParameter(v);
            }
        }
    }

    private static class Context {
        public final InspectionResult result;
        public final List<Map<String, VariableUsage>> variables = new ArrayList<Map<String, VariableUsage>>();

        Context(InspectionResult result, Map<String, VariableUsage> global) {
            this.result = result;
            this.variables.add(global);
        }

        public Map<String, VariableUsage> addNewScopeLevel() {
            Map<String, VariableUsage> variables = new HashMap<String, VariableUsage>();
            this.variables.add(variables);
            return variables;
        }

        public Map<String, VariableUsage> popLastScopeLevel() {
            return variables.remove(variables.size() - 1);
        }

        public void unusedVariable(VariableUsage variableUsage) {
            if (variableUsage.isBlank()) {
                return;
            }

            addProblem(variableUsage, "Unused variable",
                       ProblemHighlightType.LIKE_UNUSED_SYMBOL, new RemoveVariableFix());
        }

        public void unusedParameter(VariableUsage variableUsage) {
            if (!variableUsage.isBlank()) {
                addProblem(variableUsage, "Unused parameter",
                           ProblemHighlightType.LIKE_UNUSED_SYMBOL);
            }
        }

        public void unusedGlobalVariable(VariableUsage variableUsage) {
            if (variableUsage.element instanceof GoFunctionDeclaration ||
                variableUsage.element instanceof GoPsiType) {
                return;
            }

            addProblem(variableUsage, "Unused global",
                       ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                       new RemoveVariableFix());
        }

        public void addProblem(VariableUsage variableUsage, String desc,
                                ProblemHighlightType highlightType,
                                LocalQuickFix... fixes) {
            if (!variableUsage.ignoreAnyProblem) {
                result.addProblem(variableUsage.element, desc, highlightType, fixes);
            }
        }

        public void addProblem(PsiElement start, PsiElement end, String desc, ProblemHighlightType type, LocalQuickFix... fixes) {
            result.addProblem(start, end, desc, type, fixes);
        }

        public boolean isDefinedInCurrentScope(GoPsiElement element) {
            boolean isInCase = false;
            PsiElement elem = element;
            while (!(elem instanceof GoFile) && !isInCase) {
                elem = elem.getParent();
                if (elem instanceof GoSwitchTypeClause) {
                    isInCase = true;
                }
            }

            return variables.get(variables.size() - 1).containsKey(element.getText()) && !isInCase;
        }

        public void addDefinition(GoPsiElement element) {
            Map<String, VariableUsage> map = variables.get(variables.size() - 1);
            VariableUsage variableUsage = map.get(element.getText());
            if (variableUsage != null) {
                variableUsage.addUsage(element);
            } else {
                map.put(element.getText(), new VariableUsage(element));
            }
        }

        public void addUsage(GoPsiElement element) {
            for (int i = variables.size() - 1; i >= 0; i--) {
                VariableUsage variableUsage = variables.get(i)
                                                       .get(element.getText());
                if (variableUsage != null) {
                    variableUsage.addUsage(element);
                    return;
                }
            }
        }
    }
}
