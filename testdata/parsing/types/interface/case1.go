package main
type ITest interface {
    io.Writer
    Sum(values []int) []byte
}

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiElement(WS_NEW_LINES)('\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(ITest)
        PsiElement(IDENTIFIER)('ITest')
      PsiWhiteSpace(' ')
      TypeInterfaceImpl
        PsiElement(KEYWORD_INTERFACE)('interface')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiElement(WS_NEW_LINES)('\n')
        PsiWhiteSpace('    ')
        MethodDeclaration()
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('io')
        MethodDeclaration()
          TypeNameImpl
            PsiErrorElement:identifier.expected
              PsiElement(.)('.')
        MethodDeclaration()
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('Writer')
        PsiElement(WS_NEW_LINES)('\n')
        PsiWhiteSpace('    ')
        MethodDeclaration(Sum)
          PsiElement(IDENTIFIER)('Sum')
          PsiElement(()('(')
          FunctionParameterListImpl
            FunctionParameterImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('values')
              PsiWhiteSpace(' ')
              TypeSliceImpl
                PsiElement([)('[')
                PsiElement(])(']')
                TypeNameImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('int')
          PsiElement())(')')
          PsiWhiteSpace(' ')
          FunctionResult
            FunctionParameterListImpl
              FunctionParameterImpl
                TypeSliceImpl
                  PsiElement([)('[')
                  PsiElement(])(']')
                  TypeNameImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('byte')
        PsiElement(WS_NEW_LINES)('\n')
        PsiElement(})('}')
  PsiElement(WS_NEW_LINES)('\n')