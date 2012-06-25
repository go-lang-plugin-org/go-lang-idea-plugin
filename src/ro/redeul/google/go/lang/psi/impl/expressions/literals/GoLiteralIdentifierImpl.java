package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import javax.swing.*;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.resolve.references.CallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.VarOrConstReference;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static ro.redeul.google.go.lang.parser.GoElementTypes.BUILTIN_CALL_EXPRESSION;
import static ro.redeul.google.go.lang.parser.GoElementTypes.FOR_WITH_CLAUSES_STATEMENT;
import static ro.redeul.google.go.lang.parser.GoElementTypes.FOR_WITH_RANGE_STATEMENT;

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

//    @Override
//    public PsiElement getElement() {
//        return this;
//    }
//
//    @Override
//    public TextRange getRangeInElement() {
//        return new TextRange(0, getTextLength());
//    }

//    @Override
//    public PsiElement resolve() {
//        VarOrConstResolver varOrConstResolver =
//            new VarOrConstResolver(this);
//
//        PsiScopesUtil.treeWalkUp(
//            varOrConstResolver,
//            this, this.getContainingFile(),
//            GoResolveStates.initial());
//
//        return varOrConstResolver.reference();
//    }
//
//    @NotNull
//    @Override
//    public String getCanonicalText() {
//        return getText();
//    }

    @Override
    @NotNull
    public String getName() {
        return getText();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
        throws IncorrectOperationException {
        return null;
    }

//    @Override
//    public PsiElement handleElementRename(String newElementName)
//        throws IncorrectOperationException {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }

//    @Override
//    public PsiElement bindToElement(@NotNull PsiElement element)
//        throws IncorrectOperationException {
//        if (isReferenceTo(element))
//            return this;
//
//        throw new IncorrectOperationException(
//            "Cannot bind to:" + element + " of class " + element.getClass());
//    }
//
//    @Override
//    public boolean isReferenceTo(PsiElement element) {
//        return true;
//    }

    static final ElementPattern<PsiElement> NO_REFERENCE =
        psiElement()
            .withParent(
                or(
                    psiElement(GoFunctionDeclaration.class),
                    psiElement(GoFunctionParameter.class),
                    psiElement(GoMethodReceiver.class),
                    psiElement(GoTypeStructField.class),
                    psiElement(GoTypeName.class),
                    psiElement(GoLiteralExpression.class)
                        .insideStarting(
                            or(
                                psiElement(FOR_WITH_CLAUSES_STATEMENT),
                                psiElement(FOR_WITH_RANGE_STATEMENT),
                                psiElement(BUILTIN_CALL_EXPRESSION)
                            )
                        )
                )
            );

    @Override
    public PsiReference getReference() {

        if ( NO_REFERENCE.accepts(this) )
            return null;

        if (CallOrConversionReference.MATCHER.accepts(this) )
            return new CallOrConversionReference(this);

        if (VarOrConstReference.MATCHER.accepts(this))
            return new VarOrConstReference(this);

        return null;
    }

/*
    @NotNull
    @Override
    public Object[] getVariants() {

        if (GoPsiUtils.isNodeOfType(getParent(),
                                    GoTokenSets.NO_IDENTIFIER_COMPLETION_PARENTS)) {
            return PsiReference.EMPTY_ARRAY;
        }

        IdentifierVariantsCollector identifierVariantsCollector = new IdentifierVariantsCollector();

        PsiScopesUtil.treeWalkUp(identifierVariantsCollector,
                                 this.getOriginalElement(),
                                 this.getContainingFile(),
                                 GoResolveStates.initial());

        return identifierVariantsCollector.references();
    }

*/
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
    public boolean isGlobal() {
/*
        PsiElement resolve = resolve();
        if (resolve == null) {
            return false;
        }

        PsiElement parent = resolve.getParent();
        if (!(parent instanceof GoVarDeclaration) && !(parent instanceof GoConstDeclaration)) {
            return false;
        }

        PsiElement grandpa = parent.getParent();
        return grandpa != null && grandpa.getParent() instanceof GoFile;

*/
        return false;
    }

    @Override
    public boolean isQualified() {
        return findChildByType(GoTokenTypes.oDOT) != null;
    }

    @Override
    @NotNull
    public String getUnqualifiedName() {
        String name = getName();
        int pos = name.lastIndexOf('.');
        return pos < 0 ? name : name.substring(pos + 1);
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
}
