package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;

import java.util.*;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Spacings;
import static ro.redeul.google.go.formatter.blocks.GoFormatterUtil.*;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceNode;

/**
 * TODO: Document this
 * <p/>
 * Created on Dec-30-2013 22:41
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoSyntheticBlock<GoPsiType extends GoPsiElement> implements ASTBlock, GoElementTypes {

  private final ASTNode myASTNode;
  private final CommonCodeStyleSettings mySettings;

  private final Indent myIndent;
  private final Wrap myWrap;
  private final Alignment myAlignment;

  private List<Block> mySubBlocks = null;
  private Map<Alignments.Key, Alignment> knownAlignments = null;

  // multiline mode support
  private boolean myMultiLineMode = false;
  private IElementType myLeftBreakElement = null;
  private IElementType myRightBreakElement = null;

  // column alignment support
  private Set<Alignments.Key> myAlignmentKeys = Alignments.EMPTY_KEY_SET;

  // vertical line breaking tokens
  private TokenSet myLineBreakingTokens = TokenSet.EMPTY;

  protected GoSyntheticBlock(@NotNull GoPsiType myNode, CommonCodeStyleSettings mySettings) {
    this(myNode, mySettings, null, Alignments.NONE, Alignments.EMPTY_MAP);
  }

  protected GoSyntheticBlock(@NotNull GoPsiType myNode, CommonCodeStyleSettings mySettings,
                             Indent indent) {
    this(myNode, mySettings, indent, Alignments.NONE, Alignments.EMPTY_MAP);
  }

  protected GoSyntheticBlock(@NotNull GoPsiType node, CommonCodeStyleSettings settings,
                             Indent indent, Alignment alignment,
                             @NotNull Map<Alignments.Key, Alignment> alignsToUse) {
    this.myASTNode = node.getNode();
    this.mySettings = settings;
    this.myIndent = indent;
    this.myWrap = null;
    this.myAlignment = alignment;

    this.knownAlignments = alignsToUse;
  }

  protected void setMultiLineMode(boolean multilineMode,
                                  IElementType leftBreakElement,
                                  IElementType rightBreakElement) {
    this.myMultiLineMode = multilineMode;
    this.myLeftBreakElement = leftBreakElement;
    this.myRightBreakElement = rightBreakElement;
  }

  protected void setAlignmentKeys(@NotNull Set<Alignments.Key> alignmentKeys) {
    this.myAlignmentKeys = alignmentKeys;
  }

  protected void setLineBreakingTokens(@NotNull TokenSet lineBreakingTokens) {
    this.myLineBreakingTokens = lineBreakingTokens;
  }

  @NotNull
  public ASTNode getNode() {
    return myASTNode;
  }

  @NotNull
  public TextRange getTextRange() {
    return myASTNode.getTextRange();
  }

  @NotNull
  public List<Block> getSubBlocks() {
    if (mySubBlocks == null) {
      List<Block> children = buildChildren();
      if (children == null || children.isEmpty()) {
        mySubBlocks = Collections.emptyList();
      } else {
        mySubBlocks = children;
      }
    }

    return mySubBlocks;
  }

  @NotNull
  protected Set<Alignments.Key> getAlignmentKeys() {
    return myAlignmentKeys;
  }

  @Nullable
  List<Block> buildChildren() {
    List<Block> children = new ArrayList<Block>();

    Map<Alignments.Key, Alignment> alignmentsMap = Alignments.set(getAlignmentKeys());

    ASTNode prevChild = null;
    IElementType prevChildType = null;

    for (ASTNode child : getGoChildren()) {
      PsiElement childPsi = child.getPsi();

      if (child.getTextRange().getLength() == 0 || isWhiteSpaceNode(childPsi)) {
        continue;
      }

      int linesBetween = getLineCount(getTextBetween(prevChild, child));

      PsiElement prevChildPsi = prevChild != null ? prevChild.getPsi() : null;

      Indent childIndent = getChildIndent(childPsi, prevChildPsi);
      Alignment childAlign = getChildAlignment(childPsi, prevChildPsi, knownAlignments);

      if (wantsToBreakLine(prevChildType) && !holdTogether(prevChildType, child.getElementType(), linesBetween))
        alignmentsMap = Alignments.set(getAlignmentKeys());

      children.add(GoBlocks.generate(child, mySettings, childIndent, childAlign, alignmentsMap));

      prevChild = child;
      prevChildType = getASTElementType(prevChild);
    }

    return children;
  }

  @Nullable
  @Override
  public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {

    if (child1 == null)
      return Spacings.NONE;

    if (!isMultiLine())
      return Spacings.BASIC;

    IElementType typeChild1 = getASTElementType(child1);
    IElementType typeChild2 = getASTElementType(child2);

    if (isMultiLine() && (isLeftBreak(typeChild1) || isRightBreak(typeChild2)))
      return Spacings.ONE_LINE;

    if (!wantsToBreakLine(typeChild1))
      return Spacings.BASIC;

    if (holdTogether(typeChild1, typeChild2, getLineCount(getTextBetween(child1, child2))))
      return Spacings.ONE_LINE;

    return Spacings.EMPTY_LINE;
  }

  protected boolean isRightBreak(IElementType typeChild) {
    return isMultiLine() && typeChild == myRightBreakElement;
  }

  protected boolean isLeftBreak(IElementType typeChild) {
    return isMultiLine() && typeChild == myLeftBreakElement;
  }

  protected boolean isMultiLine() {
    return myMultiLineMode;
  }

  protected boolean wantsToBreakLine(IElementType child) {
    return this.myLineBreakingTokens.contains(child);
  }

  protected boolean holdTogether(@Nullable IElementType typeChild1,
                                 @Nullable IElementType typeChild2,
                                 int linesBetween) {
    if (linesBetween > 1)
      return false;

    if (typeChild1 == typeChild2)
      return true;

    if (COMMENTS.contains(typeChild1) && COMMENTS.contains(typeChild2))
      return true;

    return false;
  }

  @Nullable
  protected Alignment getChildAlignment(@NotNull PsiElement child,
                                        @Nullable PsiElement prevChild,
                                        Map<Alignments.Key, Alignment> alignments) {
    return null;
  }

  protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
    return Indent.getNoneIndent();
  }

  @Nullable
  @Override
  public Wrap getWrap() {
    return myWrap;
  }

  @Nullable
  @Override
  public Indent getIndent() {
    return myIndent;
  }

  @Nullable
  @Override
  public Alignment getAlignment() {
    return myAlignment;
  }


  @NotNull
  @Override
  public ChildAttributes getChildAttributes(int newChildIndex) {
    return ChildAttributes.DELEGATE_TO_NEXT_CHILD;
  }

  @Override
  public boolean isIncomplete() {
    return false;
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  protected ASTNode[] getGoChildren() {
    PsiElement psi = myASTNode.getPsi();
    if (psi instanceof OuterLanguageElement) {
      TextRange range = myASTNode.getTextRange();
      List<ASTNode> childList = new ArrayList<ASTNode>();
      PsiFile goFile = psi.getContainingFile().getViewProvider().getPsi(GoLanguage.INSTANCE);

      if (goFile instanceof GoFile) {
        addChildNodes(goFile, childList, range);
      }

      return childList.toArray(new ASTNode[childList.size()]);
    }

    return myASTNode.getChildren(null);
  }

  private static void addChildNodes(PsiElement elem,
                                    List<ASTNode> childNodes,
                                    TextRange range) {
    ASTNode node = elem.getNode();
    if (range.contains(elem.getTextRange()) && node != null) {
      childNodes.add(node);
    } else {
      for (PsiElement child : elem.getChildren()) {
        addChildNodes(child, childNodes, range);
      }
    }
  }
}
