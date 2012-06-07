package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.util.TextRange;

public class InspectionUtil {
    public static TextRange getProblemRange(ProblemDescriptor pd) {
        int start = pd.getStartElement().getTextOffset();
        int end = pd.getEndElement().getTextOffset() + pd.getEndElement().getTextLength();
        return new TextRange(start, end);
    }
}
