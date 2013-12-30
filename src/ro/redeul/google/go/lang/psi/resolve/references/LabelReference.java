package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.statements.GoLabeledStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static ro.redeul.google.go.lang.parser.GoElementTypes.*;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class LabelReference
    extends GoPsiReference.Single<GoLiteralIdentifier, LabelReference> {

    @SuppressWarnings("unchecked")
    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            or(
                                    psiElement(GOTO_STATEMENT),
                                    psiElement(BREAK_STATEMENT),
                                    psiElement(CONTINUE_STATEMENT)
                            )
                    );

    private static final ResolveCache.AbstractResolver<LabelReference, GoResolveResult> RESOLVER =
            new ResolveCache.AbstractResolver<LabelReference, GoResolveResult>() {
                @Override
                public GoResolveResult resolve(@NotNull LabelReference labelReference, boolean incompleteCode) {
                    GoLiteralIdentifier e = labelReference.getElement();
                    GoFunctionDeclaration function = findParentOfType(e,
                            GoFunctionDeclaration.class);
                    final String name = e.getName();
                    if (function == null || name == null || name.isEmpty()) {
                        return null;
                    }

                    final AtomicReference<PsiElement> declaration = new AtomicReference<PsiElement>();

                    new GoRecursiveElementVisitor() {
                        @Override
                        public void visitElement(GoPsiElement element) {
                            if (declaration.get() == null) {
                                super.visitElement(element);
                            }
                        }

                        @Override
                        public void visitLabeledStatement(GoLabeledStatement statement) {
                            super.visitLabeledStatement(statement);

                            GoLiteralIdentifier label = statement.getLabel();
                            if (label != null && name.equals(label.getText())) {
                                declaration.set(label);
                            }
                        }

                        @Override
                        public void visitFunctionLiteral(GoLiteralFunction literal) {
                        }
                    }.visitElement(function);

                    return GoResolveResult.fromElement(declaration.get());
                }
            };

    public LabelReference(@NotNull GoLiteralIdentifier element) {
        super(element, RESOLVER);
    }

    @Override
    protected LabelReference self() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getCanonicalName();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (!(element instanceof GoLiteralIdentifier)) {
            return false;
        }

        PsiElement parent = element.getParent();
        if (!(parent instanceof GoLabeledStatement)) {
            return false;
        }

        GoLiteralIdentifier label = ((GoLabeledStatement) parent).getLabel();
        String name = getElement().getName();
        if (label == null || name == null || name.isEmpty() || !name.equals(
                label.getName())) {
            return false;
        }

        PsiElement resolve = resolve();
        return resolve != null && resolve.getTextOffset() == element.getTextOffset();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        GoFunctionDeclaration function = findParentOfType(getElement(),
                GoFunctionDeclaration.class);
        if (function == null) {
            return new Object[0];
        }

        final TreeSet<String> labels = new TreeSet<String>();
        new GoRecursiveElementVisitor() {
            @Override
            public void visitLabeledStatement(GoLabeledStatement statement) {
                super.visitLabeledStatement(statement);

                GoLiteralIdentifier label = statement.getLabel();
                if (label != null) {
                    labels.add(label.getName());
                }
            }

            @Override
            public void visitFunctionLiteral(GoLiteralFunction literal) {
                // don't check function literal
            }
        }.visitElement(function);
        return labels.toArray();
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
