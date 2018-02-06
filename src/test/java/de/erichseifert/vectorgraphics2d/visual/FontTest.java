/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2018 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.vectorgraphics2d.visual;

import java.awt.Font;
import java.awt.Graphics2D;
import java.io.IOException;

import de.erichseifert.vectorgraphics2d.GraphicsState;

public class FontTest extends TestCase {
	private static final Font DEFAULT_FONT = GraphicsState.DEFAULT_FONT;

	public FontTest() throws IOException {
	}

	@Override
	public void draw(Graphics2D g) {
		final int tileCountH = 4;
		final int tileCountV = 8;
		final double wTile = getPageSize().getWidth()/tileCountH;
		final double hTile = getPageSize().getHeight()/tileCountV;
		final double xOrigin = (getPageSize().getWidth() - tileCountH*wTile)/2.0;
		final double yOrigin = (getPageSize().getHeight() - tileCountV*hTile)/2.0;
		double x = xOrigin;
		double y = yOrigin;

		final float[] sizes = {
				DEFAULT_FONT.getSize2D(), DEFAULT_FONT.getSize2D()/2f
		};
		final String[] names = {
				DEFAULT_FONT.getName(), Font.SERIF, Font.MONOSPACED, "Arial"
		};
		final int[] styles = {
				Font.PLAIN, Font.ITALIC, Font.BOLD, Font.BOLD | Font.ITALIC
		};

		for (float size: sizes) {
			for (String name : names) {
				for (int style : styles) {
					Font font = new Font(name, style, 10).deriveFont(size);
					g.setFont(font);
					g.drawString("vg2d", (float) x, (float) y);

					x += wTile;
					if (x >= tileCountH*wTile) {
						x = xOrigin;
						y += hTile;
					}
				}
			}
		}
	}}
