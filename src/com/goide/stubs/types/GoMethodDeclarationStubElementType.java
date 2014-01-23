package com.goide.stubs.types;

import com.goide.psi.GoMethodDeclaration;
import com.goide.psi.GoTypeReferenceExpression;
import com.goide.psi.impl.GoMethodDeclarationImpl;
import com.goide.stubs.GoFileStub;
import com.goide.stubs.GoMethodDeclarationStub;
import com.goide.stubs.index.GoMethodIndex;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoMethodDeclarationStubElementType extends GoNamedStubElementType<GoMethodDeclarationStub, GoMethodDeclaration> {
  public GoMethodDeclarationStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoMethodDeclaration createPsi(@NotNull GoMethodDeclarationStub stub) {
    return new GoMethodDeclarationImpl(stub, this);
  }

  @Override
  public GoMethodDeclarationStub createStub(@NotNull GoMethodDeclaration psi, StubElement parentStub) {
    GoTypeReferenceExpression reference = psi.getReceiver().getType().getTypeReferenceExpression();
    String text = reference != null ? reference.getIdentifier().getText() : null;
    return new GoMethodDeclarationStub(parentStub, this, psi.getName(), psi.isPublic(), text);
  }

  @Override
  public void serialize(@NotNull GoMethodDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
    dataStream.writeName(stub.getTypeName());
  }

  @NotNull
  @Override
  public GoMethodDeclarationStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoMethodDeclarationStub(parentStub, this, dataStream.readName(), dataStream.readBoolean(), dataStream.readName());
  }

  @Override
  public void indexStub(@NotNull GoMethodDeclarationStub stub, @NotNull IndexSink sink) {
    super.indexStub(stub, sink);
    String typeName = stub.getTypeName();
    if (!StringUtil.isEmpty(typeName)) {
      StubElement parent = stub.getParentStub();
      if (parent instanceof GoFileStub) {
        String packageName = ((GoFileStub)parent).getPackageName();
        if (!StringUtil.isEmpty(typeName)) {
          sink.occurrence(GoMethodIndex.KEY, packageName + "." + typeName);
        }
      }
    }
  }
}
