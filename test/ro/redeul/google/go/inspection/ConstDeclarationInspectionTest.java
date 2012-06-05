package ro.redeul.google.go.inspection;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;

import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isExtraExpressionInConst;
import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isFirstConstExpressionMissed;
import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isMissingExpressionInConst;

public class ConstDeclarationInspectionTest extends LightCodeInsightFixtureTestCase {
    private GoConstDeclarations compile(String fileText) {
        GoFile goFile = (GoFile) myFixture.configureByText(GoFileType.INSTANCE, fileText);
        return goFile.getConsts()[0];
    }

    public void testIsFirstConstExpressionMissed() {
        assertFalse(isFirstConstExpressionMissed(compile("const A = 5")));
        assertFalse(isFirstConstExpressionMissed(compile("const (A = 5)")));
        assertFalse(isFirstConstExpressionMissed(compile("const (A = 5 + 1)")));
        assertFalse(isFirstConstExpressionMissed(compile("const (A = iota)")));

        assertTrue(isFirstConstExpressionMissed(compile("const A")));
        assertTrue(isFirstConstExpressionMissed(compile("const (\nA\n)")));
        assertTrue(isFirstConstExpressionMissed(compile("const (\nA\nB\n)")));
        assertTrue(isFirstConstExpressionMissed(compile("const (\nA\nB = 2\n)")));
        assertTrue(isFirstConstExpressionMissed(compile("const (\nA, B\n)")));
    }

    public void testIsMissingExpressionInConst() {
        assertFalse(isMissingExpressionInConst(compile("const A").getDeclarations()[0]));
        assertFalse(isMissingExpressionInConst(compile("const A = 5").getDeclarations()[0]));
        assertFalse(isMissingExpressionInConst(compile("const A,B = 5,3").getDeclarations()[0]));
        assertFalse(isMissingExpressionInConst(compile("const A,B = 3,5,3").getDeclarations()[0]));
        assertFalse(isMissingExpressionInConst(compile("const (\nA,B = 5,3\n)").getDeclarations()[0]));

        assertTrue(isMissingExpressionInConst(compile("const A, B = 1 + 2").getDeclarations()[0]));
        assertTrue(isMissingExpressionInConst(compile("const (\nA,B = 5\n)").getDeclarations()[0]));
    }

    public void testIsExtraExpressionInConst() {
        assertFalse(isExtraExpressionInConst(compile("const A").getDeclarations()[0]));
        assertFalse(isExtraExpressionInConst(compile("const A = 5").getDeclarations()[0]));
        assertFalse(isExtraExpressionInConst(compile("const A,B = 5,3").getDeclarations()[0]));
        assertFalse(isExtraExpressionInConst(compile("const A,B,C = 5,3").getDeclarations()[0]));
        assertFalse(isExtraExpressionInConst(compile("const (\nA,B = 5,3\n)").getDeclarations()[0]));

        assertTrue(isExtraExpressionInConst(compile("const A = 1, 2").getDeclarations()[0]));
        assertTrue(isExtraExpressionInConst(compile("const A,B = 5,3,4\n)").getDeclarations()[0]));
        assertTrue(isExtraExpressionInConst(compile("const (\nA = 5,3\n)").getDeclarations()[0]));
    }
}
