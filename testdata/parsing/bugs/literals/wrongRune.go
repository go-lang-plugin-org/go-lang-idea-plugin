package main

const c = '\'
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n\n')
  ConstDeclarationsImpl
    PsiElement(KEYWORD_CONST)('const')
    PsiWhiteSpace(' ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('c')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
  PsiWhiteSpace(' ')
  PsiErrorElement:';' or newline expected
    PsiElement(WRONG)(''\'')