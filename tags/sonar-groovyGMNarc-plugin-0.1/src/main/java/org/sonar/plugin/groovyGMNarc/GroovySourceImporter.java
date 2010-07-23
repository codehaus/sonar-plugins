/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 Scott K.
 * mailto: skuph_marx@yahoo.com
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
 *
 */

package org.sonar.plugin.groovyGMNarc;

import java.io.File;
import java.util.List;

import org.sonar.api.batch.AbstractSourceImporter;
import org.sonar.api.batch.Phase;

import org.sonar.api.resources.Language;
import org.sonar.api.resources.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: skessler
 * Date: Feb 1, 2010
 * Time: 11:25:36 AM
 * To change this template use File | Settings | File Templates.
 */

@Phase(name = Phase.Name.PRE)
public class GroovySourceImporter extends AbstractSourceImporter {


        public GroovySourceImporter() {
            super(Groovy.INSTANCE);
        }

        protected Resource createResource(File file, List<File> sourceDirs, boolean unitTest) {
            return GroovyFile.fromIOFile(file, sourceDirs, unitTest);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }

}
