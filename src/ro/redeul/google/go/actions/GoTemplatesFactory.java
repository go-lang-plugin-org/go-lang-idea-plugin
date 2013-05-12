package ro.redeul.google.go.actions;

import java.util.Properties;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.psi.GoFile;

public class GoTemplatesFactory implements FileTemplateGroupDescriptorFactory {

    public enum Template {
        GoAppMain("Go Application"), GoFile("Go File"), GoTestFile("Go Test File");

        String file;
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

    public static GoFile createFromTemplate(PsiDirectory directory, String name, String fileName, Template template) {

        final FileTemplate fileTemplate = FileTemplateManager.getInstance().getInternalTemplate(template.getFile());

        Properties properties = new Properties(FileTemplateManager.getInstance().getDefaultProperties());

        properties.setProperty("PACKAGE_NAME", directory.getName());
        properties.setProperty("NAME", name);

        String text;
        try {
            text = fileTemplate.getText(properties);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to load template for " + template.getFile(), e);
        }

        final PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());
        final PsiFile file = factory.createFileFromText(fileName, GoFileType.INSTANCE, text);

        return (GoFile) directory.add(file);
    }
}
