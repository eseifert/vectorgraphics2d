/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2015 Erich Seifert <dev[at]erichseifert.de>
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

import java.awt.Graphics2D;
import java.io.IOException;

public class CharacterTest extends TestCase {
	public CharacterTest() throws IOException {
	}

	@Override
	public void draw(Graphics2D g) {
		double w = getPageSize().width;
		double h = getPageSize().height;

		// TODO: Test complete charsets
		char[] characters = new char[] {'ä', 'ö', 'å'};
		final int colCount = 3;
		final int rowCount = 1;
		double tileWidth = w/colCount;
		double tileHeight = h/rowCount;
		int charIndex = 0;
		for (double y = 0.0; y < h; y += tileHeight) {
			for (double x = 0.0; x < w; x += tileWidth) {
				char c = characters[charIndex];
				double tileCenterX = x + tileWidth/2.0;
				double tileCenterY = y + tileHeight/2.0;
				g.drawString(String.valueOf(c), (float) tileCenterX, (float) tileCenterY);
				charIndex++;
			}
		}
	}
}
