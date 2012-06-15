package ro.redeul.google.go.inspection;

import ro.redeul.google.go.lang.psi.GoFile;

public class UnresolvedSymbolsTest
    extends GoInspectionTestCase<UnresolvedSymbols> {

    public UnresolvedSymbolsTest() {
        super(UnresolvedSymbols.class);
    }

    public void testIfScope() throws Exception{ doTest(); }
    public void testForWithClause() throws Exception{ doTest(); }
    public void testForWithRange() throws Exception{ doTest(); }
    public void testIota() throws Exception{ doTest(); }
    public void testUndefinedTypeInMethodReceiver() throws Exception { doTest(); }

    @Override
    protected void detectProblems(GoFile file, InspectionResult result) {
        new UnresolvedSymbols().doCheckFile(file, result, false);
    }
}
