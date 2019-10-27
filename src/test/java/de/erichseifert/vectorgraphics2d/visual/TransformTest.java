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
package de.erichseifert.vectorgraphics2d.visual;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class TransformTest extends TestCase {
	public TransformTest() throws IOException {
	}

	@Override
	public void draw(Graphics2D g) {
		final int rowCount = 2;
		final int colCount = 4;
		double wTile = getPageSize().getWidth()/colCount;
		double hTile = wTile;

		g.translate(0.5*wTile, 0.5*hTile);
		AffineTransform txOrig = g.getTransform();

		Shape s = new Rectangle2D.Double(0.0, 0.0, 0.5*wTile, 0.75*hTile);

		// Row 1

		g.draw(s);

		g.translate(wTile, 0.0);
		g.draw(s);

		g.translate(wTile, 0.0);
		{
			Graphics2D g2 = (Graphics2D) g.create();
			g2.scale(0.5, 0.5);
			g2.draw(s);
			g2.dispose();
		}

		g.translate(wTile, 0.0);
		{
			Graphics2D g2 = (Graphics2D) g.create();
			g2.rotate(Math.toRadians(30.0));
			g2.draw(s);
			g2.dispose();
		}

		// Row 2

		g.setTransform(txOrig);
		g.translate(0.0, hTile);

		g.shear(0.5, 0.0);
		g.draw(s);
		g.shear(-0.5, 0.0);
		g.translate(wTile, 0.0);

		g.shear(0.0, 0.5);
		g.draw(s);
		g.shear(0.0, -0.5);
	}
}
