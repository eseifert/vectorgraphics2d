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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class ColorTest extends TestCase {
	public ColorTest() throws IOException {
	}

	@Override
	public void draw(Graphics2D g) {
		final float wPage = (float) getPageSize().width;
		final float hPage = (float) getPageSize().height;
		final float wTile = Math.min(wPage/15f, hPage/15f);
		final float hTile = wTile;

		float w = wPage - wTile;
		float h = hPage - hTile;

		for (float y = (hPage - h)/2f; y < h; y += hTile) {
			float yRel = y/h;
			for (float x = (wPage - w)/2f; x < w; x += wTile) {
				float xRel = x/w;
				Color c = Color.getHSBColor(yRel, 1f, 1f);
				int alpha = 255 - (int) (xRel*255f);
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
				g.fill(new Rectangle2D.Float(x, y, wTile, hTile));
			}
		}
	}

}
