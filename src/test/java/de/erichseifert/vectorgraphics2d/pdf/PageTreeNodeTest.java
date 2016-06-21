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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PageTreeNodeTest {
	@Test
	public void testTypeIsPages() {
		PageTreeNode pages = new PageTreeNode(null);

		String type = pages.getType();

		assertThat(type, is("Pages"));
	}

	@Test
	public void testConstructorSetsParent() {
		PageTreeNode parent = new PageTreeNode(null);

		PageTreeNode pages = new PageTreeNode(parent);

		assertThat(pages.getParent(), is(parent));
	}

	@Test
	public void testAddInsertsPage() {
		PageTreeNode pages = new PageTreeNode(null);
		Page child = new Page(null);

		pages.add(child);

		assertThat(pages.getKids(), hasItem(child));
	}

	@Test
	public void testCountReturnsZeroWhenEmpty() {
		PageTreeNode pages = new PageTreeNode(null);

		int count = pages.getCount();

		assertThat(count, is(0));
	}
}

