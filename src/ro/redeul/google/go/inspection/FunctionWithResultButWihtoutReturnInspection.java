package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.AddReturnStmtFix;
import ro.redeul.google.go.inspection.fix.RemoveFunctionResultFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.statements.*;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClause;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionClause;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeClause;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

public class FunctionWithResultButWihtoutReturnInspection extends AbstractWholeGoFileInspection
{
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

            @Override
            public void visitFunctionLiteral(GoLiteralFunction literal) {
                checkFunction(result, literal);
            }
        }.visitFile(file);
    }

    public static void checkFunction(InspectionResult result, GoFunctionDeclaration function) {
        hasResultButNoReturnAtTheEnd(result, function);
    }

    private static void hasResultButNoReturnAtTheEnd(InspectionResult result, GoFunctionDeclaration function) {
        if (hasResult(function) && hasBody(function) && !hasReturnAtTheEnd(function)) {
            LocalQuickFix fix1 = new AddReturnStmtFix(function);
            LocalQuickFix fix2 = new RemoveFunctionResultFix(function);
            PsiElement element = function.getBlock().getLastChild();
            result.addProblem(element, GoBundle.message("error.no.return.found"), fix1, fix2);
        }
    }

    private static boolean hasResult(GoFunctionDeclaration function) {
        return function.getResults().length > 0;
    }

    private static boolean hasBody(GoFunctionDeclaration function) {
        return function.getBlock() != null;
    }

    private static boolean hasReturnAtTheEnd(GoFunctionDeclaration function) {
        return isTerminating(function.getBlock());
    }

    private static boolean isPanicCall(PsiElement element) {
        if (!(element instanceof GoExpressionStatement)) {
            return false;
        }

        PsiElement call = element.getFirstChild();
        if (!(call instanceof GoBuiltinCallExpression)) {
            return false;
        }

        GoPrimaryExpression expression = ((GoBuiltinCallExpression) call).getBaseExpression();
        return expression != null && "panic".equals(expression.getText());
    }

    private static boolean isTerminating(GoStatement statement) {
        if (statement instanceof GoReturnStatement) {
            return true;
        }else if (statement instanceof GoGoStatement){
            return true;
        }else if (isPanicCall(statement)) {
            return true;
        }else if (statement instanceof GoBlockStatement) {
            GoBlockStatement block = (GoBlockStatement)statement;
            GoStatement[] statements = block.getStatements();
            return statements.length > 0 && isTerminating(statements[statements.length - 1]);
        }else if (statement instanceof GoIfStatement) {
            GoIfStatement ifStatement = (GoIfStatement)(statement);
            if (!isTerminating(ifStatement.getThenBlock())){
                return false;
            }else if (ifStatement.getElseIfStatement() != null){
                return isTerminating(ifStatement.getElseIfStatement());
            }else if (ifStatement.getElseBlock() != null) {
                return isTerminating(ifStatement.getElseBlock());
            }else {
                return false;
            }
        }else if(statement instanceof GoForWithConditionStatement){
            GoForWithConditionStatement forStatement = (GoForWithConditionStatement)statement;
            return forStatement.getCondition() == null;
        }else if(statement instanceof GoForWithClausesStatement){
            GoForWithClausesStatement forStatement = (GoForWithClausesStatement)statement;
            return forStatement.getCondition() == null;
        }else if (statement instanceof GoSwitchExpressionStatement){
            GoSwitchExpressionStatement switchExpr = (GoSwitchExpressionStatement)statement;
            boolean hasDefalut = false;
            for (GoSwitchExpressionClause clause: switchExpr.getClauses()) {
                if (clause.isDefault()) {
                    hasDefalut = true;
                }
                GoStatement[] statmentsInClause = clause.getStatements();

                if (statmentsInClause.length == 0) {
                    return false;
                }

                if (!isTerminating(statmentsInClause[statmentsInClause.length-1])){
                    return false;
                }
            }
            return hasDefalut;
        }else if (statement instanceof GoSwitchTypeStatement){
            GoSwitchTypeStatement switchTypeStatement = (GoSwitchTypeStatement)statement;
            boolean hasDefalut = false;
            for (GoSwitchTypeClause clause: switchTypeStatement.getClauses()) {
                if (clause.isDefault()) {
                    hasDefalut = true;
                }
                GoStatement[] statmentsInClause = clause.getStatements();

                if (statmentsInClause.length == 0) {
                    return false;
                }

                if (!isTerminating(statmentsInClause[statmentsInClause.length-1])){
                    return false;
                }
            }
            return hasDefalut;
        }else if (statement instanceof GoSelectStatement){
            GoSelectStatement selectStatement = (GoSelectStatement)statement;
            for (GoSelectCommClause clause : selectStatement.getCommClauses()) {
                GoStatement[] statmentsInClause = clause.getStatements();
                if (statmentsInClause.length == 0) {
                    return false;
                }
                if (!isTerminating(statmentsInClause[statmentsInClause.length-1])){
                    return false;
                }
            }
            return true;
        }else if (statement instanceof GoLabeledStatement){
            GoLabeledStatement labeledStatement = (GoLabeledStatement)statement;
            return isTerminating (labeledStatement.getStatement());
        }else{
            return false;
        }
    }

}
