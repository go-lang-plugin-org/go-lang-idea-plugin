/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Created by IntelliJ IDEA.
 * User: igork
 * Date: Nov 25, 2002
 * Time: 2:05:49 PM
 * To change this template use Options | File Templates.
 */
package ro.redeul.google.go.lang.psi.utils;

import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;

public class GoPsiScopesUtil {

    private GoPsiScopesUtil() {
    }

    public static boolean treeWalkUp(@NotNull PsiScopeProcessor processor,
                                     @NotNull PsiElement entrance,
                                     @Nullable PsiElement maxScope) {
        return treeWalkUp(processor, entrance, maxScope, ResolveState.initial());
    }

    public static boolean treeWalkUp(@NotNull final PsiScopeProcessor processor,
                                     @NotNull final PsiElement entrance,
                                     @Nullable final PsiElement maxScope,
                                     @NotNull final ResolveState state) {

        PsiElement prevParent = entrance;
        PsiElement scope = entrance;

        while (scope != null) {
            ProgressIndicatorProvider.checkCanceled();

            if (!scope.processDeclarations(processor, state, prevParent, entrance)) {
                return false; // resolved
            }


            if (scope == maxScope) break;
            prevParent = scope;
            scope = prevParent.getContext();
            if (scope != null && scope != prevParent.getParent() && !scope.isValid()) {
                break;
            }

        }

        return true;
    }

    public static boolean walkPackageExports(@NotNull PsiScopeProcessor processor,
                                             PsiElement entrance,
                                             @NotNull GoPackage goPackage) {
        return walkPackage(processor, ResolveStates.packageExports(), entrance, goPackage);
    }

    public static boolean walkPackage(PsiScopeProcessor processor, ResolveState state, PsiElement entrance, GoPackage goPackage) {
        return goPackage.processDeclarations(processor, state, null, entrance);
    }

    public static boolean walkChildrenScopes(@NotNull PsiElement thisElement,
                                             @NotNull PsiScopeProcessor processor,
                                             @NotNull ResolveState state,
                                             PsiElement lastParent,
                                             PsiElement place) {
        PsiElement child = null;
        if (lastParent != null && lastParent.getParent() == thisElement) {
            child = lastParent.getPrevSibling();
            if (child == null) return true; // first element
        }

        if (child == null) {
            child = thisElement.getLastChild();
        }

        while (child != null) {
            if (!child.processDeclarations(processor, state, lastParent, place)) return false;
            child = child.getPrevSibling();
        }

        return true;
    }

}
