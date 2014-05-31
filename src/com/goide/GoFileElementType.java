package com.goide;

import com.goide.psi.GoFile;
import com.goide.stubs.GoFileStub;
import com.goide.stubs.index.GoPackagesIndex;
import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.generate.tostring.util.StringUtil;

import java.io.IOException;

public class GoFileElementType extends IStubFileElementType<GoFileStub> {
  public static final IStubFileElementType INSTANCE = new GoFileElementType();
  public static final int VERSION = 1;

  public GoFileElementType() {
    super("GO_FILE", GoLanguage.INSTANCE);
  }

  @Override
  public int getStubVersion() {
    return VERSION;
  }

  @NotNull
  @Override
  public StubBuilder getBuilder() {
    return new DefaultStubBuilder() {
      @Override
      protected StubElement createStubForFile(@NotNull PsiFile file) {
        if (file instanceof GoFile) {
          return new GoFileStub((GoFile)file);
        }
        return super.createStubForFile(file);
      }
    };
  }

  @Override
  public void indexStub(@NotNull GoFileStub stub, @NotNull IndexSink sink) {
    super.indexStub(stub, sink);
    String packageName = stub.getPackageName();
    if (StringUtil.isNotEmpty(packageName)) {
      sink.occurrence(GoPackagesIndex.KEY, packageName);
    }
  }

  @Override
  public void serialize(@NotNull GoFileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getPackageName());
  }

  @NotNull
  @Override
  public GoFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoFileStub(null, dataStream.readName());
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "go.FILE";
  }
}
