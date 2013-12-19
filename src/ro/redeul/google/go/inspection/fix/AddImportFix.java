package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInsight.hint.QuestionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment;
import static ro.redeul.google.go.util.EditorUtil.reformatLines;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class AddImportFix implements QuestionAction {
    private final List<String> pathsToImport = new ArrayList<String>();
    private final List<String> sdkPackages;
    private final GoFile file;
    private final Editor editor;

    public AddImportFix(List<String> sdkPackages, List<String> projectPackages, GoFile file, Editor editor) {
        this.sdkPackages = sdkPackages;
        this.file = file;
        this.editor = editor;
        pathsToImport.addAll(projectPackages);
        pathsToImport.addAll(sdkPackages);
    }

    @Override
    public boolean execute() {
        if (pathsToImport.isEmpty()) {
            return true;
        }

        if (pathsToImport.size() == 1) {
            addImport(file, editor, pathsToImport.get(0));
        } else {
            JBPopupFactory popup = JBPopupFactory.getInstance();
            popup.createListPopup(new ChoosePackagePopupStep()).showInBestPositionFor(editor);
        }
        return true;
    }

    public static void addImport(final GoFile file, final Editor editor, final String pathToImport) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(file.getProject(), new Runnable() {
                    @Override
                    public void run() {
                        doAddImport(file, editor, pathToImport);
                    }
                }, null, null);
            }
        });
    }

    private static void doAddImport(GoFile file, Editor editor, String pathToImport) {
        GoImportDeclarations[] ids = file.getImportDeclarations();
        Document document = editor.getDocument();

        if (ids.length == 0) {
            addImportUnderPackage(file, document, pathToImport);
            return;
        }

        GoImportDeclarations importDeclarations = ids[ids.length - 1];
        GoImportDeclaration[] imports = importDeclarations.getDeclarations();

        if (imports.length == 0) {
            addImportUnderPackage(file, document, pathToImport);
            return;
        }

        GoImportDeclaration lastImport = imports[imports.length - 1];

        PsiElement lastChild = getPrevSiblingIfItsWhiteSpaceOrComment(importDeclarations.getLastChild());
        if (lastChild == null) {
            addImportUnderPackage(file, document, pathToImport);
            return;
        }

        if (")".equals(lastChild.getText())) {
            document.insertString(lastChild.getTextOffset(), "\"" + pathToImport + "\"\n");
            int line = document.getLineNumber(lastChild.getTextOffset());
            reformatLines(file, editor, line, line);
        } else {
            String oldImport = lastImport.getText();
            int start = lastImport.getTextOffset();
            int end = start + lastImport.getTextLength();
            String declarations = String.format("(\n%s\n\"%s\"\n)", oldImport, pathToImport);
            document.replaceString(start, end, declarations);
            reformatPositions(file, start, start + declarations.length());
        }
    }

    private static void addImportUnderPackage(GoFile file, Document document, String pathToImport) {
        int insertPoint = file.getPackage().getTextRange().getEndOffset();
        document.insertString(insertPoint, String.format("\n\nimport \"%s\"", pathToImport));
    }

    private class ChoosePackagePopupStep extends BaseListPopupStep<String> {
        public ChoosePackagePopupStep() {
            super("Choose package to import", pathsToImport);
        }

        @Override
        public boolean isSpeedSearchEnabled() {
            return true;
        }

        @Override
        public boolean isAutoSelectionEnabled() {
            return false;
        }

        @Override
        public Icon getIconFor(String aValue) {
            return sdkPackages.contains(aValue) ? PlatformIcons.LIBRARY_ICON : PlatformIcons.PACKAGE_ICON;
        }

        @Override
        public PopupStep onChosen(String selectedValue, boolean finalChoice) {
            if (finalChoice) {
                addImport(file, editor, selectedValue);
            }
            return FINAL_CHOICE;
        }
    }
}
