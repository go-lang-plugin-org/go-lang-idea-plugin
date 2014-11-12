package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoLiteralStringImpl extends GoPsiElementBase
    implements GoLiteralString
{
    public GoLiteralStringImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public String getValue() {
        return GoPsiUtils.getStringLiteralValue(getText());
    }

    @Override
    public Type getType() {
        return getText().startsWith("`")
            ? Type.RawString : Type.InterpretedString;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralString(this);
    }

    //    @Override
//    protected PsiReference[] defineReferences() {
//        if ( PlatformPatterns.psiElement().withParent(GoImportDeclaration.class).accepts(this) )
//            return new PsiReference[] { new ImportReference(this)};
//        return super.defineReferences();
//    }
}
