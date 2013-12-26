/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2013 Erich Seifert <dev[at]erichseifert.de>
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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

/**
 * Utility class for unit tests.
 */
public abstract class TestUtils {
	protected TestUtils() {
		throw new UnsupportedOperationException();
	}

	public static class Template extends LinkedList<Object> {
		public Template(Object... lines) {
			Collections.addAll(this, lines);
		}

		public Template(Template... templates) {
			for (Template template : templates) {
				addAll(template);
			}
		}
	}

	public static void assertTemplateEquals(Template expected, Template actual) {
		Iterator<Object> itExpected = expected.iterator();
		Iterator<Object> itActual = expected.iterator();
		while (itExpected.hasNext() && itActual.hasNext()) {
			Object lineExpected = itExpected.next();
			Object lineActual = itActual.next();
			if (lineExpected instanceof String) {
				assertEquals(lineExpected, lineActual);
			}
		}
		assertEquals("Number of lines in template differs.", expected.size(), actual.size());
	}
}
