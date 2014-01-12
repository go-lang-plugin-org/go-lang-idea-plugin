package ro.redeul.google.go.actions;

import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateTemplateInPackageAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.psi.GoFile;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Jun 2, 2012
 */
public class NewGoFileAction extends CreateTemplateInPackageAction<PsiElement>
    implements DumbAware {

    public NewGoFileAction() {
        super(GoBundle.message("new.go.file"),
              GoBundle.message("new.go.file.description"),
              GoIcons.GO_ICON_16x16, JavaModuleSourceRootTypes.SOURCES);
    }

    @Override
    protected PsiElement getNavigationElement(@NotNull PsiElement file) {
        return file;
    }

    @Override
    protected String getErrorTitle() {
        return "New Go file creation";
    }

    //    @Override
    protected boolean checkPackageExists(PsiDirectory directory) {
        return true;
    }

    protected void doCheckCreate(PsiDirectory dir, String parameterName,
                                 String typeName)
        throws IncorrectOperationException {
        // check to see if a file with the same name already exists

        String fileName = fileNameFromTypeName(typeName, parameterName);

        VirtualFile targetFile = dir.getVirtualFile()
                                    .findFileByRelativePath(fileName);
        if (targetFile != null) {
            throw new IncorrectOperationException(
                GoBundle.message("target.file.exists", targetFile.getPath()));
        }
    }

    @Override
    protected PsiElement doCreate(PsiDirectory dir, String parameterName, String typeName)
            throws IncorrectOperationException {
        GoTemplatesFactory.Template template = GoTemplatesFactory.Template.GoFile;

        String fileName = fileNameFromTypeName(typeName, parameterName);
        String packageName = packageNameFromTypeName(typeName, parameterName);

        if (typeName.equals("multiple")) {
            if (dir.findSubdirectory(parameterName) == null) {
                dir = dir.createSubdirectory(parameterName);
            } else {
                dir = dir.findSubdirectory(parameterName);
            }

            fileName = fileName.replaceFirst(parameterName + "/", "");
        }

        return GoTemplatesFactory.createFromTemplate(dir, packageName, fileName, template);
    }

    String fileNameFromTypeName(String typeName, String parameterName) {
        if (typeName.startsWith("lib.")) {
            return parameterName + ".go";
        }

        if (typeName.equals("multiple")) {
            return parameterName + "/" + parameterName + ".go";
        }

        return parameterName + ".go";
    }

    String packageNameFromTypeName(String typeName, String parameterName) {

        if (typeName.startsWith("lib.")) {
            return typeName.replaceFirst("^lib\\.", "");
        }

        return StringUtil.getPackageName(parameterName, '.');
    }

    //    @Override
    protected void buildDialog(Project project, PsiDirectory directory,
                               CreateFileFromTemplateDialog.Builder builder) {

        PsiFile childs[] = directory.getFiles();

        Set<String> packages = new HashSet<String>();

        for (PsiFile child : childs) {
            if (child instanceof GoFile) {
                GoFile goFile = (GoFile) child;

                if (!goFile.getPackage().isMainPackage()) {
                    packages.add(goFile.getPackage().getPackageName());
                }
            }
        }

        builder.addKind("New file", GoIcons.GO_ICON_16x16, "single.go");

        for (String packageName : packages) {
            builder.addKind("New file in library: " + packageName,
                            GoIcons.GO_ICON_16x16, "lib." + packageName);
        }
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName,
                                   String templateName) {
        return GoBundle.message("new.go.lib.action.text");
    }
}
