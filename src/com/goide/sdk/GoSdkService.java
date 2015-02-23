package com.goide.sdk;

import com.intellij.openapi.components.ServiceManager;

public abstract class GoSdkService {
  public GoSdkService getInstance() {
    return ServiceManager.getService(GoSdkService.class);
  }
}
