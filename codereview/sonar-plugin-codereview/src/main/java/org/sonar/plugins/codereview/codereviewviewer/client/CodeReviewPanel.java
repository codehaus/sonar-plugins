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

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.api.web.gwt.client.Utils;
import org.sonar.api.web.gwt.client.webservices.BaseQueryCallback;
import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.webservices.Resources;
import org.sonar.api.web.gwt.client.webservices.Violation;
import org.sonar.api.web.gwt.client.webservices.Violations;
import org.sonar.api.web.gwt.client.webservices.ViolationsQuery;
import org.sonar.api.web.gwt.client.webservices.ResourcesQuery;
import org.sonar.api.web.gwt.client.webservices.WSMetrics;
import org.sonar.api.web.gwt.client.webservices.Measure;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;

import org.sonar.plugins.codereview.codereviewviewer.client.CRAbstractSourcePanel;
import org.sonar.plugins.codereview.codereviewviewer.client.ReviewComments;
import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.Comments;
import org.sonar.plugins.codereview.codereviewviewer.client.CodeCommentPopUp;

import com.google.gwt.core.client.JavaScriptObject;

public class CodeReviewPanel extends CRAbstractSourcePanel {

  /* The review_info metric is a pipe delimited string. It has 3 paramters of svninfo and 4 parameters from 
     reviewstats plugins
   */
  private static final int NOF_SCM_PARAMETERS = 3;

  private boolean          expand = false;
  private boolean          defaultSVNAction = true;
  private ReviewComments   reviewComments;
  private FlexTable        commentTable;
  private int              commentTableRow = 0;
  private Resource         resource;
  private Comments         commentList;
  private ReviewStatistics reviewStats;
  private Violations       violations;
  private ArrayList        listeners    = new ArrayList();
  private CodeReviewPanel  thisPanel;
  private Map<Integer, List<Violation>> filteredViolationsByLine = new HashMap<Integer, List<Violation>>();


  public CodeReviewPanel(Resource resource, String filter, 
                         FlexTable commentTable, Comments commentList) {
    super(resource);
    this.resource = resource;
    this.commentTable = commentTable;
    this.commentList = commentList;
    this.reviewStats = new ReviewStatistics();
    thisPanel = this;                // Used as a reference to this object which is passed to the CodeCommentPopUp


    // get sources and identify which lines are review comments
    reviewComments = new ReviewComments(resource);
    initializeMetrics();
    loadReviewMeasures();
    loadViolations(filter);
  }

  protected void loadViolations(final String filter) {
    ViolationsQuery.create(getResource().getKey()).execute(new BaseQueryCallback<Violations>() {
      public void onResponse(Violations violations, JavaScriptObject json) {
        CodeReviewPanel.this.violations = violations;
        filter(filter);
        setStarted();
      }
    });
  }

  // Added by Symcor 
  public void initializeMetrics() {
     WSMetrics.Metric SCM_INFO   = new WSMetrics.Metric("review_info");
  }


  // Added by Symcor 
  public void loadReviewMeasures() {

    final ResourcesQuery query = ResourcesQuery.get(resource.getKey()).setMetric(WSMetrics.get("review_info"));
    query.execute(new BaseQueryCallback<Resources>() {
      public void onResponse(Resources resources, JavaScriptObject json) {
         processMeasure(resources);
      }

      public void onError(int errorCode, String errorMessage) {
         Window.alert("Error from loadReviewMeasures : " + errorMessage);
      }
    });
  }


  // Added by Symcor
  public void processMeasure(Resources resources) {
     if (resources.firstResource() != null ) {
        if (resources.firstResource().hasMeasure(WSMetrics.get("review_info"))) {
           List<Measure> measures = resources.firstResource().getMeasures();

           // There can be only one scm_info measure per source file
           for (Measure measure : measures) {
              String[] review_info_params = measure.getData().split("\\|");

              commentList.setSvnUrl(review_info_params[0]);
              commentList.setSvnChecksum(review_info_params[1]);
              commentList.setSvnVersion(Long.parseLong(review_info_params[2]));

              /* if a file does not have any reviewstats, it will have only 3 parameters
                 if it has reviewstats, then it has more than 3 parameters
               */
              if (review_info_params.length > NOF_SCM_PARAMETERS) {
                 reviewStats = new ReviewStatistics(Boolean.parseBoolean(review_info_params[3]),
                                                    Long.parseLong(review_info_params[4]),
                                                    review_info_params[5],
                                                    review_info_params[6]);
              }
           }
           // At this point, the commentList object has information about the SVN url and revision
           // of the "working copy" on which sonar was run.
           // Also, if the file has been reviewed, the reviewStats object is populated accordingly.
           // this data is passed back to the CodeReviewViewer, to update the header of the viewer
           reviewDataLoaded(reviewStats);

        }
        else
        {
           commentList.setSvnUrl(Constants.NOT_UNDER_SCM);
        }
     }
  }
  
  // Added by Symcor
  public boolean isFileReviewed() {
     return reviewComments.isFileReviewed();
  }

  public void setDefaultSVNAction(boolean status) {
     this.defaultSVNAction = status;
  }

  public boolean getDefaultSVNAction() {
     return defaultSVNAction;
  }

  public boolean isExpand() {
    return expand;
  }

  public void setExpand(boolean expand) {
    this.expand = expand;
  }

  public void filter(String filter) {
    filteredViolationsByLine.clear();
    for (Violation violation : violations.getAll()) {
      if (filter == null || filter.equals("") || violation.getRule().getKey().equals(filter) || violation.getPriority().equals(filter)) {
        List<Violation> lineViolations = filteredViolationsByLine.get(violation.getLine());
        if (lineViolations == null) {
          lineViolations = new ArrayList<Violation>();
          filteredViolationsByLine.put(violation.getLine(), lineViolations);
        }
        lineViolations.add(violation);
      }
    }
  }


  private Widget getBlankWidget() {
     //final Image hlink = new Image("/images/blankCommentLine.png");     
     final Image hlink = new Image("/images/blankCommentLine.png");     
     return hlink;
  }

  private Widget getCommentWidget(final Comment comment) {
     final Image hlink = new Image("/images/blankCommentLine.png");
     hlink.addClickListener(new ClickListener() {
        public void onClick(Widget sender)  {
           CodeCommentPopUp reviewCommentPopup = new CodeCommentPopUp(comment, commentList, thisPanel);
           
           reviewCommentPopup.show();
        }
     });

     // Add a mouse listener so that the image will change when the user
     // hovers over the extreme left hand column on screen
     hlink.addMouseListener(new MouseListener() {
        public void onMouseEnter(Widget sender) {
           hlink.setUrl("/images/comment.png");
        }
        public void onMouseLeave(Widget sender) {
           hlink.setUrl("/images/blankCommentLine.png");
        }
        public void onMouseUp(Widget sender, int x, int y) {
           ;
        }
        public void onMouseDown(Widget sender, int x, int y) {
           ;
        }
        public void onMouseMove(Widget sender, int x, int y) {
           ;
        }

     });

     return hlink;
  }






  // changed to add logic to decorate comments
  @Override
  public boolean shouldDecorateLine(int index) {
    // in Code review mode, show the entire source. 
    // do not give option to suppress some sources like in the Violations view
    // as this messes up the commentTable at the side
    return true;

  }

    /* This method is being changed to decorate source lines that are 
     * architecture review comments and also to support the default behaviour
     * of highlighting violations
     */

  @Override
  protected List<Row> decorateLine(int index, String source) {
    List<Row> rows = new ArrayList<Row>();
    List<Violation> lineViolations = filteredViolationsByLine.get(index);
    boolean hasViolations = lineViolations != null && !lineViolations.isEmpty();

    Comment commentContext = new Comment(resource);
    commentContext.setLineNo(index);
    commentContext.addSource(source);

    // The following line paints the background of the source line that has
    // a violation
    // For comment lines, paint the background green

    if (index > 0) {
      String style = (hasViolations ? "red" : 
                                     ((isReviewComment(index)||
                                       isFileComment(index)) ? "green" : "" ));
      Row row = new Row().setLineIndex(index, style).unsetValue().setSource(source, style);
      Widget rowWidget;

      row.setWidgetStyle(style);


      // Comment lines - could be regular comments of SonarReviewComments
      if (isComment(index)) {
         if (isReviewComment(index)) {
            rowWidget = getCommentWidget(reviewComments.getComment(index).setCommentProcessType(Constants.UPDATE_COMMENT));
         }
         else if (isFileComment(index)) {
            rowWidget = getBlankWidget();
         }
         else {
            rowWidget = getBlankWidget();
         }
      }
      // Violation rows
      else if (hasViolations) {
            commentContext.setViolations(lineViolations);
            rowWidget = getCommentWidget(commentContext);
      }
      // Regular source rows
      else {
         rowWidget = getCommentWidget(commentContext);
      }   

      row.setWidget(rowWidget);
      rows.add(row);
    }

    if (hasViolations) {
      for (Violation violation : lineViolations) {
        rows.add(new ViolationRow(violation, getBlankWidget()));
      } 
    }
   
    return rows;
  }




  private boolean hasViolations(int lineIndex) {
    if (lineIndex < 0) {
      return false;
    }
    List<Violation> list = filteredViolationsByLine.get(lineIndex);
    return list != null && !list.isEmpty();
  }



  // Added by Symcor
  // Return true if a line is part of an Architecture review comment
  private boolean isComment(int lineIndex) {
    if (lineIndex < 0) {
      return false;
    }
    
    return reviewComments.isComment(lineIndex);
  }

  private boolean isReviewComment(int lineIndex) {
    if (lineIndex < 0) {
      return false;
    }
    
    return reviewComments.isReviewComment(lineIndex);
  }

  private boolean isFileComment(int lineIndex) {
    if (lineIndex < 0) {
      return false;
    }
    
    return reviewComments.isFileComment(lineIndex);
  }




   // Raise an event when the reviewCommentLines are processed
   public void addListener(SourceListener listener)   {
      listeners.add(listener);
   }

   public void removeListener(SourceListener listener)   {
      listeners.remove(listener);
   }

   public void reviewDataLoaded(ReviewStatistics reviewStatistics)   {
      for(Iterator it = listeners.iterator(); it.hasNext();)   {
          SourceListener listener = (SourceListener) it.next();
          listener.onReviewDataLoaded(reviewStatistics);
      }
   }




  public static class ViolationRow extends Row {

    private Violation violation;

    public ViolationRow(Violation violation, Widget widget) {
      this.violation = violation;
      setWidget(widget);
      setWidgetStyle("msg");
    }

    @Override
    public String getColumn1() {
      return "<div class='ln msg'>&nbsp;</div>";
    }

    @Override
    public String getColumn2() {
      return "";
    }

    @Override
    public String getColumn3() {
      return "<div class='msg " + violation.getPriority() + "'><b>" + Utils.escapeHtml(violation.getRule().getName()) + "</b> : " + Utils.escapeHtml(violation.getMessage()) + "</div";
    }
  }


}
