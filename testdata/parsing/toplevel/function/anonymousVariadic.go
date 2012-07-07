package main
func foo(...interface{}) {}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  FunctionDeclaration(foo)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('foo')
    PsiElement(()('(')
    FunctionParameterListImpl
      FunctionParameterVariadicImpl
        PsiElement(...)('...')
        TypeInterfaceImpl
          PsiElement(KEYWORD_INTERFACE)('interface')
          PsiElement({)('{')
          PsiElement(})('}')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiElement(})('}')