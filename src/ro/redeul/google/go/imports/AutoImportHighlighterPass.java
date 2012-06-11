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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;
import ro.redeul.google.go.inspection.fix.AddImportFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoLiteralExpression;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.intellij.psi.util.PsiTreeUtil.findElementOfClassAtRange;

/**
 * This class search for all "Unresolved symbols" highlights, try to prompt user to import
 * potential packages.
 */
public class AutoImportHighlighterPass extends TextEditorHighlightingPass {
    private final GoFile file;
    private final Editor editor;
    private TextRange visibleRange;
    private final AtomicReference<Data> toImport = new AtomicReference<Data>();

    public AutoImportHighlighterPass(Project project, GoFile file, Editor editor) {
        super(project, editor.getDocument(), false);

        this.file = file;
        this.editor = editor;
        this.visibleRange = VisibleHighlightingPassFactory.calculateVisibleRange(editor);
    }

    @Override
    public void doCollectInformation(ProgressIndicator progress) {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
            @Override
            public void run() {
                getVisibleHighlights();
            }
        });
    }

    private void getVisibleHighlights() {
        toImport.set(null);

        Project project = editor.getProject();
        int caretOffset = editor.getCaretModel().getOffset();
        if (project == null) {
            return;
        }

        GoNamesCache namesCache = getGoNamesCache(project);
        if (namesCache == null) {
            return;
        }

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
            if (info.getSeverity() != HighlightSeverity.ERROR || !info.description.contains("Unresolved symbol")) {
                continue;
            }

            GoLiteralExpression id = findElementOfClassAtRange(file, start, end, GoLiteralExpression.class);
            if (id == null) {
                continue;
            }

            // packages exist
            List<String> packages = getAllPotentialPackages(namesCache, id);
            if (packages.size() == 0) {
                continue;
            }

            toImport.set(new Data(id, packages));
            if (id.getTextRange().getEndOffset() > caretOffset) {
                return;
            }
        }
    }

    private List<String> getAllPotentialPackages(GoNamesCache namesCache, GoLiteralExpression id) {
        String expectedPackage = id.getText();
        List<String> packageFiles = new ArrayList<String>();
        for (String p : namesCache.getAllPackages()) {
            if (expectedPackage.equals(p) || p.endsWith("/" + expectedPackage)) {
                packageFiles.add(p);
            }
        }
        return packageFiles;
    }

    private GoNamesCache getGoNamesCache(Project project) {
        PsiShortNamesCache[] extensions = project.getExtensions(PsiShortNamesCache.EP_NAME);
        return ContainerUtil.findInstance(extensions, GoNamesCache.class);
    }

    private RangeHighlighter[] getAllHighlighters(Project project) {
        return DocumentMarkupModel.forDocument(editor.getDocument(), project, true).getAllHighlighters();
    }

    @Override
    public void doApplyInformationToEditor() {
        final Data data = toImport.get();
        if (data == null || !editor.getContentComponent().hasFocus()) {
            return;
        }

        UIUtil.invokeLaterIfNeeded(new Runnable() {
            public void run() {
                if (HintManager.getInstance().hasShownHintsThatWillHideByOtherHint(true)) {
                    return;
                }

                String importMessage = getPromptMessage(data.packages);
                AddImportFix fix = new AddImportFix(data.packages, file, editor);
                int start = data.element.getTextOffset();
                int end = data.element.getTextRange().getEndOffset();
                HintManager.getInstance().showQuestionHint(editor, importMessage, start, end, fix);
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
        public final List<String> packages;

        private Data(PsiElement element, List<String> packages) {
            this.element = element;
            this.packages = packages;
        }
    }
}
