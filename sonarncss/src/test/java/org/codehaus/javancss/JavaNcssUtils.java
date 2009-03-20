package org.codehaus.javancss;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class JavaNcssUtils {

	public static File getFile(String relativePath) {
		System.out.println(JavaNcssUtils.class.getResource(relativePath));
		return FileUtils.toFile(JavaNcssUtils.class.getResource(relativePath));
	}

}
