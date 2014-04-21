package com.goide.stubs.types;

import com.goide.psi.GoMethodDeclaration;
import com.goide.psi.GoTypeReferenceExpression;
import com.goide.psi.impl.GoMethodDeclarationImpl;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.stubs.GoFileStub;
import com.goide.stubs.GoMethodDeclarationStub;
import com.goide.stubs.index.GoMethodIndex;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoMethodDeclarationStubElementType extends GoNamedStubElementType<GoMethodDeclarationStub, GoMethodDeclaration> {
  public static final GoMethodDeclaration[] EMPTY_ARRAY = new GoMethodDeclaration[0];

  public static final ArrayFactory<GoMethodDeclaration> ARRAY_FACTORY = new ArrayFactory<GoMethodDeclaration>() {
    @NotNull
    @Override
    public GoMethodDeclaration[] create(int count) {
      return count == 0 ? EMPTY_ARRAY : new GoMethodDeclaration[count];
    }
  };
  
  public GoMethodDeclarationStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoMethodDeclaration createPsi(@NotNull GoMethodDeclarationStub stub) {
    return new GoMethodDeclarationImpl(stub, this);
  }

  @Override
  public GoMethodDeclarationStub createStub(@NotNull GoMethodDeclaration psi, StubElement parentStub) {
    GoTypeReferenceExpression reference = GoPsiImplUtil.getTypeReference(psi.getReceiver().getType());
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
