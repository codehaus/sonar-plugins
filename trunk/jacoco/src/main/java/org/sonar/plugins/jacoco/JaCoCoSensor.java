/**
 * Copyright (c) 2010 Compuware Corp.
 * Sonar Plugin JaCoCo, open source software Sonar plugin.
 * mailto:anthony.dahanne@compuware.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice, this permission notice and the below disclaimer shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. WITHOUT LIMITING THE FOREGOING, COMPUWARE MAKES NO REPRESENTATIONS OR WARRANTIES CONCERNING THE COMPLETENESS, ACCURACY OR OPERATION OF THE SOFTWARE.  CLIENT SHALL HAVE THE SOLE RESPONSIBILITY FOR ADEQUATE PROTECTION AND BACKUP OF ITS DATA USED IN CONNECTION WITH THE SOFTWARE.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.sonar.plugins.jacoco;

import static org.sonar.api.utils.ParsingUtils.scaleValue;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.Plugins;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.XmlParserException;

public class JaCoCoSensor extends AbstractCoverageExtension implements Sensor,
		DependsUponMavenPlugin {
//	private final JaCoCoMavenPluginHandler handler;
	Logger logger = LoggerFactory.getLogger(getClass());

	public JaCoCoSensor(Plugins plugins, JaCoCoMavenPluginHandler handler) {
		super(plugins);
//		this.handler = handler;
	}

	@Override
	public boolean shouldExecuteOnProject(Project project) {
		return super.shouldExecuteOnProject(project)
				&& project.getFileSystem().hasJavaSourceFiles();
	}

	public void analyse(Project project, SensorContext context) {
		File report = getReport(project);
		if (report != null) {
			parseReport(report, context);
		}
	}

	public MavenPluginHandler getMavenPluginHandler(Project project) {
		return null;
	}

	/**
	 * JaCoCo sensor does not support JaCoCo Maven Plugin because ... it does
	 * not exist yet !
	 * 
	 * @param project
	 * @return
	 */
	protected File getReport(Project project) {
		File report = getReportFromProperty(project);

		if ((report == null) || !report.exists() || !report.isFile()) {
			LoggerFactory.getLogger(JaCoCoSensor.class).warn(
					"JaCoCo report not found at {}", report);
			report = null;
		}
		return report;
	}

	private File getReportFromProperty(Project project) {
		String path = (String) project
				.getProperty(JaCoCoCoreProperties.JACOCO_REPORT_PATH_PROPERTY);
		if (path != null) {
			return project.getFileSystem().resolvePath(path);
		}
		return null;
	}

	protected void parseReport(File xmlFile, final SensorContext context) {
		try {
			LoggerFactory.getLogger(JaCoCoSensor.class).info("parsing {}",
					xmlFile);
			StaxParser parser = new StaxParser(
					new StaxParser.XmlStreamHandler() {
						public void stream(SMHierarchicCursor rootCursor)
								throws XMLStreamException {
							try {
								rootCursor.advance();
								collectPackageMeasures(rootCursor
										.descendantElementCursor("package"),
										context);
							} catch (ParseException e) {
								throw new XMLStreamException(e);
							}
						}
					});
			parser.parse(xmlFile);
		} catch (XMLStreamException e) {
			throw new XmlParserException(e);
		}
	}

	private void collectPackageMeasures(SMInputCursor pack,
			SensorContext context) throws ParseException, XMLStreamException {
		while (pack.getNext() != null) {
			String packageNameWithSlashes = pack.getAttrValue("name");
			String packageName = packageNameWithSlashes.replace("/", ".");
			Map<String, FileData> fileDataPerFilename = new HashMap<String, FileData>();
			collectFileMeasures(pack.descendantElementCursor("sourcefile"),
					fileDataPerFilename, packageName);
			for (FileData cci : fileDataPerFilename.values()) {
				for (Measure measure : cci.getMeasures()) {
					context.saveMeasure(cci.getJavaFile(), measure);
				}
			}
		}
	}

	private void collectFileMeasures(SMInputCursor clazz,
			Map<String, FileData> dataPerFilename, String packageName)
			throws ParseException, XMLStreamException {
		while (clazz.getNext() != null) {
			String fileName = packageName + "."
					+ FilenameUtils.removeExtension(clazz.getAttrValue("name"));
			fileName = fileName.replace('/', '.').replace('\\', '.');
			FileData data = dataPerFilename.get(fileName);
			if (data == null) {
				data = new FileData(new JavaFile(fileName));
				dataPerFilename.put(fileName, data);
			}
			collectFileData(clazz, data);
		}
	}

	private void collectFileData(SMInputCursor clazz, FileData data)
			throws ParseException, XMLStreamException {
		SMInputCursor line = clazz.childElementCursor("line");
		while (line.getNext() != null) {
			String lineId = line.getAttrValue("nr");
			// Jacoco does not provide linehits, but status; lets do this : 2
			// for
			// F (full), 1 for P (partial), and 0 for N (not)
			int fakeHit = 0;
			boolean partiallyCovered = false;
			String lineStatus = line.getAttrValue("status");
			if ("F".equals(lineStatus)) {
				fakeHit = 2;
			} else if ("P".equals(lineStatus)) {
				fakeHit = 1;
				partiallyCovered = true;
			} else if ("N".equals(lineStatus)) {
				fakeHit = 0;
			} else {
				throw new ParseException("line status not recognized : "
						+ lineStatus, 0);
			}
			data.addLine(lineId, fakeHit);
			// again, Jacoco does not provide precise conditionLine, so we
			// assume it is 1 condition out of 2 which is covered
			if (partiallyCovered) {
				data.addConditionLine(lineId, 1, 2, "P");
			}
		}
	}

	private class FileData {
		private int lines = 0;
		private int conditions = 0;
		private int coveredLines = 0;
		private int coveredConditions = 0;

		private final JavaFile javaFile;
		private final PropertiesBuilder<String, Integer> lineHitsBuilder = new PropertiesBuilder<String, Integer>(
				CoreMetrics.COVERAGE_LINE_HITS_DATA);
		private final PropertiesBuilder<String, String> branchHitsBuilder = new PropertiesBuilder<String, String>(
				CoreMetrics.BRANCH_COVERAGE_HITS_DATA);

		public void addLine(String lineId, int lineHits) {
			lines++;
			if (lineHits > 0) {
				coveredLines++;
			}
			lineHitsBuilder.add(lineId, lineHits);
		}

		public void addConditionLine(String lineId, int coveredConditions,
				int conditions, String label) {
			this.conditions += conditions;
			this.coveredConditions += coveredConditions;
			branchHitsBuilder.add(lineId, label);
		}

		public FileData(JavaFile javaFile) {
			this.javaFile = javaFile;
		}

		public List<Measure> getMeasures() {
			List<Measure> measures = new ArrayList<Measure>();
			if (lines > 0) {
				measures.add(new Measure(CoreMetrics.COVERAGE,
						calculateCoverage(coveredLines + coveredConditions,
								lines + conditions)));

				measures.add(new Measure(CoreMetrics.LINE_COVERAGE,
						calculateCoverage(coveredLines, lines)));
				measures.add(new Measure(CoreMetrics.LINES_TO_COVER,
						(double) lines));
				measures.add(new Measure(CoreMetrics.UNCOVERED_LINES,
						(double) lines - coveredLines));
				measures.add(lineHitsBuilder.build().setPersistenceMode(
						PersistenceMode.DATABASE));

				if (conditions > 0) {
					measures.add(new Measure(CoreMetrics.BRANCH_COVERAGE,
							calculateCoverage(coveredConditions, conditions)));
					measures.add(new Measure(CoreMetrics.CONDITIONS_TO_COVER,
							(double) conditions));
					measures.add(new Measure(CoreMetrics.UNCOVERED_CONDITIONS,
							(double) conditions - coveredConditions));
					measures.add(branchHitsBuilder.build().setPersistenceMode(
							PersistenceMode.DATABASE));
				}
			}
			return measures;
		}

		public JavaFile getJavaFile() {
			return javaFile;
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	private double calculateCoverage(int coveredElements, int elements) {
		if (elements > 0) {
			return scaleValue(100.0 * ((double) coveredElements / (double) elements));
		}
		return 0.0;
	}
}
