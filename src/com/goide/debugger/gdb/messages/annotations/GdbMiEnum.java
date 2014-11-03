/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.debugger.gdb.messages.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation applied to enums which represent strings in GDB/MI messages.
 * GDB/MI strings are converted to enum values by stripping hyphens and capitalising the first
 * letter of each word. For instance, "breakpoint-hit" would become "BreakpointHit" in the enum.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface GdbMiEnum {
}
