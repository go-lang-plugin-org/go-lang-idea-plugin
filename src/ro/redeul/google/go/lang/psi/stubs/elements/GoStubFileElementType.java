package ro.redeul.google.go.lang.psi.stubs.elements;

import java.io.IOException;
import java.util.regex.Pattern;

import com.intellij.lang.Language;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.stubs.GoFileStub;
import ro.redeul.google.go.lang.psi.stubs.GoFileStubBuilder;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageImportPath;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageName;

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
        return super.getStubVersion() + 16;
    }

    public String getExternalId() {
        return "go.FILE";
    }

    @Override
    public void serialize(final GoFileStub stub, final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getPackageName().toString());

        dataStream.writeBoolean(stub.getPackageImportPath() != null);
        if ( stub.getPackageImportPath() != null ) {
            dataStream.writeName(stub.getPackageImportPath().toString());
        }

        dataStream.writeBoolean(stub.isMain());
    }

    @Override
    public GoFileStub deserialize(final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef packageName = dataStream.readName();
        StringRef packageImportPath = null;

        boolean hasPackageImportPath = dataStream.readBoolean();
        if ( hasPackageImportPath ) {
            packageImportPath = dataStream.readName();
        }

        boolean isMain = dataStream.readBoolean();

        return new GoFileStub(packageImportPath, packageName, isMain);
    }

    public void indexStub(GoFileStub stub, IndexSink sink) {
        StringRef packageImportPath = stub.getPackageImportPath();
        if ( packageImportPath != null ) {
            // don't index any package information on test files or test data.
            if (isTestDataInStandardLibrary(packageImportPath) || isTestFile(stub.getPsi())) {
                return;
            }

            sink.occurrence(GoPackageImportPath.KEY, packageImportPath.toString());
        }

        sink.occurrence(GoPackageName.KEY, stub.getPackageName().toString());
    }

    private boolean isTestFile(GoFile file) {
        return file != null && file.getName().endsWith("_test.go");
    }

    private boolean isTestDataInStandardLibrary(StringRef packageImportPath) {
        return GO_TEST_DATA_PATTERN.matcher(packageImportPath.toString()).matches();
    }

    private static final Pattern GO_TEST_DATA_PATTERN = Pattern.compile("go/.*/testdata\\b.*");
}
