/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: NoOpBlock.java, Class: NoOpBlock
 * Last modified: 2010-04-20
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 */

package com.ansorgit.plugins.bash.editor.formatting.noOpModel;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * A no-op block used when the formatter feature is turned off.
 * User: jansorg
 * Date: Mar 30, 2010
 * Time: 9:29:43 PM
 */
class NoOpBlock implements Block {
    private final ASTNode astNode;

    public NoOpBlock(ASTNode astNode) {
        this.astNode = astNode;
    }

    @NotNull
    public TextRange getTextRange() {
        return astNode.getTextRange();
    }

    @NotNull
    public List<Block> getSubBlocks() {
        return Collections.emptyList();
    }

    public Wrap getWrap() {
        return null;
    }

    public Indent getIndent() {
        return null;
    }

    public Alignment getAlignment() {
        return null;
    }

    public Spacing getSpacing(Block child1, @NotNull Block child2) {
        return null;
    }

    @NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(Indent.getNoneIndent(), Alignment.createAlignment());
        //return ChildAttributes.DELEGATE_TO_NEXT_CHILD;
    }

    public boolean isIncomplete() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }
}