package org.sonar.plugin.groovyGMNarc;

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


import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * Groovy Tester.
 *
 * A couple of simple sanity tests
 */
public class GroovyTest extends TestCase {

    Groovy gtest = null;
    public GroovyTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
     //   gtest = new Groovy();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }


    /**
     *
     * Method: getKey()
     *
     */
    public void testGetKey() throws Exception {
        assertEquals(Groovy.KEY,"grvy");
    }

    /**
     *
     * Method: getName()
     *
     */
    public void testGetName() throws Exception {
        assertEquals(Groovy.LANGUAGE_KEY,"GROOVY");
    }

    /**
     *
     * Method: containsValidSuffixes(String path)
     *
     */
    public void testContainsValidSuffixes() throws Exception {
        assertTrue(Groovy.containsValidSuffixes("something/something/Bobo.groovy"));
    }



    public static Test suite() {
        return new TestSuite(GroovyTest.class);
    }
}
