package com.echosource.ada.lexer;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sonar.api.BatchExtension;
import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;

/**
 * 
 */
@SuppressWarnings("unchecked")
public final class PageLexer implements BatchExtension {

  /**
   * The order of the tokenizers is significant, as they are processed in this order.
   * 
   * TextTokenizer must be last, it will always consume the characters until the next token arrives.
   */
  private static List tokenizers = Arrays.asList(
  /* HTML Comments */
  new CommentTokenizer("--", "\n", true),
  /* JSP Comments */
  new CommentTokenizer("<%--", "--%>", false),
  /* HTML Directive */
  // new DoctypeTokenizer("<!DOCTYPE", ">"),
      /* XML Directives */
      // new DirectiveTokenizer("<?", "?>"),
      /* JSP Directives */
      // new DirectiveTokenizer("<%@", "%>"),
      /* JSP Expressions */
      // new ExpressionTokenizer("<%", "%>"),
      /* XML and HTML Tags */
      // new ElementTokenizer("<", ">"),
      /* Text (for everything else) */
      new TextTokenizer());

  public List<Node> parse(Reader reader) {

    // CodeReader reads the file stream
    CodeReader codeReader = new CodeReader(reader);

    // ArrayList collects the nodes
    List<Node> nodeList = new ArrayList<Node>();

    // ChannelDispatcher manages the tokenizers
    ChannelDispatcher<List<Node>> channelDispatcher = new ChannelDispatcher<List<Node>>(tokenizers);
    channelDispatcher.consume(codeReader, nodeList);

    // clean up
    codeReader.close();

    return nodeList;
  }
}