package main

func main() {
    var a
    a := 5
}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n\n')
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
      VarDeclarationsImpl
        PsiElement(KEYWORD_VAR)('var')
        PsiWhiteSpace(' ')
        VarDeclarationImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      ShortVarStmtImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('a')
        PsiWhiteSpace(' ')
        PsiElement(:=)(':=')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIntegerImpl
            PsiElement(LITERAL_INT)('5')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
