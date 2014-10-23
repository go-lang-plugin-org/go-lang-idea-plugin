package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.expressions.primary.GoLiteralExpressionImpl;
import ro.redeul.google.go.lang.psi.patterns.GoElementPatterns;
import ro.redeul.google.go.lang.psi.resolve.Reference;
import ro.redeul.google.go.lang.psi.resolve.refs.*;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
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
public class GoLiteralIdentifierImpl extends GoPsiElementBase implements GoLiteralIdentifier {

    private final boolean isIota;
    private Integer iotaValue;

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

    @NotNull
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
        if (isQualified()) {
            return getLocalPackageName() + "." + getUnqualifiedName();
        }

        return getUnqualifiedName();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        return null;
    }

    @SuppressWarnings("unchecked")
    private static final ElementPattern<PsiElement> NO_REFERENCE =
            or(
                    psiElement(GoLiteralIdentifier.class)
                            .withText(string().matches("nil")),
                    psiElement()
                            .withParent(
                                    or(
                                            psiElement(GoFunctionDeclaration.class),
                                            psiElement(GoFunctionParameter.class),
                                            psiElement(GoMethodReceiver.class),
                                            psiElement(GoTypeStructField.class),
                                            psiElement(GoPsiTypeName.class),
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

    //    @NotNull
//    @Override
//    public PsiReference[] getReferences() {
//        if (NO_REFERENCE.accepts(this))
//            return PsiReference.EMPTY_ARRAY;
//
//        return new PsiReference[] {
//            new BuiltinCallOrConversionReference(this),
//            new CallOrConversionReference(this),
//            new VarOrConstReference(this)
//        }
//
//        return super.getReferences();    //To change body of overridden methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public PsiReference getReference() {
//
//        if (NO_REFERENCE.accepts(this))
//            return null;
//
//        if (BuiltinCallOrConversionReference.MATCHER.accepts(this))
//            return new BuiltinCallOrConversionReference(this);
//
//        if (CallOrConversionReference.MATCHER.accepts(this))
//            return new CallOrConversionReference(this);
//
//        if (CompositeElementOfStructFieldReference.MATCHER.accepts(this))
//            return new CompositeElementOfStructFieldReference((GoLiteralCompositeElement)getParent().getParent().getParent());
//
//        if (VarOrConstReference.MATCHER.accepts(this))
//            return new VarOrConstReference(this);
//
//        return null;
//    }
//

    @NotNull
    @Override
    public PsiReference[] defineReferences() {
//        if (NO_REFERENCE.accepts(this))
//            return PsiReference.EMPTY_ARRAY;

//        if (BuiltinCallOrConversionReference.MATCHER.accepts(this))
//            return refs(new BuiltinCallOrConversionReference(this));

//        if (LabelReference.MATCHER.accepts(this))
//            return refs(new LabelReference(this));

//        if (CompositeElementOfStructFieldReference.MATCHER_KEY.accepts(this)) {
//            GoLiteralComposite composite = findParentOfType(this, GoLiteralComposite.class);
//            if (resolveToFinalType(composite.getLiteralType()) instanceof GoPsiTypeStruct) {
//                return refs(
//                        new CompositeElementOfStructFieldReference(this, this)
//                );
//            }
//
//            return refs(
//                    new CompositeElementOfStructFieldReference(this, this),
//                    new VarOrConstReference(this)
//            );
//        }

//        if (CompositeElementOfStructFieldReference.MATCHER_ELEMENT.accepts(this))
//            return refs(new VarOrConstReference(this));

//        if (ShortVarDeclarationReference.MATCHER.accepts(this))
//            return refs(new ShortVarDeclarationReference(this));

        if (psiElement().withParent(
                psiElement(GoLiteralExpressionImpl.class).withParent(
                        psiElement(GoElementTypes.LITERAL_COMPOSITE_ELEMENT_KEY))
        ).accepts(this)) {
            GoLiteralCompositeElement parent = GoPsiUtils.findParentOfType(this, GoLiteralCompositeElement.class);

            if (parent == null)
                return PsiReference.EMPTY_ARRAY;

            GoTypeStruct typeStruct = GoTypes.resolveToStruct(parent.getElementType());
            if (typeStruct == null)
                return PsiReference.EMPTY_ARRAY;

            return new PsiReference[]{new StructFieldReference(this, typeStruct)};
        }

        //noinspection unchecked
        if (psiElement()
                .withParent(
                        psiElement(GoLiteralExpression.class)
                                .atStartOf(
                                        or(
                                                psiElement(GoBuiltinCallExpression.class),
                                                psiElement(GoCallOrConvExpression.class))))
                .withSuperParent(2,
                        or(
                                psiElement(GoBuiltinCallExpression.class),
                                psiElement(GoCallOrConvExpression.class))
                ).accepts(this)) {
            return new PsiReference[]{new FunctionOrTypeNameReference(this)};
        }

        if (psiElement().withParent(psiElement(GoShortVarDeclaration.class)).accepts(this)) {
            return new PsiReference[]{new ShortVarReference(this)};
        }

        if (psiElement().withParent(psiElement(GoSelectorExpression.class)).accepts(this)) {
            GoSelectorExpression selectorExpression = (GoSelectorExpression) getParent();

            GoType baseTypes[] = selectorExpression.getBaseExpression().getType();

            return GoTypes.visitFirstType(baseTypes, new GoType.Visitor<PsiReference[]>(PsiReference.EMPTY_ARRAY) {
                final GoLiteralIdentifier ident = GoLiteralIdentifierImpl.this;

                List<Reference> refs = new ArrayList<Reference>();

                @Override
                public PsiReference[] getData() {
                    return refs.toArray(new PsiReference[refs.size()]);
                }

                @Override
                public void visitPointer(GoTypePointer type) {
                    visit(type.getTargetType());
                }

                @Override
                public void visitPackage(GoTypePackage type) {
                    refs.add(new PackageSymbolReference(ident, type.getPackage()));
                }

                @Override
                public void visitName(final GoTypeName type) {
                    refs.add(new InterfaceMethodReference(ident, type));
                    refs.add(new MethodReference(ident, type));

                    visit(type.getDefinition());
                }

                @Override
                public void visitStruct(GoTypeStruct type) {
                    refs.add(new StructFieldReference(ident, type));
                }
            });
        }

        if (VarOrConstReference.MATCHER.accepts(this)) {
//            if (PackageReference.MATCHER.accepts(this))
                return new PsiReference[]{new VarOrConstReference(this), new PackageReference(this)};
//            else
//                return new PsiReference[]{new VarOrConstReference(this)};
        }

        if (psiElement(GoLiteralIdentifier.class).insideStarting(psiElement(GoPsiTypeName.class)).accepts(this)) {
            return new PsiReference[]{new PackageReference(this)};
        }

        return PsiReference.EMPTY_ARRAY;
    }

//    @Override
//    public ItemPresentation getPresentation() {
//        return new ItemPresentation() {
//            public String getPresentableText() {
//                return getName();
//            }
//
//            public TextAttributesKey getTextAttributesKey() {
//                return null;
//            }
//
//            public String getLocationString() {
//                return String.format(" %s (%s)",
//                                     ((GoFile) getContainingFile()).getPackage()
//                                                                   .getPackageName(),
//                                     getContainingFile().getVirtualFile()
//                                         .getPath());
//            }
//
//            public Icon getIcon(boolean open) {
//                return GoIcons.GO_ICON_16x16;
//            }
//        };
//    }

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
        if (tokens.size() == 2)
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

    @NotNull
    @Override
    public String getCanonicalName() {
        if (!isQualified())
            return getUnqualifiedName();

        String packageName = getLocalPackageName();
        if (packageName == null) {
            packageName = "";
        }

        PsiFile file = getContainingFile();
        if (file == null || !(file instanceof GoFile)) {
            return getUnqualifiedName();
        }
        GoFile goFile = (GoFile) file;

        GoImportDeclarations[] goImportDeclarations = goFile.getImportDeclarations();
        for (GoImportDeclarations importDeclarations : goImportDeclarations) {
            for (GoImportDeclaration importDeclaration : importDeclarations.getDeclarations()) {
                if (importDeclaration.getPackageAlias().toLowerCase()
                        .equals(packageName.toLowerCase())) {
                    GoLiteralString importPath = importDeclaration.getImportPath();
                    if (importPath != null)
                        return String.format("%s:%s",
                                importPath.getValue(),
                                getUnqualifiedName());
                }
            }
        }

        return getName();
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
                GoElementPatterns.GLOBAL_VAR_DECL.accepts(this) ||
                GoElementPatterns.FUNCTION_DECLARATION.accepts(this) ||
                GoElementPatterns.METHOD_DECLARATION.accepts(this)) {
            return getGlobalElementSearchScope(this, getName());
        }

//        if (isNodeOfType(getParent(), GoElementTypes.LABELED_STATEMENT) ||
//                LabelReference.MATCHER.accepts(this)) {
//            return new LocalSearchScope(
//                    findParentOfType(this, GoFunctionDeclaration.class));
//        }

        return getLocalElementSearchScope(this);
    }

    @Override
    public void setIotaValue(int value) {
        iotaValue = value;
    }

    @Override
    public Integer getIotaValue() {
        if (isIota()) {
            return iotaValue;
        }
        return null;
    }
}
