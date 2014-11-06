package ro.redeul.google.go.lang.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/31/11
 * Time: 11:06 PM
 */
public class GoPsiPackagedElementBase extends GoPsiElementBase {

    protected GoPsiPackagedElementBase(@NotNull ASTNode node) {
        super(node);
    }

//    @Override
//    public String getPackageName() {
//        PsiElement parent = getParent();
//
//        while ( parent != null &&  ! (parent instanceof GoFile) ) {
//            parent = parent.getParent();
//        }
//
//        return parent != null ? ((GoFile)parent).getPackageName() : "<>";
//    }
//    @Override
//    public String getQualifiedName() {
//        return String.format("%s.%s", getPackageName(), getName());
//    }
//
//    @Override
//    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
//        return null;
//    }
}
