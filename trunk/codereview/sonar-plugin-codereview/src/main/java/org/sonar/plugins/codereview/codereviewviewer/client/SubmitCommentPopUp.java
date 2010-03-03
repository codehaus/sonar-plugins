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
import java.util.Date;

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
import com.google.gwt.user.client.Cookies;
import com.google.gwt.i18n.client.DateTimeFormat;


import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.Comments;
import org.sonar.plugins.codereview.codereviewviewer.client.SaveCommentsPopUp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;


public class SubmitCommentPopUp {

    private SubmitCommentsServiceAsync submitCommentsService;
    private PopupPanel popup;
    private Comments commentList = null;
    private boolean commentsSubmitted = false;
    private boolean markFileAsReviewed = false;

    private Grid commentTable;
    private TextBox  svnUrl;

    private Button submit;
    private Button saveComments;


    
    public SubmitCommentPopUp(Comments commentList) {
        popup = new PopupPanel();
        popup.setHeight("350px");
        popup.setWidth("600px");
        popup.setTitle("Submit Review Comments");
        popup.setStyleName("gwt-CodeCommentPopup");
        this.commentList = commentList;

        setDefaults();

        createCommentWidget();

    }

    public void show() {
        popup.center();
    }

    private void setFileReviewed(boolean status) {
       this.markFileAsReviewed = status;
    }

    private boolean isFileReviewMarked() {
       return markFileAsReviewed;
    }


    private void setDefaults() {
       // Try to get reviewer from cookies
       String reviewer = Cookies.getCookie(Constants.COOKIE_REVIEWER);

       if (reviewer == null) {
          // if not available, try to get from 1st comment object
          if (commentList.getSize() > 0) {
             for (Comment comment : commentList.getComments()) {
                reviewer = comment.getReviewer();
                break;
             }
          }
       }

       commentList.setReviewer(reviewer);
       Date date = new Date();
       DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd-MMM-yyyy");
       commentList.setDate(dateFormat.format(date));
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

        createSharedWidgets();

        // This widget reference is needed to adjust the size of the vertical panel
        Widget commentPanel = createCommentPanel();

        mainPanel.add(createTitle());
        mainPanel.add(createSVNPanel());
        mainPanel.add(createFileReviewPanel());
        mainPanel.add(commentPanel);
        mainPanel.add(createSelectButtons());
        mainPanel.add(createSubmitPanel());

        mainPanel.setCellHeight(commentPanel, "8.5em");

        containerPanel.add(mainPanel);
        popup.add(containerPanel);
    }


    private void createSharedWidgets() {
        submit = new Button("Submit");
        saveComments = new Button("Save Review Comments");
    }


    private Label createTitle() {
        return new Label("Submit Review");
    }


    private Label createEmptyCommentTable() {
        Label commentArea = new Label("No comments to submit");
        return commentArea;
    }

    private Panel createCommentPanel() {
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidth("100%");
        scrollPanel.setHeight("150px");
        scrollPanel.setStyleName("gwt-SubmitCommentListPanel");

        if (commentList.getSize() == 0) {
           scrollPanel.add(createEmptyCommentTable());
        }
        else {
           scrollPanel.add(createCommentTable());
        }
        
        return scrollPanel;
    }

    private Grid createCommentTable() {
        //Add "1" for the header row
        commentTable = new Grid(commentList.getSize()+1, 4);
        commentTable.setWidth("100%");
        commentTable.setStyleName("gwt-SubmitCommentListTable");

        int row = 0;
        
        // Variables are used in inner class hence declared as final
        final Comments temp_CommentList = commentList;
        final Grid finalCommentTable = this.commentTable; 

        // this checkbox is only for asthetics.  Does not function.
        final CheckBox headerCB = new CheckBox();
        headerCB.setChecked(false);
        headerCB.setEnabled(false);

        commentTable.getColumnFormatter().setWidth(0, "20px");
        commentTable.getColumnFormatter().setWidth(1, "40px");
        commentTable.getColumnFormatter().setWidth(2, "210px");
        commentTable.getColumnFormatter().setWidth(3, "300px");

        commentTable.setWidget(row, 0, headerCB);
        commentTable.setText(row, 1, "Line");
        commentTable.setText(row, 2, "Source");
        commentTable.setText(row, 3, "Comment");

        commentTable.getCellFormatter().setStyleName(row, 0, "gwt-SubmitCommentListTableHeader");        
        commentTable.getCellFormatter().setStyleName(row, 1, "gwt-SubmitCommentListTableHeader");
        commentTable.getCellFormatter().setStyleName(row, 2, "gwt-SubmitCommentListTableHeader");
        commentTable.getCellFormatter().setStyleName(row, 3, "gwt-SubmitCommentListTableHeader");

        row++;
        

        for (final Comment comment : commentList.getComments()) {
           CheckBox cb = new CheckBox();

           // use the checkbox name property to store the comment line no - which is a unique key for the comment list
           // in the comments object
           cb.setName(String.valueOf(comment.getLineNo()));

           if (comment.isActive()) {
              cb.setChecked(true);
           }

           final Button final_Submit = submit;

           cb.addClickListener(new ClickListener() {
               public void onClick(Widget sender) {
                   if (((CheckBox) sender).isChecked()) {
                      temp_CommentList.getCommentByLineNo(Integer.valueOf(((CheckBox) sender).getName()).intValue()).activate();
                      if ( ! final_Submit.isEnabled()) {
                         final_Submit.setEnabled(true);
                      }
                   }
                   else {
                      temp_CommentList.getCommentByLineNo(Integer.valueOf(((CheckBox) sender).getName()).intValue()).deactivate();
                      if (temp_CommentList.getActiveComments().size() <= 0) {
                         final_Submit.setEnabled(false);
                      }
                   }
               }              
           });

           commentTable.setWidget(row, 0, cb);
           commentTable.setText(row, 1, String.valueOf(comment.getLineNo()));
           commentTable.setText(row, 2, comment.getSourcesAsString());
           commentTable.setText(row, 3, comment.getCommentsAsString());
           row++;
        }
       
        return commentTable;
    }


    public Grid createSelectButtons() {

       final Grid finalCommentTable = this.commentTable;
       final Comments final_commentList = commentList;
       final Button final_Submit = submit;
 

       Button selectAll = new Button("Select All");
       selectAll.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
             // Start with row=1 as the row 0 is the header and we don't want to toggle that checkbox
             for (int row=1; row < finalCommentTable.getRowCount(); row++) {
                ((CheckBox) finalCommentTable.getWidget(row, 0)).setChecked(true);
             }
             final_commentList.setAllCommentsState(true);
             final_Submit.setEnabled(true);
          }
       });

       Button deSelectAll = new Button("DeSelect All");
       deSelectAll.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
             // Start with row=1 as the row 0 is the header and we don't want to toggle that checkbox
             for (int row=1; row < finalCommentTable.getRowCount(); row++) {
                ((CheckBox) finalCommentTable.getWidget(row, 0)).setChecked(false);
             }
             final_commentList.setAllCommentsState(false);
             final_Submit.setEnabled(false);
          }
       });


       saveComments.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
             SaveCommentsPopUp save = new SaveCommentsPopUp(final_commentList);
             save.show();
          }
       });

       if (commentList.getActiveComments().size() <= 0) {
          saveComments.setEnabled(false);
       }

       Grid subPanel = new Grid(1, 3);
       subPanel.setWidth("100%");
       
       subPanel.getColumnFormatter().setWidth(0, "100px");
       subPanel.getColumnFormatter().setWidth(1, "100px");

       subPanel.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
       subPanel.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
       subPanel.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);

       subPanel.setWidget(0, 0, selectAll);
       subPanel.setWidget(0, 1, deSelectAll);
       subPanel.setWidget(0, 2, saveComments);
       
       return subPanel;
       
    }

    public Grid createSVNPanel() {
       Label svnLabel = new Label("SVN Repository");
       TextBox svnUrl = new TextBox();
       svnUrl.setWidth("450px");
       svnUrl.setText(commentList.getSvnUrl());
       svnUrl.setReadOnly(true);

       Grid subPanel = new Grid(1,2);
       subPanel.setWidth("100%");
       
       subPanel.getColumnFormatter().setWidth(0, "105px");

       subPanel.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
       subPanel.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);

       subPanel.setWidget(0, 0, svnLabel);
       subPanel.setWidget(0, 1, svnUrl);
       
       return subPanel;
    
    }


    public Widget createFileReviewPanel() {
       final Button final_Submit = submit;

       //TODO: Add checkbox listener
       CheckBox cb = new CheckBox();
       cb.setChecked(true);
       setFileReviewed(true);

       cb.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
             if ( ((CheckBox) sender).isChecked() ) {
                setFileReviewed(true);
                submit.setEnabled(true);                
             }
             else {
                setFileReviewed(false);
             }
          }
       });

       cb.setText("Mark this file (version " + 
                  String.valueOf(commentList.getSvnVersion()) +") as reviewed");

       if (! isFileUnderSCM() ) {
          cb.setChecked(false);
          cb.setEnabled(false);
       }

       return cb;
    }


    public Grid createSubmitPanel() {

       Panel subPanel1 = new FlowPanel();

       // Submit button has been created earlier in createSharedWidgets()       
       submit.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
             submitComments();
             popup.hide();
          }
       });

       if (commentList.getActiveComments().size() <= 0) {
          submit.setEnabled(false);
       }

       Button cancel = new Button("Cancel");
       cancel.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
             popup.hide();
          }
       });

       Grid subPanel = new Grid(1,3);
       subPanel.setWidth("100%");
       subPanel.getColumnFormatter().setWidth(1, "100px");
       subPanel.getColumnFormatter().setWidth(2, "100px");

       subPanel.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
       subPanel.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);

       // Cell 0,0 is a blank cell used to fill up space on the row
       subPanel.setWidget(0, 1, cancel);
       subPanel.setWidget(0, 2, submit);

       return subPanel;
    }


    private void submitComments() {

       if ( (!isFileReviewMarked()) && (commentList.getSize() == 0) ) {
          Window.alert("There are no comments to submit");
          return;
       }


       // If file is marked for review, create a new comment and add to the commentList object.
       if ( isFileReviewMarked() ) {
          commentList.setFileAsReviewed(true);
       }

       commentList.setComments(commentList.getSelectedComments());

       if (submitCommentsService == null) {
          submitCommentsService = GWT.create(SubmitCommentsService.class);
          ((ServiceDefTarget)submitCommentsService).setServiceEntryPoint(Constants.SUBMIT_SERVICE_ENTRYPOINT);

       }

       AsyncCallback<String> callback = new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            // do something with errors
            Window.alert("Error from submitComments() " + caught.getMessage());
          }

          public void onSuccess(String result) {
             Window.alert(result);
          }
       };

       submitCommentsService.submitComments(commentList, callback);

       // Deactivate all comments after submission
       commentList.setAllCommentsState(false);

       commentsSubmitted = true;

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






