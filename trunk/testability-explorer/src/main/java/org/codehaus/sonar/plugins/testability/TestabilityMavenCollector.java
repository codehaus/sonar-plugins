package org.codehaus.sonar.plugins.testability;

import java.io.File;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.sonar.plugins.testability.model.GlobalTestabilityCost;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.AbstractMavenCollector;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;

public class TestabilityMavenCollector extends AbstractMavenCollector<Java> {

	private static final String XML_VIOLATIONS_FILE = "testability.xml";

	public TestabilityMavenCollector(Java language) {
		super(language);
	}

	@Override
	protected boolean shouldCollectIfNoSources() {
		return false;
	}

	public void collect(MavenPom pom, ProjectContext context) {
		File file = new File( pom.getBuildDir(), XML_VIOLATIONS_FILE );
	    if (!file.exists()) {
	      throw new RuntimeException(XML_VIOLATIONS_FILE + " not found!");
	    }
	    addMeasures(readXmlViolationsFile(file), context);
	}

	private void addMeasures(GlobalTestabilityCost globalTestabilityCost, ProjectContext context) {
		context.addMeasure(TestabilityMetrics.EXCELLENT_CLASSES, Double.valueOf(globalTestabilityCost.getNbExcellentClasses()));
		context.addMeasure(TestabilityMetrics.ACCEPTABLE_CLASSES, Double.valueOf(globalTestabilityCost.getNbAcceptableClasses()));
		context.addMeasure(TestabilityMetrics.NEEDSWORK_CLASSES, Double.valueOf(globalTestabilityCost.getNbNeedWorksClasses()));
		context.addMeasure(TestabilityMetrics.GLOBAL_OVERALL_COST, Double.valueOf(globalTestabilityCost.getOverallCost()));
	}

	private GlobalTestabilityCost readXmlViolationsFile(File file) {
		SMInputFactory inf = new SMInputFactory(XMLInputFactory.newInstance());
		GlobalTestabilityCost globalTestabilityCost;
		try {
			SMHierarchicCursor rootC = inf.rootElementCursor(file);
			rootC.advance();
			globalTestabilityCost = getGlobalTestabilityCost(rootC);
			rootC.getStreamReader().closeCompletely();
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		return globalTestabilityCost;
	}

	private GlobalTestabilityCost getGlobalTestabilityCost(SMHierarchicCursor rootC) throws XMLStreamException {
		int excellent = rootC.getAttrIntValue(rootC.findAttrIndex(null, "excellent"));
		int good = rootC.getAttrIntValue(rootC.findAttrIndex(null, "good"));
		int needWorks = rootC.getAttrIntValue(rootC.findAttrIndex(null, "needsWork"));
		int overall = rootC.getAttrIntValue(rootC.findAttrIndex(null, "overall"));
		return new GlobalTestabilityCost(excellent, good, needWorks, overall);
	}

	public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
		return TestabilityMavenPluginHandler.class;
	}

}
