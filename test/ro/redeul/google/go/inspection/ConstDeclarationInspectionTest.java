package ro.redeul.google.go.inspection;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;

import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isExtraExpressionInConst;
import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isFirstConstExpressionMissed;
import static ro.redeul.google.go.inspection.ConstDeclarationInspection.isMissingExpressionInConst;

public class ConstDeclarationInspectionTest extends LightCodeInsightFixtureTestCase {
    private GoConstDeclarations parse(String fileText) {
        GoFile goFile = (GoFile) myFixture.configureByText(GoFileType.INSTANCE, fileText);
        return goFile.getConsts()[0];
    }

    public void testIsFirstConstExpressionMissed() {
        assertFalse(isFirstConstExpressionMissed(parse("const A = 5")));
        assertFalse(isFirstConstExpressionMissed(parse("const (A = 5)")));
        assertFalse(isFirstConstExpressionMissed(parse("const (A = 5 + 1)")));
        assertFalse(isFirstConstExpressionMissed(parse("const (A = iota)")));

        assertTrue(isFirstConstExpressionMissed(parse("const A")));
        assertTrue(isFirstConstExpressionMissed(parse("const (\nA\n)")));
        assertTrue(isFirstConstExpressionMissed(parse("const (\nA\nB\n)")));
        assertTrue(isFirstConstExpressionMissed(parse("const (\nA\nB = 2\n)")));
        assertTrue(isFirstConstExpressionMissed(parse("const (\nA, B\n)")));
    }

    public void testIsMissingExpressionInConst() {
        assertFalse(isMissingExpressionInConst(parse("const A").getDeclarations()[0]));
        assertFalse(isMissingExpressionInConst(parse("const A = 5").getDeclarations()[0]));
        assertFalse(isMissingExpressionInConst(parse("const A,B = 5,3").getDeclarations()[0]));
        assertFalse(isMissingExpressionInConst(parse("const A,B = 3,5,3").getDeclarations()[0]));
        assertFalse(isMissingExpressionInConst(parse("const (\nA,B = 5,3\n)").getDeclarations()[0]));

        assertTrue(isMissingExpressionInConst(parse("const A, B = 1 + 2").getDeclarations()[0]));
        assertTrue(isMissingExpressionInConst(parse("const (\nA,B = 5\n)").getDeclarations()[0]));
    }

    public void testIsExtraExpressionInConst() {
        assertFalse(isExtraExpressionInConst(parse("const A").getDeclarations()[0]));
        assertFalse(isExtraExpressionInConst(parse("const A = 5").getDeclarations()[0]));
        assertFalse(isExtraExpressionInConst(parse("const A,B = 5,3").getDeclarations()[0]));
        assertFalse(isExtraExpressionInConst(parse("const A,B,C = 5,3").getDeclarations()[0]));
        assertFalse(isExtraExpressionInConst(parse("const (\nA,B = 5,3\n)").getDeclarations()[0]));

        assertTrue(isExtraExpressionInConst(parse("const A = 1, 2").getDeclarations()[0]));
        assertTrue(isExtraExpressionInConst(parse("const A,B = 5,3,4\n)").getDeclarations()[0]));
        assertTrue(isExtraExpressionInConst(parse("const (\nA = 5,3\n)").getDeclarations()[0]));
    }
}
