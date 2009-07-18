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

package org.sonar.cpp.cppunit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

public class CppUnitTransformer{

	private transient Transformer cppunitXMLTransformer;    
    
    private static final String JUNIT_FILE_POSTFIX = ".xml";
    private static final String JUNIT_FILE_PREFIX = "TEST-";
    public static final String CPPUNIT_TO_JUNIT_XSL = "cppunit-to-junit.xsl";    
	    

    public void transform(String cppunitFileName, InputStream cppunitFileStream, File junitOutputPath) throws IOException, TransformerException,
            SAXException, ParserConfigurationException, InterruptedException, IOException {
        
    	initializeProcessor();        
        File junitTargetFile = new File(junitOutputPath, JUNIT_FILE_PREFIX + cppunitFileName + JUNIT_FILE_POSTFIX);
        FileOutputStream fileOutputStream = new FileOutputStream(junitTargetFile);
        try {
        	cppunitXMLTransformer.transform(new StreamSource(cppunitFileStream), new StreamResult(fileOutputStream));
        } finally {
            fileOutputStream.close();
        }
    }    
    
    private void initializeProcessor() 
    throws TransformerFactoryConfigurationError, TransformerConfigurationException,ParserConfigurationException, 
    	   InterruptedException, IOException{

    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
    	StreamSource streamSourceXSL= new StreamSource(this.getClass().getResourceAsStream(CPPUNIT_TO_JUNIT_XSL));    				
    	cppunitXMLTransformer = transformerFactory.newTransformer(streamSourceXSL);
    }    	
}
