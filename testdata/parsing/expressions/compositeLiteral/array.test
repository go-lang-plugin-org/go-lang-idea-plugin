package main
var e = [2]string{"str1", "str2"}
-----
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
          TypeArrayImpl
            PsiElement([)('[')
            LiteralExpressionImpl
              LiteralIntegerImpl
                PsiElement(LITERAL_INT)('2')
            PsiElement(])(']')
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('string')
          LiteralCompositeValueImpl
            PsiElement({)('{')
            LiteralCompositeElementImpl
              LiteralExpressionImpl
                LiteralStringImpl
                  PsiElement(LITERAL_STRING)('"str1"')
            PsiElement(,)(',')
            PsiWhiteSpace(' ')
            LiteralCompositeElementImpl
              LiteralExpressionImpl
                LiteralStringImpl
                  PsiElement(LITERAL_STRING)('"str2"')
            PsiElement(})('}')