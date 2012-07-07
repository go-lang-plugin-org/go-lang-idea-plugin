package main
const (
      ET_NONE   Type = 0      /* Unknown type. */
      ET_REL    Type = 1      /* Relocatable. */
      ET_EXEC   Type = 2      /* Executable. */
      ET_DYN    Type = 3      /* Shared object. */
      ET_CORE   Type = 4      /* Core file. */
      ET_LOOS   Type = 0xfe00 /* First operating system specific. */
      ET_HIOS   Type = 0xfeff /* Last operating system-specific. */
      ET_LOPROC Type = 0xff00 /* First processor-specific. */
      ET_HIPROC Type = 0xffff /* Last processor-specific. */
)
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  ConstDeclarationsImpl
    PsiElement(KEYWORD_CONST)('const')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('      ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('ET_NONE')
      PsiWhiteSpace('   ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Type')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('0')
    PsiWhiteSpace('      ')
    PsiComment(ML_COMMENT)('/* Unknown type. */')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('      ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('ET_REL')
      PsiWhiteSpace('    ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Type')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('1')
    PsiWhiteSpace('      ')
    PsiComment(ML_COMMENT)('/* Relocatable. */')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('      ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('ET_EXEC')
      PsiWhiteSpace('   ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Type')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('2')
    PsiWhiteSpace('      ')
    PsiComment(ML_COMMENT)('/* Executable. */')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('      ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('ET_DYN')
      PsiWhiteSpace('    ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Type')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('3')
    PsiWhiteSpace('      ')
    PsiComment(ML_COMMENT)('/* Shared object. */')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('      ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('ET_CORE')
      PsiWhiteSpace('   ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Type')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_INT)('4')
    PsiWhiteSpace('      ')
    PsiComment(ML_COMMENT)('/* Core file. */')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('      ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('ET_LOOS')
      PsiWhiteSpace('   ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Type')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_HEX)('0xfe00')
    PsiWhiteSpace(' ')
    PsiComment(ML_COMMENT)('/* First operating system specific. */')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('      ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('ET_HIOS')
      PsiWhiteSpace('   ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Type')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_HEX)('0xfeff')
    PsiWhiteSpace(' ')
    PsiComment(ML_COMMENT)('/* Last operating system-specific. */')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('      ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('ET_LOPROC')
      PsiWhiteSpace(' ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Type')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_HEX)('0xff00')
    PsiWhiteSpace(' ')
    PsiComment(ML_COMMENT)('/* First processor-specific. */')
    PsiWhiteSpace('\n')
    PsiWhiteSpace('      ')
    ConstSpecImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('ET_HIPROC')
      PsiWhiteSpace(' ')
      TypeNameImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('Type')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralIntegerImpl
          PsiElement(LITERAL_HEX)('0xffff')
    PsiWhiteSpace(' ')
    PsiComment(ML_COMMENT)('/* Last processor-specific. */')
    PsiWhiteSpace('\n')
    PsiElement())(')')
