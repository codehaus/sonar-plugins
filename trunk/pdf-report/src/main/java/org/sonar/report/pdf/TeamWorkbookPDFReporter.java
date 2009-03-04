package org.sonar.report.pdf;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.sonar.report.pdf.entity.Project;

import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class TeamWorkbookPDFReporter extends PDFReporter {

  private URL logo;
  private String projectKey;
  private String sonarUrl;

  public TeamWorkbookPDFReporter(URL logo, String projectKey, String sonarUrl) {
    this.logo = logo;
    this.projectKey = projectKey;
    this.sonarUrl = sonarUrl;
  }

  @Override
  protected URL getLogo() {
    return this.logo;
  }

  @Override
  protected String getProjectKey() {
    return this.projectKey;
  }

  @Override
  protected String getSonarUrl() {
    return this.sonarUrl;
  }

  @Override
  protected void printFrontPage(Document frontPageDocument, PdfWriter frontPageWriter)
      throws org.dom4j.DocumentException {
    try {
      URL largeLogo;
      if (super.getConfigProperty("front.page.logo").startsWith("http://")) {
        largeLogo = new URL(super.getConfigProperty("front.page.logo"));
      } else {
        largeLogo = this.getClass().getClassLoader().getResource(super.getConfigProperty("front.page.logo"));
      }
      Image logoImage = Image.getInstance(largeLogo);
      Rectangle pageSize = frontPageDocument.getPageSize();
      float positionX = pageSize.getWidth() / 2f - logoImage.getWidth() / 2f;
      logoImage.setAbsolutePosition(positionX, pageSize.getHeight() - logoImage.getHeight() - 100);
      frontPageDocument.add(logoImage);

      PdfPTable title = new PdfPTable(1);
      title.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      title.getDefaultCell().setBorder(Rectangle.NO_BORDER);

      String projectRow = super.getTextProperty("general.project") + ": " + super.getProject().getName();
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      String dateRow = df.format(super.getProject().getMeasures().getDate());
      String descriptionRow = super.getProject().getDescription();

      title.addCell(new Phrase(projectRow, FontStyle.frontPageFont1));
      title.addCell(new Phrase(descriptionRow, FontStyle.frontPageFont2));
      title.addCell(new Phrase(dateRow, FontStyle.frontPageFont3));
      title.setTotalWidth(pageSize.getWidth() - frontPageDocument.leftMargin() - frontPageDocument.rightMargin());
      title.writeSelectedRows(0, -1, frontPageDocument.leftMargin(),
          pageSize.getHeight() - logoImage.getHeight() - 150, frontPageWriter.getDirectContent());

    } catch (IOException e) {
      e.printStackTrace();
    } catch (BadElementException e) {
      e.printStackTrace();
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void printPdfBody(Document document) throws DocumentException, IOException, org.dom4j.DocumentException {
    // Chapter 1: Report Overview
    ChapterAutoNumber chapter1 = new ChapterAutoNumber(new Paragraph(getTextProperty("general.report_overview"),
        FontStyle.chapterFont));
    Project project = super.getProject();
    chapter1.add(new Paragraph(getTextProperty("main.text.misc.overview"), FontStyle.normalFont));
    printDashboard(project, chapter1);
    document.add(chapter1);
  }

  // TODO: tendency info
  private void printDashboard(Project project, Section section) throws DocumentException {

    // Dashboard special fonts
    Font titleFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.BLACK);
    Font dataFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, Color.GRAY);
    Font dataFont2 = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, new Color(100, 150, 190));

    // Static Analysis
    Paragraph staticAnalysis = new Paragraph(getTextProperty("general.static_analysis"), FontStyle.underlinedFont);
    PdfPTable staticAnalysisTable = new PdfPTable(3);
    staticAnalysisTable.getDefaultCell().setBorderColor(Color.WHITE);

    PdfPTable linesOfCode = new PdfPTable(1);
    linesOfCode.getDefaultCell().setBorderColor(Color.WHITE);
    linesOfCode.addCell(new Phrase(getTextProperty("general.lines_of_code"), titleFont));
    PdfPTable withTendency = new PdfPTable(2);
    withTendency.addCell(new Phrase(project.getMeasure("ncss").getFormatValue(), dataFont));
    withTendency.addCell(getTendencyImage(project.getMeasure("ncss").getQualitativeTendency(), project.getMeasure("ncss").getQuantitativeTendency()));
    
    linesOfCode.addCell(withTendency);
    linesOfCode.addCell(new Phrase(project.getMeasure("packages_count").getFormatValue() + " packages", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure("classes_count").getFormatValue() + " classes", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure("functions_count").getFormatValue() + " methods", dataFont2));
    linesOfCode.addCell(new Phrase(project.getMeasure("duplicated_lines_ratio").getFormatValue() + " duplicated lines", dataFont2));

    PdfPTable comments = new PdfPTable(1);
    comments.getDefaultCell().setBorderColor(Color.WHITE);
    comments.addCell(new Phrase(getTextProperty("general.comments"), titleFont));
    comments.addCell(new Phrase(project.getMeasure("comment_ratio").getFormatValue(), dataFont));
    comments.addCell(new Phrase(project.getMeasure("comment_lines").getFormatValue() + " comment lines", dataFont2));

    PdfPTable complexity = new PdfPTable(1);
    complexity.getDefaultCell().setBorderColor(Color.WHITE);
    complexity.addCell(new Phrase(getTextProperty("general.complexity"), titleFont));
    complexity.addCell(new Phrase(project.getMeasure("ccn_function").getFormatValue(), dataFont));
    complexity.addCell(new Phrase(project.getMeasure("ccn_class").getFormatValue() + " /class", dataFont2));
    complexity.addCell(new Phrase(project.getMeasure("ccn").getFormatValue() + " decision points", dataFont2));

    staticAnalysisTable.setSpacingBefore(10);
    staticAnalysisTable.addCell(linesOfCode);
    staticAnalysisTable.addCell(comments);
    staticAnalysisTable.addCell(complexity);
    staticAnalysisTable.setSpacingAfter(20);

    // Dynamic Analysis
    Paragraph dynamicAnalysis = new Paragraph(getTextProperty("general.dynamic_analysis"), FontStyle.underlinedFont);
    PdfPTable dynamicAnalysisTable = new PdfPTable(3);
    dynamicAnalysisTable.getDefaultCell().setBorderColor(Color.WHITE);

    PdfPTable codeCoverage = new PdfPTable(1);
    codeCoverage.getDefaultCell().setBorderColor(Color.WHITE);
    codeCoverage.addCell(new Phrase(getTextProperty("general.code_coverage"), titleFont));
    codeCoverage.addCell(new Phrase(project.getMeasure("code_coverage").getFormatValue() + " coverage", dataFont));
    codeCoverage.addCell(new Phrase(project.getMeasure("test_count").getFormatValue() + " tests", dataFont2));

    PdfPTable testSuccess = new PdfPTable(1);
    testSuccess.getDefaultCell().setBorderColor(Color.WHITE);
    testSuccess.addCell(new Phrase(getTextProperty("general.test_success"), titleFont));
    testSuccess.addCell(new Phrase(project.getMeasure("test_success_percentage").getFormatValue(), dataFont));
    testSuccess.addCell(new Phrase(project.getMeasure("test_failures_count").getFormatValue() + " failures", dataFont2));
    testSuccess.addCell(new Phrase(project.getMeasure("test_errors_count").getFormatValue() + " errors", dataFont2));

    dynamicAnalysisTable.setSpacingBefore(10);
    dynamicAnalysisTable.addCell(codeCoverage);
    dynamicAnalysisTable.addCell(testSuccess);
    dynamicAnalysisTable.addCell("");
    dynamicAnalysisTable.setSpacingAfter(20);

    Paragraph codingRulesViolations = new Paragraph(getTextProperty("general.coding_rules_violations"),
        FontStyle.underlinedFont);
    PdfPTable codingRulesViolationsTable = new PdfPTable(3);
    codingRulesViolationsTable.getDefaultCell().setBorderColor(Color.WHITE);

    PdfPTable rulesCompliance = new PdfPTable(1);
    rulesCompliance.getDefaultCell().setBorderColor(Color.WHITE);
    rulesCompliance.addCell(new Phrase(getTextProperty("general.rules_compliance"), titleFont));
    rulesCompliance.addCell(new Phrase(project.getMeasure("rules_compliance").getFormatValue(), dataFont));

    PdfPTable violations = new PdfPTable(1);
    violations.getDefaultCell().setBorderColor(Color.WHITE);
    violations.addCell(new Phrase(getTextProperty("general.violations"), titleFont));
    violations.addCell(new Phrase(project.getMeasure("rules_violations").getFormatValue(), dataFont));

    codingRulesViolationsTable.setSpacingBefore(10);
    codingRulesViolationsTable.addCell(rulesCompliance);
    codingRulesViolationsTable.addCell(violations);
    codingRulesViolationsTable.addCell("");
    codingRulesViolationsTable.setSpacingAfter(20);

    section.add(Chunk.NEWLINE);
    section.add(staticAnalysis);
    section.add(staticAnalysisTable);
    section.add(dynamicAnalysis);
    section.add(dynamicAnalysisTable);
    section.add(codingRulesViolations);
    section.add(codingRulesViolationsTable);
    try {
      section.add(Image.getInstance("http://nemo.sonar.codehaus.org/images/tendency/2-black.png"));
      
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  protected void printTocTitle(Toc tocDocument) throws com.lowagie.text.DocumentException {
    Paragraph tocTitle = new Paragraph(super.getTextProperty("main.table.of.contents"), FontStyle.tocTitleFont);
    tocTitle.setAlignment(Element.ALIGN_CENTER);
    tocDocument.getTocDocument().add(tocTitle);
    tocDocument.getTocDocument().add(Chunk.NEWLINE);
  }

}
