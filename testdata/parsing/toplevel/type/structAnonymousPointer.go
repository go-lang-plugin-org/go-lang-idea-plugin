package main
type TreeNode struct {
	*TreeNode
}
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
      TypeNameDeclaration(TreeNode)
        PsiElement(IDENTIFIER)('TreeNode')
      PsiWhiteSpace(' ')
      TypeStructImpl
        PsiElement(KEYWORD_STRUCT)('struct')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('\t')
        TypeStructFieldAnonymousImpl
          TypePointerImpl
            PsiElement(*)('*')
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('TreeNode')
        PsiWhiteSpace('\n')
        PsiElement(})('}')
