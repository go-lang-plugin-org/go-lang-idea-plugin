package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.QuestionAction;
import com.intellij.codeInspection.HintAction;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.autoImport.GoReferenceImporter;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.refs.PackageReference;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment;
import static ro.redeul.google.go.util.EditorUtil.reformatLines;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class AddImportFix extends LocalQuickFixAndIntentionActionOnPsiElement implements HintAction, QuestionAction {

    public static final ListCellRendererWrapper<String> ImportPathCellRenderer = new ListCellRendererWrapper<String>() {
        @Override
        public void customize(JList list, String value, int index, boolean selected, boolean hasFocus) {
            setText("import \"" + value + "\"");
            setIcon(AllIcons.Nodes.Package);
        }
    };

    public AddImportFix(@NotNull GoLiteralIdentifier identifier) {
        super(identifier);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull final PsiFile psiFile, @Nullable Editor editor,
                       @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        _invoke(project, psiFile, editor, startElement, endElement);
    }

    protected boolean _invoke(@NotNull Project project, @NotNull final PsiFile psiFile, @Nullable Editor editor,
                              @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        final Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);

        if (document == null)
            return false;

        if (!(psiFile instanceof GoFile))
            return false;

        final GoFile file = (GoFile) psiFile;

        List<String> importPaths = findPotentialImports(startElement);
        final JList list = new JBList(importPaths);
        list.setCellRenderer(ImportPathCellRenderer);
        list.setSelectedIndex(0);

        final Runnable importCreatorRunnable = new Runnable() {

            @Override
            public void run() {
                final String importPath = (String) list.getSelectedValue();
                if (importPath != null) {
                    final Project project = psiFile.getProject();
                    new WriteCommandAction.Simple(project, psiFile) {
                        @Override
                        protected void run() throws Throwable {
                            addImportDeclaration(importPath, file, document);
                        }
                    }.execute();
                }
            }
        };

        if (importPaths.size() == 1) {
            importCreatorRunnable.run();
            return true;
        }

        if (editor != null)
            new PopupChooserBuilder(list)
                    .setTitle("Choose package path")
                    .setItemChoosenCallback(importCreatorRunnable)
                    .createPopup()
                    .showInBestPositionFor(editor);

        return false;
    }

    @NotNull
    @Override
    public String getText() {
        return "import package \"" + getStartElement().getText() + "\"";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Add imports for package references";
    }

    @Nullable
    protected GoLiteralIdentifier getIdentifier() {
        return GoLiteralIdentifier.class.cast(getStartElement());
    }

    /*
     * {@see HintAction#showHint()}
     */
    @Override
    public boolean showHint(@NotNull Editor editor) {
        PsiElement element = getStartElement();

        if (element == null || !element.isValid() || element.getContainingFile() == null )
            return false;

        List<String> importPaths = findPotentialImports(element);

        if (importPaths.size() != 1) return false;

        HintManager.getInstance().showQuestionHint(
                editor,
                "import package: \"" + importPaths.get(0) + "\"",
                element.getTextOffset(),
                element.getTextOffset() + element.getTextLength(),
                this);

        return true;
    }

    /*
     * {@see QuestionAction#execute()
     */
    @Override
    public boolean execute() {
        PsiElement start = getStartElement();
        PsiElement end = getEndElement();

        if ( start == null )
            return false;

        if ( end == null )
            end = start;

        return _invoke(start.getProject(), start.getContainingFile(), null, start, end);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        PackageReference reference = GoReferenceImporter.getPackageReferenceAt(file, startElement.getTextOffset());
        return reference != null && reference.resolve() == null;
    }

    @NotNull
    private static List<String> findPotentialImports(@NotNull PsiElement element) {
        if ( !(element instanceof GoLiteralIdentifier))
            return Collections.emptyList();

        String packageName = element.getText();

        Collection<GoPackage> packages = GoNamesCache.getInstance(element.getProject()).getPackagesByName(packageName);
        return ContainerUtil.map(packages, new Function<GoPackage, String>() {
            @Override
            public String fun(GoPackage myPackage) {
                return myPackage.getImportPath();
            }
        });
    }

    private static void addImportUnderPackage(@NotNull GoFile file, @NotNull Document document, String pathToImport) {
        int insertPoint = file.getPackage().getTextRange().getEndOffset();
        document.insertString(insertPoint, String.format("\n\nimport \"%s\"", pathToImport));
    }

    private void addImportDeclaration(String pathToImport, @NotNull GoFile file, @NotNull final Document document) {

        GoImportDeclarations[] ids = file.getImportDeclarations();

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
            reformatLines(file, document, line, line);
        } else {
            String oldImport = lastImport.getText();
            int start = lastImport.getTextOffset();
            int end = start + lastImport.getTextLength();
            String declarations = String.format("(\n%s\n\"%s\"\n)", oldImport, pathToImport);
            document.replaceString(start, end, declarations);
            reformatPositions(file, start, start + declarations.length());
        }
    }
}

