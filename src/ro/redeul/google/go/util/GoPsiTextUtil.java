package ro.redeul.google.go.util;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-16-2014 11:44
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoPsiTextUtil {

    @Nullable
    public static TextRange getFunctionSignatureRange(@NotNull GoFunctionDeclaration function) {
        return getFunctionSignatureRange(function, false);
    }

    @Nullable
    public static TextRange getFunctionSignatureRange(@NotNull GoFunctionDeclaration function, boolean relative) {

        TextRange range = getTextRangeWithoutComments(function, true, true);

        range = range.cutOut(
            TextRange
                .create(range.getStartOffset(), function.getBlock().getTextOffset())
                .shiftRight(-range.getStartOffset()));

        if (relative)
            range = range.shiftRight(-function.getTextOffset());

        return range;
    }

    @NotNull
    public static TextRange getTextRangeWithoutComments(@NotNull GoDocumentedPsiElement element,
                                                       boolean skipLeading, boolean skipTrailing) {
        TextRange response = element.getTextRange();

        if ( skipLeading ) {
            PsiElement child = element.getFirstChild();
            while (child != null && GoPsiUtils.isWhiteSpaceOrComment(child))
                child = child.getNextSibling();

            if (child != null)
                response = response.cutOut(
                    TextRange.create(
                        child.getTextOffset(),
                        response.getEndOffset()
                    ).shiftRight(-element.getTextOffset()));
        }

        if ( skipTrailing ) {
            PsiElement child = element.getLastChild();
            while ( child != null && GoPsiUtils.isWhiteSpaceNode(child))
                child = child.getPrevSibling();

            if ( child != null && !GoPsiUtils.isWhiteSpaceNode(child))
                response = response.cutOut(
                    TextRange.create(response.getStartOffset(), child.getTextRange().getEndOffset())
                        .shiftRight(-response.getStartOffset())
                );
        }

        return response;
    }
}
