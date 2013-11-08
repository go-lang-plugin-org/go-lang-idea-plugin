package ro.redeul.google.go.template;

import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.ReflectionCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public abstract class GoTemplateContextType extends TemplateContextType {
    protected GoTemplateContextType(@NotNull @org.jetbrains.annotations.NonNls String id,
                                    @NotNull String presentableName,
                                    @Nullable Class<? extends TemplateContextType> baseContextType) {
        super(id, presentableName, baseContextType);
    }

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        if (PsiUtilBase.getLanguageAtOffset(file, offset).isKindOf(GoLanguage.INSTANCE)) {
            PsiElement element = file.findElementAt(offset);
            return element != null && !(element instanceof PsiWhiteSpace) && isInContext(element);
        }
        return false;
    }

    protected abstract boolean isInContext(PsiElement element);

    public static class File extends GoTemplateContextType {
        protected File() {
            super("GO", "Go file", EverywhereContextType.class);
        }

        @Override
        protected boolean isInContext(PsiElement element) {
            return element != null && element.getParent() != null &&
                    element.getParent().getParent() != null &&
                    ReflectionCache.isInstance(element.getParent().getParent(), GoFile.class);
        }
    }

    public static class Function extends GoTemplateContextType {
        protected Function() {
            super("GO_FUNCTION", "Go function", EverywhereContextType.class);
        }

        @Override
        protected boolean isInContext(PsiElement element) {
            return findParentOfType(element, GoFunctionDeclaration.class) != null;
        }
    }
}