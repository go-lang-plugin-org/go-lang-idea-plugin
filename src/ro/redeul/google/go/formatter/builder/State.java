package ro.redeul.google.go.formatter.builder;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import ro.redeul.google.go.formatter.blocks.GoBlockUtil;

import java.util.Map;

/**
 * <p/>
 * Created on Jan-13-2014 22:14
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
final class State {

    final CommonCodeStyleSettings settings;
    final Indent indent;
    final Map<GoBlockUtil.Alignments.Key, Alignment> alignmentsMap;
    final Alignment alignment;

    public State(CommonCodeStyleSettings settings, Indent indent, Alignment alignment,
                 Map<GoBlockUtil.Alignments.Key, Alignment> alignmentsMap) {
        this.settings = settings;
        this.indent = indent;
        this.alignmentsMap = alignmentsMap;
        this.alignment = alignment;
    }
}
