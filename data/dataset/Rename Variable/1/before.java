package com.puppycrawl.tools.checkstyle.checks.javadoc;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.DetailNode;
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;
import com.puppycrawl.tools.checkstyle.utils.JavadocUtil;

@StatelessCheck
public class JavadocParagraphCheck extends AbstractJavadocCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_TAG_AFTER = "javadoc.paragraph.tag.after";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_LINE_BEFORE = "javadoc.paragraph.line.before";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_REDUNDANT_PARAGRAPH = "javadoc.paragraph.redundant.paragraph";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_MISPLACED_TAG = "javadoc.paragraph.misplaced.tag";

    /**
     * Control whether the &lt;p&gt; tag should be placed immediately before the first word.
     */
    private boolean allowNewlineParagraph = true;

    /**
     * Setter to control whether the &lt;p&gt; tag should be placed
     * immediately before the first word.
     *
     * @param value value to set.
     * @since 6.9
     */
    public void setAllowNewlineParagraph(boolean value) {
        allowNewlineParagraph = value;
    }

    @Override
    public int[] getDefaultJavadocTokens() {
        return new int[] {
                JavadocTokenTypes.NEWLINE,
                JavadocTokenTypes.HTML_ELEMENT,
        };
    }

    @Override
    public int[] getRequiredJavadocTokens() {
        return getAcceptableJavadocTokens();
    }

    @Override
    public void visitJavadocToken(DetailNode ast) {
        if (ast.getType() == JavadocTokenTypes.NEWLINE && isEmptyLine(ast)) {
            checkEmptyLine(ast);
        }
        else if (ast.getType() == JavadocTokenTypes.HTML_ELEMENT
                && JavadocUtil.getFirstChild(ast).getType() == JavadocTokenTypes.P_TAG_START) {
            checkParagraphTag(ast);
        }
    }

    /**
     * Determines whether or not the next line after empty line has paragraph tag in the beginning.
     *
     * @param newline NEWLINE node.
     */
    private void checkEmptyLine(DetailNode newline) {
        final DetailNode nearestToken = getNearestNode(newline);
        if (nearestToken.getType() == JavadocTokenTypes.TEXT
                && !CommonUtil.isBlank(nearestToken.getText())) {
            log(newline.getLineNumber(), MSG_TAG_AFTER);
        }
    }

    /**
     * Determines whether or not the line with paragraph tag has previous empty line.
     *
     * @param tag html tag.
     */
    private void checkParagraphTag(DetailNode tag) {
        final DetailNode newLine = getNearestEmptyLine(tag);
        if (isFirstParagraph(tag)) {
            log(tag.getLineNumber(), MSG_REDUNDANT_PARAGRAPH);
        }
        else if (newLine == null || tag.getLineNumber() - newLine.getLineNumber() != 1) {
            log(tag.getLineNumber(), MSG_LINE_BEFORE);
        }
        if (allowNewlineParagraph && isImmediatelyFollowedByText(tag)) {
            log(tag.getLineNumber(), MSG_MISPLACED_TAG);
        }
    }

    /**
     * Returns nearest node.
     *
     * @param node DetailNode node.
     * @return nearest node.
     */
    private static DetailNode getNearestNode(DetailNode node) {
        DetailNode tag = node;
        while (tag.getType() == JavadocTokenTypes.LEADING_ASTERISK
                || tag.getType() == JavadocTokenTypes.NEWLINE) {
            tag = JavadocUtil.getNextSibling(tag);
        }
        return tag;
    }

    /**
     * Determines whether or not the line is empty line.
     *
     * @param newLine NEWLINE node.
     * @return true, if line is empty line.
     */
    private static boolean isEmptyLine(DetailNode newLine) {
        boolean result = false;
        DetailNode previousSibling = JavadocUtil.getPreviousSibling(newLine);
        if (previousSibling != null
                && previousSibling.getParent().getType() == JavadocTokenTypes.JAVADOC) {
            if (previousSibling.getType() == JavadocTokenTypes.TEXT
                    && CommonUtil.isBlank(previousSibling.getText())) {
                previousSibling = JavadocUtil.getPreviousSibling(previousSibling);
            }
            result = previousSibling != null
                    && previousSibling.getType() == JavadocTokenTypes.LEADING_ASTERISK;
        }
        return result;
    }

    /**
     * Determines whether or not the line with paragraph tag is first line in javadoc.
     *
     * @param paragraphTag paragraph tag.
     * @return true, if line with paragraph tag is first line in javadoc.
     */
    private static boolean isFirstParagraph(DetailNode paragraphTag) {
        boolean result = true;
        DetailNode previousNode = JavadocUtil.getPreviousSibling(paragraphTag);
        while (previousNode != null) {
            if (previousNode.getType() == JavadocTokenTypes.TEXT
                    && !CommonUtil.isBlank(previousNode.getText())
                    || previousNode.getType() != JavadocTokenTypes.LEADING_ASTERISK
                    && previousNode.getType() != JavadocTokenTypes.NEWLINE
                    && previousNode.getType() != JavadocTokenTypes.TEXT) {
                result = false;
                break;
            }
            previousNode = JavadocUtil.getPreviousSibling(previousNode);
        }
        return result;
    }

    /**
     * Finds and returns nearest empty line in javadoc.
     *
     * @param node DetailNode node.
     * @return Some nearest empty line in javadoc.
     */
    private static DetailNode getNearestEmptyLine(DetailNode node) {
        DetailNode newLine = JavadocUtil.getPreviousSibling(node);
        while (newLine != null) {
            final DetailNode previousSibling = JavadocUtil.getPreviousSibling(newLine);
            if (newLine.getType() == JavadocTokenTypes.NEWLINE && isEmptyLine(newLine)) {
                break;
            }
            newLine = previousSibling;
        }
        return newLine;
    }

    /**
     * Tests whether the paragraph tag is immediately followed by the text.
     *
     * @param tag html tag.
     * @return true, if the paragraph tag is immediately followed by the text.
     */
    private static boolean isImmediatelyFollowedByText(DetailNode tag) {
        final DetailNode nextSibling = JavadocUtil.getNextSibling(tag);
        return nextSibling.getType() == JavadocTokenTypes.NEWLINE
                || nextSibling.getType() == JavadocTokenTypes.EOF
                || nextSibling.getText().startsWith(" ");
    }

}