package ro.redeul.google.go.lang.psi.processors;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/21/11
 * Time: 6:16 PM
 */
class NamedTypeVariantsCollector extends BaseScopeProcessor {

    private static final String[] builtInTypes = {
            "uint8", "uint16", "uint32", "uint64",
            "int8", "int16", "int32", "int64",
            "float32", "float64",
            "complex64", "complex128",
            "byte", "uint", "int", "complex", "uintptr", "bool", "string"
    };

    private final List<LookupElement> variants = new ArrayList<LookupElement>();

    public boolean execute(@NotNull PsiElement element, ResolveState state) {

        if ( element instanceof GoTypeSpec ) {
            processTypeSpecification((GoTypeSpec) element, state);
        }

        return true;
    }

    private void processTypeSpecification(GoTypeSpec typeSpec, ResolveState state) {

        GoTypeNameDeclaration typeNameDeclaration = typeSpec.getTypeNameDeclaration();

        // include this if:
        //   - inside the same package
        //   - is a public name

        if ( typeNameDeclaration == null ) {
            return;
        }

        String typeName = typeNameDeclaration.getText();

        String typeText = state.get(GoResolveStates.PackageName);

        boolean isCandidate = false;
        if ( GoNamesUtil.isPublicType(typeName) ) {
            isCandidate = true;
        }

        if ( state.get(GoResolveStates.IsOriginalFile) || state.get(GoResolveStates.IsOriginalPackage) ) {
            isCandidate = true;
            typeText = "<current>";
        }

        if ( isCandidate ) {
            boolean isInterface = typeSpec.getType() instanceof GoPsiTypeInterface;
            Icon icon = isInterface ? PlatformIcons.INTERFACE_ICON : PlatformIcons.CLASS_ICON;

            String visiblePackageName = state.get(GoResolveStates.VisiblePackageName);

            if ( visiblePackageName != null && visiblePackageName.length() > 0 ) {
                typeName = String.format("%s.%s", visiblePackageName, typeName);
            }

            LookupElementBuilder lookupElement =
                LookupElementBuilder.create(typeNameDeclaration, typeName)
                                    .withIcon(icon)
                                    .withTypeText(typeText);

            variants.add(lookupElement);
        }
    }

    public Object[] references() {
        for (String builtInType : builtInTypes) {
            LookupElementBuilder lookupElement =
                LookupElementBuilder.create(builtInType)
                                    .withTypeText("builtin", true)
                                    .bold();

            variants.add(lookupElement);
        }

        return variants.toArray(new Object[variants.size()]);
    }
}
