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
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.Cookies;

import com.google.gwt.user.client.Window;

import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.CRUtils;
import org.sonar.plugins.codereview.codereviewviewer.client.CodeReviewPanel;


public class CodeCommentPopUp {

    private PopupPanel popup;
    private Comment    comment = null;
    private Comments   commentList;
    private boolean    insertingNewComment = true;
    private CodeReviewPanel codeReviewPanel;

    private TextArea commentArea;
    private TextBox  reviewerTextBox;
    private TextBox  assigneeTextBox;
    private TextBox  actionTextBox;
    private CheckBox svnActionCheckBox;
    private CheckBox jiraActionCheckBox;

    
    public CodeCommentPopUp(Comment comment, Comments commentList, CodeReviewPanel parent) {
        popup = new PopupPanel();
        popup.setHeight("300px");
        popup.setWidth("550px");
        popup.setTitle("Add Review Comments");
        popup.setStyleName("gwt-CodeCommentPopup");

        this.comment = comment;
        this.commentList = commentList;
        this.codeReviewPanel = parent;

        // if the user has entered a comment on a line and then clicked the same line again to edit the comment
        // the comment object exist in the commentList object, but does not exist in reviewComments.getComment(index)
        // reviewComments... holds only comments that exists already in the source file
        // we are checking the commentList object and will return the comment for the line no if it exists so 
        // that it can be updated.

        Comment commentToUpdate;
        if ((commentToUpdate = commentList.getCommentByLineNo(comment.getLineNo())) != null) {
           copyCommentContents(commentToUpdate, this.comment);

           insertingNewComment = false;                       /* comment is being updated */

        }

        createCommentWidget();

    }


    public void show() {
        popup.center();
    }


    private void copyCommentContents(Comment sourceComment, Comment destComment) {
        if (sourceComment != null && destComment != null) {
           destComment.setComment(sourceComment.getCommentsAsString());
           destComment.setReviewer(sourceComment.getReviewer());
           destComment.setAssignee(sourceComment.getAssignee());
           destComment.setAction(sourceComment.getActionsAsCSV());
        }
    }


    private void createCommentWidget() {
        VerticalPanel containerPanel = new VerticalPanel();
        containerPanel.setWidth("100%");
        containerPanel.setHeight("100%");
        containerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        containerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setSpacing(5);
        mainPanel.setWidth("90%");
        mainPanel.setHeight("90%");

        Grid subPanel = new Grid(3,4);
        subPanel.setWidth("100%");
        subPanel.setCellSpacing(5);

        subPanel.getColumnFormatter().setWidth(0, "110px");
        subPanel.getColumnFormatter().setWidth(1, "160px");
        subPanel.getColumnFormatter().setWidth(2, "125px");
        subPanel.getColumnFormatter().setWidth(3, "100px");

        subPanel.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);
        subPanel.getCellFormatter().setHorizontalAlignment(2, 2, HasHorizontalAlignment.ALIGN_RIGHT);
        subPanel.getCellFormatter().setHorizontalAlignment(2, 3, HasHorizontalAlignment.ALIGN_RIGHT);

        subPanel.setWidget(0, 0, createActionListLabel());
        subPanel.setWidget(0, 1, createActionTextBox());
        subPanel.setWidget(0, 2, createSVNActionCheckBox());
        subPanel.setWidget(0, 3, createJIRAActionCheckBox());
        subPanel.setWidget(1, 0, createReviewerLabel());
        subPanel.setWidget(1, 1, createReviewerTextBox());
        subPanel.setWidget(2, 0, createAssignedToLabel());
        subPanel.setWidget(2, 1, createAssigneeTextBox());
        subPanel.setWidget(2, 2, createCancelButton());
        subPanel.setWidget(2, 3, createOKButton());


        mainPanel.add(createTitle());
        mainPanel.add(createCommentTextArea());
        mainPanel.add(subPanel);

        containerPanel.add(mainPanel);
        popup.add(containerPanel);
    }


    private Label createTitle() {
        return new Label("Enter Comments");
    }


    private TextArea createCommentTextArea() {
        commentArea = new TextArea();
        commentArea.setWidth("100%");
        commentArea.setHeight("150px");
        if (comment != null ) { 
           // if the source line is a architecture review comment, it will not
           // have source and violations.
           if (comment.hasComments())  {
              commentArea.setText(comment.getCommentsAsString());
           }
           else  {
              String text = comment.getSourcesAsString();
              if (comment.hasViolations()) {
                 text = text + comment.getViolationsAsString();
              }
              commentArea.setText(text);
           }
        }
        return commentArea;
    }


    private TextBox createReviewerTextBox() {
        reviewerTextBox = new TextBox();
        if (comment != null && comment.hasComments())  {
              reviewerTextBox.setText(comment.getReviewer());
        }
        else {         
           String reviewer = Cookies.getCookie(Constants.COOKIE_REVIEWER);

           if (reviewer != null) {
              reviewerTextBox.setText(reviewer);
           }
        }

        return reviewerTextBox;
    }


    private TextBox createAssigneeTextBox() {
        assigneeTextBox = new TextBox();
        if (comment != null && comment.hasComments())  {
           assigneeTextBox.setText(comment.getAssignee());
        }
        else {
           String assignee = Cookies.getCookie(Constants.COOKIE_ASSIGNEE);

           if (assignee != null) {
              assigneeTextBox.setText(assignee);
           }
        }
        return assigneeTextBox;
    }


    private Button createOKButton() {
        return new Button("Submit", new ClickListener() {
            public void onClick(Widget sender) {
                if (validate()) {
                   saveCookies();
                   addCommentToList();
                   popup.hide();
                }
            }
        });
    }


    private void saveCookies() {
        Cookies.setCookie(Constants.COOKIE_REVIEWER, reviewerTextBox.getText());
        Cookies.setCookie(Constants.COOKIE_ASSIGNEE, assigneeTextBox.getText());
        Cookies.setCookie(Constants.COOKIE_ACTIONLIST, actionTextBox.getText());
    }


    private Button createCancelButton() {
        return new Button("Cancel", new ClickListener() {
            public void onClick(Widget sender) {
                popup.hide();
            }
        }); 
    }


    private Label createReviewerLabel() {
        return new Label("Reviewer");
    }


    private Label createAssignedToLabel() {
        Label label = new Label("Assigned to");
        return label;
    }

    
    private Label createActionListLabel() {
        return new Label("Action Expected");
    }



    private TextBox createActionTextBox() {
        actionTextBox = new TextBox();

        if (comment != null && comment.hasComments())  {
           actionTextBox.setText(comment.getActionsAsCSV());
        }
        else {
           String actionList = Cookies.getCookie(Constants.COOKIE_ACTIONLIST);

           if (actionList != null) {
              actionTextBox.setText(actionList);
           }
           else {
             actionTextBox.setText("Fix");
           }
        }
 
	return actionTextBox;
    }


    private CheckBox createSVNActionCheckBox() {
        svnActionCheckBox = new CheckBox();
        svnActionCheckBox.setText("Update SVN");
        if (isFileUnderSCM() && codeReviewPanel.getDefaultSVNAction()) {
           svnActionCheckBox.setChecked(true);
        }
        else {
           svnActionCheckBox.setChecked(false);
           svnActionCheckBox.setEnabled(false);
        }
        return svnActionCheckBox;
    }


    private CheckBox createJIRAActionCheckBox() {
        jiraActionCheckBox = new CheckBox();
        jiraActionCheckBox.setText("Raise JIRA");
        jiraActionCheckBox.setChecked(false);
        // this functionality will be built in the next release
        jiraActionCheckBox.setEnabled(false);
        return jiraActionCheckBox;
    }


    private boolean validate() {
       StringBuilder validationErrors = new StringBuilder();

       // Validate data entered and show alert to user if data is invalid
       
       if (reviewerTextBox.getText() != null && reviewerTextBox.getText().trim() != "") {
          if (! CRUtils.isEmail(reviewerTextBox.getText())) {
             validationErrors.append(Constants.GWT_NEWLINE);
             validationErrors.append("Reviewer must be a valid email id.");
          }
       }
       else {
          validationErrors.append(Constants.GWT_NEWLINE);
          validationErrors.append("Reviewer email cannot be blank.");
       }

       if (assigneeTextBox.getText() != null && assigneeTextBox.getText().trim() != "") {
          if (! CRUtils.isEmail(assigneeTextBox.getText())) {
             validationErrors.append(Constants.GWT_NEWLINE);
             validationErrors.append("Assignee must be a valid email id.");
          }
       }
       else {
          validationErrors.append(Constants.GWT_NEWLINE);
          validationErrors.append("Assignee email cannot be blank.");
       }


       if (commentArea.getText().trim().length() == 0) {
          validationErrors.append(Constants.GWT_NEWLINE);
          validationErrors.append("Comment cannot be blank."); 
       }

       if (validationErrors.length() > 0) {
          Window.alert("Correct the following errors to proceed: " + validationErrors.toString());
          return false;
       }
       
       return true;       
    }


    private void addCommentToList() {

       this.comment.setComment(commentArea.getText());
       this.comment.setReviewer(reviewerTextBox.getText());
       this.comment.setAssignee(assigneeTextBox.getText());
       this.comment.setAction(actionTextBox.getText());

       // If SVN box is checked and the comment object does not already have the SVN action handler 
       if (svnActionCheckBox.isChecked() && 
           (! this.comment.actionMatches(Constants.ACTION_SVN))) {
          this.comment.addActionHandler(Constants.ACTION_SVN);
       }

       // If SVN box is checked and the comment object does not already have the JIRA action handler 
       if (jiraActionCheckBox.isChecked() && 
           (! this.comment.actionMatches(Constants.ACTION_JIRA))) {
          this.comment.addActionHandler(Constants.ACTION_JIRA);
       }

       if (insertingNewComment) {
          commentList.addComment(this.comment);
       }

    }

    private boolean isFileUnderSCM() {
        if (((commentList.getSvnUrl() != null) && 
             (commentList.getSvnUrl().trim() != ""))   &&
            (! commentList.getSvnUrl().equals(Constants.NOT_UNDER_SCM))) {
           return true;
        }
        // Else
        return false;
    }


}






