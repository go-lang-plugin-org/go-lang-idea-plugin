package ro.redeul.google.go.findUsages;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralExprImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoVariableUsageStatVisitor extends GoRecursiveElementVisitor2 {
    private List<ProblemDescriptor> problems = new ArrayList<ProblemDescriptor>();
    private InspectionManager manager;
    private Ctx ctx;

    public GoVariableUsageStatVisitor(InspectionManager manager) {
        this.manager = manager;
    }

    public ProblemDescriptor[] getProblems() {
        return problems.toArray(new ProblemDescriptor[problems.size()]);
    }

    private void beforeVisitFile(GoFile file) {
        ctx = new Ctx(problems, manager, getGlobalVariables(file));
    }

    private void afterVisitFile(GoFile file) {
        for (Var v : ctx.popLastScopeLevel().values()) {
            if (!v.isUsed()) {
                ctx.unusedGlobalVariable(v);
            }
        }
    }

    @Override
    protected void beforeVisitElement(PsiElement element) {
        if (element instanceof GoFile) {
            beforeVisitFile((GoFile) element);
        } else if (element instanceof GoVarDeclarations) {
            for (GoVarDeclaration gvd : ((GoVarDeclarations) element).getDeclarations()) {
                beforeVisitGoVarDeclaration(gvd);
            }
        } else if (element instanceof GoVarDeclaration) {
            beforeVisitGoVarDeclaration((GoVarDeclaration) element);
        } else if (element instanceof GoConstDeclarations) {
            for (GoConstDeclaration gcd : ((GoConstDeclarations) element).getDeclarations()) {
                beforeVisitGoConstDeclaration(gcd);
            }
        } else if (element instanceof GoConstDeclaration) {
            beforeVisitGoConstDeclaration((GoConstDeclaration) element);
        } else if (element instanceof GoIdentifier) {
            beforeVisitIdentifier((GoIdentifier) element);
        } else if (element instanceof GoFunctionDeclaration) {
            beforeVisitFunctionDeclaration((GoFunctionDeclaration) element);
        } else if (couldOpenNewScope(element)) {
            ctx.addNewScopeLevel();
        }
    }

    private static boolean couldOpenNewScope(PsiElement element) {
        if (!(element instanceof GoPsiElementBase)) {
            return false;
        }

        GoPsiElementBase gpeb = (GoPsiElementBase) element;
        IElementType tt = gpeb.getTokenType();
        return tt == GoElementTypes.IF_STATEMENT || tt == GoElementTypes.FOR_STATEMENT || tt == GoElementTypes.BLOCK_STATEMENT;
    }

    @Override
    protected void afterVisitElement(PsiElement element) {
        if (element instanceof GoFile) {
            afterVisitFile((GoFile) element);
        } else if (element instanceof GoFunctionDeclaration) {
            afterVisitGoFunctionDeclaration((GoFunctionDeclaration) element);
        } else if (couldOpenNewScope(element)) {
            for (Var v : ctx.popLastScopeLevel().values()) {
                if (!v.isUsed()) {
                    ctx.unusedVariable(v);
                }
            }
        }
    }

    private void beforeVisitGoConstDeclaration(GoConstDeclaration gcd) {
        for (GoIdentifier id : gcd.getIdentifiers()) {
            ctx.addDefinition(id);
        }
    }

    private void beforeVisitGoVarDeclaration(GoVarDeclaration gvd) {
        for (GoIdentifier id : gvd.getIdentifiers()) {
            ctx.addDefinition(id);
        }
    }

    public void beforeVisitIdentifier(GoIdentifier id) {
        if (isFunctionOrMethodCall(id)) {
            return;
        }
        ctx.addUsage(id);
    }

    private boolean isFunctionOrMethodCall(GoIdentifier id) {
        if (!(id.getParent() instanceof GoLiteralExprImpl)) {
            return false;
        }

        PsiElement grandpa = id.getParent().getParent();
        return grandpa.getNode().getElementType() == GoElementTypes.CALL_OR_CONVERSION_EXPRESSION &&
                grandpa.getFirstChild().isEquivalentTo(id.getParent());
    }

    public void beforeVisitFunctionDeclaration(GoFunctionDeclaration fd) {
        getFunctionParameters(fd);
    }

    public void afterVisitGoFunctionDeclaration(GoFunctionDeclaration fd) {
        for (Var v : ctx.popLastScopeLevel().values()) {
            if (!v.isUsed()) {
                ctx.unusedParameter(v);
            }
        }
    }
    private Map<String, Var> getFunctionParameters(GoFunctionDeclaration fd) {
        Map<String, Var> variables = ctx.addNewScopeLevel();
        GoFunctionParameterList parameters = fd.getParameters();
        if (parameters == null) {
            return variables;
        }

        GoFunctionParameter[] functionParameters = parameters.getFunctionParameters();
        if (functionParameters == null) {
            return variables;
        }

        for (GoFunctionParameter fp : functionParameters) {
            for (GoIdentifier id : fp.getIdentifiers()) {
                variables.put(id.getName(), new Var(id));
            }
        }
        return variables;
    }

    private Map<String, Var> getGlobalVariables(GoFile file) {
        Map<String, Var> variables = new HashMap<String, Var>();
        for (GoConstDeclarations allConsts : file.getConsts()) {
            for (GoConstDeclaration cd : allConsts.getDeclarations()) {
                for (GoIdentifier id : cd.getIdentifiers()) {
                    variables.put(id.getName(), new Var(id));
                }
            }
        }

        for (GoVarDeclarations allVariables : file.getGlobalVariables()) {
            for (GoVarDeclaration vd : allVariables.getDeclarations()) {
                for (GoIdentifier id : vd.getIdentifiers()) {
                    variables.put(id.getName(), new Var(id));
                }
            }
        }
        return variables;
    }
}
