package main
var e = struct{a int; b int}{var}
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
              PsiErrorElement:expression.expected
                PsiElement(KEYWORD_VAR)('var')
            PsiElement(})('}')
