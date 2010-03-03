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

package org.sonar.plugins.codereview.codereviewviewer.server;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.sonar.plugins.codereview.codereviewviewer.client.Comments;
import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.Constants;


public abstract class BaseActionHandler {

   protected Comments comments;
   protected String action;

   public BaseActionHandler(Comments comments) {
      this.comments = comments;
      this. action = Constants.ACTION_NONE;
   }

   public BaseActionHandler(Comments comments, String action) {
      this(comments);
      this.action = action;
   }

   protected abstract String process();

   protected List<Comment> getCommentsToProcess() {
      List<Comment> commentList = new ArrayList<Comment>();
      for (Comment comment : comments.getComments()) {
         if (comment.actionMatches(action)) {
            commentList.add(comment);
         }
      }
      return commentList;
   }

}
