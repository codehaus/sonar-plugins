package org.sonar.plugins.taglist;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonar.plugins.api.maven.AbstractMavenPluginHandler;
import org.sonar.plugins.api.maven.model.MavenPlugin;
import org.sonar.plugins.api.maven.model.MavenPom;

public class TaglistMavenPluginHandler extends AbstractMavenPluginHandler {

	private final String[] DEFAULT_TAGS = new String[] {"TODO", "FIXME", "@todo", "@fixme"};
	
	@Override
	public void configurePlugin(MavenPom pom, MavenPlugin plugin) {
		plugin.setConfigParameter("aggregate", "false");
		plugin.setConfigParameter("emptyComments", "true");
		plugin.setConfigParameter("multipleLineComments", "true");
		plugin.setConfigParameter("encoding", "MacRoman");
		plugin.setConfigParameter("xmlOutputDirectory", "${project.build.directory}/taglist");
		
		// tags root element
		Xpp3Dom tags = new Xpp3Dom("tags");
		
		for (String t : DEFAULT_TAGS) {
			Xpp3Dom tag = new Xpp3Dom("tag");
			tag.setValue(t);
			tags.addChild(tag);
		}
		
		plugin.getConfiguration().getXpp3Dom().addChild(tags);
		
	}

	public String getArtifactId() {
		return "taglist-maven-plugin";
	}

	public String[] getGoals() {
		return new String[] { "taglist" };
	}

	public String getGroupId() {
		return MavenPom.GROUP_ID_CODEHAUS_MOJO;
	}

	public String getVersion() {
		return "2.3";
	}

	public boolean isFixedVersion() {
		return false;
	}

}
