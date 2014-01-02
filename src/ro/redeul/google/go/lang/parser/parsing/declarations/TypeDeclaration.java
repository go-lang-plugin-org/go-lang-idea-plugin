package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.types.Types;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.completeStatement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 9:38:55 PM
 */
class TypeDeclaration implements GoElementTypes {

  public static IElementType parse(PsiBuilder builder, GoParser parser) {

    PsiBuilder.Marker typeDeclarations = builder.mark();

    if (!ParserUtils.getToken(builder, kTYPE)) {
      ParserUtils.wrapError(builder, "type.keyword.expected");
      typeDeclarations.drop();
      return null;
    }

    NestedDeclarationParser.parseNestedOrBasicDeclaration(
      builder, parser,
      new NestedDeclarationParser.DeclarationParser() {
        public boolean parse(PsiBuilder builder, GoParser parser, boolean shouldComplete) {
          return parseTypeSpecification(builder, parser, shouldComplete);
        }
      });

    return completeStatement(builder, typeDeclarations, TYPE_DECLARATIONS);
  }

  static TokenSet localImportTokens = TokenSet.create(mIDENT, oDOT);

  private static boolean parseTypeSpecification(PsiBuilder builder, GoParser parser,
                                                boolean shouldComplete) {

    if (!ParserUtils.lookAhead(builder, mIDENT)) {
      builder.error("error.identifier.expected");
      return false;
    }

    PsiBuilder.Marker declaration = builder.mark();
    ParserUtils.eatElement(builder, TYPE_NAME_DECLARATION);

    if (Types.parseTypeDeclaration(builder, parser) == null) {
      builder.error(GoBundle.message("error.type.expected"));
    }

    if ( shouldComplete)
      completeStatement(builder, declaration, TYPE_DECLARATION);
    else
      declaration.done(TYPE_DECLARATION);

    return true;
  }
}
