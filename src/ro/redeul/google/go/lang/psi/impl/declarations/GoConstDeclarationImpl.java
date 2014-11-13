package ro.redeul.google.go.lang.psi.impl.declarations;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.Collections;
import java.util.List;

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
        GoExpr goExprs[] = findChildrenByClass(GoExpr.class);
//        if (goExprs.length == 0 && !hasInitializers()) {
//
//            // Omitting the list of expressions is therefore equivalent to repeating the previous list
//            // So we will find the previous list and clone it.
//
//            List<GoExpr> expressions = Collections.emptyList();
//            PsiElement sibling = this.getPrevSibling();
//            while ( sibling != null && expressions.size() == 0) {
//                while ( sibling != null && !(sibling instanceof GoConstDeclaration))
//                    sibling = sibling.getPrevSibling();
//
//                if ( sibling != null) {
//                    expressions = GoPsiUtils.findChildrenOfType(sibling, GoExpr.class);
//                    sibling = sibling.getPrevSibling();
//                }
//            }
//
//            goExprs = new GoExpr[expressions.size()];
//            for (int i = 0; i < expressions.size(); i++) {
//                GoExpr expression = expressions.get(i);
//                goExprs[i] = (GoExpr) expression.copy();
//            }
//        }

        for (GoExpr expr : goExprs) {
            setIotaValue(expr, getConstSpecIndex());
        }

        return goExprs;
    }

    private void setIotaValue(GoExpr expr, final Integer iotaValue) {
        expr.accept(new GoRecursiveElementVisitor() {
            @Override
            public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
                if (identifier.isIota())
                    identifier.setIotaValue(iotaValue);
            }
        });
    }

    @Override
    public GoExpr getExpression(GoLiteralIdentifier identifier) {
        Integer identifierIndex = getIdentifierIndex(identifier);
        if (identifierIndex == null ) {
            return null;
        }

        GoExpr[] goExprs = getExpressions();
        if (goExprs.length == 0 && !hasInitializers()) {
            // Omitting the list of expressions is therefore equivalent to repeating the previous list
            // So we will find the previous list and clone it.
            List<GoExpr> expressions = Collections.emptyList();
            PsiElement sibling = this.getPrevSibling();
            while ( sibling != null && expressions.size() == 0) {
                while ( sibling != null && !(sibling instanceof GoConstDeclaration))
                    sibling = sibling.getPrevSibling();

                if ( sibling != null) {
                    expressions = GoPsiUtils.findChildrenOfType(sibling, GoExpr.class);
                    sibling = sibling.getPrevSibling();
                }
            }

            goExprs = new GoExpr[expressions.size()];
            Integer iotaValue = getConstSpecIndex();
            for (int i = 0; i < expressions.size(); i++) {
                GoExpr expression = expressions.get(i);
                goExprs[i] = (GoExpr) expression.copy();
                setIotaValue(goExprs[i], iotaValue);
            }
        }

        if (goExprs.length <= identifierIndex)
            return null;

        return goExprs[identifierIndex];
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
        return processor.execute(this, state);
    }
}
