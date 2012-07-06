package main
type (
	Point int
	Polar Point
)
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
    PsiElement(()('(')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('\t')
    TypeSpecImpl
      TypeNameDeclaration(Point)
        PsiElement(IDENTIFIER)('Point')
      PsiWhiteSpace(' ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('int')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('\t')
    TypeSpecImpl
      TypeNameDeclaration(Polar)
        PsiElement(IDENTIFIER)('Polar')
      PsiWhiteSpace(' ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Point')
    PsiWhiteSpace('\n')
    PsiElement())(')')
