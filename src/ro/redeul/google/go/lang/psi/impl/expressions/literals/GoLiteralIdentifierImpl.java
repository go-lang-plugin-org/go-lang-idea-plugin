package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoElementType;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.expressions.primary.GoLiteralExpressionImpl;
import ro.redeul.google.go.lang.psi.patterns.GoElementPatterns;
import ro.redeul.google.go.lang.psi.resolve.Reference;
import ro.redeul.google.go.lang.psi.resolve.refs.*;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getAs;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getGlobalElementSearchScope;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getLocalElementSearchScope;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:43:49 PM
 */
public class GoLiteralIdentifierImpl extends GoPsiElementBase implements GoLiteralIdentifier {

    public static final PsiElementPattern.Capture<PsiElement> INSIDE_COMPOSITE_KEY =
            psiElement()
                    .withParent(
                            psiElement(GoLiteralExpressionImpl.class)
                                    .withParent(psiElement(GoElementTypes.LITERAL_COMPOSITE_ELEMENT_KEY))
                    );

    public static final PsiElementPattern.Capture<PsiElement> SELECTOR_MATCHER =
            psiElement()
                    .withParent(psiElement(GoSelectorExpression.class));

    private final boolean isIota;
    private BigInteger iotaValue;

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
    public boolean isIota() { return isIota; }

    @Override
    public boolean isNil() { return getText().equals("nil"); }

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
        return getText();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        return null;
    }

    @NotNull
    @Override
    public PsiReference[] defineReferences() {

//        if (LabelReference.MATCHER.accepts(this))
//            return refs(new LabelReference(this));

//        if (ShortVarDeclarationReference.MATCHER.accepts(this))
//            return refs(new ShortVarDeclarationReference(this));

        if (INSIDE_COMPOSITE_KEY.accepts(this)) {
            GoLiteralCompositeValue compositeValue = GoPsiUtils.findParentOfType(this, GoLiteralCompositeValue.class);

            if (compositeValue == null)
                return PsiReference.EMPTY_ARRAY;

            final GoLiteralIdentifier identifier = this;

            compositeValue.getType();

            GoType enclosingType = compositeValue.getType();
            List<Reference> references = enclosingType.underlyingType().accept(
                    new UpdatingTypeVisitor<List<Reference>>() {
                        @Override
                        public void visitStruct(GoTypeStruct type, List<Reference> data, TypeVisitor<List<Reference>> visitor) {
                            data.add(new StructFieldReference(identifier, type));
                        }

                        @Override
                        public void visitMap(GoTypeMap type, List<Reference> data, TypeVisitor<List<Reference>> visitor) {
                            data.add(new TypedConstReference(identifier, type.getKeyType()));
                        }
                    }, new ArrayList<Reference>()
            );

            return references.toArray(new PsiReference[references.size()]);
        }

        if (FunctionOrTypeNameReference.MATCHER.accepts(this))
            return new PsiReference[]{new FunctionOrTypeNameReference(this)};

        if (ShortVarReference.SHORT_VAR_DECLARATION.accepts(this))
            return new PsiReference[]{new ShortVarReference(this)};

        if (TypeNameReference.MATCHER.accepts(this)) {
            GoPsiTypeName typeName = (GoPsiTypeName) getParent();

            if ( typeName.isQualified() ) {
                if ( typeName.getQualifier() == this )
                    return new PsiReference[]{new PackageReference(this)};

                if ( typeName.getIdentifier() == this ) {
                    GoLiteralIdentifier packageQualifier = typeName.getQualifier();

                    GoImportDeclaration importDeclaration = getAs(GoImportDeclaration.class, resolveSafely(packageQualifier));

                    if (importDeclaration != null && importDeclaration.getPackage() != GoPackages.C)
                        return new PsiReference[]{new TypeNameReference(this, importDeclaration.getPackage())};


                    return PsiReference.EMPTY_ARRAY;
                }
            }

            return new PsiReference[] { new TypeNameReference(this) };
        }

        if (SELECTOR_MATCHER.accepts(this)) {
            GoSelectorExpression selectorExpression = (GoSelectorExpression) getParent();

            List<Reference> references = new ArrayList<Reference>();
            GoType baseTypes[] = selectorExpression.getBaseExpression().getType();
            if (baseTypes.length >= 1 && baseTypes[0] != null)
                references = baseTypes[0].accept(
                        new UpdatingTypeVisitor<List<Reference>>() {
                            final GoLiteralIdentifier ident = GoLiteralIdentifierImpl.this;

                            @Override
                            public void visitPointer(GoTypePointer type, List<Reference> data, TypeVisitor<List<Reference>> visitor) {
                                type.getTargetType().accept(visitor);
                            }

                            @Override
                            public void visitPackage(GoTypePackage type, List<Reference> data, TypeVisitor<List<Reference>> visitor) {
                                GoPackage goPackage = type.getPackage();
                                if (goPackage != GoPackages.C)
                                    data.add(new PackageSymbolReference(ident, goPackage));
                            }

                            @Override
                            public void visitName(GoTypeName type, List<Reference> data, TypeVisitor<List<Reference>> visitor) {
                                data.add(new InterfaceMethodReference(ident, type));
                                data.add(new MethodReference(ident, type));

                                // HACK: I should not have to do this here
                                if (type != type.underlyingType() && !(type.underlyingType() instanceof GoTypeName))
                                    type.underlyingType().accept(visitor);
                            }

                            @Override
                            public void visitPrimitive(GoTypePrimitive type, List<Reference> data, TypeVisitor<List<Reference>> visitor) {
                                if ( type.getType() == GoTypes.Builtin.Error )
                                    data.add(new InterfaceMethodReference(ident, type));

                                data.add(new MethodReference(ident, type));
                            }

                            @Override
                            public void visitStruct(GoTypeStruct type, List<Reference> data, TypeVisitor<List<Reference>> visitor) {
                                data.add(new StructFieldReference(ident, type));
                            }

                            @Override
                            public void visitConstant(GoTypeConstant type, List<Reference> data, TypeVisitor<List<Reference>> visitor) {
                                type.getType().accept(visitor);
                            }

                        }, new ArrayList<Reference>()
                );

            return references.toArray(new PsiReference[references.size()]);
        }

        if (VarOrConstReference.MATCHER.accepts(this))
            return new PsiReference[]{new VarOrConstReference(this), new PackageReference(this)};


//        if (NIL_TYPE.accepts(this))
//            return PsiReference.EMPTY_ARRAY;
//
//        GoPackage goPackage = null;
//        if (findChildByType(GoTokenTypes.oDOT) != null) {
//            GoLiteralIdentifier identifiers[] = findChildrenByClass(GoLiteralIdentifier.class);
//            GoImportDeclaration importDeclaration = GoPsiUtils.resolveSafely(identifiers[0], GoImportDeclaration.class);
//            goPackage = importDeclaration != null ? importDeclaration.getPackage() : null;
//        }
//
//        if ( goPackage != null && goPackage == GoPackages.C )
//            return PsiReference.EMPTY_ARRAY;
//
//        return new PsiReference[] { goPackage != null ? new TypeNameReference(this, goPackage) : new TypeNameReference(this) };
//

        return PsiReference.EMPTY_ARRAY;
    }

    @Override
    public PsiElement getNameIdentifier() {
        return this;
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
        iotaValue = BigInteger.valueOf(value);
    }

    @Override
    public BigInteger getIotaValue() {
        return iotaValue;
    }
}
