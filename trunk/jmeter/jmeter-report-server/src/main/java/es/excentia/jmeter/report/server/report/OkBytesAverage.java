/*
 * JMeter Report Server
 * Copyright (C) 2010 eXcentia
 * mailto:info AT excentia DOT es
 *
 * SONAR JMeter Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SONAR JMeter Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package es.excentia.jmeter.report.server.report;

import es.excentia.jmeter.report.server.testresults.xmlbeans.AbstractSample;

public class OkBytesAverage extends Average {
	@Override
	public void addMeasure(AbstractSample sample) {
		if (sample.getS()) {
			switch (getSummary().getActualPhaseIndex()) {
			case Report.FIRST_PHASE:
				incrementTotal();
				addToAvg(sample.getBy());
				break;
			case Report.SECOND_PHASE:
				addToDev(sample.getBy());
				break;
			}
		}
	}
}
