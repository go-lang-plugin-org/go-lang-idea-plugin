package ro.redeul.google.go.lang.lexer;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 2:35:19 AM
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
