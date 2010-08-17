package ro.redeul.google.go.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.parsing.declarations.Declaration;
import ro.redeul.google.go.lang.parser.parsing.declarations.FunctionOrMethodDeclaration;
import ro.redeul.google.go.lang.parser.parsing.expressions.Expressions;
import ro.redeul.google.go.lang.parser.parsing.helpers.IdentifierList;
import ro.redeul.google.go.lang.parser.parsing.statements.BlockStatement;
import ro.redeul.google.go.lang.parser.parsing.statements.Statements;
import ro.redeul.google.go.lang.parser.parsing.toplevel.CompilationUnit;
import ro.redeul.google.go.lang.parser.parsing.types.Types;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 7:31:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoParser implements PsiParser {

    @NotNull
    public ASTNode parse(IElementType root, PsiBuilder builder) {

        builder.setDebugMode(true);

        PsiBuilder.Marker rootMarker = builder.mark();

        CompilationUnit.parse(builder, this);
        
        while ( ! builder.eof() ) {
            builder.advanceLexer();
        }
        
        rootMarker.done(root);

        return builder.getTreeBuilt();
    }

    public boolean parseTopLevelDeclarations(PsiBuilder builder) {

        while ( ! builder.eof() ) {
            
            if ( ! parseTopLevelDeclaration(builder) ) {
                ParserUtils.wrapError(builder, "unknown.token");                
            }

            ParserUtils.skipNLS(builder);
        }

        return true;
    }

    private boolean parseTopLevelDeclaration(PsiBuilder builder) {

        ParserUtils.skipNLS(builder);
        
        if (GoTokenTypes.kFUNC.equals(builder.getTokenType())) {
            return FunctionOrMethodDeclaration.parse(builder, this);
        }

        return Declaration.parse(builder, this);
    }

    public boolean parseExpression(PsiBuilder builder, boolean inControlStmts) {
        return Expressions.parse(builder, this, inControlStmts);
    }

    public boolean parseType(PsiBuilder builder) {
        return Types.parseTypeDeclaration(builder, this);
    }

    public int parseIdentifierList(PsiBuilder builder) {
        return IdentifierList.parse(builder, this);
    }

    public boolean parseBody(PsiBuilder builder) {
        return BlockStatement.parse(builder, this);
    }

    public boolean parseStatement(PsiBuilder builder) {
        return Statements.parse(builder, this, false);
    }

    public boolean parseStatementSimple(PsiBuilder builder, boolean inControlClause) {
        return Statements.parseSimple(builder, this, inControlClause);
    }

    public boolean parseSimpleType(PsiBuilder builder) {
        return Types.parseTypeName(builder, this);
    }

    public boolean parseMethodSignature(PsiBuilder builder) {
        return FunctionOrMethodDeclaration.parseSignature(builder, this);
    }

    public int parseExpressionList(PsiBuilder builder, boolean inControlStmts) {
        return Expressions.parseList(builder, this, inControlStmts);
    }

    public boolean parsePrimaryExpression(PsiBuilder builder, boolean inControlStmts) {
        return Expressions.parsePrimary(builder, this, inControlStmts);
    }

    public boolean parseFunctionSignature(PsiBuilder builder) {
        return FunctionOrMethodDeclaration.parseCompleteMethodSignature(builder, this);
    }

    public int parseTypeList(PsiBuilder builder) {
        return Types.parseTypeDeclarationList(builder, this);
    }

    public boolean tryParseSimpleStmt(PsiBuilder builder, boolean inControlStmt) {
        return Statements.tryParseSimple(builder, this, inControlStmt);
    }
}
