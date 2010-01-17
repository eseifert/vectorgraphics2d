/* VectorGraphics2D : Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010, by Erich Seifert.
 *
 * This file is part of VectorGraphics2D.
 *
 * VectorGraphics2D is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VectorGraphics2D is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VectorGraphics2D.  If not, see <http://www.gnu.org/licenses/>.
 */

package vectorgraphics2d.tests;

import org.junit.Before;

import vectorgraphics2d.SVGGraphics2D;

public class SVGGraphics2DTest extends VectorGraphics2DTest {

	@Before
	public void setUp() {
		g = new SVGGraphics2D(DOC_X, DOC_Y, DOC_W, DOC_H);
	}

}
