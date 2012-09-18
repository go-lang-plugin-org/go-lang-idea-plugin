package main

func main() {
    resp := []byte("dafadf")
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
      ShortVarStmtImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('resp')
        PsiWhiteSpace(' ')
        PsiElement(:=)(':=')
        PsiWhiteSpace(' ')
        CallOrConversionExpressionImpl
          TypeSliceImpl
            PsiElement([)('[')
            PsiElement(])(']')
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('byte')
          PsiElement(()('(')
          LiteralExpressionImpl
            LiteralStringImpl
              PsiElement(LITERAL_STRING)('"dafadf"')
          PsiElement())(')')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
