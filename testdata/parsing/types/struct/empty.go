package main
type T struct {}
/**------
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(T)
        PsiElement(IDENTIFIER)('T')
      PsiWhiteSpace(' ')
      TypeStructImpl
        PsiElement(KEYWORD_STRUCT)('struct')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiElement(})('}')

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(T)
        PsiElement(IDENTIFIER)('T')
      PsiWhiteSpace(' ')
      TypeStructImpl
        PsiElement(KEYWORD_STRUCT)('struct')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiElement(})('}')
  PsiWhiteSpace('\n')
  PsiComment(ML_COMMENT)('/**------\nGo file\n  PackageDeclaration(main)\n    PsiElement(KEYWORD_PACKAGE)('package')\n    PsiWhiteSpace(' ')\n    PsiElement(IDENTIFIER)('main')\n  PsiWhiteSpace('\n')\n  TypeDeclarationsImpl\n    PsiElement(KEYWORD_TYPE)('type')\n    PsiWhiteSpace(' ')\n    TypeSpecImpl\n      TypeNameDeclaration(T)\n        PsiElement(IDENTIFIER)('T')\n      PsiWhiteSpace(' ')\n      TypeStructImpl\n        PsiElement(KEYWORD_STRUCT)('struct')\n        PsiWhiteSpace(' ')\n        PsiElement({)('{')\n        PsiElement(})('}')\n')