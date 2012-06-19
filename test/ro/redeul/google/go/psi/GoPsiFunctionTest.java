package ro.redeul.google.go.psi;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;

public class GoPsiFunctionTest extends GoPsiTestCase {

    public void testNoParams() throws Exception {
        GoFile file = get(parse("package main; func a() { }"));

        GoFunctionDeclaration func =
            childAt(0,
                    file.getFunctions()
            );

        assertEquals(func.getParameters().length, 0);
    }

    public void testOneParam() throws Exception {
        GoFile file = get(parse("package main; func a(a int) { }"));

        GoFunctionDeclaration func =
            childAt(0,
                    file.getFunctions()
            );

        assertEquals(func.getParameters().length, 1);
    }

    public void testOneParamVariadic() throws Exception {
        GoFile file = get(parse("package main; func a(a ...int) { }"));

        GoFunctionDeclaration func =
            childAt(0,
                    file.getFunctions()
            );

        assertEquals(func.getParameters().length, 1);
    }

    public void testUnnamedParamVariadic() throws Exception {
        GoFile file = get(parse("package main; func a(...int) { }"));
        GoFunctionParameter param =
            childAt(0,
                    childAt(0,
                            file.getFunctions()
                    ).getParameters()
            );

        assertEquals(param.isVariadic(), true);
    }
}
