package ro.redeul.google.go.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.impl.source.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.formatter.blocks.GoBlockGenerator;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 27, 2010
 */
public class GoFormattingModelBuilder implements FormattingModelBuilder {

    @NotNull
    public FormattingModel createModel(PsiElement element,
                                       CodeStyleSettings settings) {

        final FileElement fileElement = TreeUtil.getFileElement(
            (TreeElement) SourceTreeToPsiMap.psiElementToTree(element));

        CommonCodeStyleSettings goSettings =
            settings.getCommonSettings(GoLanguage.INSTANCE);

        // Here we deny what user believes about space (or tab)
        // For more detail see: http://golang.org/doc/effective_go.html#formatting
        if (goSettings.getIndentOptions() != null) {
            goSettings.getIndentOptions().USE_TAB_CHARACTER = true;
            goSettings.getIndentOptions().SMART_TABS = false;
        }

        Block block = GoBlockGenerator.generateBlock(fileElement, goSettings);

        FormattingModel formattingModel = FormattingModelProvider
                .createFormattingModelForPsiFile(
                        element.getContainingFile(),
                        block,
                        settings);


        FormattingModelDumper.dumpFormattingModel(formattingModel.getRootBlock(), 4, System.out);
        return formattingModel;
    }

    public TextRange getRangeAffectingIndent(PsiFile file, int offset,
                                             ASTNode elementAtOffset) {

/*
        ASTNode current = elementAtOffset;

        while (current != null &&
            current.getElementType() != GoElementTypes.CONST_DECLARATIONS &&
            current.getElementType() != GoElementTypes.BLOCK_STATEMENT &&
            current.getElementType() != GoParserDefinition.GO_FILE_TYPE) {
            current = current.getTreeParent();
        }

        if (current != null) {
            return current.getTextRange();
        } else {
*/
        return file.getTextRange();
//        }
    }

}
