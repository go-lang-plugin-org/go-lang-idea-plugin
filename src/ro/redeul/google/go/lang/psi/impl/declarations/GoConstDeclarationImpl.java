package ro.redeul.google.go.lang.psi.impl.declarations;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoConstDeclarationImpl extends GoPsiElementBase
        implements GoConstDeclaration {

    public GoConstDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean hasInitializers() {
        return findChildByType(GoTokenTypes.oASSIGN) != null;
    }

    @Override
    public GoLiteralIdentifier[] getIdentifiers() {
        return findChildrenByClass(GoLiteralIdentifier.class);
    }

    private Integer getIdentifierIndex(GoLiteralIdentifier identifier){
        GoLiteralIdentifier[] goIdentifiers = getIdentifiers();
        for (int i = 0; i < goIdentifiers.length; i++) {
            if (goIdentifiers[i] == identifier){
                return i;
            }
        }
        return null;
    }

    @Override
    public Integer getConstSpecIndex() {
        PsiElement parent = getParent();
        if (parent instanceof GoConstDeclarations) {
            GoConstDeclaration[] consSpecs = ((GoConstDeclarations) parent).getDeclarations();
            for (int i = 0; i < consSpecs.length; i++) {
                if (consSpecs[i] == this) {
                    return i;
                }
            }
        }
        return null;
    }

    private GoPsiType getType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public GoPsiType getIdentifiersType() {
        GoPsiType type = findChildByClass(GoPsiType.class);

        if ( type != null )
            return type;

        PsiElement declaration = this.getPrevSibling();

        while (declaration != null && !(declaration instanceof GoConstDeclaration))
            declaration = declaration.getPrevSibling();

        return declaration != null ? ((GoConstDeclaration)declaration).getIdentifiersType() : null;
    }

    @Override
    @NotNull
    public GoExpr[] getExpressions() {
        GoExpr[] goExprs = findChildrenByClass(GoExpr.class);
        if (goExprs.length == 0 && !hasInitializers()) {
            // Omitting the list of expressions is therefore equivalent to repeating the previous list
            PsiElement parent = getParent();
            if (parent instanceof GoConstDeclarations) {
                GoConstDeclaration previous = null;
                for (GoConstDeclaration declaration : ((GoConstDeclarations) parent).getDeclarations()) {
                    if (declaration != this) {
                        previous = declaration;
                    }
                    // return previous ConstSpec type
                    if (declaration == this) {
                        if (previous != null){
                            return previous.getExpressions();
                        }
                    }
                }
            }
        }
        return goExprs;
    }

    private void setIotaValue(GoExpr expr, Integer iotaValue) {
        if (expr instanceof GoLiteralExpression){
            GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
            if (literal instanceof GoLiteralIdentifier){
                if (((GoLiteralIdentifier) literal).isIota()){
                   ((GoLiteralIdentifier) literal).setIotaValue(iotaValue);
                }
            }
        }
        if (expr instanceof GoUnaryExpression){
            setIotaValue(((GoUnaryExpression) expr).getExpression(), iotaValue);
        }
        if (expr instanceof GoBinaryExpression){
            setIotaValue(((GoBinaryExpression) expr).getLeftOperand(), iotaValue);
            setIotaValue(((GoBinaryExpression) expr).getRightOperand(), iotaValue);
        }
        if (expr instanceof GoParenthesisedExpression){
            setIotaValue(((GoParenthesisedExpression) expr).getInnerExpression(), iotaValue);
        }
    }

    @Override
    public GoExpr getExpression(GoLiteralIdentifier identifier) {
        Integer identifierIndex = getIdentifierIndex(identifier);
        if (identifierIndex == null){
            return null;
        }
        if (hasInitializers()){
            GoExpr[] goExprs = getExpressions();
            if (goExprs.length <= identifierIndex)
                return null;
            GoExpr expr = goExprs[identifierIndex];
            setIotaValue(expr, getConstSpecIndex());
            return expr;
        } else {
            PsiElement goConstDecls = getParent();
            if (goConstDecls instanceof GoConstDeclarations){
                PsiElement[] goConstSpecs = goConstDecls.getChildren();
                for (int i = 1; i < goConstSpecs.length; i++) {
                    if (goConstSpecs[i] == this && goConstSpecs[i - 1] instanceof GoConstDeclaration) {
                        GoConstDeclaration prevConstSpec = (GoConstDeclaration) goConstSpecs[i - 1];
                        GoExpr prevExpr = prevConstSpec.getExpression(prevConstSpec.getIdentifiers()[identifierIndex]);
                        if (prevExpr != null){
                            // copy expression from previous constant specification and set necessary iota value
                            GoExpr expr = (GoExpr) prevExpr.copy();
                            setIotaValue(expr, getConstSpecIndex());
                            return expr;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitConstDeclaration(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        //noinspection ConstantConditions
//        if ( !GoNamesUtil.isExported(getName()) && ! ResolveStates.get(state, ResolveStates.Key.JustExports) )
//            return true;
        return processor.execute(this, state);
    }
}
