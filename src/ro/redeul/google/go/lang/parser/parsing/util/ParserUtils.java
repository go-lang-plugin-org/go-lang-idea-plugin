package ro.redeul.google.go.lang.parser.parsing.util;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 9:12:06 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParserUtils {

    /**
     * Auxiliary method for strict token appearance
     *
     * @param builder  current builder
     * @param elem     given element
     * @param errorMsg Message, that displays if element was not found; if errorMsg == null nothing displays
     * @return true if element parsed
     */
    public static boolean getToken(PsiBuilder builder, IElementType elem, String errorMsg) {
        if (elem.equals(builder.getTokenType())) {
            builder.advanceLexer();
            return true;
        } else {
            if (errorMsg != null)
                builder.error(errorMsg);
            return false;
        }
    }

    /**
     * Auxiliary method for construction like
     * <BNF>
     * token?
     * </BNF>
     * parsing
     *
     * @param builder current builder
     * @param elem    given element
     * @return true if element parsed
     */
    public static boolean getToken(PsiBuilder builder, IElementType elem) {
        if (elem.equals(builder.getTokenType())) {
            builder.advanceLexer();
            return true;
        }
        return false;
    }

    /**
     * Same as simple getToken() method but with TokenSet
     *
     * @param builder
     * @param tokenSet
     * @return
     */
    public static boolean getToken(PsiBuilder builder, TokenSet tokenSet) {
        if (tokenSet.contains(builder.getTokenType())) {
            return getToken(builder, builder.getTokenType(), null);
        }
        return false;
    }

    /**
     * Same as simple getToken() method but with TokenSet
     *
     * @param builder
     * @param tokenSet
     * @return
     */
    public static boolean getToken(PsiBuilder builder, TokenSet tokenSet, String msg) {
        if (tokenSet.contains(builder.getTokenType())) {
            return getToken(builder, builder.getTokenType(), msg);
        }
        return false;
    }

    /**
     * Checks, that following element sequence is like given
     *
     * @param builder Given PsiBuilder
     * @param elems   Array of need elements in order
     * @return true if following sequence is like a given
     */
    public static boolean lookAhead(PsiBuilder builder, IElementType ... elems) {
        if (!elems[0].equals(builder.getTokenType())) return false;

        if (elems.length == 1) return true;

        PsiBuilder.Marker rb = builder.mark();
        builder.advanceLexer();
        int i = 1;
        while (!builder.eof() && i < elems.length && (builder.getTokenType() == GoElementTypes.wsNLS ||  elems[i].equals(builder.getTokenType()))) {

            if ( builder.getTokenType() != GoElementTypes.wsNLS ) {
                i++;
            }
            
            builder.advanceLexer();
        }
        rb.rollbackTo();
        return i == elems.length;
    }

    /**
     * Checks, that following element sequence is like given
     *
     * @param builder   Given PsiBuilder
     * @param tokenSets Array of need elements in order
     * @return true if following sequence is like a given
     */
    public static boolean lookAhead(PsiBuilder builder, TokenSet... tokenSets) {
        if (!tokenSets[0].contains(builder.getTokenType())) return false;

        if (tokenSets.length == 1) return true;

        PsiBuilder.Marker rb = builder.mark();
        builder.advanceLexer();
        int i = 1;
        while (!builder.eof() && i < tokenSets.length && tokenSets[i].contains(builder.getTokenType())) {
            builder.advanceLexer();
            i++;
        }
        rb.rollbackTo();
        return i == tokenSets.length;
    }

    /**
     * Checks, that following element sequence is like given
     *
     * @param builder    Given PsiBuilder
     * @param dropMarker to drop marker after successful checking or rollback it?
     * @param elems      Array of need elements in order
     * @return true if following sequence is like a given
     */
    public static boolean lookAhead(PsiBuilder builder, boolean dropMarker, IElementType... elems) {

        if (elems.length == 0) {
            return false;
        }

        if (elems.length == 1) {
            return elems[0].equals(builder.getTokenType());
        }

        PsiBuilder.Marker rb = builder.mark();
        int i = 0;
        while (!builder.eof() && i < elems.length && elems[i].equals(builder.getTokenType())) {
            builder.advanceLexer();
            i++;
        }
        if (dropMarker && i == elems.length) {
            rb.drop();
        } else {
            rb.rollbackTo();
        }
        return i == elems.length;
    }

    /**
     * Wraps current token to node with specified element type.test
     *
     * @param builder Given builder
     * @param elem    Node element
     * @return elem type.test
     */
    public static IElementType eatElement(PsiBuilder builder, IElementType elem) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer();
        marker.done(elem);
        return elem;
    }

    /**
     * Wraps current token with error message
     *
     * @param builder
     * @param msg     Error message
     */
    public static void wrapError(PsiBuilder builder, String msg) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer();
        marker.error(msg);
    }

    public static void waitNextRCurly(PsiBuilder builder) {
        int i = 0;
        PsiBuilder.Marker em = builder.mark();
        while (!builder.eof() && !GoElementTypes.pRCURLY.equals(builder.getTokenType())) {
            builder.advanceLexer();
            i++;
        }
        if (i > 0) {
            em.error("rcurly.expected");
        } else {
            em.drop();
        }
    }

    public static void waitNextSemi(PsiBuilder builder) {
        int i = 0;
        PsiBuilder.Marker em = builder.mark();
        while (!builder.eof() && !GoElementTypes.oSEMI.equals(builder.getTokenType())) {
            builder.advanceLexer();
            i++;
        }
        if (i > 0) {
            em.error("semicolon.expected");
        } else {
            em.drop();
        }
    }

    public static void waitNext(PsiBuilder builder, IElementType elem, String errorMessage) {
        int i = 0;
        PsiBuilder.Marker em = builder.mark();
        while (!builder.eof() && !elem.equals(builder.getTokenType())) {
            builder.advanceLexer();
            i++;
        }
        if (i > 0) {
            em.error(errorMessage);
        } else {
            em.drop();
        }
    }

    public static boolean waitNext(PsiBuilder builder, IElementType elem) {
        int i = 0;
        while (!builder.eof() && !elem.equals(builder.getTokenType())) {
            builder.advanceLexer();
            i++;
        }
        return i == 0;
    }

    public static boolean waitNext(PsiBuilder builder, TokenSet tokenSet) {
        int i = 0;

        while (!builder.eof() && !tokenSet.contains(builder.getTokenType())) {
            builder.advanceLexer();
            i++;
        }
        
        return i == 0;
    }

    public static void waitNext(PsiBuilder builder, TokenSet tokenSet, String errorMessage) {
        int i = 0;

        PsiBuilder.Marker marker = builder.mark();
        while (!builder.eof() && !tokenSet.contains(builder.getTokenType())) {
            builder.advanceLexer();
            i++;
        }

        if (i > 0) {
            marker.error(errorMessage);
        } else {
            marker.drop();
        }
    }

    public static void advance(PsiBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            builder.getTokenText();
            builder.advanceLexer();
        }
    }
    
    public static void advance(PsiBuilder builder) {
        advance(builder, 1);
    }
    
    public static void skipNLS(PsiBuilder builder) {
        while (builder.getTokenType() == GoElementTypes.wsNLS) {
            builder.advanceLexer();            
        }
    }
}
