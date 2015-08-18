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

package com.goide.debugger.gdb.messages;

import org.jetbrains.annotations.NotNull;

/**
 * Class which holds a reference to all the available GDB event type wrappers.
 */
public class GdbMiEventTypes {
  /**
   * An array of the event classes.
   */
  @NotNull public static Class<?>[] classes = {
    GdbDoneEvent.class,
    GdbConnectedEvent.class,
    GdbErrorEvent.class,
    GdbExitEvent.class,
    GdbRunningEvent.class,
    GdbStoppedEvent.class};

  /**
   * An array of types of 'done' events.
   */
  @NotNull public static Class<?>[] doneEventTypes = {
    GdbBreakpoint.class,
    GdbFeatures.class,
    GdbStackTrace.class,
    GdbThreadInfo.class,
    GdbVariableObject.class,
    GdbVariableObjectChanges.class,
    GdbVariableObjects.class,
    GdbVariables.class};
}
