package ro.redeul.google.go.formatter;

import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParserDefinition;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 27, 2010
 * Time: 7:01:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoFormatterModelBuilder implements FormattingModelBuilder {

    @NotNull
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        ASTNode node = element.getNode();
        assert node != null;

        PsiFile containingFile = element.getContainingFile().getViewProvider().getPsi(GoFileType.GO_LANGUAGE);
        assert containingFile != null : element.getContainingFile();

        ASTNode astNode = containingFile.getNode();
        assert astNode != null;

        final GoBlock block = new GoBlock(astNode, null, Indent.getAbsoluteNoneIndent(), null, settings);
        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile, block, settings);
    }

    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {

        ASTNode current = elementAtOffset;

        while (current != null && current.getElementType() != GoElementTypes.BLOCK_STATEMENT && current.getElementType() != GoParserDefinition.GO_FILE_TYPE) {
            current = current.getTreeParent();
        }

        if (current != null ) {
            return current.getTextRange();
        } else {
            return file.getTextRange();
        }
    }
}
