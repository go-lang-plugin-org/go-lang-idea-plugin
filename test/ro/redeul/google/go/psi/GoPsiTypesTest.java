package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.GoTypePointer;
import ro.redeul.google.go.lang.psi.types.GoTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiTypesTest extends GoPsiTestCase {


    public void testBasic() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "type T struct {\n" +
                      "    io.Reader\n" +
                      "}"));

        GoTypeStruct structType =
            getAs(GoTypeStruct.class,
                  childAt(0,
                          childAt(0,
                                  file.getTypeDeclarations()
                          ).getTypeSpecs()
                  ).getType()
            );

        assertEquals(0, structType.getFields().length);
        assertEquals(1, structType.getAnonymousFields().length);


        GoTypeStructAnonymousField anonymousField =
            childAt(0, structType.getAnonymousFields());


        assertEquals("Reader", anonymousField.getFieldName());

        getAs(GoTypeName.class, anonymousField.getType());
    }

    public void testAnonymousPointer() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "type T struct {\n" +
                      "    *io.Reader\n" +
                      "}"));

        GoTypeStruct structType =
            getAs(GoTypeStruct.class,
                  childAt(0,
                          childAt(0,
                                  file.getTypeDeclarations()
                          ).getTypeSpecs()
                  ).getType()
            );

        assertEquals(0, structType.getFields().length);
        assertEquals(1, structType.getAnonymousFields().length);

        GoTypeStructAnonymousField field =
            childAt(0, structType.getAnonymousFields());

        GoTypePointer typePointer =
            getAs(GoTypePointer.class, field.getType());

        assertEquals("Reader", field.getFieldName());
    }
}
