/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2017 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
 *
 * This file is part of VectorGraphics2D.
 *
 * VectorGraphics2D is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VectorGraphics2D is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with VectorGraphics2D.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.vectorgraphics2d;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ProcessorsTest {
	@Test(expected = NullPointerException.class)
	public void testGetThrowsNullPointerExceptionWhenNullIsPassed() {
		Processors.get(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetThrowsIllegalArgumentExceptionWhenFormatIsUnknown() {
		Processors.get("UnknownFormat");
	}

	@DataPoints
	public static List<String> KNOWN_FORMATS = Arrays.asList("eps", "pdf", "svg");

	@Theory
	public void testGetReturnsNonNullWhenFormatIsKnown(String format) {
		Processor processor = Processors.get(format);

		assertThat(processor, is(notNullValue()));
	}
}
