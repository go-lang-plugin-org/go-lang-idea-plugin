package ro.redeul.google.go.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.lexer.GoLexer;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.psi.impl.GoFileImpl;
import ro.redeul.google.go.lang.psi.stubs.elements.GoStubFileElementType;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 7:29:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoParserDefinition implements ParserDefinition {

    public static final IStubFileElementType GO_FILE_TYPE = new GoStubFileElementType(GoFileType.GO_FILE_TYPE.getLanguage());    

    @NotNull
    public Lexer createLexer(Project project) {
        return new GoLexer();
    }

    public PsiParser createParser(Project project) {
        return new GoParser(); 
    }

    public IFileElementType getFileNodeType() {
        return GO_FILE_TYPE;
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return GoTokenTypeSets.WHITESPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return GoTokenTypeSets.COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return GoTokenTypeSets.STRINGS;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return GoPsiCreator.createElement(node);
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new GoFileImpl(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
