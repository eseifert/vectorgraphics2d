/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2016 Erich Seifert <dev[at]erichseifert.de>
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

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

import static org.junit.Assert.*;


public class GraphicsUtilsTest {

	@Test
	public void testToBufferedImage() {
		Image[] images = {
			new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB),
			new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB),
			Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(
				new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB).getSource(),
				new RGBImageFilter() {
					@Override
					public int filterRGB(int x, int y, int rgb) {
						return rgb & 0xff;
					}
				}
			))
		};

		for (Image image : images) {
			BufferedImage bimage = GraphicsUtils.toBufferedImage(image);
			assertNotNull(bimage);
			assertEquals(BufferedImage.class, bimage.getClass());
			assertEquals(image.getWidth(null), bimage.getWidth());
			assertEquals(image.getHeight(null), bimage.getHeight());
		}
	}

	@Test
	public void testHasAlpha() {
		Image image;

		image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		assertTrue(GraphicsUtils.hasAlpha(image));

		image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
		assertFalse(GraphicsUtils.hasAlpha(image));
	}

}
