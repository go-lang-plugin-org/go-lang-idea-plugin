package ro.redeul.google.go.lang.psi.processors;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/21/11
 * Time: 6:16 PM
 */
public class NamedTypeVariantsCollector extends BaseScopeProcessor {

    static final String builtInTypes[] = {
            "uint8", "uint16", "uint32", "uint64",
            "int8", "int16", "int32", "int64",
            "float32", "float64",
            "complex64", "complex128",
            "byte", "uint", "int", "float", "complex", "uintptr", "bool", "string"
    };

    List<LookupElement> variants = new ArrayList<LookupElement>();

    public boolean execute(PsiElement element, ResolveState state) {

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

            String visiblePackageName = state.get(GoResolveStates.VisiblePackageName);

            if ( visiblePackageName != null && visiblePackageName.length() > 0 ) {
                typeName = String.format("%s.%s", visiblePackageName, typeName);
            }

            variants.add(LookupElementBuilder.create(typeNameDeclaration, typeName).setTypeText(typeText) );
        }
    }

    public Object[] references() {


        for (String builtInType : builtInTypes) {
            variants.add(LookupElementBuilder.create(builtInType).setTypeText("builtin", true).setBold());
        }

        return variants.toArray(new Object[variants.size()]);
    }
}
