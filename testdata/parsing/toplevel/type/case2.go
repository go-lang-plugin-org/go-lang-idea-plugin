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
  PsiElement(WS_NEW_LINES)('\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    PsiElement(WS_NEW_LINES)('\n')
    PsiWhiteSpace('\t')
    TypeSpecImpl
      TypeNameDeclaration(Point)
        PsiElement(IDENTIFIER)('Point')
      PsiWhiteSpace(' ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('int')
    PsiElement(WS_NEW_LINES)('\n')
    PsiWhiteSpace('\t')
    TypeSpecImpl
      TypeNameDeclaration(Polar)
        PsiElement(IDENTIFIER)('Polar')
      PsiWhiteSpace(' ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Point')
    PsiElement(WS_NEW_LINES)('\n')
    PsiElement())(')')
