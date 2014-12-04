package ro.redeul.google.go.autoImport;

import com.intellij.codeInsight.daemon.ReferenceImporter;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerEx;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.inspection.fix.AddImportFix;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.refs.PackageReference;

import java.util.ArrayList;
import java.util.List;

public class GoReferenceImporter implements ReferenceImporter {

    @Override
    public boolean autoImportReferenceAtCursor(@NotNull final Editor editor, @NotNull final PsiFile file) {
        return autoImportReferenceAt(editor, file, editor.getCaretModel().getOffset());
    }

    @Override
    public boolean autoImportReferenceAt(@NotNull final Editor editor, @NotNull PsiFile file, int offset) {
        if (!file.getViewProvider().getLanguages().contains(GoLanguage.INSTANCE)) return false;

        PackageReference packageReference = getPackageReferenceAt(file, offset);

        if (packageReference == null)
            return false;


        PsiElement element = file.findElementAt(offset);
        if (element == null)
            return false;

        while (!(element instanceof GoLiteralIdentifier) && element.getStartOffsetInParent() == 0)
            element = element.getParent();

        if (!(element instanceof GoLiteralIdentifier))
            return false;

        GoLiteralIdentifier identifier = (GoLiteralIdentifier)element;

        final Project project = editor.getProject();
        Document document = editor.getDocument();

        if (project == null)
            return false;

        final List<AddImportFix> fixes = new ArrayList<AddImportFix>();
        DaemonCodeAnalyzerEx.processHighlights(editor.getDocument(), editor.getProject(), HighlightSeverity.ERROR, offset, offset + element.getTextLength(), new Processor<HighlightInfo>() {
            @Override
            public boolean process(HighlightInfo highlightInfo) {
                for (Pair<HighlightInfo.IntentionActionDescriptor, RangeMarker> quickFixActionMarker : highlightInfo.quickFixActionMarkers) {
                    IntentionAction action = quickFixActionMarker.getFirst().getAction();
                    if ( action instanceof AddImportFix ) {
                        fixes.add((AddImportFix)action);
                    }
                }
                return true;
            }
        });

        for (AddImportFix fix : fixes) {
            if (fix.isAvailable(project, file, identifier, identifier) && fix.execute())
                return false;
        }

        return false;
    }

    @Nullable
    public static PackageReference getPackageReferenceAt(PsiFile file, int offset) {
        PsiReference reference = file.findReferenceAt(offset);

        if ( reference == null ) return null;
        if ( reference instanceof PackageReference)
            return (PackageReference) reference;

        if ( reference instanceof PsiMultiReference ) {
            PsiMultiReference multiReference = (PsiMultiReference) reference;
            for (PsiReference ref : multiReference.getReferences()) {
                if ( ref instanceof PackageReference)
                    return (PackageReference) ref;
            }
        }

        return null;
    }

}
