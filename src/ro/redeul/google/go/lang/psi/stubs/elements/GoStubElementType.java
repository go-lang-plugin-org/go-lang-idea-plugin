package ro.redeul.google.go.lang.psi.stubs.elements;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.psi.GoPsiElement;

public abstract class GoStubElementType<S extends StubElement, T extends GoPsiElement> extends IStubElementType<S, T> {

    GoStubElementType(@NotNull @NonNls String debugName) {
        super(debugName, GoLanguage.INSTANCE);
    }

    public void indexStub(@NotNull final S stub, @NotNull final IndexSink sink) {

    }

    @NotNull
    public String getExternalId() {
        return "go." + super.toString();
    }
}

