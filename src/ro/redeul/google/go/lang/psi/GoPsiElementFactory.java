package ro.redeul.google.go.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class GoPsiElementFactory {
    @Nullable
    public static GoFile createGoFile(@NotNull GoFile originalFile, @NonNls @NotNull String text) {
        PsiFileFactory pff = PsiFileFactory.getInstance(originalFile.getProject());
        GoFile newFile = (GoFile) pff.createFileFromText("dummy.go", GoLanguage.INSTANCE, text);
        newFile.putUserData(PsiFileFactory.ORIGINAL_FILE, originalFile);
        return newFile;
    }

    @NotNull
    public static PsiElement[] createStatements(@NotNull GoFile originalFile, @NonNls @NotNull String statements) {
        String text = "package main\nfunc f(){" + statements + "}";
        GoFile file = createGoFile(originalFile, text);
        if (file == null) {
            return new PsiElement[0];
        }

        PsiElement child = file.getFunctions()[0].getBlock().getFirstChild();
        if (child == null || !"{".equals(child.getText())) {
            return new PsiElement[0];
        }

        List<PsiElement> nodes = new ArrayList<PsiElement>();
        while ((child = child.getNextSibling()) != null) {
            nodes.add(child);
        }
        if (nodes.size() > 0 && "}".equals(nodes.get(nodes.size() - 1).getText())) {
            nodes.remove(nodes.size() - 1);
        }
        return nodes.toArray(new PsiElement[nodes.size()]);
    }

    @Nullable
    public static GoIdentifier createIdentifier(@NotNull GoFile originalFile, @NonNls @NotNull String name) {
        PsiElement[] statements = createStatements(originalFile, name);
        if (statements.length == 0) {
            return null;
        }

        PsiElement expressionStatement = statements[0];
        if (!isNodeOfType(expressionStatement, GoElementTypes.EXPRESSION_STATEMENT)) {
            return null;
        }

        PsiElement literalStatement = expressionStatement.getFirstChild();
        return literalStatement instanceof GoLiteral ? ((GoLiteral) literalStatement).getIdentifier() : null;
    }
}
