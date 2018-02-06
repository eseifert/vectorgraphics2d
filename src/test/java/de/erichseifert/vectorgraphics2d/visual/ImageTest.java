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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageTest extends TestCase {
	public ImageTest() throws IOException {
	}

	@Override
	public void draw(Graphics2D g) {
		// Draw an image
		BufferedImage image = new BufferedImage(4, 3, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gImage = (Graphics2D) image.getGraphics();
		gImage.setPaint(new GradientPaint(
				new Point2D.Double(0.0, 0.0), Color.RED,
				new Point2D.Double(3.0, 2.0), Color.BLUE)
		);
		gImage.fill(new Rectangle2D.Double(0.0, 0.0, 4.0, 3.0));

		g.drawImage(image, 0, 0, (int) getPageSize().getWidth(), (int) (0.5*getPageSize().getHeight()), null);

		g.rotate(-10.0/180.0*Math.PI, 2.0, 1.5);
		g.drawImage(image, (int) (0.1*getPageSize().getWidth()), (int) (0.6*getPageSize().getHeight()),
				(int) (0.33*getPageSize().getWidth()), (int) (0.33*getPageSize().getHeight()), null);
	}
}
