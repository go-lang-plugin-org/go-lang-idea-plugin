package ro.redeul.google.go.lang.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.stubs.elements.GoTypeNameDeclarationType;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/23/11
 * Time: 5:23 PM
 */
public class GoTypeNameDeclarationStub extends StubBase<GoTypeNameDeclaration> implements NamedStub<GoTypeNameDeclaration> {

    private StringRef myPackage;
    private StringRef myName;

    public GoTypeNameDeclarationStub(StubElement parent, final IStubElementType elementType,
                                     final String myName, final String myPackage)
    {
        super(parent, elementType);

        this.myName = StringRef.fromString(myName);
        this.myPackage = StringRef.fromString(myPackage);
    }

    @Override
    @NotNull
    public String getName() {
        return StringRef.toString(myName);
    }

    public String getQualifiedName() {
        return String.format("%s.%s", getPackage(), getName());
    }

    public String getPackage() {
        return StringRef.toString(myPackage);
    }
}
