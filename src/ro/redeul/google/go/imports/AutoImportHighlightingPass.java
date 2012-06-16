package ro.redeul.google.go.imports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.ui.UIUtil;
import ro.redeul.google.go.inspection.fix.AddImportFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import static com.intellij.psi.util.PsiTreeUtil.findElementOfClassAtRange;

/**
 * This class search for all "Unresolved symbols" highlights, try to prompt user to import
 * potential packages.
 */
public class AutoImportHighlightingPass extends TextEditorHighlightingPass {
    private final GoFile file;
    private final Editor editor;
    private TextRange visibleRange;

    public AutoImportHighlightingPass(Project project, GoFile file,
                                      Editor editor) {
        super(project, editor.getDocument(), false);

        this.file = file;
        this.editor = editor;
        this.visibleRange = VisibleHighlightingPassFactory.calculateVisibleRange(
            editor);
    }

    @Override
    public void doCollectInformation(ProgressIndicator progress) {
    }

    private Data getVisibleHighlights() {

        Project project = editor.getProject();
        int caretOffset = editor.getCaretModel().getOffset();
        if (project == null) {
            return null;
        }

        GoNamesCache namesCache = GoNamesCache.getInstance(project);
        if (namesCache == null) {
            return null;
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
                !info.description.contains("Unresolved symbol")) {
                continue;
            }

            PsiElement id = findElementOfClassAtRange(file, start, end,
                                                      GoLiteralIdentifier.class);
            if (id == null) {
                continue;
            }

            // packages exist
            String expectedPackage = id.getText();
            List<String> sdkPackages = getPotentialPackages(
                namesCache.getSdkPackages(), expectedPackage);
            List<String> projectPackages = getPotentialPackages(
                namesCache.getProjectPackages(), expectedPackage);
            if (sdkPackages.size() == 0 && projectPackages.size() == 0) {
                continue;
            }

            toImport = new Data(id, sdkPackages, projectPackages);
            if (id.getTextRange().getEndOffset() > caretOffset) {
                return toImport;
            }
        }
        return toImport;
    }

    private List<String> getPotentialPackages(Collection<String> allPackages,
                                              String expectedPackage) {
        List<String> packageFiles = new ArrayList<String>();
        for (String p : allPackages) {
            if (expectedPackage.equals(p) || p.endsWith(
                "/" + expectedPackage)) {
                packageFiles.add(p);
            }
        }
        return packageFiles;
    }

    private RangeHighlighter[] getAllHighlighters(Project project) {
        return DocumentMarkupModel.forDocument(editor.getDocument(), project,
                                               true).getAllHighlighters();
    }

    @Override
    public void doApplyInformationToEditor() {
        if (!editor.getContentComponent().hasFocus()) {
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
                int start = data.element.getTextOffset();
                int end = data.element.getTextRange().getEndOffset();
                HintManager.getInstance()
                           .showQuestionHint(editor, importMessage, start, end,
                                             fix);
            }
        });
    }

    private String getPromptMessage(List<String> packages) {
        boolean multiple = packages.size() > 1;
        String msg = String.format("Import \"%s\"", packages.iterator().next());
        return ShowAutoImportPass.getMessage(multiple, msg);
    }

    private static final class Data {
        public final PsiElement element;
        public final List<String> sdkPackages;
        public final List<String> projectPackages;

        private Data(PsiElement element, List<String> sdkPackages,
                     List<String> projectPackages) {
            this.element = element;
            this.sdkPackages = sdkPackages;
            this.projectPackages = projectPackages;
        }
    }
}
