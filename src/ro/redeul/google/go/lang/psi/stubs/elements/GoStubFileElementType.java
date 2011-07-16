package ro.redeul.google.go.lang.psi.stubs.elements;

import com.intellij.lang.Language;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import ro.redeul.google.go.lang.psi.stubs.GoFileStub;
import ro.redeul.google.go.lang.psi.stubs.GoFileStubBuilder;
import ro.redeul.google.go.lang.psi.stubs.GoStubUtils;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageName;
import ro.redeul.google.go.lang.psi.stubs.index.GoTypeName;

import java.io.IOException;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 13, 2010
 * Time: 10:25:14 PM
 */
public class GoStubFileElementType extends IStubFileElementType<GoFileStub> {

    public GoStubFileElementType(Language language) {
        super(language);
    }

    public StubBuilder getBuilder() {
        return new GoFileStubBuilder();
    }

    @Override
    public int getStubVersion() {
        return super.getStubVersion() + 9;
    }

    public String getExternalId() {
        return "go.FILE";
    }

    @Override
    public void serialize(final GoFileStub stub, final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getPackageName().toString());
        dataStream.writeBoolean(stub.isMain());
    }

    @Override
    public GoFileStub deserialize(final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef packageName = dataStream.readName();
        boolean isMain = dataStream.readBoolean();

        return new GoFileStub(packageName, isMain);
    }

    public void indexStub(GoFileStub stub, IndexSink sink) {
        sink.occurrence(GoPackageName.KEY, stub.getPackageName().toString());
    }
}
