package com.goide.psi;

import org.jetbrains.annotations.Nullable;

public interface GoReceiverHolder extends GoNamedElement {
  @Nullable
  GoSignature getSignature();
}
