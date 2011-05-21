package ro.redeul.google.go.lang.psi.processors;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/21/11
 * Time: 6:16 PM
 */
public class NamedTypesProcessor extends BaseScopeProcessor {

    static final String builtInTypes[] = {
            "uint8", "uint16", "uint32", "uint64",
            "int8", "int16", "int32", "int64",
            "float32", "float64",
            "complex64", "complex128",
            "byte", "uint", "int", "float", "complex", "uintptr", "bool", "string"
    };

    List<GoTypeNameDeclaration> typeNameDeclarations = new ArrayList<GoTypeNameDeclaration>();

    Map<String, String> packageToImportName = new HashMap<String, String>();

    public boolean execute(PsiElement element, ResolveState state) {

        if ( element instanceof GoImportSpec ) {
            processImportSpecification((GoImportSpec)element);
        }

        if ( element instanceof GoTypeSpec ) {
            collectTypeSpecification((GoTypeSpec)element);
        }

        return true;
    }

    private void processImportSpecification(GoImportSpec element) {
        packageToImportName.put(element.getPackageName(), element.getVisiblePackageName());
    }

    private void collectTypeSpecification(GoTypeSpec typeSpec) {
        GoTypeNameDeclaration typeNameDeclaration = typeSpec.getTypeNameDeclaration();

        if (typeNameDeclaration != null && GoNamesUtil.isPublicType(typeNameDeclaration.getText()) ) {
            typeNameDeclarations.add(typeNameDeclaration);
        }
    }

    public Object[] references() {

        List<Object> typeNames = new ArrayList<Object>();

        for (String builtInType : builtInTypes) {
            typeNames.add(LookupElementBuilder.create(builtInType).setTypeText("builtin", true).setBold());
        }

        for (GoTypeNameDeclaration typeNameDeclaration : typeNameDeclarations) {
            String packageName = ((GoFile)typeNameDeclaration.getContainingFile()).getPackage().getPackageName();
            String visiblePackageName = packageToImportName.get(packageName);

            String name = typeNameDeclaration.getText();

            if ( visiblePackageName != null && visiblePackageName.length() > 0 ) {
                name = String.format("%s.%s", visiblePackageName, typeNameDeclaration.getText());
            }

            typeNames.add(LookupElementBuilder.create(typeNameDeclaration, name).setTypeText(packageName) );
        }

        return typeNames.toArray(new Object[typeNames.size()]);
    }
}
