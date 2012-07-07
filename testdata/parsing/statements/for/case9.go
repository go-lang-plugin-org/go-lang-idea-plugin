package main
func f() { for ; ; i++ { } }
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
      PsiWhiteSpace(' ')
      ForWithClausesStmtImpl
        PsiElement(KEYWORD_FOR)('for')
        PsiWhiteSpace(' ')
        EmptyStmt
          <empty list>
        PsiElement(;)(';')
        PsiWhiteSpace(' ')
        PsiElement(;)(';')
        PsiWhiteSpace(' ')
        IncDecStmt
          LiteralExpressionImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('i')
          PsiElement(++)('++')
        PsiWhiteSpace(' ')
        BlockStmtImpl
          PsiElement({)('{')
          PsiWhiteSpace(' ')
          PsiElement(})('}')
      PsiWhiteSpace(' ')
      PsiElement(})('}')
