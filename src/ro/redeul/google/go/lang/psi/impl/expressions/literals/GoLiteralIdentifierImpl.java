package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import java.util.List;
import javax.swing.*;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.patterns.GoElementPatterns;
import ro.redeul.google.go.lang.psi.resolve.references.BuiltinCallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.CallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.CompositeElementToStructFieldReference;
import ro.redeul.google.go.lang.psi.resolve.references.VarOrConstReference;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static com.intellij.patterns.StandardPatterns.string;
import static ro.redeul.google.go.lang.lexer.GoTokenTypes.mIDENT;
import static ro.redeul.google.go.lang.parser.GoElementTypes.FOR_WITH_CLAUSES_STATEMENT;
import static ro.redeul.google.go.lang.parser.GoElementTypes.FOR_WITH_RANGE_STATEMENT;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getGlobalElementSearchScope;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getLocalElementSearchScope;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:43:49 PM
 */
public class GoLiteralIdentifierImpl extends GoPsiElementBase
    implements GoLiteralIdentifier {

    boolean isIota;

    public GoLiteralIdentifierImpl(@NotNull ASTNode node) {
        this(node, false);
    }

    public GoLiteralIdentifierImpl(@NotNull ASTNode node, boolean isIota) {
        super(node);

        this.isIota = isIota;
    }

    @Override
    public boolean isBlank() {
        return getText().equals("_");
    }

    @Override
    public boolean isIota() {
        return isIota;
    }

    @Override
    public String getValue() {
        return getText();
    }

    @Override
    public Type getType() {
        return Type.Identifier;
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralIdentifier(this);
    }

    @Override
    @NotNull
    public String getName() {
        if ( isQualified() ) {
            return getLocalPackageName() + "." + getUnqualifiedName();
        }

        return getUnqualifiedName();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
        throws IncorrectOperationException {
        return null;
    }

    static final ElementPattern<PsiElement> NO_REFERENCE =
        or(
            psiElement(GoLiteralIdentifier.class)
                .withText(string().matches("nil|print|println")),
            psiElement()
                .withParent(
                    or(
                        psiElement(GoFunctionDeclaration.class),
                        psiElement(GoFunctionParameter.class),
                        psiElement(GoMethodReceiver.class),
                        psiElement(GoTypeStructField.class),
                        psiElement(GoTypeName.class),
                        psiElement(GoLiteralExpression.class)
                            .withParent(
                                or(
                                    psiElement(FOR_WITH_CLAUSES_STATEMENT),
                                    psiElement(FOR_WITH_RANGE_STATEMENT)
//                                    psiElement(BUILTIN_CALL_EXPRESSION)
                                ))
                            .atStartOf(
                                or(
                                    psiElement(FOR_WITH_CLAUSES_STATEMENT),
                                    psiElement(FOR_WITH_RANGE_STATEMENT)
//                                    psiElement(BUILTIN_CALL_EXPRESSION)
                                )
                            )
                    )
                )
        );

    @Override
    public PsiReference getReference() {

        if (NO_REFERENCE.accepts(this))
            return null;

        if (BuiltinCallOrConversionReference.MATCHER.accepts(this))
            return new BuiltinCallOrConversionReference(this);

        if (CallOrConversionReference.MATCHER.accepts(this))
            return new CallOrConversionReference(this);

        if (CompositeElementToStructFieldReference.MATCHER.accepts(this))
            return new CompositeElementToStructFieldReference((GoLiteralCompositeElement)getParent().getParent().getParent());

        if (VarOrConstReference.MATCHER.accepts(this))
            return new VarOrConstReference(this);

        return null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
                return getName();
            }

            public TextAttributesKey getTextAttributesKey() {
                return null;
            }

            public String getLocationString() {
                return String.format(" %s (%s)",
                                     ((GoFile) getContainingFile()).getPackage()
                                                                   .getPackageName(),
                                     getContainingFile().getVirtualFile()
                                         .getPath());
            }

            public Icon getIcon(boolean open) {
                return GoIcons.GO_ICON_16x16;
            }
        };
    }

    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public boolean isQualified() {
        return findChildByType(GoTokenTypes.oDOT) != null;
    }

    @Override
    @NotNull
    public String getUnqualifiedName() {

        List<PsiElement> tokens = findChildrenByType(mIDENT);
        if (tokens.size() == 2 )
            return tokens.get(1).getText();

        return tokens.size() > 0 ? tokens.get(0).getText() : "";
    }

    @Override
    public String getLocalPackageName() {
        if (isQualified()) {
            return findChildrenByType(GoTokenTypes.mIDENT).get(0).getText();
        }

        return null;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return processor.execute(this, state);
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        if (GoElementPatterns.GLOBAL_CONST_DECL.accepts(this) ||
            GoElementPatterns.GLOBAL_VAR_DECL.accepts(this)) {
            return getGlobalElementSearchScope(this, getName());
        }

        return getLocalElementSearchScope(this);
    }
}
