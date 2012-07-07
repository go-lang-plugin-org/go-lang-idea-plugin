package main
func foo()(a int, int) {}
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
    PsiElement())(')')
    FunctionResult
      PsiElement(()('(')
      FunctionParameterListImpl
        FunctionParameterImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
        PsiElement(,)(',')
        PsiWhiteSpace(' ')
        FunctionParameterImpl
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
      PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiElement(})('}')