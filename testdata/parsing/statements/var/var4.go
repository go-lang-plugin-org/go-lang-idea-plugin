package main
func main() {
    var a int var x int
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
    PsiElement(IDENTIFIER)('main')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      VarDeclarationsImpl
        PsiElement(KEYWORD_VAR)('var')
        PsiWhiteSpace(' ')
        VarDeclarationImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
      PsiWhiteSpace(' ')
      PsiErrorElement:';' or newline expected
        PsiElement(KEYWORD_VAR)('var')
      PsiWhiteSpace(' ')
      ExpressionStmtImpl
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('x')
      PsiWhiteSpace(' ')
      PsiErrorElement:';' or newline expected
        PsiElement(IDENTIFIER)('int')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
