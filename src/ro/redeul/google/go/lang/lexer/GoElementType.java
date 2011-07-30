package ro.redeul.google.go.lang.lexer;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 2:04:19 AM
 */
public abstract class GoElementType extends IElementType {

    public GoElementType(@NotNull @NonNls String debugName, @Nullable Language language) {
        super(debugName, language);
    }

}
