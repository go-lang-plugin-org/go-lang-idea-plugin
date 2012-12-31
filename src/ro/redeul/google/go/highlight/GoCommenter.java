package ro.redeul.google.go.highlight;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

public class GoCommenter implements CodeDocumentationAwareCommenter {

    public String getLineCommentPrefix() {
        return "//";
    }

    public String getBlockCommentPrefix() {
        return "/*";
    }

    public String getBlockCommentSuffix() {
        return "*/";
    }

    public String getCommentedBlockCommentPrefix() {
        return "/*";
    }

    public String getCommentedBlockCommentSuffix() {
        return "*/";
    }

    public IElementType getLineCommentTokenType() {
        return GoTokenTypes.mSL_COMMENT;
    }

    public IElementType getBlockCommentTokenType() {
        return GoTokenTypes.mML_COMMENT;
    }

    public IElementType getDocumentationCommentTokenType() {
        return null;
    }

    public String getDocumentationCommentPrefix() {
        return null;
    }

    public String getDocumentationCommentLinePrefix() {
        return null;
    }

    public String getDocumentationCommentSuffix() {
        return null;
    }

    public boolean isDocumentationComment(PsiComment element) {
        return false;
    }
}
