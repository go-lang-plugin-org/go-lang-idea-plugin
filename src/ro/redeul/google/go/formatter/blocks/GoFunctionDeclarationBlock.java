package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.CustomSpacing;

class GoFunctionDeclarationBlock extends GoSyntheticBlock<GoFunctionDeclaration> {

    private static CustomSpacing CUSTOM_SPACING_RULES = CustomSpacing.Builder()
        .setNone(LITERAL_IDENTIFIER, pLPAREN) // func main|()

        .setNone(pLPAREN, FUNCTION_PARAMETER_LIST) // func main(|a int)
        .setNone(FUNCTION_PARAMETER_LIST, pRPAREN) // func main(a, b int|)

        .setNone(pLPAREN, METHOD_RECEIVER) // func (|a int) main()
        .setNone(METHOD_RECEIVER, pRPAREN) // func (a int|) main()

        .setNone(pLPAREN, pRPAREN) // (|)
        .build();

    private static final GoBlockUtil.CustomSpacing EMPTY_BLOCK_SPACING =
        GoBlockUtil.CustomSpacing.Builder()
            .setNone(pLCURLY, pRCURLY)
            .build();

    private static final GoBlockUtil.CustomSpacing NON_EMPTY_BLOCK_SPACING =
        GoBlockUtil.CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, GoBlockUtil.Spacings.ONE_LINE_KEEP_BREAKS)
            .build();

    private static final TokenSet FORCE_MULTILINE_CHILDS = TokenSet.create(
        LABELED_STATEMENT,
        FOR_WITH_CLAUSES_STATEMENT,
        FOR_WITH_CONDITION_STATEMENT,
        FOR_WITH_RANGE_AND_VARS_STATEMENT,
        FOR_WITH_RANGE_STATEMENT,
        SWITCH_EXPR_STATEMENT,
        SWITCH_TYPE_STATEMENT,
        IF_STATEMENT
    );

    public GoFunctionDeclarationBlock(GoFunctionDeclaration psi,
                                      CommonCodeStyleSettings settings,
                                      Indent indent,
                                      Alignment alignment,
                                      Map<Alignments.Key, Alignment> alignmentsMap) {
        super(psi, settings, indent, null, alignmentsMap);

        setCustomSpacing(CUSTOM_SPACING_RULES);
    }

    @Nullable
    @Override
    protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild, Map<Alignments.Key, Alignment> alignments) {
        if (child instanceof PsiComment)
            return alignments.get(Alignments.Key.Comments);

        return super.getChildAlignment(child, prevChild, alignments);
    }


    @Override
    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        if (childPsi == getPsi().getBlock()) {
            GoBlockStatementBlock statementBlock = (GoBlockStatementBlock) childBlock;
            GoBlockStatement statement = getPsi().getBlock();

            boolean isMultiLine = StringUtil.containsLineBreak(getPsi().getBlock().getText());
            for (GoStatement innerStatement : statement.getStatements()) {
                if ( FORCE_MULTILINE_CHILDS.contains(innerStatement.getNode().getElementType())) {
                    isMultiLine = true;
                    break;
                }
            }

            statementBlock.setMultiLineMode(isMultiLine, pLCURLY, pRCURLY);

            statementBlock.setCustomSpacing(
                !statementBlock.isMultiLine()
                    ? EMPTY_BLOCK_SPACING
                    : NON_EMPTY_BLOCK_SPACING
            );
        }

        return super.customizeBlock(childBlock, childPsi);
    }
}
