package main
func main() {
    if e { } else
    {
    }
}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  FunctionDeclaration(main)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('main')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      IfStmtImpl
        PsiElement(KEYWORD_IF)('if')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('e')
        PsiWhiteSpace(' ')
        BlockStmtImpl
          PsiElement({)('{')
          PsiWhiteSpace(' ')
          PsiElement(})('}')
        PsiWhiteSpace(' ')
        PsiElement(KEYWORD_ELSE)('else')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        BlockStmtImpl
          PsiElement({)('{')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('    ')
          PsiElement(})('}')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
