package com.echosource.ada.lexer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.measures.CoreMetrics;

/**
 * 
 */
public class PageLineCounter implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(PageLineCounter.class);

  private int blankLines;
  private int commentLines;
  private int linesOfCode;

  private void addMeasures(AdaSourceCode sourceCode) {

    sourceCode.addMeasure(CoreMetrics.LINES, (double) linesOfCode + commentLines + blankLines);
    sourceCode.addMeasure(CoreMetrics.NCLOC, linesOfCode);
    sourceCode.addMeasure(CoreMetrics.COMMENT_LINES, commentLines);

    LOG.debug("WebSensor: " + sourceCode.toString() + ":" + linesOfCode + "," + commentLines + "," + blankLines);
  }

  public void count(List<Node> nodeList, AdaSourceCode sourceCode) {
    linesOfCode = 0;
    blankLines = 0;
    commentLines = 0;

    for (int i = 0; i < nodeList.size(); i++) {
      Node node = nodeList.get(i);
      Node previousNode = i > 0 ? nodeList.get(i - 1) : null;
      Node nextNode = i < nodeList.size() - 1 ? nodeList.get(i) : null;
      handleToken(node, previousNode, nextNode);
    }
    addMeasures(sourceCode);
  }

  private void handleToken(Node node, Node previousNode, Node nextNode) {

    int linesOfCodeCurrentNode = node.getLinesOfCode();
    if (nextNode == null) {
      linesOfCodeCurrentNode++;
    }

    switch (node.getNodeType()) {
      case Tag:
      case Directive:
      case Expression:
        linesOfCode += linesOfCodeCurrentNode;
        break;
      case Comment:
        commentLines += linesOfCodeCurrentNode;
        break;
      case Text:
        handleTextToken((TextNode) node, previousNode, linesOfCodeCurrentNode);

        break;
      default:
        break;
    }
  }

  private void handleTextToken(TextNode textNode, Node previousNode, int linesOfCodeCurrentNode) {

    if (textNode.isBlank() && linesOfCodeCurrentNode > 0) {
      int nonBlankLines = 0;

      // add one newline to the previous node
      if (previousNode != null) {
        switch (previousNode.getNodeType()) {
          case Comment:
            commentLines++;
            nonBlankLines++;
            break;
          case Tag:
          case Directive:
          case Expression:
            linesOfCode++;
            nonBlankLines++;
            break;
          default:
            break;
        }
      }

      // remaining newlines are added to blanklines
      if (linesOfCodeCurrentNode > 0) {
        blankLines += linesOfCodeCurrentNode - nonBlankLines;
      }
    } else {
      linesOfCode += linesOfCodeCurrentNode;
    }
  }
}