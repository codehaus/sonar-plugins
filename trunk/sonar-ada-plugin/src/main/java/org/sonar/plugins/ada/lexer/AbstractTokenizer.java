package com.echosource.ada.lexer;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;

/**
 * 
 */
abstract class AbstractTokenizer<T extends List<Node>> extends Channel<T> {

  private final class EndTokenMatcher implements EndMatcher {

    private final CodeReader codeReader;
    private boolean quoting;
    private int nesting;

    private EndTokenMatcher(CodeReader codeReader) {
      this.codeReader = codeReader;
    }

    public boolean match(int endFlag) {
      if (endFlag == '"') {
        quoting = !quoting;
      }
      if ( !quoting) {
        boolean started = ArrayUtils.isEquals(codeReader.peek(startChars.length), startChars);
        if (started) {
          nesting++;
        } else {
          boolean ended = ArrayUtils.isEquals(codeReader.peek(endChars.length), endChars);
          if (ended) {
            nesting--;
            return nesting < 0;
          }
        }
      }
      return false;
    }
  }

  private final char[] endChars;

  private final char[] startChars;

  public AbstractTokenizer(String startChars, String endChars) {
    this.startChars = startChars.toCharArray();
    this.endChars = endChars.toCharArray();
  }

  protected void addNode(List<Node> nodeList, Node node) {
    nodeList.add(node);
  }

  @Override
  public boolean consume(CodeReader codeReader, T nodeList) {
    if (ArrayUtils.isEquals(codeReader.peek(startChars.length), startChars)) {
      Node node = createNode();
      setStartPosition(codeReader, node);

      StringBuilder stringBuilder = new StringBuilder();
      codeReader.popTo(new EndTokenMatcher(codeReader), stringBuilder);
      for (int i = 0; i < endChars.length; i++) {
        codeReader.pop(stringBuilder);
      }
      node.setCode(stringBuilder.toString());
      setEndPosition(codeReader, node);

      addNode(nodeList, node);

      return true;
    } else {
      return false;
    }
  }

  abstract Node createNode();

  protected final void setEndPosition(CodeReader code, Node node) {
    node.setEndLinePosition(code.getLinePosition());
    node.setEndColumnPosition(code.getColumnPosition());
  }

  protected final void setStartPosition(CodeReader code, Node node) {
    node.setStartLinePosition(code.getLinePosition());
    node.setStartColumnPosition(code.getColumnPosition());
  }
}