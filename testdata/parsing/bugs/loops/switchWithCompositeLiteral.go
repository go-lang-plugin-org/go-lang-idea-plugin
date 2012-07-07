package main
func (p *Package) gccMachine() []string {
	switch goarch {
	case "amd64":
		return []string{"-m64"}
	case "386":
		return []string{"-m32"}
	}
	return nil
}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  MethodDeclaration(gccMachine)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    PsiElement(()('(')
    MethodReceiverImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('p')
      PsiWhiteSpace(' ')
      TypePointerImpl
        PsiElement(*)('*')
        TypeNameImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('Package')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('gccMachine')
    PsiElement(()('(')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    FunctionResult
      FunctionParameterListImpl
        FunctionParameterImpl
          TypeSliceImpl
            PsiElement([)('[')
            PsiElement(])(']')
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('string')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('\t')
      SwitchExprStmt
        PsiElement(KEYWORD_SWITCH)('switch')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('goarch')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('\t')
        SwitchExprCase
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralStringImpl
              PsiElement(LITERAL_STRING)('"amd64"')
          PsiElement(:)(':')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('\t\t')
          ReturnStmtImpl
            PsiElement(KEYWORD_RETURN)('return')
            PsiWhiteSpace(' ')
            LiteralExpressionImpl
              LiteralCompositeImpl
                TypeSliceImpl
                  PsiElement([)('[')
                  PsiElement(])(']')
                  TypeNameImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('string')
                LiteralCompositeValueImpl
                  PsiElement({)('{')
                  LiteralCompositeElementImpl
                    LiteralExpressionImpl
                      LiteralStringImpl
                        PsiElement(LITERAL_STRING)('"-m64"')
                  PsiElement(})('}')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('\t')
        SwitchExprCase
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          LiteralExpressionImpl
            LiteralStringImpl
              PsiElement(LITERAL_STRING)('"386"')
          PsiElement(:)(':')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('\t\t')
          ReturnStmtImpl
            PsiElement(KEYWORD_RETURN)('return')
            PsiWhiteSpace(' ')
            LiteralExpressionImpl
              LiteralCompositeImpl
                TypeSliceImpl
                  PsiElement([)('[')
                  PsiElement(])(']')
                  TypeNameImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('string')
                LiteralCompositeValueImpl
                  PsiElement({)('{')
                  LiteralCompositeElementImpl
                    LiteralExpressionImpl
                      LiteralStringImpl
                        PsiElement(LITERAL_STRING)('"-m32"')
                  PsiElement(})('}')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('\t')
        PsiElement(})('}')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('\t')
      ReturnStmtImpl
        PsiElement(KEYWORD_RETURN)('return')
        PsiWhiteSpace(' ')
        LiteralExpressionImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('nil')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
