/* VectorGraphics2D : Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010, by Erich Seifert.
 *
 * This file is part of VectorGraphics2D.
 *
 * VectorGraphics2D is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VectorGraphics2D is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VectorGraphics2D.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.vectorgraphics2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.vectorgraphics2d.VectorGraphics2D;


public abstract class VectorGraphics2DTest {
	protected static final double DOC_X =   0.0;
	protected static final double DOC_Y =   0.0;
	protected static final double DOC_W = 210.0;
	protected static final double DOC_H = 297.0;

	protected VectorGraphics2D g;

	@Before
	public abstract void setUp();

	@Test
	public void testCreation() {
		assertEquals(Color.WHITE, g.getBackground());
		assertEquals(Color.BLACK, g.getColor());
		assertNull(g.getClip());
		assertNull(g.getClipBounds());
	}

	@Test
	public void testBounds() {
		Rectangle2D bounds = new Rectangle2D.Double(DOC_X, DOC_Y, DOC_W, DOC_H);
		assertEquals(bounds, g.getBounds());
	}

	@Test
	public void testDraw() {
		g.drawArc(0, 0, 210, 297, 30, 330);
		g.drawLine(0, 0, 210, 297);
		g.drawOval(0, 0, 210, 297);
		g.drawPolygon(new int[] {0, 210, 0}, new int[] {0, 297, 297}, 3);
		g.drawPolyline(new int[] {0, 210}, new int[] {0, 297}, 2);
		g.drawRect(0, 0, 210, 297);
		g.drawRoundRect(0, 0, 210, 297, 5, 5);
		// TODO: Assert something
	}

	@Test
	public void testFill() {
		g.fillArc(0, 0, 210, 297, 30, 330);
		g.fillOval(0, 0, 210, 297);
		g.fillPolygon(new int[] {0, 210, 0}, new int[] {0, 297, 297}, 3);
		g.fillRect(0, 0, 210, 297);
		g.fillRoundRect(0, 0, 210, 297, 5, 5);
		// TODO: Assert something
	}

	@Test
	public void testShapes() {
		Path2D path = new GeneralPath();
		path.moveTo(0.0, 0.0);
		path.lineTo(1.0, 1.0);
		path.curveTo(0.7, 1.0, 0.0, 0.7, 0.0, 0.5);
		path.quadTo(0.0, 0.0, 0.5, 0.5);
		path.closePath();

		Shape[] shapes = {
			new Line2D.Double(0.00, 0.25, 1.00, 0.75),
			new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0),
			new RoundRectangle2D.Double(0.0, 0.0, 1.0, 1.0, 0.25, 0.25),
			new Ellipse2D.Double(0.0, 0.0, 1.0, 1.0),
			new Arc2D.Double(0.0, 0.0, 1.0, 1.0, 30.0, 330.0, Arc2D.PIE),
			path
		};

		for (Shape shape : shapes) {
			g.fill(shape);
			g.draw(shape);
		}
		// TODO: Assert something
	}

	@Test
	public void testToString() {
		g.draw(new Line2D.Double(0.00, 0.25, 1.00, 0.75));
		String emptyDoc = g.toString();
		assertTrue(emptyDoc.length() > 0);
	}

	@Test
	public void testDrawString() {
		g.drawString("foo", 0, 0);
		g.drawString("bar", 0f, 0.5f);
		// TODO: Assert something
	}

	@Test
	public void testRenderingHints() {
		// Initialization
		assertEquals(0, g.getRenderingHints().size());
		// Modification
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		assertEquals(RenderingHints.VALUE_INTERPOLATION_BICUBIC, g.getRenderingHint(RenderingHints.KEY_INTERPOLATION));
	}

	@Test
	public void testBackground() {
		Color c = Color.BLUE;
		g.setBackground(c);
		assertEquals(c, g.getBackground());
	}

	@Test
	public void testColor() {
		Color c = Color.RED;
		g.setColor(c);
		assertEquals(c, g.getColor());
	}

	@Test
	public void testFont() {
		Font f = Font.decode(null);
		f = f.deriveFont(f.getSize2D()*2f);
		g.setFont(f);
		assertEquals(f, g.getFont());
	}

	@Test
	public void testPaint() {
		Paint p = Color.GREEN;
		g.setPaint(p);
		assertEquals(p, g.getPaint());
		assertEquals(p, g.getColor());
	}

	@Test
	public void testStroke() {
		Stroke s = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 20f, new float[] {1f, 1f}, 2f);
		g.setStroke(s);
		assertEquals(s, g.getStroke());
	}

	@Test
	public void testDrawImage() {
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		g.drawImage(image, AffineTransform.getScaleInstance(0.5, 0.5), null);
		g.drawImage(image, new AffineTransformOp(AffineTransform.getTranslateInstance(0.5, 0.5), AffineTransformOp.TYPE_NEAREST_NEIGHBOR), 0, 0);
		g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), Color.WHITE, null);
		// TODO: Assert something
	}

	@Test
	public void testTransform() {
		// Initial transform must be identity
		AffineTransform txOrig = g.getTransform();
		assertEquals(new AffineTransform(), txOrig);
		// Translate
		g.setTransform(txOrig);
		g.translate(1, 2);
		assertEquals(AffineTransform.getTranslateInstance(1, 2), g.getTransform());
		// Scale
		g.setTransform(txOrig);
		g.scale(3, 4);
		assertEquals(AffineTransform.getScaleInstance(3, 4), g.getTransform());
		// Rotate 1
		g.setTransform(txOrig);
		g.rotate(Math.PI/4.0);
		assertEquals(AffineTransform.getRotateInstance(Math.PI/4.0), g.getTransform());
		// Rotate 2
		g.setTransform(txOrig);
		g.rotate(Math.PI/2.0, 0.5, 0.5);
		assertEquals(AffineTransform.getRotateInstance(Math.PI/2.0, 0.5, 0.5), g.getTransform());
		// Shear
		g.setTransform(txOrig);
		g.shear(5, 6);
		assertEquals(AffineTransform.getShearInstance(5, 6), g.getTransform());
	}

}
