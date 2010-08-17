#!/bin/sh

/home/mtoader/Work/IntellijIdea/ideaIC-95.429/tools/lexer/jflex-1.4/bin/jflex \
    --table \
    --skel /home/mtoader/Work/IntellijIdea/ideaIC-95.429/tools/lexer/idea-flex.skeleton \
    --charat --nobak \
    -d src/ro/redeul/google/go/lang/lexer \
    src/ro/redeul/google/go/lang/lexer/go.flex


    