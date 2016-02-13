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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class PaintTest extends TestCase {
	public PaintTest() throws IOException {
	}

	@Override
	public void draw(Graphics2D g) {
		// Draw multiple rotated rectangles
		final int steps = 25;
		final int cols = 5;
		final int rows = steps/cols;
		final double tileWidth = getPageSize().width/cols;
		final double tileHeight = getPageSize().height/rows;
		g.translate(tileWidth/2, tileHeight/2);

		final double rectWidth = tileWidth*0.8;
		final double rectHeight = tileHeight*0.8;
		Rectangle2D rect = new Rectangle2D.Double(-rectWidth/2, -rectHeight/2, rectWidth, rectHeight);
		g.setPaint(new GradientPaint(0f, (float) (-rectHeight/2), Color.RED, 0f, (float) (rectHeight/2), Color.BLUE));
		for (int i = 0; i < steps; i++) {
			AffineTransform txOld = g.getTransform();
			AffineTransform tx = new AffineTransform(txOld);
			int col = i%5;
			int row = i/5;
			tx.translate(col*tileWidth, row*tileHeight);
			tx.rotate(i*Math.toRadians(360.0/steps));
			g.setTransform(tx);
			g.fill(rect);
			g.setTransform(txOld);
		}
	}
}
