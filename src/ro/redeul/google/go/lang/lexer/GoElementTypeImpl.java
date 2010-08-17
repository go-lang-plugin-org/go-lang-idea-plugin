package ro.redeul.google.go.lang.lexer;

import com.intellij.lang.Language;
import com.sun.istack.internal.Nullable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 2:35:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoElementTypeImpl extends GoElementType {

    private String debugName;

    public GoElementTypeImpl(@NotNull @NonNls String debugName) {
        super(debugName, GoFileType.GO_FILE_TYPE.getLanguage());
        this.debugName = debugName;
    }
    
    @Override
    public String toString() {
        return debugName;
    }
}
