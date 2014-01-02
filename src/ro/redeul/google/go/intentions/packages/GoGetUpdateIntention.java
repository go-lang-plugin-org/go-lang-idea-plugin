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
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Collection;

public class GoGetUpdateIntention extends Intention {

    private static final String ID = "Go Console";
    private static final String TITLE = "go get";

    private static Collection<String> allPackages;
    private static ConsoleView consoleView;

    @Override
    protected boolean satisfiedBy(PsiElement element) {
        if (!(element instanceof GoImportDeclarations)) {
            return false;
        }

        Project project = element.getProject();
        GoGetUpdateIntention.allPackages = GoNamesCache.getInstance(project).getAllPackages();

        return !this.processElement(element).equals("");
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor) throws IntentionExecutionException {

        final Project project = element.getProject();
        final String projectDir = project.getBaseDir().getCanonicalPath();


        String packagesToUpdate = this.getUpdateablePackages(element);
        String packagesToImport = (this.getMissingPackages(element) + " " + packagesToUpdate).trim();

        if (packagesToImport.isEmpty()) {
            return;
        }

        if (!packagesToUpdate.isEmpty()) {
            if (Messages.showYesNoDialog(
                    "This will UPDATE ALL the following package(s)\n" + packagesToUpdate.replaceAll(" ", "\n") + "\n Are you sure you want to continue?",
                    "go get -u",
                    GoIcons.GO_ICON_13x13) == Messages.NO) {
                return;
            }
        }

        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow window = manager.getToolWindow(ID);

        if (GoGetUpdateIntention.consoleView == null) {
            GoGetUpdateIntention.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
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

        Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
        if ( sdk == null ) {
            Messages.showErrorDialog("Error while processing go get command.\nNo Go Sdk defined for this project", "Error on Google Go Plugin");
            return;
        }


        final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
        if ( sdkData == null ) {
            Messages.showErrorDialog("Error while processing go get command.\nNo Go Sdk defined for this project", "Error on Google Go Plugin");
            return;
        }


        final String command = String.format(
                "%s get -v -u %s",
                sdkData.GO_BIN_PATH,
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

    private String processElement(PsiElement element) {
        return (this.getMissingPackages(element) + " " + this.getUpdateablePackages(element)).trim();
    }

    private String getUpdateablePackages(PsiElement element) {
        GoImportDeclaration[] declarations = ((GoImportDeclarations) element).getDeclarations();

        String externalPackages = "";

        Project project = element.getProject();

        for (GoImportDeclaration declaration : declarations) {
            if (!declaration.isValidImport() ||
                    declaration.getImportPath() == null) {
                continue;
            }

            String packageImportPath = declaration.getImportPath().getText();
            packageImportPath = packageImportPath.substring(1, packageImportPath.length() - 1);
            boolean hasPackageInPath = false;

            for (String definedPackagePath : GoGetUpdateIntention.allPackages) {
                if (definedPackagePath.equals(packageImportPath)) {
                    hasPackageInPath = true;
                    break;
                }
            }

            if (hasPackageInPath && GoSdkUtil.isImportedPackage(project, packageImportPath)) {
                externalPackages += packageImportPath + " ";
            }
        }

        return externalPackages.trim();
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

            for (String definedPackagePath : GoGetUpdateIntention.allPackages) {
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
        Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
        if ( sdk == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
        if ( sdkData == null ) {
            throw new CantRunException("No Go Sdk defined for this project");
        }

        String[] goEnv = GoSdkUtil.getExtendedGoEnv(sdkData, projectDir, "");

        Runtime rt = Runtime.getRuntime();

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

    private String getSdkHomePath(GoSdkData sdkData) {
        if (sdkData.GO_GOROOT_PATH.isEmpty()) {
            return new File(sdkData.GO_BIN_PATH).getParent();
        }
        return sdkData.GO_GOROOT_PATH;
    }
}