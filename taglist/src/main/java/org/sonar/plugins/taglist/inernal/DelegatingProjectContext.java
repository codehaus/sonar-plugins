package org.sonar.plugins.taglist.inernal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sonar.commons.Metric;
import org.sonar.commons.resources.Measure;
import org.sonar.commons.resources.ProjectLink;
import org.sonar.commons.resources.Resource;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RuleFailureLevel;
import org.sonar.commons.rules.RuleFailureParam;
import org.sonar.plugins.api.maven.ProjectContext;

public class DelegatingProjectContext implements ProjectContext {

	private transient static final Log logger = LogFactory.getLog(DelegatingProjectContext.class.getName());
	private ProjectContext delegate;

	public DelegatingProjectContext(ProjectContext delegate) {
		this.delegate = delegate;
	}

	public void addLink(ProjectLink link) {
		delegate.addLink(link);
	}

	public void addMeasure(Measure measure) {
		delegate.addMeasure(measure);
	}

	public void addMeasure(Metric metric, Double value) {
		delegate.addMeasure(metric, value);
	}

	public void addMeasure(Metric metric, String value) {
		delegate.addMeasure(metric, value);
	}

	public void addMeasure(Resource resource, Measure measure) {
		delegate.addMeasure(resource, measure);
	}

	public void addMeasure(Resource resource, Metric metric, Double value) {
		delegate.addMeasure(resource, metric, value);
	}

	public void addMeasure(Resource resource, Metric metric, String value) {
		delegate.addMeasure(resource, metric, value);
	}

	public void addSource(Resource resource, String source) {
		delegate.addSource(resource, source);
	}

	public void addViolation(Resource resource, Rule rule, String message, RuleFailureLevel level,
			RuleFailureParam... params) {
		
		logger.info(String.format("Adding violation: %s, %s, %s, %s", resource, rule, message, level));
		delegate.addViolation(resource, rule, message, level, params);
	}

	public String getResourceKey(Resource resource) {
		return delegate.getResourceKey(resource);
	}

	public void removeLink(String key) {
		delegate.removeLink(key);
	}

	
}
