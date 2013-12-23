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
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Collection;

public class GoGetIntention extends Intention {

    private static final String ID = "Go Console";
    private static final String TITLE = "go get";

    private static Collection<String> allPackages;
    private static ConsoleView consoleView;

    @Override
    protected boolean satisfiedBy(PsiElement element) {
        if (!(element instanceof GoImportDeclarations || element instanceof GoLiteralString)) {
            return false;
        }

        Project project = element.getProject();
        GoGetIntention.allPackages = GoNamesCache.getInstance(project).getAllPackages();

        return !this.processElement(element).equals("");
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor) throws IntentionExecutionException {

        Project project = element.getProject();
        String projectDir = project.getBaseDir().getCanonicalPath();

        String packageImportPath = this.processElement(element);

        if (packageImportPath.equals("")) {
            return;
        }

        try {
            runGoGet(project, packageImportPath, projectDir);
        } catch (CantRunException ignored) {

        }
    }

    private String processElement(PsiElement element) {
        if (element instanceof GoLiteralString) {
            if (element.getParent() ==  null ||
                    element.getParent().getParent() == null ||
                    !(element.getParent().getParent() instanceof GoImportDeclarations)) {
                return "";
            }

            String packageImportPath = element.getText();
            packageImportPath = packageImportPath.substring(1, packageImportPath.length()-1);
            boolean hasPackageInPath = false;

            for (String definedPackagePath : GoGetIntention.allPackages) {
                if (definedPackagePath.equals(packageImportPath)) {
                    hasPackageInPath = true;
                    break;
                }
            }

            return hasPackageInPath ? "" : packageImportPath;
        }

        GoImportDeclaration[] declarations = ((GoImportDeclarations) element).getDeclarations();

        for (GoImportDeclaration declaration : declarations) {
            if (!declaration.isValidImport()) {
                return "";
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
                return packageImportPath;
            }
        }

        return "";
    }

    private void runGoGet(Project project, String packageImportPath, String projectDir) throws CantRunException {
        Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
        if ( sdk == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
        if ( sdkData == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        try {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            ToolWindow window = manager.getToolWindow(ID);

            if (GoGetIntention.consoleView == null) {
                GoGetIntention.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
            }

            if (window == null) {
                window = manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);

                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
                window.getContentManager().addContent(content);
                window.setIcon(GoIcons.GO_ICON_13x13);
                window.setToHideOnEmptyContent(true);
                window.setTitle(TITLE);

            }

            window.show(EmptyRunnable.getInstance());

            String[] goEnv = GoSdkUtil.getExtendedGoEnv(sdkData, projectDir, "");

            String command = String.format(
                    "%s get %s",
                    sdkData.GO_BIN_PATH,
                    packageImportPath
            );

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, goEnv);
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            consoleView.attachToProcess(handler);
            consoleView.print(String.format("%s%n", command), ConsoleViewContentType.NORMAL_OUTPUT);
            handler.startNotify();

            if (proc.waitFor() == 0) {
                VirtualFileManager.getInstance().syncRefresh();

                consoleView.print(String.format("%nFinished installing %s%n", packageImportPath), ConsoleViewContentType.NORMAL_OUTPUT);
            } else {
                consoleView.print(String.format("%nCould not install %s%n", packageImportPath), ConsoleViewContentType.ERROR_OUTPUT);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing go get command.", "Error on Google Go Plugin");
        }
    }

    private String getSdkHomePath(GoSdkData sdkData) {
        if (sdkData.GO_GOROOT_PATH.isEmpty()) {
            return new File(sdkData.GO_BIN_PATH).getParent();
        }
        return sdkData.GO_GOROOT_PATH;
    }
}