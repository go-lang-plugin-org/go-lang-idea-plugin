package com.goide.dlv.protocol;

import java.util.List;

public class DlvStacktraceRequest extends DlvRequest<List<Api.Location>> {
  public DlvStacktraceRequest() {
    writeInt("Id", -1);
    writeInt("Depth", 100);
  }

  @Override
  public String getMethodName() {
    return "RPCServer." + "StacktraceGoroutine";
  }
}
