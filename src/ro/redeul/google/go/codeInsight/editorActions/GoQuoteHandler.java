package ro.redeul.google.go.codeInsight.editorActions;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

public class GoQuoteHandler extends SimpleTokenSetQuoteHandler {
    public GoQuoteHandler() {
        super(GoTokenTypeSets.STRINGS);
    }

    @Override
    public boolean isOpeningQuote(HighlighterIterator iterator, int offset) {
        return iterator.getTokenType() == GoTokenTypes.mWRONG || super.isOpeningQuote(iterator, offset);
    }

    @Override
    public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator iterator, int offset) {
        return true;
    }
}
