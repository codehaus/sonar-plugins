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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Ignore;
import org.junit.Test;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.JavaPackage;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.test.IsMeasure;

public class JaCoCoSensorTest {

	@Test
	public void shouldGetReportPathFromProperty() throws URISyntaxException {
		DefaultProjectFileSystem fileSystem = mock(DefaultProjectFileSystem.class);
		when(fileSystem.resolvePath("foo")).thenReturn(getCoverageReport());

		Project project = mock(Project.class);
		when(project.getFileSystem()).thenReturn(fileSystem);
		when(project.getProperty(CoreProperties.JACOCO_REPORT_PATH_PROPERTY))
				.thenReturn("foo");

		File report = new JaCoCoSensor(null, null).getReport(project);
		verify(fileSystem).resolvePath("foo");
		assertNotNull(report);
	}

	@Test
	public void doNotExecuteMavenPluginIfReuseReports() {
		Project project = mock(Project.class);
		when(project.getAnalysisType()).thenReturn(
				Project.AnalysisType.REUSE_REPORTS);
		assertThat(new JaCoCoSensor(null, new JaCoCoMavenPluginHandler())
				.getMavenPluginHandler(project), nullValue());
	}

	@Test
	public void doNotExecuteMavenPluginIfStaticAnalysis() {
		Project project = mock(Project.class);
		when(project.getAnalysisType()).thenReturn(Project.AnalysisType.STATIC);
		assertThat(new JaCoCoSensor(null, new JaCoCoMavenPluginHandler())
				.getMavenPluginHandler(project), nullValue());
	}

	@Test
	public void doNotCollectProjectCoverage() throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);

		verify(context, never()).saveMeasure(eq(CoreMetrics.COVERAGE),
				anyDouble());
	}

	@Test
	public void doNotCollectProjectLineCoverage() throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);

		verify(context, never()).saveMeasure(eq(CoreMetrics.LINE_COVERAGE),
				anyDouble());
		verify(context, never()).saveMeasure(
				argThat(new IsMeasure(CoreMetrics.COVERAGE_LINE_HITS_DATA)));
	}

	@Test
	public void doNotCollectProjectBranchCoverage() throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);

		verify(context, never()).saveMeasure(eq(CoreMetrics.BRANCH_COVERAGE),
				anyDouble());
		verify(context, never()).saveMeasure(
				argThat(new IsMeasure(CoreMetrics.BRANCH_COVERAGE_HITS_DATA)));
	}

	@Test
	public void collectPackageLineCoverage() throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);

		verify(context, never()).saveMeasure(
				(Resource<?>) argThat(is(JavaPackage.class)),
				eq(CoreMetrics.LINE_COVERAGE), anyDouble());
		verify(context, never()).saveMeasure(
				(Resource<?>) argThat(is(JavaPackage.class)),
				eq(CoreMetrics.UNCOVERED_LINES), anyDouble());
	}

	@Test
	public void collectPackageBranchCoverage() throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);

		verify(context, never()).saveMeasure(
				(Resource<?>) argThat(is(JavaPackage.class)),
				eq(CoreMetrics.BRANCH_COVERAGE), anyDouble());
		verify(context, never()).saveMeasure(
				(Resource<?>) argThat(is(JavaPackage.class)),
				eq(CoreMetrics.UNCOVERED_CONDITIONS), anyDouble());
	}

	@Test
	public void packageCoverageIsCalculatedLaterByDecorator()
			throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);

		verify(context, never()).saveMeasure(
				(Resource<?>) argThat(is(JavaPackage.class)),
				eq(CoreMetrics.COVERAGE), anyDouble());
	}

	@Test
	public void collectFileLineCoverage() throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);

		final JavaFile file = new JavaFile("org.jacoco.ant.ReportTask");
		verify(context).saveMeasure(eq(file),
				argThat(new IsMeasure(CoreMetrics.LINE_COVERAGE, 89.2)));
		verify(context).saveMeasure(eq(file),
				argThat(new IsMeasure(CoreMetrics.LINES_TO_COVER, 176.0)));
		verify(context).saveMeasure(eq(file),
				argThat(new IsMeasure(CoreMetrics.UNCOVERED_LINES, 19.0)));
	}

	@Test
	public void collectFileBranchCoverage() throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);

		final JavaFile file = new JavaFile("org.jacoco.ant.ReportTask");
		verify(context).saveMeasure(eq(file),
				argThat(new IsMeasure(CoreMetrics.BRANCH_COVERAGE, 50.0)));
		verify(context).saveMeasure(eq(file),
				argThat(new IsMeasure(CoreMetrics.CONDITIONS_TO_COVER, 2.0)));
		verify(context).saveMeasure(eq(file),
				argThat(new IsMeasure(CoreMetrics.UNCOVERED_CONDITIONS, 1.0)));
	}

	@Ignore("JaCoCo does not take into account interfaces")
	@Test
	public void javaInterfaceHasNoCoverage() throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);

		final JavaFile interfaze = new JavaFile("org.some.interface");
		verify(context, never()).saveMeasure(eq(interfaze),
				argThat(new IsMeasure(CoreMetrics.COVERAGE)));

		verify(context, never()).saveMeasure(eq(interfaze),
				argThat(new IsMeasure(CoreMetrics.LINE_COVERAGE)));
		verify(context, never()).saveMeasure(eq(interfaze),
				argThat(new IsMeasure(CoreMetrics.LINES_TO_COVER)));
		verify(context, never()).saveMeasure(eq(interfaze),
				argThat(new IsMeasure(CoreMetrics.UNCOVERED_LINES)));

		verify(context, never()).saveMeasure(eq(interfaze),
				argThat(new IsMeasure(CoreMetrics.BRANCH_COVERAGE)));
		verify(context, never()).saveMeasure(eq(interfaze),
				argThat(new IsMeasure(CoreMetrics.CONDITIONS_TO_COVER)));
		verify(context, never()).saveMeasure(eq(interfaze),
				argThat(new IsMeasure(CoreMetrics.UNCOVERED_CONDITIONS)));
	}

	@Test
	public void collectFileLineHitsData() throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);
		verify(context)
				.saveMeasure(
						eq(new JavaFile("org.jacoco.ant.MergeTask")),
						argThat(new IsMeasure(
								CoreMetrics.COVERAGE_LINE_HITS_DATA,
								"100=0;101=0;104=1;105=2;106=2;108=2;111=2;113=2;114=2;115=2;117=2;119=2;120=0;121=0;124=1;125=2;127=2;40=2;44=2;53=2;54=2;63=2;64=2;68=2;69=2;72=2;73=2;76=2;78=2;80=2;81=2;82=2;84=2;85=0;88=2;91=2;93=2;94=2;96=2;97=2;99=2")));
	}

	@Test
	public void collectFileBranchHitsData() throws URISyntaxException {
		SensorContext context = mock(SensorContext.class);
		new JaCoCoSensor(null, null).parseReport(getCoverageReport(), context);

		// no conditions
		verify(context, never()).saveMeasure(
				eq(new JavaFile("org.jacoco.ant.AgentTask")),
				argThat(new IsMeasure(CoreMetrics.BRANCH_COVERAGE_HITS_DATA)));

		verify(context).saveMeasure(
				eq(new JavaFile("org.jacoco.ant.MergeTask")),
				argThat(new IsMeasure(CoreMetrics.BRANCH_COVERAGE_HITS_DATA,
						"104=P;124=P")));

		// verify(context).saveMeasure(
		// eq(new JavaFile("org.apache.commons.chain.generic.CopyCommand")),
		// argThat(new IsMeasure(CoreMetrics.BRANCH_COVERAGE_HITS_DATA,
		// "132=0%;136=0%")));
	}

	private File getCoverageReport() throws URISyntaxException {
		return new File(
				getClass()
						.getResource(
								"/org/sonar/plugins/jacoco/JaCoCoSensorTest/jacoco-coverage.xml")
						.toURI());
	}
}
