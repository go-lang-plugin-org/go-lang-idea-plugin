package ro.redeul.google.go.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.IndexPatternUtil;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer;
import com.intellij.psi.search.IndexPattern;
import ro.redeul.google.go.lang.lexer.GoLexer;
import ro.redeul.google.go.lang.parser.GoElementTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoToDoIndexer extends LexerBasedTodoIndexer {
    @Override
    public Lexer createLexer(final OccurrenceConsumer consumer) {
        return new GoTodoLexer(consumer);
    }

    public static class TodoScanningData {
        final IndexPattern pattern;
        final Matcher matcher;

        public TodoScanningData(IndexPattern pattern, Matcher matcher) {
            this.matcher = matcher;
            this.pattern = pattern;
        }
    }

    private static class GoTodoLexer extends GoLexer {
        TodoScanningData[] todoScanningData;
        private final OccurrenceConsumer consumer;

        public GoTodoLexer(OccurrenceConsumer consumer) {
            this.consumer = consumer;
        }

        private TodoScanningData[] getTodoScanningData() {
            if (todoScanningData != null) {
                return todoScanningData;
            }

            IndexPattern[] patterns = IndexPatternUtil.getIndexPatterns();
            todoScanningData = new TodoScanningData[patterns.length];

            for (int i = 0; i < patterns.length; ++i) {
                IndexPattern indexPattern = patterns[i];
                Pattern pattern = indexPattern.getPattern();

                if (pattern != null) {
                    todoScanningData [i] = new TodoScanningData(indexPattern, pattern.matcher(""));
                }
            }
            return todoScanningData;
        }

        @Override
        public void advance() {
            if (consumer.isNeedToDo() && GoElementTypes.COMMENTS.contains(getTokenType())) {
                for (TodoScanningData data : getTodoScanningData()) {
                    if (data == null) {
                        continue;
                    }
                    Matcher matcher = data.matcher;
                    matcher.reset(getTokenText());

                    while (matcher.find()) {
                        if (matcher.start() != matcher.end()) {
                            consumer.incTodoOccurrence(data.pattern);
                        }
                    }
                }

            }

            super.advance();
        }
    }
}
