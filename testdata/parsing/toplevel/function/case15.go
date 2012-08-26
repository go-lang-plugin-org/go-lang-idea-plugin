package main
func f(a, b int, z float) (bool) {}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  FunctionDeclaration(f)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('f')
    PsiElement(()('(')
    FunctionParameterListImpl
      FunctionParameterImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('a')
        PsiElement(,)(',')
        PsiWhiteSpace(' ')
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('b')
        PsiWhiteSpace(' ')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('int')
      PsiElement(,)(',')
      PsiWhiteSpace(' ')
      FunctionParameterImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('z')
        PsiWhiteSpace(' ')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('float')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    FunctionResult
      PsiElement(()('(')
      FunctionParameterListImpl
        FunctionParameterImpl
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('bool')
      PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiElement(})('}')
