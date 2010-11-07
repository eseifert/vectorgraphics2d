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
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

/**
 * <code>Graphics2D</code> implementation that saves all operations to a SVG string.
 */
public class SVGGraphics2D extends VectorGraphics2D {
	/** Mapping of stroke endcap values from Java to SVG. */
	private static final Map<Integer, String> STROKE_ENDCAPS = DataUtils.map(
		new Integer[] { BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND, BasicStroke.CAP_SQUARE },
		new String[] { "butt", "round", "square" }
	);

	/** Mapping of line join values for path drawing from Java to SVG. */
	private static final Map<Integer, String> STROKE_LINEJOIN = DataUtils.map(
		new Integer[] { BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL },
		new String[] { "miter", "round", "bevel" }
	);

	/** Prefix string for ids of clipping paths. */
	private static final String CLIP_PATH_ID = "clip";
	/** Number of the current clipping path. */
	private long clipCounter;

	/**
	 * Constructor that initializes a new <code>SVGGraphics2D</code> instance.
	 * The document dimension must be specified as parameters.
	 */
	public SVGGraphics2D(double x, double y, double width, double height) {
		super(x, y, width, height);
		writeHeader();
	}

	@Override
	protected void writeString(String str, double x, double y) {
		// Escape string
		str = str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		// Output
		writeln("<text x=\"", x, "\" y=\"", y, "\">", str, "</text>");
	}

	@Override
	protected void writeImage(Image img, int imgWidth, int imgHeight, double x,
			double y, double width, double height) {
		String imgData = getSvg(img);
		write("<image x=\"" , x, "\" y=\"" , y, "\" ",
				"width=\"" , width, "\" height=\"" , height, "\" ",
				"xlink:href=\"", imgData, "\" ",
				"/>");
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		write("<polygon points=\"");
		for (int i = 0; i < nPoints; i++) {
			if (i > 0) {
				write(" ");
			}
			write(xPoints[i], ",", yPoints[i]);
		}
		write("\" ");
		writeClosingDraw();
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		write("<polyline points=\"");
		for (int i = 0; i < nPoints; i++) {
			if (i > 0) {
				write(" ");
			}
			write(xPoints[i], ",", yPoints[i]);
		}
		write("\" ");
		writeClosingDraw();
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		write("<polygon points=\"");
		for (int i = 0; i < nPoints; i++) {
			if (i > 0) {
				write(" ");
			}
			write(xPoints[i], ",", yPoints[i]);
		}
		write("\" ");
		writeClosingFill();
	}

	@Override
	public void setClip(Shape clip) {
		super.setClip(clip);
		if (getClip() != null) {
			writeln("<clipPath id=\"", CLIP_PATH_ID, ++clipCounter, "\">");
			writeShape(getClip());
			writeln("/>");
			writeln("</clipPath>");
		}
	}

	@Override
	protected void writeHeader() {
		Rectangle2D bounds = getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		writeln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writeln("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" ",
				"\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
		writeln("<svg version=\"1.2\" xmlns=\"http://www.w3.org/2000/svg\" ",
			"xmlns:xlink=\"http://www.w3.org/1999/xlink\" ",
			"x=\"", x, "mm\" y=\"", y, "mm\" ",
			"width=\"", w, "mm\" height=\"", h, "mm\" " +
			"viewBox=\"", x, " ", y, " ", w, " ", h, "\"",
			">"
		);
		writeln("<style type=\"text/css\"><![CDATA[");
		writeln("text { font-family:", getFont().getFamily(), ";font-size:", getFont().getSize2D(), "px; }");
		writeln("]]></style>");
	}

	/**
	 * Utility method for writing a tag closing fragment for drawing operations.
	 */
	@Override
	protected void writeClosingDraw() {
		write("style=\"fill:none;stroke:", getSvg(getColor()));
		if (getStroke() instanceof BasicStroke) {
			BasicStroke s = (BasicStroke) getStroke();
			if (s.getLineWidth() != 1f) {
				write(";stroke-width:", s.getLineWidth());
			}
			if (s.getEndCap() != BasicStroke.CAP_BUTT) {
				write(";stroke-linecap:", STROKE_ENDCAPS.get(s.getEndCap()));
			}
			if (s.getLineJoin() != BasicStroke.JOIN_MITER) {
				write(";stroke-linejoin:", STROKE_LINEJOIN.get(s.getLineJoin()));
			}
			//write(";stroke-miterlimit:", s.getMiterLimit());
			if (s.getDashArray() != null && s.getDashArray().length>0) {
				write(";stroke-dasharray:"); write(s.getDashArray());
				write(";stroke-dashoffset:", s.getDashPhase());
			}
		}
		if (getClip() != null) {
			write("\" clip-path=\"url(#", CLIP_PATH_ID, clipCounter, ")");
		}
		writeln("\" />");
	}

	/**
	 * Utility method for writing a tag closing fragment for filling operations.
	 */
	@Override
	protected void writeClosingFill() {
		write("style=\"fill:", getSvg(getColor()), ";stroke:none");
		if (getClip() != null) {
			write("\" clip-path=\"url(#", CLIP_PATH_ID, clipCounter, ")");
		}
		writeln("\" />");
	}

	/**
	 * Utility method for writing an arbitrary shape to.
	 * It tries to translate Java2D shapes to the corresponding SVG shape tags.
	 */
	@Override
	protected void writeShape(Shape s) {
		AffineTransform transform = getTransform();
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
				write("<line x1=\"", x1, "\" y1=\"", y1, "\" x2=\"", x2, "\" y2=\"", y2, "\" ");
				return;
			} else if (s instanceof Rectangle2D) {
				Rectangle2D r = (Rectangle2D) s;
				double x = sx*r.getX() + tx;
				double y = sy*r.getY() + ty;
				double width = sx*r.getWidth();
				double height = sy*r.getHeight();
				write("<rect x=\"", x, "\" y=\"", y, "\" width=\"", width, "\" height=\"", height, "\" ");
				return;
			} else if (s instanceof RoundRectangle2D) {
				RoundRectangle2D r = (RoundRectangle2D) s;
				double x = sx*r.getX() + tx;
				double y = sy*r.getY() + ty;
				double width = sx*r.getWidth();
				double height = sy*r.getHeight();
				double arcWidth = sx*r.getArcWidth();
				double arcHeight = sy*r.getArcHeight();
				write("<rect x=\"", x, "\" y=\"", y, "\" width=\"", width, "\" height=\"", height, "\" rx=\"", arcWidth, "\" ry=\"", arcHeight, "\" ");
				return;
			} else if (s instanceof Ellipse2D) {
				Ellipse2D e = (Ellipse2D) s;
				double x = sx*e.getX() + tx;
				double y = sy*e.getY() + ty;
				double rx = sx*e.getWidth()/2.0;
				double ry = sy*e.getHeight()/2.0;
				write("<ellipse cx=\"", x+rx, "\" cy=\"", y+ry, "\" rx=\"", rx, "\" ry=\"", ry, "\" ");
				return;
			}
		}

		s = transform.createTransformedShape(s);
		write("<path d=\"");
		PathIterator segments = s.getPathIterator(null);
		double[] coords = new double[6];
		for (int i = 0; !segments.isDone(); i++, segments.next()) {
			if (i > 0) {
				write(" ");
			}
			int segmentType = segments.currentSegment(coords);
			switch (segmentType) {
			case PathIterator.SEG_MOVETO:
				write("M", coords[0], ",", coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				write("L", coords[0], ",", coords[1]);
				break;
			case PathIterator.SEG_CUBICTO:
				write("C", coords[0], ",", coords[1], " ", coords[2], ",", coords[3], " ", coords[4], ",", coords[5]);
				break;
			case PathIterator.SEG_QUADTO:
				write("Q", coords[0], ",", coords[1], " ", coords[2], ",", coords[3]);
				break;
			case PathIterator.SEG_CLOSE:
				write("Z");
				break;
			}
		}
		write("\" ");
	}

	private static String getSvg(Color c) {
		String color = "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
		if (c.getAlpha() < 255) {
			double opacity = c.getAlpha()/255.0;
			color += ";opacity:" + opacity;
		}
		return color;
	}

	private static String getSvg(Image img) {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		BufferedImage bufferedImg = GraphicsUtils.toBufferedImage(img);
		try {
			ImageIO.write(bufferedImg, "png", data);
		} catch (IOException e) {
			return "";
		}
		String dataBase64 = DatatypeConverter.printBase64Binary(data.toByteArray());
		return "data:image/png;base64," + dataBase64;
	}

	@Override
	protected String getFooter() {
		return "</svg>\n";
	}

}
