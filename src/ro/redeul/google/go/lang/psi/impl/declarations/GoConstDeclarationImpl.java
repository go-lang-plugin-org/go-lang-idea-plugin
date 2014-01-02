package ro.redeul.google.go.lang.psi.impl.declarations;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
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
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypePsiBacked;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.LinkedList;
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

    private GoPsiType getType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public GoPsiType getIdentifiersType() {
        GoPsiType types = findChildByClass(GoPsiType.class);
        PsiElement parent = getParent();
        if (types == null && parent instanceof GoConstDeclarations) {
            for (GoConstDeclaration declaration : ((GoConstDeclarations) parent).getDeclarations()) {
                if (declaration != this) {
                    types = ((GoConstDeclarationImpl) declaration).getType();
                    if (types != null)
                        return types;
                }
            }
        }
        if (types == null) {
            for (GoExpr goExpr : getExpressions()) {
                for (GoType goType : goExpr.getType()) {
                    if (goType instanceof GoTypePsiBacked)
                        return ((GoTypePsiBacked) goType).getPsiType();
                }
            }
        }
        return types;
    }

    @Override
    @NotNull
    public GoExpr[] getExpressions() {
        return findChildrenByClass(GoExpr.class);
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
