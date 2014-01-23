package com.goide.stubs;

import com.goide.GoFileElementType;
import com.goide.psi.GoFile;
import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;

public class GoFileStub extends PsiFileStubImpl<GoFile> {
  private final StringRef myPackageName;

  public GoFileStub(GoFile file) {
    super(file);
    myPackageName = StringRef.fromNullableString(file.getPackageName());
  }

  public GoFileStub(GoFile file, StringRef packageName) {
    super(file);
    myPackageName = packageName;
  }

  public String getPackageName() {
    return myPackageName.getString();
  }

  @Override
  public IStubFileElementType getType() {
    return GoFileElementType.INSTANCE;
  }
}
