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
package de.erichseifert.vectorgraphics2d.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
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
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;
import org.junit.BeforeClass;
import org.junit.Test;


public class GraphicsUtilsTest {
	private static final double DELTA = 1e-15;

	private static final int TEST_IMAGE_WIDTH = 3;
	private static final int TEST_IMAGE_HEIGHT = 3;
	private static BufferedImage blackGrayscaleImage;
	private static BufferedImage whiteGrayscaleImage;

	private void assertBufferedImageEquals(Image expected, BufferedImage actual) {
		assertNotNull(actual);
		assertEquals(expected.getWidth(null), actual.getWidth());
		assertEquals(expected.getHeight(null), actual.getHeight());
	}

	private void assertBufferedImageEquals(RenderedImage expected, BufferedImage actual) {
		assertNotNull(actual);
		assertEquals(expected.getWidth(), actual.getWidth());
		assertEquals(expected.getHeight(), actual.getHeight());
	}

	private void assertBufferedImageContentEquals(BufferedImage expected, BufferedImage actual) {
		DataBuffer expectedData = expected.getData().getDataBuffer();
		DataBuffer actualData = actual.getData().getDataBuffer();

		int expectedDataSize = expectedData.getSize();
		int actualDataSize = actualData.getSize();

		assertEquals(expectedDataSize, actualDataSize);

		for (int i = 0; i < actualDataSize; i++) {
			assertEquals(String.format("Mismatch at pixel %d", i),
					actualData.getElem(i), expectedData.getElem(i));
		}
	}

	@BeforeClass
	public static void setUpClass() {
		{
			blackGrayscaleImage = new BufferedImage(
					TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
			Graphics g = blackGrayscaleImage.getGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT);
		}
		{
			whiteGrayscaleImage = new BufferedImage(
					TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
			Graphics g = whiteGrayscaleImage.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT);
		}
	}

	@Test
	public void toBufferedImageCanConvertImageWithAlphaChannel() {
		Image image = new BufferedImage(
				TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		BufferedImage result = GraphicsUtils.toBufferedImage(image);

		assertBufferedImageEquals(image, result);
	}

	@Test
	public void toBufferedImageCanConvertImageWithoutAlphaChannel() {
		Image image = new BufferedImage(
				TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

		BufferedImage result = GraphicsUtils.toBufferedImage(image);

		assertBufferedImageEquals(image, result);
	}

	@Test
	public void toBufferedImageCanConvertFilteredImage() {
		Image image = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(
			new BufferedImage(
					TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB).getSource(),
			new RGBImageFilter() {
				@Override
				public int filterRGB(int x, int y, int rgb) {
					return rgb & 0xff;
				}
			}
		));

		BufferedImage result = GraphicsUtils.toBufferedImage(image);

		assertBufferedImageEquals(image, result);
	}

	@Test
	public void toBufferedImageCanConvertRenderedImage() {
		final BufferedImage bimage = new BufferedImage(
				TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		RenderedImage image = new RenderedImage() {
			@Override public Vector<RenderedImage> getSources() { return bimage.getSources(); }
			@Override public Object getProperty(String name) { if ("foo".equals(name)) return 42; else return bimage.getProperty(name); }
			@Override public String[] getPropertyNames() { return new String[] {"foo"}; }
			@Override public ColorModel getColorModel() { return bimage.getColorModel(); }
			@Override public SampleModel getSampleModel() { return bimage.getSampleModel(); }
			@Override public int getWidth() { return bimage.getWidth(); }
			@Override public int getHeight() { return bimage.getHeight(); }
			@Override public int getMinX() { return bimage.getMinX(); }
			@Override public int getMinY() { return bimage.getMinY(); }
			@Override public int getNumXTiles() { return bimage.getNumXTiles(); }
			@Override public int getNumYTiles() { return bimage.getNumYTiles(); }
			@Override public int getMinTileX() { return bimage.getMinTileX(); }
			@Override public int getMinTileY() { return bimage.getMinTileY(); }
			@Override public int getTileWidth() { return bimage.getTileWidth(); }
			@Override public int getTileHeight() { return bimage.getTileHeight(); }
			@Override public int getTileGridXOffset() { return bimage.getTileGridXOffset(); }
			@Override public int getTileGridYOffset() { return bimage.getTileGridYOffset(); }
			@Override public Raster getTile(int tileX, int tileY) { return bimage.getTile(tileX, tileY); }
			@Override public Raster getData() { return bimage.getData(); }
			@Override public Raster getData(Rectangle rect) { return bimage.getData(rect); }
			@Override public WritableRaster copyData(WritableRaster raster) { return bimage.copyData(raster); }
		};

		BufferedImage result = GraphicsUtils.toBufferedImage(image);

		assertBufferedImageEquals(image, result);
	}

	@Test
	public void hasAlphaIsTrueForImageWithAlphaChannel() {
		Image image = new BufferedImage(
				TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		assertTrue(GraphicsUtils.hasAlpha(image));
	}

	@Test
	public void hasAlphaIsFalseForImageWithoutAlphaChannel() {
		Image image = new BufferedImage(
				TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

		assertFalse(GraphicsUtils.hasAlpha(image));
	}

	@Test
	public void getPhysicalFontReturnsPhysicalFontForFontFamily() {
		// FIXME: Use valid fonts for headless Continuous Integration environment
		assumeFalse(GraphicsEnvironment.isHeadless());

		Font font = new Font("Monospaced", Font.PLAIN, 12);

		assertNotSame(font, GraphicsUtils.getPhysicalFont(font));
	}

	@Test
	public void getPhysicalFontReturnsSameObjectForPhysicalFont() {
		// FIXME: Use valid fonts for headless Continuous Integration environment
		assumeFalse(GraphicsEnvironment.isHeadless());

		Font font = new Font("Arial", Font.PLAIN, 12);

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

	private void assertShapeClassIsCloneable(Class shapeClass) throws IllegalAccessException, InstantiationException {
		Shape shape = (Shape) shapeClass.newInstance();
		Shape clone = GraphicsUtils.clone(shape);
		assertNotNull(clone);
		assertShapeEquals(shape, clone);
	}

	@Test
	public void linesCanBeCloned() throws InstantiationException, IllegalAccessException {
		for (Class shapeClass : new Class[] {
			Line2D.Float.class, Line2D.Double.class
		}) {
			assertShapeClassIsCloneable(shapeClass);
		}
	}

	@Test
	public void rectanglesCanBeCloned() throws InstantiationException, IllegalAccessException {
		for (Class shapeClass : new Class[] {
			Rectangle.class, Rectangle2D.Float.class, Rectangle2D.Double.class,
		}) {
			assertShapeClassIsCloneable(shapeClass);
		}
	}

	@Test
	public void roundedRectanglesCanBeCloned() throws InstantiationException, IllegalAccessException {
		for (Class shapeClass : new Class[] {
				RoundRectangle2D.Float.class, RoundRectangle2D.Double.class
		}) {
			assertShapeClassIsCloneable(shapeClass);
		}
	}

	@Test
	public void ellipsesCanBeCloned() throws InstantiationException, IllegalAccessException {
		for (Class shapeClass : new Class[] {
			Ellipse2D.Float.class, Ellipse2D.Double.class
		}) {
			assertShapeClassIsCloneable(shapeClass);
		}
	}

	@Test
	public void arcsCanBeCloned() throws InstantiationException, IllegalAccessException {
		for (Class shapeClass : new Class[] {
			Arc2D.Float.class, Arc2D.Double.class
		}) {
			assertShapeClassIsCloneable(shapeClass);
		}
	}

	@Test
	public void polygonsCanBeCloned() throws InstantiationException, IllegalAccessException {
		assertShapeClassIsCloneable(Polygon.class);
	}

	@Test
	public void cubicCurvesCanBeCloned() throws InstantiationException, IllegalAccessException {
		for (Class shapeClass : new Class[] {
			CubicCurve2D.Float.class, CubicCurve2D.Double.class
		}) {
			assertShapeClassIsCloneable(shapeClass);
		}
	}

	@Test
	public void quadCurvesCanBeCloned() throws InstantiationException, IllegalAccessException {
		for (Class shapeClass : new Class[] {
			QuadCurve2D.Float.class, QuadCurve2D.Double.class
		}) {
			assertShapeClassIsCloneable(shapeClass);
		}
	}

	@Test
	public void pathsCanBeCloned() throws InstantiationException, IllegalAccessException {
		for (Class shapeClass : new Class[] {
			Path2D.Float.class, Path2D.Double.class
		}) {
			assertShapeClassIsCloneable(shapeClass);
		}
	}

	@Test
	public void cloneNullReturnsNull() throws InstantiationException, IllegalAccessException {
		Shape result = GraphicsUtils.clone(null);

		assertNull(result);
	}

	@Test
	public void equalsReturnsTrueForEmptyPathsWithSameWindingRules() {
		Path2D path1 = new Path2D.Double(PathIterator.WIND_EVEN_ODD);
		Path2D path2 = new Path2D.Double(PathIterator.WIND_EVEN_ODD);

		assertTrue(GraphicsUtils.equals(path1, path2));
	}

	@Test
	public void equalsReturnsFalseForEmptyPathsWithDifferentWindingRules() {
		Path2D windNonZero = new Path2D.Double(PathIterator.WIND_NON_ZERO);
		Path2D windEvenOdd = new Path2D.Double(PathIterator.WIND_EVEN_ODD);

		assertFalse(GraphicsUtils.equals(windNonZero, windEvenOdd));
	}

	@Test
	public void equalsReturnsFalseForPathsWithSamePointCoordinatesButDifferentSegmentTypes() {
		Path2D path1 = new Path2D.Double();
		path1.moveTo(12d, 34d);
		path1.lineTo(56d, 78d);
		Path2D path2 = new Path2D.Double();
		path2.moveTo(12d, 34d);
		path2.curveTo(56d, 78d, 90d, 12d, 34d, 56d);

		assertFalse(GraphicsUtils.equals(path1, path2));
	}

	@Test
	public void equalsReturnsFalseForPathsWithDifferentPointCoordinates() {
		Path2D path1 = new Path2D.Double();
		path1.moveTo(12d, 34d);
		path1.lineTo(56d, 78d);
		Path2D path2 = new Path2D.Double();
		path2.moveTo(12d, 34d);
		path2.lineTo(56d, 90d);

		assertFalse(GraphicsUtils.equals(path1, path2));
	}

	@Test
	public void equalsReturnsFalseForPathsWithNumberOfPoints() {
		Path2D path1 = new Path2D.Double();
		path1.moveTo(12d, 34d);
		path1.lineTo(56d, 78d);
		Path2D path2 = new Path2D.Double();
		path2.moveTo(12d, 34d);
		path2.lineTo(56d, 78d);
		path2.lineTo(90d, 12d);

		assertFalse(GraphicsUtils.equals(path1, path2));
	}

	@Test
	public void usesAlphaReturnsFalseForNullImage() {
		boolean result = GraphicsUtils.usesAlpha(null);

		assertFalse(result);
	}

	@Test
	public void usesAlphaReturnsFalseForImageWithoutAlphaChannel() {
		Image image = new BufferedImage(
				TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

		boolean result = GraphicsUtils.usesAlpha(image);

		assertFalse(result);
	}

	@Test
	public void usesAlphaReturnsTrueForImageWithTransparentPixels() {
		Image image = new BufferedImage(
				TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		boolean result = GraphicsUtils.usesAlpha(image);

		assertTrue(result);
	}

	@Test
	public void usesAlphaReturnsFalseForImageWithoutTransparentPixels() {
		BufferedImage image = new BufferedImage(
				TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT);

		boolean result = GraphicsUtils.usesAlpha(image);

		assertFalse(result);
	}

	@Test
	public void getAlphaImageReturnsWhiteImageForInputWithoutAlphaChannel() {
		BufferedImage image = new BufferedImage(
				TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

		BufferedImage result = GraphicsUtils.getAlphaImage(image);

		assertBufferedImageContentEquals(whiteGrayscaleImage, result);
	}

	@Test
	public void getAlphaImageReturnsBlackImageForTransparentInput() {
		BufferedImage image = new BufferedImage(
				TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		BufferedImage result = GraphicsUtils.getAlphaImage(image);

		assertBufferedImageContentEquals(blackGrayscaleImage, result);
	}

	@Test
	public void getAlphaImageReturnsBlackImageForTransparentInputWithBitmaskAlpha() {
		ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel colorModel = new ComponentColorModel(
				colorSpace, true, false, Transparency.BITMASK, DataBuffer.TYPE_BYTE);
		WritableRaster raster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, TEST_IMAGE_WIDTH, TEST_IMAGE_WIDTH, 4, null);
		BufferedImage image = new BufferedImage(
				colorModel, raster, colorModel.isAlphaPremultiplied(), null);

		BufferedImage result = GraphicsUtils.getAlphaImage(image);

		assertBufferedImageContentEquals(blackGrayscaleImage, result);
	}

	@Test
	public void getAlphaImageReturnsWhiteImageForOpaqueInputWithBitmaskAlpha() {
		ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel colorModel = new ComponentColorModel(
				colorSpace, true, false, Transparency.BITMASK, DataBuffer.TYPE_BYTE);
		WritableRaster raster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, TEST_IMAGE_WIDTH, TEST_IMAGE_WIDTH, 4, null);
		BufferedImage image = new BufferedImage(
				colorModel, raster, colorModel.isAlphaPremultiplied(), null);
		Graphics g = image.getGraphics();
		g.setColor(Color.RED);
		g.fillRect(0, 0, TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT);

		BufferedImage result = GraphicsUtils.getAlphaImage(image);

		assertBufferedImageContentEquals(whiteGrayscaleImage, result);
	}
}
