package main

func Foo(x interface{}) {
    var c, c1, c2, c3 chan int
    var i1, i2 int
    select {
        case i1 = <-c1:
            print("received ", i1, " from c1\n")
        case c2 <- i2:
            print("sent ", i2, " to c2\n")
        case i3, ok := (<-c3): // same as: i3, ok := <-c3
            if ok {
                print("received ", i3, " from c3\n")
            } else {
                print("c3 is closed\n")
            }
        default:
            print("no communication\n")
    }

    for { // send random sequence of bits to c
        select {
            case c <- 0: // note: no statement, no fallthrough, no folding of cases
            case c <- 1:
        }
    }

    select {} // block forever
}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n\n')
  FunctionDeclaration(Foo)
    PsiElement(KEYWORD_FUNC)('func')
    PsiWhiteSpace(' ')
    LiteralIdentifierImpl
      PsiElement(IDENTIFIER)('Foo')
    PsiElement(()('(')
    FunctionParameterListImpl
      FunctionParameterImpl
        LiteralIdentifierImpl
          PsiElement(IDENTIFIER)('x')
        PsiWhiteSpace(' ')
        TypeInterfaceImpl
          PsiElement(KEYWORD_INTERFACE)('interface')
          PsiElement({)('{')
          PsiElement(})('}')
    PsiElement())(')')
    PsiWhiteSpace(' ')
    BlockStmtImpl
      PsiElement({)('{')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      VarDeclarationsImpl
        PsiElement(KEYWORD_VAR)('var')
        PsiWhiteSpace(' ')
        VarDeclarationImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('c')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('c1')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('c2')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('c3')
          PsiWhiteSpace(' ')
          TypeChanBidiImpl
            PsiElement(KEYWORD_CHAN)('chan')
            PsiWhiteSpace(' ')
            TypeNameImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('int')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      VarDeclarationsImpl
        PsiElement(KEYWORD_VAR)('var')
        PsiWhiteSpace(' ')
        VarDeclarationImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('i1')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('i2')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
      PsiWhiteSpace('\n')
      PsiWhiteSpace('    ')
      SelectStmtImpl
        PsiElement(KEYWORD_SELECT)('select')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SelectCommClauseRecvImpl
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          SelectCaseRecvExpr
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('i1')
            PsiWhiteSpace(' ')
            PsiElement(=)('=')
            PsiWhiteSpace(' ')
            UnaryExpressionImpl
              PsiElement(<-)('<-')
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('c1')
          PsiElement(:)(':')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('            ')
          ExpressionStmtImpl
            BuiltInCallExpressionImpl
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('print')
              PsiElement(()('(')
              ExpressionListImpl
                LiteralExpressionImpl
                  LiteralStringImpl
                    PsiElement(LITERAL_STRING)('"received "')
                PsiElement(,)(',')
                PsiWhiteSpace(' ')
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('i1')
                PsiElement(,)(',')
                PsiWhiteSpace(' ')
                LiteralExpressionImpl
                  LiteralStringImpl
                    PsiElement(LITERAL_STRING)('" from c1\n"')
              PsiElement())(')')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SelectCommClauseSendImpl
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          SendStmtImpl
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('c2')
            PsiWhiteSpace(' ')
            PsiElement(<-)('<-')
            PsiWhiteSpace(' ')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('i2')
          PsiElement(:)(':')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('            ')
          ExpressionStmtImpl
            BuiltInCallExpressionImpl
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('print')
              PsiElement(()('(')
              ExpressionListImpl
                LiteralExpressionImpl
                  LiteralStringImpl
                    PsiElement(LITERAL_STRING)('"sent "')
                PsiElement(,)(',')
                PsiWhiteSpace(' ')
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('i2')
                PsiElement(,)(',')
                PsiWhiteSpace(' ')
                LiteralExpressionImpl
                  LiteralStringImpl
                    PsiElement(LITERAL_STRING)('" to c2\n"')
              PsiElement())(')')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SelectCommClauseRecvImpl
          PsiElement(KEYWORD_CASE)('case')
          PsiWhiteSpace(' ')
          SelectCaseRecvExpr
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('i3')
            PsiElement(,)(',')
            PsiWhiteSpace(' ')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('ok')
            PsiWhiteSpace(' ')
            PsiElement(:=)(':=')
            PsiWhiteSpace(' ')
            ParenthesisedExpressionImpl
              PsiElement(()('(')
              UnaryExpressionImpl
                PsiElement(<-)('<-')
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('c3')
              PsiElement())(')')
          PsiElement(:)(':')
          PsiWhiteSpace(' ')
          PsiComment(SL_COMMENT)('// same as: i3, ok := <-c3')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('            ')
          IfStmtImpl
            PsiElement(KEYWORD_IF)('if')
            PsiWhiteSpace(' ')
            LiteralExpressionImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('ok')
            PsiWhiteSpace(' ')
            BlockStmtImpl
              PsiElement({)('{')
              PsiWhiteSpace('\n')
              PsiWhiteSpace('                ')
              ExpressionStmtImpl
                BuiltInCallExpressionImpl
                  LiteralExpressionImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('print')
                  PsiElement(()('(')
                  ExpressionListImpl
                    LiteralExpressionImpl
                      LiteralStringImpl
                        PsiElement(LITERAL_STRING)('"received "')
                    PsiElement(,)(',')
                    PsiWhiteSpace(' ')
                    LiteralExpressionImpl
                      LiteralIdentifierImpl
                        PsiElement(IDENTIFIER)('i3')
                    PsiElement(,)(',')
                    PsiWhiteSpace(' ')
                    LiteralExpressionImpl
                      LiteralStringImpl
                        PsiElement(LITERAL_STRING)('" from c3\n"')
                  PsiElement())(')')
              PsiWhiteSpace('\n')
              PsiWhiteSpace('            ')
              PsiElement(})('}')
            PsiWhiteSpace(' ')
            PsiElement(KEYWORD_ELSE)('else')
            PsiWhiteSpace(' ')
            BlockStmtImpl
              PsiElement({)('{')
              PsiWhiteSpace('\n')
              PsiWhiteSpace('                ')
              ExpressionStmtImpl
                BuiltInCallExpressionImpl
                  LiteralExpressionImpl
                    LiteralIdentifierImpl
                      PsiElement(IDENTIFIER)('print')
                  PsiElement(()('(')
                  LiteralExpressionImpl
                    LiteralStringImpl
                      PsiElement(LITERAL_STRING)('"c3 is closed\n"')
                  PsiElement())(')')
              PsiWhiteSpace('\n')
              PsiWhiteSpace('            ')
              PsiElement(})('}')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('        ')
        SelectCommClauseDefaultImpl
          PsiElement(KEYWORD_DEFAULT)('default')
          PsiElement(:)(':')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('            ')
          ExpressionStmtImpl
            BuiltInCallExpressionImpl
              LiteralExpressionImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('print')
              PsiElement(()('(')
              LiteralExpressionImpl
                LiteralStringImpl
                  PsiElement(LITERAL_STRING)('"no communication\n"')
              PsiElement())(')')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('    ')
        PsiElement(})('}')
      PsiWhiteSpace('\n\n')
      PsiWhiteSpace('    ')
      ForWithConditionStmtImpl
        PsiElement(KEYWORD_FOR)('for')
        PsiWhiteSpace(' ')
        BlockStmtImpl
          PsiElement({)('{')
          PsiWhiteSpace(' ')
          PsiComment(SL_COMMENT)('// send random sequence of bits to c')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('        ')
          SelectStmtImpl
            PsiElement(KEYWORD_SELECT)('select')
            PsiWhiteSpace(' ')
            PsiElement({)('{')
            PsiWhiteSpace('\n')
            PsiWhiteSpace('            ')
            SelectCommClauseSendImpl
              PsiElement(KEYWORD_CASE)('case')
              PsiWhiteSpace(' ')
              SendStmtImpl
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('c')
                PsiWhiteSpace(' ')
                PsiElement(<-)('<-')
                PsiWhiteSpace(' ')
                LiteralExpressionImpl
                  LiteralIntegerImpl
                    PsiElement(LITERAL_INT)('0')
              PsiElement(:)(':')
            PsiWhiteSpace(' ')
            PsiComment(SL_COMMENT)('// note: no statement, no fallthrough, no folding of cases')
            PsiWhiteSpace('\n')
            PsiWhiteSpace('            ')
            SelectCommClauseSendImpl
              PsiElement(KEYWORD_CASE)('case')
              PsiWhiteSpace(' ')
              SendStmtImpl
                LiteralExpressionImpl
                  LiteralIdentifierImpl
                    PsiElement(IDENTIFIER)('c')
                PsiWhiteSpace(' ')
                PsiElement(<-)('<-')
                PsiWhiteSpace(' ')
                LiteralExpressionImpl
                  LiteralIntegerImpl
                    PsiElement(LITERAL_INT)('1')
              PsiElement(:)(':')
            PsiWhiteSpace('\n')
            PsiWhiteSpace('        ')
            PsiElement(})('}')
          PsiWhiteSpace('\n')
          PsiWhiteSpace('    ')
          PsiElement(})('}')
      PsiWhiteSpace('\n\n')
      PsiWhiteSpace('    ')
      SelectStmtImpl
        PsiElement(KEYWORD_SELECT)('select')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiElement(})('}')
      PsiWhiteSpace(' ')
      PsiComment(SL_COMMENT)('// block forever')
      PsiWhiteSpace('\n')
      PsiElement(})('}')
