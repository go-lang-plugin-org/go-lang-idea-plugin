package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-04-2014 00:54
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoBlockGenerator extends GoElementVisitorWithData<Block> {


    private CommonCodeStyleSettings mySettings;
    private Indent myChildIndent;
    private Alignment myChildAlign;
    private Map<Alignments.Key,Alignment> myAlignmentsMap;

    Block generate(ASTNode child,
                   CommonCodeStyleSettings settings,
                   Indent childIndent, Alignment childAlign,
                   Map<Alignments.Key, Alignment> alignmentsMap,
                   boolean isCommentGroup) {
        GoPsiElement psiElement = null;

        this.mySettings = settings;
        this.myChildIndent = childIndent;
        this.myChildAlign = childAlign;
        this.myAlignmentsMap = alignmentsMap;

        return psiElement.accept(this);
    }

    @Override
    public void visitPackageDeclaration(GoPackageDeclaration declaration) {
        setData(new GoSyntheticBlock<GoPackageDeclaration>(declaration, mySettings));
    }

}
