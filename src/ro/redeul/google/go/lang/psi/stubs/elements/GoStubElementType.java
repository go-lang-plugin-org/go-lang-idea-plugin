package ro.redeul.google.go.lang.psi.stubs.elements;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/23/11
 * Time: 6:05 PM
 */
public abstract class GoStubElementType<S extends StubElement, T extends GoPsiElement> extends IStubElementType<S, T> {

    public GoStubElementType(@NotNull @NonNls String debugName) {
        super(debugName, GoFileType.GO_LANGUAGE);
    }

    public void indexStub(final S stub, final IndexSink sink) {

    }

    public String getExternalId() {
        return "go." + super.toString();
    }
}

