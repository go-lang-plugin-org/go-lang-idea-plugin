package ro.redeul.google.go.imports;

import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.ShowAutoImportPass;
import com.intellij.codeInsight.daemon.impl.VisibleHighlightingPassFactory;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.inspection.fix.AddImportFix;
import ro.redeul.google.go.inspection.fix.RemoveImportFix;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.options.GoSettings;

import java.util.*;

import static com.intellij.psi.util.PsiTreeUtil.findElementOfClassAtRange;
import static ro.redeul.google.go.lang.psi.utils.GoFileUtils.isPackageNameImported;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;

/**
 * This class search for all "Unresolved symbols" highlights, try to prompt user to import
 * potential packages.
 */
public class AutoImportHighlightingPass extends TextEditorHighlightingPass {
    private final GoFile file;
    private final Editor editor;
    private final TextRange visibleRange;

    public AutoImportHighlightingPass(Project project, GoFile file,
                                      Editor editor) {
        super(project, editor.getDocument(), false);

        this.file = file;
        this.editor = editor;
        this.visibleRange = VisibleHighlightingPassFactory.calculateVisibleRange(
            editor);
    }

    @Override
    public void doCollectInformation(@NotNull ProgressIndicator progress) {
    }

    private Data getVisibleHighlights() {

        Project project = editor.getProject();
        int caretOffset = editor.getCaretModel().getOffset();
        if (project == null) {
            return null;
        }

        GoNamesCache namesCache = GoNamesCache.getInstance(project);

        Set<String> imported = new HashSet<String>();
        for (GoImportDeclarations ids : file.getImportDeclarations()) {
            for (GoImportDeclaration id : ids.getDeclarations()) {
                String name = id.getPackageName();
                if (name != null && !id.getVisiblePackageName().isEmpty()) {
                    imported.add(name);
                }
            }
        }

        Data toImport = null;
        for (RangeHighlighter highlighter : getAllHighlighters(project)) {
            int start = highlighter.getStartOffset();
            int end = highlighter.getEndOffset();
            TextRange range = new TextRange(start, end);
            Object errorStripeTooltip = highlighter.getErrorStripeTooltip();
            if (!visibleRange.contains(range) ||
                editor.getFoldingModel().isOffsetCollapsed(start) ||
                !(errorStripeTooltip instanceof HighlightInfo)) {
                continue;
            }

            // if this a "Unresolved symbol" error
            HighlightInfo info = (HighlightInfo) errorStripeTooltip;
            if (info.getSeverity() != HighlightSeverity.ERROR ||
                !info.getDescription().contains("Unresolved symbol")) {
                continue;
            }

            GoLiteralIdentifier id = findElementOfClassAtRange(file, start, end, GoLiteralIdentifier.class);
            if (!isPackageUsage(id)) {
                continue;
            }

            if (id == null) {
                continue;
            }

            // packages exist
            String expectedPackage = id.isQualified() ? id.getLocalPackageName() : id.getText();
            List<String> sdkPackages = getPackagesByName(
                    namesCache.getSdkPackages(), expectedPackage);
            List<String> projectPackages = getPackagesByName(
                    namesCache.getProjectPackages(), expectedPackage);
            if (imported.contains(expectedPackage) || sdkPackages.size() == 0 && projectPackages.size() == 0) {
                continue;
            }

            toImport = new Data(id, sdkPackages, projectPackages);
            if (id.getTextRange().getEndOffset() > caretOffset) {
                return toImport;
            }
        }
        return toImport;
    }

    private static boolean isPackageUsage(GoLiteralIdentifier id) {
        if (id == null) {
            return false;
        }

        PsiElement parent = id.getParent();
        if (parent instanceof GoLiteralExpression) {
            // package usage in expression
            boolean isTheFirstChild = parent.getStartOffsetInParent() == 0;
            return isTheFirstChild && parent.getParent() instanceof GoSelectorExpression;
        } else if (parent instanceof GoPsiTypeName) {
            // package usage in type name
            return id.isQualified();
        }
        return false;
    }

    public static List<String> getPackagesByName(Collection<String> allPackages,
                                                 String expectedName) {
        List<String> packageFiles = new ArrayList<String>();
        for (String p : allPackages) {
            if (expectedName.equals(p) || p.endsWith(
                "/" + expectedName)) {
                packageFiles.add(p);
            }
        }
        return packageFiles;
    }

    private RangeHighlighter[] getAllHighlighters(Project project) {
        return DocumentMarkupModel.forDocument(editor.getDocument(), project,
                                               true).getAllHighlighters();
    }

    private boolean couldDoAutoImport() {
        // If user is editing the import statement don't optimize it.
        if (isUserEditingImports()) {
            return false;
        }

        // If there is any errors in the document don't optimize it.
        if (containsAnyErrorsInDocument()) {
            return false;
        }

        // If caret is after a dot, don't optimize it
        if (isCaretOnWhiteSpaceAndAfterDot()) {
            return false;
        }

        // if caret is on a package name, don't optimize it.
        return !isCaretOnPackageName();
    }

    private boolean isCaretOnPackageName() {
        int offset = editor.getCaretModel().getOffset();
        //PsiElement caretElement = file.findElementAt(offset);

        return isElementAPackageName(file.findElementAt(offset)) || offset > 0 && isElementAPackageName(file.findElementAt(offset - 1));
    }

    private boolean isElementAPackageName(PsiElement element) {
        return element != null &&
                !isWhiteSpaceNode(element) &&
                isPackageNameImported(file, element.getText());
    }

    /**
     * For code like this:
     *   a.<caret>
     *   fmt.Println()
     *
     *   From syntax definition, "fmt" is only a member field of "a", instead of an usage of package "fmt".
     *   import of "fmt" could be removed incorrectly because of this.
     *   So suppress auto import when caret is on a whitespace and after a dot
     */
    private boolean isCaretOnWhiteSpaceAndAfterDot() {
        int offset = editor.getCaretModel().getOffset();
        PsiElement caretElement = file.findElementAt(offset);
        return caretElement != null && isWhiteSpaceNode(caretElement) &&
               isNodeOfType(getPrevSiblingIfItsWhiteSpaceOrComment(caretElement), GoTokenTypes.oDOT);
    }

    @Override
    public void doApplyInformationToEditor() {
        if (!editor.getContentComponent().hasFocus()) {
            return;
        }

        GoSettings settings = GoSettings.getInstance();
        if (settings.OPTIMIZE_IMPORTS_ON_THE_FLY &&
            couldDoAutoImport()) {
            GoImportOptimizer.optimize(file);
        }

        if (!settings.SHOW_IMPORT_POPUP) {
            return;
        }

        UIUtil.invokeLaterIfNeeded(new Runnable() {
            public void run() {
                if (HintManager.getInstance()
                               .hasShownHintsThatWillHideByOtherHint(true)) {
                    return;
                }

                Data data = getVisibleHighlights();
                if (data == null) {
                    return;
                }

                List<String> allPackages = new ArrayList<String>(
                    data.projectPackages);
                allPackages.addAll(data.sdkPackages);
                String importMessage = getPromptMessage(allPackages);
                AddImportFix fix = new AddImportFix(data.sdkPackages,
                                                    data.projectPackages, file,
                                                    editor);
                GoLiteralIdentifier identifier = data.identifier;
                int start = identifier.getTextOffset();
                int end = identifier.getTextRange().getEndOffset();
                if (identifier.isQualified()) {
                    String localPackageName = identifier.getLocalPackageName();
                    if (localPackageName != null) {
                        end = start + localPackageName.length();
                    }
                }

                HintManager.getInstance()
                           .showQuestionHint(editor, importMessage, start, end,
                                             fix);
            }
        });
    }

    private boolean containsAnyErrorsInDocument() {
        Project project = editor.getProject();
        if (project == null) {
            return false;
        }

        for (RangeHighlighter highlighter : getAllHighlighters(project)) {
            Object errorStripeTooltip = highlighter.getErrorStripeTooltip();
            if (!(errorStripeTooltip instanceof HighlightInfo)) {
                continue;
            }
            HighlightInfo info = (HighlightInfo) errorStripeTooltip;
            if (info.getSeverity() == HighlightSeverity.ERROR &&
                    !containsRemoveImportFix(info)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsRemoveImportFix(HighlightInfo info) {
        if (info.quickFixActionRanges == null) {
            return false;
        }

        for (Pair<HighlightInfo.IntentionActionDescriptor, TextRange> pair : info.quickFixActionRanges) {
            if (pair.getFirst().getAction() instanceof RemoveImportFix) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserEditingImports() {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = getPrevSiblingIfItsWhiteSpaceOrComment(file.findElementAt(offset));
        return findParentOfType(element, GoImportDeclarations.class) != null;
    }

    private String getPromptMessage(List<String> packages) {
        boolean multiple = packages.size() > 1;
        String msg = String.format("Import \"%s\"", packages.iterator().next());
        return ShowAutoImportPass.getMessage(multiple, msg);
    }

    private static final class Data {
        public final GoLiteralIdentifier identifier;
        public final List<String> sdkPackages;
        public final List<String> projectPackages;

        private Data(GoLiteralIdentifier identifier, List<String> sdkPackages,
                     List<String> projectPackages) {
            this.identifier = identifier;
            this.sdkPackages = sdkPackages;
            this.projectPackages = projectPackages;
        }
    }
}
