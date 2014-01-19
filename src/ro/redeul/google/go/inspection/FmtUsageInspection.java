package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstSpec;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.Arrays;

import static ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral.Type.InterpretedString;
import static ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral.Type.RawString;

public class FmtUsageInspection extends AbstractWholeGoFileInspection {

    private static final String GENERAL_VERBS = "vT";
    private static final String BOOL_VERBS = "t";
    private static final String INT_VERBS = "bcdoqxXU";
    private static final String FLOAT_VERBS = "beEfgG";
    private static final String STR_VERBS = "sqxX";
    private static final String PTR_VERBS = "p";

    private static final String INVALID_VERBS_IN_SCANNING = "pT";

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Fmt usage";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitCallOrConvExpression(GoCallOrConvExpression expression) {
                checkFmtCall(result, expression);
            }
        }.visitFile(file);
    }

    private static void checkFmtCall(InspectionResult result, GoCallOrConvExpression call) {
        GoPrimaryExpression callNameExpression = call.getBaseExpression();

        if (callNameExpression == null)
            return;

        String methodCall = callNameExpression.getText();

        GoExpr[]args = call.getArguments();

        if ("fmt.Fprintf".equals(methodCall) && args.length > 1) {
            checkFormat(result, call, Arrays.copyOfRange(args, 1, args.length), false);
            return;
        }

        if ("fmt.Printf".equals(methodCall) ||
                "fmt.Errorf".equals(methodCall) ||
                "fmt.Sprintf".equals(methodCall)) {
            checkFormat(result, call, args, false);
            return;
        }

        if ( args.length > 1 &&
            ("fmt.Fscanf".equals(methodCall) || "fmt.Sscanf".equals(methodCall)))
        {
            checkFormat(result, call, Arrays.copyOfRange(args, 1, args.length), true);
            return;
        }

        if ("fmt.Scanf".equals(methodCall)) {
            checkFormat(result, call, args, true);
        }
    }

    private static void checkFormat(InspectionResult result,
                                    GoCallOrConvExpression expression,
                                    GoExpr[]args, boolean isScanning) {

        if (args.length < 1) {
            return;
        }

        GoExpr fmtExpr = args[0];
        if (!(fmtExpr instanceof GoLiteralExpression)) {
            return;
        }

        GoLiteralExpression literalExpression = (GoLiteralExpression)fmtExpr;

        if (literalExpression.getLiteral() == null)
            return;

        GoLiteral literal = literalExpression.getLiteral();

        switch (literal.getType()) {
            case Identifier:
                literal = findConstDefinition((GoLiteralIdentifier)literal);
                if (literal == null ||
                    literal.getType() != InterpretedString &&
                    literal.getType() != RawString)
                   break;
            case InterpretedString:
            case RawString:
                GoLiteralString stringLiteral = (GoLiteralString) literal;
                Context ctx = new Context(stringLiteral,
                                          result,
                                          Arrays.copyOfRange(args, 1, args.length),
                                          isScanning);
                checkFormat(stringLiteral.getValue(), ctx);
                ctx.checkAllExtraParameters();
        }
    }

    private static GoLiteralString findConstDefinition(GoLiteralIdentifier idToFind) {
        String name = idToFind.getName();
        if (idToFind.isBlank() || idToFind.isIota() || name == null || name.isEmpty()) {
            return null;
        }

        PsiElement resolve = GoPsiUtils.resolveSafely(idToFind, PsiElement.class);
        if (resolve == null) {
            return null;
        }

        PsiElement parent = resolve.getParent();
        if (!(parent instanceof GoConstSpec)) {
            return null;
        }

        GoConstSpec cd = ((GoConstSpec) parent);
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

        public final GoLiteral fmtLiteral;
        public final InspectionResult result;
        public final GoExpr[] parameters;
        public final boolean isScanning;
        public int currentParameter = 0;
        public int startOffset = 0;
        public int endOffset = 0;

        private Context(GoLiteral fmtLiteral,
                        InspectionResult result, GoExpr[] parameters, boolean isScanning) {
            this.fmtLiteral = fmtLiteral;
            this.result = result;
            this.parameters = parameters;
            this.isScanning = isScanning;
        }

        public GoExpr getNextParameter() {
            if (currentParameter < parameters.length) {
                GoExpr param = parameters[currentParameter++];
                if (!isScanning) {
                    // TODO: param should be a pointer
                }
                return param;
            }

            missingParameter();
            return null;
        }

        public void checkAllExtraParameters() {
            for (int i = currentParameter; i < parameters.length; i++) {
                extraParameter(parameters[i]);
            }
        }

        public void missingParameter() {
            result.addProblem(fmtLiteral, startOffset + 1, endOffset + 2, "Missing parameter", TYPE);
        }

        public void extraParameter(PsiElement expr) {
            result.addProblem(expr, "Extra parameter", TYPE);
        }

        public void unknownFlag() {
            result.addProblem(fmtLiteral, startOffset, endOffset + 1, "Unknown flag", TYPE);
        }
    }
}
