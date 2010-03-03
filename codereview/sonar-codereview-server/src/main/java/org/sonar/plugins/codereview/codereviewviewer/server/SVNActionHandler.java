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

import org.tmatesoft.svn.core.SVNException;

/*
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
*/

import org.sonar.plugins.codereview.codereviewviewer.client.Comments;
import org.sonar.plugins.codereview.codereviewviewer.client.Comment;
import org.sonar.plugins.codereview.codereviewviewer.client.Constants;
import org.sonar.plugins.codereview.codereviewviewer.server.SVNConnector;


public class SVNActionHandler extends BaseActionHandler {

    private String sourceUrl;
    private SVNConnector svnConnector;



    public SVNActionHandler(Comments comments, SVNConnector svnConnector) {
        super(comments, Constants.ACTION_SVN);
        sourceUrl = comments.getSvnUrl();

        this.svnConnector = svnConnector;
    }


    //TODO: throw exception on failure
    public String process() {

        String status = "";
        // if there are no comments to insert into SVN return a null status string.      
        if (getCommentsToProcess().size() == 0 || comments.getSvnUrl().equals(Constants.NOT_UNDER_SCM)) {
           return status;
        }

        SourceFile source;

        try {
           source = svnConnector.getSourceFromSVN(sourceUrl);

           // Else if there are comments to process
           if (source.getChecksum().equals(comments.getSvnChecksum())) {
               SourceFile modifiedSource = source.insertComments(getCommentsToProcess());

               if (comments.isFileMarkedAsReviewed()) {
                  modifiedSource = modifiedSource.insertFileReviewedComment(comments.getReviewer(),
                                                                            comments.getDate(),
                                                                            comments.getSvnVersion());
               }

               System.out.println("After calling source.insertComments()");

               source.displayFileContents();
               modifiedSource.displayFileContents();

               status = svnConnector.commitChangedFile(sourceUrl, "", source.getByteArray(), 
                                                       modifiedSource.getByteArray());
               status = Constants.SVN_SUCCESS;
           }
           else {
              status = Constants.ERROR_CHECKSUM_FAILED;
           }

           status = status + Constants.GWT_NEWLINE;
        } 
        catch (SVNException svne) {
            System.out.println("ERROR: from commitChangedFile");
            status = Constants.RPC_RETURN_ERROR + svne.getMessage();
        }

        return status;
    }

}



