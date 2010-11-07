/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010 Erich Seifert <dev[at]erichseifert.de>
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;

/**
 * <code>Graphics2D</code> implementation that saves all operations to a SVG string.
 */
public class EPSGraphics2D extends VectorGraphics2D {
	/** Constant to convert values from millimeters to PostScript® units (1/72th inch). */
	protected static final double MM_IN_UNITS = 72.0 / 25.4;

	/** Mapping of stroke endcap values from Java to PostScript®. */
	private static final Map<Integer, Integer> STROKE_ENDCAPS = DataUtils.map(
		new Integer[] { BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND, BasicStroke.CAP_SQUARE },
		new Integer[] { 0, 1, 2 }
	);

	/** Mapping of line join values for path drawing from Java to PostScript®. */
	private static final Map<Integer, Integer> STROKE_LINEJOIN = DataUtils.map(
		new Integer[] { BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL },
		new Integer[] { 0, 1, 2 }
	);

	/**
	 * Constructor that initializes a new <code>SVGGraphics2D</code> instance.
	 * The document dimension must be specified as parameters.
	 */
	public EPSGraphics2D(double x, double y, double width, double height) {
		super(x, y, width, height);
		writeHeader();
	}

	@Override
	protected void writeString(String str, double x, double y) {
		// TODO Encode string
		//byte[] bytes = str.getBytes("ISO-8859-1");
		// Escape string
		str = str.replaceAll("\\\\", "\\\\")
			.replaceAll("\n", "\\n").replaceAll("\r", "\\r")
			.replaceAll("\t", "\\t").replaceAll("\b", "\\b").replaceAll("\f", "\\f")
			.replaceAll("\\(", "\\(").replaceAll("\\)", "\\)");
		// Output
		writeln("gsave ", x, " ", y, " M 1 -1 scale (", str, ") show ", " grestore");
	}

	@Override
	public void setStroke(Stroke s) {
		BasicStroke bsPrev;
		if (getStroke() instanceof BasicStroke) {
			bsPrev = (BasicStroke) getStroke();
		} else {
			bsPrev = new BasicStroke();
		}

		super.setStroke(s);

		if (s instanceof BasicStroke) {
			BasicStroke bs = (BasicStroke) s;
			if (bs.getLineWidth() != bsPrev.getLineWidth()) {
				writeln(bs.getLineWidth(), " setlinewidth");
			}
			if (bs.getLineJoin() != bsPrev.getLineJoin()) {
				writeln(STROKE_LINEJOIN.get(bs.getLineJoin()), " setlinejoin");
			}
			if (bs.getEndCap() != bsPrev.getEndCap()) {
				writeln(STROKE_ENDCAPS.get(bs.getEndCap()), " setlinecap");
			}
			if ((!Arrays.equals(bs.getDashArray(), bsPrev.getDashArray())) ||
				(bs.getDashPhase() != bsPrev.getDashPhase())) {
				writeln("[", DataUtils.join(" ", bs.getDashArray()), "] ",
						bs.getDashPhase(), " setdash");
			}
		}
	}

	@Override
	protected void writeImage(Image img, int imgWidth, int imgHeight, double x, double y, double width, double height) {
		String imgData = getEps(img);
		writeln("gsave");
		writeln(x, " ", y, " ", width, " ", height, " ",
			imgWidth, " ", imgHeight, " img"
		);
		writeln(imgData, ">");
		writeln("grestore");
	}

	@Override
	public void setColor(Color c) {
		Color color = getColor();
		if (c != null) {
			super.setColor(c);
			// TODO Add transparency hints for PDF conversion
			/*if (color.getAlpha() != c.getAlpha()) {
				double a = c.getAlpha()/255.0;
				writeln("[ /ca ", a, " /SetTransparency pdfmark");
			}*/
			if (color.getRed() != c.getRed() || color.getGreen() != c.getGreen() || color.getBlue() != c.getBlue()) {
				double r = c.getRed()/255.0;
				double g = c.getGreen()/255.0;
				double b = c.getBlue()/255.0;
				writeln(r, " ", g, " ", b, " rgb");
			}
		}
	}

	@Override
	public void setFont(Font font) {
		if (!getFont().equals(font)) {
			super.setFont(font);
			writeln("/", font.getPSName(), " ", font.getSize2D(), " selectfont");
		}
	}

	@Override
	public void setClip(Shape clip) {
		if (getClip() != null) {
			writeln("cliprestore");
		}
		super.setClip(clip);
		if (getClip() != null) {
			writeShape(getClip());
			writeln(" clip");
		}
	}

	@Override
	protected void writeHeader() {
		Rectangle2D bounds = getBounds();
		int x = (int)Math.floor(bounds.getX() * MM_IN_UNITS);
		int y = (int)Math.floor(bounds.getY() * MM_IN_UNITS);
		int w = (int)Math.ceil(bounds.getWidth() * MM_IN_UNITS);
		int h = (int)Math.ceil(bounds.getHeight() * MM_IN_UNITS);

		writeln("%!PS-Adobe-3.0 EPSF-3.0");
		writeln("%%BoundingBox: ", x, " ", y, " ", w, " ", h);
		writeln("%%LanguageLevel: 2");
		writeln("%%Pages: 1");
		writeln("%%Page: 1 1");

		// Utility functions
		writeln("/M /moveto load def");
		writeln("/L /lineto load def");
		writeln("/C /curveto load def");
		writeln("/Z /closepath load def");
		writeln("/RL /rlineto load def");
		writeln("/rgb /setrgbcolor load def");
		writeln("/rect { ",
				"/height exch def /width exch def /y exch def /x exch def ",
				"x y M width 0 RL 0 height RL width neg 0 RL ",
				"} bind def");
		// TODO Round rectangle
		writeln("/rrect { ",
				"/archeight exch def /arcwidth exch def /height exch def /width exch def /y exch def /x exch def ",
				"x y M width 0 RL 0 height RL width neg 0 RL ",
				"} bind def");
		writeln("/ellipse { ",
			"/endangle exch def /startangle exch def /ry exch def /rx exch def /y exch def /x exch def ",
			"/savematrix matrix currentmatrix def ",
			"x y translate rx ry scale 0 0 1 startangle endangle arc ",
			"savematrix setmatrix ",
			"} bind def");
		writeln("/img { ",
				"/imgheight exch def /imgwidth exch def /height exch def /width exch def /y exch def /x exch def ",
				"x y translate width height scale << ",
				"/ImageType 1 /Width imgwidth /Height imgheight ",
				"/BitsPercomponent 8 /Decode [0 1 0 1 0 1] ",
				"/ImageMatrix [imgwidth 0 0 imgheight 0 imgheight] ",
				"/DataSource currentfile /ASCIIHexDecode filter ",
				">> image",
				"} bind def");
		// Set default font
		writeln("/", getFont().getPSName(), " ", getFont().getSize2D(), " selectfont");
		//writeln("<< /AllowTransparency true >> setdistillerparams"); // TODO
		// Save state
		writeln("gsave");
		// Save state
		writeln("clipsave");
		// Settings
		writeln("/DeviceRGB setcolorspace");
		// Adjust page size and page origin
		writeln("0 ", h, " translate 1 -1 scale");
		writeln(MM_IN_UNITS, " ", MM_IN_UNITS, " scale");
	}

	/**
	 * Utility method for writing a tag closing fragment for drawing operations.
	 */
	@Override
	protected void writeClosingDraw() {
		writeln(" stroke");
	}

	/**
	 * Utility method for writing a tag closing fragment for filling operations.
	 */
	@Override
	protected void writeClosingFill() {
		writeln(" fill");
	}

	/**
	 * Utility method for writing an arbitrary shape to.
	 * It tries to translate Java2D shapes to the corresponding SVG shape tags.
	 */
	@Override
	protected void writeShape(Shape s) {
		AffineTransform transform = getTransform();
		write("newpath ");
		if (!isDistorted()) {
			double sx = transform.getScaleX();
			double sy = transform.getScaleX();
			double tx = transform.getTranslateX();
			double ty = transform.getTranslateY();
			if (s instanceof Line2D) {
				Line2D l = (Line2D) s;
				double x1 = sx*l.getX1() + tx;
				double y1 = sy*l.getY1() + ty;
				double x2 = sx*l.getX2() + tx;
				double y2 = sy*l.getY2() + ty;
				write(x1, " ", y1, " M ", x2, " ", y2, " L");
				return;
			} else if (s instanceof Rectangle2D) {
				Rectangle2D r = (Rectangle2D) s;
				double x = sx*r.getX() + tx;
				double y = sy*r.getY() + ty;
				double width = sx*r.getWidth();
				double height = sy*r.getHeight();
				write(x, " ", y, " ", width, " ", height, " rect Z");
				return;
			} else if (s instanceof RoundRectangle2D) {
				RoundRectangle2D r = (RoundRectangle2D) s;
				double x = sx*r.getX() + tx;
				double y = sy*r.getY() + ty;
				double width = sx*r.getWidth();
				double height = sy*r.getHeight();
				double arcWidth = sx*r.getArcWidth();
				double arcHeight = sy*r.getArcWidth();
				write(x, " ", y, " ", width, " ", height, " ", arcWidth, " ", arcHeight, " rrect Z");
				return;
			} else if (s instanceof Ellipse2D) {
				Ellipse2D e = (Ellipse2D) s;
				double x = sx*e.getX() + e.getWidth()/2.0 + tx;
				double y = sy*e.getY() + e.getHeight()/2.0 + ty;
				double rx = sx*e.getWidth()/2.0;
				double ry = sy*e.getHeight()/2.0;
				write(x, " ", y, " ", rx, " ", ry, " ", 0.0, " ", 360.0, " ellipse Z");
				return;
			} else if (s instanceof Arc2D) {
				Arc2D e = (Arc2D) s;
				double x = sx*e.getX() + tx;
				double y = sy*e.getY() + ty;
				double rx = sx*e.getWidth()/2.0;
				double ry = sy*e.getHeight()/2.0;
				double startAngle = e.getAngleStart();
				double endAngle = e.getAngleExtent();
				write(x, " ", y, " ", rx, " ", ry, " ", startAngle, " ", endAngle, " ellipse Z");
				return;
			}
		}

		s = transform.createTransformedShape(s);
		PathIterator segments = s.getPathIterator(null);
		double[] coordsCur = new double[6];
		double[] pointPrev = new double[2];
		for (int i = 0; !segments.isDone(); i++, segments.next()) {
			if (i > 0) {
				write(" ");
			}
			int segmentType = segments.currentSegment(coordsCur);
			switch (segmentType) {
			case PathIterator.SEG_MOVETO:
				write(coordsCur[0], " ", coordsCur[1], " M");
				pointPrev[0] = coordsCur[0];
				pointPrev[1] = coordsCur[1];
				break;
			case PathIterator.SEG_LINETO:
				write(coordsCur[0], " ", coordsCur[1], " L");
				pointPrev[0] = coordsCur[0];
				pointPrev[1] = coordsCur[1];
				break;
			case PathIterator.SEG_CUBICTO:
				write(coordsCur[0], " ", coordsCur[1], " ", coordsCur[2], " ", coordsCur[3], " ", coordsCur[4], " ", coordsCur[5], " C");
				pointPrev[0] = coordsCur[4];
				pointPrev[1] = coordsCur[5];
				break;
			case PathIterator.SEG_QUADTO:
				double x1 = pointPrev[0] + 2.0/3.0*(coordsCur[0] - pointPrev[0]);
				double y1 = pointPrev[1] + 2.0/3.0*(coordsCur[1] - pointPrev[1]);
				double x2 = coordsCur[0] + 1.0/3.0*(coordsCur[2] - coordsCur[0]);
				double y2 = coordsCur[1] + 1.0/3.0*(coordsCur[3] - coordsCur[1]);
				double x3 = coordsCur[2];
				double y3 = coordsCur[3];
				write(x1, " ", y1, " ", x2, " ", y2, " ", x3, " ", y3, " C");
				pointPrev[0] = x3;
				pointPrev[1] = y3;
				break;
			case PathIterator.SEG_CLOSE:
				write("Z");
				break;
			}
		}
	}

	public static String getEps(Image img) {
		BufferedImage bufferedImg = GraphicsUtils.toBufferedImage(img);
		int[] data = bufferedImg.getRaster().getPixels(
				bufferedImg.getMinX(), bufferedImg.getMinY(),
				bufferedImg.getWidth(), bufferedImg.getHeight(),
				(int[])null);
		StringBuffer str = new StringBuffer(data.length*6);
		for (int i : data) {
			if ((i > 0) && (i%bufferedImg.getWidth() == 0)) {
				str.append("\n");
			}
			String hex = String.format("%02x", i);
			if (hex.length() > 2) {
				hex = hex.substring(hex.length() - 2);
			}
			str.append(hex);
		}
		return str.toString();
	}

	@Override
	protected String getFooter() {
		return "grestore  % Restore state\n%%EOF\n";
	}

}
