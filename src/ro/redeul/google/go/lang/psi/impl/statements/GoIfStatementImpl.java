package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoIfStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoIfStatementImpl extends GoPsiElementBase implements GoIfStatement {
    public GoIfStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoStatement getSimpleStatement() {
        PsiElement semicolon = findChildByType(GoTokenTypes.oSEMI);
        if (semicolon == null) {
            return null;
        }

        PsiElement stmt = semicolon.getPrevSibling();
        while (stmt != null) {
            if (stmt instanceof GoStatement) {
                return (GoStatement) stmt;
            }
            stmt = stmt.getPrevSibling();
        }

        return null;
    }

    @Override
    public GoExpr getCondition() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public GoBlockStatement getThenClause() {
        return findChildByClass(GoBlockStatement.class);
    }

    @Override
    public GoStatement getElseClause() {
        PsiElement elseLiteral = findChildByType(GoTokenTypes.kELSE);
        if (elseLiteral == null) {
            return null;
        }

        PsiElement stmt = elseLiteral.getNextSibling();
        while (stmt != null) {
            if (stmt instanceof GoBlockStatement || stmt instanceof GoIfStatement) {
                return (GoStatement) stmt;
            }

            stmt = stmt.getNextSibling();
        }
        return null;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitIfStatement(this);
    }
}
