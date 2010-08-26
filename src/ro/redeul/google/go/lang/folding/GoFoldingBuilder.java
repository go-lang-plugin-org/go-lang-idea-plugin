package ro.redeul.google.go.lang.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.parsing.statements.BlockStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 17, 2010
 * Time: 11:21:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoFoldingBuilder implements FoldingBuilder, DumbAware, GoElementTypes {
    @NotNull
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
        appendDescriptors(node.getPsi(), document, descriptors);
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private void appendDescriptors(PsiElement psi, Document document, List<FoldingDescriptor> descriptors) {
        ASTNode node = psi.getNode();
        if (node == null) return;
        IElementType type = node.getElementType();

        if (mML_COMMENT == type && isMultiline(psi)) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
            return;
        }

        if ( TYPE_DECLARATIONS == type && isMultiline(psi)) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
            return;
        }

        if ( CONST_DECLARATIONS == type && isMultiline(psi)) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
            return;
        }

        if ( VAR_DECLARATIONS == type && isMultiline(psi)) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
            return;
        }

        if ( BLOCK_STATEMENT == type && isMultiline(psi)) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
        }

        if ( METHOD_DECLARATION == type && isMultiline(psi)) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
        }

        if ( FUNCTION_DECLARATION == type && isMultiline(psi)) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
        }


        PsiElement child = psi.getFirstChild();
        while (child != null) {
          appendDescriptors(child, document, descriptors);
          child = child.getNextSibling();
        }

//        PsiElement child = element.getFirstChild();
//        while (child != null) {
//          appendDescriptors(child, document, descriptors);
//          child = child.getNextSibling();
//        }
//
//        if (element instanceof GroovyFile) {
//          GroovyFile file = (GroovyFile)element;
//          addFoldingsForImports(descriptors, file);
//        }


    }

    public String getPlaceholderText(@NotNull ASTNode node) {
        return "{ ... }";
    }

    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }

    private static boolean isMultiline(PsiElement element) {
        String text = element.getText();
        return text.contains("\n") || text.contains("\r") || text.contains("\r\n");
    }
}
