package ro.redeul.google.go.lang.lexer;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import com.sun.istack.internal.Nullable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 2:04:19 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GoElementType extends IElementType {

    public GoElementType(@NotNull @NonNls String debugName, @Nullable Language language) {
        super(debugName, language);
    }

}
