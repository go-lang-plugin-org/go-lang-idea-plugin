package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Spacings;
import static ro.redeul.google.go.formatter.blocks.GoFormatterUtil.getASTElementType;
import static ro.redeul.google.go.formatter.blocks.GoFormatterUtil.getLineCount;
import static ro.redeul.google.go.formatter.blocks.GoFormatterUtil.getTextBetween;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceNode;

/**
 * TODO: Document this
 * <p/>
 * Created on Dec-30-2013 22:41
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoSyntheticBlock<GoPsiType extends GoPsiElement> implements ASTBlock, GoElementTypes {

  final ASTNode myASTNode;
  final CommonCodeStyleSettings mySettings;

  final Indent myIndent;
  final Wrap myWrap;

  private List<Block> mySubBlocks = null;

  protected GoSyntheticBlock(@NotNull GoPsiType myNode, CommonCodeStyleSettings mySettings) {
    this(myNode, mySettings, null, null);
  }

  protected GoSyntheticBlock(@NotNull GoPsiType myNode, CommonCodeStyleSettings mySettings,
                             Indent indent) {
    this(myNode, mySettings, indent, null);
  }

  protected GoSyntheticBlock(@NotNull GoPsiType myNode, CommonCodeStyleSettings mySettings,
                             Wrap wrap) {
    this(myNode, mySettings, null, wrap);
  }

  protected GoSyntheticBlock(@NotNull GoPsiType myNode, CommonCodeStyleSettings mySettings,
                             Indent indent, Wrap wrap) {
    this.myASTNode = myNode.getNode();
    this.mySettings = mySettings;
    this.myIndent = indent;
    this.myWrap = wrap;
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


  @Nullable
  List<Block> buildChildren() {
    List<Block> children = new ArrayList<Block>();

    ASTNode prevChild = null;
    for (ASTNode child : getGoChildren()) {
      if (child.getTextRange().getLength() == 0 || isWhiteSpaceNode(child.getPsi())) {
        continue;
      }

      Indent indent = getChildIndent(
        child.getPsi(),
        prevChild != null ? prevChild.getPsi() : null
      );

      children.add(GoBlockGenerator.generateBlock(child, indent, mySettings));
      prevChild = child;
    }

    return children;
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
    return null;
  }

//  @Nullable
//  @Override
//  public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
//    return GoBlockUtil.BASIC_KEEP_BREAKS;
//  }

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

    if (!wantsBreakup(typeChild1))
      return Spacings.BASIC;

    if (holdTogether(typeChild1, typeChild2, getLineCount(getTextBetween(child1, child2))))
      return Spacings.ONE_LINE;

    return Spacings.EMPTY_LINE;
  }

  protected boolean isRightBreak(IElementType typeChild) {
    return false;
  }

  protected boolean isLeftBreak(IElementType typeChild) {
    return false;
  }

  protected boolean isMultiLine() {
    return false;
  }

  protected boolean wantsBreakup(IElementType typeChild1) {
    return false;
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


  @NotNull
  @Override
  public ChildAttributes getChildAttributes(int newChildIndex) {
    return ChildAttributes.DELEGATE_TO_NEXT_CHILD;
  }

//  @Nullable
//  @Override
//  public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
//    return null;
//  }
//
//  @NotNull
//  @Override
//  public ChildAttributes getChildAttributes(int newChildIndex) {
//    return null;
//  }

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
