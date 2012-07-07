package main
func f() {
    for key, val = range m { }
    // asdfa
}
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
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      ForWithRangeStmtImpl
        PsiElement(KEYWORD_FOR)('for')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('key')
        PsiElement(,)(',')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('val')
        PsiWhiteSpace(' ')
        PsiElement(=)('=')
        PsiWhiteSpace(' ')
        PsiElement(KEYWORD_RANGE)('range')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('m')
        PsiWhiteSpace(' ')
        BlockStmtImpl
          PsiElement({)('{')
          PsiWhiteSpace(' ')
          PsiElement(})('}')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      PsiComment(SL_COMMENT)('// asdfa')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
