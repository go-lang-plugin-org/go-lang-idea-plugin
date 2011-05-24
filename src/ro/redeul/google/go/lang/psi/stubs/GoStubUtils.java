package ro.redeul.google.go.lang.psi.stubs;

import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/24/11
 * Time: 8:31 PM
 */
public class GoStubUtils {

    public static void writeStringArray(StubOutputStream dataStream, String[] array) throws IOException {
        dataStream.writeByte(array.length);
        for (String s : array) {
            dataStream.writeName(s);
        }
    }

    public static String[] readStringArray(StubInputStream dataStream) throws IOException {
        final byte b = dataStream.readByte();
        final String[] annNames = new String[b];
        for (int i = 0; i < b; i++) {
            annNames[i] = dataStream.readName().toString();
        }
        return annNames;
    }

    public static void writeNullableString(StubOutputStream dataStream, @Nullable String typeText) throws IOException {
        dataStream.writeBoolean(typeText != null);
        if (typeText != null) {
            dataStream.writeUTFFast(typeText);
        }
    }

    @Nullable
    public static String readNullableString(StubInputStream dataStream) throws IOException {
        final boolean hasTypeText = dataStream.readBoolean();
        return hasTypeText ? dataStream.readUTFFast() : null;
    }

}
