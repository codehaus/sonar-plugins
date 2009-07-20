package org.sonar.cpp;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.sonar.commons.resources.Resource;

public class CppTest {

	@Test
	public void hasCppAsKeyAndName() throws Exception {
		Cpp cpp = new Cpp();
		assertThat(cpp.getKey(), is(Cpp.KEY));
		assertThat(cpp.getName(), is(Cpp.NAME));
	}

	@Test
	public void hasSuffixesForVariousCandCppFiles() throws Exception {
		Cpp cpp = new Cpp();
		List<String> suffixesList = Arrays.asList(cpp.getFileSuffixes());
		assertThat(suffixesList, hasItem("cpp"));
		assertThat(suffixesList, hasItem("c"));
		assertThat(suffixesList, hasItem("C"));
		assertThat(suffixesList, hasItem("hpp"));
		assertThat(suffixesList, hasItem("h"));
	}

	@Test
	public void shouldCreateSimpleFile() throws Exception {
		Cpp cpp = new Cpp();
		Resource fileResource = cpp.fileResource("toto.cpp");
		checkResource(fileResource, "toto.cpp", "toto.cpp");
	}

	@Test
	public void shouldCreateFileFromPath() throws Exception {
		Cpp cpp = new Cpp();
		Resource fileResource = cpp.fileResource("src/toto.cpp");
		checkResource(fileResource, "src/toto.cpp", "toto.cpp");
	}

	@Test
	public void shouldTrimWhitespaceFromFilePathsWhenCreatingFiles()
			throws Exception {
		Cpp cpp = new Cpp();
		Resource fileResource = cpp.fileResource(" 		src/toto.cpp  ");
		checkResource(fileResource, "src/toto.cpp", "toto.cpp");
	}

	private void checkResource(Resource fileResource, String key, String name) {
		assertThat(fileResource.getKey(), is(key));
		assertThat(fileResource.getName(), is(name));
		assertThat(fileResource.getQualifier(), is(Resource.QUALIFIER_FILE));
	}

}
