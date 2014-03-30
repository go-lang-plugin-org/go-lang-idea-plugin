package com.goide.psi;

import org.jetbrains.annotations.Nullable;

public interface GoSignatureOwner extends GoNamedElement {
  @Nullable
  GoSignature getSignature();
}
