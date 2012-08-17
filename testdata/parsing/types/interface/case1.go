package main
type ITest interface {
    Writer
    Sum(values []int) []byte
}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
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
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('Writer')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        FunctionDeclaration(Sum)
          LiteralIdentifierImpl
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
        PsiWhiteSpace('\n')
        PsiElement(})('}')
