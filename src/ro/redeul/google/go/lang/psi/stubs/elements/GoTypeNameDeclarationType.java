package ro.redeul.google.go.lang.psi.stubs.elements;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoTypeNameDeclarationImpl;
import ro.redeul.google.go.lang.psi.stubs.GoStubUtils;
import ro.redeul.google.go.lang.psi.stubs.GoTypeNameDeclarationStub;
import ro.redeul.google.go.lang.psi.stubs.index.GoQualifiedTypeName;
import ro.redeul.google.go.lang.psi.stubs.index.GoTypeName;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;

import java.io.IOException;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/23/11
 * Time: 5:29 PM
 */
public class GoTypeNameDeclarationType extends GoStubElementType<GoTypeNameDeclarationStub, GoTypeNameDeclaration> {

    public GoTypeNameDeclarationType() {
        super("type.name.declaration.stub");
    }

    public String getExternalId() {
        return "go.type.name.declaration";
    }

    @Override
    public GoTypeNameDeclaration createPsi(GoTypeNameDeclarationStub stub) {
        return new GoTypeNameDeclarationImpl(stub, this);
    }

    @Override
    public GoTypeNameDeclarationStub createStub(GoTypeNameDeclaration psi, StubElement parentStub) {
        return new GoTypeNameDeclarationStub(parentStub, this, psi.getName(), psi.getPackageName());
    }

    @Override
    public void serialize(GoTypeNameDeclarationStub stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        GoStubUtils.writeNullableString(dataStream, stub.getPackage());
    }

    @Override
    public GoTypeNameDeclarationStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return
                new GoTypeNameDeclarationStub(parentStub,
                        this,
                        StringRef.toString(dataStream.readName()),
                        GoStubUtils.readNullableString(dataStream));
    }

    @Override
    public void indexStub(GoTypeNameDeclarationStub stub, IndexSink sink) {
        sink.occurrence(GoTypeName.KEY, stub.getName());
        sink.occurrence(GoQualifiedTypeName.KEY, stub.getQualifiedName());
    }
}
