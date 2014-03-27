package ro.redeul.google.go.actions;

import com.intellij.ide.fileTemplates.*;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoIcons;

import java.io.File;
import java.util.Properties;

public class GoTemplatesFactory implements FileTemplateGroupDescriptorFactory {

    public enum Template {
        GoAppMain("Go Application"), GoFile("Go File"), GoTestFile("Go Test File"),
        GoAppEngineMain("Go App Engine Application"), GoAppEngineConfig("Go App Engine YAML");

        final String file;
        Template(String file) {
            this.file = file;
        }

        public String getFile() {
            return file;
        }
    }

    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        String title = GoBundle.message("file.template.group.title.go");
        final FileTemplateGroupDescriptor group =
                new FileTemplateGroupDescriptor(title, GoIcons.GO_ICON_16x16);

        for (Template template : Template.values()) {
            group.addTemplate(new FileTemplateDescriptor(template.getFile(), GoIcons.GO_ICON_16x16));
        }

        return group;
    }

    public static PsiElement createFromTemplate(PsiDirectory directory, String name, String fileName, Template template) {
        String packageName = directory.getName();

        return createFromTemplate(directory, packageName, name, fileName, template);
    }

    public static PsiElement createFromTemplate(PsiDirectory directory, String packageName, String name, String fileName, Template template) {

        final FileTemplate fileTemplate = FileTemplateManager.getInstance().getInternalTemplate(template.getFile());

        Properties properties = new Properties(FileTemplateManager.getInstance().getDefaultProperties());

        properties.setProperty("PACKAGE_NAME", packageName);
        properties.setProperty("NAME", name);

        String text;
        try {
            text = fileTemplate.getText(properties);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to load template for " + template.getFile(), e);
        }

        final PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());

        if ((new File(fileName)).exists()) {
            throw new RuntimeException("File already exists");
        }

        final PsiFile file = factory.createFileFromText(fileName, GoFileType.INSTANCE, text);

        return directory.add(file);
    }
}
