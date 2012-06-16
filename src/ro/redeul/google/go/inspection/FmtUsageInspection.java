package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildrenOfType;

public class FmtUsageInspection extends AbstractWholeGoFileInspection {

    public static final String GENERAL_VERBS = "vT";
    public static final String BOOL_VERBS = "t";
    public static final String INT_VERBS = "bcdoqxXU";
    public static final String FLOAT_VERBS = "beEfgG";
    public static final String STR_VERBS = "sqxX";
    public static final String PTR_VERBS = "p";

    public static final String INVALID_VERBS_IN_SCANNING = "pT";

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Fmt usage";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result, boolean isOnTheFly) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitElement(GoPsiElement element) {
                super.visitElement(element);

                if (element instanceof GoCallOrConversionExpression) {
                    List<GoExpr> parameters = findChildrenOfType(element, GoExpr.class);
                    if (!parameters.isEmpty()) {
                        checkFmtCall(result, parameters);
                    }
                }
            }
        }.visitFile(file);
    }

    private static void checkFmtCall(InspectionResult result, List<GoExpr> parameters) {
        GoExpr functionExpr = parameters.get(0);
        if (functionExpr == null) {
            return;
        }

        String function = functionExpr.getText();
        if ("fmt.Fprintf".equals(function)) {
            checkFormat(result, parameters.subList(1, parameters.size()), false);
            return;
        }

        if ("fmt.Printf".equals(function) ||
                "fmt.Errorf".equals(function) ||
                "fmt.Sprintf".equals(function)) {
            checkFormat(result, parameters, false);
            return;
        }

        if ("fmt.Fscanf".equals(function) || "fmt.Sscanf".equals(function)) {
            checkFormat(result, parameters.subList(1, parameters.size()), true);
            return;
        }

        if ("fmt.Scanf".equals(function)) {
            checkFormat(result, parameters, true);
            return;
        }
    }

    private static void checkFormat(InspectionResult result, List<GoExpr> parameters, boolean isScanning) {
        if (parameters.size() < 2) {
            return;
        }

        GoExpr fmtExpr = parameters.get(1);
        if (!(fmtExpr instanceof GoLiteralExpression)) {
            return;
        }

        PsiElement parent = fmtExpr.getParent();
        if (!(parent instanceof GoCallOrConversionExpression)) {
            return;
        }

        GoLiteral fmtLiteral = ((GoLiteralExpression) fmtExpr).getLiteral();
        if (fmtLiteral instanceof GoLiteralIdentifier) {
            fmtLiteral = findConstDefinition((GoLiteralIdentifier) fmtLiteral);
        }

        if (!(fmtLiteral instanceof GoLiteralString)) {
            return;
        }

        Context ctx = new Context((GoCallOrConversionExpression) parent, fmtLiteral, result,
                parameters.subList(2, parameters.size()), isScanning);
        checkFormat(fmtLiteral.getText(), ctx);
        ctx.checkAllExtraParameters();
    }

    private static GoLiteralString findConstDefinition(GoLiteralIdentifier idToFind) {
        String name = idToFind.getName();
        if (idToFind.isBlank() || idToFind.isIota() || name == null || name.isEmpty()) {
            return null;
        }

        PsiElement resolve = idToFind.resolve();
        if (resolve == null) {
            return null;
        }

        PsiElement parent = resolve.getParent();
        if (!(parent instanceof GoConstDeclaration)) {
            return null;
        }

        GoConstDeclaration cd = ((GoConstDeclaration) parent);
        GoLiteralIdentifier[] ids = cd.getIdentifiers();
        GoExpr[] exprs = cd.getExpressions();
        if (ids == null || exprs == null || ids.length != exprs.length) {
            return null;
        }

        for (int i = 0; i < ids.length; i++) {
            if (name.equals(ids[i].getName())) {
                GoExpr expr = exprs[i];
                if (!(expr instanceof GoLiteralExpression)) {
                    return null;
                }

                PsiElement child = expr.getFirstChild();
                if (!(child instanceof GoLiteralString)) {
                    return null;
                }

                return (GoLiteralString) child;
            }
        }
        return null;
    }

    private static void checkFormat(String fmt, Context ctx) {
        for (int i = 0; i < fmt.length(); i++) {
            char c = fmt.charAt(i);
            if (c != '%') {
                continue;
            }

            if (i == fmt.length() - 1) {
                continue;
            }

            ctx.startOffset = i;
            while (++i < fmt.length()) {
                char verb = fmt.charAt(i);
                if (Character.isDigit(verb) || verb == '+' || verb == '-' || verb == ' ' || verb == '#' ||
                    verb == '.') {
                    // It's not a verb, it's a flag, ignore it.
                    continue;
                }

                if (verb == '*') {
                    assertIntParameter(ctx);
                    continue;
                }

                ctx.endOffset = i;

                if (ctx.isScanning && INVALID_VERBS_IN_SCANNING.indexOf(verb) != -1) {
                    ctx.unknownFlag();
                } else if (verb == '%') {
                    // A literal percent sign, consumes no value
                } else if (BOOL_VERBS.indexOf(verb) != -1) {
                    assertBoolParameter(ctx);
                } else if (GENERAL_VERBS.indexOf(verb) != -1) {
                    assertAnyParameter(ctx);
                } else if (INT_VERBS.indexOf(verb) != -1) {
                    assertIntParameter(ctx);
                } else if (FLOAT_VERBS.indexOf(verb) != -1) {
                    assertFloatParameter(ctx);
                } else if (STR_VERBS.indexOf(verb) != -1) {
                    assertStrParameter(ctx);
                } else if (PTR_VERBS.indexOf(verb) != -1) {
                    assertPtrParameter(ctx);
                } else {
                    ctx.unknownFlag();
                }
                break;
            }
        }
    }

    private static void assertPtrParameter(Context ctx) {
        GoExpr expr = ctx.getNextParameter();
        if (expr != null) {
            // TODO: should be pointer
        }
    }

    private static void assertStrParameter(Context ctx) {
        GoExpr expr = ctx.getNextParameter();
        if (expr != null) {
            // TODO: should be str or byte slice
        }
    }

    private static void assertBoolParameter(Context ctx) {
        GoExpr expr = ctx.getNextParameter();
        if (expr != null) {
            // TODO: should be bool
        }
    }

    private static void assertIntParameter(Context ctx) {
        GoExpr expr = ctx.getNextParameter();
        if (expr != null) {
            // TODO: should be int
        }
    }

    private static void assertFloatParameter(Context ctx) {
        GoExpr expr = ctx.getNextParameter();
        if (expr != null) {
            // TODO: should be float or complex
        }
    }

    private static void assertAnyParameter(Context ctx) {
        ctx.getNextParameter();
    }

    private static class Context {
        private static final ProblemHighlightType TYPE = ProblemHighlightType.LIKE_UNUSED_SYMBOL;

        public final GoCallOrConversionExpression theCall;
        public final boolean isFmtLiteralString;
        public final GoLiteral fmtLiteral;
        public final InspectionResult result;
        public final List<GoExpr> parameters;
        public final boolean isScanning;
        public int currentParameter = 0;
        public int startOffset = 0;
        public int endOffset = 0;

        private Context(GoCallOrConversionExpression theCall, GoLiteral fmtLiteral,
                        InspectionResult result, List<GoExpr> parameters, boolean isScanning) {
            this.fmtLiteral = fmtLiteral;
            this.theCall = theCall;
            this.isFmtLiteralString = fmtLiteral.getParent() instanceof GoCallOrConversionExpression;
            this.result = result;
            this.parameters = parameters;
            this.isScanning = isScanning;
        }

        public GoExpr getNextParameter() {
            if (currentParameter < parameters.size()) {
                GoExpr param = parameters.get(currentParameter++);
                if (!isScanning) {
                    // TODO: param should be a pointer
                }
                return param;
            }

            missingParameter();
            return null;
        }

        public void checkAllExtraParameters() {
            for (int i = currentParameter; i < parameters.size(); i++) {
                extraParameter(parameters.get(i));
            }
        }

        public void addWrongParameterType(GoExpr expr, String msg) {
            result.addProblem(expr, msg, TYPE);
        }

        public void missingParameter() {
            // If the format string is defined elsewhere, also mark the right parenthesis as error.
            if (!isFmtLiteralString) {
                result.addProblem(theCall.getLastChild(), "Missing parameter", TYPE);
            }

            result.addProblem(fmtLiteral, startOffset, endOffset + 1, "Missing parameter", TYPE);
        }

        public void extraParameter(PsiElement expr) {
            result.addProblem(expr, "Extra parameter", TYPE);
        }

        public void unknownFlag() {
            result.addProblem(fmtLiteral, startOffset, endOffset + 1, "Unknown flag", TYPE);
        }
    }
}
