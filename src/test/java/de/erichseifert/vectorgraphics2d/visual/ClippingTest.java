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
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.IOException;

public class ClippingTest extends TestCase {
	public ClippingTest() throws IOException {
	}

	@Override
	public void draw(Graphics2D g) {
		double w = getPageSize().getWidth();
		double h = getPageSize().getHeight();

		AffineTransform txOrig = g.getTransform();
		g.translate(w/2.0, h/2.0);

		g.setClip(new Ellipse2D.Double(-0.6*w/2.0, -h/2.0, 0.6*w, h));
		for (double x = -w/2.0; x < w/2.0; x += 4.0) {
			g.draw(new Line2D.Double(x, -h/2.0, x, h/2.0));
		}

		g.rotate(Math.toRadians(-90.0));
		g.clip(new Ellipse2D.Double(-0.6*w/2.0, -h/2.0, 0.6*w, h));
		for (double x = -h/2.0; x < h/2.0; x += 4.0) {
			g.draw(new Line2D.Double(x, -w/2.0, x, w/2.0));
		}

		g.setTransform(txOrig);
		g.setClip(null);
		g.draw(new Line2D.Double(0.0, 0.0, w, h));
	}
}
