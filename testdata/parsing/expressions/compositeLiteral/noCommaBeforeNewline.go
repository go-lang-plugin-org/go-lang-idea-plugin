package main
var e = Type{1:10
}
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
              PsiElement(IDENTIFIER)('Type')
          LiteralCompositeValueImpl
            PsiElement({)('{')
            LiteralCompositeElementImpl
              CompositeLiteralElementKey
                LiteralExpressionImpl
                  LiteralIntegerImpl
                    PsiElement(LITERAL_INT)('1')
              PsiElement(:)(':')
              LiteralExpressionImpl
                LiteralIntegerImpl
                  PsiElement(LITERAL_INT)('10')
            PsiErrorElement:',' expected before newline
              <empty list>
            PsiWhiteSpace('\n')
            PsiElement(})('}')
