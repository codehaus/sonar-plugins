/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.codereview.codereviewviewer.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sonar.api.web.gwt.client.AbstractViewer;
import org.sonar.api.web.gwt.client.Utils;
import org.sonar.api.web.gwt.client.webservices.BaseQueryCallback;
import org.sonar.api.web.gwt.client.webservices.Measure;
import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.webservices.Resources;
import org.sonar.api.web.gwt.client.webservices.ResourcesQuery;
import org.sonar.api.web.gwt.client.webservices.WSMetrics;
import org.sonar.api.web.gwt.client.widgets.LoadingLabel;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gen2.table.override.client.Grid;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.Window;

import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.Comments;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;


public class SaveCommentsPopUp {

    private PopupPanel popup;
    private Comments   commentList = null;

    private TextArea   commentArea;
    private Button     close;


    
    public SaveCommentsPopUp(Comments commentList) {
        popup = new PopupPanel();
        popup.setHeight("450px");
        popup.setWidth("650px");
        popup.setTitle("Save Review Comments");
        popup.setStyleName("gwt-CodeCommentPopup");
        this.commentList = commentList;
        createCommentWidget();

    }

    public void show() {
        popup.center();
    }

    private void createCommentWidget() {
        VerticalPanel containerPanel = new VerticalPanel();
        containerPanel.setWidth("100%");
        containerPanel.setHeight("100%");
        containerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        containerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setSpacing(5);
        mainPanel.setWidth("95%");
        mainPanel.setHeight("95%");

        Widget heading = createHeading();
        Widget closeButton = createCloseButton();

        mainPanel.add(heading);
        mainPanel.add(createTextArea());
        mainPanel.add(createCloseButton());

        mainPanel.setCellHeight(heading, "25px");
        mainPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);

        containerPanel.add(mainPanel);
        popup.add(containerPanel);

    }


    private Widget createHeading() {
        Label heading = new Label("Save Comments:" + Constants.GWT_NEWLINE +
                                  "    Use CTRL-A to select text and CTRL-C to copy comments to clipboard");
        return heading;
    }


    private Widget createTextArea() {
       commentArea = new TextArea();
       commentArea.setCharacterWidth(80);
       commentArea.setVisibleLines(18);
       populateTextArea();
       commentArea.setReadOnly(true);
    
       return commentArea;
    }


    private Widget createCloseButton() {
       Button close = new Button("Close");
       close.setWidth("100px");
       close.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
             popup.hide();
          }
       });

       return close;
    }

    private void populateTextArea() {
       StringBuilder sb = new StringBuilder();

       for (Comment comment: commentList.getComments()) {
          sb.append(comment.formatCommentForText());
          sb.append(Constants.GWT_NEWLINE);
          sb.append(Constants.GWT_NEWLINE);
       }

       commentArea.setText(sb.toString());
    }

}






