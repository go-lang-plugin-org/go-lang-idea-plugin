/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypePointer;
import ro.redeul.google.go.lang.psi.types.GoTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

/**
 * // TODO: Explain yourself.
 *
 * @author Mihai Claudiu Toader <mtoader@midokura.com>
 *         Date: 7/3/12
 */
public class StructFieldsReference extends GoPsiReference<GoLiteralIdentifier> {

    GoTypeStruct type;
    GoSelectorExpression selector;

    public StructFieldsReference(GoSelectorExpression element) {
        super(element.getIdentifier());

        selector = element;
        type = findTypeStructDeclaration();
    }

    private GoTypeStruct findTypeStructDeclaration() {
        GoType type = selector.getBaseExpression().getType()[0];
        while ( type != null && ! (type instanceof GoTypeStruct) ) {
            if ( type instanceof GoTypePointer )
                type = ((GoTypePointer)type).getTargetType();

            GoTypeSpec typeSpec =
                GoPsiUtils.resolveSafely(type, GoTypeSpec.class);

            if (typeSpec != null) {
                type = typeSpec.getType();
            }
        }

        if ( type == null )
            return null;

        return (GoTypeStruct)type;
    }

    @Override
    public PsiElement resolve() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();

        for (GoTypeStructField field : type.getFields()) {
            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                variants.add(field.getCompletionPresentation(identifier));
            }
        }

        for (GoTypeStructAnonymousField field : type.getAnonymousFields()) {
            variants.add(field.getCompletionPresentation());
        }

        return variants.toArray(new LookupElementBuilder[variants.size()]);
    }

    @Override
    public boolean isSoft() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
