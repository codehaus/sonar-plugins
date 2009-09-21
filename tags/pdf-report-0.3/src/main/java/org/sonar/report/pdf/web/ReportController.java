/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 GMV-SGI
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
package org.sonar.report.pdf.web;

import com.lowagie.text.DocumentException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URL;
import java.util.Properties;
import org.sonar.report.pdf.PDFReporter;
import org.sonar.report.pdf.DefaultPDFReporter;
import org.sonar.report.pdf.TeamWorkbookPDFReporter;
import org.sonar.report.pdf.entity.exception.ReportException;

public class ReportController extends HttpServlet {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 7345885422328007022L;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doPDFReport(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doPDFReport(req, resp);
  }

  private void doPDFReport(HttpServletRequest req, HttpServletResponse resp) {
    String projectKey = req.getParameter("project");
    resp.setContentType("application/pdf;charset=UTF-8");
    resp.setHeader("Expires", "0");
    resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    resp.setHeader("Pragma", "public");

    Properties config = new Properties();
    Properties configText = new Properties();
    URL resource = this.getClass().getClassLoader().getResource("report.properties");
    URL resourceText = this.getClass().getClassLoader().getResource("report-texts-en.properties");
    
    try {
      config.load(resource.openStream());
      configText.load(resourceText.openStream());
    } catch (IOException ex) {
      Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, "Can not read report config file", ex);
    }

    try {

      String sonarUrl = null;
      if (!config.getProperty("sonar.base.url").equals("")) {
        sonarUrl = config.getProperty("sonar.base.url");
      } else {
        // TODO: retrieve local server base URL
      }

      // TODO: set the name of the return file
      PDFReporter reporter = new DefaultPDFReporter(new URL(sonarUrl + "/images/sonar-120.png"), projectKey, sonarUrl, config, configText);
      ByteArrayOutputStream baos = reporter.getReport();
      resp.setContentLength(baos.size());
      ServletOutputStream out = resp.getOutputStream();
      baos.writeTo(out);
      out.flush();
    } catch (MalformedURLException ex) {
      Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, "Sonar base URL is not correct", ex);
    } catch (DocumentException ex) {
      Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, "Error generating pdf report", ex);
    } catch (IOException ex) {
      Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (org.dom4j.DocumentException ex) {
      Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, "Error parsing response from Sonar", ex);
    } catch (ReportException ex) {
      Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, "Internal error", ex);
      ex.printStackTrace();
    }
  }

}
