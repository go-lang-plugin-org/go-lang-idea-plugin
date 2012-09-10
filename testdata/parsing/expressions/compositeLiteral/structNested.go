package main
var e = struct{a int; b int}{a, Point{y: -1, x: 10}}
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
          TypeStructImpl
            PsiElement(KEYWORD_STRUCT)('struct')
            PsiElement({)('{')
            TypeStructFieldImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('a')
              PsiWhiteSpace(' ')
              TypeNameImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('int')
            PsiElement(;)(';')
            PsiWhiteSpace(' ')
            TypeStructFieldImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('b')
              PsiWhiteSpace(' ')
              TypeNameImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('int')
            PsiElement(})('}')
          LiteralCompositeValueImpl
            PsiElement({)('{')
            LiteralCompositeElementImpl
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('a')
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
                            PsiElement(LITERAL_INT)('1')
                    PsiElement(,)(',')
                    PsiWhiteSpace(' ')
                    LiteralCompositeElementImpl
                      CompositeLiteralElementKey
                        LiteralExpressionImpl
                          LiteralIdentifierImpl
                            PsiElement(IDENTIFIER)('x')
                      PsiElement(:)(':')
                      PsiWhiteSpace(' ')
                      LiteralExpressionImpl
                        LiteralIntegerImpl
                          PsiElement(LITERAL_INT)('10')
                    PsiElement(})('}')
            PsiElement(})('}')
