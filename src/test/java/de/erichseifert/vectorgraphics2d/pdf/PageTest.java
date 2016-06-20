/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2016 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.vectorgraphics2d.pdf;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.geom.Rectangle2D;
import org.junit.Test;

import org.hamcrest.CoreMatchers;

public class PageTest {
	@Test
	public void testTypeEntryIsPage() {
		Page page = new Page(null);
		String typeKey = "Type";

		Object type = page.get(typeKey);

		assertThat((String) type, is("Page"));
	}

	@Test
	public void testConstructorSetsMediaBoxEntry() {
		Rectangle2D mediaBox = new Rectangle2D.Double(2, 4, 24, 42);
		Page page = new Page(mediaBox);

		assertThat(page.get("MediaBox"), CoreMatchers.<Object>is(mediaBox));
	}
}

