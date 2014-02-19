package com.goide.psi;

import org.jetbrains.annotations.Nullable;

public interface GoTypeOwner extends GoCompositeElement {
  @Nullable
  GoType getGoType();
}
