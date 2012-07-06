package main
func (p Point) Scale(factor float64) {}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  MethodDeclaration(Scale)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    MethodReceiverImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('p')
      PsiWhiteSpace(' ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Point')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('Scale')
    PsiElement(()('(')
    FunctionParameterListImpl
      FunctionParameterImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('factor')
        PsiWhiteSpace(' ')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('float64')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiElement(})('}')
