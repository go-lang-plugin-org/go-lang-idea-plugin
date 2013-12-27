package main
type T struct {
    T2
    x int
}
type T3 struct { T }
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
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        TypeStructFieldAnonymousImpl
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('T2')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('x')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
        PsiWhiteSpace('\n')
        PsiElement(})('}')
  PsiWhiteSpace('\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(T3)
        PsiElement(IDENTIFIER)('T3')
      PsiWhiteSpace(' ')
      TypeStructImpl
        PsiElement(KEYWORD_STRUCT)('struct')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace(' ')
        TypeStructFieldAnonymousImpl
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('T')
        PsiWhiteSpace(' ')
        PsiElement(})('}')
