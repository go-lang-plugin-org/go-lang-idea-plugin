package main
func main() {
    continue a;
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
      ContinueStmtImpl
        PsiElement(KEYWORD_CONTINUE)('continue')
        PsiWhiteSpace(' ')
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('a')
      PsiElement(;)(';')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
