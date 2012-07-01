package main
var e = LoggedUserInfo{
                Email:    email,
                LastTime: datastore.SecondsToTime(time.Seconds()),
        }

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiElement(WS_NEW_LINES)('\n')
  VarDeclarationsImpl
    PsiElement(KEYWORD_VAR)('var')
    PsiWhiteSpace(' ')
    VarDeclarationImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('e')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralCompositeImpl
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('LoggedUserInfo')
          LiteralCompositeValueImpl
            PsiElement({)('{')
            PsiElement(WS_NEW_LINES)('\n')
            PsiWhiteSpace('                ')
            LiteralCompositeElementImpl
              CompositeLiteralElementKey
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('Email')
              PsiElement(:)(':')
              PsiWhiteSpace('    ')
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('email')
            PsiElement(,)(',')
            PsiElement(WS_NEW_LINES)('\n')
            PsiWhiteSpace('                ')
            LiteralCompositeElementImpl
              CompositeLiteralElementKey
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('LastTime')
              PsiElement(:)(':')
              PsiWhiteSpace(' ')
              CallOrConversionExpressionImpl
                SelectorExpression
                  LiteralExpressionImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('datastore')
                  PsiElement(.)('.')
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('SecondsToTime')
                PsiElement(()('(')
                CallOrConversionExpressionImpl
                  SelectorExpression
                    LiteralExpressionImpl
                      LiteralIdentifierImpl
                        PsiElement(IDENTIFIER)('time')
                    PsiElement(.)('.')
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('Seconds')
                  PsiElement(()('(')
                  PsiElement())(')')
                PsiElement())(')')
            PsiElement(,)(',')
            PsiElement(WS_NEW_LINES)('\n')
            PsiWhiteSpace('        ')
            PsiElement(})('}')
  PsiElement(WS_NEW_LINES)('\n')