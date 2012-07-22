package main
func (q *actionQueue) Swap(i, j int)      { (*q)[i], (*q)[j] = (*q)[j], (*q)[i] }
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  MethodDeclaration(Swap)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    MethodReceiverImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('q')
      PsiWhiteSpace(' ')
      TypePointerImpl
        PsiElement(*)('*')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('actionQueue')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('Swap')
    PsiElement(()('(')
    FunctionParameterListImpl
      FunctionParameterImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('i')
        PsiElement(,)(',')
        PsiWhiteSpace(' ')
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('j')
        PsiWhiteSpace(' ')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('int')
    PsiElement())(')')
    PsiWhiteSpace('      ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace(' ')
      AssignStmtImpl
        ExpressionListImpl
          IndexExpressionImpl
            ParenthesisedExpressionImpl
              PsiElement(()('(')
              UnaryExpressionImpl
                PsiElement(*)('*')
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('q')
              PsiElement())(')')
            PsiElement([)('[')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('i')
            PsiElement(])(']')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          IndexExpressionImpl
            ParenthesisedExpressionImpl
              PsiElement(()('(')
              UnaryExpressionImpl
                PsiElement(*)('*')
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('q')
              PsiElement())(')')
            PsiElement([)('[')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('j')
            PsiElement(])(']')
        PsiWhiteSpace(' ')
        PsiElement(=)('=')
        PsiWhiteSpace(' ')
        ExpressionListImpl
          IndexExpressionImpl
            ParenthesisedExpressionImpl
              PsiElement(()('(')
              UnaryExpressionImpl
                PsiElement(*)('*')
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('q')
              PsiElement())(')')
            PsiElement([)('[')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('j')
            PsiElement(])(']')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          IndexExpressionImpl
            ParenthesisedExpressionImpl
              PsiElement(()('(')
              UnaryExpressionImpl
                PsiElement(*)('*')
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('q')
              PsiElement())(')')
            PsiElement([)('[')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('i')
            PsiElement(])(']')
      PsiWhiteSpace(' ')
      PsiElement(})('}')
