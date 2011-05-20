package ro.redeul.google.go.lang.psi.stubs.elements;

import com.intellij.lang.Language;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import ro.redeul.google.go.lang.psi.stubs.GoFileStub;
import ro.redeul.google.go.lang.psi.stubs.GoFileStubBuilder;
import ro.redeul.google.go.lang.psi.stubs.impl.GoFileStubImpl;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageName;

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
        dataStream.writeName(stub.getPackageName().toString());
        dataStream.writeBoolean(stub.isMain());
//        dataStream.writeName(stub.getPackageReference().toString());
//        dataStream.writeName(stub.getPackageReference().toString());
//        dataStream.writeBoolean(stub.isScript());
//        GrStubUtils.writeStringArray(dataStream, stub.getAnnotations());
    }

    @Override
    public GoFileStub deserialize(final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef packageName = dataStream.readName();
        boolean isMain = dataStream.readBoolean();
        return new GoFileStubImpl(packageName, isMain);
    }

    public void indexStub(GoFileStub stub, IndexSink sink) {

        sink.occurrence(GoPackageName.KEY, stub.getPackageName().toString());

//        String name = stub.getPackageReference().toString();
//        if (stub.isScript() && name != null) {
//            sink.occurrence(GrScriptClassNameIndex.KEY, name);
//            final String pName = stub.getPackageReference().toString();
//            final String fqn = pName == null || pName.length() == 0 ? name : pName + "." + name;
//            sink.occurrence(GrFullScriptNameIndex.KEY, fqn.hashCode());
//        }
//
//        for (String anno : stub.getAnnotations()) {
//            sink.occurrence(GrAnnotatedMemberIndex.KEY, anno);
//        }
    }
}
