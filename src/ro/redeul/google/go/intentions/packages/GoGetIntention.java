package ro.redeul.google.go.intentions.packages;

import com.intellij.execution.CantRunException;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.runner.GoCommonConsoleView;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.util.Collection;

public class GoGetIntention extends Intention {

    private static final String TITLE = "go get";

    private static Collection<String> allPackages;

    @Override
    protected boolean satisfiedBy(PsiElement element) {
        if (!(element instanceof GoImportDeclarations)) {
            return false;
        }

        Project project = element.getProject();
        GoGetIntention.allPackages = GoNamesCache.getInstance(project).getAllPackages();

        return !this.getMissingPackages(element).equals("");
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor) throws IntentionExecutionException {

        final Project project = element.getProject();
        final String projectDir = project.getBaseDir().getCanonicalPath();

        String packagesToImport = this.getMissingPackages(element);

        if (packagesToImport.isEmpty()) {
            return;
        }

        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow window = manager.getToolWindow(GoCommonConsoleView.ID);

        if (GoCommonConsoleView.consoleView == null) {
            GoCommonConsoleView.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        }
        ConsoleView consoleView = GoCommonConsoleView.consoleView;

        Sdk sdk = GoSdkUtil.getProjectSdk(project);
        if (sdk == null) {
            return;
        }

        if (window == null) {
            window = manager.registerToolWindow(GoCommonConsoleView.ID, false, ToolWindowAnchor.BOTTOM);

            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
            window.getContentManager().addContent(content);
            window.setIcon(GoSdkUtil.getProjectIcon(sdk));
            window.setToHideOnEmptyContent(true);
        }
        window.setTitle(TITLE);

        String goExecName = GoSdkUtil.getGoExecName(sdk);
        if (goExecName == null) {
            return;
        }

        final String command = String.format(
                "%s get -v %s",
                goExecName,
                packagesToImport
        );

        consoleView.print(String.format("%s%n", command), ConsoleViewContentType.NORMAL_OUTPUT);

        window.show(EmptyRunnable.getInstance());

        final String finalPackagesToImport = packagesToImport.replace(" ", ", ");

        window.activate(new Runnable() {
            @Override
            public void run() {
                try {
                    runGoGet(project, command, finalPackagesToImport, projectDir);
                } catch (CantRunException e) {
                    Messages.showErrorDialog("Error while processing go get command.\n" + e.getMessage(), "Error on Google Go Plugin");
                }
            }
        });
    }

    private String getMissingPackages(PsiElement element) {
        GoImportDeclaration[] declarations = ((GoImportDeclarations) element).getDeclarations();

        String missingPackages = "";

        for (GoImportDeclaration declaration : declarations) {
            if (!declaration.isValidImport() ||
                    declaration.getImportPath() == null) {
                continue;
            }

            String packageImportPath = declaration.getImportPath().getText();
            packageImportPath = packageImportPath.substring(1, packageImportPath.length() - 1);
            boolean hasPackageInPath = false;

            for (String definedPackagePath : GoGetIntention.allPackages) {
                if (definedPackagePath.equals(packageImportPath)) {
                    hasPackageInPath = true;
                    break;
                }
            }

            if (!hasPackageInPath) {
                missingPackages += packageImportPath + " ";
            }
        }

        return missingPackages.trim();
    }

    private void runGoGet(Project project, String command, String packagesToImport, String projectDir) throws CantRunException {
        Sdk sdk = GoSdkUtil.getProjectSdk(project);
        if (sdk == null) {
            return;
        }

        String[] goEnv = GoSdkUtil.getGoEnv(sdk, projectDir);
        if (goEnv == null) {
            return;
        }

        Runtime rt = Runtime.getRuntime();
        ConsoleView consoleView = GoCommonConsoleView.consoleView;

        try {

            Process proc = rt.exec(command, goEnv);
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            consoleView.attachToProcess(handler);

            handler.startNotify();

            if (proc.waitFor() == 0) {
                VirtualFileManager.getInstance().syncRefresh();

                consoleView.print(String.format("%nFinished installing %s%n", packagesToImport), ConsoleViewContentType.NORMAL_OUTPUT);
            } else {
                consoleView.print(String.format("%nCould not install %s%n", packagesToImport), ConsoleViewContentType.ERROR_OUTPUT);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing go get command.", "Error on Google Go Plugin");
        }
    }
}