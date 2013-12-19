package ro.redeul.google.go.lang.psi.stubs.elements;

import com.intellij.lang.Language;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.stubs.GoFileStub;
import ro.redeul.google.go.lang.psi.stubs.GoFileStubBuilder;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageImportPath;
import ro.redeul.google.go.lang.psi.stubs.index.GoPackageName;

import java.io.IOException;
import java.util.regex.Pattern;

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

    @Override
    @NotNull
    public String getExternalId() {
        return "go.FILE";
    }

    @Override
    public void serialize(@NotNull final GoFileStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getPackageName().toString());

        dataStream.writeBoolean(stub.getPackageImportPath() != null);
        if ( stub.getPackageImportPath() != null ) {
            dataStream.writeName(stub.getPackageImportPath().toString());
        }

        dataStream.writeBoolean(stub.isMain());
    }

    @NotNull
    @Override
    public GoFileStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef packageName = dataStream.readName();
        StringRef packageImportPath = null;

        boolean hasPackageImportPath = dataStream.readBoolean();
        if ( hasPackageImportPath ) {
            packageImportPath = dataStream.readName();
        }

        boolean isMain = dataStream.readBoolean();

        return new GoFileStub(packageImportPath, packageName, isMain);
    }

    public void indexStub(@NotNull GoFileStub stub, @NotNull IndexSink sink) {
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
