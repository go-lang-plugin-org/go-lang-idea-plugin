package main

type T interface {
    p1.X

    Method() int
}


/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(T)
        PsiElement(IDENTIFIER)('T')
      PsiWhiteSpace(' ')
      TypeInterfaceImpl
        PsiElement(KEYWORD_INTERFACE)('interface')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('p1')
          PsiElement(.)('.')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('X')
        PsiWhiteSpace('\n\n')
        PsiWhiteSpace('    ')
        FunctionDeclaration(Method)
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('Method')
          PsiElement(()('(')
          PsiElement())(')')
          PsiWhiteSpace(' ')
          FunctionResult
            FunctionParameterListImpl
              FunctionParameterImpl
                TypeNameImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('int')
        PsiWhiteSpace('\n')
        PsiElement(})('}')
  PsiWhiteSpace('\n\n')