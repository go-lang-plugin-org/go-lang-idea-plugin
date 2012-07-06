package main
type T struct { x int
y int32 }
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
        PsiWhiteSpace(' ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('x')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
        PsiWhiteSpace('\n')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('y')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int32')
        PsiWhiteSpace(' ')
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
        PsiWhiteSpace(' ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('x')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
        PsiWhiteSpace('\n')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('y')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int32')
        PsiWhiteSpace(' ')
        PsiElement(})('}')
  PsiWhiteSpace('\n')
  PsiComment(ML_COMMENT)('/**------\nGo file\n  PackageDeclaration(main)\n    PsiElement(KEYWORD_PACKAGE)('package')\n    PsiWhiteSpace(' ')\n    PsiElement(IDENTIFIER)('main')\n  PsiWhiteSpace('\n')\n  TypeDeclarationsImpl\n    PsiElement(KEYWORD_TYPE)('type')\n    PsiWhiteSpace(' ')\n    TypeSpecImpl\n      TypeNameDeclaration(T)\n        PsiElement(IDENTIFIER)('T')\n      PsiWhiteSpace(' ')\n      TypeStructImpl\n        PsiElement(KEYWORD_STRUCT)('struct')\n        PsiWhiteSpace(' ')\n        PsiElement({)('{')\n        PsiWhiteSpace(' ')\n        TypeStructFieldImpl\n          LiteralIdentifierImpl\n            PsiElement(IDENTIFIER)('x')\n          PsiWhiteSpace(' ')\n          TypeNameImpl\n            LiteralIdentifierImpl\n              PsiElement(IDENTIFIER)('int')\n        PsiWhiteSpace('\n')\n        TypeStructFieldImpl\n          LiteralIdentifierImpl\n            PsiElement(IDENTIFIER)('y')\n          PsiWhiteSpace(' ')\n          TypeNameImpl\n            LiteralIdentifierImpl\n              PsiElement(IDENTIFIER)('int32')\n        PsiWhiteSpace(' ')\n        PsiElement(})('}')\n')