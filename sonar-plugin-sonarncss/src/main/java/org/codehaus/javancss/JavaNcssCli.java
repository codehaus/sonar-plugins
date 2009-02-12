/*
Copyright (C) 2001 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS

JavaNCSS is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

JavaNCSS is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with JavaNCSS; see the file COPYING.  If not, write to
the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.  */
package org.codehaus.javancss;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.codehaus.javancss.report.JavaNcssXmlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaNcssCli {

	private static Logger logger = LoggerFactory.getLogger(JavaNcssCli.class);
	private static Options cliOptions;
	private static CommandLine cmd;

	public static void main(String[] args) {

		try {
			logger.info("JavaNCSS is starting analysing source files.");
			cliOptions = defineCliOptions();
			CommandLineParser parser = new PosixParser();
			cmd = parser.parse(cliOptions, args);

			if (cmd.hasOption("help")) {
				printHelp();
				return;
			}

			JavaNcss javaNcss = new JavaNcss(getDirectoryToAnalyse());
			Resource project = javaNcss.analyseSources();
			if (cmd.hasOption("xml")) {
				JavaNcssXmlGenerator xmlGenerator = new JavaNcssXmlGenerator(new File(cmd.getOptionValue("out")),
						project);
				xmlGenerator.generateReport();
			}

		} catch (ParseException e) {
			printHelp();
		} catch (RuntimeException e) {
			logger.error("JavaNCSS didn't manage to analyse source files.", e);
		}
	}

	private static File getDirectoryToAnalyse() {
		List<String> args = cmd.getArgList();
		if (args.size() == 0) {
			printHelp();
			throw new IllegalArgumentException("You must specify one directory to analyse.");
		} else if (args.size() > 1) {
			printHelp();
			throw new IllegalArgumentException("You can't currently specify more than one directory to analyse.");
		}
		File dirToAnalyse = new File(args.get(0));
		if (!dirToAnalyse.exists()) {
			throw new IllegalArgumentException("The directory you've specified isn't correct : " + args.get(0));
		}
		return dirToAnalyse;
	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("JavaNCSS [options] dir", cliOptions);
	}

	private static Options defineCliOptions() {
		Options options = new Options();
		options.addOption("xml", false,
				"Output in xml and not in ascii format. Additional option '-all' is recommended.");
		options.addOption("out", true,
				"Output goes normally to standard output, with this option an output file can be specified. ");
		options.addOption("help", false, "Prints out some basic information.");
		return options;
	}
}
