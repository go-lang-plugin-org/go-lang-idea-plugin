package main
var e = Line{origin, Point{y: -4, z: 12.3}}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
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
              PsiElement(IDENTIFIER)('Line')
          LiteralCompositeValueImpl
            PsiElement({)('{')
            LiteralCompositeElementImpl
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('origin')
            PsiElement(,)(',')
            PsiWhiteSpace(' ')
            LiteralCompositeElementImpl
              LiteralExpressionImpl
                LiteralCompositeImpl
                  TypeNameImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('Point')
                  LiteralCompositeValueImpl
                    PsiElement({)('{')
                    LiteralCompositeElementImpl
                      CompositeLiteralElementKey
                        LiteralExpressionImpl
                          LiteralIdentifierImpl
                            PsiElement(IDENTIFIER)('y')
                      PsiElement(:)(':')
                      PsiWhiteSpace(' ')
                      UnaryExpressionImpl
                        PsiElement(-)('-')
                        LiteralExpressionImpl
                          LiteralIntegerImpl
                            PsiElement(LITERAL_INT)('4')
                    PsiElement(,)(',')
                    PsiWhiteSpace(' ')
                    LiteralCompositeElementImpl
                      CompositeLiteralElementKey
                        LiteralExpressionImpl
                          LiteralIdentifierImpl
                            PsiElement(IDENTIFIER)('z')
                      PsiElement(:)(':')
                      PsiWhiteSpace(' ')
                      LiteralExpressionImpl
                        LiteralFloatImpl
                          PsiElement(LITERAL_FLOAT)('12.3')
                    PsiElement(})('}')
            PsiElement(})('}')
