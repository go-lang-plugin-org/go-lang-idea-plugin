/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.dlv;

import com.goide.dlv.rdp.*;
import com.google.gson.stream.JsonToken;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.debugger.ObjectPropertyImpl;
import org.jetbrains.debugger.ValueModifier;
import org.jetbrains.debugger.Variable;
import org.jetbrains.debugger.VariableImpl;
import org.jetbrains.debugger.values.*;

import java.util.*;

public final class DlvValueManager extends ValueManager<DlvVm> {
  private final Map<String, Value> refToValue = new THashMap<String, Value>();

  private final List<String> actorsNotYetPromotedToThreadLifetime = new ArrayList<String>();

  public DlvValueManager(@NotNull DlvVm vm) {
    super(vm);
  }

  @NotNull
  public List<Variable> createProperties(@Nullable Map<String, PropertyDescriptor> propertyDescriptors,
                                         @Nullable Grip prototypeGrip,
                                         @Nullable Map<String, SafeGetterValue> safeGetterValues) {
    Variable prototype = prototypeGrip == null ? null : createProtoVariable(prototypeGrip);
    if (propertyDescriptors == null || propertyDescriptors.isEmpty()) {
      return prototype == null ? Collections.<Variable>emptyList() : Collections.singletonList(prototype);
    }
    else {
      Variable[] variables = new Variable[propertyDescriptors.size() + (prototype == null ? 0 : 1)];
      int i = 0;
      for (Map.Entry<String, PropertyDescriptor> entry : propertyDescriptors.entrySet()) {
        String name = entry.getKey();
        variables[i++] = createProperty(name, entry.getValue(), safeGetterValues == null ? null : safeGetterValues.get(name));
      }

      if (prototype != null) {
        variables[variables.length - 1] = prototype;
      }
      return Arrays.asList(variables);
    }
  }

  @NotNull
  public Variable createProtoVariable(@Nullable Grip prototypeGrip) {
    return createVariable("__proto__", prototypeGrip, null);
  }

  @NotNull
  public Variable createProperty(@NotNull String name,
                                 @NotNull PropertyDescriptor descriptor,
                                 @Nullable SafeGetterValue getterValueDescriptor) {
    int flags = 0;
    if (descriptor.writable()) {
      flags |= ObjectPropertyImpl.WRITABLE;
    }
    if (descriptor.configurable()) {
      flags |= ObjectPropertyImpl.CONFIGURABLE;
    }
    if (descriptor.enumerable()) {
      flags |= ObjectPropertyImpl.ENUMERABLE;
    }
    // https://youtrack.jetbrains.com/issue/WEB-14889
    // if safe getter value provided, don't add getter/setter
    return new ObjectPropertyImpl(name,
                                  createValue(getterValueDescriptor == null ? descriptor : getterValueDescriptor),
                                  getterValueDescriptor == null ? createValueAccessor(descriptor.get()) : null,
                                  getterValueDescriptor == null ? createValueAccessor(descriptor.set()) : null,
                                  null,
                                  flags);
  }

  @Nullable
  private FunctionValue createValueAccessor(@Nullable Grip grip) {
    if (grip == null || grip.type() == Grip.Type.UNDEFINED) {
      return null;
    }
    else {
      Value value = createValue(grip, null, null);
      return value instanceof FunctionValue ? (FunctionValue)value : null;
    }
  }

  @NotNull
  public Variable createVariable(@NotNull String name, @Nullable Grip valueData, @Nullable ValueModifier valueModifier) {
    return new VariableImpl(name, createValue(valueData, null, null), valueModifier);
  }

  @Nullable
  public Value createValue(@NotNull ValueHolder valueHolder) {
    return createValue(valueHolder.value(), valueHolder.primitiveValue(), valueHolder.primitiveValueType());
  }

  @Nullable
  public Value createValue(@Nullable Grip valueData, @Nullable String primitiveValue, @Nullable JsonToken primitiveValueType) {
    if (valueData == null && primitiveValue == null) {
      // property, getter or setter should be provided
      return null;
    }

    String actor = valueData == null ? null : valueData.actor();
    if (actor == null) {
      return doCreateValue(valueData, primitiveValue, primitiveValueType);
    }

    synchronized (refToValue) {
      Value value = refToValue.get(actor);
      if (value == null) {
        value = doCreateValue(valueData, primitiveValue, primitiveValueType);
        refToValue.put(actor, value);
        actorsNotYetPromotedToThreadLifetime.add(actor);
      }
      return value;
    }
  }

  @NotNull
  public Promise<Void> promoteRecentlyAddedActorsToThreadLifetime() {
    synchronized (refToValue) {
      if (actorsNotYetPromotedToThreadLifetime.isEmpty()) {
        return Promise.DONE;
      }

      Promise<Void> promise = vm.getCommandProcessor().send(
        DlvRequest.pauseLifetimeGripsToThreadLifetime(vm.threadActor, actorsNotYetPromotedToThreadLifetime));
      actorsNotYetPromotedToThreadLifetime.clear();
      return promise;
    }
  }

  @NotNull
  private Value doCreateValue(@Nullable Grip valueData, @Nullable String primitiveValue, @Nullable JsonToken primitiveValueType) {
    if (primitiveValue != null) {
      ValueType valueType;
      assert primitiveValueType != null;
      switch (primitiveValueType) {
        case STRING:
          valueType = ValueType.STRING;
          break;

        case NUMBER:
          valueType = ValueType.NUMBER;
          break;

        case BOOLEAN:
          return PrimitiveValue.bool(primitiveValue);

        case NULL:
          return PrimitiveValue.NULL;

        default:
          throw new UnsupportedOperationException("Unsupported type " + primitiveValueType);
      }
      return new PrimitiveValue(valueType, primitiveValue);
    }

    assert valueData != null;
    switch (valueData.type()) {
      case OBJECT: {
        ValueType valueType = null;
        Grip.Preview preview = valueData.preview();
        if (preview != null) {
          if (preview.kind() == Grip.Preview.Kind.DOM_NODE) {
            valueType = ValueType.NODE;
          }
          else if (preview.kind() == Grip.Preview.Kind.ARRAY_LIKE) {
            if (preview.length() == -1) {
              valueType = ValueType.ARRAY;
            }
            else {
              return new DlvArray(preview.length(), valueData, this);
            }
          }
        }
        if (valueType == null) {
          if ("Array".equals(valueData.className())) {
            valueType = ValueType.ARRAY;
          }
          else if ("Function".equals(valueData.className())) {
            return new DlvFunction(valueData, this);
          }
          else {
            valueType = ValueType.OBJECT;
          }
        }
        return new DlvObject(valueType, valueData, this);
      }

      case NA_N:
        return PrimitiveValue.NAN;

      case INFINITY:
        return PrimitiveValue.INFINITY;

      case UNDEFINED:
        return PrimitiveValue.UNDEFINED;

      case NULL:
        return PrimitiveValue.NULL;

      default:
        throw new UnsupportedOperationException();
    }
  }

  @NotNull
  public Promise<Void> release() {
    markObsolete();
    synchronized (refToValue) {
      actorsNotYetPromotedToThreadLifetime.clear();
      if (refToValue.isEmpty()) {
        return Promise.DONE;
      }
      else {
        Promise<Void> promise = vm.getCommandProcessor().send(DlvRequest.release(vm.threadActor, refToValue.keySet()));
        refToValue.clear();
        return promise;
      }
    }
  }

  @Override
  public void clearCaches() {
    super.clearCaches();

    synchronized (refToValue) {
      refToValue.clear();
    }
  }
}