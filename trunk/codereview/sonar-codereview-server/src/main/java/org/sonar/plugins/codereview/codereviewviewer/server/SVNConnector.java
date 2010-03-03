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

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
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

//import org.sonar.plugins.codereview.codereviewviewer.client.Comments;
//import org.sonar.plugins.codereview.codereviewviewer.client.Comment;

import org.sonar.plugins.codereview.codereviewviewer.client.Constants;


public class SVNConnector  {

    private static final String SVN_REVISION_PROPERTY = "svn:entry:revision";

    private String name;
    private String password;
    private String baseURL;
    private String filePath;
    private String searchPattern;
    private String replacePattern;


    public SVNConnector(String SVNusername, String SVNpassword, String searchPattern, String replacePattern) {
        this.name = SVNusername;
        this.password = SVNpassword;
        this.searchPattern = searchPattern;
        this.replacePattern = replacePattern;

        setupLibrary();
    }



    /*
     * Initializes the library to work with a repository via 
     * different protocols.
     */
    private static void setupLibrary() {
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();
        
        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();
    }


    private void processSVNUrl(String sourceUrl) {

        /*
         *  SVN access is sometimes integrated with active directory or a 3rd party authentication system
         *  In that case, the sonar application, would have to have its own userd id with "GOD" rights on SVN
         *  in order to access any project in SVN
         *  Since active directory users, typically should be real users (and not systems) it is sometimes not possible
         *  to create a "sonar" user for SVN.
         *  A work around to this is to modify the URL which is used to access subversion.  Apache can be configured
         *  to validate the modified URL against apache htaccess rather than against active directory.
         *  for e.g. all connections to http://subversion:10080 are validated against active directory, but 
         *  apache can be configured to validate all urls http://subversion:10080/repo with apache htaccess.
         *  Note: /repo is a random identifier and must not be a valid path under the subversion repository.  
         *  "repo" could very well have been "xyz123".
         */

        // if the sourceURL already consists of the pattern to be replaced, don't do anything
        // for e.g. if search pattern is http://subversion:10080  and replace pattern is http://subversion:10080/repo
        // first check that the sourceUrl is not already of the form http://subversion:10080/repo
        // if it is, do not replace the search pattern with the replace pattern to avoid ending up with 
        // http://subversion:10080/repo/repo

        String rewrittenURL = sourceUrl;

        if (sourceUrl.indexOf(replacePattern) == Constants.NOT_FOUND) {
            rewrittenURL = sourceUrl.replaceFirst(searchPattern, replacePattern);
        }

        System.out.println("processSVNUrl: new SVNurl=" + rewrittenURL);

        String[] paths = rewrittenURL.split("/");

        this.filePath = paths[paths.length - 1];            // filePath = Java Source filename
        this.baseURL  = rewrittenURL.replace(filePath, "");  // The svn Url without the filename
    }


    public String getSvnRevision(String url) throws SVNException {

        processSVNUrl(url);

        System.out.println("getSVNRevision: URL [" + baseURL +"] filePath [" + filePath +"]");

        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(baseURL));
        } catch (SVNException svne) {
            /*
             * Perhaps a malformed URL is the cause of this exception
             */
            System.err.println("getSVNRevision: error while creating an SVNRepository for the location '"
                                + baseURL + "': " + svne.getMessage());
            throw svne;
        }

        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
        repository.setAuthenticationManager(authManager);

        /*
         * This Map will be used to get the file properties. Each Map key is a
         * property name and the value associated with the key is the property
         * value.
         */
        SVNProperties fileProperties = new SVNProperties();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            /*
             * Checks up if the specified path really corresponds to a file. If
             * doesn't the program exits. SVNNodeKind is that one who says what is
             * located at a path in a revision. -1 means the latest revision.
             */
            SVNNodeKind nodeKind = repository.checkPath(filePath, -1);
            
            if (nodeKind == SVNNodeKind.NONE) {
                System.err.println("There is no entry at '" + baseURL + "'.");
                throw new SVNException(SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "There is no entry at '" + baseURL + "'."));
            } else if (nodeKind == SVNNodeKind.DIR) {
                System.err.println("The entry at '" + baseURL + 
                                   "' is a directory while a file was expected.");
                throw new SVNException(SVNErrorMessage.create(SVNErrorCode.UNKNOWN, 
                                       "The entry at '" + baseURL + " is a directory while a file was expected."));
            }
            /*
             * Gets the contents and properties of the file located at filePath
             * in the repository at the latest revision (which is meant by a
             * negative revision number).
             */
            repository.getFile(filePath, -1, fileProperties, baos);

        } catch (SVNException svne) {
            System.err.println("error while fetching the file contents and properties: " + svne.getMessage());
            throw svne;
        }

        String propertyName = SVN_REVISION_PROPERTY;
        String propertyValue = fileProperties.getStringValue(propertyName);

        System.out.println("getSVNRevision: File Revision: " + propertyValue);

        return propertyValue;

    }




    public SourceFile getSourceFromSVN(String url) throws SVNException {

        processSVNUrl(url);
        
        SVNRepository repository = null;
        try {
            /*
             * Creates an instance of SVNRepository to work with the repository.
             * All user's requests to the repository are relative to the
             * repository location used to create this SVNRepository.
             * SVNURL is a wrapper for URL strings that refer to repository locations.
             */
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(baseURL));
        } catch (SVNException svne) {
            /*
             * Perhaps a malformed URL is the cause of this exception
             */
            System.err.println("error while creating an SVNRepository for the location '"
                               + baseURL + "': " + svne.getMessage());
            throw svne;
        }

        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
        repository.setAuthenticationManager(authManager);

        /*
         * This Map will be used to get the file properties. Each Map key is a
         * property name and the value associated with the key is the property
         * value.
         */
        SVNProperties fileProperties = new SVNProperties();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            /*
             * Checks up if the specified path really corresponds to a file. If
             * doesn't the program exits. SVNNodeKind is that one who says what is
             * located at a path in a revision. -1 means the latest revision.
             */
            SVNNodeKind nodeKind = repository.checkPath(filePath, -1);
            
            if (nodeKind == SVNNodeKind.NONE) {
                System.err.println("There is no entry at '" + baseURL + "'.");
                throw new SVNException(SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "There is no entry at '" + url + "'."));
            } else if (nodeKind == SVNNodeKind.DIR) {
                System.err.println("The entry at '" + baseURL + 
                                   "' is a directory while a file was expected.");
                throw new SVNException(SVNErrorMessage.create(SVNErrorCode.UNKNOWN, 
                                       "The entry at '" + baseURL + " is a directory while a file was expected."));
            }
            /*
             * Gets the contents and properties of the file located at filePath
             * in the repository at the latest revision (which is meant by a
             * negative revision number).
             */
            repository.getFile(filePath, -1, fileProperties, baos);

        } catch (SVNException svne) {
            System.err.println("error while fetching the file contents and properties: " + svne.getMessage());
            throw svne; 
        }

        /*
         * Here the SVNProperty class is used to get the value of the
         * svn:mime-type property (if any). SVNProperty is used to facilitate
         * the work with versioned properties.
         */
        String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);

        /*
         * SVNProperty.isTextMimeType(..) method checks up the value of the mime-type
         * file property and says if the file is a text (true) or not (false).
         */
        boolean isTextType = SVNProperty.isTextMimeType(mimeType);

        Iterator iterator = fileProperties.nameSet().iterator();
        /*
         * Displays file properties.
         */
        while (iterator.hasNext()) {
            String propertyName = (String) iterator.next();
            String propertyValue = fileProperties.getStringValue(propertyName);
            System.out.println("File property: " + propertyName + "="
                    + propertyValue);
        }
        /*
         * Displays the file contents in the console if the file is a text.
         */
        byte[] source = baos.toByteArray();

        if (isTextType) {
           
            System.out.println("File contents:");
            System.out.println();
            try {
                baos.writeTo(System.out);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            System.out
                    .println("File contents can not be displayed in the console since the mime-type property says that it's not a kind of a text file.");
        }

        /*
         * Gets the latest revision number of the repository
         */
        long latestRevision = -1;
        try {
            latestRevision = repository.getLatestRevision();
            System.out.println("");
            System.out.println("---------------------------------------------");
            System.out.println("Repository latest revision: " + latestRevision);
        } 
        catch (SVNException svne) {
            System.err.println("error while fetching the latest repository revision: " + svne.getMessage());
        }


        SourceFile sourceFile = new SourceFile(source);
        sourceFile.setChecksum(fileProperties.getStringValue(SVNProperty.CHECKSUM));
        sourceFile.setRepoRevision(Long.valueOf(fileProperties.getStringValue(SVNProperty.REVISION)).longValue());


        return sourceFile;
    }



    //TODO remove System.exit

    public String commitChangedFile(String sourceUrl, String dirPath, 
                                    byte[] source, byte[] modifiedSource)  throws SVNException {

        processSVNUrl(sourceUrl);

        /*
         * URL that points to repository. 
         */
        SVNURL url = SVNURL.parseURIEncoded(baseURL);
        
        /*
         * Sample file contents.
         */
        byte[] oldData = source;
        byte[] newData = modifiedSource;

        /*
         * Create an instance of SVNRepository class. This class is the main entry point 
         * for all "low-level" Subversion operations supported by Subversion protocol. 
         * 
         * These operations includes browsing, update and commit operations. See 
         * SVNRepository methods javadoc for more details.
         */
        SVNRepository repository = SVNRepositoryFactory.create(url);

        /*
         * User's authentication information (name/password) is provided via  an 
         * ISVNAuthenticationManager  instance.  SVNWCUtil  creates  a   default 
         * authentication manager given user's name and password.
         * 
         * Default authentication manager first attempts to use provided user name 
         * and password and then falls back to the credentials stored in the 
         * default Subversion credentials storage that is located in Subversion 
         * configuration area. If you'd like to use provided user name and password 
         * only you may use BasicAuthenticationManager class instead of default 
         * authentication manager:
         * 
         *  authManager = new BasicAuthenticationsManager(userName, userPassword);
         *  
         * You may also skip this point - anonymous access will be used. 
         */
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
        repository.setAuthenticationManager(authManager);

        /*
         * Get type of the node located at URL we used to create SVNRepository.
         * 
         * "" (empty string) is path relative to that URL, 
         * -1 is value that may be used to specify HEAD (latest) revision.
         */
        SVNNodeKind nodeKind = repository.checkPath("", -1);

        /*
         * Checks  up  if the current path really corresponds to a directory. If 
         * it doesn't, the program exits. SVNNodeKind is that one who says  what
         * is located at a path in a revision. 
         */
        if (nodeKind == SVNNodeKind.NONE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "SVNConnector: No entry at URL ''{0}''", url);
            throw new SVNException(err);
        } else if (nodeKind == SVNNodeKind.FILE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "SVNConnector: Entry at URL ''{0}'' is a file while directory was expected", url);
            throw new SVNException(err);
        }
        
        /*
         * Get exact value of the latest (HEAD) revision.
         */
        long latestRevision = repository.getLatestRevision();
        System.out.println("Repository latest revision (before committing): " + latestRevision);
        
        /*
         * Gets an editor for committing the changes to  the  repository.  NOTE:
         * you  must not invoke methods of the SVNRepository until you close the
         * editor with the ISVNEditor.closeEdit() method.
         * 
         * commitMessage will be applied as a log message of the commit.
         * 
         * ISVNWorkspaceMediator instance will be used to store temporary files, 
         * when 'null' is passed, then default system temporary directory will be used to
         * create temporary files.  
         */
        ISVNEditor editor = repository.getCommitEditor("Review comments inserted by Sonar", null);



        /*
         * Always called first. Opens the current root directory. It  means  all
         * modifications will be applied to this directory until  a  next  entry
         * (located inside the root) is opened/added.
         * 
         * -1 - revision is HEAD
         */
        editor.openRoot(-1);
        /*
         * Opens a next subdirectory (in this example program it's the directory
         * added  in  the  last  commit).  Since this moment all changes will be
         * applied to this directory.
         * 
         * dirPath is relative to the root directory.
         * -1 - revision is HEAD
         */
        editor.openDir(dirPath, -1);
        /*
         * Opens the file added in the previous commit.
         * 
         * filePath is also defined as a relative path to the root directory.
         */
        editor.openFile(filePath, -1);
        
        /*
         * The next steps are directed to applying and writing the file delta.
         */
        editor.applyTextDelta(filePath, null);
        
        /*
         * Use delta generator utility class to generate and send delta
         * 
         * Note that you may use only 'target' data to generate delta when there is no 
         * access to the 'base' (previous) version of the file. However, here we've got 'base' 
         * data, what in case of larger files results in smaller network overhead.
         * 
         * SVNDeltaGenerator will call editor.textDeltaChunk(...) method for each generated 
         * "diff window" and then editor.textDeltaEnd(...) in the end of delta transmission.  
         * Number of diff windows depends on the file size. 
         *  
         */
        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(oldData), 0, new ByteArrayInputStream(newData), editor, true);

        /*
         * Closes the file.
         */
        editor.closeFile(filePath, checksum);

        /*
         * Closes the directory.
         */
        editor.closeDir();

        /*
         * Closes the root directory.
         */
        editor.closeDir();

        /*
         * This is the final point in all editor handling. Only now all that new
         * information previously described with the editor's methods is sent to
         * the server for committing. As a result the server sends the new
         * commit information.
         */
        SVNCommitInfo commitInfo =  editor.closeEdit();        
        System.out.println("The file was changed: " + commitInfo);

        return Constants.SUCCESS;

    }





}



