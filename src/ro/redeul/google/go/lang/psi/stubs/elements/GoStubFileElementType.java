package ro.redeul.google.go.lang.psi.stubs.elements;

import com.intellij.lang.Language;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import ro.redeul.google.go.lang.psi.stubs.GoFileStub;
import ro.redeul.google.go.lang.psi.stubs.GoFileStubBuilder;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 13, 2010
 * Time: 10:25:14 PM
 * To change this template use File | Settings | File Templates.
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
        return super.getStubVersion() + 6;
    }

    public String getExternalId() {
        return "go.FILE";
    }

    @Override
    public void indexStub(PsiFileStub stub, IndexSink sink) {
        super.indexStub(stub, sink);
    }

    @Override
    public void serialize(final GoFileStub stub, final StubOutputStream dataStream) throws IOException {
//        dataStream.writeName(stub.getPackageName().toString());
//        dataStream.writeName(stub.getPackageName().toString());
//        dataStream.writeBoolean(stub.isScript());
//        GrStubUtils.writeStringArray(dataStream, stub.getAnnotations());
    }

    @Override
    public GoFileStub deserialize(final StubInputStream dataStream, final StubElement parentStub) throws IOException {
//        StringRef packName = dataStream.readName();
//        StringRef name = dataStream.readName();
//        boolean isScript = dataStream.readBoolean();
        return new GoFileStub();
    }

    public void indexStub(GoFileStub stub, IndexSink sink) {
//        String name = stub.getPackageName().toString();
//        if (stub.isScript() && name != null) {
//            sink.occurrence(GrScriptClassNameIndex.KEY, name);
//            final String pName = stub.getPackageName().toString();
//            final String fqn = pName == null || pName.length() == 0 ? name : pName + "." + name;
//            sink.occurrence(GrFullScriptNameIndex.KEY, fqn.hashCode());
//        }
//
//        for (String anno : stub.getAnnotations()) {
//            sink.occurrence(GrAnnotatedMemberIndex.KEY, anno);
//        }
    }
}
