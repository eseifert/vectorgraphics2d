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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Font;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import org.junit.Test;


public class GraphicsUtilsTest {
	private static final double DELTA = 1e-15;

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

	@Test
	public void testPhysicalFont() {
		Font font;

		font = new Font("Monospaced", Font.PLAIN, 12);
		assertNotSame(font, GraphicsUtils.getPhysicalFont(font));

		font = new Font("Arial", Font.PLAIN, 12);
		assertSame(font, GraphicsUtils.getPhysicalFont(font));
	}

	private static void assertShapeEquals(Shape expected, Shape actual) {
		if ((expected instanceof Line2D) && (actual instanceof Line2D)) {
			assertEquals(((Line2D) expected).getP1(), ((Line2D) actual).getP1());
			assertEquals(((Line2D) expected).getP2(), ((Line2D) actual).getP2());
		} else if ((expected instanceof Polygon) && (actual instanceof Polygon)) {
			int n = ((Polygon) actual).npoints;
			assertEquals(((Polygon) expected).npoints, n);
			if (n > 0) {
				assertArrayEquals(((Polygon) expected).xpoints, ((Polygon) actual).xpoints);
				assertArrayEquals(((Polygon) expected).ypoints, ((Polygon) actual).ypoints);
			}
		} else if ((expected instanceof QuadCurve2D) && (actual instanceof QuadCurve2D)) {
			assertEquals(((QuadCurve2D) expected).getP1(), ((QuadCurve2D) actual).getP1());
			assertEquals(((QuadCurve2D) expected).getCtrlPt(), ((QuadCurve2D) actual).getCtrlPt());
			assertEquals(((QuadCurve2D) expected).getP2(), ((QuadCurve2D) actual).getP2());
		} else if ((expected instanceof CubicCurve2D) && (actual instanceof CubicCurve2D)) {
			assertEquals(((CubicCurve2D) expected).getP1(), ((CubicCurve2D) actual).getP1());
			assertEquals(((CubicCurve2D) expected).getCtrlP1(), ((CubicCurve2D) actual).getCtrlP1());
			assertEquals(((CubicCurve2D) expected).getCtrlP2(), ((CubicCurve2D) actual).getCtrlP2());
			assertEquals(((CubicCurve2D) expected).getP2(), ((CubicCurve2D) actual).getP2());
		} else if ((expected instanceof Path2D) && (actual instanceof Path2D)) {
			PathIterator itExpected = expected.getPathIterator(null);
			PathIterator itActual = actual.getPathIterator(null);
			double[] segmentExpected = new double[6];
			double[] segmentActual = new double[6];
			for (; !itExpected.isDone() || !itActual.isDone(); itExpected.next(), itActual.next()) {
				assertEquals(itExpected.getWindingRule(), itActual.getWindingRule());
				itExpected.currentSegment(segmentExpected);
				itActual.currentSegment(segmentActual);
				assertArrayEquals(segmentExpected, segmentActual, DELTA);
			}
		} else {
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testCloneShape() throws InstantiationException, IllegalAccessException {
		Class<?>[] shapeClasses = {
			Line2D.Float.class,
			Line2D.Double.class,
			Rectangle.class,
			Rectangle2D.Float.class,
			Rectangle2D.Double.class,
			RoundRectangle2D.Float.class,
			RoundRectangle2D.Double.class,
			Ellipse2D.Float.class,
			Ellipse2D.Double.class,
			Arc2D.Float.class,
			Arc2D.Double.class,
			Polygon.class,
			CubicCurve2D.Float.class,
			CubicCurve2D.Double.class,
			QuadCurve2D.Float.class,
			QuadCurve2D.Double.class,
			Path2D.Float.class,
			Path2D.Double.class
		};

		for (Class<?> shapeClass : shapeClasses) {
			Shape shape = (Shape) shapeClass.newInstance();
			Shape clone = GraphicsUtils.clone(shape);
			assertNotNull(clone);
			assertShapeEquals(shape, clone);
		}
	}
}
