#!/bin/sh

if [ -z "$IDEA_HOME" ]; then
    IDEA_HOME=~/workspace-personal/intellij-community/
fi

${IDEA_HOME}/tools/lexer/jflex-1.4/bin/jflex \
    --table \
    --skel ${IDEA_HOME}/tools/lexer/idea-flex.skeleton \
    --charat --nobak \
    -d src/ro/redeul/google/go/lang/lexer \
    src/ro/redeul/google/go/lang/lexer/go.flex

