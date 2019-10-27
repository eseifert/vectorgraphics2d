/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2019 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.vectorgraphics2d.util;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Rectangle2D;
import org.junit.Test;


public class PageSizeTest {
	public static final double DELTA = 1e-15;

	@Test
	public void pageSizeGetsInitializedCorrectlyWithXYWidthHeight() {
		PageSize pageSize = new PageSize(1.0, 2.0, 3.0, 4.0);

		assertEquals(1.0, pageSize.getX(), DELTA);
		assertEquals(2.0, pageSize.getY(), DELTA);
		assertEquals(3.0, pageSize.getWidth(), DELTA);
		assertEquals(4.0, pageSize.getHeight(), DELTA);
	}

	@Test
	public void pageSizeGetsInitializedCorrectlyWithWidthHeight() {
		PageSize pageSize = new PageSize(3.0, 4.0);

		assertEquals(0.0, pageSize.getX(), DELTA);
		assertEquals(0.0, pageSize.getY(), DELTA);
		assertEquals(3.0, pageSize.getWidth(), DELTA);
		assertEquals(4.0, pageSize.getHeight(), DELTA);
	}

	@Test
	public void pageSizeGetsInitializedCorrectlyWithRectangle() {
		PageSize pageSize = new PageSize(new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0));

		assertEquals(1.0, pageSize.getX(), DELTA);
		assertEquals(2.0, pageSize.getY(), DELTA);
		assertEquals(3.0, pageSize.getWidth(), DELTA);
		assertEquals(4.0, pageSize.getHeight(), DELTA);
	}

	@Test
	public void getPortraitTurnsALandscapeFormatIntoPortrait() {
		PageSize portrait = new PageSize(3.0, 4.0);
		PageSize landscape = new PageSize(4.0, 3.0);

		assertEquals(portrait.getWidth(), landscape.getPortrait().getWidth(), DELTA);
		assertEquals(portrait.getHeight(), landscape.getPortrait().getHeight(), DELTA);
	}

	@Test
	public void getPortraitDoesNotChangeAPortraitFormat() {
		PageSize portrait = new PageSize(3.0, 4.0);

		assertEquals(portrait.getWidth(), portrait.getPortrait().getWidth(), DELTA);
		assertEquals(portrait.getHeight(), portrait.getPortrait().getHeight(), DELTA);
	}

	@Test
	public void getLandscapeTurnsAPortraitFormatIntoLandscape() {
		PageSize portrait = new PageSize(3.0, 4.0);
		PageSize landscape = new PageSize(4.0, 3.0);

		assertEquals(landscape.getWidth(), portrait.getLandscape().getWidth(), DELTA);
		assertEquals(landscape.getHeight(), portrait.getLandscape().getHeight(), DELTA);
	}

	@Test
	public void getLandscapeDoesNotChangeALandscapeFormat() {
		PageSize landscape = new PageSize(4.0, 3.0);

		assertEquals(landscape.getWidth(), landscape.getLandscape().getWidth(), DELTA);
		assertEquals(landscape.getHeight(), landscape.getLandscape().getHeight(), DELTA);
	}
}
