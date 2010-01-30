/*
 * Copyright (C) 2010 Evgeny Mandrikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.maven.scm.provider.svn.svnjava.command.blame;


import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.AbstractBlameCommand;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.svn.command.SvnCommand;
import org.apache.maven.scm.provider.svn.svnjava.repository.SvnJavaScmProviderRepository;
import org.apache.maven.scm.provider.svn.svnjava.util.SvnJavaUtil;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:olamy@apache.org">Olivier Lamy</a>
 */
public class SvnJavaBlameCommand extends AbstractBlameCommand implements SvnCommand {

  @Override
  public BlameScmResult executeBlameCommand(ScmProviderRepository repo, ScmFileSet workingDirectory, String filename)
      throws ScmException {
    try {
      SvnJavaScmProviderRepository javaRepo = (SvnJavaScmProviderRepository) repo;
      javaRepo.getClientManager();
      AnnotationHandler handler = new AnnotationHandler();
      SvnJavaUtil.blame(javaRepo.getClientManager(), new File(workingDirectory.getBasedir(), filename), handler);
      return new BlameScmResult("", handler.lines);
    }
    catch (SVNException e) {
      throw new ScmException(e.getMessage(), e);
    }
  }

  private static class AnnotationHandler implements ISVNAnnotateHandler {

    private List<BlameLine> lines = new ArrayList<BlameLine>();

    public void handleEOF() {
      // no op
    }

    /**
     * @deprecated
     */
    public void handleLine(Date arg0, long arg1, String arg2, String arg3) throws SVNException {
      // no op
    }

    public void handleLine(Date date, long revision, String author, String line, Date mergedDate,
                           long mergedRevision, String mergedAuthor, String mergedPath, int lineNumber)
        throws SVNException {
      lines.add(new BlameLine(date, Long.toString(revision), author));
    }

    public boolean handleRevision(Date arg0, long arg1, String arg2, File arg3) throws SVNException {
      return true;
    }

  }

}
