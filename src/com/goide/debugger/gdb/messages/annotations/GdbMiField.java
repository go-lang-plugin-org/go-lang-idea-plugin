/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation applied to fields in classes which represent fields in GDB/MI messages.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface GdbMiField {
  /**
   * The name of the field.
   */
  @NotNull String name();

  /**
   * The supported GDB/MI value types.
   */
  @NotNull GdbMiValue.Type[] valueType();

  /**
   * Name of the function to use to convert the value from the GDB format to the variable format.
   * May be the name of a function on the parent class, or a fully-qualified name to a static
   * function.
   */
  @NotNull String valueProcessor() default "";
}
