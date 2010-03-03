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
import java.util.Iterator;

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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

//Added by Symcor
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import org.sonar.plugins.codereview.codereviewviewer.client.CodeCommentPopUp;
import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.SourceListener;


public class CodeReviewViewer extends AbstractViewer implements SourceListener {
  public static final String GWT_ID = "org.sonar.plugins.codereview.codereviewviewer.CodeReviewViewer";

  protected String getGwtId() {
    return GWT_ID;
  }

  protected void exportJavascript() {
    exportNativeJavascript(this);
  }

  public static native void exportNativeJavascript(CodeReviewViewer obj) /*-{
    $wnd.load_org_sonar_plugins_codereview_codereviewviewer_CodeReviewViewer = function() {
      obj.@org.sonar.plugins.codereview.codereviewviewer.client.CodeReviewViewer::loadContainer()();
    }
    $wnd.on_resource_loaded_org_sonar_plugins_codereview_codereviewviewer_CodeReviewViewer = function() {
      obj.@org.sonar.plugins.codereview.codereviewviewer.client.CodeReviewViewer::onResourceLoaded()();
    }
  }-*/;

  protected boolean isDefault(WSMetrics.Metric metric, Resource resource) {
    return isMetricInList(metric, WSMetrics.VIOLATIONS_DENSITY, WSMetrics.WEIGHTED_VIOLATIONS, WSMetrics.VIOLATIONS);
  }

  protected boolean isForResource(Resource resource) {
    return resource.getScope().equals(Resource.SCOPE_FILE) &&
        (resource.getQualifier().equals(Resource.QUALIFIER_CLASS) || resource.getQualifier().equals(Resource.QUALIFIER_FILE));
  }


  private Resource resource;
  private final Panel mainPanel      = new VerticalPanel();
  private final LoadingLabel loading = new LoadingLabel();

  // source
  private CodeReviewPanel sourcePanel;
  private FlexTable       commentPanel;

  // header
  private Grid header     = null;
  private Grid headerRow1 = null;
  private Grid headerRow2 = null;
  private Grid headerRow3 = null;

  private ListBox  filterBox = null;
  private CheckBox expandCheckbox = null;
  private CheckBox showViolationCheckBox = null;
  private Label    reviewCaptionLabel = null;
  private Button   toggleStatusButton = null;
  private String   defaultFilter;

  private Button   submitReviewButton = null;

  private TextBox svnURLTextBox;
  private Label   svnRevisionText;
  private Label   sonarRevisionText;
  private Label   fileReviewStatusText;
  private Label   reviewStatusText;

  private Label   svnURLLabel;
  private Label   svnRevisionLabel;
  private Label   sonarRevisionLabel;
  private Label   fileReviewStatusLabel;
  private Label   reviewStatusLabel;


  private boolean resourceHasViolations = false;
  private Comments commentList;
  private SubmitCommentsServiceAsync submitCommentsService;



  protected Widget render(final Resource resource) {

    this.resource = resource;
    commentList = new Comments(resource);

    // Initialize the mainPanel
    mainPanel.clear();
    mainPanel.add(loading);
    mainPanel.setWidth("100%");
    mainPanel.setStyleName("gwt-Violations");

    //initDefaultFilter();
    initCodeReviewPanel();

    // Create the Header
    header = new Grid(3, 1);
    header.setWidth("100%");
    header.setStyleName("gwt-ViewerHeader");
    header.setCellPadding(10);

    /* 
     * header consists of headerRow1, headerRow2 and headerRow3 
     */

    // Create the Header Row 1
    headerRow1 = new Grid(1, 4);
    headerRow1.setWidth("100%");

    // Create the Header Row 2
    headerRow2 = new Grid(1, 4);
    headerRow2.setWidth("100%");

    // Create the Header Row 3
    headerRow3 = new Grid(1, 3);
    headerRow3.setWidth("100%");


    /* 
     * Header Row 1: Create Widgets 
     */
    svnRevisionLabel   = new Label("SVN Revision");
    svnRevisionText    = new Label("NA");
 
    svnURLLabel   = new Label("SVN URL");
    svnURLTextBox = new TextBox();
    svnURLTextBox.setText(Constants.NOT_UNDER_SCM);
    svnURLTextBox.setReadOnly(true);
    svnURLTextBox.setVisibleLength(80);

    headerRow1.setWidget(0, 0, svnRevisionLabel);
    headerRow1.setWidget(0, 1, svnRevisionText);
    headerRow1.setWidget(0, 2, svnURLLabel);
    headerRow1.setWidget(0, 3, svnURLTextBox);

    headerRow1.getColumnFormatter().setWidth(0, "125px");
    headerRow1.getColumnFormatter().setWidth(1, "50px");
    headerRow1.getColumnFormatter().setWidth(2, "125px");


    /* 
     * Header Row 2: Create Widgets 
     */
    sonarRevisionLabel   = new Label("Sonar Revision");
    sonarRevisionText    = new Label("NA");
 
    fileReviewStatusLabel   = new Label("File Reviewed ?");
    fileReviewStatusText    = new Label("No");

    headerRow2.setWidget(0, 0, sonarRevisionLabel);
    headerRow2.setWidget(0, 1, sonarRevisionText);
    headerRow2.setWidget(0, 2, fileReviewStatusLabel);
    headerRow2.setWidget(0, 3, fileReviewStatusText);

    headerRow2.getColumnFormatter().setWidth(0, "125px");
    headerRow2.getColumnFormatter().setWidth(1, "50px");
    headerRow2.getColumnFormatter().setWidth(2, "125px");


    /* 
     * Header Row 3: Create Widgets 
     */
    submitReviewButton = new Button("Submit Review");
    submitReviewButton.addClickListener(new ClickListener() {
        public void onClick(Widget sender) {
           SubmitCommentPopUp submitPopup = new SubmitCommentPopUp(commentList);
           submitPopup.show();
        }
    });

    reviewStatusLabel   = new Label("Status");
    reviewStatusText    = new Label(Constants.FILE_IS_UNVERSIONED);

    headerRow3.setWidget(0, 0, submitReviewButton);
    headerRow3.setWidget(0, 1, reviewStatusLabel);
    headerRow3.setWidget(0, 2, reviewStatusText);

    headerRow3.getColumnFormatter().setWidth(0, "175px");
    headerRow3.getColumnFormatter().setWidth(1, "125px");


    /* 
     * Main Header : Add header Rows
     */

    header.setWidget(0, 0, headerRow1);
    header.setWidget(1, 0, headerRow2);
    header.setWidget(2, 0, headerRow3);


    // header widget will be added to the mainPanel later, after the sources are loaded.

    loadRulePriorities();

    return mainPanel;
  }


  private void initCodeReviewPanel() {
    commentPanel = new FlexTable();
    commentPanel.setStyleName("gwt-SourcePanel");

    sourcePanel = new CodeReviewPanel(resource, defaultFilter, commentPanel, commentList);

    // Listen for commentLoaded event and set text of reviewStatusLabel
    sourcePanel.addListener(this);
  }



  // LOAD PRIORITIES


  private void loadRulePriorities() {
    final ResourcesQuery query = ResourcesQuery.get(resource.getKey())
        .setMetric(WSMetrics.VIOLATIONS)
        .filterOnRulePriorities(true);
    query.execute(new BaseQueryCallback<Resources>(loading) {
      public void onResponse(Resources resources, JavaScriptObject json) {
        setResourceHasViolations(resources);
        loadRules();
      }
    });
  }



  // LOAD RULES

  private void loadRules() {
    final ResourcesQuery query = ResourcesQuery.get(resource.getKey())
        .setMetric(WSMetrics.VIOLATIONS)
        .filterOnRules(true);
    query.execute(new BaseQueryCallback<Resources>(loading) {
      public void onResponse(Resources resources, JavaScriptObject json) {
        setResourceHasViolations(resources);
        displayRules(chooseResourceForRendering(resource, resources));
        loadSources();
      }
    });
  }
  
  private void setResourceHasViolations(Resources resourcesResponse) {
    resourceHasViolations = resourcesResponse != null && resourcesResponse.firstResource() != null && 
      resourcesResponse.firstResource().hasMeasure(WSMetrics.VIOLATIONS);
  }
  
  private Resource chooseResourceForRendering(Resource ctxResource, Resources resourcesResponse) {
    if (resourceHasViolations) {
      return resourcesResponse.firstResource();
    }
    if (ctxResource.getMeasures() == null) {
      ctxResource.setMeasures(new ArrayList<Measure>());
    }
    return ctxResource;
  }

  private void displayRules(Resource resource) {
    Collections.sort(resource.getMeasures(), new Comparator<Measure>() {
      public int compare(Measure m1, Measure m2) {
        return m1.getRuleName().compareTo(m2.getRuleName());
      }
    });
    

    loading.removeFromParent();
    mainPanel.add(header);
  }

  // LOAD SOURCES

  private void loadSources() {
    mainPanel.remove(sourcePanel);

    if (resourceHasViolations || expandCheckbox.isChecked()) {
      mainPanel.add(sourcePanel);
    }
  }

  // Handle event after commentLines have been loaded
 
  public void onReviewDataLoaded(ReviewStatistics reviewStats) {
     svnURLTextBox.setText(commentList.getSvnUrl());
     sonarRevisionText.setText(String.valueOf(commentList.getSvnVersion()));

     updateFileReviewStatusInHeader(reviewStats);

     loadSVNRevision(commentList.getSvnUrl());
  }


  private void loadSVNRevision(String svnURL) {
     if (submitCommentsService == null) {
        submitCommentsService = GWT.create(SubmitCommentsService.class);
        ((ServiceDefTarget)submitCommentsService).setServiceEntryPoint(Constants.SUBMIT_SERVICE_ENTRYPOINT);
     }

     AsyncCallback<String> callback = new AsyncCallback<String>() {
        public void onFailure(Throwable caught) {
           // do something with errors
           Window.alert("Error retrieving the revision number from SVN. " + caught.getMessage());
        }

        public void onSuccess(String revision) {
           updateSVNRevisionInHeader(revision);
        }
     };

     submitCommentsService.getSVNRevision(svnURL, callback);
  }


  private void updateFileReviewStatusInHeader(ReviewStatistics reviewStats) {
    if (reviewStats.isFileReviewed()) {
       fileReviewStatusText.setText(Constants.FILE_IS_REVIEWED);
    }
    else if (reviewStats.getReviewDate() != "") {
       /* this means that a previous version of the file was reviewed */
       fileReviewStatusText.setText(Constants.OLD_FILE_REVIEWED); 
    }
    else {
       fileReviewStatusText.setText(Constants.FILE_IS_NOT_REVIEWED); 
    }
  }

  private void updateSVNRevisionInHeader(String latestSVNRevision) {
     svnRevisionText.setText(latestSVNRevision);

     // Compare the svn and sonar versions of the file to set the status
     long sonarVersion = commentList.getSvnVersion();
     long svnVersion = Long.parseLong(latestSVNRevision);

     if (sonarVersion == svnVersion) {
        reviewStatusText.setText(Constants.FILE_IS_LATEST);
     }
     else if (sonarVersion < svnVersion) {
        reviewStatusText.setText(Constants.FILE_IS_OLD);
        sourcePanel.setDefaultSVNAction(false);
     }

 
  }
}
