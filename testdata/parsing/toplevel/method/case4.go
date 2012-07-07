package main
func (p Point) Length() float64 {}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  MethodDeclaration(Length)
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
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('Length')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    FunctionResult
      FunctionParameterListImpl
        FunctionParameterImpl
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('float64')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiElement(})('}')
