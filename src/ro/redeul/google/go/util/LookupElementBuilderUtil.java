package ro.redeul.google.go.util;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.completion.insertHandler.FunctionInsertHandler;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeInterface;

public class LookupElementBuilderUtil {
    public static LookupElementBuilder createLookupElementBuilder(PsiElement element, String name) {
        LookupElementBuilder builder = LookupElementBuilder.create(element, name);
        if (element instanceof GoMethodDeclaration) {
            builder = builder.setIcon(PlatformIcons.METHOD_ICON).setInsertHandler(new FunctionInsertHandler());
        } else if (element instanceof GoFunctionDeclaration) {
            builder = builder.setIcon(PlatformIcons.FUNCTION_ICON).setInsertHandler(new FunctionInsertHandler());
        }

        if (element instanceof GoLiteralIdentifier) {
            PsiElement parent = element.getParent();
            if (parent instanceof GoVarDeclaration) {
                builder = builder.setIcon(PlatformIcons.VARIABLE_ICON);
            } else if (parent instanceof GoConstDeclaration) {
                builder = builder.setIcon(GoIcons.CONST_ICON);
            }
        }

        if (element instanceof GoTypeSpec) {
            GoType type = ((GoTypeSpec) element).getType();
            if (type instanceof GoTypeInterface) {
                builder = builder.setIcon(PlatformIcons.INTERFACE_ICON);
            } else {
                builder = builder.setIcon(PlatformIcons.CLASS_ICON);
            }
        }

        return builder;
    }
}
