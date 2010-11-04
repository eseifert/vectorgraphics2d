/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010 Erich Seifert <info[at]erichseifert.de>
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

package de.erichseifert.vectorgraphics2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class VectorGraphics2D extends Graphics2D {
	/** Constants to define how fonts are rendered. */
	public static enum FontRendering {
		/** Constant indicating that fonts should be rendered as text objects. */
		TEXT,
		/** Constant indicating that fonts should be converted to vectors. */
		VECTORS
	}

	private final RenderingHints hints;
	private final StringBuffer document;
	private final Rectangle2D bounds;

	private Color background;
	private Color color;
	private Shape clip;
	private Rectangle clipBounds;
	private Composite composite;
	private GraphicsConfiguration deviceConfig;
	private Font font;
	private FontMetrics fontMetrics;
	private final FontRenderContext fontRenderContext;
	private Paint paint;
	private Stroke stroke;
	private final AffineTransform transform;
	private Color xorMode;

	private FontRendering fontRendering;

	/**
	 * Constructor to initialize a new {@code VectorGraphics2D} document.
	 * The dimensions of the document must be passed.
	 * @param x Horizontal position of document origin.
	 * @param y Vertical position of document origin.
	 * @param width Width of document.
	 * @param height Height of document.
	 */
	public VectorGraphics2D(double x, double y, double width, double height) {
		hints = new RenderingHints(new HashMap<RenderingHints.Key, Object>());
		document = new StringBuffer();
		bounds = new Rectangle2D.Double(x, y, width, height);
		fontRendering = FontRendering.TEXT;

		background = Color.WHITE;
		color = Color.BLACK;
		composite = AlphaComposite.getInstance(AlphaComposite.CLEAR);
		font = Font.decode(null);
		fontRenderContext = new FontRenderContext(null, false, true);
		paint = color;
		stroke = new BasicStroke(1f);
		transform = new AffineTransform();
		xorMode = Color.BLACK;
	}

	@Override
	public void addRenderingHints(Map<?,?> hints) {
		this.hints.putAll(hints);
	}

	@Override
	public void clip(Shape s) {
		// TODO
	}

	@Override
	public void draw(Shape s) {
		writeShape(s);
		writeClosingDraw();
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		draw(g.getOutline(x, y));
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		BufferedImage bimg = getTransformedImage(img, xform);
		drawImage(bimg, null, bimg.getMinX(), bimg.getMinY());
		return true;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		if (op != null) {
			img = op.filter(img, null);
		}
		drawImage(img, x, y, img.getWidth(), img.getHeight(), null);
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		drawRenderedImage(img.createDefaultRendering(), xform);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		// TODO
	}

	@Override
	public void drawString(String str, int x, int y) {
		drawString(str, (float)x, (float)y);
	}

	@Override
	public void drawString(String str, float x, float y) {
		switch (getFontRendering()) {
		case VECTORS:
			TextLayout layout = new TextLayout(str, getFont(), getFontRenderContext());
			Shape s = layout.getOutline(AffineTransform.getTranslateInstance(x, y));
			fill(s);
			break;
		case TEXT:
			writeString(str, x, y);
			break;
		}
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		drawString(iterator, (float)x, (float)y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		// TODO Take text formatting into account
		StringBuffer buf = new StringBuffer();
		for (char c = iterator.first(); c != AttributedCharacterIterator.DONE; c = iterator.next()) {
			buf.append(c);
		}
		drawString(buf.toString(), x, y);
	}

	@Override
	public void fill(Shape s) {
		writeShape(s);
		writeClosingFill();
	}

	@Override
	public Color getBackground() {
		return background;
	}

	@Override
	public Composite getComposite() {
		return composite;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return deviceConfig;
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return fontRenderContext;
	}

	@Override
	public Paint getPaint() {
		return paint;
	}

	@Override
	public Object getRenderingHint(RenderingHints.Key hintKey) {
		if (RenderingHints.KEY_ANTIALIASING.equals(hintKey)) {
			return RenderingHints.VALUE_ANTIALIAS_OFF;
		} else if (RenderingHints.KEY_TEXT_ANTIALIASING.equals(hintKey)) {
			return RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
		} else if (RenderingHints.KEY_FRACTIONALMETRICS.equals(hintKey)) {
			return RenderingHints.VALUE_FRACTIONALMETRICS_ON;
		}
		return hints.get(hintKey);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return hints;
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public AffineTransform getTransform() {
		return new AffineTransform(transform);
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		if (onStroke) {
			Shape sStroke = getStroke().createStrokedShape(s);
			return sStroke.intersects(rect);
		} else  {
			return s.intersects(rect);
		}
	}

	@Override
	public void rotate(double theta) {
		transform.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		transform.rotate(theta, x, y);
	}

	@Override
	public void scale(double sx, double sy) {
		transform.scale(sx, sy);
	}

	@Override
	public void setBackground(Color color) {
		background = color;
	}

	@Override
	public void setComposite(Composite comp) {
		composite = comp;
	}

	@Override
	public void setPaint(Paint paint) {
		if (paint != null) {
			this.paint = paint;
			if (paint instanceof Color) {
				setColor((Color) paint);
			} else if (paint instanceof MultipleGradientPaint) {
				// Set brightest or least opaque color for gradients
				Color[] colors = ((MultipleGradientPaint)paint).getColors();
				if (colors.length == 1) {
					setColor(colors[0]);
				} else if (colors.length > 1) {
					Color colLight = colors[0];
					float brightness = getBrightness(colLight);
					int alpha = colLight.getAlpha();

					for (int i = 1; i < colors.length; i++) {
						Color c = colors[i];
						float b = getBrightness(c);
						int a = c.getAlpha();
						if (b < brightness || a < alpha) {
							colLight = c;
							brightness = b;
						}
					}
					setColor(colLight);
				}
			}
		}
	}

	/**
	 * Utility method to get the brightness of a specified color.
	 * @param c Color.
	 * @return Brightness value between 0f (black) and 1f (white).
	 */
	private static float getBrightness(Color c) {
		return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null)[2];
	}

	@Override
	public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
		hints.put(hintKey, hintValue);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		this.hints.putAll(hints);
	}

	@Override
	public void setStroke(Stroke s) {
		stroke = s;
	}

	@Override
	public void setTransform(AffineTransform tx) {
		transform.setTransform(tx);
	}

	@Override
	public void shear(double shx, double shy) {
		transform.shear(shx, shy);
	}

	@Override
	public void transform(AffineTransform tx) {
		transform.concatenate(tx);
	}

	@Override
	public void translate(int x, int y) {
		translate((double)x, (double)y);
	}

	@Override
	public void translate(double tx, double ty) {
		transform.translate(tx, ty);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		// TODO Auto-generated method stub
	}

	@Override
	public Graphics create() {
		return this;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		draw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		int imgWidth = img.getWidth(observer);
		int imgHeight = img.getHeight(observer);
		writeImage(img, imgWidth, imgHeight, x, y, width, height);
		return true;  // TODO Return only true if image data was complete
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		return drawImage(img, x, y, width, height, observer);
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		draw(new Line2D.Double(x1, y1, x2, y2));
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		draw(new Ellipse2D.Double(x, y, width, height));
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		Path2D p = new Path2D.Float();
		for (int i = 0; i < nPoints; i++) {
			if (i > 0) {
				p.lineTo(xPoints[i], yPoints[i]);
			} else {
				p.moveTo(xPoints[i], yPoints[i]);
			}
		}
		p.closePath();
		draw(p);
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		Path2D p = new Path2D.Float();
		for (int i = 0; i < nPoints; i++) {
			if (i > 0) {
				p.lineTo(xPoints[i], yPoints[i]);
			} else {
				p.moveTo(xPoints[i], yPoints[i]);
			}
		}
		draw(p);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		draw(new Rectangle2D.Double(x, y, width, height));
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		fill(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.PIE));
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		fill(new Ellipse2D.Double(x, y, width, height));
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		Path2D p = new Path2D.Float();
		for (int i = 0; i < nPoints; i++) {
			if (i > 0) {
				p.lineTo(xPoints[i], yPoints[i]);
			} else {
				p.moveTo(xPoints[i], yPoints[i]);
			}
		}
		p.closePath();

		fill(p);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		fill(new Rectangle2D.Double(x, y, width, height));
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		fill(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
	}

	@Override
	public Shape getClip() {
		return clip;
	}

	@Override
	public Rectangle getClipBounds() {
		return clipBounds;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return fontMetrics;
	}

	@Override
	public void setClip(Shape clip) {
		this.clip = clip;
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		clip = new Rectangle(x, y, width, height);
	}

	@Override
	public void setColor(Color c) {
		color = c;
	}

	@Override
	public void setFont(Font font) {
		if (!this.font.equals(font)) {
			this.font = font;
		}
	}

	@Override
	public void setPaintMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setXORMode(Color c1) {
		xorMode = c1;
	}

	/**
	 * Utility method for writing multiple objects to the SVG document.
	 * @param strs Objects to be written
	 */
	protected void write(Object... strs) {
		for (Object o : strs) {
			String str = o.toString();
			if ((o instanceof Double) || (o instanceof Float)) {
				str = String.format(Locale.ENGLISH, "%.7f", o).replaceAll("\\.?0+$", "");
			}
			document.append(str);
		}
	}

	/**
	 * Utility method for writing a line of multiple objects to the SVG document.
	 * @param strs Objects to be written
	 */
	protected void writeln(Object... strs) {
		write(strs);
		write("\n");
	}

	/**
	 * Write the specified shape to the document. This does not necessarily
	 * contain the actual command to paint the shape.
	 * @param s Shape to be written.
	 */
	protected abstract void writeShape(Shape s);

	/**
	 * Write the specified image to the document. A number of dimensions will
	 * specify how the image will be placed in the document.
	 * @param img Image to be rendered.
	 * @param imgWidth Number of pixels in horizontal direction.
	 * @param imgHeight Number of pixels in vertical direction
	 * @param x Horizontal position in document units where the upper left corner of the image should be placed.
	 * @param y Vertical position in document units where the upper left corner of the image should be placed.
	 * @param width Width of the image in document units.
	 * @param height Height of the image in document units.
	 */
	protected abstract void writeImage(Image img, int imgWidth, int imgHeight, double x, double y, double width, double height);

	/**
	 * Write a text string to the document at a specified position.
	 * @param str Text to be rendered.
	 * @param x Horizontal position in document units.
	 * @param y Vertical position in document units.
	 */
	protected abstract void writeString(String str, double x, double y);

	/**
	 * Write a command to draw the outline of a previously inserted shape.
	 */
	protected abstract void writeClosingDraw();

	/**
	 * Write a command to fill the outline of a previously inserted shape.
	 */
	protected abstract void writeClosingFill();

	/**
	 * Write the header to start a new document.
	 */
	protected abstract void writeHeader();

	/**
	 * Returns a string of the footer to end a document.
	 */
	protected abstract String getFooter();

	private BufferedImage getTransformedImage(Image image, AffineTransform xform) {
		Integer interpolationType = (Integer)hints.get(RenderingHints.KEY_INTERPOLATION);
		if (RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.equals(interpolationType)) {
			interpolationType = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
		} else if (RenderingHints.VALUE_INTERPOLATION_BILINEAR.equals(interpolationType)) {
			interpolationType = AffineTransformOp.TYPE_BILINEAR;
		} else {
			interpolationType = AffineTransformOp.TYPE_BICUBIC;
		}
		AffineTransformOp op = new AffineTransformOp(xform, interpolationType);
		BufferedImage bufferedImage = GraphicsUtils.toBufferedImage(image);
		return op.filter(bufferedImage, null);
	}

	/**
	 * Returns whether a distorting transformation has been applied to the
	 * document.
	 * @return <code>true</code> if the document is distorted, otherwise <code>false</code>.
	 */
	protected boolean isDistorted() {
		int type = transform.getType();
		int otherButTranslatedOrScaled = ~(AffineTransform.TYPE_TRANSLATION | AffineTransform.TYPE_MASK_SCALE);
		return (type & otherButTranslatedOrScaled) != 0;
	}

	@Override
	public String toString() {
		return document.toString() + getFooter();
	}

	/**
	 * Returns the dimensions of the document.
	 * @return
	 */
	public Rectangle2D getBounds() {
		Rectangle2D b = new Rectangle2D.Double();
		b.setFrame(bounds);
		return b;
	}

	/**
	 * Returns the number of bytes of the document.
	 * @return size of the document in bytes.
	 */
	protected int size() {
		return document.length();
	}

	/**
	 * Returns how fonts should be rendered.
	 * @return Font rendering mode.
	 */
	public FontRendering getFontRendering() {
		return fontRendering;
	}

	/**
	 * Sets how fonts should be rendered. For example, they can be converted
	 * to vector shapes.
	 * @param mode New font rendering mode.
	 */
	public void setFontRendering(FontRendering mode) {
		fontRendering = mode;
	}

}