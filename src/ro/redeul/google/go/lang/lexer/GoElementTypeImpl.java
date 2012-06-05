package ro.redeul.google.go.lang.lexer;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;

public class GoElementTypeImpl extends GoElementType {

    private String debugName;

    public GoElementTypeImpl(@NotNull @NonNls String debugName) {
        super(debugName, GoFileType.INSTANCE.getLanguage());
        this.debugName = debugName;
    }

    @Override
    public String toString() {
        return debugName;
    }
}
