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
package de.erichseifert.vectorgraphics2d.visual;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

public class ShapesTest extends TestCase {
	public ShapesTest() throws IOException {
	}

	@Override
	public void draw(Graphics2D g) {
		final int tileCountH = 4;
		final int tileCountV = 6;
		final double wTile = getPageSize().width/tileCountH;
		final double hTile = getPageSize().height/tileCountV;
		final double xOrigin = (getPageSize().width - tileCountH*wTile)/2.0;
		final double yOrigin = (getPageSize().height - tileCountV*hTile)/2.0;
		double x = xOrigin;
		double y = yOrigin;

		g.draw(new Line2D.Double(x, y, x + 0.8*wTile, y + 0.6*hTile));
		x += wTile;
		g.draw(new QuadCurve2D.Double(x, y, x + 0.8*wTile, y, x + 0.8*wTile, y + 0.6*hTile));
		x += wTile;
		g.draw(new CubicCurve2D.Double(x, y, x + 0.8*wTile, y, x, y + 0.6*hTile, x + 0.8*wTile, y + 0.6*hTile));

		x = xOrigin;
		y += hTile;
		g.fill(new Rectangle2D.Double(x, y, 0.8*wTile, 0.6*hTile));
		x += wTile;
		g.draw(new Rectangle2D.Double(x, y, 0.8*wTile, 0.6*hTile));
		x += wTile;

		g.fill(new RoundRectangle2D.Double(x, y, 0.8*wTile, 0.6*hTile, 0.2*wTile, 0.2*hTile));
		x += wTile;
		g.draw(new RoundRectangle2D.Double(x, y, 0.8*wTile, 0.6*hTile, 0.2*wTile, 0.2*hTile));
		x += wTile;


		x = xOrigin;
		y += hTile;
		g.fill(new Ellipse2D.Double(x, y, 0.8*wTile, 0.6*hTile));
		x += wTile;
		g.draw(new Ellipse2D.Double(x, y, 0.8*wTile, 0.6*hTile));
		x += wTile;

		g.fill(new Polygon(
				new int[] {(int) (x),             (int) (x + 0.8*wTile/2.0), (int) (x + 0.8*wTile)},
				new int[] {(int) (y + 0.6*hTile), (int) (y),                 (int) (y + 0.6*hTile)},
				3
		));
		x += wTile;
		g.draw(new Polygon(
				new int[] {(int) (x),             (int) (x + 0.8*wTile/2.0), (int) (x + 0.8*wTile)},
				new int[] {(int) (y + 0.6*hTile), (int) (y),                 (int) (y + 0.6*hTile)},
				3
		));


		x = xOrigin;
		y += hTile;
		g.fill(new Arc2D.Double(x, y, 0.8*wTile, 0.6*hTile, 110, 320, Arc2D.PIE));
		x += wTile;
		g.draw(new Arc2D.Double(x, y, 0.8*wTile, 0.6*hTile, 110, 320, Arc2D.PIE));
		x += wTile;
		g.fill(new Arc2D.Double(x, y, 0.6*hTile, 0.8*wTile, 10, 320, Arc2D.CHORD));
		x += wTile;
		g.draw(new Arc2D.Double(x, y, 0.6*hTile, 0.8*wTile, 10, 320, Arc2D.CHORD));


		x = xOrigin;
		y += hTile;
		g.fill(new Arc2D.Double(x, y, 0.6*hTile, 0.8*wTile, 10, 320, Arc2D.OPEN));
		x += wTile;
		g.draw(new Arc2D.Double(x, y, 0.6*hTile, 0.8*wTile, 10, 320, Arc2D.OPEN));
		x += wTile;
		g.fill(new Arc2D.Double(x, y, 0.6*hTile, 0.8*wTile, 10, 320, Arc2D.PIE));
		x += wTile;
		g.draw(new Arc2D.Double(x, y, 0.6*hTile, 0.8*wTile, 10, 320, Arc2D.PIE));


		x = xOrigin;
		y += hTile;

		final Path2D path1 = new Path2D.Double();
		path1.moveTo(0.00, 0.00);
		path1.lineTo(0.33, 1.00);
		path1.lineTo(0.67, 0.00);
		path1.quadTo(0.33, 0.00, 0.33, 0.50);
		path1.quadTo(0.33, 1.00, 0.67, 1.00);
		path1.quadTo(1.00, 1.00, 1.00, 0.50);
		path1.lineTo(0.67, 0.50);
		path1.transform(AffineTransform.getScaleInstance(0.8 * wTile, 0.6 * hTile));

		path1.transform(AffineTransform.getTranslateInstance(x, y));
		g.fill(path1);
		x += wTile;
		path1.transform(AffineTransform.getTranslateInstance(wTile, 0.0));
		g.draw(path1);
		x += wTile;

		final Path2D path2 = new Path2D.Double();
		path2.moveTo(0.0, 0.4);
		path2.curveTo(0.0, 0.3, 0.0, 0.0, 0.2, 0.0);
		path2.curveTo(0.3, 0.0, 0.4, 0.1, 0.4, 0.3);
		path2.curveTo(0.4, 0.5, 0.2, 0.8, 0.0, 1.0);
		path2.lineTo(0.6, 1.0);
		path2.lineTo(0.6, 0.0);
		path2.curveTo(0.8, 0.0, 1.0, 0.2, 1.0, 0.5);
		path2.curveTo(1.0, 0.6, 1.0, 0.8, 0.9, 0.9);
		path2.closePath();
		path2.transform(AffineTransform.getScaleInstance(0.8*wTile, 0.6*hTile));

		path2.transform(AffineTransform.getTranslateInstance(x, y));
		g.fill(path2);
		x += wTile;
		path2.transform(AffineTransform.getTranslateInstance(wTile, 0.0));
		g.draw(path2);
	}
}
