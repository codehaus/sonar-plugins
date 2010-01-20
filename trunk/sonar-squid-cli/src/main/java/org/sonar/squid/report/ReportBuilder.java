/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sonar.squid.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.sonar.squid.ant.Analyser;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceMethod;
import org.sonar.squid.report.entries.ClassReportEntry;
import org.sonar.squid.report.entries.MethodReportEntry;
import org.sonar.squid.report.entries.PackageReportEntry;

/**
 *
 * @author Romain PELISSE - belaran@gmail.com
 */
public class ReportBuilder {

    private ReportBuilder() {}

    public static SquidReport addPackagesMetricsToReport(SquidReport report, Collection<SourceCode> packages) {
        List<PackageReportEntry> javaPackages = new ArrayList<PackageReportEntry>();
        report.setPackages(javaPackages);
        for (SourceCode javaPackage : packages) {
            PackageReportEntry packagesEntry = new PackageReportEntry(javaPackage.getKey());
            javaPackages.add((PackageReportEntry) Analyser.addBasicMetrics(javaPackage, packagesEntry));
            packagesEntry.setClasses(addClassesToPackage(javaPackage.getChildren()));
        }
        return report;
    }

    private static List<ClassReportEntry> addClassesToPackage(Collection<SourceCode> classesCode) {
        List<ClassReportEntry> javaClasses = new ArrayList<ClassReportEntry>();
        for (SourceCode javaCode : classesCode) {
            if (javaCode instanceof SourceFile) {
                SourceFile javaClass = (SourceFile) javaCode;
                ClassReportEntry classesEntry = new ClassReportEntry(javaClass.getName());
                classesEntry.setMethods(addMethodsToClass(javaClass.getChildren()));
                javaClasses.add((ClassReportEntry) Analyser.addBasicMetrics(javaClass, classesEntry));
            }
        }
        return javaClasses;
    }

    private static List<MethodReportEntry> addMethodsToClass(Collection<SourceCode> methods) {
        List<MethodReportEntry> methodEntries = new ArrayList<MethodReportEntry>();
        for (SourceCode child : methods) {
            if (child instanceof SourceMethod) {
                SourceMethod method = (SourceMethod) child;
                MethodReportEntry methodEntry = new MethodReportEntry(method.getName());
                methodEntries.add(( (MethodReportEntry) Analyser.addBasicMetrics(method, methodEntry)));
            }
        }
        return methodEntries;
    }
}
