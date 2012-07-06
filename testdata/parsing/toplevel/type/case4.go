package main

type T struct {
    /*begin*/a/*end.Duplicate field a*/, b int
    a bool
}

/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n\n')
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
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        PsiComment(ML_COMMENT)('/*begin*/')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
          PsiComment(ML_COMMENT)('/*end.Duplicate field a*/')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('b')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('bool')
        PsiWhiteSpace('\n')
        PsiElement(})('}')
  PsiWhiteSpace('\n')
